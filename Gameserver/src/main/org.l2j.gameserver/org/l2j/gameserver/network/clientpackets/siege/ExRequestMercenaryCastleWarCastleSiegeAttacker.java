package org.l2j.gameserver.network.clientpackets.siege;

import org.l2j.gameserver.engine.siege.SiegeEngine;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.siege.ExMCWCastleSiegeAttackerList;

import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class ExRequestMercenaryCastleWarCastleSiegeAttacker extends ClientPacket {
    private int castleId;

    @Override
    protected void readImpl() throws Exception {
        castleId = readInt();
    }

    @Override
    protected void runImpl() {
        SiegeEngine.getInstance().showAttackerList(client.getPlayer(), castleId);
    }
}
