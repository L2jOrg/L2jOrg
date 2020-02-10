package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.w3c.dom.Node;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class CanBookmarkAddSlotSkillCondition implements SkillCondition {

	public final int slots;

	private CanBookmarkAddSlotSkillCondition(int slots) {
		this.slots = slots;
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

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new CanBookmarkAddSlotSkillCondition(parseInt(xmlNode.getAttributes(), "slots"));
		}

		@Override
		public String conditionName() {
			return "can-add-bookmark-slot";
		}
	}
}
