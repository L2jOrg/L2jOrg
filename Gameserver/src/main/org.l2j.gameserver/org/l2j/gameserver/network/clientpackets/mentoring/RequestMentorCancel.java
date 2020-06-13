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
package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.Mentee;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeLeft;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeRemove;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author UnAfraid
 */
public class RequestMentorCancel extends ClientPacket {
    private int _confirmed;
    private String _name;

    @Override
    public void readImpl() {
        _confirmed = readInt();
        _name = readString();
    }

    @Override
    public void runImpl() {
        if (_confirmed != 1) {
            return;
        }

        final Player player = client.getPlayer();
        final int objectId = PlayerNameTable.getInstance().getIdByName(_name);
        if (player != null) {
            if (player.isMentor()) {
                final Mentee mentee = MentorManager.getInstance().getMentee(player.getObjectId(), objectId);
                if (mentee != null) {
                    MentorManager.getInstance().cancelAllMentoringBuffs(mentee.getPlayerInstance());

                    if (MentorManager.getInstance().isAllMenteesOffline(player.getObjectId(), mentee.getObjectId())) {
                        MentorManager.getInstance().cancelAllMentoringBuffs(player);
                    }

                    player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED_THE_MENTOR_CANNOT_OBTAIN_ANOTHER_MENTEE_FOR_TWO_DAYS).addString(_name));
                    MentorManager.getInstance().setPenalty(player.getObjectId(), Config.MENTOR_PENALTY_FOR_MENTEE_LEAVE);
                    MentorManager.getInstance().deleteMentor(player.getObjectId(), mentee.getObjectId());

                    // Notify to scripts
                    EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeRemove(player, mentee), player);
                }

            } else if (player.isMentee()) {
                final Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
                if ((mentor != null) && (mentor.getObjectId() == objectId)) {
                    MentorManager.getInstance().cancelAllMentoringBuffs(player);

                    if (MentorManager.getInstance().isAllMenteesOffline(mentor.getObjectId(), player.getObjectId())) {
                        MentorManager.getInstance().cancelAllMentoringBuffs(mentor.getPlayerInstance());
                    }

                    MentorManager.getInstance().setPenalty(mentor.getObjectId(), Config.MENTOR_PENALTY_FOR_MENTEE_LEAVE);
                    MentorManager.getInstance().deleteMentor(mentor.getObjectId(), player.getObjectId());

                    // Notify to scripts
                    EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeLeft(mentor, player), player);

                    mentor.getPlayerInstance().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED_THE_MENTOR_CANNOT_OBTAIN_ANOTHER_MENTEE_FOR_TWO_DAYS).addString(_name));
                }
            }
        }
    }
}
