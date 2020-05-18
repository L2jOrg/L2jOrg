package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.InstanceListManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.entity.Fort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;


public final class FortDataManager implements InstanceListManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FortDataManager.class);

    private static final Map<Integer, Fort> _forts = new ConcurrentSkipListMap<>();

    private FortDataManager() {

    }

    public final Fort findNearestFort(WorldObject obj) {
        return findNearestFort(obj, Long.MAX_VALUE);
    }

    public final Fort findNearestFort(WorldObject obj, long maxDistance) {
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
            if (f.getId() == fortId) {
                return f;
            }
        }
        return null;
    }

    public final Fort getFortByOwner(Clan clan) {
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

    public final Fort getFort(WorldObject activeObject) {
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
            LOGGER.warn("Exception: loadFortData(): " + e.getMessage(), e);
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

    public static FortDataManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final FortDataManager INSTANCE = new FortDataManager();
    }
}
