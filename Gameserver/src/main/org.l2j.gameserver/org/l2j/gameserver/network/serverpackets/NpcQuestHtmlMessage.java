package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_NPC_QUEST_HTML_MESSAGE);

        writeInt(getNpcObjId());
        writeString(getHtml());
        writeInt(_questId);
    }


    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.NPC_QUEST_HTML;
    }
}
