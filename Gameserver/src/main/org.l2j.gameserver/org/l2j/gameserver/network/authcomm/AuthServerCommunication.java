package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.Connector;
import io.github.joealisson.mmocore.PacketExecutor;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.network.l2.GameClient;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class AuthServerCommunication implements Runnable, PacketExecutor<AuthServerClient> {

	private static final Logger logger = LoggerFactory.getLogger(AuthServerCommunication.class);
	private static final AuthServerCommunication instance = new AuthServerCommunication();

	private final Map<String, GameClient> waitingClients = new HashMap<>();
	private final Map<String, GameClient> authedClients = new HashMap<>();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private AuthServerClient client;
	private volatile boolean shutdown = false;

	private AuthServerCommunication() { }

	public static AuthServerCommunication getInstance() {
		return instance;
	}

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

	public GameClient addWaitingClient(GameClient client) {
		writeLock.lock();
		try {
			return waitingClients.put(client.getLogin(), client);
		} finally {
			writeLock.unlock();
		}
	}

	public GameClient removeWaitingClient(String account) {
		writeLock.lock();
		try {
			return waitingClients.remove(account);
		} finally {
			writeLock.unlock();
		}
	}

	public GameClient addAuthedClient(GameClient client) {
		writeLock.lock();
		try {
			return authedClients.put(client.getLogin(), client);
		} finally {
			writeLock.unlock();
		}
	}

	public GameClient removeAuthedClient(String login) {
		writeLock.lock();
		try {
			return authedClients.remove(login);
		} finally {
			writeLock.unlock();
		}
	}

	public GameClient getAuthedClient(String login) {
		readLock.lock();
		try {
			return authedClients.get(login);
		} finally {
			readLock.unlock();
		}
	}

	public List<GameClient> getAuthedClientsByIP(String ip) {
		List<GameClient> clients = new ArrayList<GameClient>();

		readLock.lock();
		try {
			for(GameClient client : authedClients.values()) {
				if(client.getIpAddr().equalsIgnoreCase(ip))
					clients.add(client);
			}
		} finally {
			readLock.unlock();
		}
		return clients;
	}

	public List<GameClient> getAuthedClientsByHWID(String hwid) {
		List<GameClient> clients = new ArrayList<>();
		if(isNullOrEmpty(hwid))
			return clients;

		readLock.lock();
		try {
			for(GameClient client : authedClients.values()) {
				String h = client.getHWID();
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

	public GameClient removeClient(GameClient client) {
		writeLock.lock();
		try {
			if(client.getState() == GameClient.GameClientState.AUTHED) {
				return authedClients.remove(client.getLogin());
			} else {
				return waitingClients.remove(client.getLogin());
			}
		} finally {
			writeLock.unlock();
		}
	}

	public String[] getAccounts() {
		readLock.lock();
		try {
			return authedClients.keySet().toArray(new String[authedClients.size()]);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void execute(ReadablePacket<AuthServerClient> packet) {
		ThreadPoolManager.getInstance().execute(packet);
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
		ThreadPoolManager.getInstance().execute(this);
	}
}
