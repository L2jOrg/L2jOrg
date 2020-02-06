package handlers.effecthandlers.stat;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author NosBit
 */
public class MaxHp extends AbstractStatEffect {
    public final boolean heal;

    public MaxHp(StatsSet params) {
        super(params, Stat.MAX_HP);

        heal = params.getBoolean("heal", false);
    }

    @Override
    public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {
        if (heal) {
            ThreadPool.schedule(() -> {
                if (!effected.isHpBlocked()) {
                    switch (mode) {
                        case DIFF -> effected.setCurrentHp(effected.getCurrentHp() + amount);
                        case PER -> effected.setCurrentHp(effected.getCurrentHp() + (effected.getMaxHp() * (amount / 100)));
                    }
                }
            }, 100);
        }
    }
}