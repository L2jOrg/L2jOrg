package org.l2j.gameserver.instancemanager;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.InstanceListManager;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.items.instance.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * @author JoeAlisson
 */
public final class CastleManager implements InstanceListManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CastleManager.class);

    private static final int[] castleCirclets = {
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

    private final IntMap<Castle> castles = new CHashIntMap<>();
    private final IntMap<LocalDateTime> castleSiegesDate = new CHashIntMap<>();

    private CastleManager() {
    }

    public final Castle findNearestCastle(WorldObject obj) {
        return findNearestCastle(obj, Long.MAX_VALUE);
    }

    public final Castle findNearestCastle(WorldObject obj, long maxDistance) {
        Castle nearestCastle = getCastle(obj);

        if (nearestCastle == null) {
            double distance;
            for (Castle castle : castles.values()) {
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
        return castles.get(castleId);
    }

    public final Castle getCastleByOwner(Clan clan) {
        for (Castle temp : castles.values()) {
            if (temp.getOwnerId() == clan.getId()) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastle(String name) {
        for (Castle temp : castles.values()) {
            if (temp.getName().equalsIgnoreCase(name.trim())) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastle(int x, int y, int z) {
        return castles.values().stream().filter(c -> c.checkIfInZone(x, y, z)).findFirst().orElse(null);
    }

    public final Castle getCastle(ILocational loc) {
        return getCastle(loc.getX(), loc.getY(), loc.getZ());
    }

    public final Collection<Castle> getCastles() {
        return castles.values();
    }

    public int getCircletByCastleId(int castleId) {
        if ((castleId > 0) && (castleId < 10)) {
            return castleCirclets[castleId];
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
                            player.getInventory().unEquipItemInSlot(InventorySlot.fromId(circlet.getLocationSlot()));
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
                castles.put(castleId, new Castle(castleId));
            }
            LOGGER.info("Loaded: {} castles.", castles.size());
        } catch (Exception e) {
            LOGGER.error( e.getMessage(), e);
        }
    }

    @Override
    public void updateReferences() {
    }

    @Override
    public void activateInstances() {
        castles.values().forEach(Castle::activateInstance);
    }

    public void registerSiegeDate(Castle castle, LocalDateTime siegeDate) {
        castle.setSiegeDate(siegeDate);
        castleSiegesDate.put(castle.getId(), siegeDate);
    }

    public Siege getSiegeOnLocation(ILocational loc) {
        return castles.values().stream().map(Castle::getSiege).filter(s -> s.checkIfInZone(loc)).findFirst().orElse(null);
    }

    public int getSiegesOnDate(LocalDateTime siegeDate) {
        return (int) castleSiegesDate.values().stream().filter(date -> ChronoUnit.DAYS.between(siegeDate, date) == 0).count();
    }

    public static CastleManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CastleManager INSTANCE = new CastleManager();
    }
}
