/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ItemsAutoDestroy;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.data.ItemOnGroundData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.World;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * This class manage all items on ground.
 *
 * @author Enforcer
 * @author JoeAlisson
 */
public final class ItemsOnGroundManager implements Runnable {

    private final Set<Item> items = ConcurrentHashMap.newKeySet();

    private ItemsOnGroundManager() {
        final var saveDropItemInterval = GeneralSettings.saveDroppedItemInterval();
        if(saveDropItemInterval.compareTo(Duration.ZERO) > 0) {
            ThreadPool.scheduleAtFixedRate(this, saveDropItemInterval, saveDropItemInterval);
        }
    }

    private void load() {
        final var itemDAO = getDAO(ItemDAO.class);
        // If SaveDroppedItem is false, may want to delete all items previously stored to avoid add old items on reactivate
        if (!GeneralSettings.saveDroppedItems()) {
            if(GeneralSettings.clearDroppedItems()) {
                itemDAO.deleteItemsOnGround();
            }
            return;
        }

        if (GeneralSettings.destroyPlayerDroppedItem()) {
            if (!GeneralSettings.destroyEquipableItem()) {
                itemDAO.updateNonEquipDropTimeByNonDestroyable(System.currentTimeMillis());
            } else {
                itemDAO.updateDropTimeByNonDestroyable(System.currentTimeMillis());
            }
        }

        itemDAO.findAllItemsOnGround().stream().map(Item::new).forEach(item -> {
            World.getInstance().addObject(item);
            items.add(item);

            if (!GeneralSettings.isProtectedItem(item.getId()) && !item.isProtected()
                    && ((GeneralSettings.autoDestroyItemTime() > 0 && !item.getTemplate().hasExImmediateEffect())
                        || (GeneralSettings.autoDestroyHerbTime() > 0 && item.getTemplate().hasExImmediateEffect()))) {

                ItemsAutoDestroy.getInstance().addItem(item);
            }
        });

        if (GeneralSettings.clearDroppedItemsAfterLoad()) {
            itemDAO.deleteItemsOnGround();
        }
    }

    public void save(Item item) {
        if (GeneralSettings.saveDroppedItems()) {
            items.add(item);
        }
    }

    public void removeObject(Item item) {
        if (GeneralSettings.saveDroppedItems()) {
            items.remove(item);
        }
    }

    public void saveInDb() {
        run();
    }

    public void cleanUp() {
        items.clear();
    }

    @Override
    public synchronized void run() {
        if (!GeneralSettings.saveDroppedItems()) {
            return;
        }
        final var itemDAO = getDAO(ItemDAO.class);
        itemDAO.deleteItemsOnGround();

        if (items.isEmpty()) {
            return;
        }

        itemDAO.save(items.stream().map(ItemOnGroundData::of).collect(Collectors.toList()));
    }

    public static void init() {
        getInstance().load();
    }

    public static ItemsOnGroundManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemsOnGroundManager INSTANCE = new ItemsOnGroundManager();
    }
}
