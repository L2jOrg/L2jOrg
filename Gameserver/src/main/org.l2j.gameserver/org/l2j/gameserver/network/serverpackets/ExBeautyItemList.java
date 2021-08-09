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
import org.l2j.gameserver.model.beautyshop.BeautyData;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sdw
 */
public class ExBeautyItemList extends ServerPacket {
    private static final int HAIR_TYPE = 0;
    private static final int FACE_TYPE = 1;
    private static final int COLOR_TYPE = 2;
    private final BeautyData _beautyData;
    private final Map<Integer, List<BeautyItem>> _colorData = new HashMap<>();
    private int _colorCount;

    public ExBeautyItemList(Player activeChar) {
        _beautyData = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType());

        for (BeautyItem hair : _beautyData.getHairList().values()) {
            final List<BeautyItem> colors = new ArrayList<>();
            for (BeautyItem color : hair.getColors().values()) {
                colors.add(color);
                _colorCount++;
            }
            _colorData.put(hair.getId(), colors);
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_BEAUTY_ITEM_LIST, buffer );

        buffer.writeInt(HAIR_TYPE);
        buffer.writeInt(_beautyData.getHairList().size());
        for (BeautyItem hair : _beautyData.getHairList().values()) {
            buffer.writeInt(0); // ?
            buffer.writeInt(hair.getId());
            buffer.writeInt(hair.getAdena());
            buffer.writeInt(hair.getResetAdena());
            buffer.writeInt(hair.getBeautyShopTicket());
            buffer.writeInt(1); // Limit
        }

        buffer.writeInt(FACE_TYPE);
        buffer.writeInt(_beautyData.getFaceList().size());
        for (BeautyItem face : _beautyData.getFaceList().values()) {
            buffer.writeInt(0); // ?
            buffer.writeInt(face.getId());
            buffer.writeInt(face.getAdena());
            buffer.writeInt(face.getResetAdena());
            buffer.writeInt(face.getBeautyShopTicket());
            buffer.writeInt(1); // Limit
        }

        buffer.writeInt(COLOR_TYPE);
        buffer.writeInt(_colorCount);
        for (int hairId : _colorData.keySet()) {
            for (BeautyItem color : _colorData.get(hairId)) {
                buffer.writeInt(hairId);
                buffer.writeInt(color.getId());
                buffer.writeInt(color.getAdena());
                buffer.writeInt(color.getResetAdena());
                buffer.writeInt(color.getBeautyShopTicket());
                buffer.writeInt(1);
            }
        }
    }

}
