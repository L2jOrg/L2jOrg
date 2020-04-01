package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTutorialEvent;

/**
 * @author JoeAlisson
 */
public class RequestTutorialClientEvent extends ClientPacket {
    private int event;

    @Override
    public void readImpl() {
        event = readInt();
    }

    @Override
    public void runImpl() {
        final var player = client.getPlayer();
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTutorialEvent(player, event), player);
    }
}
