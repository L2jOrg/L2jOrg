package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExConfirmAddingContact;

/**
 * Format: (ch)S S: Character Name
 *
 * @author UnAfraid & mrTJO
 */
public class RequestExAddContactToContactList extends ClientPacket {
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

        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final boolean charAdded = activeChar.getContactList().add(_name);
        activeChar.sendPacket(new ExConfirmAddingContact(_name, charAdded));
    }
}
