package handler.items;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SystemMessage;

public class Battleground extends SimpleItemHandler
{
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		if(!player.isInSiegeZone())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return false;
		}

		if(!reduceItem(player, item))
			return false;

		sendUseMessage(player, item);

		switch(itemId)
		{
			//Battleground Spell - Shield Master
			case 10143:
				for(int skill : new int[] { 2379, 2380, 2381, 2382, 2383 })
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(skill, 1));
				}
				break;
			// Battleground Spell - Wizard
			case 10144:
				for(int skill : new int[] { 2379, 2380, 2381, 2384, 2385 })
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(skill, 1));
				}
				break;
			// Battleground Spell - Healer
			case 10145:
				for(int skill : new int[] { 2379, 2380, 2381, 2384, 2386 })
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(skill, 1));
				}
				break;
			// Battleground Spell - Dagger Master
			case 10146:
				for(int skill : new int[] { 2379, 2380, 2381, 2388, 2383 })
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(skill, 1));
				}
				break;
			// Battleground Spell - Bow Master
			case 10147:
				for(int skill : new int[] { 2379, 2380, 2381, 2389, 2383 })
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(skill, 1));
				}
				break;
			// Battleground Spell - Bow Master
			case 10148:
				for(int skill : new int[] { 2390, 2391 })
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(skill, 1));
				}
				break;
			//Full Bottle of Souls - 5 Souls (For Combat)
			case 10411:
				for(int skill : new int[] { 2499 })
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(skill, 1));
				}
				break;
			default:
				return false;
		}

		return true;
	}
}
