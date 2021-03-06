/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

/**
 * @author NB4L1
 * @author JoeAlisson
 */
public final class Disconnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(Disconnection.class);

    private final GameClient client;
    private final Player player;

    private Disconnection(GameClient client) {
        this(client, client.getPlayer());
    }

    private Disconnection(Player player) {
        this(player.getClient(), player);
    }

    private Disconnection(GameClient client, Player player) {
        this.client = getClient(client, player);
        this.player = getPlayer(client, player);

        AntiFeedManager.getInstance().onDisconnect(this.client);
    }

    private GameClient getClient(GameClient client, Player player) {
        if (nonNull(client)) {
            return client;
        }
        if (nonNull(player)) {
            return player.getClient();
        }
        return null;
    }

    private Player getPlayer(GameClient client, Player player) {
        if (nonNull(player)) {
            return player;
        }

        if (nonNull(client)) {
            return client.getPlayer();
        }
        return null;
    }

    public void logout(boolean toLoginScreen) {
        defaultSequence();
        close(toLoginScreen);
    }

    private void close(boolean toLoginScreen) {
        if (nonNull(client)) {
            client.close(toLoginScreen);
        }
    }

    public void restart() {
        defaultSequence();
    }

    private void defaultSequence() {
        if(nonNull(player)) {
            EventDispatcher.getInstance().notifyEvent(new OnPlayerLogout(player), player);
        }
        storeMe();
        deleteMe();
    }

    private void storeMe() {
        if (nonNull(player)) {
            try {
                player.storeMe();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if(nonNull(client)) {
            try{
                client.storeAccountData();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void deleteMe() {
        if (nonNull(player) && player.isOnline()) {
            try {
                player.deleteMe();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
        }
        detachPlayerFromClient();
    }

    private void detachPlayerFromClient() {
        if (nonNull(client)) {
            client.setPlayer(null);
        }
    }

    public void onDisconnection() {
        if (player != null) {
            ThreadPool.schedule(this::defaultSequence, GameUtils.canLogout(player) ? 0 : AttackStanceTaskManager.COMBAT_TIME);
        }
    }

    public static Disconnection of(GameClient client) {
        return new Disconnection(client);
    }

    public static Disconnection of(Player player) {
        return new Disconnection(player);
    }

    public static Disconnection of(GameClient client, Player player) {
        return new Disconnection(client, player);
    }
}