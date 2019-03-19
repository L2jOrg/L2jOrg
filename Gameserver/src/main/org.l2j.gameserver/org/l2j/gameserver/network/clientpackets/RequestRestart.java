package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.network.serverpackets.RestartResponse;
import org.l2j.gameserver.util.OfflineTradeUtil;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 *
 * @version $Revision: 1.11.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRestart extends IClientIncomingPacket {
    protected static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (!player.canLogout()) {
            client.sendPacket(RestartResponse.FALSE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        LOGGER_ACCOUNTING.info("Logged out, " + client);

        if (!OfflineTradeUtil.enteredOfflineMode(player)) {
            Disconnection.of(client, player).storeMe().deleteMe();
        }

        // return the client to the authed status
        client.setConnectionState(ConnectionState.AUTHENTICATED);

        client.sendPacket(RestartResponse.TRUE);

        // send char list
        final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId());
        client.sendPacket(cl);
        client.setCharSelection(cl.getCharInfo());
    }
}
