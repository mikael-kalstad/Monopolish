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
DROP TABLE IF EXISTS lobby;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS property;
DROP TABLE IF EXISTS gameproperty;

-- Enable again
SET FOREIGN_KEY_CHECKS=1;

/*
CREATE TABLES
 */

-- User
CREATE TABLE account
(
  userid INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL,
  email VARCHAR(50) NOT NULL,
  password BINARY(32) NOT NULL,
  salt BINARY(32) NOT NULL,
  regdate DATETIME NOT NULL,
  highscore INT NOT NULL DEFAULT 0
)

-- Lobby
CREATE TABLE lobby
(
  roomid INT NOT NULL AUTO_INCREMENT,
  userid INT NOT NULL,
  ready BIT NOT NULL DEFAULT 0,
  CONSTRAINT pk_lobby PRIMARY KEY (roomid, userid)
)

-- Player
CREATE TABLE player
(
  userid INT NOT NULL,
  gameid INT NOT NULL,
  money INT NOT NULL DEFAULT 0,
  currentpos INT NOT NULL DEFAULT 0,
  injail BIT NOT NULL DEFAULT 0,
  bankrupt BIT NOT NULL DEFAULT 0,
  CONSTRAINT pk_player PRIMARY KEY (userid, gameid)
)

/*
ADD FOREIGN KEYS
 */

ALTER TABLE lobby
ADD FOREIGN KEY (userid) REFERENCES account(userid);

ALTER TABLE player
ADD FOREIGN KEY (userid) REFERENCES account(userid);
ALTER TABLE player
ADD FOREIGN KEY (gameid) REFERENCES game(gameid);

