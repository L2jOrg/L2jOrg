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
package org.l2j.gameserver.network.serverpackets.item.upgrade;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collections;

/**
 * @author JoeAlisson
 */
public class ExShowUpgradeSystem  extends AbstractUpgradeSystem {

    /**
     * FE CD 01 :  01 00       64 00 00 00 00 00 00 00 00 00 - Rare
     */
    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_SHOW_UPGRADE_SYSTEM, buffer );
        buffer.writeShort(0x01); // flag
        buffer.writeShort(0x64); // commission ratio

        writeMaterial(Collections.emptyList(), buffer);

    }
}
