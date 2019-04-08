/**
    AccountProcedures contains procedures to add, update and verify users in the account table
 */
/**
    Drops
*/

DROP PROCEDURE IF EXISTS account_insert_user;
DROP PROCEDURE IF EXISTS account_validate_user;
DROP PROCEDURE IF EXISTS account_reset_password;
DROP PROCEDURE IF EXISTS account_reset_password;
DROP PROCEDURE IF EXISTS account_getUserid;
DROP PROCEDURE IF EXISTS account_set_inactive;
DROP PROCEDURE IF EXISTS account_get_active;
DROP PROCEDURE IF EXISTS account_games_played;
DROP PROCEDURE IF EXISTS account_highscore;

-- -----------------------------------------------
/**
  Procedure to add new users

    in uname:  username of the player
    in mail:  game_id of the current game
    in password: the new user's password
    in reg_date: the datetime when the user registered

  issued by: AccountDAO.insertAccount()
 */

-- (username, email, password, salt, regdate)
-- Error/exit codes:
-- 0 = no errors, 1 = username already taken, 2 = email already taken
CREATE PROCEDURE account_insert_user(
    IN uname VARCHAR(30),
    IN mail VARCHAR(50),
    IN password VARCHAR(30),
    IN reg_date DATETIME
  )
  BEGIN
    DECLARE salt_pw BINARY(32);
    DECLARE hashed_pwd VARCHAR(64);

    SET salt_pw = RAND();
    SET hashed_pwd = SHA2(CONCAT(password, salt_pw), 256);

    INSERT INTO account VALUES(DEFAULT, uname, mail, hashed_pwd, salt_pw, reg_date, DEFAULT);
  END;

-- ----------------------------------------------------------------------------

/**
  Procedure to check password on login

    in uname:  username of the player
    in password: the new user's password

    out(columnIndex/columnLabel):
    1/username
    2/email
    3/regdate -- registration-date
    4/money
  issued by: AccountDAO.getAccountByCredentials()
 */

-- return: username, email, regdate, highscore
CREATE PROCEDURE account_validate_user(IN uname VARCHAR(30), IN password VARCHAR(30))
  BEGIN

    DECLARE salt_pw BINARY(32);
    DECLARE hashed_pwd VARCHAR(64);

    SELECT salt FROM account WHERE uname = account.username OR uname = account.email INTO salt_pw;
    SELECT SHA2(CONCAT(password, salt_pw), 256) INTO hashed_pwd;

    UPDATE `account` SET `active` = 1 WHERE `salt` = salt_pw;

    SELECT username, email, regdate, money
    FROM account
    LEFT JOIN player p ON account.user_id = p.user_id
    WHERE hashed_password = hashed_pwd;

  END;

-- -------------------------------------------------------


/**
  Procedure to reset password



CREATE PROCEDURE account_reset_password(
  IN uname VARCHAR(30),
  IN old_password VARCHAR(30),
  IN new_password VARCHAR(30),
  OUT var BOOL
)
  BEGIN
    DECLARE old_pwd_hashed VARCHAR(64);
    DECLARE new_pwd_hashed VARCHAR(64);
    DECLARE old_salt BINARY(32);
    DECLARE new_salt BINARY(32);

    -- SET new_salt = RANDOM_BYTES(32);
    SET new_salt = RAND();

    SELECT SHA2(CONCAT(old_password, old_salt), 256) INTO old_pwd_hashed;
    -- SELECT hashed_password FROM account
    SELECT SHA2(CONCAT(new_password, new_salt), 256) INTO new_pwd_hashed;

    -- SELECT username FROM account WHERE uname = account.username;
    -- UPDATE old_pwd_hashed
    UPDATE account SET hashed_password = new_pwd_hashed AND salt = new_salt WHERE uname = account.username;
    SELECT username, email, regdate FROM account WHERE new_pwd_hashed = account.hashed_password;
  END;

 */

/**
  Gets the user_id of the player

    in u_name:  username of the player

    out(columnIndex/columnLabel):
    1/user_id

  issued by: ?!
 */

delimiter $$
create procedure account_getUserid(
  in u_name int
)
begin
  select user_id from account where u_name = username;
  commit;
end $$
delimiter ;

-- --------------------------------------

/*
  Sets user to inactive

   in u_name:  username of the player

  issued by: AccountDAO.setInactive()
 */
CREATE PROCEDURE account_set_inactive(IN u_name VARCHAR(30))
  BEGIN
    UPDATE `account` SET `active` = 0 WHERE `username` = u_name;
  END;

-- ----------------------------------------
-- ?!
/*
  Gets the user's active state

   in u_name:  username of the player

   out(columnIndex/columnLabel):
   1/active

  issued by: AccountDAO.getActive()
 */
CREATE PROCEDURE account_get_active(IN u_name VARCHAR(30))
  BEGIN
    SELECT active FROM account WHERE username = u_name;
  END;

-- -----------------------------------------------------


/*
  Gets the amount of games the user has played

   in u_name:  username of the player

   out(columnIndex/columnLabel):
   1/games -- number of played games

  issued by: AccountDAO.getGamesPlayed()
 */

CREATE PROCEDURE account_games_played(IN u_name VARCHAR(30))
BEGIN
  SELECT count(game.game_id) games
    FROM game JOIN player ON game.game_id = player.game_id
      JOIN account ON player.user_id = account.user_id
        WHERE username = u_name;
END;

-- ---------------------------------------------------------------------

/*
  Gets the user's Highscore

   in u_name:  username of the player

   out(columnIndex/columnLabel):
   1/highscore

  issued by: AccountDAO.getHighscore()
 */

CREATE PROCEDURE account_highscore(IN u_name VARCHAR(30))
BEGIN
  SELECT max(score) highscore
  FROM player JOIN account ON player.user_id = account.user_id WHERE username = u_name;
END;


