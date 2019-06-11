package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitInfo;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitInfo extends IClientIncomingPacket {
    private int _clanId;

    @Override
    public void readImpl() {
        _clanId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null) {
            return;
        }

        activeChar.sendPacket(new ExPledgeRecruitInfo(_clanId));
    }
}
