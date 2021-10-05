package com.example.pokeflux.configuration;

import com.example.pokeflux.model.Pokemon;
import com.example.pokeflux.model.Trainer;
import io.r2dbc.spi.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class TrainerMapper implements BiFunction<Row, Object, Trainer> {
    @Override
    public Trainer apply(Row row, Object o) {
        Integer trainerId = row.get("id", Integer.class);
        String trainerName = row.get("name", String.class);
        String homeTown = row.get("home_town", String.class);
        String region = row.get("region", String.class);
        String gender = row.get("gender", String.class);
        String[] pokemonsString = row.get("pokemons", String[].class);

        List<Pokemon> pokemons = new ArrayList<>();
        for (String pokemon:pokemonsString) {
            String[] pokemonString = pokemon.split("-");
            int id = Integer.parseInt(pokemonString[0]);
            String name = pokemonString[1];
            pokemons.add(new Pokemon(id, name, trainerId));
        }

        Trainer trainer = new Trainer(trainerId, trainerName, homeTown, region, gender, pokemons);
        return trainer;
    }
}
