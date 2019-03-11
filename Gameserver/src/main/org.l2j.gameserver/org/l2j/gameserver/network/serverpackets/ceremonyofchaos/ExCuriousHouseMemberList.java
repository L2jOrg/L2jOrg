package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExCuriousHouseMemberList extends IClientOutgoingPacket {
    private final int _id;
    private final int _maxPlayers;
    private final Collection<CeremonyOfChaosMember> _players;

    public ExCuriousHouseMemberList(int id, int maxPlayers, Collection<CeremonyOfChaosMember> players) {
        _id = id;
        _maxPlayers = maxPlayers;
        _players = players;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CURIOUS_HOUSE_MEMBER_LIST.writeId(packet);

        packet.putInt(_id);
        packet.putInt(_maxPlayers);
        packet.putInt(_players.size());
        for (CeremonyOfChaosMember cocPlayer : _players) {
            final L2PcInstance player = cocPlayer.getPlayer();
            packet.putInt(cocPlayer.getObjectId());
            packet.putInt(cocPlayer.getPosition());
            if (player != null) {
                packet.putInt(player.getMaxHp());
                packet.putInt(player.getMaxCp());
                packet.putInt((int) player.getCurrentHp());
                packet.putInt((int) player.getCurrentCp());
            } else {
                packet.putInt(0x00);
                packet.putInt(0x00);
                packet.putInt(0x00);
                packet.putInt(0x00);
            }
        }
    }

}
