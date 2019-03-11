package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.model.zone.ZoneId;

/**
 * Tax zone type.
 *
 * @author malyelfik
 */
public class L2TaxZone extends L2ZoneType {
    private int _domainId;
    private Castle _castle;

    public L2TaxZone(int id) {
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
    protected void onEnter(L2Character character) {
        character.setInsideZone(ZoneId.TAX, true);
        if (character.isNpc()) {
            ((L2Npc) character).setTaxZone(this);
        }
    }

    @Override
    protected void onExit(L2Character character) {
        character.setInsideZone(ZoneId.TAX, false);
        if (character.isNpc()) {
            ((L2Npc) character).setTaxZone(null);
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
