package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * NpcHtmlMessage server packet implementation.
 *
 * @author HorridoJoho
 */
public final class NpcHtmlMessage extends AbstractHtmlPacket {
    private final int _itemId;

    public NpcHtmlMessage() {
        _itemId = 0;
    }

    public NpcHtmlMessage(int npcObjId) {
        super(npcObjId);
        _itemId = 0;
    }

    public NpcHtmlMessage(String html) {
        super(html);
        _itemId = 0;
    }

    public NpcHtmlMessage(int npcObjId, String html) {
        super(npcObjId, html);
        _itemId = 0;
    }

    public NpcHtmlMessage(int npcObjId, int itemId) {
        super(npcObjId);

        if (itemId < 0) {
            throw new IllegalArgumentException();
        }

        _itemId = itemId;
    }

    public NpcHtmlMessage(int npcObjId, int itemId, String html) {
        super(npcObjId, html);

        if (itemId < 0) {
            throw new IllegalArgumentException();
        }

        _itemId = itemId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.NPC_HTML_MESSAGE.writeId(packet);

        packet.putInt(getNpcObjId());
        writeString(getHtml(), packet);
        packet.putInt(_itemId);
        packet.putInt(0x00); // TODO: Find me!
    }

    @Override
    public HtmlActionScope getScope() {
        return _itemId == 0 ? HtmlActionScope.NPC_HTML : HtmlActionScope.NPC_ITEM_HTML;
    }

    @Override
    protected int size(L2GameClient client) {
        return 20 + getHtml().length() * 2;
    }
}
