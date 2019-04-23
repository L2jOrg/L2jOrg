package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestReplyStartPledgeWar extends IClientIncomingPacket {
    private int _answer;

    @Override
    public void readImpl(ByteBuffer packet) {
        readString(packet);
        _answer = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        final L2PcInstance requestor = activeChar.getActiveRequester();
        if (requestor == null) {
            return;
        }

        if (_answer == 1) {
            final L2Clan attacked = activeChar.getClan();
            final L2Clan attacker = requestor.getClan();
            if ((attacked != null) && (attacker != null)) {
                final ClanWar clanWar = attacker.getWarWith(attacked.getId());
                if (clanWar.getState() == ClanWarState.BLOOD_DECLARATION) {
                    clanWar.mutualClanWarAccepted(attacker, attacked);
                    ClanTable.getInstance().storeClanWars(clanWar);
                }
            }
        } else {
            requestor.sendPacket(SystemMessageId.THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED_2);
        }
        activeChar.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}
