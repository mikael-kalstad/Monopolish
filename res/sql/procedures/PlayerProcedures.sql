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

  update player set active = 2 where user_id = u_id and gameid = g_id;
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
  in score int
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
                    score = score
    where player_id = u_id;
  commit;

end $$
delimiter ;


delimiter $$
create procedure player_endgame(
  in game_id int
)
begin
  update player set active = 0 where gameid = game_id and active = 1;

  update player set score = (select (money + sum)
    from (select money, sum(price) as sum from property join player
      on player.user_id = property.user_id group by user_id) as a)
        where player.game_id = game_id and active = 0;
  commit;

end $$
delimiter ;

drop procedure player_getByGameId;

delimiter $$
create procedure player_getByGameId(
  in game_id int
)
begin
  select a.username, p.money, p.currentpos, p.injail, p.bankrupt, p.active, p.score
  from player p
  join account a on p.user_id = a.user_id
  where game_id = p.game_id
  ORDER BY p.player_id ASC;
  commit;
end $$
delimiter ;

DROP PROCEDURE player_get_highscore;

CREATE PROCEDURE player_get_highscore()
BEGIN

  SELECT username, score
  FROM account
  LEFT JOIN player p on account.user_id = p.user_id
  ORDER BY IFNULL(p.score, 0)
  DESC LIMIT 10;

END;

CAlL player_get_highscore();

-- TESTING: -----------
SELECT score FROM player ORDER BY IFNULL(score, 0);

SELECT IFNULL(score, 0) FROM player;
SELECT
       CASE
         WHEN score IS NULL THEN 'N/A'
         ELSE score
           END AS Result
FROM player;

CREATE PROCEDURE fisk()
  BEGIN
    DECLARE variabel INT;
  SELECT username, score
  FROM account
    LEFT JOIN player p on account.user_id = p.user_id
    ORDER BY score DESC LIMIT 10 INTO variabel;
    SELECT CASE WHEN variabel IS NULL THEN 0 END AS score;

    CREATE VIEW v AS SELECT variabel, score AS value FROM player;
    SELECT * FROM v;

  END;
-- ---------------