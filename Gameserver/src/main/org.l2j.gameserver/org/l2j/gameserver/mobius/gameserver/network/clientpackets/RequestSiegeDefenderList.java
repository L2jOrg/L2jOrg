package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.mobius.gameserver.model.entity.Castle;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SiegeDefenderList;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestSiegeDefenderList extends IClientIncomingPacket {
    private int _castleId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _castleId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        if (castle == null) {
            return;
        }
        client.sendPacket(new SiegeDefenderList(castle));
    }
}
