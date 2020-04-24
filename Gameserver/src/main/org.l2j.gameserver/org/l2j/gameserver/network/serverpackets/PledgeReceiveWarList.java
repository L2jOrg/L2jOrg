package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collection;

/**
 * @author -Wooden-
 */
public class PledgeReceiveWarList extends ServerPacket {
    private final Clan _clan;
    private final int _tab;
    private final Collection<ClanWar> _clanList;

    public PledgeReceiveWarList(Clan clan, int tab) {
        _clan = clan;
        _tab = tab;
        _clanList = clan.getWarList().values();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_VIEW_PLEDGE_WARLIST);

        writeInt(_tab); // page
        writeInt(_clanList.size());
        for (ClanWar clanWar : _clanList) {
            final Clan clan = clanWar.getOpposingClan(_clan);

            if (clan == null) {
                continue;
            }

            writeString(clan.getName());
            writeInt(clanWar.getState().ordinal()); // type: 0 = Declaration, 1 = Blood Declaration, 2 = In War, 3 = Victory, 4 = Defeat, 5 = Tie, 6 = Error
            writeInt(clanWar.getRemainingTime()); // Time if friends to start remaining
            writeInt(clanWar.getKillDifference(_clan)); // Score
            writeInt(0); // @TODO: Recent change in points
            writeInt(clanWar.getKillToStart()); // Friends to start war left
        }
    }

}
