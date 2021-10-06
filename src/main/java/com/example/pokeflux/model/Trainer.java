package com.example.pokeflux.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Trainer {

    @Id
    int id;
    String name;
    String homeTown;
    String region;
    String gender;
    List<String> pokemonsOnHand;

    public Trainer(int id, String name, String homeTown, String region, String gender, List<String> pokemonsOnHand) {
        this.id = id;
        this.name = name;
        this.homeTown = homeTown;
        this.region = region;
        this.gender = gender;
        this.pokemonsOnHand = pokemonsOnHand;
    }

    public Trainer(String name, String homeTown, String region, String gender, List<String> pokemonsOnHand) {
        this.name = name;
        this.homeTown = homeTown;
        this.region = region;
        this.gender = gender;
        this.pokemonsOnHand = pokemonsOnHand;
    }

    public Trainer() {
        pokemonsOnHand = new ArrayList<>();
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

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getPokemonsOnHand() {
        return new ArrayList<>(pokemonsOnHand);
    }

    public void setPokemonsOnHand(List<String> pokemonsOnHand) {
        this.pokemonsOnHand = new ArrayList<>(pokemonsOnHand);
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", homeTown='" + homeTown + '\'' +
                ", region='" + region + '\'' +
                ", gender='" + gender + '\'' +
                ", pokemonsOnHand=" + pokemonsOnHand +
                '}';
    }
}
