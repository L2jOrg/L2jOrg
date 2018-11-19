package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.StatsSet;

/**
 * @author Bonux
**/
public class HideHairAccessories extends Skill
{
	public HideHairAccessories(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(!activeChar.isPlayer())
			return false;

		final Inventory inventory = activeChar.getPlayer().getInventory();
		ItemInstance item = inventory.getPaperdollItem(Inventory.PAPERDOLL_HAIR);
		if(item == null)
			item = inventory.getPaperdollItem(Inventory.PAPERDOLL_DHAIR);

		if(item == null)
		{
			activeChar.sendPacket(SystemMsg.PLEASE_EQUIP_THE_HAIR_ACCESSORY_AND_TRY_AGAIN);
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		Player player = target.getPlayer();
		if(player == null)
			return;
		
		player.setHideHeadAccessories(!player.hideHeadAccessories());
		player.sendUserInfo(true);

		if(player.hideHeadAccessories())
			player.sendPacket(SystemMsg.HAIR_ACCESSORIES_WILL_NO_LONGER_BE_DISPLAYED);
		else
			player.sendPacket(SystemMsg.HAIR_ACCESSORIES_WILL_BE_DISPLAYED_FROM_NOW_ON);
	}
}