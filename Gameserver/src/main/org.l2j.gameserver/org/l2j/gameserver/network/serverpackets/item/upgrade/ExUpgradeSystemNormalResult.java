package org.l2j.gameserver.network.serverpackets.item.upgrade;

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
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_UPGRADE_SYSTEM_NORMAL_RESULT);
        writeShort(0x01); // result
        writeInt(upgrade.id()); // id

        writeByte(success);
        writeInt(items.size());
        items.forEach(this::writeResultItem);

        writeByte(!bonus.isEmpty());
        writeInt(bonus.size());
        bonus.forEach(this::writeResultItem);
    }

    private void writeResultItem(int objectId, ItemHolder item) {
        writeInt(objectId);
        writeInt(item.getId());
        writeInt(item.getEnchantment());
        writeInt((int) item.getCount());
    }

    public static ExUpgradeSystemNormalResult success(CommonUpgrade upgrade) {
        return new ExUpgradeSystemNormalResult(true, upgrade);
    }

    public static ExUpgradeSystemNormalResult fail(CommonUpgrade upgrade) {
        return new ExUpgradeSystemNormalResult(false, upgrade);
    }
}
