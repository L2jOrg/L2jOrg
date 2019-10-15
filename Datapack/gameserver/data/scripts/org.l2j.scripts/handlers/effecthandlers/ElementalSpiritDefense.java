package handlers.effecthandlers;

import org.l2j.gameserver.engine.elemental.api.ElementalType;
import org.l2j.gameserver.model.StatsSet;

public class ElementalSpiritDefense extends AbstractStatEffect {

    public ElementalSpiritDefense(StatsSet params) {
        super(params, params.getEnum("type", ElementalType.class).getDefenseStat());
    }
}
