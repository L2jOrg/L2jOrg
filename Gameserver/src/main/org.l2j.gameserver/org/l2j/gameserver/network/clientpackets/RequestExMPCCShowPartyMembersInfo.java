package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;

/**
 * Format:(ch) d
 *
 * @author chris_00
 */
public final class RequestExMPCCShowPartyMembersInfo extends ClientPacket {
    private int _partyLeaderId;

    @Override
    public void readImpl() {
        _partyLeaderId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Player player = L2World.getInstance().getPlayer(_partyLeaderId);
        if ((player != null) && (player.getParty() != null)) {
            client.sendPacket(new ExMPCCShowPartyMemberInfo(player.getParty()));
        }
    }
}
