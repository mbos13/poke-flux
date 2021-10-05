package com.example.pokeflux.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table
@Data
public class Pokemon {

    @Id
    private int id;

    private String name;

    private int trainerId;

    public Pokemon() {
    }

    public Pokemon(int id, String name, int trainerId) {
        this.id = id;
        this.name = name;
        this.trainerId = trainerId;
    }

    public Pokemon(String pokemonName, Integer trainerId) {
        this.name = pokemonName;
        this.trainerId = trainerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return id == pokemon.id && trainerId == pokemon.trainerId && Objects.equals(name, pokemon.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, trainerId);
    }
}
