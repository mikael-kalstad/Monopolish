/**
  Procedure to propose a trade

 */
DROP PROCEDURE IF EXISTS trading_add_trade;
DROP PROCEDURE IF EXISTS trading_is_trade;
DROP PROCEDURE IF EXISTS trading_accept_trade;
DROP PROCEDURE IF EXISTS trading_get_trade;
DROP PROCEDURE IF EXISTS trading_get_trade2;
DROP PROCEDURE IF EXISTS trading_get_trade3;
DROP PROCEDURE IF EXISTS trading_remove_trade;

drop view if exists seller;
drop view if exists buyer;
drop view if exists trading_view;

/**
  Procedure to add new trade
 */
/*
CREATE PROCEDURE trading_add_trade(
  -- IN s_id INT,
  IN seller VARCHAR(30),
  -- IN b_id INT,
  IN buyer VARCHAR(30),
  IN s_price INT,
  IN b_price INT,
  IN property_id INT
)
  BEGIN -- seller_id, buyer_id, price, property_id
    DECLARE seller_id INT;
    DECLARE buyer_id INT;
    SELECT p.player_id FROM player p, account a WHERE seller = a.username AND p.user_id = a.user_id AND p.active = 1 INTO seller_id;
    SELECT p.player_id FROM player p, account a WHERE buyer = a.username AND p.user_id = a.user_id AND p.active = 1 INTO buyer_id;
    -- INSERT INTO trading VALUES(DEFAULT, s_id, b_id, in_price, property_id, DEFAULT);
    INSERT INTO trading VALUES(DEFAULT, seller_id, buyer_id, s_price, b_price, DEFAULT);
    INSERT INTO tradeProperty VALUES (DEFAULT, property_id);
  END;

DROP PROCEDURE trading_add_trade;






CREATE PROCEDURE trading_get_trade(
  IN buyer VARCHAR(30)
)
  BEGIN
    DECLARE b_id INT;
    -- DECLARE b_id VARCHAR(30);

    SELECT p.player_id FROM player p, account a WHERE buyer = a.username AND p.user_id = a.user_id AND p.active = 1 INTO b_id;
    -- SELECT a.username FROM player p, account a WHERE buyer = a.username AND p.user_id = a.user_id AND p.active = 1 INTO b_id;

    -- SELECT DISTINCT t.seller_id, t.buyer_id, t.price, t.prop_id
    SELECT a.username, t.prop_id, t.price
    FROM trading t JOIN player p ON t.buyer_id = p.player_id JOIN account a ON p.user_id = a.user_id
    WHERE b_id IN (t.buyer_id, t.seller_id);-- buyer = a.username AND p.user_id = a.user_id AND p.active = 1;
  END;




-- ---------------
DROP PROCEDURE IF EXISTS trading_get_trade2;


CREATE PROCEDURE trading_get_trade2(
  IN buyer VARCHAR(30)
)
  BEGIN
    -- DECLARE b_id INT;
    DECLARE b_id VARCHAR(30);

    SELECT t.sellername, t.buyername, t.price, t.prop_id
    FROM trading_view t
    WHERE t.buyername = buyer;
  end;



CREATE PROCEDURE trading_get_trade3(IN buyer VARCHAR(30))
  BEGIN
    DECLARE b_id INT;
    SELECT p.player_id FROM player p, account a WHERE buyer = a.username AND p.user_id = a.user_id AND p.active = 1 INTO b_id;

    SELECT t.seller_id, t.buyer_id, t.seller_price, t.buyer_price, tp.prop_id
    FROM tradeProperty tp, trading t
    WHERE b_id = t.buyer_id;

    SELECT a.username, tp.prop_id
    FROM trading t, tradeProperty tp JOIN player p ON t.buyer_id = p.player_id JOIN account a ON p.user_id = a.user_id
    WHERE b_id IN (t.buyer_id, t.seller_id);-- buyer = a.username AND p.user_id = a.user_id AND p.active = 1;
  end;

CREATE PROCEDURE trading_get_trade4(IN buyer VARCHAR(30))
  BEGIN
    DECLARE b_id INT;
    SELECT p.player_id FROM player p, account a WHERE buyer = a.username AND p.user_id = a.user_id AND p.active = 1 INTO b_id;
    SELECT tv.sellername, tv.buyername, tv.seller_price, tv.buyer_price, tv.prop_id
    FROM trading_view tv WHERE b_id IN (tv.buyer_id, tv.seller_id);
  end;

CALL trading_get_trade4('giske');


DROP PROCEDURE IF EXISTS trading_accept_trade;

CREATE PROCEDURE trading_accept_trade(
  IN seller VARCHAR(30),
  IN buyer VARCHAR(30)
)
  BEGIN
    DECLARE b_id INT;
    DECLARE s_id INT;
    SELECT p.player_id FROM player p, account a WHERE seller = a.username AND p.user_id = a.user_id AND p.active = 1 INTO s_id;
    SELECT p.player_id FROM player p, account a WHERE buyer = a.username AND p.user_id = a.user_id AND p.active = 1 INTO b_id;

    UPDATE trading t SET t.accepted = 1 WHERE s_id = t.seller_id AND b_id = t.buyer_id;
  end;



CREATE PROCEDURE trading_is_trade(IN uname VARCHAR(30))
  BEGIN
    DECLARE s_id INT;
    DECLARE teller INT;
    DECLARE status BIT;
    SELECT p.player_id FROM player p, account a WHERE uname = a.username AND p.user_id = a.user_id AND p.active = 1 INTO s_id;
    SELECT COUNT(*) FROM trading t WHERE s_id = t.seller_id OR s_id = t.buyer_id INTO teller;
    IF (teller > 0) THEN
      SET status = 1;
    END IF;
    SELECT status;
  end;

DROP PROCEDURE IF EXISTS trading_is_trade;


CALL trading_is_trade('eirik');

CREATE PROCEDURE trading_remove_trade(IN uname VARCHAR(30))
  BEGIN
    DECLARE s_id INT;
    SELECT p.player_id FROM player p, account a WHERE uname = a.username AND p.user_id = a.user_id AND p.active = 1 INTO s_id;

    DELETE FROM tradeProperty WHERE tradeProperty.trade_id = (SELECT trade_id FROM trading WHERE s_id = trading.seller_id);
    DELETE FROM trading WHERE trading.buyer_id = s_id OR trading.seller_id = s_id;
  end;



CREATE PROCEDURE trading_get_trade_status(IN uname VARCHAR(30)) -- returns trade status. 0 = default, 1= accepted, 2= refused;
  SELECT t.accepted FROM trading t WHERE uname = username;






create view seller as
  select (username) sellername, seller_id from trading join player p on trading.seller_id = p.player_id
    join account a on p.user_id = a.user_id;

create view buyer as
  select (username) buyername, buyer_id from trading join player p on trading.buyer_id = p.player_id
    join account a on p.user_id = a.user_id;

create view trading_view as
  select distinct buyer.*, seller.*, seller_price, buyer_price, prop_id
  from buyer join seller join trading JOIN tradeProperty tp
           on trading.seller_id = seller.seller_id and trading.buyer_id = buyer.buyer_id and tp.trade_id = trading.trade_id;

DROP VIEW IF EXISTS trading_view;
*/