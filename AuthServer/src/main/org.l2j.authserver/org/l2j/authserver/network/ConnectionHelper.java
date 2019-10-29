package org.l2j.authserver.network;

import io.github.joealisson.mmocore.ConnectionFilter;
import io.github.joealisson.mmocore.PacketExecutor;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.commons.threading.ThreadPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class ConnectionHelper implements PacketExecutor<AuthClient>,  ConnectionFilter {

    @Override
    public void execute(ReadablePacket<AuthClient> packet) {
        ThreadPool.execute(packet);
    }

    @Override
    public boolean accept(AsynchronousSocketChannel channel) {
        try {
            var socketAddress = (InetSocketAddress) channel.getRemoteAddress();
            return !AuthController.getInstance().isBannedAddress(socketAddress.getAddress().getHostAddress());
        } catch (IOException e) {
            return false;
        }
    }
}
