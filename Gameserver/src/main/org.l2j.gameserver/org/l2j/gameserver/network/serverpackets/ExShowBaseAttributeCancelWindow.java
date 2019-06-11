package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

public class ExShowBaseAttributeCancelWindow extends IClientOutgoingPacket {
    private final Collection<L2ItemInstance> _items;
    private long _price;

    public ExShowBaseAttributeCancelWindow(L2PcInstance player) {
        _items = player.getInventory().getItems(L2ItemInstance::hasAttributes);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SHOW_BASE_ATTRIBUTE_CANCEL_WINDOW);

        writeInt(_items.size());
        for (L2ItemInstance item : _items) {
            writeInt(item.getObjectId());
            writeLong(getPrice(item));
        }
    }


    /**
     * TODO: Unhardcode! Update prices for Top/Mid/Low S80/S84
     *
     * @param item
     * @return
     */
    private long getPrice(L2ItemInstance item) {
        switch (item.getItem().getCrystalType()) {
            case S: {
                if (item.isWeapon()) {
                    _price = 50000;
                } else {
                    _price = 40000;
                }
                break;
            }
        }
        return _price;
    }
}
