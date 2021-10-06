package com.example.pokeflux.rest.controller;

import com.example.pokeflux.model.Trainer;
import com.example.pokeflux.repository.TrainerRepository;
import com.example.pokeflux.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    TrainerRepository reactiveTrainerRepository;
    TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerRepository reactiveTrainerRepository, TrainerService trainerService) {
        this.trainerService = trainerService;
        this.reactiveTrainerRepository = reactiveTrainerRepository;
    }

    @GetMapping
    public Flux<Trainer> getAll() {
        return reactiveTrainerRepository.findAllTrainers();
    }

    @GetMapping("/trainer/{id}")
    public Mono<Trainer> getOneTrainer(@PathVariable int id) {
        return reactiveTrainerRepository.findTrainerById(id);
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
