package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author UnAfraid, mrTJO
 */
public class ExShowContactList extends IClientOutgoingPacket {
    private final Set<String> _contacts;

    public ExShowContactList(L2PcInstance player) {
        _contacts = player.getContactList().getAllContacts();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CONFIRM_ADDING_POST_FRIEND);

        writeInt(_contacts.size());
        _contacts.forEach(contact -> writeString(contact));
    }

}

