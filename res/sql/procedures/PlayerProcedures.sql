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

  -- Get username from user_id
  select user_id  into u_id from account where u_name = username;

  -- Update this player's status, and release any property that they might own
  update player set active = 2 where user_id = u_id and game_id = g_id;
  update gameproperty set user_id = null where game_id = g_id and user_id = u_id;

  -- If this is the last player to leave, close the game
  IF ((SELECT COUNT(*) FROM player p WHERE p.game_id=g_id AND p.active=1) < 1) THEN
    CALL game_close(g_id);
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

  update player set score = (select (money + sum)
    from (select money, sum(price) as sum from property join player
      on player.user_id = property.user_id group by user_id) as a)
        where player.game_id = gameid and active = 0;
  commit;

end $$
delimiter ;

drop procedure if exists player_getByGameId;

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

  SELECT username, score
  FROM account
  LEFT JOIN player p on account.user_id = p.user_id
  ORDER BY IFNULL(p.score, 0)
  DESC LIMIT 10;

END;

CAlL player_get_highscore();

-- default 0, 1 = quit, 2 = continue
CREATE PROCEDURE player_set_forfeit(
  IN username VARCHAR(30),
  IN game_id INT,
  IN forfeit_value INT
)
  BEGIN
    UPDATE player p, account a SET forfeit = forfeit_value WHERE p.game_id = game_id AND a.username = username;
  END;

CALL player_set_forfeit(1, 1, 1);

DROP PROCEDURE player_get_forfeit;

CREATE PROCEDURE player_get_forfeit(IN game_id INT)
  BEGIN
    DECLARE quit INT;
    DECLARE fortset INT;
    SELECT COUNT(p.forfeit) FROM player p, account a WHERE game_id = p.game_id AND p.forfeit = 1 INTO quit;
    SELECT COUNT(p.forfeit) FROM player p, account a WHERE game_id = p.game_id AND p.forfeit = 2 INTO fortset;
    SELECT quit, fortset;


  END;

CALL player_get_forfeit(1);