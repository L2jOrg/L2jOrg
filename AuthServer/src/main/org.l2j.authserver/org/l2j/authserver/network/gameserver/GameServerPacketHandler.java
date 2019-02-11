package org.l2j.authserver.network.gameserver;

import io.github.joealisson.mmocore.PacketExecutor;
import io.github.joealisson.mmocore.PacketHandler;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.authserver.controller.ThreadPoolManager;
import org.l2j.authserver.network.gameserver.packet.game2auth.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static java.lang.Byte.toUnsignedInt;
import static org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail.NOT_AUTHED;

public final class GameServerPacketHandler implements PacketHandler<ServerClient>, PacketExecutor<ServerClient> {

    private static final Logger logger = LoggerFactory.getLogger(GameServerPacketHandler.class);

    @Override
    public ReadablePacket<ServerClient> handlePacket(ByteBuffer buffer, ServerClient client) {
        var opcode = toUnsignedInt(buffer.get());
        switch (client.getState()) {
            case CONNECTED:
                return handlePacketInConnected(client, opcode);
            case AUTHED:
                return handleAuthedPacket(client, opcode);
            default:
                handleUnkownOpcode(client, opcode);
        }
        return null;
    }

    private ReadablePacket<ServerClient> handleAuthedPacket(ServerClient client, int opcode) {
        ReadablePacket<ServerClient> packet = null;
        switch (opcode) {
            case 0x03:
                packet = new PlayerInGame();
                break;
            case 0x04:
                packet = new PlayerLogout();
                break;
            case 0x11:
                packet = new ChangeAccessLevel();
                break;
            case 0x02:
                packet = new PlayerAuthRequest();
                break;
            case 0x06:
                packet = new ServerStatus();
                break;
            case 0x05:
                packet = new AccountInfo();
                break;
            default:
                handleUnkownOpcode(client, opcode);
                break;
        }
        return packet;
    }

    private ReadablePacket<ServerClient> handlePacketInConnected(ServerClient client, int opcode) {
        ReadablePacket<ServerClient> packet = null;
        switch (opcode) {
            case 0x00:
                packet = new AuthRequest();
                break;
            default:
                handleUnkownOpcode(client, opcode);
                break;
        }
        return packet;
    }

    private void handleUnkownOpcode(ServerClient client, int opcode) {
        logger.warn("Unknown Opcode ({}) on state {} from GameServer, closing socket.", Integer.toHexString(opcode).toUpperCase(), client.getState());
        client.close(NOT_AUTHED);
    }

    @Override
    public void execute(ReadablePacket<ServerClient> packet) {
        ThreadPoolManager.getInstance().execute(packet);
    }
}
