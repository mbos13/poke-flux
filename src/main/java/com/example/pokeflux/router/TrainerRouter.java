package com.example.pokeflux.router;

import com.example.pokeflux.handler.TrainerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TrainerRouter {

    private static final String GET_ALL_TRAINERS = "/trainers/fun";
    private static final String GET_ONE_TRAINER = GET_ALL_TRAINERS + "{id}";
    private static final String CATCH_POKEMON = GET_ALL_TRAINERS + "/trainer/{id}/pokemon/{name}";

    @Bean
    public RouterFunction<ServerResponse> trainersRoute(TrainerHandler trainerHandler) {
        return RouterFunctions.route()
                .GET("/trainers/fun", RequestPredicates.accept(MediaType.APPLICATION_JSON), trainerHandler::getAllTrainers)
                .GET("/trainers/fun/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), trainerHandler::getOneTrainer)
                .POST("/trainers/fun", trainerHandler::createTrainer)
                .PUT("/trainers/fun", trainerHandler::updateTrainer)
                .PUT("/trainers/fun/trainer/{id}/pokemon/{name}", trainerHandler::catchPokemon)
                .build();
    }
}
