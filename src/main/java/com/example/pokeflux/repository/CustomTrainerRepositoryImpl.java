package com.example.pokeflux.repository;

import com.example.pokeflux.configuration.TrainerMapper;
import com.example.pokeflux.model.Trainer;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomTrainerRepositoryImpl implements CustomTrainerRepository {

    DatabaseClient databaseClient;

    public CustomTrainerRepositoryImpl(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Trainer> findTrainerByPokemon(String pokemon) {
        String query = """ 
                select t.id, t.name, t.home_town, t.region, t.gender, array_agg(p.id || '-' || p.name) pokemons from trainer t
                inner join pokemon p on t.id = p.trainer_id where t.id=
                (select t.id from trainer t inner join pokemon p on t.id = p.trainer_id where p.name=:pokemonName) group by t.id;
                """;

        TrainerMapper trainerMapper = new TrainerMapper();

        return databaseClient.sql(query)
                .bind("pokemonName", pokemon)
                .map(trainerMapper::apply)
                .first();
    }

    @Override
    public Mono<Trainer> findTrainerById(int trainerId) {
        String query = """
                select t.id, t.name, t.home_town, t.region, t.gender, array_agg(p.id || '-' || p.name) pokemons from trainer t
                inner join pokemon p on t.id = p.trainer_id where t.id=:trainerId
                group by t.id;
                """;

        TrainerMapper trainerMapper = new TrainerMapper();

        return databaseClient.sql(query)
                .bind("trainerId", trainerId)
                .map(trainerMapper::apply)
                .one();
    }

    @Override
    public Flux<Trainer> findAllTrainers() {
        String query = """
                select t.id, t.name, t.home_town, t.region, t.gender, array_agg(p.id || '-' || p.name) pokemons from trainer t
                inner join pokemon p on t.id = p.trainer_id group by t.id
                """;

        TrainerMapper trainerMapper = new TrainerMapper();

        return databaseClient.sql(query)
                .map(trainerMapper::apply)
                .all();
    }
}
