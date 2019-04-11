/**
  LobbyProcedures contain procedures to create, join, leave and delete lobbies
 */

/**
  Drops
 */
DROP PROCEDURE IF EXISTS new_lobby;
DROP PROCEDURE IF EXISTS join_lobby;
DROP PROCEDURE IF EXISTS lobby_insert;
DROP PROCEDURE IF EXISTS lobby_set_player_ready;
DROP PROCEDURE IF EXISTS lobby_delete;
DROP PROCEDURE IF EXISTS lobby_delete_user;
DROP PROCEDURE IF EXISTS lobby_get_users_in_lobby;
DROP PROCEDURE IF EXISTS getAllLobbies;
DROP PROCEDURE IF EXISTS getALlReadyInLobby;
DROP PROCEDURE IF EXISTS lobby_removeEmptyLobbies;
DROP PROCEDURE IF EXISTS lobby_get_id;




/**
Procedure to create a new lobby and return its lobby_id

 in username: username
 in lobby_name
 out lobby_id : true if it is a success

 out(columnIndex/columnLabel):
  1/lobby_id

issued by: LobbyDAO.newLobby()

 */
CREATE PROCEDURE new_lobby(IN username VARCHAR(30), IN lobby_name varchar(30), OUT lobby_id INT)
  BEGIN
    DECLARE lobby_id INT;
    DECLARE account_id INT;

    -- Generate unique room code
    DECLARE code INT DEFAULT 0;
    SET code = FLOOR(RAND()*(999999-1+1)+1);
    WHILE (code IN (SELECT session_code FROM game)) DO
      SET code = FLOOR(RAND()*(999999-1+1)+1);
    END WHILE;

    SET lobby_id = (SELECT IFNULL((MAX(room_id)+1), 1)FROM lobby);
    SET account_id = (SELECT a.user_id FROM account a WHERE a.username LIKE username LIMIT 1);
    INSERT INTO lobbyname(lobby_id, lobbyname, session_code) values (lobby_id, lobby_name, code);
    INSERT INTO lobby (room_id, user_id)
    VALUES (lobby_id, account_id);
  END;

-- ------------------------------------

/**
Procedure to add user to a lobby

 in username: username
 in lobby_id
 out ok : true if it is a success

 out(columnIndex/columnLabel):
  1/ok

issued by: LobbyDAO.addPlayer()
 */

CREATE PROCEDURE join_lobby(IN username VARCHAR(30), IN lobby_id INT, OUT ok BIT)
  BEGIN
    declare num_of_players int;
    declare account_id int;
      SET num_of_players = (SELECT COUNT(*) FROM lobby WHERE lobby_id = room_id);
      SET account_id = (SELECT a.user_id FROM account a WHERE a.username LIKE username LIMIT 1);
      SET ok = false;

      IF (num_of_players < 4) THEN
        SET ok = true;
        -- Lock tables so that other players can't join in this exact moment
        INSERT INTO lobby (room_id, user_id)
        VALUES (lobby_id, account_id);
    END IF;
  END;

-- ------------------------------------


/**
Procedure to insert a lobby

  in username: username
  out lobby_id

   out(columnIndex/columnLabel):
    1/lobby_id

issued by: LobbyDAO.insertLobby()
 */
CREATE PROCEDURE lobby_insert(IN username VARCHAR(30), OUT lobby_id INT)
  BEGIN
    declare account_id int;
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

    DROP VIEW if exists lobbies;

    SET account_id = (SELECT a.user_id FROM account a WHERE a.username LIKE username LIMIT 1);

    INSERT INTO lobby(room_id, user_id)
    VALUES(lobby_id, account_id);
  END;

-- -------------------------------------------

/**
Procedure to set ready status

  in roomid:  lobby_id
  in user_name: username
  in ready_in: new ready value

issued by: LobbyDAO.setReady()
 */
CREATE PROCEDURE lobby_set_player_ready(IN roomid INT, IN user_name VARCHAR(30), IN ready_in BIT)
  BEGIN
    declare userid int;
    SET userid = (SELECT a.user_id FROM account a WHERE a.username LIKE user_name LIMIT 1);
    UPDATE lobby l
    SET l.ready=ready_in
    WHERE l.room_id=roomid AND l.user_id=userid;
  END;

-- -------------------------------------------------

/**
Procedure to delete a lobby

  in room_id:  lobby_id

issued by: LobbyDAO.deleteLobby()
 */
CREATE PROCEDURE lobby_delete(IN room_id INT)
  BEGIN
    DELETE FROM lobby WHERE lobby.room_id = room_id;
    DELETE FROM lobbyname WHERE lobby_id = room_id;
  END;

-- -------------------------------------------------------

/**
Procedure to delete user from lobby

  in room_id:  lobby_id
  in username: username

issued by: LobbyDAO.removePlayer()
 */
CREATE PROCEDURE lobby_delete_user(IN room_id INT, IN username VARCHAR(30))
  BEGIN
    DECLARE user_id INT;
    SET user_id = (SELECT a.user_id FROM account a WHERE a.username LIKE username LIMIT 1);
    DELETE FROM lobby WHERE lobby.room_id=room_id AND lobby.user_id=user_id;
  END;

-- ---------------------------------------


/*
Procedure to get all users from a lobby

  in room_id:  lobby_id

  out(columnIndex/columnLabel):
    1/username

issued by: LobbyDAO.getUsersInLobby()
 */
CREATE PROCEDURE lobby_get_users_in_lobby(IN room_id INT)
  BEGIN
    SELECT a.username
    FROM lobby l
    JOIN account a ON a.user_id=l.user_id
    WHERE l.room_id=room_id;
  END;

-- ------------------------------------------

/*
procedure to get all the lobbies

  out(columnIndex/columnLabel):
    1/room_id : lobby_id
    2/username
    3/ready : true if the player is ready
    4/lobbyname : name of the lobby

issued by: LobbyDAO.getAllLobbies()
 */
CREATE PROCEDURE getAllLobbies()
  BEGIN
    SELECT l.room_id, a.username, l.ready, lobbyname FROM lobby l
    JOIN account a ON l.user_id = a.user_id JOIN lobbyname l2 ON l.room_id = l2.lobby_id
    WHERE a.user_id = l.user_id;
  END;

-- ---------------------------------------------------------------


/*
procedure to get the number of players who are ready in a lobby

  in lobby_in:  lobby_id

  out(columnIndex):
    1 : number of ready players

issued by: LobbyDAO.getAllReadyInLobby()
 */
CREATE PROCEDURE getALlReadyInLobby(IN lobby_id INT)
  BEGIN
    SELECT COUNT(*) FROM lobby
    WHERE lobby_id = room_id
    AND ready = 1;
  END;

-- -------------------------------------------------

/*
procedure to delete empty lobbies

issued by: LobbyDAO.removeEmptyLobbies()
 */
CREATE PROCEDURE lobby_removeEmptyLobbies()
  BEGIN
    DELETE FROM lobbyname where lobby_id not in (select room_id from lobby);
  end;

-- ------------------------------------------------------



/*
returns the lobby_id of the lobby the user is in

  in user_name:  username

  out(columnIndex/columnLabel):
    1/room_id

issued by: LobbyDAO.getLobbyId()
 */
CREATE PROCEDURE lobby_get_id(
  in user_name varchar(30)
  )
  BEGIN
    declare u_id integer;

    declare exit handler for SQLEXCEPTION
      begin
        select 100000;
      end;

    Set u_id = (Select user_id from account where user_name = username);
    SELECT room_id from lobby where user_id = u_id;
  end;