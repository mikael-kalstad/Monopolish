/**
  Procedure to add new users
 */

-- DELIMITER $$
DROP PROCEDURE account_insert;     -- (username, email, password, salt, regdate)

CREATE PROCEDURE account_insert_user(IN uname VARCHAR(30), IN mail VARCHAR(50), IN password VARCHAR(30), IN reg_date DATETIME)
  BEGIN
    DECLARE salt_pw BINARY(32);
    DECLARE hashed_pwd VARCHAR(64);

    SET salt_pw = RANDOM_BYTES(32);
    SET hashed_pwd = SHA2(CONCAT(password, salt_pw), 256);

    INSERT INTO account VALUES(DEFAULT, uname, mail, hashed_pwd, salt_pw, reg_date, DEFAULT);
  END;
-- END$$

-- TEST:
CALL account_insert_user('Testman', 'test@man.com', 'secret', DATE('2019-02-02 20:00:00'));

/**
  Procedure to check password on login
 */

DROP PROCEDURE checkPassword;

CREATE PROCEDURE account_validate_user(IN uname VARCHAR(30), IN password VARCHAR(30))
  BEGIN

    DECLARE salt_pw BINARY(32);
    DECLARE hashed_pwd VARCHAR(64);

    SELECT salt FROM account WHERE uname = account.username INTO salt_pw;
    SELECT SHA2(CONCAT(password, salt_pw), 256) INTO hashed_pwd;
    SELECT * FROM account WHERE hashed_pwd = account.hashed_password;
  END;

-- TEST:
CALL account_validate_user('Testman', 'secret');