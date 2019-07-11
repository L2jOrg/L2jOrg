package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PledgeReceivePowerInfo;

/**
 * Format: (ch) dS
 *
 * @author -Wooden-
 */
public final class RequestPledgeMemberPowerInfo extends ClientPacket {
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
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        // do we need powers to do that??
        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        final ClanMember member = clan.getClanMember(_player);
        if (member == null) {
            return;
        }
        activeChar.sendPacket(new PledgeReceivePowerInfo(member));
    }
}