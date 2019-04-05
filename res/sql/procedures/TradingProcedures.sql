/**
  Procedure to propose a trade

  issued in
 */

CREATE PROCEDURE trading_add_trade(
  -- IN s_id INT,
  IN seller VARCHAR(30),
  -- IN b_id INT,
  IN buyer VARCHAR(30),
  IN in_price INT,
  IN property_id INT
)
  BEGIN -- seller_id, buyer_id, price, property_id
    DECLARE seller_id INT;
    DECLARE buyer_id INT;
    SELECT p.player_id FROM player p, account a WHERE seller = a.username AND p.user_id = a.user_id AND p.active = 1 INTO seller_id;
    SELECT p.player_id FROM player p, account a WHERE buyer = a.username AND p.user_id = a.user_id AND p.active = 1 INTO buyer_id;
    -- INSERT INTO trading VALUES(DEFAULT, s_id, b_id, in_price, property_id, DEFAULT);
    INSERT INTO trading VALUES(DEFAULT, seller_id, buyer_id, in_price, property_id, DEFAULT);
  END;

DROP PROCEDURE trading_add_trade;

CALL trading_add_trade(15, 14, 69, 2);

CALL trading_add_trade('giske', 'yourmum', 6969, 3);

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

DROP PROCEDURE trading_get_trade;

CALL trading_get_trade('giske');

-- ---------------

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

CALL trading_get_trade2('giske');

DROP PROCEDURE trading_get_trade2;



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

CALL trading_accept_trade('giske', 'yourmum');

CALL trading_accept_trade('yourmum', 'giske');

DROP PROCEDURE trading_accept_trade;


drop view if exists seller;
drop view if exists buyer;
drop view if exists trading_view;


create view seller as
  select (username) sellername, seller_id from trading join player p on trading.seller_id = p.player_id
    join account a on p.user_id = a.user_id;

create view buyer as
  select (username) buyername, buyer_id from trading join player p on trading.buyer_id = p.player_id
    join account a on p.user_id = a.user_id;

create view trading_view as
  select buyer.*, seller.*, price, prop_id from buyer join seller join trading on trading.seller_id = seller.seller_id and trading.buyer_id = buyer.buyer_id;