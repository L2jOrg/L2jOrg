/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.actor.request;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.util.Objects;

/**
 * @author UnAfraid
 */
public class PartyRequest extends AbstractRequest {
    private final L2PcInstance _targetPlayer;
    private final L2Party _party;

    public PartyRequest(L2PcInstance activeChar, L2PcInstance targetPlayer, L2Party party) {
        super(activeChar);
        Objects.requireNonNull(targetPlayer);
        Objects.requireNonNull(party);
        _targetPlayer = targetPlayer;
        _party = party;
    }

    public L2PcInstance getTargetPlayer() {
        return _targetPlayer;
    }

    public L2Party getParty() {
        return _party;
    }

    @Override
    public boolean isUsing(int objectId) {
        return false;
    }

    @Override
    public void onTimeout() {
        super.onTimeout();
        getActiveChar().removeRequest(getClass());
        _targetPlayer.removeRequest(getClass());
    }
}
