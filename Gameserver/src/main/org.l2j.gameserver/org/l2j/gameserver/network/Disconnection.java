/*
 * Copyright © 2019-2020 L2JOrg
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
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

/**
 * @author NB4L1
 */
public final class Disconnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(Disconnection.class);
    private final GameClient client;
    private final Player player;

    private Disconnection(GameClient client) {
        this(client, client.getPlayer());
    }
    private Disconnection(Player activeChar) {
        this(activeChar.getClient(), activeChar);
    }

    private Disconnection(GameClient client, Player player) {
        this.client = getClient(client, player);
        this.player = getPlayer(client, player);

        // Anti Feed
        AntiFeedManager.getInstance().onDisconnect(this.client);

        if (this.client != null) {
            this.client.setPlayer(null);
        }

        if (this.player != null) {
            this.player.setClient(null);
        }
    }

    public static GameClient getClient(GameClient client, Player player) {
        if (nonNull(client)) {
            return client;
        }
        if (nonNull(player)) {
            return player.getClient();
        }
        return null;
    }

    public static Player getPlayer(GameClient client, Player player) {
        if (nonNull(player)) {
            return player;
        }

        if (nonNull(client)) {
            return client.getPlayer();
        }
        return null;
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

    public Disconnection storeMe() {
        try {
            if(nonNull(player)) {
                player.storeMe();
            }

            if(nonNull(client)) {
                client.storeAccountData();
            }
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return this;
    }

    public Disconnection deleteMe() {
        try {
            if ((player != null) && player.isOnline()) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogout(player), player);
                player.deleteMe();
            }
        } catch (RuntimeException e) {
            LOGGER.warn(e.getMessage());
        }

        return this;
    }

    public Disconnection close(boolean toLoginScreen) {
        if (client != null) {
            client.close(toLoginScreen);
        }

        return this;
    }

    public Disconnection close(ServerPacket packet) {
        if (client != null) {
            client.close(packet);
        }

        return this;
    }

    public void defaultSequence(boolean toLoginScreen) {
        defaultSequence();
        close(toLoginScreen);
    }

    public void defaultSequence(ServerPacket packet) {
        defaultSequence();
        close(packet);
    }

    private void defaultSequence() {
        storeMe();
        deleteMe();
    }

    public void onDisconnection() {
        if (player != null) {
            ThreadPool.schedule(this::defaultSequence, player.canLogout() ? 0 : AttackStanceTaskManager.COMBAT_TIME);
        }
    }
}