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
package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collections;

/**
 * @author JoeAlisson
 */
public class ExShowUpgradeSystemNormal extends AbstractUpgradeSystem {

    private final UpgradeType type;

    public ExShowUpgradeSystemNormal(UpgradeType type) {
        this.type = type;
    }

    /**
     *   FE 03 02 :  01 00 02 00 64 00 00 00 00 00 00 00 00 00 - Normal
     */
    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_UPGRADE_SYSTEM_NORMAL);
        writeShort(0x01); // flag
        writeShort(type.ordinal());
        writeShort(0x64); // commission ratio
        writeMaterial(Collections.emptyList());
    }
}
