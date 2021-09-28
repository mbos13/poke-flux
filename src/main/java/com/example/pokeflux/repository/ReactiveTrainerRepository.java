package com.example.pokeflux.repository;

import com.example.pokeflux.model.Trainer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ReactiveTrainerRepository extends ReactiveMongoRepository<Trainer, Integer> {
    Mono<Trainer> findByPokemonsOnHand(String pokemonsOnHand);
}
