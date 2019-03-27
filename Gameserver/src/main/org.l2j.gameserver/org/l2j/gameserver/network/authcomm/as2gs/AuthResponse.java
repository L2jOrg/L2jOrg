package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class AuthResponse extends ReceivablePacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthResponse.class);

    private int serverId;
    private String serverName;

    @Override
    protected void readImpl(ByteBuffer buffer) {
        serverId = buffer.get();
        serverName = readString(buffer);
    }

    @Override
    protected void runImpl() {
        String[] accounts = AuthServerCommunication.getInstance().getAccounts();
        sendPacket(new PlayerInGame(accounts));
        getSettings(ServerSettings.class).setServerId(serverId);
        LOGGER.info("Registered on authserver as {} [{}]", serverId, serverName);
    }
}