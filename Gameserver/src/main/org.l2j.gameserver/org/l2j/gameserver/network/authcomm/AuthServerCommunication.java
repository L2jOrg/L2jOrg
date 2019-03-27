package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.Connector;
import io.github.joealisson.mmocore.PacketExecutor;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.authcomm.gs2as.ChangePassword;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class AuthServerCommunication implements Runnable, PacketExecutor<AuthServerClient> {

    private static final Logger logger = LoggerFactory.getLogger(AuthServerCommunication.class);

    private final Map<String, L2GameClient> waitingClients = new HashMap<>();
    private final Map<String, L2GameClient> authedClients = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private AuthServerClient client;
    private volatile boolean shutdown = false;

    private AuthServerCommunication() { }

    public void connect() throws IOException, ExecutionException, InterruptedException {
        var serverSettings = getSettings(ServerSettings.class);
        logger.info("Connecting to authserver on {}:{}",serverSettings.authServerAddress(), serverSettings.authServerPort());
        InetSocketAddress address = isNullOrEmpty(serverSettings.authServerAddress()) ? new InetSocketAddress(serverSettings.port()) : new InetSocketAddress(serverSettings.authServerAddress(), serverSettings.authServerPort());
        client = Connector.create(AuthServerClient::new, new PacketHandler(), this).connect(address);
    }

    @Override
    public void run() {
        while (!shutdown && (isNull(client) || !client.isConnected())) {
            try {
                connect();
            } catch (IOException | ExecutionException | InterruptedException e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    // do nothing
                }
            }
        }
    }

    public L2GameClient addWaitingClient(L2GameClient client) {
        writeLock.lock();
        try {
            return waitingClients.put(client.getAccountName(), client);
        } finally {
            writeLock.unlock();
        }
    }

    public L2GameClient removeWaitingClient(String account) {
        writeLock.lock();
        try {
            return waitingClients.remove(account);
        } finally {
            writeLock.unlock();
        }
    }

    public L2GameClient addAuthedClient(L2GameClient client) {
        writeLock.lock();
        try {
            return authedClients.put(client.getAccountName(), client);
        } finally {
            writeLock.unlock();
        }
    }

    public L2GameClient removeAuthedClient(String login) {
        writeLock.lock();
        try {
            return authedClients.remove(login);
        } finally {
            writeLock.unlock();
        }
    }

    public L2GameClient getAuthedClient(String login) {
        readLock.lock();
        try {
            return authedClients.get(login);
        } finally {
            readLock.unlock();
        }
    }

    public List<L2GameClient> getAuthedClientsByIP(String ip) {
        List<L2GameClient> clients = new ArrayList<>();

        readLock.lock();
        try {
            for(L2GameClient client : authedClients.values()) {
                if(client.getHostAddress().equalsIgnoreCase(ip))
                    clients.add(client);
            }
        } finally {
            readLock.unlock();
        }
        return clients;
    }

    public List<L2GameClient> getAuthedClientsByHWID(String hwid) {
        List<L2GameClient> clients = new ArrayList<>();
        if(isNullOrEmpty(hwid))
            return clients;

        readLock.lock();
        try {
            for(L2GameClient client : authedClients.values()) {
                String h = client.getHardwareInfo().getMacAddress();
                if(!isNullOrEmpty(h) && h.equalsIgnoreCase(hwid))
                    clients.add(client);
            }
        }
        finally
        {
            readLock.unlock();
        }
        return clients;
    }

    public L2GameClient removeClient(L2GameClient client) {
        writeLock.lock();
        try {
            if(client.getConnectionState() == ConnectionState.AUTHENTICATED) {
                return authedClients.remove(client.getAccountName());
            } else {
                return waitingClients.remove(client.getAccountName());
            }
        } finally {
            writeLock.unlock();
        }
    }

    public String[] getAccounts() {
        readLock.lock();
        try {
            return authedClients.keySet().toArray(String[]::new);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void execute(ReadablePacket<AuthServerClient> packet) {
        ThreadPoolManager.execute(packet);
    }

    public void shutdown() {
        shutdown = true;
        if(nonNull(client) && client.isConnected()) {
            client.close(null);
        }
    }

    public void sendPacket(SendablePacket packet) {
        if(nonNull(client) && client.isConnected()) {
            client.sendPacket(packet);
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void restart() {
        restart(5);
    }

    public void restart(int waitSeconds) {
        if(nonNull(client)) {
            client.close(null);
        }
        client = null;
        ThreadPoolManager.schedule(this, waitSeconds, TimeUnit.SECONDS);
    }

    public void sendChangePassword(String accountName, String oldPass, String curpass) {
        sendPacket(new ChangePassword(accountName, oldPass, curpass, ""));
    }

    public static AuthServerCommunication getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AuthServerCommunication INSTANCE = new AuthServerCommunication();
    }
}
