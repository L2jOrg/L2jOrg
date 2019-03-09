package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.quest.QuestState;

import java.nio.ByteBuffer;

public class RequestTutorialClientEvent extends IClientIncomingPacket
{
    int _eventId = 0;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _eventId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if (player == null)
        {
            return;
        }

        // TODO: UNHARDCODE ME!
        final QuestState qs = player.getQuestState("255_Tutorial");
        if (qs != null)
        {
            qs.getQuest().notifyEvent("CE" + _eventId + "", null, player);
        }
    }
}
