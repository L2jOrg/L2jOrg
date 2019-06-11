package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.PledgeReceiveMemberInfo;

import java.nio.ByteBuffer;

/**
 * Format: (ch) dS
 *
 * @author -Wooden-
 */
public final class RequestPledgeMemberInfo extends IClientIncomingPacket {
    @SuppressWarnings("unused")
    private int _unk1;
    private String _player;

    @Override
    public void readImpl() {
        _unk1 = readInt();
        _player = readString();
    }

    @Override
    public void runImpl() {
        // LOGGER.info("C5: RequestPledgeMemberInfo d:"+_unk1);
        // LOGGER.info("C5: RequestPledgeMemberInfo S:"+_player);
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        // do we need powers to do that??
        final L2Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        final L2ClanMember member = clan.getClanMember(_player);
        if (member == null) {
            return;
        }
        client.sendPacket(new PledgeReceiveMemberInfo(member));
    }

}