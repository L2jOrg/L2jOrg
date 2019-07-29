package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Format: (ch)S S: Character Name
 *
 * @author UnAfraid & mrTJO
 */
public class RequestExDeleteContactFromContactList extends ClientPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        if (!Config.ALLOW_MAIL) {
            return;
        }

        if (_name == null) {
            return;
        }

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        activeChar.getContactList().remove(_name);
    }
}
