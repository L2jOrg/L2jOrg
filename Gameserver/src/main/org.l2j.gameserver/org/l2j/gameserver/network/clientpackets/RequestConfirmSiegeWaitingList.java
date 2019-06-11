package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.SiegeDefenderList;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestConfirmSiegeWaitingList extends IClientIncomingPacket {
    private int _approved;
    private int _castleId;
    private int _clanId;

    @Override
    public void readImpl() {
        _castleId = readInt();
        _clanId = readInt();
        _approved = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        // Check if the player has a clan
        if (activeChar.getClan() == null) {
            return;
        }

        final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        if (castle == null) {
            return;
        }

        // Check if leader of the clan who owns the castle?
        if ((castle.getOwnerId() != activeChar.getClanId()) || (!activeChar.isClanLeader())) {
            return;
        }

        final L2Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null) {
            return;
        }

        if (!castle.getSiege().getIsRegistrationOver()) {
            if (_approved == 1) {
                if (castle.getSiege().checkIsDefenderWaiting(clan)) {
                    castle.getSiege().approveSiegeDefenderClan(_clanId);
                } else {
                    return;
                }
            } else if ((castle.getSiege().checkIsDefenderWaiting(clan)) || (castle.getSiege().checkIsDefender(clan))) {
                castle.getSiege().removeSiegeClan(_clanId);
            }
        }

        // Update the defender list
        client.sendPacket(new SiegeDefenderList(castle));
    }
}
