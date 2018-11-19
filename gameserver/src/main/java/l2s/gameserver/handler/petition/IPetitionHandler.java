package l2s.gameserver.handler.petition;

import l2s.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 22:15/25.07.2011
 */
public interface IPetitionHandler
{
	void handle(Player player, int id, String txt);
}