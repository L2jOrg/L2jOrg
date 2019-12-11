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

    private Disconnection(GameClient client, Player activeChar) {
        this.client = getClient(client, activeChar);
        player = getPlayer(client, activeChar);

        // Anti Feed
        AntiFeedManager.getInstance().onDisconnect(this.client);

        if (this.client != null) {
            this.client.setPlayer(null);
        }

        if (player != null) {
            player.setClient(null);
        }
    }

    public static GameClient getClient(GameClient client, Player player) {
        if (client != null) {
            return client;
        }

        if (player != null) {
            return player.getClient();
        }

        return null;
    }

    public static Player getPlayer(GameClient client, Player activeChar) {
        if (activeChar != null) {
            return activeChar;
        }

        if (client != null) {
            return client.getPlayer();
        }

        return null;
    }

    public static Disconnection of(GameClient client) {
        return new Disconnection(client);
    }

    public static Disconnection of(Player activeChar) {
        return new Disconnection(activeChar);
    }

    public static Disconnection of(GameClient client, Player activeChar) {
        return new Disconnection(client, activeChar);
    }

    public Disconnection storeMe() {
        try {
            if ((player != null) && player.isOnline()) {
                player.storeMe();
            }

            if(client != null) {
                client.storeAccountData();
            }
        } catch (RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
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