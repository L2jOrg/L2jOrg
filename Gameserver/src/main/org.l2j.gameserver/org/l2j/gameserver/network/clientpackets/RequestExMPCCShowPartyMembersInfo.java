package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.world.World;
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
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Player player = World.getInstance().findPlayer(_partyLeaderId);
        if ((player != null) && (player.getParty() != null)) {
            client.sendPacket(new ExMPCCShowPartyMemberInfo(player.getParty()));
        }
    }
}
