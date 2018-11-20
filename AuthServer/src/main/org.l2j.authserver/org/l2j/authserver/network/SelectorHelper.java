package org.l2j.authserver.network;

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.controller.ThreadPoolManager;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.mmocore.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author KenM
 */
public class SelectorHelper implements PacketExecutor<AuthClient>,  ConnectionFilter {

    @Override
    public void execute(ReadablePacket<AuthClient> packet) {
        ThreadPoolManager.getInstance().execute(packet);
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
