package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * NpcQuestHtmlMessage server packet implementation.
 *
 * @author HorridoJoho
 */
public final class NpcQuestHtmlMessage extends AbstractHtmlPacket {
    private final int _questId;

    public NpcQuestHtmlMessage(int npcObjId, int questId) {
        super(npcObjId);
        _questId = questId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_NPC_QUEST_HTML_MESSAGE.writeId(packet);

        packet.putInt(getNpcObjId());
        writeString(getHtml(), packet);
        packet.putInt(_questId);
    }

    @Override
    protected int size(L2GameClient client) {
        return 13 + getHtml().length() * 2;
    }

    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.NPC_QUEST_HTML;
    }
}
