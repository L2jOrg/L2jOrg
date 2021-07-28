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
package org.l2j.gameserver.network.clientpackets.shortcut;

import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author JoeAlisson
 */
public final class RequestShortCutReg extends ClientPacket {

    private ShortcutType type;
    private int id;
    private int lvl;
    private int subLvl;
    private int characterType; // 1 - player, 2 - pet
    private int room;

    @Override
    public void readImpl() {
        final int typeId = readInt();
        type = ShortcutType.values()[(typeId < 1) || (typeId > 6) ? 0 : typeId];
        room = readInt();
        readByte(); // unk 0
        id = readInt();
        lvl = readShort();
        subLvl = readShort(); // Sublevel
        characterType = readInt();
    }

    @Override
    public void runImpl() {
        if(room < 0 || ( room > Shortcut.MAX_ROOM  && room != Shortcut.AUTO_POTION_ROOM)) {
            return;
        }

        var player = client.getPlayer();
        if (!validatedShortcut(player)) {
            return;
        }

        player.registerShortCut(new Shortcut(room, type, id, lvl, subLvl, characterType));
        if(room == Shortcut.AUTO_POTION_ROOM) {
            AutoPlayEngine.getInstance().setActiveAutoShortcut(player, room, true);
        }
    }

    private boolean validatedShortcut(Player player) {
        if(type == ShortcutType.ITEM  || room == Shortcut.AUTO_POTION_ROOM) {
            var item = player.getInventory().getItemByObjectId(id);
            if(item == null) {
                return false;
            }
            return room != Shortcut.AUTO_POTION_ROOM || item.isAutoPotion();
        }
        return true;
    }
}
