package org.l2j.gameserver.network.clientpackets.castle;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SiegeDefenderList;

public class ExRequestMercenaryCastleWarCastleSiegeDefender extends ClientPacket {
    private int castleid;
    @Override
    protected void readImpl() throws Exception {
        // dummy byte
        castleid = readInt();
    }

    @Override
    protected void runImpl() {
        final Castle castle = CastleManager.getInstance().getCastleById(castleid);
        if (castle == null) {
            return;
        }
        client.sendPacket(new SiegeDefenderList(castle));
    }
}
