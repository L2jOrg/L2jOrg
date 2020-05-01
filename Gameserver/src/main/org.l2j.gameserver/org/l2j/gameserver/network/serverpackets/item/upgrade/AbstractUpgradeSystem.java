package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public abstract class AbstractUpgradeSystem extends ServerPacket {

    protected void writeMaterial(Collection<ItemHolder> materials) {
        writeInt(materials.size());
        materials.stream().mapToInt(ItemHolder::getId).forEach(this::writeInt);

        writeInt(materials.size());
        materials.forEach(i -> writeInt(5)); // material ratio
    }
}
