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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @author UnAfraid
 */
public class ExInzoneWaiting extends IClientOutgoingPacket {
    private final int _currentTemplateId;
    private final Map<Integer, Long> _instanceTimes;
    private final boolean _sendByClient;

    public ExInzoneWaiting(L2PcInstance activeChar, boolean sendByClient) {
        final Instance instance = InstanceManager.getInstance().getPlayerInstance(activeChar, false);
        _currentTemplateId = ((instance != null) && (instance.getTemplateId() >= 0)) ? instance.getTemplateId() : -1;
        _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(activeChar);
        _sendByClient = sendByClient;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_INZONE_WAITING_INFO);

        writeByte((byte) (_sendByClient ? 0x00 : 0x01)); // Grand Crusade
        writeInt(_currentTemplateId);
        writeInt(_instanceTimes.size());
        for (Entry<Integer, Long> entry : _instanceTimes.entrySet()) {
            final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
            writeInt(entry.getKey());
            writeInt((int) instanceTime);
        }
    }

}
