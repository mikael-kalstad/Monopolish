drop procedure if exists player_create;
drop procedure if exists player_remove;
drop procedure if exists player_update;
drop procedure if exists player_endgame;
drop procedure if exists player_getByGameId;

delimiter $$
create procedure player_create(
    in g_id int,
    in u_name varchar(30)
  )
  begin
    declare u_id int;

    select user_id into u_id from account where u_name = username;

    insert into player(game_id, user_id) values (g_id, u_id);
  commit;

end $$
delimiter ;


delimiter $$
create procedure player_remove(
  in u_name varchar(30),
  in g_id int
  )
begin
  declare u_id int;

  select user_id  into u_id from account where u_name = username;

  update player set active = 2 where user_id = u_id and game_id = g_id;

  IF ((SELECT COUNT(*) FROM player p WHERE p.game_id=g_id AND active=1) < 1) THEN
    UPDATE game SET game.endtime=NOW() WHERE game.game_id=g_id;
  end if;
  commit;
end $$
delimiter ;


delimiter $$
create procedure player_update(
  in u_name varchar(30),
  in g_id int,
  in pos int,
  in moneyChange int,
  in in_jail bit,
  in bankrupt bit,
  in active int,
  in money int
)
begin
  declare u_id int;

  select p.player_id into u_id
  from account a
  join player p on a.user_id = p.user_id
  where u_name = a.username and p.game_id = g_id;

  update player set currentpos = pos,
                    money = moneyChange,
                    injail = in_jail,
                    bankrupt = bankrupt,
                    active = active,
                    money = money
    where player_id = u_id;
  commit;

end $$
delimiter ;


delimiter $$
create procedure player_endgame(
  in gameid int
)
begin
  update player set active = 0 where gameid = game_id and active = 1;

  update player set money = (select (money + sum)
    from (select money, sum(price) as sum from property join player
      on player.user_id = property.user_id group by user_id) as a)
        where player.game_id = gameid and active = 0;
  commit;

end $$
delimiter ;

drop procedure player_getByGameId;

delimiter $$
create procedure player_getByGameId(
  in game_id int
)
begin
  select a.username, p.money, p.currentpos, p.injail, p.bankrupt, p.active, p.money
  from player p
  join account a on p.user_id = a.user_id
  where game_id = p.game_id AND p.active=1
  ORDER BY p.player_id ASC;
  commit;
end $$
delimiter ;

DROP PROCEDURE player_get_highscore;

CREATE PROCEDURE player_get_highscore()
BEGIN

  SELECT username, money
  FROM account
  LEFT JOIN player p on account.user_id = p.user_id
  ORDER BY IFNULL(p.money, 0)
  DESC LIMIT 10;

END;

CAlL player_get_highscore();

-- TESTING: -----------
SELECT money FROM player ORDER BY IFNULL(money, 0);

SELECT IFNULL(money, 0) FROM player;
SELECT
       CASE
         WHEN money IS NULL THEN 'N/A'
         ELSE money
           END AS Result
FROM player;

CREATE PROCEDURE fisk()
  BEGIN
    DECLARE variabel INT;
  SELECT username, money
  FROM account
    LEFT JOIN player p on account.user_id = p.user_id
    ORDER BY money DESC LIMIT 10 INTO variabel;
    SELECT CASE WHEN variabel IS NULL THEN 0 END AS money;

    CREATE VIEW v AS SELECT variabel, money AS value FROM player;
    SELECT * FROM v;

  END;
-- ---------------