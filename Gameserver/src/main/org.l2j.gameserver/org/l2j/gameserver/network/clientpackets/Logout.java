package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.OfflineTradeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 *
 * @version $Revision: 1.9.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class Logout extends ClientPacket {
    protected static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player == null) {
            client.closeNow();
            return;
        }

        if (!player.canLogout()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        LOGGER_ACCOUNTING.info("Logged out, " + client);

        if (!OfflineTradeUtil.enteredOfflineMode(player)) {
            Disconnection.of(client, player).defaultSequence(false);
        }
    }
}
