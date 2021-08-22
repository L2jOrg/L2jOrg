/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExConfirmAddingContact;
import org.l2j.gameserver.settings.GeneralSettings;

import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * Format: (ch)S S: Character Name
 *
 * @author UnAfraid & mrTJO
 */
public class RequestExAddContactToContactList extends ClientPacket {
    private String name;

    @Override
    public void readImpl() {
        name = readString();
    }

    @Override
    public void runImpl() {
        if (isNullOrEmpty(name)) {
            return;
        }

        final Player player = client.getPlayer();
        final boolean charAdded = player.getContacts().add(name);
        player.sendPacket(new ExConfirmAddingContact(name, charAdded));
    }
}
