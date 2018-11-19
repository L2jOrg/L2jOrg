package handler.onshiftaction;

import l2s.gameserver.Config;
import l2s.gameserver.handler.admincommands.impl.AdminEditChar;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExGMViewQuestItemListPacket;
import l2s.gameserver.network.l2.s2c.GMHennaInfoPacket;
import l2s.gameserver.network.l2.s2c.GMViewItemListPacket;

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
				player.sendPacket(new GMViewItemListPacket(p, items, items.length - questSize));
				player.sendPacket(new ExGMViewQuestItemListPacket(p, items, questSize));
				player.sendPacket(new GMHennaInfoPacket(p));
			}
			return false;
		}

		AdminEditChar.showCharacterList(player, p);
		return true;
	}
}
