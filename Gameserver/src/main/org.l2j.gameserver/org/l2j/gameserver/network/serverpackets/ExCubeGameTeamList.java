package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author mrTJO
 */
public class ExCubeGameTeamList extends ServerPacket {
    // Players Lists
    private final List<Player> _bluePlayers;
    private final List<Player> _redPlayers;

    // Common Values
    private final int _roomNumber;

    /**
     * Show Minigame Waiting List to Player
     *
     * @param redPlayers  Red Players List
     * @param bluePlayers Blue Players List
     * @param roomNumber  Arena/Room ID
     */
    public ExCubeGameTeamList(List<Player> redPlayers, List<Player> bluePlayers, int roomNumber) {
        _redPlayers = redPlayers;
        _bluePlayers = bluePlayers;
        _roomNumber = roomNumber - 1;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_BLOCK_UP_SET_LIST);

        writeInt(0x00);

        writeInt(_roomNumber);
        writeInt(0xffffffff);

        writeInt(_bluePlayers.size());
        for (Player player : _bluePlayers) {
            writeInt(player.getObjectId());
            writeString(player.getName());
        }
        writeInt(_redPlayers.size());
        for (Player player : _redPlayers) {
            writeInt(player.getObjectId());
            writeString(player.getName());
        }
    }

}