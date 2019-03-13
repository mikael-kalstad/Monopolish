/**
Procedure to join or create lobby
 */

-- DELIMITER $$
DROP PROCEDURE lobby_insert;

CREATE PROCEDURE lobby_insert(IN account_id int, OUT lobby_id INT)
  BEGIN
    -- Create a view of all current lobbies, with total players in each session
    CREATE OR REPLACE VIEW lobbies AS
      SELECT l.room_id, COUNT(*) AS players
      FROM lobby l
      GROUP BY l.room_id;

    -- Set lobby id to either (in this order):
    -- 1. The session with the most (but below 4) players IF NOT:
    -- 2. New session with one larger room id than the last IF NOT:
    -- 3. New session with room id = 1
    SET lobby_id=(IFNULL((SELECT l.room_id
                          FROM lobbies l
                          WHERE players < 4
                          ORDER BY players DESC
                          LIMIT 1), IFNULL((SELECT (MAX(room_id)+1) FROM lobby), 1)));

    DROP VIEW lobbies;

    INSERT INTO lobby(room_id, user_id)
    VALUES(lobby_id, account_id);
  END;
-- END$$

/**
Procedure to set ready status
 */

-- DELIMITER $$
DROP PROCEDURE lobby_set_player_ready;

CREATE PROCEDURE lobby_set_player_ready(IN room_id INT, IN user_id INT, IN ready BIT)
  BEGIN
    UPDATE lobby l
    SET l.ready=ready
    WHERE l.room_id=room_id AND l.user_id=user_id;
  END;
-- END$$

/**
Procedure to delete lobby
 */

-- DELIMITER $$
DROP PROCEDURE lobby_delete;

CREATE PROCEDURE lobby_delete(IN room_id INT)
  BEGIN
    DELETE FROM lobby WHERE lobby.room_id=room_id;
  END;
-- END$$

/**
Procedure to delete user from lobby
 */

-- DELIMITER $$
DROP PROCEDURE lobby_delete_user;

CREATE PROCEDURE lobby_delete(IN room_id INT, IN user_id INT)
  BEGIN
    DELETE FROM lobby WHERE lobby.room_id=room_id AND lobby.user_id=user_id;
  END;
-- END$$

/**
Procedure to get all users from a lobby
 */

-- DELIMITER $$
DROP PROCEDURE lobby_get_users_in_lobby;

CREATE PROCEDURE lobby_get_users_in_lobby(IN room_id INT)
  BEGIN
    SELECT a.username
    FROM lobby l
    JOIN account a ON a.user_id=l.user_id
    WHERE l.room_id=room_id;
  END;
-- END$$