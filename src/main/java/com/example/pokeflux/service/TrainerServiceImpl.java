package com.example.pokeflux.service;

import com.example.pokeflux.repository.ReactiveTrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    @Value("${pokeapi.pokemon}")
    private String pokeApi;

    ReactiveTrainerRepository reactiveTrainerRepository;

    WebClient localApiClient;

    @Autowired
    private TrainerServiceImpl(ReactiveTrainerRepository reactiveTrainerRepository,
                               WebClient localApiClient) {
        this.reactiveTrainerRepository = reactiveTrainerRepository;
        this.localApiClient = localApiClient;
    }

    @Override
    public Mono<?> catchPokemon(int trainerId, String pokemonName) {
        return localApiClient.get()
                .uri(pokeApi + "/" + pokemonName)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        return reactiveTrainerRepository
                                .findByPokemonsOnHand(pokemonName)
                                .flatMap(trainer -> Mono.error(new RuntimeException("Pokemon is already caught.")))
                                .switchIfEmpty(reactiveTrainerRepository
                                        .findById(trainerId)
                                        .flatMap(newTrainer -> {
                                            List<String> pokemons = newTrainer.getPokemonsOnHand();
                                            pokemons.add(pokemonName);
                                            newTrainer.setPokemonsOnHand(pokemons);
                                            return reactiveTrainerRepository.save(newTrainer)
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
