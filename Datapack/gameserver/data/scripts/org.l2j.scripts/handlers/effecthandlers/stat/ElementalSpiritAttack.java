package handlers.effecthandlers.stat;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.StatsSet;

/**
 * @author JoeAlisson
 */
public class ElementalSpiritAttack extends AbstractStatAddEffect {

    public ElementalSpiritAttack(StatsSet params) {
        super(params, params.getEnum("type", ElementalType.class).getAttackStat());
    }
}
