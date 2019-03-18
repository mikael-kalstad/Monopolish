drop procedure if exists player_create;
drop procedure if exists player_remove;
drop procedure if exists player_update;
drop procedure if exists player_clean;
drop procedure if exists player_getByGameId;

delimiter $$
create procedure player_create(
    in g_id int,
    in u_name varchar(30)
  )
  begin
    declare u_id int;

    select userid  into u_id from account where u_name = username;

    insert into player(game_id, userid) values (g_id, u_id);
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
  in moneyChange int
)
begin
  declare u_id int;

  select user_id  into u_id from account where u_name = username and game_id = g_id;

  update player set position = pos and money = moneyChange
    where user_id = u_id;
  commit;

end $$
delimiter ;


delimiter $$
create procedure player_(
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


delimiter $$
create procedure player_getByGameId(
  in game_id int
)
begin
  select * from player where game_id = player.game_id;
  commit;

end $$
delimiter ;




