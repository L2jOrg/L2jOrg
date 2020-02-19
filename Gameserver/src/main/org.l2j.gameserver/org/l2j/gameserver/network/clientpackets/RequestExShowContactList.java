package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExShowContactList;
import org.l2j.gameserver.settings.GeneralSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Format: (ch)
 *
 * @author mrTJO & UnAfraid
 */
public final class RequestExShowContactList extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        if (!getSettings(GeneralSettings.class).allowMail()) {
            return;
        }

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new ExShowContactList(activeChar));
    }
}
