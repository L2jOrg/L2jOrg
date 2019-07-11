package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Set;

/**
 * @author UnAfraid, mrTJO
 */
public class ExShowContactList extends ServerPacket {
    private final Set<String> _contacts;

    public ExShowContactList(Player player) {
        _contacts = player.getContactList().getAllContacts();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CONFIRM_ADDING_POST_FRIEND);

        writeInt(_contacts.size());
        _contacts.forEach(contact -> writeString(contact));
    }

}

