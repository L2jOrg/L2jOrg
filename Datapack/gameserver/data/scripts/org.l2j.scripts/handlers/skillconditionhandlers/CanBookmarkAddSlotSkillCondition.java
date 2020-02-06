package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.isNull;

public class CanBookmarkAddSlotSkillCondition implements ISkillCondition {

	public final int slots;
	
	public CanBookmarkAddSlotSkillCondition(StatsSet params)
	{
		slots = params.getInt("teleportBookmarkSlots");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		final Player player = caster.getActingPlayer();

		if (isNull(player)) {
			return false;
		}

		if (player.getBookMarkSlot() + slots > 18) {
			player.sendPacket(SystemMessageId.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT);
			return false;
		}
		
		return true;
	}
}
