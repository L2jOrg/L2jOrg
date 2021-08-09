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

import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Request Save Key Mapping client packet.
 *
 * @author Mobius
 * @author JoeAlisson
 */
public class RequestSaveKeyMapping extends ClientPacket {
    private byte[] uiKeyMapping;

    @Override
    public void readImpl() throws Exception {
        final int dataSize = readInt();
        if (dataSize > 0) {
            uiKeyMapping = new byte[dataSize];
            readBytes(uiKeyMapping);
        } else {
            throw new InvalidDataPacketException("The ui key mapping is empty");
        }
    }

    @Override
    public void runImpl() throws Exception {
        if (!CharacterSettings.storeUISettings()) {
            return;
        }
        var mappingPath = ServerSettings.dataPackDirectory().resolve(Path.of("client_store", "key", client.getAccountName()));
        Files.createDirectories(mappingPath.getParent());

        Files.write(mappingPath, uiKeyMapping, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }
}
