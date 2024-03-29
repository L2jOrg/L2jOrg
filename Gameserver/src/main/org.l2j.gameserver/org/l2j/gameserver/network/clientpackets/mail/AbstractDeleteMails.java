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
package org.l2j.gameserver.network.clientpackets.mail;

import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.settings.CharacterSettings;

/**
 * @author JoeAlisson
 */
abstract class AbstractDeleteMails extends ClientPacket {
    private static final int BATCH_LENGTH = 4;

    protected int[] mailIds = null;

    @Override
    protected void readImpl() throws Exception {
        var count = readInt();
        if (count <= 0 || count > CharacterSettings.maxItemInPacket() || count * BATCH_LENGTH != available()) {
            throw new InvalidDataPacketException();
        }

        mailIds = new int[count];
        for (int i = 0; i < count; i++) {
            mailIds[i] = readInt();
        }
    }
}
