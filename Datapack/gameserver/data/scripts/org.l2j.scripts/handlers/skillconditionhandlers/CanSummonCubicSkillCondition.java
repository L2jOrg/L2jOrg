package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class CanSummonCubicSkillCondition implements SkillCondition {

    private CanSummonCubicSkillCondition() {
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target)
    {
        if (!isPlayer(caster) || caster.isAlikeDead() || caster.getActingPlayer().inObserverMode()) {
            return false;
        }

        final Player player = caster.getActingPlayer();
        return !player.inObserverMode() && !player.isMounted() && !player.isSpawnProtected() && !player.isTeleportProtected();
    }

    public static final class Factory extends SkillConditionFactory {
        private static final CanSummonCubicSkillCondition INSTANCE = new CanSummonCubicSkillCondition();

        @Override
        public SkillCondition create(Node xmlNode) {
            return INSTANCE;
        }

        @Override
        public String conditionName() {
            return "CanSummonCubic";
        }
    }
}
