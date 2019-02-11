package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.L2LoginServerPacket;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;
import org.l2j.authserver.settings.AuthServerSettings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 *
 *  * TYPE_BARE=0 - Indicates that each game server will have its basic information specified.
 *  *
 *  * TYPE_C0=1 - Indicates that each game server will have its additional and dynamic information specified.
 *  *
 *  * TYPE_NAMED=2 - Indicates that each game server will have its name specified.
 *  *
 *  * TYPE_C1=3 - Indicates that each game server will have its type mask specified.
 *  *
 *  * TYPE_C2=4 - Indicates that each game server will have its bracket flag specified.
 *  *
 *  * TYPE_FREYA=5 - Indicates that each game server will have reader's character count(s) specified.
 *
 *
 * Server will be considered as
 *  Good when the number of online players is less than half the maximum.
 *  as Normal between half and 4/5 and Full when there's more than 4/5 of the maximum number of players
 */
public final class ServerList extends L2LoginServerPacket {

    private final byte listType;

    public ServerList(byte listType) {
        this.listType = listType;
    }

    @Override
    public void writeImpl(AuthClient client, ByteBuffer buffer) {
        var servers = GameServerManager.getInstance().getRegisteredGameServers();
        buffer.put((byte)0x04);
        buffer.put((byte)servers.size());
        buffer.put((byte)client.getLastServer());

        for (var server : servers.values()) {
            buffer.put((byte)server.getId());

            var host = client.usesInternalIP() ? server.getInternalHost() : server.getExternalHost();

            try {
                var ip = InetAddress.getByName(host);
                var address = ip.getAddress();
                buffer.put((byte)Byte.toUnsignedInt(address[0]));
                buffer.put((byte)Byte.toUnsignedInt(address[1]));
                buffer.put((byte)Byte.toUnsignedInt(address[2]));
                buffer.put((byte)Byte.toUnsignedInt(address[3]));
            } catch (UnknownHostException e) {
                e.printStackTrace();
                buffer.put((byte)127);
                buffer.put((byte)0);
                buffer.put((byte)0);
                buffer.put((byte)1);
            }

            buffer.putInt(server.getPort());
            buffer.put(server.getAgeLimit()); // minimum age
            buffer.put((byte) (server.isPvp() ? 0x01 : 0x00)) ;
            buffer.putShort((short) server.getOnlinePlayersCount());
            buffer.putShort((short) server.getMaxPlayers());

            var status = server.getStatus();
            if(ServerStatus.STATUS_GM_ONLY == status && client.getAccessLevel() < getSettings(AuthServerSettings.class).gmMinimumLevel()) {
                status = ServerStatus.STATUS_DOWN;
            }

            buffer.put((byte) (ServerStatus.STATUS_DOWN == status ? 0x00 : 0x01));
            buffer.putInt(server.getServerType());
            buffer.put((byte) (server.isShowingBrackets() ? 0x01 : 0x00)); // Region
        }

        buffer.putShort((short)0xa4);
        for (var server : servers.values()) {
            buffer.put((byte)server.getId());
            buffer.put((byte)client.getPlayersOnServer(server.getId()));
        }
    }

    @Override
    protected int size(AuthClient client) {
        var serverSize = GameServerManager.getInstance().getRegisteredGameServers().size();
        return super.size(client) + 5 + serverSize * 24;
    }
}
