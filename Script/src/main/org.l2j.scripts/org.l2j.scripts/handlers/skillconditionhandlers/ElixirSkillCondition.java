package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class ElixirSkillCondition implements SkillCondition {

    private ElixirSkillCondition() {
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        if (!isPlayer(caster)) {
            caster.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }

        if (isPlayer(caster) && caster.getActingPlayer().getStatsData().getElixirsPoints() >= 5 + getElixirBonus(caster.getActingPlayer())) {
            caster.sendPacket(SystemMessageId.YOU_CANNOT_USE_ANY_MORE_ELIXIRS);
            return false;
        }

        return true;
    }

    private int getElixirBonus(Player activeChar) {
        if(activeChar.getLevel() >= 88)
            return 5;
        return 0;
    }

    public static final class Factory extends SkillConditionFactory {
        private static final ElixirSkillCondition INSTANCE = new ElixirSkillCondition();

        @Override
        public SkillCondition create(Node xmlNode) {
            return INSTANCE;
        }

        @Override
        public String conditionName() {
            return "Elixir";
        }
    }

}
