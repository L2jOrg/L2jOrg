package org.l2j.gameserver.model;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCreatureSee;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isAnyNull;
import static org.l2j.gameserver.util.GameUtils.*;

public final class World {

    private static final Logger LOGGER = LoggerFactory.getLogger(World.class);

    /**
     * Gracia border Flying objects not allowed to the east of it.
     */
    public static final int GRACIA_MAX_X = -166168;
    public static final int GRACIA_MAX_Z = 6105;
    public static final int GRACIA_MIN_Z = -895;
    /**
     * Bit shift, defines number of regions note, shifting by 15 will result in regions corresponding to map tiles shifting by 11 divides one tile to 16x16 regions.
     */
    private static final int SHIFT_BY = 11;
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
    private static final int OFFSET_X = Math.abs(MAP_MIN_X >> SHIFT_BY);
    private static final int OFFSET_Y = Math.abs(MAP_MIN_Y >> SHIFT_BY);
    /**
     * Number of regions.
     */
    private static final int REGIONS_X = (MAP_MAX_X >> SHIFT_BY) + OFFSET_X;
    private static final int REGIONS_Y = (MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y;
    /**
     * Map containing all the players in game.
     */
    private final IntMap<Player> players = new CHashIntMap<>();
    /**
     * Map containing all visible objects.
     */
    private final IntMap<WorldObject> objects = new CHashIntMap<>();
    /**
     * Map with the pets instances and their owner ID.
     */
    private final IntMap<Pet> pets = new CHashIntMap<>();

    private final AtomicInteger _partyNumber = new AtomicInteger();
    private final AtomicInteger _memberInPartyNumber = new AtomicInteger();

    private final WorldRegion[][] regions = new WorldRegion[REGIONS_X + 1][REGIONS_Y + 1];

    private World() {
        initRegions();
    }

    private void initRegions() {

        for (int x = 0; x <= REGIONS_X; x++) {
            for (int y = 0; y <= REGIONS_Y; y++) {

                if(isNull(regions[x][y])) {
                    regions[x][y] = new WorldRegion(x, y);
                }

                List<WorldRegion> surroundingRegions = initSurroundingRegions(x, y);
                regions[x][y].setSurroundingRegions(surroundingRegions.toArray(WorldRegion[]::new));
            }
        }

        LOGGER.info("World Region Grid set up: {} by {}", REGIONS_X, REGIONS_Y);
    }

    private List<WorldRegion> initSurroundingRegions(int x, int y) {
        List<WorldRegion> surroundingRegions = new ArrayList<>(9);

        for (int sx = x - 1; sx <= (x + 1); sx++) {
            for (int sy = y - 1; sy <= (y + 1); sy++) {

                if (((sx >= 0) && (sx <= REGIONS_X) && (sy >= 0) && (sy <= REGIONS_Y))) {
                    if(isNull(regions[sx][sy])) {
                        regions[sx][sy] = new WorldRegion(sx, sy);
                    }
                    surroundingRegions.add(regions[sx][sy]);
                }
            }
        }
        return surroundingRegions;
    }

    public void addObject(WorldObject object) {
        if (objects.putIfAbsent(object.getObjectId(), object) != null) {
            LOGGER.warn("Object {}  already exists in the world. Stack Trace: {}", object, CommonUtil.getTraceString(Thread.currentThread().getStackTrace()));
        }

        if (isPlayer(object)) {

            final Player player = (Player) object;

            // TODO: Drop when we stop removing player from the world while teleporting.
            if (player.isTeleporting()) {
                return;
            }

            onPlayerEnter(player);
        }
    }

    private void onPlayerEnter(Player player) {
        final Player existingPlayer = players.putIfAbsent(player.getObjectId(), player);

        if (nonNull(existingPlayer)) {
            Disconnection.of(existingPlayer).defaultSequence(false);
            Disconnection.of(player).defaultSequence(false);
            LOGGER.warn("Duplicate character!? Disconnected both characters {})", player);
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
        objects.remove(object.getObjectId());

        if (isPlayer(object)) {
            final Player player = (Player) object;
            if (player.isTeleporting()) // TODO: Drop when we stop removing player from the world while teleporting.
            {
                return;
            }

            players.remove(object.getObjectId());
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
        return objects.get(objectId);
    }

    public Collection<WorldObject> getVisibleObjects() {
        return objects.values();
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    /**
     * <B>If you have access to player objectId use {@link #findPlayer(int playerObjId)}</B>
     *
     * @param name Name of the player to get Instance
     * @return the player instance corresponding to the given name.
     */
    public Player findPlayer(String name) {
        return findPlayer(PlayerNameTable.getInstance().getIdByName(name));
    }

    /**
     * @param objectId of the player to get Instance
     * @return the player instance corresponding to the given object ID.
     */
    public Player findPlayer(int objectId) {
        return players.get(objectId);
    }

    /**
     * @param ownerId ID of the owner
     * @return the pet instance from the given ownerId.
     */
    public Pet findPet(int ownerId) {
        return pets.get(ownerId);
    }

    /**
     * Add the given pet instance from the given ownerId.
     *
     * @param ownerId ID of the owner
     * @param pet     Pet of the pet
     * @return existent Pet with ownerId
     */
    public Pet addPet(int ownerId, Pet pet) {
        return pets.put(ownerId, pet);
    }

    /**
     * Remove the given pet instance.
     *
     * @param ownerId ID of the owner
     */
    public void removePet(int ownerId) {
        pets.remove(ownerId);
    }

    /**
     * Add a WorldObject in the world. <B><U> Concept</U> :</B> WorldObject (including Player) are identified in <B>_visibleObjects</B> of his current WorldRegion and in <B>_knownObjects</B> of other surrounding L2Characters <BR>
     * Player are identified in <B>players</B> of World, in <B>players</B> of his current WorldRegion and in <B>_knownPlayer</B> of other surrounding L2Characters <B><U> Actions</U> :</B>
     * <li>Add the WorldObject object in players* of World</li>
     * <li>Add the WorldObject object in _gmList** of GmListTable</li>
     * <li>Add object in _knownObjects and _knownPlayer* of all surrounding WorldRegion L2Characters</li><BR>
     * <li>If object is a Creature, add all surrounding WorldObject in its _knownObjects and all surrounding Player in its _knownPlayer</li><BR>
     * <I>* only if object is a Player</I><BR>
     * <I>** only if object is a GM Player</I> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object in _visibleObjects and players* of WorldRegion (need synchronisation)</B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to objects and players* of World (need synchronisation)</B></FONT> <B><U> Example of use </U> :</B>
     * <li>Drop an Item</li>
     * <li>Spawn a Creature</li>
     * <li>Apply Death Penalty of a Player</li>
     *
     * @param object    L2object to add in the world
     * @param newRegion WorldRegion in wich the object will be add (not used)
     */
    public void addVisibleObject(WorldObject object, WorldRegion newRegion) {
        if (!newRegion.isActive()) {
            return;
        }

        forEachVisibleObject(object, WorldObject.class, wo -> beAwereOfEachOther(object, wo));
    }

    private void beAwereOfEachOther(WorldObject object, WorldObject wo) {
        describeObjectToOther(object, wo);

        describeObjectToOther(wo, object);

        if (isNpc(wo) && isCreature(object)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((Npc) wo, (Creature) object, object.isSummon()), (Npc) wo);
        }

        if (isNpc(object) && isCreature(wo)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((Npc) object, (Creature) wo, wo.isSummon()), (Npc) object);
        }
    }

    private void describeObjectToOther(WorldObject object, WorldObject wo) {
        if(!isPlayer(object) || wo.isVisibleFor((Player) object)) {
            return;
        }

        wo.sendInfo((Player) object);

        if (isCreature(wo)) {
            final CreatureAI ai = ((Creature) wo).getAI();

            if (nonNull(ai)) {
                ai.describeStateToPlayer((Player) object);

                if (isMonster(wo)) {
                    ai.setActiveIfIdle();
                }
            }
        }
    }

    /**
     * Remove a WorldObject from the world. <B><U> Concept</U> :</B> WorldObject (including Player) are identified in <B>_visibleObjects</B> of his current WorldRegion and in <B>_knownObjects</B> of other surrounding L2Characters <BR>
     * Player are identified in <B>players</B> of World, in <B>players</B> of his current WorldRegion and in <B>_knownPlayer</B> of other surrounding L2Characters <B><U> Actions</U> :</B>
     * <li>Remove the WorldObject object from players* of World</li>
     * <li>Remove the WorldObject object from _visibleObjects and players* of WorldRegion</li>
     * <li>Remove the WorldObject object from _gmList** of GmListTable</li>
     * <li>Remove object from _knownObjects and _knownPlayer* of all surrounding WorldRegion L2Characters</li><BR>
     * <li>If object is a Creature, remove all WorldObject from its _knownObjects and all Player from its _knownPlayer</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from objects of World</B></FONT> <I>* only if object is a Player</I><BR>
     * <I>** only if object is a GM Player</I> <B><U> Example of use </U> :</B>
     * <li>Pickup an Item</li>
     * <li>Decay a Creature</li>
     *
     * @param object    L2object to remove from the world
     * @param oldRegion WorldRegion in which the object was before removing
     */
    public void removeVisibleObject(WorldObject object, WorldRegion oldRegion) {
        if (isAnyNull(object, oldRegion)) {
            return;
        }

        oldRegion.removeVisibleObject(object);
        oldRegion.forEachObjectInSurrounding(Creature.class, other ->  forgetEachOther(object, other), other -> !object.equals(other));
    }

    void switchRegionIfNeed(WorldObject object) {
        var newRegion = getRegion(object);
        WorldRegion oldRegion;

        if (nonNull(newRegion) && !newRegion.equals(oldRegion = object.getWorldRegion())) {
            if (nonNull(oldRegion)) {
                oldRegion.removeVisibleObject(object);
            }
            switchRegion(object, oldRegion, newRegion);
            newRegion.addVisibleObject(object);
            object.setWorldRegion(newRegion);
        }
    }

    private void switchRegion(WorldObject object, WorldRegion oldRegion, WorldRegion newRegion) {

        if(nonNull(oldRegion)) {
            oldRegion.forEachSurroundingRegion(w -> {
                if (!newRegion.isSurroundingRegion(w)) {
                    w.forEachCreature(other -> this.forgetEachOther(object, other), other -> !object.equals(other));
                }
            });
        }

        newRegion.forEachSurroundingRegion(w -> {
            if (!w.isSurroundingRegion(oldRegion)) {
                w.forEachObject(WorldObject.class, other -> beAwereOfEachOther(object, other), other -> !object.equals(other) && Objects.equals(other.getInstanceWorld(), object.getInstanceWorld()));
            }
        });
    }

    private void forgetEachOther(WorldObject object, WorldObject other) {
        forgetObject(object, other);
        forgetObject(other, object);
    }

    private void forgetObject(WorldObject object, WorldObject wo) {
        if (isCreature(object)) {
            final Creature objectCreature = (Creature) object;
            final CreatureAI ai = objectCreature.getAI();
            if (nonNull(ai)) {
                ai.notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, wo);
            }
        }
    }

    public WorldObject getVisibleObject(WorldObject reference, int objectId) {
        var currentRegion = getRegion(reference);

        if(isNull(currentRegion)) {
            return null;
        }

        return currentRegion.getObjectInSurrounding(reference, objectId, getSettings(CharacterSettings.class).partyRange());
    }

    public boolean hasVisiblePlayer(WorldObject object) {
        var region = getRegion(object);

        if(isNull(region)) {
            return false;
        }

        return region.hasObjectInSurrounding(Player.class, isVisible(object, getSettings(CharacterSettings.class).partyRange()));
    }

    public <T extends WorldObject> void forEachVisibleObject(WorldObject object, Class<T> clazz, Consumer<T> action) {
        forEachVisibleObjectInRange(object, clazz, getSettings(CharacterSettings.class).partyRange(), action);
    }

    public <T extends WorldObject> void forEachVisibleObject(WorldObject object, Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        var region = getRegion(object);
        if(isNull(region)) {
            return;
        }
        region.forEachObjectInSurrounding(clazz, action, filter.and(isVisible(object, getSettings(CharacterSettings.class).partyRange())));
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

    public <T extends WorldObject> void forEachVisibleObjectInRange(WorldObject object, Class<T> clazz, int range, Consumer<T> action) {
        forEachVisibleObjectInRange(object, clazz, range, action, o -> true);
    }

    public <T extends WorldObject> void forEachVisibleObjectInRange(WorldObject object, Class<T> clazz, int range, Consumer<T> action, Predicate<T> filter) {
        var region = getRegion(object);
        if (isNull(region)) {
            return;
        }

        region.forEachObjectInSurrounding(clazz, action, filter.and(isVisible(object, range)));
    }

    private static <T extends WorldObject> Predicate<T> isVisible(WorldObject object, int range) {
        return visible -> nonNull(visible) && !visible.equals(object) &&
                Objects.equals(visible.getInstanceWorld(),  object.getInstanceWorld()) && MathUtil.isInsideRadius3D(object, visible, range);
    }


    public <T extends WorldObject> void forAnyVisibleObject(WorldObject object, Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        forAnyVisibleObjectInRange(object, clazz, getSettings(CharacterSettings.class).partyRange(), action, filter);
    }

    public <T extends WorldObject> void forAnyVisibleObjectInRange(WorldObject object, Class<T> clazz, int range, Consumer<T> action, Predicate<T> filter) {
        WorldRegion region = getRegion(object);

        if(isNull(region)) {
            return;
        }
        region.forAnyObjectInSurrounding(clazz, action, filter.and(isVisible(object, range)));
    }

    /**
     * Calculate the current L2WorldRegions of the object according to its position (x,y). <B><U> Example of use </U> :</B>
     * <li>Set position of a new WorldObject (drop, spawn...)</li>
     * <li>Update position of a WorldObject after a movement</li><BR>
     *
     * @param object the object
     * @return
     */
    public WorldRegion getRegion(WorldObject object) {
        if(isNull(object)) {
            return null;
        }
        try {
            return regions[(object.getX() >> SHIFT_BY) + OFFSET_X][(object.getY() >> SHIFT_BY) + OFFSET_Y];
        } catch (ArrayIndexOutOfBoundsException e) // Precaution. Moved at invalid region?
        {
            disposeOutOfBoundsObject(object);
            return null;
        }
    }

    public WorldRegion getRegion(int x, int y) {
        try {
            return regions[(x >> SHIFT_BY) + OFFSET_X][(y >> SHIFT_BY) + OFFSET_Y];
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
        } else if (objects.remove(object.getObjectId()) != null) {
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


    public static World getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final World INSTANCE = new World();
    }
}
