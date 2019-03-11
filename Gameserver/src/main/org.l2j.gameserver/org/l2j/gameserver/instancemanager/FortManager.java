package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.InstanceListManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.entity.Fort;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FortManager implements InstanceListManager {
    private static final Logger LOGGER = Logger.getLogger(FortManager.class.getName());

    private final Map<Integer, Fort> _forts = new ConcurrentSkipListMap<>();

    public static FortManager getInstance() {
        return SingletonHolder._instance;
    }

    public final Fort findNearestFort(L2Object obj) {
        return findNearestFort(obj, Long.MAX_VALUE);
    }

    public final Fort findNearestFort(L2Object obj, long maxDistance) {
        Fort nearestFort = getFort(obj);
        if (nearestFort == null) {
            for (Fort fort : _forts.values()) {
                final double distance = fort.getDistance(obj);
                if (maxDistance > distance) {
                    maxDistance = (long) distance;
                    nearestFort = fort;
                }
            }
        }
        return nearestFort;
    }

    public final Fort getFortById(int fortId) {
        for (Fort f : _forts.values()) {
            if (f.getResidenceId() == fortId) {
                return f;
            }
        }
        return null;
    }

    public final Fort getFortByOwner(L2Clan clan) {
        for (Fort f : _forts.values()) {
            if (f.getOwnerClan() == clan) {
                return f;
            }
        }
        return null;
    }

    public final Fort getFort(String name) {
        for (Fort f : _forts.values()) {
            if (f.getName().equalsIgnoreCase(name.trim())) {
                return f;
            }
        }
        return null;
    }

    public final Fort getFort(int x, int y, int z) {
        for (Fort f : _forts.values()) {
            if (f.checkIfInZone(x, y, z)) {
                return f;
            }
        }
        return null;
    }

    public final Fort getFort(L2Object activeObject) {
        return getFort(activeObject.getX(), activeObject.getY(), activeObject.getZ());
    }

    public final Collection<Fort> getForts() {
        return _forts.values();
    }

    @Override
    public void loadInstances() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT id FROM fort ORDER BY id")) {
            while (rs.next()) {
                final int fortId = rs.getInt("id");
                _forts.put(fortId, new Fort(fortId));
            }

            LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _forts.values().size() + " fortress");
            for (Fort fort : _forts.values()) {
                fort.getSiege().loadSiegeGuard();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception: loadFortData(): " + e.getMessage(), e);
        }
    }

    @Override
    public void updateReferences() {
    }

    @Override
    public void activateInstances() {
        for (Fort fort : _forts.values()) {
            fort.activateInstance();
        }
    }

    private static class SingletonHolder {
        protected static final FortManager _instance = new FortManager();
    }
}
