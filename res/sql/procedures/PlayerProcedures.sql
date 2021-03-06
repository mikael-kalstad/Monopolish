/*

 PlayerProcedures contains procedures that manage Players.

*/

/*

  Drops

*/
drop procedure if exists player_create;
drop procedure if exists player_remove;
drop procedure if exists player_update;
drop procedure if exists player_endgame;
drop procedure if exists player_getByGameId;
DROP PROCEDURE IF EXISTS player_set_forfeit;
DROP PROCEDURE IF EXISTS player_get_forfeit;
DROP PROCEDURE IF EXISTS player_get_playerid;
DROP PROCEDURE IF EXISTS player_get_highscore;
DROP PROCEDURE IF EXISTS get_forfeit_check;
DROP PROCEDURE IF EXISTS set_forfeit_check;

/*
removes a player from active gameplay
by setting the active value of the player to 2 in the database
and takes away their properties,
if the player is the last remaining player, call game_close(g_id)

  in u_name:  username of the player
  in g_id:  game_id of the current game

issued by: PlayerDAO.removePlayer()
*/
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
-- ---------------------------------------------------------------------


/*
updates the player table, based on the changes in the game

  in u_name:  username of the player
  in g_id:  game_id of the current game
  in pos:  the current position of a player
  in moneychange:  the current capital of the player
  in in_jail:  shows if the player is in jail or not
  in bankrupt:  shows if the player is bankrupt or not
  in active: shows if player is in game, and eventually how they left

issued by: PlayerDAO.updatePlayer()
*/
delimiter $$
create procedure player_update(
  in u_name varchar(30),
  in g_id int,
  in pos int,
  in moneyChange int,
  in in_jail bit,
  in bankrupt_in bit,
  in active_in int,
  in freeparking BIT
)
begin
  declare u_id int;
# derives user_id from username
  select p.player_id into u_id
  from account a
  join player p on a.user_id = p.user_id
  where u_name = a.username and p.game_id = g_id;

  update player set currentpos = pos,
                    money = moneyChange,
                    injail = in_jail,
                    bankrupt = bankrupt_in,
                    active = active_in,
                    free_parking = freeparking
    where player_id = u_id;
  commit;

end $$
delimiter ;


/*
updates the active value of the players if it is 1 to 0
calculates the score of the players who have active stat=0
(we calculate the score because we choose to delete gameproperties as they serve no other purpose,
and are many and automatically generated)

  in gameid:  game_id of the current game

issued by: PlayerDAO.endgame()
*/
delimiter $$
create procedure player_endgame(
  in gameid int,
  in user_name varchar(30)
)
begin
  declare p_id int;-- player_id
  select player_id into p_id
    from account join player
      on account.user_id = player.user_id
        where user_name = username and gameid = game_id;

  update player set active = 2 where p_id = player_id and active = 1;

  -- calculates and sets score
  update player set score = (select (money + sum)
    from (select money, ifnull(sum(price),0) sum from player left join gameproperty on player.user_id = gameproperty.user_id and player.game_id = gameproperty.game_id
      left join property on property.property_id = gameproperty.property_id
        where player.player_id = p_id and gameid = player.game_id
          group by player_id) as inner_query)
            where player.player_id = p_id;


  IF ((SELECT COUNT(*) FROM player WHERE game_id=gameid AND active <> 2) = 0) THEN
    UPDATE game SET endtime=NOW() WHERE game_id=gameid;
    DELETE FROM gameproperty WHERE gameproperty.game_id=gameid;
  END IF;
end $$

/*
returns the game's players, in ascending order.

  in game_id:  game_id of the current game

  out(columnIndex/columnLabel):
    1/username
    2/money
    3/currentpos
    4/injail
    5/bankrupt
    6/active

issued by: PlayerDAO.getPlayersInGame()
 */
delimiter ;

delimiter $$
create procedure player_getByGameId(
  in game_id int
)
begin
  select a.username, p.money, p.currentpos, p.injail, p.bankrupt, p.active, p.free_parking
  from player p
  join account a on p.user_id = a.user_id
  where game_id = p.game_id AND p.active=1
  ORDER BY p.player_id ASC;
  commit;
end $$
delimiter ;



/*
gives top ten scores with associated username

  out(columnIndex/columnLabel):
    1/username
    2/score

issued by: PlayerDAO.getHighscoreList()
 */
CREATE PROCEDURE player_get_highscore()
BEGIN

  SELECT username, score
  FROM account
  LEFT JOIN player p on account.user_id = p.user_id
  ORDER BY IFNULL(p.score, 0)
  DESC LIMIT 10;

END;



/**
Procedure that sets the forfeit value of a player in a game
default 0, 1 = quit, 2 = continue

  in in_username: username of user
  in in_gam_id: game_id of the current game
  in forfeit_value: the player's vote

issued by: PlayerDAO.setForfeitStatus()
 */
CREATE PROCEDURE player_set_forfeit(
  IN in_username VARCHAR(30),
  IN in_game_id INT,
  IN forfeit_value INT
)
  BEGIN
    UPDATE player p, account a SET forfeit = forfeit_value
      WHERE in_game_id = p.game_id AND in_username = a.username AND p.user_id = a.user_id;
  END;

/**
Procedure that counts how many players want to quit the game

  in game_id: game_id of the current game

  out(columnIndex/columnLabel):
    1/quit -- number of players who wish to quit
    2/not_quit -- number of players who wish to continue

issued by: PlayerDAO.getForfeitStatus()
 */
CREATE PROCEDURE player_get_forfeit(IN game_id INT)
  BEGIN
    DECLARE quit INT;
    DECLARE not_quit INT;

    SELECT COUNT(DISTINCT p.player_id)
      FROM player p, account a
        WHERE game_id = p.game_id AND p.forfeit = 1 INTO quit;

    SELECT COUNT(DISTINCT p.player_id)
      FROM player p, account a
        WHERE game_id = p.game_id AND p.forfeit = 2 INTO not_quit;

    SELECT quit, not_quit;

  END;


/**
Procedure that counts checks if all players have voted.

  in gameid: game_id of the current game

  out(columnIndex/columnLabel):
    1/check_bit -- true if all players have voted

issued by: PlayerDAO.getForfeitCheck()
 */
CREATE PROCEDURE get_forfeit_check(IN gameid INT)
BEGIN
  select if(players > checked, 0, 1) check_bit from
    (select count(player_id) players from
        player where gameid = game_id and active = 1) sub1 join
    (select count(player_id) checked from
        player where gameid = player.game_id and forfeit_check = 1 and active = 1) sub2;
END;


/**
Procedure that sets forfeit_check in DB to check_in

  in gameid: game_id of the current game
  in user_name: the username of the player
  in check_bit: the new value of forfeit_check

issued by: PlayerDAO.setForfeitCheck()
 */
CREATE PROCEDURE set_forfeit_check(IN gameid INT, in user_name varchar(30), in check_in bit)
BEGIN
  declare p_id int;

  select player_id into p_id from
    player join account
      on player.user_id = account.user_id
        where user_name = username
          and player.game_id = gameid;

  update player set forfeit_check = check_in where player_id = p_id;
END;