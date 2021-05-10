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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import io.github.joealisson.primitive.maps.IntLongMap;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.concurrent.TimeUnit;

/**
 * @author UnAfraid
 */
public class ExInZoneWaiting extends ServerPacket {
    private final int _currentTemplateId;
    private final IntLongMap _instanceTimes;
    private final boolean _hide;

    public ExInZoneWaiting(Player activeChar, boolean hide) {
        final Instance instance = InstanceManager.getInstance().getPlayerInstance(activeChar, false);
        _currentTemplateId = ((instance != null) && (instance.getTemplateId() >= 0)) ? instance.getTemplateId() : -1;
        _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(activeChar);
        _hide = hide;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_INZONE_WAITING_INFO, buffer );

        buffer.writeByte(!_hide); // Grand Crusade
        buffer.writeInt(_currentTemplateId);
        buffer.writeInt(_instanceTimes.size());
        for (var entry : _instanceTimes.entrySet()) {
            final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
            buffer.writeInt(entry.getKey());
            buffer.writeInt((int) instanceTime);
        }
    }

}
