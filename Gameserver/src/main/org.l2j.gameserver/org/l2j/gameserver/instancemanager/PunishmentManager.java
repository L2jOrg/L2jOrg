package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.holders.PunishmentHolder;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public final class PunishmentManager {
    private static final Logger LOGGER = Logger.getLogger(PunishmentManager.class.getName());

    private final Map<PunishmentAffect, PunishmentHolder> _tasks = new ConcurrentHashMap<>();

    protected PunishmentManager() {
        load();
    }

    /**
     * Gets the single instance of {@code PunishmentManager}.
     *
     * @return single instance of {@code PunishmentManager}
     */
    public static PunishmentManager getInstance() {
        return SingletonHolder._instance;
    }

    private void load() {
        // Initiate task holders.
        for (PunishmentAffect affect : PunishmentAffect.values()) {
            _tasks.put(affect, new PunishmentHolder());
        }

        int initiated = 0;
        int expired = 0;

        // Load punishments.
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery("SELECT * FROM punishments")) {
            while (rset.next()) {
                final int id = rset.getInt("id");
                final String key = rset.getString("key");
                final PunishmentAffect affect = PunishmentAffect.getByName(rset.getString("affect"));
                final PunishmentType type = PunishmentType.getByName(rset.getString("type"));
                final long expirationTime = rset.getLong("expiration");
                final String reason = rset.getString("reason");
                final String punishedBy = rset.getString("punishedBy");
                if ((type != null) && (affect != null)) {
                    if ((expirationTime > 0) && (System.currentTimeMillis() > expirationTime)) // expired task.
                    {
                        expired++;
                    } else {
                        initiated++;
                        _tasks.get(affect).addPunishment(new PunishmentTask(id, key, affect, type, expirationTime, reason, punishedBy, true));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while loading punishments: ", e);
        }

        LOGGER.info(getClass().getSimpleName() + ": Loaded " + initiated + " active and " + expired + " expired punishments.");
    }

    public void startPunishment(PunishmentTask task) {
        _tasks.get(task.getAffect()).addPunishment(task);
    }

    public void stopPunishment(PunishmentAffect affect, PunishmentType type) {
        final PunishmentHolder holder = _tasks.get(affect);
        if (holder != null) {
            holder.stopPunishment(type);
        }
    }

    public void stopPunishment(Object key, PunishmentAffect affect, PunishmentType type) {
        final PunishmentTask task = getPunishment(key, affect, type);
        if (task != null) {
            _tasks.get(affect).stopPunishment(task);
        }
    }

    public boolean hasPunishment(Object key, PunishmentAffect affect, PunishmentType type) {
        final PunishmentHolder holder = _tasks.get(affect);
        return holder.hasPunishment(String.valueOf(key), type);
    }

    public long getPunishmentExpiration(Object key, PunishmentAffect affect, PunishmentType type) {
        final PunishmentTask p = getPunishment(key, affect, type);
        return p != null ? p.getExpirationTime() : 0;
    }

    private PunishmentTask getPunishment(Object key, PunishmentAffect affect, PunishmentType type) {
        return _tasks.get(affect).getPunishment(String.valueOf(key), type);
    }

    private static class SingletonHolder {
        protected static final PunishmentManager _instance = new PunishmentManager();
    }
}
