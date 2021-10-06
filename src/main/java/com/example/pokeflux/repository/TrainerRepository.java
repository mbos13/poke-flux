package com.example.pokeflux.repository;

import com.example.pokeflux.model.Trainer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TrainerRepository extends CustomTrainerRepository, ReactiveCrudRepository<Trainer, Integer> {
}
