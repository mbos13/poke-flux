create table if not exists TRAINER
(
    id serial PRIMARY KEY NOT NULL,
    name VARCHAR(4000),
    home_town VARCHAR(4000),
    region VARCHAR(4000),
    gender VARCHAR(4000)
);

create table if not exists POKEMON
(
    id serial PRIMARY KEY NOT NULL,
    name VARCHAR(4000),
    trainer_id serial NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES TRAINER (id)
);