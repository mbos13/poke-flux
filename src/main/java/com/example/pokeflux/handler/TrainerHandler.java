package com.example.pokeflux.handler;

import com.example.pokeflux.model.Pokemon;
import com.example.pokeflux.model.Trainer;
import com.example.pokeflux.repository.PokemonRepository;
import com.example.pokeflux.repository.TrainerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TrainerHandler {

    TrainerRepository trainerRepository;
    PokemonRepository pokemonRepository;
    WebClient localApiClient;

    @Value("${pokeapi.pokemon}")
    private String pokeApi;

    @Autowired
    public TrainerHandler(TrainerRepository trainerRepository,
                          WebClient localApiClient,
                          PokemonRepository pokemonRepository) {
        this.trainerRepository = trainerRepository;
        this.localApiClient = localApiClient;
        this.pokemonRepository = pokemonRepository;
    }

    public Mono<ServerResponse> getAllTrainers(ServerRequest serverRequest) {
        Flux<Trainer> trainerFlux = trainerRepository.findAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(trainerFlux, Trainer.class);
    }

    public Mono<ServerResponse> createTrainer(ServerRequest serverRequest) {
        Mono<Trainer> trainerMono = serverRequest.bodyToMono(Trainer.class);
        return trainerMono.flatMap(trainer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(trainerRepository.save(trainer), Trainer.class));
    }

    public Mono<ServerResponse> getOneTrainer(ServerRequest serverRequest) {
        int trainerId = Integer.parseInt(serverRequest.pathVariable("id"));
        return trainerRepository.findTrainerById(trainerId)
                .flatMap(trainer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(trainer))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateTrainer(ServerRequest serverRequest) {
        Integer trainerId = Integer.parseInt(serverRequest.pathVariable("id"));
        Mono<Trainer> updatedTrainer = serverRequest.bodyToMono(Trainer.class)
                .flatMap(trainer -> trainerRepository.findById(trainerId).flatMap(currentTrainer -> {
                    currentTrainer.setName(trainer.getName());
                    currentTrainer.setHomeTown(trainer.getHomeTown());
                    currentTrainer.setGender(trainer.getGender());
                    currentTrainer.setRegion(trainer.getRegion());
                    currentTrainer.setPokemonsOnHand(trainer.getPokemonsOnHand());
                    return trainerRepository.save(currentTrainer);
                }));
        return updatedTrainer
                .flatMap(trainer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(trainer)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> catchPokemon(ServerRequest serverRequest) {
        Integer trainerId = Integer.parseInt(serverRequest.pathVariable("id"));
        String pokemonName = serverRequest.pathVariable("name");

        return localApiClient.get()
                .uri(pokeApi + "/" + pokemonName)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        return trainerRepository.findTrainerByPokemon(pokemonName)
                                .flatMap(trainer -> ServerResponse.badRequest().build())
                                .switchIfEmpty(trainerRepository.findTrainerById(trainerId)
                                        .flatMap(newTrainer -> {
                                            Pokemon newPokemon = new Pokemon(pokemonName, newTrainer.getId());

                                            return pokemonRepository.save(newPokemon)
                                                    .flatMap(trainer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                                            .body(BodyInserters.fromValue(newPokemon)));
                                        }).switchIfEmpty(ServerResponse.notFound().build())
                                );
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        return ServerResponse.notFound().build();
                    } else {
                        return ServerResponse.badRequest().build();
                    }
                });
    }
}
