package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
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
        final Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        // Notify scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPressTutorialMark(player, _number), player);
    }
}
