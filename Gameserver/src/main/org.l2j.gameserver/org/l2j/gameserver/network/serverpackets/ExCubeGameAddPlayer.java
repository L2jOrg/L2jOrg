package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mrTJO
 */
public class ExCubeGameAddPlayer extends IClientOutgoingPacket {
    L2PcInstance _player;
    boolean _isRedTeam;

    /**
     * Add Player To Minigame Waiting List
     *
     * @param player    Player Instance
     * @param isRedTeam Is Player from Red Team?
     */
    public ExCubeGameAddPlayer(L2PcInstance player, boolean isRedTeam) {
        _player = player;
        _isRedTeam = isRedTeam;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_BLOCK_UP_SET_LIST);

        writeInt(0x01);

        writeInt(0xffffffff);

        writeInt(_isRedTeam ? 0x01 : 0x00);
        writeInt(_player.getObjectId());
        writeString(_player.getName());
    }

}
