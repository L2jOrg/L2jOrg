package handlers.skillconditionhandlers;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpTargetNpcSkillCondition implements SkillCondition {

    public final IntSet npcId;

    protected OpTargetNpcSkillCondition(IntSet npcs) {
        npcId = npcs;
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target)
    {
        return isNpc(target) && npcId.contains(target.getId());
    }
}
