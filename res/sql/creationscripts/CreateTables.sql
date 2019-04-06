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
DROP TABLE IF EXISTS trading;
DROP TABLE IF EXISTS eventlog;


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
  regdate DATETIME NOT NULL,
  active BIT NOT NULL DEFAULT 0
); # represents the user account

-- lobbynavn
CREATE TABLE lobbyname
(
  lobby_id INT NOT NULL,
  lobbyname VARCHAR(30) NOT NULL,
  CONSTRAINT pk_lobbname PRIMARY KEY(lobby_id)
); # represents the lobby

-- Lobby
CREATE TABLE lobby
(
  room_id INT NOT NULL, -- lobby_id
  user_id INT NOT NULL,
  ready BIT NOT NULL DEFAULT 0,
  CONSTRAINT pk_lobby PRIMARY KEY (room_id, user_id)
); # represents the player-lobby relation and shows if the user is ready to begin a game

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
  active int not null default 1, -- active in game = 1, quit = 2, game is finished = 0
  score INT DEFAULT 0,
  forfeit INT DEFAULT 0,
  free_parking BIT NOT NULL DEFAULT 0,
  forfeit_check bit not null default 0
); # represents a player and its stats

-- active = 1 : aktiv,
-- active = 0 : endgame
-- active = 2 : sluttet selv

create table property(
  property_id INTEGER NOT NULL,
  name VARCHAR(30) NOT NULL,
  price REAL,
  position INTEGER not null DEFAULT 0,
  categorycolor varchar(12),
  property_type int not null,
  primary key(property_id)
); # represents the parts of properties that doesn't change between games

create table game(
  game_id integer not null AUTO_INCREMENT,
  starttime datetime not null,
  endtime datetime,
  currentplayer integer,
  forfeit bit default 0,
  primary key(game_id)
); # represents the current status of the game

create table gameproperty(
  game_id integer not null,
  property_id integer not null,
  pawned bit default 0,
  user_id integer,
  rent_level INTEGER NOT NULL DEFAULT 0, # 0 = no houses and no colorset, 1= colorset and no houses, 2-> = 1 + number of houses
  PRIMARY KEY (game_id, property_id)
); # represents the stats of a property in a game


create table chatmessage(
  player_id integer not null,
  message_id integer not null auto_increment,
  time_in datetime not null,
  message varchar(40),
  primary key (message_id)
); # represents a chatmessage in a from a player

create table eventlog(
  event_id integer not null auto_increment,
  game_id integer not null,
  time_in datetime not null,
  event_text varchar(40),
  primary key (event_id)
); # creates messages that can be distributed to all players, to keep them updated

CREATE TABLE trading(
  trade_id INT NOT NULL AUTO_INCREMENT,
  seller_id INT NOT NULL, -- sellers player_id
  buyer_id INT NOT NULL, -- buyers player_id
  price INT DEFAULT 0,
  prop_id INT NOT NULL,
  accepted BIT NOT NULL DEFAULT 0,
  PRIMARY KEY (trade_id)
); # represents trade deals

/*
ADD FOREIGN KEYS
*/

ALTER TABLE trading
  ADD FOREIGN KEY (seller_id) REFERENCES player(player_id);
ALTER TABLE trading
  ADD FOREIGN KEY (buyer_id) REFERENCES player(player_id);
ALTER TABLE trading
  ADD FOREIGN KEY (prop_id) REFERENCES property(property_id);
-- ADD FOREIGN KEY (prop_id) REFERENCES gameproperty(property_id);

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

ALTER TABLE eventlog
  ADD FOREIGN KEY (game_id) REFERENCES game(game_id);