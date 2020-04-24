package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Migi
 */
public class ExChangePostState extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CHANGE_POST_STATE);

        writeInt(_receivedBoard ? 1 : 0);
        writeInt(_changedMsgIds.length);
        for (int postId : _changedMsgIds) {
            writeInt(postId); // postId
            writeInt(_changeId); // state
        }
    }

}
