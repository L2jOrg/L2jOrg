package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class ExShowBaseAttributeCancelWindow extends ServerPacket {
    private final Collection<Item> _items;
    private long _price;

    public ExShowBaseAttributeCancelWindow(Player player) {
        _items = player.getInventory().getItems(Item::hasAttributes);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_BASE_ATTRIBUTE_CANCEL_WINDOW);

        writeInt(_items.size());
        for (Item item : _items) {
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
    private long getPrice(Item item) {
        switch (item.getTemplate().getCrystalType()) {
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
