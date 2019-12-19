package handlers.effecthandlers.stat;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Nik
 */
public class MaxCp extends AbstractStatEffect {
    private final boolean heal;

    public MaxCp(StatsSet params) {
        super(params, Stat.MAX_CP);

        heal = params.getBoolean("heal", false);
    }

    @Override
    public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {
        if (heal) {
            ThreadPool.schedule(() -> {
                switch (mode) {
                    case DIFF -> effected.setCurrentCp(effected.getCurrentCp() + amount);
                    case PER -> effected.setCurrentCp(effected.getCurrentCp() + (effected.getMaxCp() * (amount / 100)));
                }
            }, 100);
        }
    }
}
