package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * Tax zone type.
 *
 * @author malyelfik
 */
public class TaxZone extends Zone {
    private int domainId;
    private Castle castle;

    public TaxZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equalsIgnoreCase("domainId")) {
            domainId = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.TAX, true);
        if (isNpc(creature)) {
            ((Npc) creature).setTaxZone(this);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.TAX, false);
        if (isNpc(creature)) {
            ((Npc) creature).setTaxZone(null);
        }
    }

    /**
     * Gets castle associated with tax zone.<br>
     *
     * @return instance of {@link Castle} if found otherwise {@code null}
     */
    public Castle getCastle() {
        // Lazy loading is used because zone is loaded before residence
        if (castle == null) {
            castle = CastleManager.getInstance().getCastleById(domainId);
        }
        return castle;
    }
}
