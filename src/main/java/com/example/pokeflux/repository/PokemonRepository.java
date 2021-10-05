package com.example.pokeflux.repository;

import com.example.pokeflux.model.Pokemon;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PokemonRepository extends ReactiveCrudRepository<Pokemon, Integer> {
}
