package com.example.pokeflux;

import com.example.pokeflux.model.Trainer;
import com.example.pokeflux.repository.ReactiveTrainerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainerControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ReactiveTrainerRepository trainerRepository;

    private List<Trainer> trainers = Arrays.asList(
            new Trainer(1, "Ash", "Pallet Town", "Kanto", "male", Collections.singletonList("pikachu")),
            new Trainer(2, "Misty", "Pallet Town", "Kanto", "female", Collections.singletonList("Staryu")),
            new Trainer(3, "Brock", "Pallet Town", "Kanto", "male", Collections.singletonList("Onix")),
            new Trainer(4, "Dawn", "Pallet Town", "Kanto", "female", Collections.singletonList("Mamoswine"))
    );

    @Before
    public void setUp() {
        trainerRepository.deleteAll()
                .thenMany(Flux.fromIterable(trainers))
                .flatMap(trainerRepository::save)
                .doOnNext(System.out::println)
                .then()
                .block();
    }

    @Test
    public void testGetAllTrainers() {
        webTestClient.get().uri("/trainers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Trainer.class)
                .hasSize(4)
                .consumeWith(System.out::println);
    }

    @Test
    public void testGetTrainer() {
        webTestClient.get().uri("/trainers/trainer/{id}", trainers.get(0).getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Trainer.class)
                .consumeWith(System.out::println);
    }

    @Test
    public void testCreateTrainer() {
        Trainer trainer = new Trainer("Iris", "Town", "Region", "female", Collections.emptyList());

        webTestClient.post().uri("/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(trainer), Trainer.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Iris")
                .jsonPath("$.homeTown").isEqualTo("Town")
                .jsonPath("$.region").isEqualTo("Region")
                .jsonPath("$.gender").isEqualTo("female")
                .consumeWith(System.out::println);
    }

    @Test
    public void testUpdateTrainer() {
        Trainer trainer = trainers.get(1);
        trainer.setRegion("New region");

        webTestClient.put().uri("/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(trainer), Trainer.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo("Misty")
                .jsonPath("$.region").isEqualTo("New region")
                .consumeWith(System.out::println);
    }

}
