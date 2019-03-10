package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PledgeReceivePowerInfo;

import java.nio.ByteBuffer;

/**
 * Format: (ch) dS
 *
 * @author -Wooden-
 */
public final class RequestPledgeMemberPowerInfo extends IClientIncomingPacket {
    @SuppressWarnings("unused")
    private int _unk1;
    private String _player;

    @Override
    public void readImpl(ByteBuffer packet) {
        _unk1 = packet.getInt();
        _player = readString(packet);
    }

    @Override
    public void runImpl() {
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
        activeChar.sendPacket(new PledgeReceivePowerInfo(member));
    }
}