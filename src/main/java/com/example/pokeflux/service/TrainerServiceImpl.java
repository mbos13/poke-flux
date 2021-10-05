package com.example.pokeflux.service;

import com.example.pokeflux.model.Pokemon;
import com.example.pokeflux.repository.PokemonRepository;
import com.example.pokeflux.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TrainerServiceImpl implements TrainerService {

    @Value("${pokeapi.pokemon}")
    private String pokeApi;

    TrainerRepository trainerRepository;
    PokemonRepository pokemonRepository;

    WebClient localApiClient;

    @Autowired
    private TrainerServiceImpl(TrainerRepository trainerRepository,
                               WebClient localApiClient,
                               PokemonRepository pokemonRepository) {
        this.trainerRepository = trainerRepository;
        this.localApiClient = localApiClient;
        this.pokemonRepository = pokemonRepository;
    }

    @Override
    public Mono<?> catchPokemon(int trainerId, String pokemonName) {
        return localApiClient.get()
                .uri(pokeApi + "/" + pokemonName)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        return trainerRepository
                                .findTrainerByPokemon(pokemonName)
                                .flatMap(trainer -> Mono.error(new RuntimeException("Pokemon is already caught.")))
                                .switchIfEmpty(trainerRepository
                                        .findById(trainerId)
                                        .flatMap(newTrainer -> {
                                            Pokemon newPokemon = new Pokemon(pokemonName, newTrainer.getId());
                                            return pokemonRepository.save(newPokemon)
                                                    .flatMap(Mono::just);
                                        })
                                        .switchIfEmpty(Mono.error(new RuntimeException("Trainer doesnt exist.")))
                                ).onErrorResume(e -> Mono.just(e.getMessage()));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        return Mono.error(new RuntimeException("Pokemon doesnt exist."))
                                .onErrorResume(e -> Mono.just(e.getMessage()));
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                });
    }
}
