package org.l2j.gameserver.utils;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;

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