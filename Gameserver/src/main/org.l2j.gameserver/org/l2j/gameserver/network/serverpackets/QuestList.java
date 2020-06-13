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
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.LinkedList;
import java.util.List;

public class QuestList extends ServerPacket {
    private final List<QuestState> _activeQuests;
    private final byte[] _oneTimeQuestMask;

    public QuestList(Player player) {
        _activeQuests = new LinkedList<>();
        _oneTimeQuestMask = new byte[128];

        for (QuestState qs : player.getAllQuestStates()) {
            final int questId = qs.getQuest().getId();
            if (questId > 0) {
                if (qs.isStarted()) {
                    _activeQuests.add(qs);
                } else if (qs.isCompleted() && !(((questId > 255) && (questId < 10256)) || (questId > 11023))) {
                    _oneTimeQuestMask[(questId % 10000) / 8] |= 1 << (questId % 8);
                }
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.QUESTLIST);
        writeShort(_activeQuests.size());
        for (QuestState qs : _activeQuests) {
            writeInt(qs.getQuest().getId());
            writeInt(qs.getCond());
        }
        writeBytes(_oneTimeQuestMask);
    }

}
