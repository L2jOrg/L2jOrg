package handlers.effecthandlers;

import org.l2j.gameserver.data.database.elemental.ElementalType;
import org.l2j.gameserver.model.StatsSet;

public class ElementalSpiritDefense extends AbstractStatEffect {

    public ElementalSpiritDefense(StatsSet params) {
        super(params, params.getEnum("type", ElementalType.class).getDefenseStat());
    }
}
