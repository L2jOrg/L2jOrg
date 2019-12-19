package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ReflectMagic extends AbstractStatAddEffect {

    public ReflectMagic(StatsSet params) {
        super(params, Stat.VENGEANCE_SKILL_MAGIC_DAMAGE);
    }
}
