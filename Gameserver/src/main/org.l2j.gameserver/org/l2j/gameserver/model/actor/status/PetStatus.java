/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

public class PetStatus extends SummonStatus {
    private int _currentFed = 0; // Current Fed of the Pet

    public PetStatus(Pet activeChar) {
        super(activeChar);
    }

    @Override
    public final void reduceHp(double value, Creature attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHpConsumption) {
        if (getOwner().isDead()) {
            return;
        }

        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);

        if (attacker != null) {
            if (!isDOT && (getOwner().getOwner() != null)) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_RECEIVED_S2_DAMAGE_BY_C1);
                sm.addString(attacker.getName());
                sm.addInt((int) value);
                getOwner().sendPacket(sm);
            }
            getOwner().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);
        }
    }

    public int getCurrentFed() {
        return _currentFed;
    }

    public void setCurrentFed(int value) {
        _currentFed = value;
    }

    @Override
    public Pet getOwner() {
        return (Pet) super.getOwner();
    }
}
