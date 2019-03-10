package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;

import java.nio.ByteBuffer;

public class RequestTutorialQuestionMark extends IClientIncomingPacket {
    private int _number = 0;

    @Override
    public void readImpl(ByteBuffer packet) {
        packet.get(); // index ?
        _number = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        // Notify scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPressTutorialMark(player, _number), player);
    }
}
