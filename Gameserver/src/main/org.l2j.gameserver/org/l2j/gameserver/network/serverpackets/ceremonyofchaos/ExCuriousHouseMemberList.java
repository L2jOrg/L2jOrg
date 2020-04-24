package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExCuriousHouseMemberList extends ServerPacket {
    private final int _id;
    private final int _maxPlayers;
    private final Collection<CeremonyOfChaosMember> _players;

    public ExCuriousHouseMemberList(int id, int maxPlayers, Collection<CeremonyOfChaosMember> players) {
        _id = id;
        _maxPlayers = maxPlayers;
        _players = players;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CURIOUS_HOUSE_MEMBER_LIST);

        writeInt(_id);
        writeInt(_maxPlayers);
        writeInt(_players.size());
        for (CeremonyOfChaosMember cocPlayer : _players) {
            final Player player = cocPlayer.getPlayer();
            writeInt(cocPlayer.getObjectId());
            writeInt(cocPlayer.getPosition());
            if (player != null) {
                writeInt(player.getMaxHp());
                writeInt(player.getMaxCp());
                writeInt((int) player.getCurrentHp());
                writeInt((int) player.getCurrentCp());
            } else {
                writeInt(0x00);
                writeInt(0x00);
                writeInt(0x00);
                writeInt(0x00);
            }
        }
    }

}
