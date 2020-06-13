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

import org.l2j.gameserver.engine.item.enchant.EnchantResultType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public class EnchantResult extends ServerPacket {
    private final EnchantResultType result;
    private final int crystal;
    private final long crystalCount;
    private final int enchantLevel;
    private final int[] enchantOptions;
    private int stone;
    private long stoneCount;

    private EnchantResult(EnchantResultType result, int crystal, long crystalCount, int enchantLevel, int[] options) {
        this.result = result;
        this.crystal = crystal;
        this.crystalCount = crystalCount;
        this.enchantLevel = enchantLevel;
        enchantOptions = options;
    }

    private EnchantResult(EnchantResultType result, Item item) {
        this(result, 0, 0, item.getEnchantLevel(), item.getEnchantOptions());
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ENCHANT_RESULT);

        writeInt(result.ordinal());
        writeInt(crystal);
        writeLong(crystalCount);
        writeInt(stone);
        writeLong(stoneCount);
        writeInt(enchantLevel);
        for (int option : enchantOptions) {
            writeInt(option);
        }
    }

    public void withStone(int stoneId, long stoneCount) {
        this.stone = stoneId;
        this.stoneCount = stoneCount;
    }

    public static EnchantResult success(Item item) {
        return new EnchantResult(EnchantResultType.SUCCESS, item);
    }

    public static EnchantResult error() {
        return new EnchantResult(EnchantResultType.ERROR, 0 , 0, 0, Item.DEFAULT_ENCHANT_OPTIONS);
    }

    public static EnchantResult fail() {
        return new EnchantResult(EnchantResultType.NO_CRYSTAL, 0, 0, 0, Item.DEFAULT_ENCHANT_OPTIONS);
    }

    public static EnchantResult fail(int crystalId, int count) {
        return new EnchantResult(EnchantResultType.FAIL, crystalId, count, 0, Item.DEFAULT_ENCHANT_OPTIONS);
    }

    public static EnchantResult safe(Item item) {
        return new EnchantResult(EnchantResultType.SAFE_FAIL, item);
    }

    public static EnchantResult safeReduced(Item item) {
        return new EnchantResult(EnchantResultType.SAFE_REDUCED, item);
    }

    public static EnchantResult blessed(Item item) {
        return new EnchantResult(EnchantResultType.BLESSED_FAIL, item);
    }
}
