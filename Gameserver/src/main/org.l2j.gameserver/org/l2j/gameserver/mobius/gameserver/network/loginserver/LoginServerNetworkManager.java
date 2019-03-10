/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.loginserver;

import com.l2jmobius.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.l2j.gameserver.mobius.gameserver.network.EventLoopGroupManager;

import java.util.logging.Logger;

/**
 * @author NosBit
 */
public class LoginServerNetworkManager {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());

    private final Bootstrap _bootstrap;

    private ChannelFuture _channelFuture;

    public LoginServerNetworkManager() {
        //@formatter:off
        _bootstrap = new Bootstrap()
                .group(EventLoopGroupManager.getInstance().getWorkerGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoginServerInitializer());
        //@formatter:on
    }

    public static LoginServerNetworkManager getInstance() {
        return SingletonHolder._instance;
    }

    public ChannelFuture getChannelFuture() {
        return _channelFuture;
    }

    public void connect() throws InterruptedException {
        if ((_channelFuture != null) && _channelFuture.isSuccess()) {
            return;
        }
        _channelFuture = _bootstrap.connect(Config.GAME_SERVER_LOGIN_HOST, Config.GAME_SERVER_LOGIN_PORT).sync();
        LOGGER.info("Connected to " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
    }

    public void disconnect() throws InterruptedException {
        _channelFuture.channel().close().sync();
    }

    private static class SingletonHolder {
        protected static final LoginServerNetworkManager _instance = new LoginServerNetworkManager();
    }
}
