DROP PROCEDURE IF EXISTS chat_add;
DROP PROCEDURE IF EXISTS chat_get;
drop view if exists message_view;
CREATE PROCEDURE chat_add(IN user_name varchar(30), in message_in varchar(40))
BEGIN
  declare playerid int;
  select player_id into playerid  from player join account a
    on player.user_id = a.user_id
      where user_name = username
       and player.active = 1;
  insert into chatmessage(player_id, time_in, message) values(playerid, now(), message_in);
END;

create view message_view as(
 select username, account.user_id, game_id, player_id from account
   join player p
    on account.user_id = p.user_id
  );

create procedure chat_get(in gameid int)
  begin
  select username, DATE_FORMAT(time_in, '%T') as time_String, message from message_view join chatmessage on message_view.player_id = chatmessage.player_id
    where game_id = gameid order by time_in asc;
  end;
