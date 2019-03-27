/**
Procedure to generate a new game
 */

-- DELIMITER $$
DROP PROCEDURE game_insert;

CREATE PROCEDURE game_insert(IN lobby_id int, IN user_name VARCHAR(30), OUT gameid INT)
  proc_label:BEGIN
    -- If this game session already has been created, get that game ID
    SET gameid=(SELECT p.game_id
    FROM lobby l
    LEFT JOIN player p ON l.user_id=p.user_id
    LEFT JOIN account a on p.user_id = a.user_id
    LEFT JOIN game g on p.game_id = g.game_id
    WHERE a.username LIKE user_name AND l.room_id=lobby_id AND (p.active=1 AND g.endtime IS NULL) LIMIT 1);

    -- If not, be the one who creates the game session!
    IF (gameid IS NULL) THEN
      -- To ensure that a user is not active in two games at the same time, we make sure
      -- to set their status in any other game to LEFT as a fail-safe
      UPDATE player SET player.active=2
      WHERE user_id IN (SELECT user_id FROM lobby WHERE room_id=lobby_id);

      -- Create a new game
      INSERT INTO game (starttime)
      VALUES (NOW());
      -- Get the new Game ID
      SET gameid = LAST_INSERT_ID();

      -- Get all players waiting in the lobby and put them in the player table
      INSERT INTO player (game_id, user_id)
      SELECT gameid, l.user_id FROM lobby l WHERE l.room_id=lobby_id;
    end if;
  END;
-- END$$

/**
Procedure to retrieve the current player
 */

-- DELIMITER $$
DROP PROCEDURE game_get_current_player;

CREATE PROCEDURE game_get_current_player(IN gameid int)
  BEGIN
    SELECT IFNULL(a.username, "") FROM game g
    JOIN player p on g.currentplayer = p.player_id
    JOIN account a on p.user_id = a.user_id
    WHERE g.game_id=gameid;
  END;
-- END$$


/**
Procedure to retrieve the current player
 */

-- DELIMITER $$
DROP PROCEDURE game_exists;

CREATE PROCEDURE game_exists(IN gameid int)
  BEGIN
    SELECT IFNULL(game_id, 0) FROM game WHERE game_id=gameid;
  END;
-- END$$

/**
Procedure to retrieve the current player
 */

-- DELIMITER $$
DROP PROCEDURE game_set_current_player;

CREATE PROCEDURE game_set_current_player(IN gameid INT, IN current_username VARCHAR(30))
  BEGIN
    DECLARE current_player_id INT;
    SET current_player_id = (SELECT p.player_id
    FROM player p
    JOIN account a on p.user_id = a.user_id
    WHERE a.username LIKE current_username LIMIT 1);

    IF (current_player_id IS NOT NULL) THEN
      UPDATE game
      SET currentplayer=current_player_id
      WHERE game_id=gameid;
    END IF;
  END;
-- END$$

/**
Procedure to retrieve the current player
 */

-- DELIMITER $$
DROP PROCEDURE if exists game_close;

CREATE PROCEDURE game_close(IN gameid INT)
  BEGIN

    DELETE FROM chatmessage
      WHERE player_id IN
        (SELECT player_id FROM message_view WHERE game_id = gameid);

    UPDATE game g
    SET g.currentplayer=NULL,
        g.endtime=NOW()
    WHERE g.game_id=gameid;

    DELETE FROM gameproperty WHERE game_id=gameid;
  END;
-- END$$

/**
Procedure to retrieve the current player
 */

-- DELIMITER $$
DROP PROCEDURE if exists game_get_winner;

CREATE PROCEDURE game_get_winner(IN gameid INT, OUT winner_id INT)
  BEGIN
    declare winner_id int;
    select user_id into winner_id from player where game_id = gameid order by score desc LIMIT 1;
  END;
-- END$$