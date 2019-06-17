package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;

public class RequestTutorialQuestionMark extends ClientPacket {
    private int _number = 0;

    @Override
    public void readImpl() {
        readByte(); // index ?
        _number = readInt();
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
