/**
Procedure to generate a new game
 */

-- DELIMITER $$
DROP PROCEDURE game_insert;

CREATE PROCEDURE game_insert(IN lobby_id int, OUT game_id INT)
  proc_label:BEGIN
    IF ((SELECT p.user_id
        FROM lobby l
        LEFT JOIN player p ON l.user_id=p.user_id LIMIT 1) IS NULL) THEN
      LEAVE proc_label;
    end if;

    -- If no lobby with this ID exists, abort
    IF (lobby_id NOT IN (SELECT room_id FROM lobby)) THEN
      LEAVE proc_label;
    end if;

    -- Create a new game
    INSERT INTO game (starttime)
    VALUES (NOW());
    -- Get the new Game ID
    SET game_id = LAST_INSERT_ID();

    -- Get all players waiting in the lobby and put them in the player table
    INSERT INTO player (game_id, user_id)
    SELECT game_id, l.user_id FROM lobby l WHERE l.room_id=lobby_id;
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
DROP PROCEDURE game_close;

CREATE PROCEDURE game_close(IN gameid INT, IN winner_id INT)
  BEGIN
    IF (winner_id = 0) THEN
      SET winner_id = NULL;
    END IF;

    UPDATE game g
    SET g.currentplayer=NULL,
        g.endtime=NOW(),
        g.winner=winner_id
    WHERE g.game_id=gameid;
  END;
-- END$$

/**
Procedure to retrieve the current player
 */

-- DELIMITER $$
DROP PROCEDURE game_get_winner;

CREATE PROCEDURE game_get_winner(IN game_id INT, OUT winner_id INT)
  BEGIN
    SET winner_id=(SELECT IFNULL(g.winner, 0)
    FROM game g
    WHERE g.game_id=game_id LIMIT 1);
  END;
-- END$$