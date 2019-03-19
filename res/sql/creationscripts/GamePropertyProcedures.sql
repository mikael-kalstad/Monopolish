drop procedure if exists property_create;
drop procedure if exists property_update;
drop procedure if exists player_property;
drop procedure if exists available_property;
drop procedure if exists position_property;
drop procedure if exists property_clean;

delimiter $$
create procedure property_create(
    in g_id int,
    in prop_id int
  )
  begin
    insert into gameproperty(game_id, property_id) values (g_id, prop_id);

    select gameproperty.*, property.position, property.price, property.categorycolor
      from gameproperty join property p
        on gameproperty.property_id = p.property_id
          where prop_id = property_id;
  commit;

end $$
delimiter ;

DROP PROCEDURE property_get_all;

DELIMITER $$
CREATE PROCEDURE property_get_all(
  IN game_id INT
)
BEGIN
  -- Delete all data related to this game session before starting
  DELETE FROM gameproperty WHERE gameproperty.game_id=game_id;

  INSERT INTO gameproperty (game_id, property_id, position)
  SELECT game_id, property_id, position FROM property;

  select gp.property_id, p.name, p.price, p.position, p.categorycolor, IFNULL(a.username, '')
  from gameproperty gp
  join property p on gp.property_id = p.property_id
  left join player p2 on gp.user_id = p2.user_id
  left join account a on p2.user_id = a.user_id
  WHERE gp.game_id=game_id;
END $$

delimiter $$
create procedure property_update(
    in prop_id int,
    in g_id int,
    in pawn bit,
    in u_name varchar (30)
  )
  begin
    declare u_id int;

  if u_name is null then set u_id = null;

  else select user_id  into u_id from account where username LIKE u_name;

  end if;

   update gameproperty set pawned = pawn,
                           user_id = u_id
      where property_id = prop_id and game_id = g_id;
end $$
delimiter ;


delimiter $$
create procedure property_clean(
  in g_id int
)
begin
  delete from gameproperty where game_id = g_id;
commit;

end $$
delimiter ;


/*
delimiter $$
create procedure available_property(
)
begin
  select gameproperty.*, property.position, property.price, property.categorycolor
    from gameproperty join property p
      on gameproperty.property_id = p.property_id
        where user_id is null;
  commit;

end $$
delimiter ;



delimiter $$
create procedure player_property(
in u_id int
)
begin
  select gameproperty.*, property.position, property.price, property.categorycolor
    from gameproperty join property p
      on gameproperty.property_id = p.property_id
        where user_id = u_id;
  commit;

end $$
delimiter ;


delimiter $$
create procedure position_property(
  in pos int
)
begin
  select gameproperty.*, property.position, property.price, property.categorycolor
    from gameproperty join property p
      on gameproperty.property_id = p.property_id
        where position = pos;
  commit;

end $$
delimiter ;
*/

