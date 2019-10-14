--DROP TABLE player;
--DROP TABLE fight_result;

CREATE TABLE player (
    id serial PRIMARY KEY,
    player_name VARCHAR(200),
    friend_code VARCHAR(200),
    in_game_nickname VARCHAR(200),
    town_name VARCHAR(200)
);

CREATE TABLE fight_result (
    id serial PRIMARY KEY,
    one integer, 
    two integer,
    result_one integer,
    result_two integer,
    registered boolean
);

CREATE TABLE tournament (
    id serial PRIMARY KEY,
    tournament_name VARCHAR(200),
    url VARCHAR(200)
);

CREATE TABLE tournament_participant (
    id serial PRIMARY KEY,
    player_id integer,
    tournament_id integer
);

CREATE TABLE team (
    id serial PRIMARY KEY,
    player_id integer,
    tournament_id integer,
    image bytea
);