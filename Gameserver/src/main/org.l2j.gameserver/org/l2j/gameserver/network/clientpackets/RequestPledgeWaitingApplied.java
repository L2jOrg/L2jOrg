package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingListApplied;

import java.util.OptionalInt;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingApplied extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if ((activeChar == null) || (activeChar.getClan() != null)) {
            return;
        }

        final OptionalInt clanId = ClanEntryManager.getInstance().getClanIdForPlayerApplication(activeChar.getObjectId());
        if (clanId.isPresent()) {
            activeChar.sendPacket(new ExPledgeWaitingListApplied(clanId.getAsInt(), activeChar.getObjectId()));
        }
    }
}
