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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Zoey76
 */
public class HennaEquipList extends ServerPacket {
    private final Player _player;
    private final List<Henna> _hennaEquipList;

    public HennaEquipList(Player player) {
        _player = player;
        _hennaEquipList = HennaData.getInstance().getHennaList(player.getClassId());
    }

    public HennaEquipList(Player player, List<Henna> list) {
        _player = player;
        _hennaEquipList = list;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.HENNA_EQUIP_LIST);
        writeLong(_player.getAdena()); // activeChar current amount of Adena
        writeInt(3); // available equip slot
        writeInt(_hennaEquipList.size());

        for (Henna henna : _hennaEquipList) {
            // Player must have at least one dye in inventory
            // to be able to see the Henna that can be applied with it.
            if ((_player.getInventory().getItemByItemId(henna.getDyeItemId())) != null) {
                writeInt(henna.getDyeId()); // dye Id
                writeInt(henna.getDyeItemId()); // item Id of the dye
                writeLong(henna.getWearCount()); // amount of dyes required
                writeLong(henna.getWearFee()); // amount of Adena required
                writeInt(henna.isAllowedClass(_player.getClassId()) ? 0x01 : 0x00); // meet the requirement or not
                // writeInt(0x00); // Does not exist in Classic.
            }
        }
    }

}
