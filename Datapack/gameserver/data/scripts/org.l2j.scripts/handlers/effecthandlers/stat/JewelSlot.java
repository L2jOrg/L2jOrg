package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class JewelSlot extends AbstractStatAddEffect {

    public JewelSlot(StatsSet params) {
        super(params, Stat.BROOCH_JEWELS);
    }
}
