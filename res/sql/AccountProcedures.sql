/**
    AccountProcedures contains procedures to add, update and verify users in the account table
 */

/**
  Procedure to add new users
 */

-- DELIMITER $$
DROP PROCEDURE account_insert_user;     -- (username, email, password, salt, regdate)
-- Error/exit codes:
-- 0 = no errors, 1 = username allredy taken, 2 = email allredy taken
CREATE PROCEDURE account_insert_user(
    IN uname VARCHAR(30),
    IN mail VARCHAR(50),
    IN password VARCHAR(30),
    IN reg_date DATETIME,
    OUT error_code INT
  )
  BEGIN
    DECLARE salt_pw BINARY(32);
    DECLARE hashed_pwd VARCHAR(64);
    DECLARE test_username VARCHAR(30);
    DECLARE test_email VARCHAR(50);
    DECLARE user_error INT;
    -- error_code
    -- DECLARE EXIT HANDLER FOR 1062 SELECT 1 AS error_code;
  /*
    DECLARE EXIT HANDLER FOR 1062
      BEGIN
        SELECT LOWER(username) FROM account WHERE LOWER(username = uname) INTO test_username;
        SET error_code = 1;
      END;
    */
    /*
    DECLARE EXIT HANDLER FOR 1062
      BEGIN
        SELECT LOWER(email) FROM account WHERE LOWER(email = mail) INTO test_email;
        SET error_code = 2;
      END;
      */

    SET salt_pw = RANDOM_BYTES(32);
    SET hashed_pwd = SHA2(CONCAT(password, salt_pw), 256);

/*
    SELECT LOWER(username) FROM account WHERE LOWER(username = uname) INTO test_username;
    SELECT COUNT(*) FROM account WHERE LOWER(username = 'testman3') INTO;
    SELECT LOWER(email) FROM account WHERE LOWER(email = mail) INTO test_email;
*/
    /*

    IF LOWER(test_username = uname) THEN
      SET error_code = 1;
    ELSEIF LOWER(test_email = mail) THEN
      SET error_code = 2;
    ELSEIF LOWER()
    ELSE
      SET error_code = 0;
    END IF;
    */

    SELECT @error_code;
    INSERT INTO account VALUES(DEFAULT, uname, mail, hashed_pwd, salt_pw, reg_date);

  END;
-- END$$
DROP PROCEDURE account_insert_user;
-- TEST:
CALL account_insert_user('giske888', 'giske888@damer.no', 'damer', DATE('2019-02-02 20:00:00'), @error_code);

/**
  Procedure to check password on login
  Takes in username or email
 */

DROP PROCEDURE account_validate_user;
-- return: username, email, regdate, highscore
CREATE PROCEDURE account_validate_user(IN uname VARCHAR(30), IN password VARCHAR(30))
  BEGIN

    DECLARE salt_pw BINARY(32);
    DECLARE hashed_pwd VARCHAR(64);

    SELECT salt FROM account WHERE uname = account.username OR uname = account.email INTO salt_pw;
    SELECT SHA2(CONCAT(password, salt_pw), 256) INTO hashed_pwd;
    SELECT username, email, regdate FROM account WHERE hashed_pwd = account.hashed_password;
  END;

-- TEST:
CALL account_validate_user('giske@damer.no', 'damer');

/**
  Procedure to reset password
 */
DROP PROCEDURE account_reset_password;

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

    SET new_salt = RANDOM_BYTES(32);

    SELECT SHA2(CONCAT(old_password, old_salt), 256) INTO old_pwd_hashed;
    -- SELECT hashed_password FROM account
    SELECT SHA2(CONCAT(new_password, new_salt), 256) INTO new_pwd_hashed;

    -- SELECT username FROM account WHERE uname = account.username;
    -- UPDATE old_pwd_hashed
    UPDATE account SET hashed_password = new_pwd_hashed AND salt = new_salt WHERE uname = account.username;
    SELECT username, email, regdate FROM account WHERE new_pwd_hashed = account.hashed_password;
  END;


/**
  Procedure to get top 10 highscores
 */
DROP PROCEDURE account_get_highscore;

CREATE PROCEDURE account_get_highscore()
  BEGIN
    SELECT username, highscore FROM account ORDER BY highscore DESC LIMIT 10;
  END;

/**
  Procedure to generate test-users
 */
DROP PROCEDURE account_generate_test_users;

CREATE PROCEDURE account_generate_test_users()
  BEGIN
    CALL account_insert_user('giske', 'giske@damer.no', 'damer', DATE('2019-02-02 20:00:00'), @error_code);
    CALL account_insert_user('giske2', 'giske2@damer.no', 'damer', DATE('2019-02-02 20:00:00'), @error_code);
    CALL account_insert_user('testman3', 'test@man.com', 'secret', DATE('2019-02-02 20:00:00'), @error_code);
    CALL account_insert_user('helgeingstad', 'helge@ingstad', 'båt', DATE('2019-02-02 20:00:00'), @error_code);
    CALL account_insert_user('jesus', 'jesus@jesus.no', 'gud', DATE('2019-02-02 20:00:00'), @error_code);
    CALL account_insert_user('gud', 'gud@gud.no', 'gud', DATE('2019-02-02 20:00:00'), @error_code);
    CALL account_insert_user('test69', 'test69@test.no', 'test', DATE('2019-02-02 20:00:00'), @error_code);
    CALL account_insert_user('giske1337', 'giske1337@damer.no', 'damer', DATE('2019-02-02 20:00:00'), @error_code);
  END;

CALL account_generate_test_users();