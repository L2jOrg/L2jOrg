package org.l2j.gameserver.model;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.instancemanager.PlayerCountManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCreatureSee;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.DeleteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class L2World {

    private static final Logger LOGGER = LoggerFactory.getLogger(L2World.class);

    /**
     * Gracia border Flying objects not allowed to the east of it.
     */
    public static final int GRACIA_MAX_X = -166168;
    public static final int GRACIA_MAX_Z = 6105;
    public static final int GRACIA_MIN_Z = -895;
    /**
     * Bit shift, defines number of regions note, shifting by 15 will result in regions corresponding to map tiles shifting by 11 divides one tile to 16x16 regions.
     */
    public static final int SHIFT_BY = 11;
    public static final int TILE_SIZE = 32768;
    /**
     * Map dimensions.
     */
    public static final int TILE_X_MIN = 11;
    public static final int TILE_X_MAX = 28;
    public static final int TILE_Y_MIN = 10;
    public static final int TILE_Y_MAX = 26;
    public static final int TILE_ZERO_COORD_X = 20;
    public static final int TILE_ZERO_COORD_Y = 18;
    public static final int MAP_MIN_X = (TILE_X_MIN - TILE_ZERO_COORD_X) * TILE_SIZE;
    public static final int MAP_MAX_X = ((TILE_X_MAX - TILE_ZERO_COORD_X) + 1) * TILE_SIZE;
    public static final int MAP_MIN_Y = (TILE_Y_MIN - TILE_ZERO_COORD_Y) * TILE_SIZE;
    public static final int MAP_MAX_Y = ((TILE_Y_MAX - TILE_ZERO_COORD_Y) + 1) * TILE_SIZE;
    /**
     * Calculated offset used so top left region is 0,0
     */
    public static final int OFFSET_X = Math.abs(MAP_MIN_X >> SHIFT_BY);
    public static final int OFFSET_Y = Math.abs(MAP_MIN_Y >> SHIFT_BY);
    /**
     * Number of regions.
     */
    private static final int REGIONS_X = (MAP_MAX_X >> SHIFT_BY) + OFFSET_X;
    private static final int REGIONS_Y = (MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y;
    /**
     * Map containing all the players in game.
     */
    private final Map<Integer, Player> _allPlayers = new ConcurrentHashMap<>();
    /**
     * Map containing all visible objects.
     */
    private final Map<Integer, WorldObject> _allObjects = new ConcurrentHashMap<>();
    /**
     * Map with the pets instances and their owner ID.
     */
    private final Map<Integer, Pet> _petsInstance = new ConcurrentHashMap<>();

    private final AtomicInteger _partyNumber = new AtomicInteger();
    private final AtomicInteger _memberInPartyNumber = new AtomicInteger();

    private final L2WorldRegion[][] _worldRegions = new L2WorldRegion[REGIONS_X + 1][REGIONS_Y + 1];

    private L2World() {
        initRegions();
    }

    private void initRegions() {
        for (int x = 0; x <= REGIONS_X; x++) {
            for (int y = 0; y <= REGIONS_Y; y++) {
                _worldRegions[x][y] = new L2WorldRegion(x, y);
            }
        }

        // Set surrounding regions.
        for (int rx = 0; rx <= REGIONS_X; rx++) {
            for (int ry = 0; ry <= REGIONS_Y; ry++) {
                final List<L2WorldRegion> surroundingRegions = new ArrayList<>();
                for (int sx = rx - 1; sx <= (rx + 1); sx++) {
                    for (int sy = ry - 1; sy <= (ry + 1); sy++) {

                        if (((sx >= 0) && (sx <= REGIONS_X) && (sy >= 0) && (sy <= REGIONS_Y))) {
                            surroundingRegions.add(_worldRegions[sx][sy]);
                        }
                    }
                }
                L2WorldRegion[] regionArray = new L2WorldRegion[surroundingRegions.size()];
                regionArray = surroundingRegions.toArray(regionArray);
                _worldRegions[rx][ry].setSurroundingRegions(regionArray);
            }
        }

        LOGGER.info("World Region Grid set up: {} by {}", REGIONS_X, REGIONS_Y);
    }

    public void addObject(WorldObject object) {
        if (_allObjects.putIfAbsent(object.getObjectId(), object) != null) {
            LOGGER.warn("Object {}  already exists in the world. Stack Trace: {}", object, CommonUtil.getTraceString(Thread.currentThread().getStackTrace()));
        }

        if (object.isPlayer()) {
            PlayerCountManager.getInstance().incConnectedCount();

            final Player newPlayer = (Player) object;
            if (newPlayer.isTeleporting()) // TODO: Drop when we stop removing player from the world while teleporting.
            {
                return;
            }

            final Player existingPlayer = _allPlayers.putIfAbsent(object.getObjectId(), newPlayer);
            if (existingPlayer != null) {
                Disconnection.of(existingPlayer).defaultSequence(false);
                Disconnection.of(newPlayer).defaultSequence(false);
                LOGGER.warn(getClass().getSimpleName() + ": Duplicate character!? Disconnected both characters (" + newPlayer.getName() + ")");
            }
        }
    }

    /**
     * Removes an object from the world.<br>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>Delete item from inventory, transfer Item from inventory to warehouse</li>
     * <li>Crystallize item</li>
     * <li>Remove NPC/PC/Pet from the world</li>
     * </ul>
     *
     * @param object the object to remove
     */
    public void removeObject(WorldObject object) {
        _allObjects.remove(object.getObjectId());
        if (object.isPlayer()) {
            PlayerCountManager.getInstance().decConnectedCount();

            final Player player = (Player) object;
            if (player.isTeleporting()) // TODO: Drop when we stop removing player from the world while teleporting.
            {
                return;
            }
            _allPlayers.remove(object.getObjectId());
        }
    }

    /**
     * <B><U> Example of use</U>:</B>
     * <ul>
     * <li>Client packets : Action, AttackRequest, RequestJoinParty, RequestJoinPledge...</li>
     * </ul>
     *
     * @param objectId Identifier of the WorldObject
     * @return the WorldObject object that belongs to an ID or null if no object found.
     */
    public WorldObject findObject(int objectId) {
        return _allObjects.get(objectId);
    }

    public Collection<WorldObject> getVisibleObjects() {
        return _allObjects.values();
    }

    public Collection<Player> getPlayers() {
        return _allPlayers.values();
    }

    /**
     * <B>If you have access to player objectId use {@link #getPlayer(int playerObjId)}</B>
     *
     * @param name Name of the player to get Instance
     * @return the player instance corresponding to the given name.
     */
    public Player getPlayer(String name) {
        return getPlayer(CharNameTable.getInstance().getIdByName(name));
    }

    /**
     * @param objectId of the player to get Instance
     * @return the player instance corresponding to the given object ID.
     */
    public Player getPlayer(int objectId) {
        return _allPlayers.get(objectId);
    }

    /**
     * @param ownerId ID of the owner
     * @return the pet instance from the given ownerId.
     */
    public Pet getPet(int ownerId) {
        return _petsInstance.get(ownerId);
    }

    /**
     * Add the given pet instance from the given ownerId.
     *
     * @param ownerId ID of the owner
     * @param pet     Pet of the pet
     * @return
     */
    public Pet addPet(int ownerId, Pet pet) {
        return _petsInstance.put(ownerId, pet);
    }

    /**
     * Remove the given pet instance.
     *
     * @param ownerId ID of the owner
     */
    public void removePet(int ownerId) {
        _petsInstance.remove(ownerId);
    }

    /**
     * Add a WorldObject in the world. <B><U> Concept</U> :</B> WorldObject (including Player) are identified in <B>_visibleObjects</B> of his current L2WorldRegion and in <B>_knownObjects</B> of other surrounding L2Characters <BR>
     * Player are identified in <B>_allPlayers</B> of L2World, in <B>_allPlayers</B> of his current L2WorldRegion and in <B>_knownPlayer</B> of other surrounding L2Characters <B><U> Actions</U> :</B>
     * <li>Add the WorldObject object in _allPlayers* of L2World</li>
     * <li>Add the WorldObject object in _gmList** of GmListTable</li>
     * <li>Add object in _knownObjects and _knownPlayer* of all surrounding L2WorldRegion L2Characters</li><BR>
     * <li>If object is a Creature, add all surrounding WorldObject in its _knownObjects and all surrounding Player in its _knownPlayer</li><BR>
     * <I>* only if object is a Player</I><BR>
     * <I>** only if object is a GM Player</I> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object in _visibleObjects and _allPlayers* of L2WorldRegion (need synchronisation)</B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to _allObjects and _allPlayers* of L2World (need synchronisation)</B></FONT> <B><U> Example of use </U> :</B>
     * <li>Drop an Item</li>
     * <li>Spawn a Creature</li>
     * <li>Apply Death Penalty of a Player</li>
     *
     * @param object    L2object to add in the world
     * @param newRegion L2WorldRegion in wich the object will be add (not used)
     */
    public void addVisibleObject(WorldObject object, L2WorldRegion newRegion) {
        if (!newRegion.isActive()) {
            return;
        }

        forEachVisibleObject(object, WorldObject.class, wo -> beAwereOfObject(object, wo));
    }

    private void beAwereOfObject(WorldObject object, WorldObject wo) {
        describeObjectToOther(object, wo);

        describeObjectToOther(wo, object);

        if (wo.isNpc() && object.isCharacter()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((Npc) wo, (Creature) object, object.isSummon()), (Npc) wo);
        }

        if (object.isNpc() && wo.isCharacter()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((Npc) object, (Creature) wo, wo.isSummon()), (Npc) object);
        }
    }

    private void describeObjectToOther(WorldObject object, WorldObject wo) {
        if (object.isPlayer() && wo.isVisibleFor((Player) object)) {
            wo.sendInfo((Player) object);
            if (wo.isCharacter()) {
                final CreatureAI ai = ((Creature) wo).getAI();
                if (ai != null) {
                    ai.describeStateToPlayer((Player) object);
                    if (wo.isMonster()) {
                        if (ai.getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                            ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove a WorldObject from the world. <B><U> Concept</U> :</B> WorldObject (including Player) are identified in <B>_visibleObjects</B> of his current L2WorldRegion and in <B>_knownObjects</B> of other surrounding L2Characters <BR>
     * Player are identified in <B>_allPlayers</B> of L2World, in <B>_allPlayers</B> of his current L2WorldRegion and in <B>_knownPlayer</B> of other surrounding L2Characters <B><U> Actions</U> :</B>
     * <li>Remove the WorldObject object from _allPlayers* of L2World</li>
     * <li>Remove the WorldObject object from _visibleObjects and _allPlayers* of L2WorldRegion</li>
     * <li>Remove the WorldObject object from _gmList** of GmListTable</li>
     * <li>Remove object from _knownObjects and _knownPlayer* of all surrounding L2WorldRegion L2Characters</li><BR>
     * <li>If object is a Creature, remove all WorldObject from its _knownObjects and all Player from its _knownPlayer</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of L2World</B></FONT> <I>* only if object is a Player</I><BR>
     * <I>** only if object is a GM Player</I> <B><U> Example of use </U> :</B>
     * <li>Pickup an Item</li>
     * <li>Decay a Creature</li>
     *
     * @param object    L2object to remove from the world
     * @param oldRegion L2WorldRegion in which the object was before removing
     */
    public void removeVisibleObject(WorldObject object, L2WorldRegion oldRegion) {
        if (object == null) {
            return;
        }

        if (oldRegion != null) {
            oldRegion.removeVisibleObject(object);

            // Go through all surrounding L2WorldRegion L2Characters
            oldRegion.forEachSurroundingRegion(w ->
            {
                forgetObjectsInRegion(object, w);
                return true;
            });
        }
    }

    public void switchRegion(WorldObject object, L2WorldRegion newRegion) {
        final L2WorldRegion oldRegion = object.getWorldRegion();
        if ((oldRegion == null) || (oldRegion == newRegion)) {
            return;
        }

        oldRegion.forEachSurroundingRegion(w ->
        {
            if (!newRegion.isSurroundingRegion(w)) {
                forgetObjectsInRegion(object, w);
            }
            return true;
        });

        newRegion.forEachSurroundingRegion(w ->
        {
            if (!oldRegion.isSurroundingRegion(w)) {
                for (WorldObject wo : w.getVisibleObjects().values()) {
                    if ((wo == object) || (wo.getInstanceWorld() != object.getInstanceWorld())) {
                        continue;
                    }

                    beAwereOfObject(object, wo);
                }
            }
            return true;
        });
    }

    private void forgetObjectsInRegion(WorldObject object, L2WorldRegion w) {
        for (WorldObject wo : w.getVisibleObjects().values()) {
            if (wo == object) {
                continue;
            }

            forgetObject(object, wo);

            forgetObject(wo, object);
        }
    }

    private void forgetObject(WorldObject object, WorldObject wo) {
        if (object.isCharacter()) {
            final Creature objectCreature = (Creature) object;
            final CreatureAI ai = objectCreature.getAI();
            if (ai != null) {
                ai.notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, wo);
            }

            if (objectCreature.getTarget() == wo) {
                objectCreature.setTarget(null);
            }

            if (object.isPlayer()) {
                object.sendPacket(new DeleteObject(wo));
            }
        }
    }

    public WorldObject getVisibleObject(WorldObject reference, int objectId) {
        var currentRegion = getRegion(reference);
        if(isNull(currentRegion)) {
            return null;
        }
        WorldObject object = null;
        for(var region : currentRegion.getSurroundingRegions()) {
            if( nonNull(object = region.getVisibleObjects().get(objectId)) ) {
                return object;
            }
        }
        return object;
    }

    public <T extends WorldObject> List<T> getVisibleObjects(WorldObject object, Class<T> clazz) {
        final List<T> result = new ArrayList<>();
        forEachVisibleObject(object, clazz, result::add);
        return result;
    }

    public <T extends WorldObject> List<T> getVisibleObjects(WorldObject object, Class<T> clazz, Predicate<T> predicate) {
        final List<T> result = new ArrayList<>();
        forEachVisibleObject(object, clazz, o ->
        {
            if (predicate.test(o)) {
                result.add(o);
            }
        });
        return result;
    }

    public <T extends WorldObject> void forEachVisibleObject(WorldObject object, Class<T> clazz, Consumer<T> c) {
        if (object == null) {
            return;
        }

        final L2WorldRegion centerWorldRegion = getRegion(object);
        if (centerWorldRegion == null) {
            return;
        }

        for (L2WorldRegion region : centerWorldRegion.getSurroundingRegions()) {
            for (WorldObject visibleObject : region.getVisibleObjects().values()) {
                if ((visibleObject == null) || (visibleObject == object) || !clazz.isInstance(visibleObject)) {
                    continue;
                }

                if (visibleObject.getInstanceWorld() != object.getInstanceWorld()) {
                    continue;
                }

                c.accept(clazz.cast(visibleObject));
            }
        }
    }

    public <T extends WorldObject> List<T> getVisibleObjectsInRange(WorldObject object, Class<T> clazz, int range) {
        final List<T> result = new ArrayList<>();
        forEachVisibleObjectInRange(object, clazz, range, result::add);
        return result;
    }

    public <T extends WorldObject> List<T> getVisibleObjectsInRange(WorldObject object, Class<T> clazz, int range, Predicate<T> predicate) {
        final List<T> result = new ArrayList<>();
        forEachVisibleObjectInRange(object, clazz, range, o ->
        {
            if (predicate.test(o)) {
                result.add(o);
            }
        });
        return result;
    }

    public <T extends WorldObject> void forEachVisibleObjectInRange(WorldObject object, Class<T> clazz, int range, Consumer<T> c) {
        if (object == null) {
            return;
        }

        final L2WorldRegion centerWorldRegion = getRegion(object);
        if (centerWorldRegion == null) {
            return;
        }

        for (L2WorldRegion region : centerWorldRegion.getSurroundingRegions()) {
            for (WorldObject visibleObject : region.getVisibleObjects().values()) {
                if ((visibleObject == null) || (visibleObject == object) || !clazz.isInstance(visibleObject)) {
                    continue;
                }

                if (visibleObject.getInstanceWorld() != object.getInstanceWorld()) {
                    continue;
                }

                if (visibleObject.calculateDistance3D(object) <= range) {
                    c.accept(clazz.cast(visibleObject));
                }
            }
        }
    }

    /**
     * Calculate the current L2WorldRegions of the object according to its position (x,y). <B><U> Example of use </U> :</B>
     * <li>Set position of a new WorldObject (drop, spawn...)</li>
     * <li>Update position of a WorldObject after a movement</li><BR>
     *
     * @param object the object
     * @return
     */
    public L2WorldRegion getRegion(WorldObject object) {
        try {
            return _worldRegions[(object.getX() >> SHIFT_BY) + OFFSET_X][(object.getY() >> SHIFT_BY) + OFFSET_Y];
        } catch (ArrayIndexOutOfBoundsException e) // Precaution. Moved at invalid region?
        {
            disposeOutOfBoundsObject(object);
            return null;
        }
    }

    public L2WorldRegion getRegion(int x, int y) {
        try {
            return _worldRegions[(x >> SHIFT_BY) + OFFSET_X][(y >> SHIFT_BY) + OFFSET_Y];
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Incorrect world region X: " + ((x >> SHIFT_BY) + OFFSET_X) + " Y: " + ((y >> SHIFT_BY) + OFFSET_Y));
            return null;
        }
    }

    public synchronized void disposeOutOfBoundsObject(WorldObject object) {
        if (object.isPlayer()) {
            ((Creature) object).stopMove(((Player) object).getLastServerPosition());
        } else if (object.isSummon()) {
            final Summon summon = (Summon) object;
            summon.unSummon(summon.getOwner());
        } else if (_allObjects.remove(object.getObjectId()) != null) {
            if (object.isNpc()) {
                final Npc npc = (Npc) object;
                LOGGER.warn("Deleting npc " + object.getName() + " NPCID[" + npc.getId() + "] from invalid location X:" + object.getX() + " Y:" + object.getY() + " Z:" + object.getZ());
                npc.deleteMe();

                final Spawn spawn = npc.getSpawn();
                if (spawn != null) {
                    LOGGER.warn("Spawn location X:" + spawn.getX() + " Y:" + spawn.getY() + " Z:" + spawn.getZ() + " Heading:" + spawn.getHeading());
                }
            } else if (object.isCharacter()) {
                LOGGER.warn("Deleting object " + object.getName() + " OID[" + object.getObjectId() + "] from invalid location X:" + object.getX() + " Y:" + object.getY() + " Z:" + object.getZ());
                ((Creature) object).deleteMe();
            }

            if (object.getWorldRegion() != null) {
                object.getWorldRegion().removeVisibleObject(object);
            }
        }
    }

    public void incrementParty() {
        _partyNumber.incrementAndGet();
    }

    public void decrementParty() {
        _partyNumber.decrementAndGet();
    }

    public void incrementPartyMember() {
        _memberInPartyNumber.incrementAndGet();
    }

    public void decrementPartyMember() {
        _memberInPartyNumber.decrementAndGet();
    }

    public int getPartyCount() {
        return _partyNumber.get();
    }

    public int getPartyMemberCount() {
        return _memberInPartyNumber.get();
    }


    public static L2World getInstance() {
        return Singleton.INSTANCE;
    }


    private static class Singleton {
        private static final L2World INSTANCE = new L2World();
    }
}
