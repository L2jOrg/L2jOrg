package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mrTJO
 */
public class ExCubeGameExtendedChangePoints extends IClientOutgoingPacket {
    int _timeLeft;
    int _bluePoints;
    int _redPoints;
    boolean _isRedTeam;
    L2PcInstance _player;
    int _playerPoints;

    /**
     * Update a Secret Point Counter (used by client when receive ExCubeGameEnd)
     *
     * @param timeLeft     Time Left before Minigame's End
     * @param bluePoints   Current Blue Team Points
     * @param redPoints    Current Blue Team points
     * @param isRedTeam    Is Player from Red Team?
     * @param player       Player Instance
     * @param playerPoints Current Player Points
     */
    public ExCubeGameExtendedChangePoints(int timeLeft, int bluePoints, int redPoints, boolean isRedTeam, L2PcInstance player, int playerPoints) {
        _timeLeft = timeLeft;
        _bluePoints = bluePoints;
        _redPoints = redPoints;
        _isRedTeam = isRedTeam;
        _player = player;
        _playerPoints = playerPoints;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BLOCK_UP_SET_STATE.writeId(packet);

        packet.putInt(0x00);

        packet.putInt(_timeLeft);
        packet.putInt(_bluePoints);
        packet.putInt(_redPoints);

        packet.putInt(_isRedTeam ? 0x01 : 0x00);
        packet.putInt(_player.getObjectId());
        packet.putInt(_playerPoints);
    }
}
