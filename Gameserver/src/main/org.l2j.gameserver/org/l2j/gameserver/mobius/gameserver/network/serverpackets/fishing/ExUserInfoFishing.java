package org.l2j.gameserver.mobius.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExUserInfoFishing extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final boolean _isFishing;
    private final ILocational _baitLocation;

    public ExUserInfoFishing(L2PcInstance activeChar, boolean isFishing, ILocational baitLocation) {
        _activeChar = activeChar;
        _isFishing = isFishing;
        _baitLocation = baitLocation;
    }

    public ExUserInfoFishing(L2PcInstance activeChar, boolean isFishing) {
        _activeChar = activeChar;
        _isFishing = isFishing;
        _baitLocation = null;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_USER_INFO_FISHING.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.put((byte) (_isFishing ? 1 : 0));
        if (_baitLocation == null) {
            packet.putInt(0);
            packet.putInt(0);
            packet.putInt(0);
        } else {
            packet.putInt(_baitLocation.getX());
            packet.putInt(_baitLocation.getY());
            packet.putInt(_baitLocation.getZ());
        }
    }
}
