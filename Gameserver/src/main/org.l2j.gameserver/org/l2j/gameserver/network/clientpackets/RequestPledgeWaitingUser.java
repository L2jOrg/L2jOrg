package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingList;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingUser;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingUser extends ClientPacket {
    private int _clanId;
    private int _playerId;

    @Override
    public void readImpl() {
        _clanId = readInt();
        _playerId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (activeChar.getClanId() != _clanId)) {
            return;
        }

        final PledgeApplicantInfo infos = ClanEntryManager.getInstance().getPlayerApplication(_clanId, _playerId);
        if (infos == null) {
            client.sendPacket(new ExPledgeWaitingList(_clanId));
        } else {
            client.sendPacket(new ExPledgeWaitingUser(infos));
        }
    }
}