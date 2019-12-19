package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MAtk extends AbstractStatEffect {

    public MAtk(StatsSet params) {
        super(params, Stat.MAGIC_ATTACK);
    }
}
