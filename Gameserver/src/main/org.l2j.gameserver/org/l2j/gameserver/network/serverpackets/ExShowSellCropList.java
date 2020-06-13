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

import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author l3x
 */
public final class ExShowSellCropList extends ServerPacket {
    private final int _manorId;
    private final Map<Integer, Item> _cropsItems = new HashMap<>();
    private final Map<Integer, CropProcure> _castleCrops = new HashMap<>();

    public ExShowSellCropList(PlayerInventory inventory, int manorId) {
        _manorId = manorId;
        CastleManorManager.getInstance().getCropIds().forEach(cropId -> {
            doIfNonNull(inventory.getItemByItemId(cropId), item -> _cropsItems.put(cropId, item));
        });

        for (CropProcure crop : CastleManorManager.getInstance().getCropProcure(_manorId, false)) {
            if (_cropsItems.containsKey(crop.getSeedId()) && (crop.getAmount() > 0)) {
                _castleCrops.put(crop.getSeedId(), crop);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_SELL_CROP_LIST);

        writeInt(_manorId); // manor id
        writeInt(_cropsItems.size()); // size
        for (Item item : _cropsItems.values()) {
            final Seed seed = CastleManorManager.getInstance().getSeedByCrop(item.getId());
            writeInt(item.getObjectId()); // Object id
            writeInt(item.getId()); // crop id
            writeInt(seed.getLevel()); // seed level
            writeByte((byte) 0x01);
            writeInt(seed.getReward(1)); // reward 1 id
            writeByte((byte) 0x01);
            writeInt(seed.getReward(2)); // reward 2 id
            if (_castleCrops.containsKey(item.getId())) {
                final CropProcure crop = _castleCrops.get(item.getId());
                writeInt(_manorId); // manor
                writeLong(crop.getAmount()); // buy residual
                writeLong(crop.getPrice()); // buy price
                writeByte((byte) crop.getReward()); // reward
            } else {
                writeInt(0xFFFFFFFF); // manor
                writeLong(0x00); // buy residual
                writeLong(0x00); // buy price
                writeByte((byte) 0x00); // reward
            }
            writeLong(item.getCount()); // my crops
        }
    }

}