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
package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;

import java.util.List;

/**
 * @author Sdw
 */
public class AdenaDistributionRequest extends AbstractRequest {
    private final L2PcInstance _distributor;
    private final List<L2PcInstance> _players;
    private final int _adenaObjectId;
    private final long _adenaCount;

    public AdenaDistributionRequest(L2PcInstance activeChar, L2PcInstance distributor, List<L2PcInstance> players, int adenaObjectId, long adenaCount) {
        super(activeChar);
        _distributor = distributor;
        _adenaObjectId = adenaObjectId;
        _players = players;
        _adenaCount = adenaCount;
    }

    public L2PcInstance getDistributor() {
        return _distributor;
    }

    public List<L2PcInstance> getPlayers() {
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
