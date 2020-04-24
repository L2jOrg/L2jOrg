package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author mrTJO
 */
public class ExCubeGameChangeTeam extends ServerPacket {
    Player _player;
    boolean _fromRedTeam;

    /**
     * Move Player from Team x to Team y
     *
     * @param player      Player Instance
     * @param fromRedTeam Is Player from Red Team?
     */
    public ExCubeGameChangeTeam(Player player, boolean fromRedTeam) {
        _player = player;
        _fromRedTeam = fromRedTeam;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);

        writeInt(0x05);

        writeInt(_player.getObjectId());
        writeInt(_fromRedTeam ? 0x01 : 0x00);
        writeInt(_fromRedTeam ? 0x00 : 0x01);
    }

}
