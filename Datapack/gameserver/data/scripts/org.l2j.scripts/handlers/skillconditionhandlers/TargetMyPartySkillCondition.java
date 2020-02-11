package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class TargetMyPartySkillCondition implements SkillCondition {

    public final boolean includeCaster;

    protected TargetMyPartySkillCondition(boolean includeCaster) {
        this.includeCaster = includeCaster;
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {

        if (!isPlayer(target)) {
            return false;
        }

        final Party party = caster.getParty();
        final Party targetParty = target.getActingPlayer().getParty();
        return ((party == null) ? (includeCaster && (caster == target)) : (includeCaster ? party == targetParty : (party == targetParty) && (caster != target)));
    }
}
