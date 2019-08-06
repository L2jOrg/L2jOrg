package org.l2j.gameserver.world.zone;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneExit;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Abstract base class for any zone type handles basic operations.
 *
 * @author durgus
 */
public abstract class Zone extends ListenersContainer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Zone.class.getName());

    private final int id;
    private List<ZoneArea> blockedZones;
    private boolean enabled;
    /**
     * Parameters to affect specific characters
     */
    private boolean checkAffected = false;
    private String name = null;
    private int minLvl;
    private int maxLvl;
    private int[] races;
    private int[] classes;
    private char classType;
    private InstanceType target = InstanceType.Creature; // default all chars
    private boolean allowStore;
    private AbstractZoneSettings settings;
    private int instanceTemplateId;
    private IntMap<Boolean> enabledInInstance;

    protected ZoneArea area;
    protected IntMap<Creature> creatures = new CHashIntMap<>();

    protected Zone(int id) {
        this.id = id;
        maxLvl = 0xFF;

        allowStore = true;
        enabled = true;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Setup new parameters for this zone
     *
     * @param name
     * @param value
     */
    public void setParameter(String name, String value) {
        checkAffected = true;

        // Zone name
        switch (name) {
            case "name":
                this.name = value;
                break;
            // Minimum level
            case "affectedLvlMin":
                minLvl = Integer.parseInt(value);
                break;
            // Maximum level
            case "affectedLvlMax":
                maxLvl = Integer.parseInt(value);
                break;
            // Affected Races
            case "affectedRace":
                // Create a new array holding the affected race
                if (races == null) {
                    races = new int[1];
                    races[0] = Integer.parseInt(value);
                } else {
                    final int[] temp = new int[races.length + 1];

                    int i = 0;
                    for (; i < races.length; i++) {
                        temp[i] = races[i];
                    }

                    temp[i] = Integer.parseInt(value);

                    races = temp;
                }
                break;
            // Affected classes
            case "affectedClassId":
                // Create a new array holding the affected classIds
                if (classes == null) {
                    classes = new int[1];
                    classes[0] = Integer.parseInt(value);
                } else {
                    final int[] temp = new int[classes.length + 1];

                    int i = 0;
                    for (; i < classes.length; i++) {
                        temp[i] = classes[i];
                    }

                    temp[i] = Integer.parseInt(value);

                    classes = temp;
                }
                break;
            // Affected class type
            case "affectedClassType":
                if (value.equals("Fighter")) {
                    classType = 1;
                } else {
                    classType = 2;
                }
                break;
            case "targetClass":
                target = Enum.valueOf(InstanceType.class, value);
                break;
            case "allowStore":
                allowStore = Boolean.parseBoolean(value);
                break;
            case "default_enabled":
                enabled = Boolean.parseBoolean(value);
                break;
            case "instanceId":
                instanceTemplateId = Integer.parseInt(value);
                break;
            default:
                LOGGER.info("Unknown parameter - {} in zone: {}", name, id);
                break;
        }
    }

    /**
     * @param character the player to verify.
     * @return {@code true} if the given character is affected by this zone, {@code false} otherwise.
     */
    private boolean isAffected(Creature character) {
        // Check instance
        final Instance world = character.getInstanceWorld();
        if (nonNull(world)) {
            if (world.getTemplateId() != instanceTemplateId) {
                return false;
            }
            if (!isEnabled(character.getInstanceId())) {
                return false;
            }
        } else if (instanceTemplateId > 0) {
            return false;
        }

        // Check lvl
        if ((character.getLevel() < minLvl) || (character.getLevel() > maxLvl)) {
            return false;
        }

        // check obj class
        if (!character.isInstanceTypes(target)) {
            return false;
        }

        if (isPlayer(character)) {
            // Check class type
            if (classType != 0) {
                if (((Player) character).isMageClass()) {
                    if (classType == 1) {
                        return false;
                    }
                } else if (classType == 2) {
                    return false;
                }
            }

            // Check race
            if (races != null) {
                boolean ok = false;

                for (int element : races) {
                    if (character.getRace().ordinal() == element) {
                        ok = true;
                        break;
                    }
                }

                if (!ok) {
                    return false;
                }
            }

            // Check class
            if (classes != null) {
                boolean ok = false;

                for (int _clas : classes) {
                    if (((Player) character).getClassId().ordinal() == _clas) {
                        ok = true;
                        break;
                    }
                }

                return ok;
            }
        }
        return true;
    }

    /**
     * Returns this zones zone form.
     *
     * @return {@link #area}
     */
    public ZoneArea getArea() {
        return area;
    }

    /**
     * Set the zone for this Zone Instance
     *
     * @param zone
     */
    public void setArea(ZoneArea zone) {
        if (area != null) {
            throw new IllegalStateException("Zone already set");
        }
        area = zone;
    }

    public List<ZoneArea> getBlockedZones() {
        return blockedZones;
    }

    public void setBlockedZones(List<ZoneArea> blockedZones) {
        if (this.blockedZones != null) {
            throw new IllegalStateException("Blocked zone already set");
        }
        this.blockedZones = blockedZones;
    }

    /**
     * Returns zone name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the zone name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks if the given coordinates are within the zone, ignores instanceId check
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isInsideZone(int x, int y, int z) {
        return area.isInsideZone(x, y, z) && !isInsideBannedZone(x, y, z);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return {@code true} if this location is within banned zone boundaries, {@code false} otherwise
     */
    public boolean isInsideBannedZone(int x, int y, int z) {
        return (blockedZones != null) && blockedZones.stream().allMatch(zone -> !zone.isInsideZone(x, y, z));
    }

    /**
     * Checks if the given coordinates are within zone's plane
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isInsideZone(int x, int y) {
        return isInsideZone(x, y, area.getHighZ());
    }

    /**
     * Checks if the given coordinates are within the zone, ignores instanceId check
     *
     * @param loc
     * @return
     */
    public boolean isInsideZone(ILocational loc) {
        return isInsideZone(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Checks if the given object is inside the zone.
     *
     * @param object
     * @return
     */
    public boolean isInsideZone(WorldObject object) {
        return isInsideZone(object.getX(), object.getY(), object.getZ());
    }

    public double getDistanceToZone(int x, int y) {
        return area.getDistanceToZone(x, y);
    }

    public double getDistanceToZone(WorldObject object) {
        return area.getDistanceToZone(object.getX(), object.getY());
    }

    public void revalidateInZone(Creature character) {
        // If the object is inside the zone...
        if (isInsideZone(character)) {
            // If the character can't be affected by this zone return
            if (checkAffected && !isAffected(character)) {
                return;
            }

            if (creatures.putIfAbsent(character.getObjectId(), character) == null) {
                // Notify to scripts.
                EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneEnter(character, this), this);
                // Notify Zone implementation.
                onEnter(character);
            }
        } else {
            removeCharacter(character);
        }
    }

    /**
     * Force fully removes a character from the zone Should use during teleport / logoff
     *
     * @param character
     */
    public void removeCharacter(Creature character) {
        // Was the character inside this zone?
        if (creatures.containsKey(character.getObjectId())) {
            // Notify to scripts.
            EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneExit(character, this), this);

            // Unregister player.
            creatures.remove(character.getObjectId());

            // Notify Zone implementation.
            onExit(character);
        }
    }

    /**
     * Will scan the zones char list for the character
     *
     * @param character
     * @return
     */
    public boolean isCreatureInZone(Creature character) {
        return creatures.containsKey(character.getObjectId());
    }

    public AbstractZoneSettings getSettings() {
        return settings;
    }

    public void setSettings(AbstractZoneSettings settings) {
        if (this.settings != null) {
            this.settings.clear();
        }
        this.settings = settings;
    }

    protected abstract void onEnter(Creature character);

    protected abstract void onExit(Creature character);

    public void onDieInside(Creature character) {
    }

    public void onReviveInside(Creature character) {
    }

    public void onPlayerLoginInside(Player player) {
    }

    public void onPlayerLogoutInside(Player player) {
    }

    public IntMap<Creature> getCharacters() {
        return creatures;
    }

    public Collection<Creature> getCharactersInside() {
        return creatures.values();
    }

    public List<Player> getPlayersInside() {
        return creatures.values().stream().filter(GameUtils::isPlayer).map(WorldObject::getActingPlayer).collect(Collectors.toList());
    }

    /**
     * Broadcasts packet to all players inside the zone
     *
     * @param packet
     */
    public void broadcastPacket(ServerPacket packet) {
        if (creatures.isEmpty()) {
            return;
        }

        creatures.values().parallelStream().filter(GameUtils::isPlayer).map(WorldObject::getActingPlayer).forEach(packet::sendTo);
    }

    public InstanceType getTargetType() {
        return target;
    }

    public void setTargetType(InstanceType type) {
        target = type;
        checkAffected = true;
    }

    protected boolean getAllowStore() {
        return allowStore;
    }

    public int getInstanceTemplateId() {
        return instanceTemplateId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]";
    }

    public void visualizeZone(int z) {
        area.visualizeZone(z);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean state) {
        enabled = state;
    }

    public void setEnabled(boolean state, int instanceId) {
        if (enabledInInstance == null) {
            synchronized (this) {
                if (enabledInInstance == null) {
                    enabledInInstance = new CHashIntMap<>();
                }
            }
        }

        enabledInInstance.put(instanceId, state);
    }

    public boolean isEnabled(int instanceId) {
        if (nonNull(enabledInInstance)) {
            return enabledInInstance.getOrDefault(instanceId, enabled);
        }

        return enabled;
    }

    public void oustAllPlayers() {
        if(creatures.isEmpty()) {
            return;
        }

        //@formatter:off
        creatures.values().parallelStream()
                .filter(GameUtils::isPlayer)
                .map(WorldObject::getActingPlayer)
                .filter(Player::isOnline)
                .forEach(player -> player.teleToLocation(TeleportWhereType.TOWN));
        //@formatter:off
    }

    public void movePlayersTo(Location loc) {
        if (creatures.isEmpty()) {
            return;
        }

        creatures.values().parallelStream().filter(p -> GameUtils.isPlayer(p) && ((Player)p).isOnline()).forEach(p -> p.teleToLocation(loc));
    }
}