/*
Creates all database tables in Monopolish.

Author:   eirikhem, lisawil
Date:     07.03.2019
Version:  1.0
 */
-- Disable foreign key check
SET FOREIGN_KEY_CHECKS=0;

-- Drop tables
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS lobbyname;
DROP TABLE IF EXISTS lobby;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS property;
DROP TABLE IF EXISTS gameproperty;
DROP TABLE IF EXISTS chatmessage;


-- Enable again
SET FOREIGN_KEY_CHECKS=1;

/*
CREATE TABLES
 */

-- User
CREATE TABLE account
(
  user_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  email VARCHAR(50) NOT NULL UNIQUE,
  hashed_password VARCHAR(64) NOT NULL,
  salt BINARY(32) NOT NULL,
  regdate DATETIME NOT NULL
);

-- lobbynavn
CREATE TABLE lobbyname
(
  lobby_id INT NOT NULL,
  lobbyname VARCHAR(30) NOT NULL,
  CONSTRAINT pk_lobbname PRIMARY KEY(lobby_id)
);

-- Lobby
CREATE TABLE lobby
(
  room_id INT NOT NULL,
  user_id INT NOT NULL,
  ready BIT NOT NULL DEFAULT 0,
  CONSTRAINT pk_lobby PRIMARY KEY (room_id, user_id)
);

-- Player
CREATE TABLE player
(
  player_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  game_id INT NOT NULL,
  money INT NOT NULL DEFAULT 0,
  currentpos INT NOT NULL DEFAULT 0,
  injail BIT NOT NULL DEFAULT 0,
  bankrupt BIT NOT NULL DEFAULT 0,
  active int not null default 1,
  score INT DEFAULT 0
);

-- active = 1 : aktiv,
-- active = 0 : endgame
-- active = 2 : sluttet selv

create table property(
  property_id INTEGER NOT NULL,
  name VARCHAR(30) NOT NULL,
  price REAL,
  position INTEGER not null DEFAULT 0,
  categorycolor varchar(12),
  primary key(property_id)
);

create table game(
  game_id integer not null AUTO_INCREMENT,
  starttime datetime not null,
  endtime datetime,
  currentplayer integer,
  primary key(game_id)
);

create table gameproperty(
  game_id integer not null,
  property_id integer not null,
  position int not null DEFAULT 0,
  pawned bit default 0,
  user_id integer,
  constraint pk_gameproperty PRIMARY KEY (game_id, property_id)
);


create table chatmessage(
  player_id integer not null,
  message_id integer not null auto_increment,
  time_in datetime not null,
  message varchar(40),
  primary key (message_id, player_id)
);

/*
ADD FOREIGN KEYS
 */

ALTER TABLE lobby
  ADD FOREIGN KEY (room_id) REFERENCES lobbyname(lobby_id);
ALTER TABLE lobby
ADD FOREIGN KEY (user_id) REFERENCES account(user_id);

ALTER TABLE player
  ADD FOREIGN KEY (user_id) REFERENCES account(user_id);
ALTER TABLE player
  ADD FOREIGN KEY (game_id) REFERENCES game(game_id);

ALTER TABLE gameproperty
  ADD foreign key (game_id) references game(game_id);
ALTER TABLE gameproperty
  ADD foreign key (property_id) references property(property_id);
ALTER TABLE gameproperty
  ADD foreign key (user_id) references player(user_id);

ALTER TABLE chatmessage
  ADD FOREIGN KEY (player_id) REFERENCES player(player_id) ;