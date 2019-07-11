package org.l2j.gameserver.network;

import org.l2j.commons.threading.ThreadPoolManager;
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
    private final L2GameClient _client;
    private final Player _activeChar;

    private Disconnection(L2GameClient client) {
        this(client, client.getActiveChar());
    }
    private Disconnection(Player activeChar) {
        this(activeChar.getClient(), activeChar);
    }

    private Disconnection(L2GameClient client, Player activeChar) {
        _client = getClient(client, activeChar);
        _activeChar = getActiveChar(client, activeChar);

        // Anti Feed
        AntiFeedManager.getInstance().onDisconnect(_client);

        if (_client != null) {
            _client.setActiveChar(null);
        }

        if (_activeChar != null) {
            _activeChar.setClient(null);
        }
    }

    public static L2GameClient getClient(L2GameClient client, Player activeChar) {
        if (client != null) {
            return client;
        }

        if (activeChar != null) {
            return activeChar.getClient();
        }

        return null;
    }

    public static Player getActiveChar(L2GameClient client, Player activeChar) {
        if (activeChar != null) {
            return activeChar;
        }

        if (client != null) {
            return client.getActiveChar();
        }

        return null;
    }

    public static Disconnection of(L2GameClient client) {
        return new Disconnection(client);
    }

    public static Disconnection of(Player activeChar) {
        return new Disconnection(activeChar);
    }

    public static Disconnection of(L2GameClient client, Player activeChar) {
        return new Disconnection(client, activeChar);
    }

    public Disconnection storeMe() {
        try {
            if ((_activeChar != null) && _activeChar.isOnline()) {
                _activeChar.storeMe();
            }

            if(_client != null) {
                _client.storeAccountData();
            }
        } catch (RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return this;
    }

    public Disconnection deleteMe() {
        try {
            if ((_activeChar != null) && _activeChar.isOnline()) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogout(_activeChar), _activeChar);
                _activeChar.deleteMe();
            }
        } catch (RuntimeException e) {
            LOGGER.warn(e.getMessage());
        }

        return this;
    }

    public Disconnection close(boolean toLoginScreen) {
        if (_client != null) {
            _client.close(toLoginScreen);
        }

        return this;
    }

    public Disconnection close(ServerPacket packet) {
        if (_client != null) {
            _client.close(packet);
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
        if (_activeChar != null) {
            ThreadPoolManager.schedule(this::defaultSequence, _activeChar.canLogout() ? 0 : AttackStanceTaskManager.COMBAT_TIME);
        }
    }
}