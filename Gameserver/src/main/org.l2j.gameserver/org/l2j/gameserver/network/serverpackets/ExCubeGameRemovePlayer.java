package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mrTJO
 */
public class ExCubeGameRemovePlayer extends IClientOutgoingPacket {
    L2PcInstance _player;
    boolean _isRedTeam;

    /**
     * Remove Player from Minigame Waiting List
     *
     * @param player    Player to Remove
     * @param isRedTeam Is Player from Red Team?
     */
    public ExCubeGameRemovePlayer(L2PcInstance player, boolean isRedTeam) {
        _player = player;
        _isRedTeam = isRedTeam;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BLOCK_UP_SET_LIST.writeId(packet);

        packet.putInt(0x02);

        packet.putInt(0xffffffff);

        packet.putInt(_isRedTeam ? 0x01 : 0x00);
        packet.putInt(_player.getObjectId());
    }
}
