package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public final class ExRpItemLink extends AbstractItemPacket {
    private final Item _item;

    public ExRpItemLink(Item item) {
        _item = item;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_RP_ITEM_LINK);

        writeItem(_item);
    }

}
