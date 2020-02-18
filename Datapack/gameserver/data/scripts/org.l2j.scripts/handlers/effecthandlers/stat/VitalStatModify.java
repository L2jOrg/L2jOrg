package handlers.effecthandlers.stat;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author JoeAlisson
 *
 */
public class VitalStatModify extends AbstractStatEffect {

    private final boolean heal;

    private VitalStatModify(StatsSet params) {
        super(params, params.getEnum("stat", Stat.class));
        heal = params.getBoolean("heal", false);
    }

    @Override
    public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {
        if (heal) {
            ThreadPool.schedule(() -> {
                switch (mode) {
                    case DIFF -> instantDiff(effected);
                    case PER -> instantPercent(effected);
                }
            }, 100);
        }
    }

    private void instantDiff(Creature effected) {
        switch (addStat) {
            case MAX_CP -> effected.setCurrentCp(effected.getCurrentCp() + power);
            case MAX_HP -> effected.setCurrentHp(effected.getCurrentHp() + power);
            case MAX_MP -> effected.setCurrentMp(effected.getCurrentMp() + power);
        };
    }

    private void instantPercent(Creature effected) {
        var percent = power / 100;
        switch (mulStat) {
            case MAX_CP -> effected.setCurrentCp(effected.getCurrentCp() + (effected.getMaxCp() * percent));
            case MAX_HP -> effected.setCurrentHp(effected.getCurrentHp() + (effected.getMaxHp() * percent));
            case MAX_MP -> effected.setCurrentMp(effected.getCurrentMp() + (effected.getMaxMp() * percent));
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new VitalStatModify(data);
        }

        @Override
        public String effectName() {
            return "vital-stat-modify";
        }
    }

}
