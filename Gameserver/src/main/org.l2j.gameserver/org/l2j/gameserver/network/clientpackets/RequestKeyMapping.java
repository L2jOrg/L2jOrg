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

import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.serverpackets.ExUISetting;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.ServerSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author KenM / mrTJO
 * @author JoeAlisson
 */
public class RequestKeyMapping extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() throws IOException {
        if (CharacterSettings.storeUISettings()) {
            var mappingPath = ServerSettings.dataPackDirectory().resolve(Path.of("client_store", "key", client.getAccountName()));
            if(Files.exists(mappingPath)) {
                var bytes = Files.readAllBytes(mappingPath);
                client.sendPacket(new ExUISetting(bytes));
            } else {
                client.sendPacket(new ExUISetting(Util.BYTE_ARRAY_EMPTY));
            }
        }
    }
}
