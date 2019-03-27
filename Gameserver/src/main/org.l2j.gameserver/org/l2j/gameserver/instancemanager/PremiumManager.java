package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Mobius
 */
public class PremiumManager {
    // SQL Statement
    private static final String LOAD_SQL = "SELECT account_name,enddate FROM account_premium WHERE account_name = ?";
    private static final String UPDATE_SQL = "REPLACE INTO account_premium (enddate,account_name) VALUE (?,?)";
    private static final String DELETE_SQL = "DELETE FROM account_premium WHERE account_name = ?";
    // Data Cache
    private final Map<String, Long> premiumData = new HashMap<>();
    // expireTasks
    private final Map<String, ScheduledFuture<?>> expiretasks = new HashMap<>();
    // Listeners
    private final ListenersContainer listenerContainer = Containers.Players();
    private final Consumer<OnPlayerLogin> playerLoginEvent = (event) ->
    {
        final L2PcInstance player = event.getActiveChar();
        final String accountName = player.getAccountName();
        loadPremiumData(accountName);
        final long now = System.currentTimeMillis();
        final long premiumExpiration = getPremiumExpiration(accountName);
        player.setPremiumStatus(premiumExpiration > now);

        if (player.hasPremiumStatus()) {
            startExpireTask(player, premiumExpiration - now);
        } else if (premiumExpiration > 0) {
            removePremiumStatus(accountName, false);
        }
    };
    private final Consumer<OnPlayerLogout> playerLogoutEvent = (event) ->
    {
        L2PcInstance player = event.getActiveChar();
        stopExpireTask(player);
    };

    private PremiumManager() {
        listenerContainer.addListener(new ConsumerEventListener(listenerContainer, EventType.ON_PLAYER_LOGIN, playerLoginEvent, this));
        listenerContainer.addListener(new ConsumerEventListener(listenerContainer, EventType.ON_PLAYER_LOGOUT, playerLogoutEvent, this));
    }

    /**
     * @param player
     * @param delay
     */
    private void startExpireTask(L2PcInstance player, long delay) {
        ScheduledFuture<?> task = ThreadPoolManager.getInstance().schedule(new PremiumExpireTask(player), delay);
        expiretasks.put(player.getAccountName(), task);
    }

    /**
     * @param player
     */
    private void stopExpireTask(L2PcInstance player) {
        ScheduledFuture<?> task = expiretasks.remove(player.getAccountName());
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }

    private void loadPremiumData(String accountName) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(LOAD_SQL)) {
            stmt.setString(1, accountName);
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    premiumData.put(rset.getString(1), rset.getLong(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getPremiumExpiration(String accountName) {
        return premiumData.getOrDefault(accountName, 0L);
    }

    public void addPremiumTime(String accountName, int timeValue, TimeUnit timeUnit) {
        long addTime = timeUnit.toMillis(timeValue);
        long now = System.currentTimeMillis();
        // new premium task at least from now
        long oldPremiumExpiration = Math.max(now, getPremiumExpiration(accountName));
        long newPremiumExpiration = oldPremiumExpiration + addTime;

        // UPDATE DATABASE
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_SQL)) {
            stmt.setLong(1, newPremiumExpiration);
            stmt.setString(2, accountName);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // UPDATE CACHE
        premiumData.put(accountName, newPremiumExpiration);

        // UPDATE PlAYER PREMIUMSTATUS
        L2PcInstance playerOnline = L2World.getInstance().getPlayers().stream().filter(p -> accountName.equals(p.getAccountName())).findFirst().orElse(null);
        if (playerOnline != null) {
            stopExpireTask(playerOnline);
            startExpireTask(playerOnline, newPremiumExpiration - now);

            if (!playerOnline.hasPremiumStatus()) {
                playerOnline.setPremiumStatus(true);
            }
        }
    }

    public void removePremiumStatus(String accountName, boolean checkOnline) {
        if (checkOnline) {
            L2PcInstance playerOnline = L2World.getInstance().getPlayers().stream().filter(p -> accountName.equals(p.getAccountName())).findFirst().orElse(null);
            if ((playerOnline != null) && playerOnline.hasPremiumStatus()) {
                playerOnline.setPremiumStatus(false);
                stopExpireTask(playerOnline);
            }
        }

        // UPDATE CACHE
        premiumData.remove(accountName);

        // UPDATE DATABASE
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, accountName);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PremiumManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PremiumManager INSTANCE = new PremiumManager();
    }
    class PremiumExpireTask implements Runnable {

        final L2PcInstance player;

        PremiumExpireTask(L2PcInstance player) {
            this.player = player;
        }

        @Override
        public void run() {
            player.setPremiumStatus(false);
        }
    }
}