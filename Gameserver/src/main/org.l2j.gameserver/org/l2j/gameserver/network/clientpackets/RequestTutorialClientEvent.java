package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.QuestState;

import java.nio.ByteBuffer;

public class RequestTutorialClientEvent extends IClientIncomingPacket {
    int _eventId = 0;

    @Override
    public void readImpl() {
        _eventId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        // TODO: UNHARDCODE ME!
        final QuestState qs = player.getQuestState("Q00255_Tutorial");
        if (qs != null) {
            qs.getQuest().notifyEvent(String.valueOf(_eventId), null, player);
        }
    }
}
