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

import org.l2j.gameserver.api.item.UseItemAPI;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public final class UseItem extends ClientPacket {

    private int objectId;
    private boolean ctrlPressed;

    @Override
    public void readImpl() {
        objectId = readInt();
        ctrlPressed = readInt() != 0;
    }

    @Override
    public void runImpl() {
        if (!client.getFloodProtectors().getUseItem().tryPerformAction("use item")) {
            return;
        }

        var player = client.getPlayer();
        if (isNull(player)) {
            return;
        }
        UseItemAPI.useItem(player, objectId, ctrlPressed);
    }
}
