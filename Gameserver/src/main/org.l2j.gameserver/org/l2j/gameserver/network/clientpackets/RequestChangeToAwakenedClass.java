package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerChangeToAwakenedClass;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

/**
 * @author Sdw
 */
public class RequestChangeToAwakenedClass extends ClientPacket {
    private boolean _change;

    @Override
    public void readImpl() {
        _change = readInt() == 1;
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (_change) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerChangeToAwakenedClass(player), player);
        } else {
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
