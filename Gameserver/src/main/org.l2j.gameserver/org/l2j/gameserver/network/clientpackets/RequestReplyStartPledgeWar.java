package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * This class ...
 *
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestReplyStartPledgeWar extends ClientPacket {
    private int _answer;

    @Override
    public void readImpl() {
        readString();
        _answer = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        final Player requestor = activeChar.getActiveRequester();
        if (requestor == null) {
            return;
        }

        if (_answer == 1) {
            final Clan attacked = activeChar.getClan();
            final Clan attacker = requestor.getClan();
            if ((attacked != null) && (attacker != null)) {
                final ClanWar clanWar = attacker.getWarWith(attacked.getId());
                if (clanWar.getState() == ClanWarState.BLOOD_DECLARATION) {
                    clanWar.mutualClanWarAccepted(attacker, attacked);
                    clanWar.save();
                }
            }
        } else {
            requestor.sendPacket(SystemMessageId.THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED);
        }
        activeChar.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}
