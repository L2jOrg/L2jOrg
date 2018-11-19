package l2s.gameserver.utils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;

/**
 * @author VISTALL
 * @date 23:43/17.05.2012
 */
public class PlayerUtils
{
	public static void updateAttackableFlags(Player player)
	{
		player.broadcastRelation();
		for(Servitor servitor : player.getServitors())
			servitor.broadcastCharInfo();
	}
}