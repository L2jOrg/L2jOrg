/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
