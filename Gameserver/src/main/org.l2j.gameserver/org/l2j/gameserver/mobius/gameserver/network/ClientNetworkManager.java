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
package org.l2j.gameserver.mobius.gameserver.network;

import com.l2jmobius.Config;
import org.l2j.commons.network.NetworkManager;

/**
 * @author Nos
 */
public class ClientNetworkManager extends NetworkManager {
    protected ClientNetworkManager() {
        super(EventLoopGroupManager.getInstance().getBossGroup(), EventLoopGroupManager.getInstance().getWorkerGroup(), new ClientInitializer(), Config.GAMESERVER_HOSTNAME, Config.PORT_GAME);
    }

    public static ClientNetworkManager getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final ClientNetworkManager _instance = new ClientNetworkManager();
    }
}
