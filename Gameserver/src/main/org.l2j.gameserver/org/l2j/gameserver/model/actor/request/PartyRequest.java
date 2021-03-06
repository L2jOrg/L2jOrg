/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Objects;

/**
 * @author UnAfraid
 */
public class PartyRequest extends AbstractRequest {
    private final Player _targetPlayer;
    private final Party _party;

    public PartyRequest(Player activeChar, Player targetPlayer, Party party) {
        super(activeChar);
        Objects.requireNonNull(targetPlayer);
        Objects.requireNonNull(party);
        _targetPlayer = targetPlayer;
        _party = party;
    }

    public Player getTargetPlayer() {
        return _targetPlayer;
    }

    public Party getParty() {
        return _party;
    }

    @Override
    public boolean isUsingItem(int objectId) {
        return false;
    }

    @Override
    public void onTimeout() {
        super.onTimeout();
        getPlayer().removeRequest(getClass());
        _targetPlayer.removeRequest(getClass());
    }
}
