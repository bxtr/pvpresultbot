CREATE TABLE IF NOT EXISTS player (
    id SERIAL PRIMARY KEY,
    player_name VARCHAR(200),
    friend_code VARCHAR(200),
    in_game_nickname VARCHAR(200),
    town_name VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS fight_result (
    id SERIAL PRIMARY KEY,
    one INTEGER,
    two INTEGER,
    result_one INTEGER,
    result_two INTEGER,
    registered BOOLEAN
);

CREATE TABLE IF NOT EXISTS tournament (
    id SERIAL PRIMARY KEY,
    tournament_name VARCHAR(200),
    url VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS tournament_participant (
    id SERIAL PRIMARY KEY,
    player_id INTEGER,
    tournament_id INTEGER
);

CREATE TABLE IF NOT EXISTS team (
    id SERIAL PRIMARY KEY,
    player_id INTEGER,
    tournament_id INTEGER,
    image bytea
);