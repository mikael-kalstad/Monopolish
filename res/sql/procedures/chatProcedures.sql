/**
    chatProcedures contains procedures to add, and get chatMessages
 */
/**
    Drops
*/

DROP PROCEDURE IF EXISTS chat_add;
DROP PROCEDURE IF EXISTS chat_get;
drop view if exists message_view;



/**
  Procedure to add a chat-message

    in user_name:  username of the player
    in message_in: the new chat-message

  issued by: GameDAO.addChatMessage()
 */

CREATE PROCEDURE chat_add(IN user_name varchar(30), in message_in varchar(40))
BEGIN
  declare playerid int;
  select player_id into playerid  from player join account a
    on player.user_id = a.user_id
      where user_name = username
       and player.active = 1;
  insert into chatmessage(player_id, time_in, message)
    values(playerid, now(), message_in);
END;

-- --------------------------------------------------------------------------------
/**
  Procedure to check password on login

    in gameid: id of the current game

    out(columnIndex/columnLabel):
    1/username
    2/time_String -- HH:MI:SS
    3/message -- chat-message

  issued by: GameDAO.getChat()
 */
create procedure chat_get(in gameid int)
  begin
  select username, DATE_FORMAT(time_in, '%T') as time_String, message from message_view join chatmessage on message_view.player_id = chatmessage.player_id
    where game_id = gameid order by time_in asc;
  end;



/**
  View to be used by chat_get with username, userId, gameId and playerId

    out(columnLabel):
    username
    user_id
    game_id
    player_id

  issued by: GameDAO.getChat()
 */
create view message_view as(
  select username, account.user_id, game_id, player_id
  from account
    join player
      on account.user_id = p.user_id
);