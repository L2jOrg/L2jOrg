package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * This packet is received from client when a party leader requests to change the leadership to another player in his party.
 */
public final class RequestChangePartyLeader extends ClientPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Party party = activeChar.getParty();
        if ((party != null) && party.isLeader(activeChar)) {
            party.changePartyLeader(_name);
        }
    }
}
