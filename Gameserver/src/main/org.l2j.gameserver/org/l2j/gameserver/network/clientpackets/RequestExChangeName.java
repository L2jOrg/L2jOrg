/*
 * Copyright © 2019-2020 L2JOrg
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

import org.l2j.gameserver.network.serverpackets.ExNeedToChangeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reply for {@link ExNeedToChangeName}
 *
 * @author JIV
 */
public class RequestExChangeName extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExChangeName.class);
    private String _newName;
    private int _type;
    private int _charSlot;

    @Override
    public void readImpl() {
        _type = readInt();
        _newName = readString();
        _charSlot = readInt();
    }

    @Override
    public void runImpl() {
        LOGGER.info("Recieved " + getClass().getSimpleName() + " name: " + _newName + " type: " + _type + " CharSlot: " + _charSlot);
    }
}
