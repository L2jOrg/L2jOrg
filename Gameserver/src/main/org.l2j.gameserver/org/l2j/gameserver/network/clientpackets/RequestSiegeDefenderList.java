package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.SiegeDefenderList;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestSiegeDefenderList extends ClientPacket {
    private int _castleId;

    @Override
    public void readImpl() {
        _castleId = readInt();
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
