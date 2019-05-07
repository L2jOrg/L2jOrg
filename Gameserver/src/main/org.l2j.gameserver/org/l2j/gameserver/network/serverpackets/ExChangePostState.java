package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Migi
 */
public class ExChangePostState extends IClientOutgoingPacket {
    private final boolean _receivedBoard;
    private final int[] _changedMsgIds;
    private final int _changeId;

    public ExChangePostState(boolean receivedBoard, int[] changedMsgIds, int changeId) {
        _receivedBoard = receivedBoard;
        _changedMsgIds = changedMsgIds;
        _changeId = changeId;
    }

    public ExChangePostState(boolean receivedBoard, int changedMsgId, int changeId) {
        _receivedBoard = receivedBoard;
        _changedMsgIds = new int[]
                {
                        changedMsgId
                };
        _changeId = changeId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CHANGE_POST_STATE.writeId(packet);

        packet.putInt(_receivedBoard ? 1 : 0);
        packet.putInt(_changedMsgIds.length);
        for (int postId : _changedMsgIds) {
            packet.putInt(postId); // postId
            packet.putInt(_changeId); // state
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 13 + _changedMsgIds.length * 8;
    }
}
