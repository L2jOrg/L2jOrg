package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.InstanceListManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.items.instance.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class CastleManager implements InstanceListManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CastleManager.class);
    private static final int[] _castleCirclets =
            {
                    0,
                    6838,
                    6835,
                    6839,
                    6837,
                    6840,
                    6834,
                    6836,
                    8182,
                    8183
            };
    private final Map<Integer, Castle> _castles = new ConcurrentSkipListMap<>();
    private final Map<Integer, Long> _castleSiegeDate = new ConcurrentHashMap<>();

    private CastleManager() {
    }

    public final Castle findNearestCastle(WorldObject obj) {
        return findNearestCastle(obj, Long.MAX_VALUE);
    }

    public final Castle findNearestCastle(WorldObject obj, long maxDistance) {
        Castle nearestCastle = getCastle(obj);
        if (nearestCastle == null) {
            double distance;
            for (Castle castle : _castles.values()) {
                distance = castle.getDistance(obj);
                if (maxDistance > distance) {
                    maxDistance = (long) distance;
                    nearestCastle = castle;
                }
            }
        }
        return nearestCastle;
    }

    public final Castle getCastleById(int castleId) {
        return _castles.get(castleId);
    }

    public final Castle getCastleByOwner(Clan clan) {
        for (Castle temp : _castles.values()) {
            if (temp.getOwnerId() == clan.getId()) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastle(String name) {
        for (Castle temp : _castles.values()) {
            if (temp.getName().equalsIgnoreCase(name.trim())) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastle(int x, int y, int z) {
        for (Castle temp : _castles.values()) {
            if (temp.checkIfInZone(x, y, z)) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastle(WorldObject activeObject) {
        return getCastle(activeObject.getX(), activeObject.getY(), activeObject.getZ());
    }

    public final Collection<Castle> getCastles() {
        return _castles.values();
    }

    public boolean hasOwnedCastle() {
        boolean hasOwnedCastle = false;
        for (Castle castle : _castles.values()) {
            if (castle.getOwnerId() > 0) {
                hasOwnedCastle = true;
                break;
            }
        }
        return hasOwnedCastle;
    }

    public int getCircletByCastleId(int castleId) {
        if ((castleId > 0) && (castleId < 10)) {
            return _castleCirclets[castleId];
        }

        return 0;
    }

    // remove this castle's circlets from the clan

    public void removeCirclet(Clan clan, int castleId) {
        for (ClanMember member : clan.getMembers()) {
            removeCirclet(member, castleId);
        }
    }
    public void removeCirclet(ClanMember member, int castleId) {
        if (member == null) {
            return;
        }
        final Player player = member.getPlayerInstance();
        final int circletId = getCircletByCastleId(castleId);

        if (circletId != 0) {
            // online-player circlet removal
            if (player != null) {
                try {
                    final Item circlet = player.getInventory().getItemByItemId(circletId);
                    if (circlet != null) {
                        if (circlet.isEquipped()) {
                            player.getInventory().unEquipItemInSlot(circlet.getLocationSlot());
                        }
                        player.destroyItemByItemId("CastleCircletRemoval", circletId, 1, player, true);
                    }
                    return;
                } catch (NullPointerException e) {
                    // continue removing offline
                }
            }
            // else offline-player circlet removal
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM items WHERE owner_id = ? and item_id = ?")) {
                ps.setInt(1, member.getObjectId());
                ps.setInt(2, circletId);
                ps.execute();
            } catch (Exception e) {
                LOGGER.warn(getClass().getSimpleName() + ": Failed to remove castle circlets offline for player " + member.getName() + ": ", e);
            }
        }
    }

    @Override
    public void loadInstances() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT id FROM castle ORDER BY id")) {
            while (rs.next()) {
                final int castleId = rs.getInt("id");
                _castles.put(castleId, new Castle(castleId));
            }
            LOGGER.info("Loaded: {} castles.", _castles.size());
        } catch (Exception e) {
            LOGGER.error( e.getMessage(), e);
        }
    }

    @Override
    public void updateReferences() {
    }

    @Override
    public void activateInstances() {
        for (Castle castle : _castles.values()) {
            castle.activateInstance();
        }
    }

    public void registerSiegeDate(int castleId, long siegeDate) {
        _castleSiegeDate.put(castleId, siegeDate);
    }

    public int getSiegeDates(long siegeDate) {
        int count = 0;
        for (long date : _castleSiegeDate.values()) {
            if (Math.abs(date - siegeDate) < 1000) {
                count++;
            }
        }
        return count;
    }

    public static CastleManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CastleManager INSTANCE = new CastleManager();
    }
}
