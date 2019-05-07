package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.SimpleTimeZone;

/**
 * @author KenM
 */
public final class ExRpItemLink extends AbstractItemPacket {
    private final L2ItemInstance _item;

    public ExRpItemLink(L2ItemInstance item) {
        _item = item;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RP_ITEM_LINK.writeId(packet);

        writeItem(packet, _item);
    }

    @Override
    protected int size(L2GameClient client) {
        return 103;
    }
}
