package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.packet.L2LoginServerPacket;
import org.l2j.commons.Config;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
    public void write() {
        var servers = GameServerManager.getInstance().getRegisteredGameServers();
        writeByte(0x04);
        writeByte(servers.size());
        writeByte(client.getLastServer());

        for (var server : servers.values()) {
            writeByte(server.getId());

            var host = client.usesInternalIP() ? server.getInternalHost() : server.getExternalHost();

            try {
                var ip = InetAddress.getByName(host);
                var address = ip.getAddress();
                writeByte(Byte.toUnsignedInt(address[0]));
                writeByte(Byte.toUnsignedInt(address[1]));
                writeByte(Byte.toUnsignedInt(address[2]));
                writeByte(Byte.toUnsignedInt(address[3]));
            } catch (UnknownHostException e) {
                e.printStackTrace();
                writeByte(127);
                writeByte(0);
                writeByte(0);
                writeByte(1);
            }

            writeInt(server.getPort());
            writeByte(0x00); // minimum age
            writeByte(server.isPvp() ? 1 : 0);
            writeShort(server.getOnlinePlayersCount());
            writeShort(server.getMaxPlayers());

            var status = server.getStatus();
            if(ServerStatus.STATUS_GM_ONLY == status && client.getAccessLevel() < Config.GM_MIN) {
                status = ServerStatus.STATUS_DOWN;
            }

            writeByte(ServerStatus.STATUS_DOWN == status ? 0x00 : 0x01);
            writeInt(server.getServerType());
            writeByte(server.isShowingBrackets() ? 0x01 : 0x00); // Region
        }

        writeShort(0xa4);
        for (var server : servers.values()) {
            writeByte(server.getId());
            writeByte(client.getPlayersOnServer(server.getId()));
        }
    }

    @Override
    protected int packetSize() {
        var serverSize = GameServerManager.getInstance().getRegisteredGameServers().size();
        return super.packetSize() + 5 + serverSize * 24;
    }
}
