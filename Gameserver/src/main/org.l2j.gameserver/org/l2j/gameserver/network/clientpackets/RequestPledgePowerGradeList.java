package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PledgePowerGradeList;

/**
 * Format: (ch)
 *
 * @author -Wooden-
 */
public final class RequestPledgePowerGradeList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final L2Clan clan = player.getClan();
        if (clan != null) {
            final L2Clan.RankPrivs[] privs = clan.getAllRankPrivs();
            player.sendPacket(new PledgePowerGradeList(privs));
        }
    }
}