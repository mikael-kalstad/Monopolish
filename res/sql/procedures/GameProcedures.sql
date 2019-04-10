/**
  GameProcedures contains procedures to create games, change player turns, close games,
 */

/**
  Drops
 */
DROP PROCEDURE IF EXISTS game_insert;
DROP PROCEDURE IF EXISTS game_get_current_player;
DROP PROCEDURE IF EXISTS game_exists;
DROP PROCEDURE IF EXISTS game_set_current_player;
DROP PROCEDURE IF EXISTS game_close;
DROP PROCEDURE IF EXISTS game_get_winner;
DROP PROCEDURE IF EXISTS get_forfeit;
DROP PROCEDURE IF EXISTS set_forfeit;



/**
Procedure to generate a new game
  in lobby: lobbyId
  in user_name: username
  out gameid: gameId of the new game

issued by: GameDAO.insertGame()
 */
CREATE PROCEDURE game_insert(IN lobby int, IN user_name VARCHAR(30), OUT gameid INT)
  proc_label:BEGIN
    DECLARE session INT DEFAULT 0;
    START TRANSACTION;
      -- If this game session already has been created, get that game ID
      SET gameid=(SELECT g.game_id FROM game g
                  LEFT JOIN lobbyname ln ON g.session_code=ln.session_code
                  WHERE ln.lobby_id=lobby LIMIT 1);

      -- If not, be the one who creates the game session!
      IF (gameid IS NULL) THEN
        -- To ensure that a user is not active in two games at the same time, we make sure
        -- to set their status in any other game to LEFT as a fail-safe
        UPDATE player SET player.active=2
        WHERE user_id IN (SELECT user_id FROM lobby WHERE room_id=lobby);

        -- Create a new game
        SET session = (SELECT session_code FROM lobbyname ln WHERE ln.lobby_id=lobby LIMIT 1);
        INSERT INTO game (starttime, session_code)
        VALUES (NOW(), session);
        -- Get the new Game ID
        SET gameid = LAST_INSERT_ID();

        -- Get all players waiting in the lobby and put them in the player table
        INSERT INTO player (game_id, user_id)
        SELECT gameid, l.user_id FROM lobby l WHERE l.room_id=lobby;
      end if;
    COMMIT;
  END;

-- -------------------------------------------------------------
/**
Procedure to retrieve the current player

  in gameid: game_id

  out(columnIndex)
  1: username of the current player

issued by: GameDAO.getCurrentPlayer()
 */
CREATE PROCEDURE game_get_current_player(IN gameid int)
  BEGIN
    SELECT IFNULL(a.username, "") FROM game g
    JOIN player p on g.currentplayer = p.player_id
    JOIN account a on p.user_id = a.user_id
    WHERE g.game_id=gameid;
  END;

-- ----------------------------------------------------------------

/**
Procedure to check if the game already exists

  in gameid: game_id

  out(columnIndex)
  1: gameId of the game, or 0

issued by: ?!
 */
CREATE PROCEDURE game_exists(IN gameid int)
  BEGIN
    SELECT IFNULL(game_id, 0) FROM game WHERE game_id=gameid;
  END;

-- --------------------------------------------------------

/**
Procedure set the new current player

  in gameid: gameId of the game
  in current_username: the new current player's username

issued by: GameDAO.setCurrentPlayer()
 */

CREATE PROCEDURE game_set_current_player(IN gameid INT, IN current_username VARCHAR(30))
  BEGIN
    DECLARE current_player_id INT;

    SET current_player_id = (SELECT p.player_id
    FROM player p
    JOIN account a ON p.user_id = a.user_id
    WHERE a.username LIKE current_username LIMIT 1);

    IF (current_player_id IS NOT NULL) THEN
      UPDATE game
      SET currentplayer=current_player_id
      WHERE game_id=gameid;
    ELSE

    END IF;
  END;

-- -----------------------------------------------------

/**
Procedure to close a game

  in gameid: gameId of the game

issued by: GameDAO.finishGame()
 */
CREATE PROCEDURE game_close(IN gameid INT)
  BEGIN
    START TRANSACTION;
      DELETE FROM chatmessage
        WHERE player_id IN
          (SELECT player_id FROM message_view WHERE game_id = gameid);

      UPDATE game g
      SET g.currentplayer=NULL,
          g.endtime=NOW()
      WHERE g.game_id=gameid;

      DELETE FROM gameproperty WHERE game_id=gameid;
    COMMIT;
  END;

-- ----------------------------------------------------


/**
Procedure to retrieve the current forfeit state of a game
  in gameid: game_id

  out(columnIndex/columnLabel):
  1/forfeit: forfeit stat
issued by: GameDAO.getForfeit()
 */
CREATE PROCEDURE get_forfeit(in gameid int)
  BEGIN
    select forfeit from game where gameid = game.game_id;
  END;



/**
Procedure set the forfeit stat of a game

  in gameid: game_id
  in forfeit_in: the new current forfeit stat

issued by: GameDAO.setForfeit()
 */
CREATE PROCEDURE set_forfeit(in gameid int, in forfeit_in BIT)
BEGIN
  update game set forfeit = forfeit_in where gameid = game_id;
END;

/**
Procedure to get the winner of a game

  in gameid: game_id
  out winner: The winner username

issued by: GameDAO.getWinner()
 */
CREATE PROCEDURE game_get_winner(IN gameid INT, OUT winner VARCHAR(30))
  BEGIN
    SET winner = (SELECT a.username FROM player p
                  JOIN account a on p.user_id = a.user_id
                  WHERE p.game_id=gameid ORDER BY score DESC LIMIT 1);
  END;