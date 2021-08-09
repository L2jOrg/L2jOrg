package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.scripts.handlers.effecthandlers.stat.AbstractStatAddEffect;

public class SayhaGraceExpRate extends AbstractStatAddEffect {
    public SayhaGraceExpRate(StatsSet params)
    {
        super(params, Stat.SAYHA_GRACE_EXP_RATE);
    }

    @Override
    public void pump(Creature effected, Skill skill)
    {
        effected.getStats().mergeAdd(Stat.SAYHA_GRACE_EXP_RATE, (amount / 100));
        // Send exp bonus to player.
        final Player player = effected.getActingPlayer();
        if (player != null)
        {

            //send userboost info
        }
    }
    public static class Factory implements SkillEffectFactory {
        @Override
        public AbstractEffect create(StatsSet data) {
            return new SayhaGraceExpRate(data);
        }

        @Override
        public String effectName() {
            return "SayhaGraceExpRate";
        }
    }
}
