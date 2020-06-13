/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Tempy
 */
public class GmViewQuestInfo extends ServerPacket {
    private final Player _activeChar;
    private final List<Quest> _questList;

    public GmViewQuestInfo(Player cha) {
        _activeChar = cha;
        _questList = cha.getAllActiveQuests();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.GM_VIEW_QUEST_INFO);
        writeString(_activeChar.getName());
        writeShort((short) _questList.size()); // quest count

        for (Quest quest : _questList) {
            final QuestState qs = _activeChar.getQuestState(quest.getName());

            writeInt(quest.getId());
            writeInt(qs == null ? 0 : qs.getCond());
        }
        writeShort((short) 0x00); // some size
        // for size; ddQQ
    }

}
