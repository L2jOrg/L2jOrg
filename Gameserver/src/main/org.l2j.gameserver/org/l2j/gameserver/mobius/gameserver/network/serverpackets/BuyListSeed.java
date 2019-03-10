package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.SeedProduction;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author l3x
 */
public final class BuyListSeed extends IClientOutgoingPacket {
    private final int _manorId;
    private final long _money;
    private final List<SeedProduction> _list = new ArrayList<>();

    public BuyListSeed(long currentMoney, int castleId) {
        _money = currentMoney;
        _manorId = castleId;

        for (SeedProduction s : CastleManorManager.getInstance().getSeedProduction(castleId, false)) {
            if ((s.getAmount() > 0) && (s.getPrice() > 0)) {
                _list.add(s);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.BUY_LIST_SEED.writeId(packet);

        packet.putLong(_money); // current money
        packet.putInt(0x00); // TODO: Find me!
        packet.putInt(_manorId); // manor id

        if (!_list.isEmpty()) {
            packet.putShort((short) _list.size()); // list length
            for (SeedProduction s : _list) {
                packet.put((byte) 0x00); // mask item 0 to print minimal item information
                packet.putInt(s.getId()); // ObjectId
                packet.putInt(s.getId()); // ItemId
                packet.put((byte) 0xFF); // T1
                packet.putLong(s.getAmount()); // Quantity
                packet.put((byte) 0x05); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
                packet.put((byte) 0x00); // Filler (always 0)
                packet.putShort((short) 0x00); // Equipped : 00-No, 01-yes
                packet.putLong(0x00); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
                packet.putShort((short) 0x00); // Enchant level (pet level shown in control item)
                packet.putInt(-1);
                packet.putInt(-9999);
                packet.put((byte) 0x01); // GOD Item enabled = 1 disabled (red) = 0
                packet.putLong(s.getPrice()); // price
            }
            _list.clear();
        } else {
            packet.putShort((short) 0x00);
        }
    }
}
