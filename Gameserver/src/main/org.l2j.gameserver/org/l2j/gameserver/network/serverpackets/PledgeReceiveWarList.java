package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author -Wooden-
 */
public class PledgeReceiveWarList extends IClientOutgoingPacket {
    private final L2Clan _clan;
    private final int _tab;
    private final Collection<ClanWar> _clanList;

    public PledgeReceiveWarList(L2Clan clan, int tab) {
        _clan = clan;
        _tab = tab;
        _clanList = clan.getWarList().values();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_RECEIVE_WAR_LIST.writeId(packet);

        packet.putInt(_tab); // page
        packet.putInt(_clanList.size());
        for (ClanWar clanWar : _clanList) {
            final L2Clan clan = clanWar.getOpposingClan(_clan);

            if (clan == null) {
                continue;
            }

            writeString(clan.getName(), packet);
            packet.putInt(clanWar.getState().ordinal()); // type: 0 = Declaration, 1 = Blood Declaration, 2 = In War, 3 = Victory, 4 = Defeat, 5 = Tie, 6 = Error
            packet.putInt(clanWar.getRemainingTime()); // Time if friends to start remaining
            packet.putInt(clanWar.getKillDifference(_clan)); // Score
            packet.putInt(0); // @TODO: Recent change in points
            packet.putInt(clanWar.getKillToStart()); // Friends to start war left
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 13 + _clanList.size() * 55;
    }
}
