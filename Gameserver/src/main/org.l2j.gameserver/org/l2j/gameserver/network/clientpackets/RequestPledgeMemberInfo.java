package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PledgeReceiveMemberInfo;

/**
 * Format: (ch) dS
 *
 * @author -Wooden-
 */
public final class RequestPledgeMemberInfo extends ClientPacket {
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
        client.sendPacket(new PledgeReceiveMemberInfo(member));
    }

}