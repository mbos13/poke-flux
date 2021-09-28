package com.example.pokeflux.controller;

import com.example.pokeflux.model.Trainer;
import com.example.pokeflux.repository.ReactiveTrainerRepository;
import com.example.pokeflux.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private static final int DELAY_PER_ITEM_MS = 1000;

    @Autowired
    ReactiveTrainerRepository reactiveTrainerRepository;

    @Autowired
    TrainerService trainerService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Trainer> getAll() {
        return reactiveTrainerRepository.findAll()
                .delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

    @GetMapping("/trainer/{id}")
    public Mono<Trainer> getOneTrainer(@PathVariable int id) {
        return reactiveTrainerRepository.findById(id);
    }

    @PostMapping
    public Mono<Trainer> createTrainer(@RequestBody Trainer trainer) {
        return reactiveTrainerRepository.save(trainer);
    }

    @PutMapping
    public Mono<Trainer> updateTrainer(@RequestBody Trainer trainer) {
        return reactiveTrainerRepository.save(trainer);
    }

    @PutMapping("/trainer/{id}/pokemon/{name}")
    public Mono<?> catchPokemon(@PathVariable int id, @PathVariable String name) {
        return trainerService.catchPokemon(id, name);
    }
}
