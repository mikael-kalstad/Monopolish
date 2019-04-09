/**
  GameProcedures contains procedures to create, update, remove and sort properties
 */


/**
  Drops
 */
drop procedure if exists property_create;
drop procedure if exists property_update;
drop procedure if exists player_property;
drop procedure if exists available_property;
drop procedure if exists position_property;
drop procedure if exists property_clean;
DROP PROCEDURE IF EXISTS property_get_all;
DROP PROCEDURE IF EXISTS property_get_color_set;
DROP PROCEDURE IF EXISTS property_get_by_owner;




/**
Procedure to retrieve the Properties of a game
  in g_id: game_id

  out(columnIndex/columnLabel):
  1/property_id
  2/name -- property name
  3/price -- initial price
  4/position -- the position of the property
  5/categorycolor -- the category color of the property
  6/username -- owner's username
  7/property_type -- street/boat/transport company
  8/rent_level -- the number of houses

issued by: PropertyDAO.getAllProperties()
 */
DELIMITER $$
CREATE PROCEDURE property_get_all(
  IN g_id INT
)
BEGIN
  -- Delete all data related to this game session before starting
  DELETE FROM gameproperty WHERE gameproperty.game_id=g_id;

  INSERT INTO gameproperty (game_id, property_id)
  SELECT g_id, property_id FROM property;

  select gp.property_id, p.name, p.price, p.position, p.categorycolor, IFNULL(a.username, ''), p.property_type,
         gp.rent_level, gp.pawned
  from gameproperty gp
  join property p on gp.property_id = p.property_id
  left join player p2 on gp.user_id = p2.user_id
  left join account a on p2.user_id = a.user_id
  WHERE gp.game_id=g_id;
END $$
-- --------------------------------------------------

/**
Procedure to retrieve all properties of a given color
  in g_id: game_id
  in color_hex: hex code for the color

  out(columnIndex/columnLabel):
  1/property_id
  2/name -- property name
  3/price -- initial price
  4/position -- the position of the property
  5/categorycolor -- the category color of the property
  6/username -- owner's username
  7/property_type -- street/boat/transport company
  8/rent_level -- the number of houses
  9/pawned -- Is property pawned?

issued by: PropertyDAO.getColorSet()
 */
DELIMITER $$
CREATE PROCEDURE property_get_color_set(
  IN g_id INT,
  IN color_hex VARCHAR(10)
)
  BEGIN
    select gp.property_id, p.name, p.price, p.position, p.categorycolor, IFNULL(a.username, ''), p.property_type, gp.rent_level,
           gp.pawned
    from gameproperty gp
           join property p on gp.property_id = p.property_id
           left join player p2 on gp.user_id = p2.user_id
           left join account a on p2.user_id = a.user_id
    WHERE gp.game_id=g_id AND p.categorycolor LIKE color_hex group by p.property_id;
  END $$
DELIMITER ;


/**
Procedure to update a gameProperty

  in prop_id: property_id
  in g_id: game_id
  in pawn: pawn state
  in u_name: username
  in r_level: rent level

issued by: PropertyDAO.updateProperty()
 */
delimiter $$
create procedure property_update(
    in prop_id int,
    in g_id int,
    in pawn bit,
    in u_name varchar (30),
    in r_level INT,
    IN pawn BIT
  )
  begin
    declare u_id int;

  if u_name is null then set u_id = null;

  else select user_id  into u_id from account where username LIKE u_name;

  end if;

   update gameproperty set pawned = pawn,
                           user_id = u_id,
                           rent_level = r_level
      where property_id = prop_id and game_id = g_id;
end $$
delimiter ;



/**
Procedure to retrieve all properties belonging to a given player
  in g_id: game_id
  in username: username of the owner

  out(columnIndex/columnLabel):
  1/property_id
  2/name -- property name
  3/price -- initial price
  4/position -- the position of the property
  5/categorycolor -- the category color of the property
  6/username -- owner's username
  7/property_type -- street/boat/transport company
  8/rent_level -- the number of houses
  9/pawned -- Is property pawned?

issued by: PropertyDAO.getPropertiesByOwner()
 */
DELIMITER $$
CREATE PROCEDURE property_get_by_owner(
  IN g_id INT,
  IN username VARCHAR(30)
)
BEGIN
  DECLARE owner_id INT;
  SET owner_id = (SELECT a.user_id
                  FROM account a
                  WHERE a.username LIKE username);

  IF (username IS NULL OR username LIKE '') THEN
    select gp.property_id, p.name, p.price, p.position, p.categorycolor, IFNULL(a.username, ''), p.property_type, gp.rent_level,
           gp.pawned
      from gameproperty gp
      join property p on gp.property_id = p.property_id
      left join player p2 on gp.user_id = p2.user_id
      left join account a on p2.user_id = a.user_id
      WHERE gp.game_id=g_id AND a.username IS NULL group by p.property_id;
  ELSE
    select gp.property_id, p.name, p.price, p.position, p.categorycolor, IFNULL(a.username, ''), p.property_type, gp.rent_level,
           gp.pawned
    from gameproperty gp
      join property p on gp.property_id = p.property_id
      left join player p2 on gp.user_id = p2.user_id
      left join account a on p2.user_id = a.user_id
      WHERE gp.game_id=g_id AND a.username LIKE username group by p.property_id;
  END IF;
END $$
DELIMITER ;



/**
Procedure to retrieve all properties of a given color

  in g_id: game_id

issued by: ?!
 */
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
create procedure property_create(
    in g_id int,
    in prop_id int,
    in user_name varchar(30)
  )
  begin
    declare u_id int;

    -- Get username from user_id
    select user_id  into u_id from account where user_name = username;

    insert into gameproperty(game_id, property_id, user_id) values (g_id, prop_id, u_id);

    select gameproperty.*, property.position, property.price, property.categorycolor, p.property_type
      from gameproperty join property p
        on gameproperty.property_id = p.property_id
          where prop_id = property_id and g_id = game_id;
  commit;

end $$
delimiter ;

*/

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