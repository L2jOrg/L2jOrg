package org.l2j.gameserver.network.clientpackets.castle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.ExMercenaryCastleWarCastleSiegeInfo;

public class ExRequestMercenaryCastleWarCastleSiegeInfo extends ClientPacket {
    private int castleid;
    @Override
    protected void readImpl() throws Exception {
        // dummy byte
        castleid = readInt();
    }

    @Override
    protected void runImpl() {
        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }
        player.sendPacket(new ExMercenaryCastleWarCastleSiegeInfo(castleid));
    }
}
