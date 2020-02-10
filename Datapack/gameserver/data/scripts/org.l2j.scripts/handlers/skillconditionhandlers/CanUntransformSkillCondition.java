package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class CanUntransformSkillCondition implements SkillCondition {

    private CanUntransformSkillCondition() {
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        boolean canUntransform = true;
        final Player player = caster.getActingPlayer();
        if (player == null)
        {
            canUntransform = false;
        }
        else if (player.isAlikeDead() || player.isCursedWeaponEquipped())
        {
            canUntransform = false;
        }
        else if (player.isFlyingMounted() && !player.isInsideZone(ZoneType.LANDING))
        {
            player.sendPacket(SystemMessageId.YOU_ARE_TOO_HIGH_TO_PERFORM_THIS_ACTION_PLEASE_LOWER_YOUR_ALTITUDE_AND_TRY_AGAIN); // TODO: check if message is retail like.
            canUntransform = false;
        }

        return canUntransform;
    }

    public static final class Factory extends SkillConditionFactory {
        private static final CanUntransformSkillCondition INSTANCE = new CanUntransformSkillCondition();
        @Override
        public SkillCondition create(Node xmlNode) {
            return INSTANCE;
        }

        @Override
        public String conditionName() {
            return "CanUntransform";
        }
    }
}
