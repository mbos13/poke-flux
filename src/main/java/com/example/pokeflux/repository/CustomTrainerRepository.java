package com.example.pokeflux.repository;

import com.example.pokeflux.model.Trainer;
import reactor.core.publisher.Mono;

public interface CustomTrainerRepository {
    Mono<Trainer> findTrainerByPokemon(String pokemon);
    Mono<Trainer> findTrainerById(int trainerId);
}
