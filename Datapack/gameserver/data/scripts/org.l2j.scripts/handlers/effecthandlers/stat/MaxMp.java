package handlers.effecthandlers.stat;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MaxMp extends AbstractStatEffect {
    public final boolean heal;

    public MaxMp(StatsSet params) {
        super(params, Stat.MAX_MP);
        heal = params.getBoolean("heal", false);
    }

    @Override
    public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {
        if (heal) {
            ThreadPool.schedule(() -> {
                switch (mode) {
                    case DIFF -> effected.setCurrentMp(effected.getCurrentMp() + power);
                    case PER -> effected.setCurrentMp(effected.getCurrentMp() + (effected.getMaxMp() * (power / 100)));
                }
            }, 100);
        }
    }
}
