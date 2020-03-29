package handlers.itemhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isMonster;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author l3x
 */
public final class Harvester implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!Config.ALLOW_MANOR)
		{
			return false;
		}
		else if (!isPlayer(playable))
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final List<ItemSkillHolder> skills = item.getSkills(ItemSkillType.NORMAL);
		if (skills == null)
		{
			LOGGER.warn(": is missing skills!");
			return false;
		}
		
		final Player activeChar = playable.getActingPlayer();
		final WorldObject target = activeChar.getTarget();
		if (!isMonster(target) || !((Creature) target).isDead())
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		skills.forEach(holder -> activeChar.useMagic(holder.getSkill(), item, false, false));
		return true;
	}
}
