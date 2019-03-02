package org.l2j.scripts.handler.onshiftaction;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.admincommands.impl.AdminEditChar;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ExGMViewQuestItemListPacket;
import org.l2j.gameserver.network.l2.s2c.GMHennaInfoPacket;
import org.l2j.gameserver.network.l2.s2c.GMViewItemListPacket;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_Player extends ScriptOnShiftActionHandler<Player>
{
	@Override
	public Class<Player> getClazz()
	{
		return Player.class;
	}

	@Override
	public boolean call(Player p, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
		{
			if(Config.SHOW_TARGET_PLAYER_INVENTORY_ON_SHIFT_CLICK)
			{
				ItemInstance[] items = p.getInventory().getItems();
				int questSize = 0;
				for(ItemInstance item : items)
				{
					if(item.getTemplate().isQuest())
						questSize++;
				}
				player.sendPacket(new GMViewItemListPacket(1, p, items, items.length - questSize));
				player.sendPacket(new GMViewItemListPacket(2, p, items, items.length - questSize));
				player.sendPacket(new ExGMViewQuestItemListPacket(p, items, questSize));
				player.sendPacket(new GMHennaInfoPacket(p));
			}
			return false;
		}

		AdminEditChar.showCharacterList(player, p);
		return true;
	}
}
