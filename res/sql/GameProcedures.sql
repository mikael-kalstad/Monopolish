/**
Procedure to generate a new game
 */

-- DELIMITER $$
DROP PROCEDURE game_insert;

CREATE PROCEDURE game_insert(IN lobby_id int, OUT game_id INT)
  BEGIN
    -- Create a new game
    INSERT INTO game (starttime)
    VALUES (NOW());
    -- Get the new Game ID
    SET game_id = LAST_INSERT_ID();

    -- Get all players waiting in the lobby and put them in the player table
    INSERT INTO player (game_id, user_id)
    SELECT game_id, l.user_id FROM lobby l WHERE l.room_id=lobby_id;

    -- Delete all players from the lobby when the game is started
    DELETE FROM lobby l WHERE l.room_id=lobby_id;
  END;
-- END$$