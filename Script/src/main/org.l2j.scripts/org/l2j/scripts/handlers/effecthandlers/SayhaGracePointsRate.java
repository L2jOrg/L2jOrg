package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.scripts.handlers.effecthandlers.stat.AbstractStatAddEffect;

public class SayhaGracePointsRate  extends AbstractStatAddEffect {
    public SayhaGracePointsRate(StatsSet params) {
            super(params, Stat.SAYHA_GRACE_CONSUME_RATE);
        }

    public static class Factory implements SkillEffectFactory {
        @Override
        public AbstractEffect create(StatsSet data) {
            return new SayhaGracePointsRate(data);
        }

        @Override
        public String effectName() {
            return "SayhaGracePointsRate";
        }
    }


}
