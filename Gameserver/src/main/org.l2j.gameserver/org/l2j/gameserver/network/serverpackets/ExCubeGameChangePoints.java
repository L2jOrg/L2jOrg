package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author mrTJO
 */
public class ExCubeGameChangePoints extends ServerPacket {
    int _timeLeft;
    int _bluePoints;
    int _redPoints;

    /**
     * Change Client Point Counter
     *
     * @param timeLeft   Time Left before Minigame's End
     * @param bluePoints Current Blue Team Points
     * @param redPoints  Current Red Team Points
     */
    public ExCubeGameChangePoints(int timeLeft, int bluePoints, int redPoints) {
        _timeLeft = timeLeft;
        _bluePoints = bluePoints;
        _redPoints = redPoints;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BLOCK_UPSET_STATE);

        writeInt(0x02);

        writeInt(_timeLeft);
        writeInt(_bluePoints);
        writeInt(_redPoints);
    }

}
