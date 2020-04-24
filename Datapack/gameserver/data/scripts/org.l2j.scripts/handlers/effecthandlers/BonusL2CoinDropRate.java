package handlers.effecthandlers;

import handlers.effecthandlers.stat.AbstractStatEffect;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;


public class BonusL2CoinDropRate extends AbstractStatEffect {
    public BonusL2CoinDropRate(StatsSet params)
    {
        super(params, Stat.BONUS_L2COIN_DROP_RATE);
    }
    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new BonusL2CoinDropRate(data);
        }

        @Override
        public String effectName() {
            return "BonusL2CoinDropRate";
        }
    }

}
