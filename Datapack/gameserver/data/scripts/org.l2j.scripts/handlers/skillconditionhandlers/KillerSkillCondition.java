package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public class KillerSkillCondition implements SkillCondition {

    private KillerSkillCondition() {

    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        return isPlayer(caster) && caster.getActingPlayer().getPkKills() > 0;
    }

    public static final class  Factory extends SkillConditionFactory {

        private static final KillerSkillCondition INSTANCE = new KillerSkillCondition();

        @Override
        public SkillCondition create(Node xmlNode) {
            return INSTANCE;
        }

        @Override
        public String conditionName() {
            return "Killer";
        }
    }


}
