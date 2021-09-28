package com.example.pokeflux.handler;

import com.example.pokeflux.model.Trainer;
import com.example.pokeflux.repository.ReactiveTrainerRepository;
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

import java.util.List;

@Component
@Slf4j
public class TrainerHandler {

    ReactiveTrainerRepository reactiveTrainerRepository;
    WebClient localApiClient;

    @Value("${pokeapi.pokemon}")
    private String pokeApi;

    @Autowired
    public TrainerHandler(ReactiveTrainerRepository reactiveTrainerRepository, WebClient localApiClient) {
        this.reactiveTrainerRepository = reactiveTrainerRepository;
        this.localApiClient = localApiClient;
    }

    public Mono<ServerResponse> getAllTrainers(ServerRequest serverRequest) {
        Flux<Trainer> trainerFlux = reactiveTrainerRepository.findAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(trainerFlux, Trainer.class);
    }

    public Mono<ServerResponse> createTrainer(ServerRequest serverRequest) {
        Mono<Trainer> trainerMono = serverRequest.bodyToMono(Trainer.class);
        return trainerMono.flatMap(trainer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(reactiveTrainerRepository.save(trainer), Trainer.class));
    }

    public Mono<ServerResponse> getOneTrainer(ServerRequest serverRequest) {
        int trainerId = Integer.parseInt(serverRequest.pathVariable("id"));
        return reactiveTrainerRepository.findById(trainerId)
                .flatMap(trainer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(trainer))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateTrainer(ServerRequest serverRequest) {
        Integer trainerId = Integer.parseInt(serverRequest.pathVariable("id"));
        Mono<Trainer> updatedTrainer = serverRequest.bodyToMono(Trainer.class)
                .flatMap(trainer -> reactiveTrainerRepository.findById(trainerId).flatMap(currentTrainer -> {
                    currentTrainer.setName(trainer.getName());
                    currentTrainer.setHomeTown(trainer.getHomeTown());
                    currentTrainer.setGender(trainer.getGender());
                    currentTrainer.setRegion(trainer.getRegion());
                    currentTrainer.setPokemonsOnHand(trainer.getPokemonsOnHand());
                    return reactiveTrainerRepository.save(currentTrainer);
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
                        return reactiveTrainerRepository.findByPokemonsOnHand(pokemonName)
                                .flatMap(trainer -> ServerResponse.badRequest().build())
                                .switchIfEmpty(reactiveTrainerRepository.findById(trainerId)
                                        .flatMap(newTrainer -> {
                                            List<String> pokemons = newTrainer.getPokemonsOnHand();
                                            pokemons.add(pokemonName);
                                            newTrainer.setPokemonsOnHand(pokemons);
                                            return reactiveTrainerRepository.save(newTrainer)
                                                    .flatMap(trainer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                                            .body(BodyInserters.fromValue(trainer)));
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
