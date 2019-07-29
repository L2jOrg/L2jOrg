package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitInfo;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitInfo extends ClientPacket {
    private int _clanId;

    @Override
    public void readImpl() {
        _clanId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null) {
            return;
        }

        activeChar.sendPacket(new ExPledgeRecruitInfo(_clanId));
    }
}
