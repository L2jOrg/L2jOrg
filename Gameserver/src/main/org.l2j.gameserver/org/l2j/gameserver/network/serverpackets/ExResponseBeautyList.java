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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Map;

/**
 * @author Sdw
 */
public class ExResponseBeautyList extends ServerPacket {
    public static final int SHOW_FACESHAPE = 1;
    public static final int SHOW_HAIRSTYLE = 0;
    private final Player _activeChar;
    private final int _type;
    private final Map<Integer, BeautyItem> _beautyItem;

    public ExResponseBeautyList(Player activeChar, int type) {
        _activeChar = activeChar;
        _type = type;
        if (type == SHOW_HAIRSTYLE) {
            _beautyItem = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType()).getHairList();
        } else {
            _beautyItem = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType()).getFaceList();
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_RESPONSE_BEAUTY_LIST, buffer );

        buffer.writeLong(_activeChar.getAdena());
        buffer.writeLong(_activeChar.getBeautyTickets());
        buffer.writeInt(_type);
        buffer.writeInt(_beautyItem.size());
        for (BeautyItem item : _beautyItem.values()) {
            buffer.writeInt(item.getId());
            buffer.writeInt(1); // Limit
        }
        buffer.writeInt(0);
    }

}
