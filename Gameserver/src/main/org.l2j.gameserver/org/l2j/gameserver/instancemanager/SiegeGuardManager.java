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

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.SiegeDAO;
import org.l2j.gameserver.data.database.data.CastleSiegeGuardData;
import org.l2j.gameserver.data.xml.impl.CastleDataManager;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.instance.Defender;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.holders.SiegeGuardHolder;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * Siege Guard Manager.
 *
 * @author St3eT
 */
public final class SiegeGuardManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiegeGuardManager.class);

    private final Set<Item> _droppedTickets = ConcurrentHashMap.newKeySet();
    private final IntMap<Set<Spawn>> _siegeGuardSpawn = new CHashIntMap<>();

    private SiegeGuardManager() {
        _droppedTickets.clear();
    }

    private void load() {
        getDAO(SiegeDAO.class).loadHiredGuards().forEach(this::loadHiredGuard);
        LOGGER.info("Loaded {} siege guards tickets.", _droppedTickets.size());
    }

    private void loadHiredGuard(CastleSiegeGuardData data) {
        final Castle castle = CastleManager.getInstance().getCastle(data.getX(), data.getY(), data.getZ());
        if (isNull(castle)) {
            LOGGER.warn("Siege guard ticket cannot be placed! Castle is null at x: {}, y: {}, z:{}", data.getX(), data.getY(), data.getZ());
            return;
        }

        final SiegeGuardHolder holder = getSiegeGuardByNpc(castle.getId(), data.getNpcId());
        if (nonNull(holder) && !castle.getSiege().isInProgress()) {
            final Item dropticket = ItemEngine.getInstance().createItem("SiegeGuard", holder.itemId(),1,  null, null);
            dropticket.dropMe(null, data.getX(), data.getY(), data.getZ());
            _droppedTickets.add(dropticket);
        }
    }

    /**
     * Finds {@code SiegeGuardHolder} equals to castle id and npc id.
     *
     * @param castleId the ID of the castle
     * @param itemId   the ID of the item
     * @return the {@code SiegeGuardHolder} for this castle ID and item ID if any, otherwise {@code null}
     */
    public SiegeGuardHolder getSiegeGuardByItem(int castleId, int itemId) {
        return CastleDataManager.getInstance().getSiegeGuardsForCastle(castleId).stream().filter(g -> (g.itemId() == itemId)).findFirst().orElse(null);
    }

    /**
     * Finds {@code SiegeGuardHolder} equals to castle id and npc id.
     *
     * @param castleId the ID of the castle
     * @param npcId    the ID of the npc
     * @return the {@code SiegeGuardHolder} for this castle ID and npc ID if any, otherwise {@code null}
     */
    public SiegeGuardHolder getSiegeGuardByNpc(int castleId, int npcId) {
        return CastleDataManager.getInstance().getSiegeGuardsForCastle(castleId).stream().filter(g -> (g.npcId() == npcId)).findFirst().orElse(null);
    }

    /**
     * Checks if {@code PlayerInstance} is too much close to another ticket.
     *
     * @param player the PlayerInstance
     * @return {@code true} if {@code PlayerInstance} is too much close to another ticket, {@code false} otherwise
     */
    public boolean isTooCloseToAnotherTicket(Player player) {
        return _droppedTickets.stream().anyMatch(g -> MathUtil.isInsideRadius3D(g, player, 25));
    }

    /**
     * Checks if castle is under npc limit.
     *
     * @param castleId the ID of the castle
     * @param itemId   the ID of the item
     * @return {@code true} if castle is under npc limit, {@code false} otherwise
     */
    public boolean isAtNpcLimit(int castleId, int itemId) {
        final long count = _droppedTickets.stream().filter(i -> i.getId() == itemId).count();
        final SiegeGuardHolder holder = getSiegeGuardByItem(castleId, itemId);
        return count >= holder.maxNpcAmount();
    }

    /**
     * Adds ticket in current world.
     *
     * @param itemId the ID of the item
     * @param player the PlayerInstance
     */
    public void addTicket(int itemId, Player player) {
        final Castle castle = CastleManager.getInstance().getCastle(player);
        if (castle == null) {
            return;
        }

        if (isAtNpcLimit(castle.getId(), itemId)) {
            return;
        }

        final SiegeGuardHolder holder = getSiegeGuardByItem(castle.getId(), itemId);
        if (nonNull(holder)) {
            CastleSiegeGuardData data = CastleSiegeGuardData.of(castle.getId(), holder.npcId(), player.getLocation(), 1);
            getDAO(SiegeDAO.class).save(data);
            spawnMercenary(player, holder);
            final Item dropticket = ItemEngine.getInstance().createItem("SiegeGuard", itemId, 1, null, player);
            dropticket.dropMe(null, player.getX(), player.getY(), player.getZ());
            _droppedTickets.add(dropticket);
        }
    }

    /**
     * Spawns Siege Guard in current world.
     *
     * @param pos    the object containing the spawn location coordinates
     * @param holder SiegeGuardHolder holder
     */
    private void spawnMercenary(IPositionable pos, SiegeGuardHolder holder) {
        final NpcTemplate template = NpcData.getInstance().getTemplate(holder.npcId());
        if (template != null) {
            final Defender npc = new Defender(template);
            npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
            npc.setDecayed(false);
            npc.setHeading(pos.getHeading());
            npc.spawnMe(pos.getX(), pos.getY(), (pos.getZ() + 20));
            npc.scheduleDespawn(3000);
            npc.setIsImmobilized(holder.stationary());
        }
    }

    /**
     * Delete all tickets from a castle.
     *
     * @param castleId the ID of the castle
     */
    public void deleteTickets(int castleId) {
        for (Item ticket : _droppedTickets) {
            if ((ticket != null) && (getSiegeGuardByItem(castleId, ticket.getId()) != null)) {
                ticket.decayMe();
                _droppedTickets.remove(ticket);
            }
        }
    }

    /**
     * remove a single ticket and its associated spawn from the world (used when the castle lord picks up a ticket, for example).
     *
     * @param item the item ID
     */
    public void removeTicket(Item item) {
        final Castle castle = CastleManager.getInstance().getCastle(item);
        if (castle == null) {
            return;
        }

        final SiegeGuardHolder holder = getSiegeGuardByItem(castle.getId(), item.getId());
        if (holder == null) {
            return;
        }

        removeSiegeGuard(holder.npcId(), item);
        _droppedTickets.remove(item);
    }

    /**
     * Loads all siege guards for castle.
     *
     * @param castle the castle instance
     */
    private void loadSiegeGuard(Castle castle) {
        for (CastleSiegeGuardData data : getDAO(SiegeDAO.class).loadGuardOfCastle(castle.getId(), castle.getOwnerId() > 0 ? 1 : 0)) {
            final Spawn spawn;
            try {
                spawn = new Spawn(data.getNpcId());
                spawn.setAmount(1);
                spawn.setXYZ(data.getX(), data.getY(), data.getZ());
                spawn.setHeading(data.getHeading());
                spawn.setRespawnDelay(data.getRespawnDelay());
                spawn.setLocationId(0);

                getSpawnedGuards(castle.getId()).add(spawn);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                LOGGER.warn("Error loading siege guard for castle {}", castle.getName(), e);
            }
        }
    }

    /**
     * Remove single siege guard.
     *
     * @param npcId the ID of NPC
     * @param pos
     */
    public void removeSiegeGuard(int npcId, IPositionable pos) {
        getDAO(SiegeDAO.class).deleteGuard(npcId, pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Remove all siege guards for castle.
     *
     * @param castle the castle instance
     */
    public void removeSiegeGuards(Castle castle) {
        getDAO(SiegeDAO.class).deleteHiredGuardsOfCastle(castle.getId());
    }

    /**
     * Spawn all siege guards for castle.
     *
     * @param castle the castle instance
     */
    public void spawnSiegeGuard(Castle castle) {
        try {
            final boolean isHired = (castle.getOwnerId() > 0);
            loadSiegeGuard(castle);

            for (Spawn spawn : getSpawnedGuards(castle.getId())) {
                if (spawn != null) {
                    spawn.init();
                    if (isHired || (spawn.getRespawnDelay() == 0)) {
                        spawn.stopRespawn();
                    }

                    final SiegeGuardHolder holder = getSiegeGuardByNpc(castle.getId(), spawn.getLastSpawn().getId());
                    if (holder == null) {
                        continue;
                    }

                    spawn.getLastSpawn().setIsImmobilized(holder.stationary());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error spawning siege guards for castle " + castle.getName(), e);
        }
    }

    /**
     * Unspawn all siege guards for castle.
     *
     * @param castle the castle instance
     */
    public void unspawnSiegeGuard(Castle castle) {
        for (Spawn spawn : getSpawnedGuards(castle.getId())) {
            if ((spawn != null) && (spawn.getLastSpawn() != null)) {
                spawn.stopRespawn();
                spawn.getLastSpawn().doDie(spawn.getLastSpawn());
            }
        }
        getSpawnedGuards(castle.getId()).clear();
    }

    public Set<Spawn> getSpawnedGuards(int castleId) {
        return _siegeGuardSpawn.computeIfAbsent(castleId, key -> ConcurrentHashMap.newKeySet());
    }

    public static void init() {
        getInstance().load();
    }

    public static SiegeGuardManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final SiegeGuardManager INSTANCE = new SiegeGuardManager();
    }
}
