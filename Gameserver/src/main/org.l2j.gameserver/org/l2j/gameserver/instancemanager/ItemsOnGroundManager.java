package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ItemsAutoDestroy;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.items.instance.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * This class manage all items on ground.
 *
 * @author Enforcer
 */
public final class ItemsOnGroundManager implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemsOnGroundManager.class);

    private final Set<Item> _items = ConcurrentHashMap.newKeySet();

    private ItemsOnGroundManager() {
        if (Config.SAVE_DROPPED_ITEM_INTERVAL > 0) {
            ThreadPool.scheduleAtFixedRate(this, Config.SAVE_DROPPED_ITEM_INTERVAL, Config.SAVE_DROPPED_ITEM_INTERVAL);
        }
        load();
    }

    private void load() {
        // If SaveDroppedItem is false, may want to delete all items previously stored to avoid add old items on reactivate
        var generalSettings = getSettings(GeneralSettings.class);
        if (!generalSettings.saveDroppedItems()) {
            if (Config.CLEAR_DROPPED_ITEM_TABLE) {
                emptyTable();
            }
            return;
        }

        // if DestroyPlayerDroppedItem was previously false, items currently protected will be added to ItemsAutoDestroy
        if (Config.DESTROY_DROPPED_PLAYER_ITEM) {
            String str = null;
            if (!Config.DESTROY_EQUIPABLE_PLAYER_ITEM) {
                // Recycle misc. items only
                str = "UPDATE itemsonground SET drop_time = ? WHERE drop_time = -1 AND equipable = 0";
            } else if (Config.DESTROY_EQUIPABLE_PLAYER_ITEM) {
                // Recycle all items including equip-able
                str = "UPDATE itemsonground SET drop_time = ? WHERE drop_time = -1";
            }

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement(str)) {
                ps.setLong(1, System.currentTimeMillis());
                ps.execute();
            } catch (Exception e) {
                LOGGER.error(getClass().getSimpleName() + ": Error while updating table ItemsOnGround " + e.getMessage(), e);
            }
        }

        // Add items to world
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT object_id,item_id,count,enchant_level,x,y,z,drop_time,equipable FROM itemsonground")) {
            int count = 0;
            try (ResultSet rs = ps.executeQuery()) {
                Item item;
                while (rs.next()) {
                    item = new Item(rs.getInt(1), rs.getInt(2));
                    World.getInstance().addObject(item);
                    // this check and..
                    if (item.isStackable() && (rs.getInt(3) > 1)) {
                        item.setCount(rs.getInt(3));
                    }
                    // this, are really necessary?
                    if (rs.getInt(4) > 0) {
                        item.setEnchantLevel(rs.getInt(4));
                    }
                    item.setXYZ(rs.getInt(5), rs.getInt(6), rs.getInt(7));
                    item.setWorldRegion(World.getInstance().getRegion(item));
                    item.getWorldRegion().addVisibleObject(item);
                    final long dropTime = rs.getLong(8);
                    item.setDropTime(dropTime);
                    item.setProtected(dropTime == -1);
                    item.setSpawned(true);
                    World.getInstance().addVisibleObject(item, item.getWorldRegion());
                    _items.add(item);
                    count++;
                    // add to ItemsAutoDestroy only items not protected
                    if (!Config.LIST_PROTECTED_ITEMS.contains(item.getId())) {
                        if (dropTime > -1) {
                            if ((generalSettings.autoDestroyItemTime() > 0 && !item.getTemplate().hasExImmediateEffect()) || (generalSettings.autoDestroyHerbTime() > 0 && item.getTemplate().hasExImmediateEffect())) {
                                ItemsAutoDestroy.getInstance().addItem(item);
                            }
                        }
                    }
                }
            }
            LOGGER.info(getClass().getSimpleName() + ": Loaded " + count + " items.");
        } catch (Exception e) {
            LOGGER.error(getClass().getSimpleName() + ": Error while loading ItemsOnGround " + e.getMessage(), e);
        }

        if (Config.EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD) {
            emptyTable();
        }
    }

    public void save(Item item) {
        if (getSettings(GeneralSettings.class).saveDroppedItems()) {
            _items.add(item);
        }
    }

    public void removeObject(Item item) {
        if (getSettings(GeneralSettings.class).saveDroppedItems()) {
            _items.remove(item);
        }
    }

    public void saveInDb() {
        run();
    }

    public void cleanUp() {
        _items.clear();
    }

    public void emptyTable() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement()) {
            s.executeUpdate("DELETE FROM itemsonground");
        } catch (Exception e1) {
            LOGGER.error(getClass().getSimpleName() + ": Error while cleaning table ItemsOnGround " + e1.getMessage(), e1);
        }
    }

    @Override
    public synchronized void run() {
        if (!getSettings(GeneralSettings.class).saveDroppedItems()) {
            return;
        }

        emptyTable();

        if (_items.isEmpty()) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO itemsonground(object_id,item_id,count,enchant_level,x,y,z,drop_time,equipable) VALUES(?,?,?,?,?,?,?,?,?)")) {
            for (Item item : _items) {
                if (item == null) {
                    continue;
                }

                if (CursedWeaponsManager.getInstance().isCursed(item.getId())) {
                    continue; // Cursed Items not saved to ground, prevent double save
                }

                try {
                    statement.setInt(1, item.getObjectId());
                    statement.setInt(2, item.getId());
                    statement.setLong(3, item.getCount());
                    statement.setInt(4, item.getEnchantLevel());
                    statement.setInt(5, item.getX());
                    statement.setInt(6, item.getY());
                    statement.setInt(7, item.getZ());
                    statement.setLong(8, (item.isProtected() ? -1 : item.getDropTime())); // item is protected or AutoDestroyed
                    statement.setLong(9, (item.isEquipable() ? 1 : 0)); // set equip-able
                    statement.execute();
                    statement.clearParameters();
                } catch (Exception e) {
                    LOGGER.error(getClass().getSimpleName() + ": Error while inserting into table ItemsOnGround: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            LOGGER.error(getClass().getSimpleName() + ": SQL error while storing items on ground: " + e.getMessage(), e);
        }
    }

    public static ItemsOnGroundManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemsOnGroundManager INSTANCE = new ItemsOnGroundManager();
    }
}
