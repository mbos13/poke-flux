package com.example.pokeflux.service;

import reactor.core.publisher.Mono;

public interface TrainerService {

    Mono<?> catchPokemon(int trainerId, String pokemonName);
}
