package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * *
 *
 * @author mrTJO
 */
public class ExCubeGameEnd extends IClientOutgoingPacket {
    boolean _isRedTeamWin;

    /**
     * Show Minigame Results
     *
     * @param isRedTeamWin Is Red Team Winner?
     */
    public ExCubeGameEnd(boolean isRedTeamWin) {
        _isRedTeamWin = isRedTeamWin;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_BLOCK_UP_SET_STATE);

        writeInt(0x01);

        writeInt(_isRedTeamWin ? 0x01 : 0x00);
        writeInt(0x00); // TODO: Find me!
    }

}
