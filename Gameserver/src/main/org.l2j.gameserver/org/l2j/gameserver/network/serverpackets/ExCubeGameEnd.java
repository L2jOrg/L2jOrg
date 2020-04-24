package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * *
 *
 * @author mrTJO
 */
public class ExCubeGameEnd extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BLOCK_UPSET_STATE);

        writeInt(0x01);

        writeInt(_isRedTeamWin ? 0x01 : 0x00);
        writeInt(0x00); // TODO: Find me!
    }

}
