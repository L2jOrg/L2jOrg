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
package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;

import java.util.List;

/**
 * @author Sdw
 */
public class AdenaDistributionRequest extends AbstractRequest {
    private final Player _distributor;
    private final List<Player> _players;
    private final int _adenaObjectId;
    private final long _adenaCount;

    public AdenaDistributionRequest(Player activeChar, Player distributor, List<Player> players, int adenaObjectId, long adenaCount) {
        super(activeChar);
        _distributor = distributor;
        _adenaObjectId = adenaObjectId;
        _players = players;
        _adenaCount = adenaCount;
    }

    public Player getDistributor() {
        return _distributor;
    }

    public List<Player> getPlayers() {
        return _players;
    }

    public int getAdenaObjectId() {
        return _adenaObjectId;
    }

    public long getAdenaCount() {
        return _adenaCount;
    }

    @Override
    public boolean isUsing(int objectId) {
        return objectId == _adenaObjectId;
    }

    @Override
    public void onTimeout() {
        super.onTimeout();
        _players.forEach(p ->
        {
            p.removeRequest(AdenaDistributionRequest.class);
            p.sendPacket(ExDivideAdenaCancel.STATIC_PACKET);
        });
    }
}
