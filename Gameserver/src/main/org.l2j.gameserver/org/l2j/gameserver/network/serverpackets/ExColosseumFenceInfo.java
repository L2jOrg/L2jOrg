package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.model.actor.instance.Fence;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author HoridoJoho / FBIagent
 */
public class ExColosseumFenceInfo extends ServerPacket {
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _width;
    private final int _length;
    private final int _clientState;

    public ExColosseumFenceInfo(Fence fence) {
        this(fence.getObjectId(), fence.getX(), fence.getY(), fence.getZ(), fence.getWidth(), fence.getLength(), fence.getState());
    }

    public ExColosseumFenceInfo(int objId, double x, double y, double z, int width, int length, FenceState state) {
        _objId = objId;
        _x = (int) x;
        _y = (int) y;
        _z = (int) z;
        _width = width;
        _length = length;
        _clientState = state.getClientId();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_COLOSSEUM_FENCE_INFO);

        writeInt(_objId);
        writeInt(_clientState);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(_width);
        writeInt(_length);
    }

}