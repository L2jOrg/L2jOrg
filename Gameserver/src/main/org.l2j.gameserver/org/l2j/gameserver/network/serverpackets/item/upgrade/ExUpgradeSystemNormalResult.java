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
import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.engine.upgrade.CommonUpgrade;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExUpgradeSystemNormalResult extends ServerPacket {

    private final boolean success;
    private final CommonUpgrade upgrade;
    private IntMap<ItemHolder> items = Containers.emptyIntMap();
    private IntMap<ItemHolder> bonus = Containers.emptyIntMap();

    private ExUpgradeSystemNormalResult(boolean success, CommonUpgrade upgrade) {
        this.success = success;
        this.upgrade = upgrade;
    }

    public ExUpgradeSystemNormalResult with(IntMap<ItemHolder> items) {
        this.items = items;
        return this;
    }

    public void withBonus(IntMap<ItemHolder> items) {
        this.bonus = items;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_UPGRADE_SYSTEM_NORMAL_RESULT, buffer );
        buffer.writeShort(0x01); // result
        buffer.writeInt(upgrade.id()); // id

        buffer.writeByte(success);
        buffer.writeInt(items.size());
        items.forEach((objectId, item) -> writeResultItem(objectId, item, buffer));

        buffer.writeByte(!bonus.isEmpty());
        buffer.writeInt(bonus.size());
        bonus.forEach((objectId, item) -> writeResultItem(objectId, item, buffer));
    }

    private void writeResultItem(int objectId, ItemHolder item, WritableBuffer buffer) {
        buffer.writeInt(objectId);
        buffer.writeInt(item.getId());
        buffer.writeInt(item.getEnchantment());
        buffer.writeInt((int) item.getCount());
    }

    public static ExUpgradeSystemNormalResult success(CommonUpgrade upgrade) {
        return new ExUpgradeSystemNormalResult(true, upgrade);
    }

    public static ExUpgradeSystemNormalResult fail(CommonUpgrade upgrade) {
        return new ExUpgradeSystemNormalResult(false, upgrade);
    }
}
