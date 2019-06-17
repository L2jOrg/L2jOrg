package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.l2j.gameserver.network.serverpackets.KeyPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProtocolVersion extends ClientPacket {
    private static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    private int _version;

    @Override
    public void readImpl() {
        _version = readInt();
    }

    @Override
    public void runImpl() {
        // this packet is never encrypted
        if (_version == -2) {
            // this is just a ping attempt from the new C2 client
            client.closeNow();
        } else if (!Config.PROTOCOL_LIST.contains(_version)) {
            LOGGER_ACCOUNTING.warn("Wrong protocol version {}, {}", _version, client);
            AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(client.getAccountName()));
            client.setProtocolOk(false);
            client.close(new KeyPacket(client.enableCrypt(), 0));
        } else {
            client.setProtocolOk(true);
            client.sendPacket(new KeyPacket(client.enableCrypt(), 1));
        }
    }
}
