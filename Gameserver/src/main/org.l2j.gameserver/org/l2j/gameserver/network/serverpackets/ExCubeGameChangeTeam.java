package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mrTJO
 */
public class ExCubeGameChangeTeam extends IClientOutgoingPacket {
    L2PcInstance _player;
    boolean _fromRedTeam;

    /**
     * Move Player from Team x to Team y
     *
     * @param player      Player Instance
     * @param fromRedTeam Is Player from Red Team?
     */
    public ExCubeGameChangeTeam(L2PcInstance player, boolean fromRedTeam) {
        _player = player;
        _fromRedTeam = fromRedTeam;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_BLOCK_UP_SET_LIST);

        writeInt(0x05);

        writeInt(_player.getObjectId());
        writeInt(_fromRedTeam ? 0x01 : 0x00);
        writeInt(_fromRedTeam ? 0x00 : 0x01);
    }

}
