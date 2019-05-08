package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.itemcontainer.PcInventory;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExUserInfoEquipSlot extends AbstractMaskPacket<InventorySlot> {
    private final L2PcInstance _activeChar;

    private final byte[] _masks = new byte[]
            {
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00, // 152
                    (byte) 0x00, // 152
                    (byte) 0x00, // 152
            };

    public ExUserInfoEquipSlot(L2PcInstance cha) {
        this(cha, true);
    }

    public ExUserInfoEquipSlot(L2PcInstance cha, boolean addAll) {
        _activeChar = cha;

        if (addAll) {
            addComponentType(InventorySlot.values());
        }
    }

    @Override
    protected byte[] getMasks() {
        return _masks;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_USER_INFO_EQUIP_SLOT.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putShort((short) InventorySlot.values().length); // 152
        packet.put(_masks);

        final PcInventory inventory = _activeChar.getInventory();
        for (InventorySlot slot : InventorySlot.values()) {
            if (containsMask(slot)) {
                    final VariationInstance augment = inventory.getPaperdollAugmentation(slot.getSlot());
                packet.putShort((short) 22); // 10 + 4 * 3
                packet.putInt(inventory.getPaperdollObjectId(slot.getSlot()));
                packet.putInt(inventory.getPaperdollItemId(slot.getSlot()));
                packet.putInt(augment != null ? augment.getOption1Id() : 0);
                packet.putInt(augment != null ? augment.getOption2Id() : 0);
                packet.putInt(0x00); // Visual ID not used on classic
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 12 + InventorySlot.values().length * 22;
    }
}