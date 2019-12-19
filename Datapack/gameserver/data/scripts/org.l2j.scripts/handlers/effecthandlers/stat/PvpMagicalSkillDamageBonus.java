package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvpMagicalSkillDamageBonus extends AbstractStatEffect {

    public PvpMagicalSkillDamageBonus(StatsSet params) {
        super(params, Stat.PVP_MAGICAL_SKILL_DAMAGE);
    }
}
