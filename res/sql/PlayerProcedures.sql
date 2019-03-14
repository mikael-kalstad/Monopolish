drop procedure if exists player_create;
drop procedure if exists player_remove;

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
  in u_name varchar(30)
)
begin
  declare u_id int;

  select userid  into u_id from account where u_name = username;

  delete * from player where userid = u_id;
  commit;

end $$
delimiter ;


delimiter $$
create procedure player_update(
  in u_name varchar(30),
  in pos int,
  in moneyChange int
)
begin
  declare u_id int;

  select userid  into u_id from account where u_name = username;

  update player set position = pos and money = moneyChange
    where userid = u_id;
  commit;

end $$
delimiter ;