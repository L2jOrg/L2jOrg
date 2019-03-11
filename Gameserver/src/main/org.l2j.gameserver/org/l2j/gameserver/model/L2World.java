package org.l2j.gameserver.model;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.ai.L2CharacterAI;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.instancemanager.PlayerCountManager;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCreatureSee;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.DeleteObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public final class L2World {
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
    public static final int TILE_Y_MIN = 10;
    public static final int TILE_X_MAX = 28;
    public static final int TILE_Y_MAX = 26;
    public static final int TILE_ZERO_COORD_X = 20;
    public static final int TILE_ZERO_COORD_Y = 18;
    public static final int MAP_MIN_X = (TILE_X_MIN - TILE_ZERO_COORD_X) * TILE_SIZE;
    public static final int MAP_MIN_Y = (TILE_Y_MIN - TILE_ZERO_COORD_Y) * TILE_SIZE;
    public static final int MAP_MAX_X = ((TILE_X_MAX - TILE_ZERO_COORD_X) + 1) * TILE_SIZE;
    public static final int MAP_MAX_Y = ((TILE_Y_MAX - TILE_ZERO_COORD_Y) + 1) * TILE_SIZE;
    /**
     * Calculated offset used so top left region is 0,0
     */
    public static final int OFFSET_X = Math.abs(MAP_MIN_X >> SHIFT_BY);
    public static final int OFFSET_Y = Math.abs(MAP_MIN_Y >> SHIFT_BY);
    private static final Logger LOGGER = Logger.getLogger(L2World.class.getName());
    /**
     * Number of regions.
     */
    private static final int REGIONS_X = (MAP_MAX_X >> SHIFT_BY) + OFFSET_X;
    private static final int REGIONS_Y = (MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y;
    /**
     * Map containing all the Good players in game.
     */
    private static final Map<Integer, L2PcInstance> _allGoodPlayers = new ConcurrentHashMap<>();
    /**
     * Map containing all the Evil players in game.
     */
    private static final Map<Integer, L2PcInstance> _allEvilPlayers = new ConcurrentHashMap<>();
    /**
     * Map containing all the players in game.
     */
    private final Map<Integer, L2PcInstance> _allPlayers = new ConcurrentHashMap<>();
    /**
     * Map containing all visible objects.
     */
    private final Map<Integer, L2Object> _allObjects = new ConcurrentHashMap<>();
    /**
     * Map with the pets instances and their owner ID.
     */
    private final Map<Integer, L2PetInstance> _petsInstance = new ConcurrentHashMap<>();

    private final AtomicInteger _partyNumber = new AtomicInteger();
    private final AtomicInteger _memberInPartyNumber = new AtomicInteger();

    private final L2WorldRegion[][] _worldRegions = new L2WorldRegion[REGIONS_X + 1][REGIONS_Y + 1];

    /**
     * Constructor of L2World.
     */
    protected L2World() {
        // Initialize regions.
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

        LOGGER.info(getClass().getSimpleName() + ": (" + REGIONS_X + " by " + REGIONS_Y + ") World Region Grid set up.");
    }

    public static void addFactionPlayerToWorld(L2PcInstance player) {
        if (player.isGood()) {
            _allGoodPlayers.put(player.getObjectId(), player);
        } else if (player.isEvil()) {
            _allEvilPlayers.put(player.getObjectId(), player);
        }
    }

    /**
     * @return the current instance of L2World
     */
    public static L2World getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Adds an object to the world.<br>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>Withdraw an item from the warehouse, create an item</li>
     * <li>Spawn a L2Character (PC, NPC, Pet)</li>
     * </ul>
     *
     * @param object
     */
    public void addObject(L2Object object) {
        if (_allObjects.putIfAbsent(object.getObjectId(), object) != null) {
            LOGGER.warning(getClass().getSimpleName() + ": Object " + object + " already exists in the world. Stack Trace: " + CommonUtil.getTraceString(Thread.currentThread().getStackTrace()));
        }

        if (object.isPlayer()) {
            PlayerCountManager.getInstance().incConnectedCount();

            final L2PcInstance newPlayer = (L2PcInstance) object;
            if (newPlayer.isTeleporting()) // TODO: Drop when we stop removing player from the world while teleporting.
            {
                return;
            }

            final L2PcInstance existingPlayer = _allPlayers.putIfAbsent(object.getObjectId(), newPlayer);
            if (existingPlayer != null) {
                Disconnection.of(existingPlayer).defaultSequence(false);
                Disconnection.of(newPlayer).defaultSequence(false);
                LOGGER.warning(getClass().getSimpleName() + ": Duplicate character!? Disconnected both characters (" + newPlayer.getName() + ")");
            } else if (Config.FACTION_SYSTEM_ENABLED) {
                addFactionPlayerToWorld(newPlayer);
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
    public void removeObject(L2Object object) {
        _allObjects.remove(object.getObjectId());
        if (object.isPlayer()) {
            PlayerCountManager.getInstance().decConnectedCount();

            final L2PcInstance player = (L2PcInstance) object;
            if (player.isTeleporting()) // TODO: Drop when we stop removing player from the world while teleporting.
            {
                return;
            }
            _allPlayers.remove(object.getObjectId());

            if (Config.FACTION_SYSTEM_ENABLED) {
                if (player.isGood()) {
                    _allGoodPlayers.remove(player.getObjectId());
                } else if (player.isEvil()) {
                    _allEvilPlayers.remove(player.getObjectId());
                }
            }
        }
    }

    /**
     * <B><U> Example of use</U>:</B>
     * <ul>
     * <li>Client packets : Action, AttackRequest, RequestJoinParty, RequestJoinPledge...</li>
     * </ul>
     *
     * @param objectId Identifier of the L2Object
     * @return the L2Object object that belongs to an ID or null if no object found.
     */
    public L2Object findObject(int objectId) {
        return _allObjects.get(objectId);
    }

    public Collection<L2Object> getVisibleObjects() {
        return _allObjects.values();
    }

    /**
     * Get the count of all visible objects in world.
     *
     * @return count off all L2World objects
     */
    public int getVisibleObjectsCount() {
        return _allObjects.size();
    }

    public Collection<L2PcInstance> getPlayers() {
        return _allPlayers.values();
    }

    public Collection<L2PcInstance> getAllGoodPlayers() {
        return _allGoodPlayers.values();
    }

    public Collection<L2PcInstance> getAllEvilPlayers() {
        return _allEvilPlayers.values();
    }

    /**
     * <B>If you have access to player objectId use {@link #getPlayer(int playerObjId)}</B>
     *
     * @param name Name of the player to get Instance
     * @return the player instance corresponding to the given name.
     */
    public L2PcInstance getPlayer(String name) {
        return getPlayer(CharNameTable.getInstance().getIdByName(name));
    }

    /**
     * @param objectId of the player to get Instance
     * @return the player instance corresponding to the given object ID.
     */
    public L2PcInstance getPlayer(int objectId) {
        return _allPlayers.get(objectId);
    }

    /**
     * @param ownerId ID of the owner
     * @return the pet instance from the given ownerId.
     */
    public L2PetInstance getPet(int ownerId) {
        return _petsInstance.get(ownerId);
    }

    /**
     * Add the given pet instance from the given ownerId.
     *
     * @param ownerId ID of the owner
     * @param pet     L2PetInstance of the pet
     * @return
     */
    public L2PetInstance addPet(int ownerId, L2PetInstance pet) {
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
     * Add a L2Object in the world. <B><U> Concept</U> :</B> L2Object (including L2PcInstance) are identified in <B>_visibleObjects</B> of his current L2WorldRegion and in <B>_knownObjects</B> of other surrounding L2Characters <BR>
     * L2PcInstance are identified in <B>_allPlayers</B> of L2World, in <B>_allPlayers</B> of his current L2WorldRegion and in <B>_knownPlayer</B> of other surrounding L2Characters <B><U> Actions</U> :</B>
     * <li>Add the L2Object object in _allPlayers* of L2World</li>
     * <li>Add the L2Object object in _gmList** of GmListTable</li>
     * <li>Add object in _knownObjects and _knownPlayer* of all surrounding L2WorldRegion L2Characters</li><BR>
     * <li>If object is a L2Character, add all surrounding L2Object in its _knownObjects and all surrounding L2PcInstance in its _knownPlayer</li><BR>
     * <I>* only if object is a L2PcInstance</I><BR>
     * <I>** only if object is a GM L2PcInstance</I> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object in _visibleObjects and _allPlayers* of L2WorldRegion (need synchronisation)</B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to _allObjects and _allPlayers* of L2World (need synchronisation)</B></FONT> <B><U> Example of use </U> :</B>
     * <li>Drop an Item</li>
     * <li>Spawn a L2Character</li>
     * <li>Apply Death Penalty of a L2PcInstance</li>
     *
     * @param object    L2object to add in the world
     * @param newRegion L2WorldRegion in wich the object will be add (not used)
     */
    public void addVisibleObject(L2Object object, L2WorldRegion newRegion) {
        if (!newRegion.isActive()) {
            return;
        }

        forEachVisibleObject(object, L2Object.class, wo ->
        {
            if (object.isPlayer() && wo.isVisibleFor((L2PcInstance) object)) {
                wo.sendInfo((L2PcInstance) object);
                if (wo.isCharacter()) {
                    final L2CharacterAI ai = ((L2Character) wo).getAI();
                    if (ai != null) {
                        ai.describeStateToPlayer((L2PcInstance) object);
                        if (wo.isMonster()) {
                            if (ai.getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                                ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                            }
                        }
                    }
                }
            }

            if (wo.isPlayer() && object.isVisibleFor((L2PcInstance) wo)) {
                object.sendInfo((L2PcInstance) wo);
                if (object.isCharacter()) {
                    final L2CharacterAI ai = ((L2Character) object).getAI();
                    if (ai != null) {
                        ai.describeStateToPlayer((L2PcInstance) wo);
                        if (object.isMonster()) {
                            if (ai.getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                                ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                            }
                        }
                    }
                }
            }

            if (wo.isNpc() && object.isCharacter()) {
                EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((L2Npc) wo, (L2Character) object, object.isSummon()), (L2Npc) wo);
            }

            if (object.isNpc() && wo.isCharacter()) {
                EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((L2Npc) object, (L2Character) wo, wo.isSummon()), (L2Npc) object);
            }
        });
    }

    /**
     * Remove a L2Object from the world. <B><U> Concept</U> :</B> L2Object (including L2PcInstance) are identified in <B>_visibleObjects</B> of his current L2WorldRegion and in <B>_knownObjects</B> of other surrounding L2Characters <BR>
     * L2PcInstance are identified in <B>_allPlayers</B> of L2World, in <B>_allPlayers</B> of his current L2WorldRegion and in <B>_knownPlayer</B> of other surrounding L2Characters <B><U> Actions</U> :</B>
     * <li>Remove the L2Object object from _allPlayers* of L2World</li>
     * <li>Remove the L2Object object from _visibleObjects and _allPlayers* of L2WorldRegion</li>
     * <li>Remove the L2Object object from _gmList** of GmListTable</li>
     * <li>Remove object from _knownObjects and _knownPlayer* of all surrounding L2WorldRegion L2Characters</li><BR>
     * <li>If object is a L2Character, remove all L2Object from its _knownObjects and all L2PcInstance from its _knownPlayer</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of L2World</B></FONT> <I>* only if object is a L2PcInstance</I><BR>
     * <I>** only if object is a GM L2PcInstance</I> <B><U> Example of use </U> :</B>
     * <li>Pickup an Item</li>
     * <li>Decay a L2Character</li>
     *
     * @param object    L2object to remove from the world
     * @param oldRegion L2WorldRegion in which the object was before removing
     */
    public void removeVisibleObject(L2Object object, L2WorldRegion oldRegion) {
        if (object == null) {
            return;
        }

        if (oldRegion != null) {
            oldRegion.removeVisibleObject(object);

            // Go through all surrounding L2WorldRegion L2Characters
            oldRegion.forEachSurroundingRegion(w ->
            {
                for (L2Object wo : w.getVisibleObjects().values()) {
                    if (wo == object) {
                        continue;
                    }

                    if (object.isCharacter()) {
                        final L2Character objectCreature = (L2Character) object;
                        final L2CharacterAI ai = objectCreature.getAI();
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

                    if (wo.isCharacter()) {
                        final L2Character woCreature = (L2Character) wo;
                        final L2CharacterAI ai = woCreature.getAI();
                        if (ai != null) {
                            ai.notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
                        }

                        if (woCreature.getTarget() == object) {
                            woCreature.setTarget(null);
                        }

                        if (wo.isPlayer()) {
                            wo.sendPacket(new DeleteObject(object));
                        }
                    }
                }
                return true;
            });
        }
    }

    public void switchRegion(L2Object object, L2WorldRegion newRegion) {
        final L2WorldRegion oldRegion = object.getWorldRegion();
        if ((oldRegion == null) || (oldRegion == newRegion)) {
            return;
        }

        oldRegion.forEachSurroundingRegion(w ->
        {
            if (!newRegion.isSurroundingRegion(w)) {
                for (L2Object wo : w.getVisibleObjects().values()) {
                    if (wo == object) {
                        continue;
                    }

                    if (object.isCharacter()) {
                        final L2Character objectCreature = (L2Character) object;
                        final L2CharacterAI ai = objectCreature.getAI();
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

                    if (wo.isCharacter()) {
                        final L2Character woCreature = (L2Character) wo;
                        final L2CharacterAI ai = woCreature.getAI();
                        if (ai != null) {
                            ai.notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
                        }

                        if (woCreature.getTarget() == object) {
                            woCreature.setTarget(null);
                        }

                        if (wo.isPlayer()) {
                            wo.sendPacket(new DeleteObject(object));
                        }
                    }
                }
            }
            return true;
        });

        newRegion.forEachSurroundingRegion(w ->
        {
            if (!oldRegion.isSurroundingRegion(w)) {
                for (L2Object wo : w.getVisibleObjects().values()) {
                    if ((wo == object) || (wo.getInstanceWorld() != object.getInstanceWorld())) {
                        continue;
                    }

                    if (object.isPlayer() && wo.isVisibleFor((L2PcInstance) object)) {
                        wo.sendInfo((L2PcInstance) object);
                        if (wo.isCharacter()) {
                            final L2CharacterAI ai = ((L2Character) wo).getAI();
                            if (ai != null) {
                                ai.describeStateToPlayer((L2PcInstance) object);
                                if (wo.isMonster()) {
                                    if (ai.getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                                        ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                                    }
                                }
                            }
                        }
                    }

                    if (wo.isPlayer() && object.isVisibleFor((L2PcInstance) wo)) {
                        object.sendInfo((L2PcInstance) wo);
                        if (object.isCharacter()) {
                            final L2CharacterAI ai = ((L2Character) object).getAI();
                            if (ai != null) {
                                ai.describeStateToPlayer((L2PcInstance) wo);
                                if (object.isMonster()) {
                                    if (ai.getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                                        ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                                    }
                                }
                            }
                        }
                    }

                    if (wo.isNpc() && object.isCharacter()) {
                        EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((L2Npc) wo, (L2Character) object, object.isSummon()), (L2Npc) wo);
                    }

                    if (object.isNpc() && wo.isCharacter()) {
                        EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((L2Npc) object, (L2Character) wo, wo.isSummon()), (L2Npc) object);
                    }
                }
            }
            return true;
        });
    }

    public <T extends L2Object> List<T> getVisibleObjects(L2Object object, Class<T> clazz) {
        final List<T> result = new ArrayList<>();
        forEachVisibleObject(object, clazz, result::add);
        return result;
    }

    public <T extends L2Object> List<T> getVisibleObjects(L2Object object, Class<T> clazz, Predicate<T> predicate) {
        final List<T> result = new ArrayList<>();
        forEachVisibleObject(object, clazz, o ->
        {
            if (predicate.test(o)) {
                result.add(o);
            }
        });
        return result;
    }

    public <T extends L2Object> void forEachVisibleObject(L2Object object, Class<T> clazz, Consumer<T> c) {
        if (object == null) {
            return;
        }

        final L2WorldRegion centerWorldRegion = getRegion(object);
        if (centerWorldRegion == null) {
            return;
        }

        for (L2WorldRegion region : centerWorldRegion.getSurroundingRegions()) {
            for (L2Object visibleObject : region.getVisibleObjects().values()) {
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

    public <T extends L2Object> List<T> getVisibleObjectsInRange(L2Object object, Class<T> clazz, int range) {
        final List<T> result = new ArrayList<>();
        forEachVisibleObjectInRange(object, clazz, range, result::add);
        return result;
    }

    public <T extends L2Object> List<T> getVisibleObjectsInRange(L2Object object, Class<T> clazz, int range, Predicate<T> predicate) {
        final List<T> result = new ArrayList<>();
        forEachVisibleObjectInRange(object, clazz, range, o ->
        {
            if (predicate.test(o)) {
                result.add(o);
            }
        });
        return result;
    }

    public <T extends L2Object> void forEachVisibleObjectInRange(L2Object object, Class<T> clazz, int range, Consumer<T> c) {
        if (object == null) {
            return;
        }

        final L2WorldRegion centerWorldRegion = getRegion(object);
        if (centerWorldRegion == null) {
            return;
        }

        for (L2WorldRegion region : centerWorldRegion.getSurroundingRegions()) {
            for (L2Object visibleObject : region.getVisibleObjects().values()) {
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
     * <li>Set position of a new L2Object (drop, spawn...)</li>
     * <li>Update position of a L2Object after a movement</li><BR>
     *
     * @param object the object
     * @return
     */
    public L2WorldRegion getRegion(L2Object object) {
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
            LOGGER.warning(getClass().getSimpleName() + ": Incorrect world region X: " + ((x >> SHIFT_BY) + OFFSET_X) + " Y: " + ((y >> SHIFT_BY) + OFFSET_Y));
            return null;
        }
    }

    /**
     * Returns the whole 3d array containing the world regions used by ZoneData.java to setup zones inside the world regions
     *
     * @return
     */
    public L2WorldRegion[][] getWorldRegions() {
        return _worldRegions;
    }

    public synchronized void disposeOutOfBoundsObject(L2Object object) {
        if (object.isPlayer()) {
            ((L2Character) object).stopMove(((L2PcInstance) object).getLastServerPosition());
        } else if (object.isSummon()) {
            final L2Summon summon = (L2Summon) object;
            summon.unSummon(summon.getOwner());
        } else if (_allObjects.remove(object.getObjectId()) != null) {
            if (object.isNpc()) {
                final L2Npc npc = (L2Npc) object;
                LOGGER.warning("Deleting npc " + object.getName() + " NPCID[" + npc.getId() + "] from invalid location X:" + object.getX() + " Y:" + object.getY() + " Z:" + object.getZ());
                npc.deleteMe();

                final L2Spawn spawn = npc.getSpawn();
                if (spawn != null) {
                    LOGGER.warning("Spawn location X:" + spawn.getX() + " Y:" + spawn.getY() + " Z:" + spawn.getZ() + " Heading:" + spawn.getHeading());
                }
            } else if (object.isCharacter()) {
                LOGGER.warning("Deleting object " + object.getName() + " OID[" + object.getObjectId() + "] from invalid location X:" + object.getX() + " Y:" + object.getY() + " Z:" + object.getZ());
                ((L2Character) object).deleteMe();
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

    private static class SingletonHolder {
        protected static final L2World _instance = new L2World();
    }
}
