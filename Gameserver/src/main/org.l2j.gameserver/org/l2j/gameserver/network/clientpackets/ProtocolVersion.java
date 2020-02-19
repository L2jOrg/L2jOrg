package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.l2j.gameserver.network.serverpackets.KeyPacket;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.contains;

/**
 * @author JoeAlisson
 */
public final class ProtocolVersion extends ClientPacket {
    private static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    private int version;

    @Override
    public void readImpl() {
        version = readInt();
    }

    @Override
    public void runImpl() {
        // this packet is never encrypted
        if (version == -2) {
            // this is just a ping attempt from the new C2 client
            client.closeNow();
        } else if (!contains(getSettings(ServerSettings.class).acceptedProtocols(), version)) {
            LOGGER_ACCOUNTING.warn("Wrong protocol version {}, {}", version, client);
            AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(client.getAccountName()));
            client.setProtocolOk(false);
            client.close(new KeyPacket(client.enableCrypt(), 0));
        } else {
            client.setProtocolOk(true);
            client.sendPacket(new KeyPacket(client.enableCrypt(), 1));
        }
    }
}
