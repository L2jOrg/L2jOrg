package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.itemcontainer.PlayerInventory;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class ExUserInfoEquipSlot extends AbstractMaskPacket<InventorySlot> {
    private final Player player;

    private final byte[] masks = new byte[] {
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00, // 152
        0x00, // 152
        0x00, // 152
    };

    public ExUserInfoEquipSlot(Player cha) {
        this(cha, true);
    }

    public ExUserInfoEquipSlot(Player cha, boolean addAll) {
        player = cha;

        if (addAll) {
            addComponentType(InventorySlot.values());
        }
    }

    @Override
    protected byte[] getMasks() {
        return masks;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_USER_INFO_EQUIP_SLOT);

        writeInt(player.getObjectId());
        writeShort(InventorySlot.values().length); // 152
        writeBytes(masks);

        final PlayerInventory inventory = player.getInventory();
        for (var slot : getPaperdollOrder()) {
            if (containsMask(slot)) {
                final VariationInstance augment = inventory.getPaperdollAugmentation(slot);
                writeShort(22); // 10 + 4 * 3
                writeInt(inventory.getPaperdollObjectId(slot));
                writeInt(inventory.getPaperdollItemId(slot));
                writeInt(zeroIfNullOrElse(augment, VariationInstance::getOption1Id));
                writeInt(zeroIfNullOrElse(augment, VariationInstance::getOption2Id));
                writeInt(0x00); // Visual ID not used on classic
            }
        }
    }

}