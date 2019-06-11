package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.L2Mentee;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeLeft;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeRemove;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestMentorCancel extends IClientIncomingPacket {
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

        final L2PcInstance player = client.getActiveChar();
        final int objectId = CharNameTable.getInstance().getIdByName(_name);
        if (player != null) {
            if (player.isMentor()) {
                final L2Mentee mentee = MentorManager.getInstance().getMentee(player.getObjectId(), objectId);
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
                final L2Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
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
