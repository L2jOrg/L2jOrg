package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.PledgeInfo;

import java.nio.ByteBuffer;

public final class RequestPledgeInfo extends IClientIncomingPacket {
    private int _clanId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _clanId = packet.getInt();
    }

    @Override
    public void runImpl() {

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null) {
            return; // we have no clan data ?!? should not happen
        }

        client.sendPacket(new PledgeInfo(clan));
    }
}
