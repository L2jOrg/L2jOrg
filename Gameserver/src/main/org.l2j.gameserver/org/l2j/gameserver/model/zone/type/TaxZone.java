package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.zone.Zone;
import org.l2j.gameserver.model.zone.ZoneId;

/**
 * Tax zone type.
 *
 * @author malyelfik
 */
public class TaxZone extends Zone {
    private int _domainId;
    private Castle _castle;

    public TaxZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equalsIgnoreCase("domainId")) {
            _domainId = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature character) {
        character.setInsideZone(ZoneId.TAX, true);
        if (character.isNpc()) {
            ((Npc) character).setTaxZone(this);
        }
    }

    @Override
    protected void onExit(Creature character) {
        character.setInsideZone(ZoneId.TAX, false);
        if (character.isNpc()) {
            ((Npc) character).setTaxZone(null);
        }
    }

    /**
     * Gets castle associated with tax zone.<br>
     *
     * @return instance of {@link Castle} if found otherwise {@code null}
     */
    public Castle getCastle() {
        // Lazy loading is used because zone is loaded before residence
        if (_castle == null) {
            _castle = CastleManager.getInstance().getCastleById(_domainId);
        }
        return _castle;
    }
}
