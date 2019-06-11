package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_USER_INFO_FISHING);

        writeInt(_activeChar.getObjectId());
        writeByte((byte) (_isFishing ? 1 : 0));
        if (_baitLocation == null) {
            writeInt(0);
            writeInt(0);
            writeInt(0);
        } else {
            writeInt(_baitLocation.getX());
            writeInt(_baitLocation.getY());
            writeInt(_baitLocation.getZ());
        }
    }

}
