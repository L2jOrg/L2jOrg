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
package org.l2j.gameserver.model.actor;

import io.github.joealisson.primitive.*;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.EmptyQueue;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.AttackableAI;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.data.xml.impl.TransformData;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.geo.SyncMode;
import org.l2j.gameserver.engine.geo.settings.GeoEngineSettings;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.*;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.TimersManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.DamageInfo.DamageType;
import org.l2j.gameserver.model.actor.instance.FriendlyNpc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.tasks.character.NotifyAITask;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.*;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.returns.DamageReturn;
import org.l2j.gameserver.model.events.returns.LocationReturn;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.IDeletable;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.interfaces.ISkillsHolder;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.model.skills.*;
import org.l2j.gameserver.model.stats.*;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldRegion;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneRegion;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.Math.max;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.*;
import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static org.l2j.gameserver.util.GameUtils.*;
import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * Mother class of all character objects of the world (PC, NPC...)<br>
 * Creature:<br>
 * <ul>
 * <li>Door</li>
 * <li>Playable</li>
 * <li>Npc</li>
 * <li>StaticWorldObject</li>
 * <li>L2Trap</li>
 * <li>Vehicle</li>
 * </ul>
 * <br>
 * <b>Concept of CreatureTemplate:</b><br>
 * Each Creature owns generic and static properties (ex : all Keltir have the same number of HP...).<br>
 * All of those properties are stored in a different template for each type of Creature.<br>
 * Each template is loaded once in the server cache memory (reduce memory use).<br>
 * When a new instance of Creature is spawned, server just create a link between the instance and the template.<br>
 * This link is stored in {@link #template}
 */
public abstract class Creature extends WorldObject implements ISkillsHolder, IDeletable {

    public static final double MAX_STATUS_BAR_PX = 352.0;

    protected Map<SkillCastingType, SkillCaster> skillCasters = new ConcurrentHashMap<>();
    protected MoveData move;

    protected byte zoneValidateCounter = 4;
    protected long exceptions;
    protected boolean running;
    protected boolean isTeleporting;
    protected boolean showSummonAnimation;

    private final StampedLock attackLock = new StampedLock();
    private final EffectList effectList = new EffectList(this);
    private final IntIntMap knownRelations = new CHashIntIntMap();
    private final IntMap<Skill> skills = new CHashIntMap<>();
    private final LongMap<Long> disabledSkills = new CHashLongMap<>();
    private final AtomicInteger abnormalShieldBlocks = new AtomicInteger();
    private final IntMap<TimeStamp> reuseTimeStampsItems = new CHashIntMap<>();
    private final LongMap<TimeStamp> reuseTimeStampsSkills = new CHashLongMap<>();
    private final IntMap<AtomicInteger> blockActionsAllowedSkills = new CHashIntMap<>();
    private final Map<ShotType, Double> chargedShots = new EnumMap<>(ShotType.class);
    private final Map<StatusUpdateType, Integer> statusUpdates = new ConcurrentHashMap<>();
    private final Map<BasicProperty, BasicPropertyResist> basicPropertyResists = new EnumMap<>(BasicProperty.class);

    private final byte[] zones = new byte[ZoneType.getZoneCount()];

    private volatile long attackEndTime;
    private volatile long disableRangedAttackEndTime;

    private Team team = Team.NONE;
    private String title;
    private Creature summoner;
    private Transform transform;
    private CreatureAI ai;
    private WorldObject target;
    private IntMap<Npc> summonedNpcs;
    private CreatureStats stats;
    private CreatureStatus status;
    private BuffFinishTask buffFinishTask;
    private CreatureTemplate template;
    private SkillChannelizer channelizer;
    private SkillChannelized channelized;
    private ScheduledFuture<?> hitTask;
    private IntMap<OptionsSkillHolder> triggerSkills;

    private int reputation = 0;
    private double hpUpdateIncCheck;
    private double hpUpdateDecCheck;
    private double hpUpdateInterval;
    private boolean lethalable = true;
    private boolean allSkillsDisabled;
    private boolean isDead;
    private boolean isImmobilized;
    private boolean isOverloaded;
    private boolean isPendingRevive;
    private boolean isInvul;
    private boolean isUndying;
    private boolean isFlying;
    private boolean blockActions;
    private boolean cursorKeyMovement;
    private boolean cursorKeyMovementActive;
    private boolean aiDisabled;

    /**
     * Creates a creature.
     *
     * @param template the creature template
     */
    public Creature(CreatureTemplate template) {
        this(IdFactory.getInstance().getNextId(), template);
    }

    /**
     * Constructor of Creature.<br>
     * <B><U>Concept</U>:</B><br>
     * Each Creature owns generic and static properties (ex : all Keltir have the same number of HP...).<br>
     * All of those properties are stored in a different template for each type of Creature. Each template is loaded once in the server cache memory (reduce memory use).<br>
     * When a new instance of Creature is spawned, server just create a link between the instance and the template This link is stored in <B>_template</B><br>
     * <B><U> Actions</U>:</B>
     * <ul>
     * <li>Set the _template of the Creature</li>
     * <li>Set _overloaded to false (the character can take more items)</li>
     * <li>If Creature is a L2NPCInstance, copy skills from template to object</li>
     * <li>If Creature is a L2NPCInstance, link _calculators to NPC_STD_CALCULATOR</li>
     * <li>If Creature is NOT a L2NPCInstance, create an empty _skills slot</li>
     * <li>If Creature is a Player or Summon, copy basic Calculator set to object</li>
     * </ul>
     *
     * @param objectId Identifier of the object to initialized
     * @param template The CreatureTemplate to apply to the object
     */
    public Creature(int objectId, CreatureTemplate template) {
        super(objectId);
        this.template = Objects.requireNonNull(template);
        setInstanceType(InstanceType.Creature);

        initCharStat();
        initCharStatus();
        setIsInvul(true);
    }

    public final EffectList getEffectList() {
        return effectList;
    }

    /**
     * @return character inventory, default null, overridden in Playable types and in L2NPcInstance
     */
    public Inventory getInventory() {
        return null;
    }

    public boolean destroyItemByItemId(String process, int itemId, long count, WorldObject reference, boolean sendMessage) {
        // Default: NPCs consume virtual items for their skills
        // TODO: should be logged if even happens.. should be false
        return true;
    }

    public boolean destroyItem(String process, int objectId, long count, WorldObject reference, boolean sendMessage) {
        // Default: NPCs consume virtual items for their skills
        // TODO: should be logged if even happens.. should be false
        return true;
    }

    /**
     * Check if the character is in the given zone Id.
     *
     * @param zone the zone Id to check
     * @return {code true} if the character is in that zone
     */
    @Override
    public final boolean isInsideZone(ZoneType zone) {
        return switch (zone) {
            case PVP -> isInPvPInstance() || zones[ZoneType.PVP.ordinal()] > 0;
            case PEACE -> !isInPvPInstance() && zones[ZoneType.PEACE.ordinal()] > 0;
            default -> zones[zone.ordinal()] > 0;
        };
    }

    private boolean isInPvPInstance() {
        return falseIfNullOrElse(getInstanceWorld(), Instance::isPvP);
    }

    public final void setInsideZone(ZoneType zone, boolean state) {
        synchronized (zones) {
            if (state) {
                zones[zone.ordinal()]++;
            } else if (zones[zone.ordinal()] > 0) {
                zones[zone.ordinal()]--;
            }
        }
    }

    /**
     * @return {@code true} if this creature is transformed including stance transformation {@code false} otherwise.
     */
    public boolean isTransformed() {
        return nonNull(transform);
    }

    /**
     * @param filter any conditions to be checked for the transformation, {@code null} otherwise.
     * @return {@code true} if this creature is transformed under the given filter conditions, {@code false} otherwise.
     */
    public boolean checkTransformed(Predicate<Transform> filter) {
        return nonNull(transform) && filter.test(transform);
    }

    /**
     * Tries to transform this creature with the specified template id.
     *
     * @param id        the id of the transformation template
     * @param addSkills {@code true} if skills of this transformation template should be added, {@code false} otherwise.
     * @return {@code true} if template is found and transformation is done, {@code false} otherwise.
     */
    public boolean transform(int id, boolean addSkills) {
        final var transformation = TransformData.getInstance().getTransform(id);
        if (nonNull(transformation)) {
            transform(transformation, addSkills);
            return true;
        }

        return false;
    }

    public void transform(Transform transformation, boolean addSkills) {
        if (!Config.ALLOW_MOUNTS_DURING_SIEGE && transformation.isRiding() && isInsideZone(ZoneType.SIEGE)) {
            return;
        }

        transform = transformation;
        transformation.onTransform(this, addSkills);
    }

    public void untransform() {
        if(nonNull(transform)) {
            transform.onUntransform(this);
            transform = null;
        }
    }

    public Optional<Transform> getTransformation() {
        return Optional.ofNullable(transform);
    }

    /**
     * This returns the transformation Id of the current transformation. For example, if a player is transformed as a Buffalo, and then picks up the Zariche, the transform Id returned will be that of the Zariche, and NOT the Buffalo.
     *
     * @return Transformation Id
     */
    public int getTransformationId() {
        return nonNull(transform)  ? transform.getId() : 0;
    }

    public int getTransformationDisplayId() {
        return nonNull(transform) && !transform.isStance() ? transform.getDisplayId() : 0;
    }

    public double getCollisionRadius() {
        final double defaultCollisionRadius = template.getCollisionRadius();
        return nonNull(transform) ? transform.getCollisionRadius(this, defaultCollisionRadius) : defaultCollisionRadius;
    }

    public double getCollisionHeight() {
        final double defaultCollisionHeight = template.getCollisionHeight();
        return nonNull(transform) ? transform.getCollisionHeight(this, defaultCollisionHeight) : defaultCollisionHeight;
    }

    /**
     * TODO pull up to Player
     *
     * This will return true if the player is GM,<br>
     * but if the player is not GM it will return false.
     *
     * @return GM status
     */
    public boolean isGM() {
        return false;
    }

    /**
     * Overridden in Player.
     *
     * @return the access level.
     */
    public AccessLevel getAccessLevel() {
        return null;
    }

    protected void initCharStatusUpdateValues() {
        hpUpdateIncCheck = stats.getMaxHp();
        hpUpdateInterval = hpUpdateIncCheck / MAX_STATUS_BAR_PX;
        hpUpdateDecCheck = hpUpdateIncCheck - hpUpdateInterval;
    }

    /**
     * Remove the Creature from the world when the decay task is launched.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of World </B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT>
     */
    public void onDecay() {
        decayMe();
        final ZoneRegion region = ZoneManager.getInstance().getRegion(this);
        if (region != null) {
            region.removeFromZones(this);
        }

        // Removes itself from the summoned list.
        if ((summoner != null)) {
            summoner.removeSummonedNpc(getObjectId());
        }
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        revalidateZone(true);
    }

    public synchronized void onTeleported() {
        if (!isTeleporting) {
            return;
        }
        setSpawned(true);
        World.getInstance().switchRegionIfNeed(this);
        setIsTeleporting(false);
        EventDispatcher.getInstance().notifyEventAsync(new OnCreatureTeleported(this), this);
    }

    /**
     * Add Creature instance that is attacking to the attacker list.
     *
     * @param player The Creature that attacks this one
     */
    public void addAttackerToAttackByList(Creature player) {
        // DS: moved to Attackable
    }

    /**
     * Send a packet to Players around
     */
    public void broadcastPacket(ServerPacket packet) {
        checkBroadcast(packet);
        World.getInstance().forEachVisibleObject(this, Player.class, packet::sendTo, this::isVisibleFor);
    }

    /**
     * Send a packet to Players around in the radius
     */
    public void broadcastPacket(ServerPacket packet, int radius) {
        checkBroadcast(packet);
        World.getInstance().forEachPlayerInRange(this, radius, packet::sendTo, this::isVisibleFor);
    }

    private void checkBroadcast(ServerPacket packet) {
        if(World.getInstance().getPlayersCountInSurroundRegions(this) > 100) { // need to profile to find out the best amount
            packet.sendInBroadcast(true);
        }
    }

    /**
     * @return true if hp update should be done, false if not
     */
    protected boolean needHpUpdate() {
        final double currentHp = status.getCurrentHp();
        final double maxHp = stats.getMaxHp();

        if ((currentHp <= 1.0) || (maxHp < MAX_STATUS_BAR_PX)) {
            return true;
        }

        if ((currentHp < hpUpdateDecCheck) || (Math.abs(currentHp - hpUpdateDecCheck) <= 1e-6) || (currentHp > hpUpdateIncCheck) || (Math.abs(currentHp - hpUpdateIncCheck) <= 1e-6)) {
            if (Math.abs(currentHp - maxHp) <= 1e-6) {
                hpUpdateIncCheck = currentHp + 1;
                hpUpdateDecCheck = currentHp - hpUpdateInterval;
            } else {
                final double doubleMulti = currentHp / hpUpdateInterval;
                int intMulti = (int) doubleMulti;

                hpUpdateDecCheck = hpUpdateInterval * (doubleMulti < intMulti ? --intMulti : intMulti);
                hpUpdateIncCheck = hpUpdateDecCheck + hpUpdateInterval;
            }

            return true;
        }

        return false;
    }

    public final void broadcastStatusUpdate() {
        broadcastStatusUpdate(null);
    }

    /**
     * Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Create the Server->Client packet StatusUpdate with current HP and MP</li>
     * <li>Send the Server->Client packet StatusUpdate with current HP and MP to all Creature called _statusListener that must be informed of HP/MP updates of this Creature</li>
     * </ul>
     * <FONT COLOR=#FF0000><B><U>Caution</U>: This method DOESN'T SEND CP information</B></FONT>
     *
     * @param caster TODO
     */
    public void broadcastStatusUpdate(Creature caster) {
        final StatusUpdate su = new StatusUpdate(this);
        if (caster != null) {
            su.addCaster(caster);
        }

        su.addUpdate(StatusUpdateType.MAX_HP, stats.getMaxHp());
        su.addUpdate(StatusUpdateType.CUR_HP, (int) status.getCurrentHp());

        computeStatusUpdate(su, StatusUpdateType.MAX_MP);
        computeStatusUpdate(su, StatusUpdateType.CUR_MP);

        broadcastPacket(su);
    }

    public void sendMessage(String text) {
        // default implementation
    }
    
    private void teleToLocation(int x, int y, int z, int heading, Instance instance) {
        final LocationReturn term = EventDispatcher.getInstance().notifyEvent(new OnCreatureTeleport(this, x, y, z, heading, instance), this, LocationReturn.class);
        if (term != null) {
            if (term.terminate()) {
                return;
            } else if (term.overrideLocation()) {
                x = term.getX();
                y = term.getY();
                z = term.getZ();
                heading = term.getHeading();
                instance = term.getInstance();
            }
        }

        // Prepare creature for teleport
        if (isPendingRevive) {
            doRevive();
        }

        // Abort any client actions, casting and remove target.
        sendPacket(ActionFailed.of(SkillCastingType.NORMAL));
        sendPacket(ActionFailed.of(SkillCastingType.NORMAL_SECOND));
        if (isMoving()) {
            stopMove(null);
        }
        abortCast();
        setTarget(null);

        setIsTeleporting(true);

        // Adjust position a bit
        z += 5;

        // Send teleport packet to player and visible players
        broadcastPacket(new TeleportToLocation(this, x, y, z, heading));

        World.getInstance().removeVisibleObject(this, this.getWorldRegion());
        setWorldRegion(null);

        getAI().setIntention(AI_INTENTION_ACTIVE);

        // Change instance world
        if (getInstanceWorld() != instance) {
            setInstance(instance);
        }

        // temporary fix for heading on teleport
        if (heading != 0) {
            setHeading(heading);
        }

        // Set the x,y,z position of the WorldObject and if necessary modify its _worldRegion
        setXYZInvisible(x, y, z);

        // Send teleport finished packet to player
        sendPacket(new ExTeleportToLocationActivate(this));

        if (!isPlayer(this)) {
            onTeleported();
            revalidateZone(true);
        }
    }

    public void teleToLocation(int x, int y, int z) {
        teleToLocation(x, y, z, 0, getInstanceWorld());
    }

    public void teleToLocation(int x, int y, int z, Instance instance) {
        teleToLocation(x, y, z, 0, instance);
    }

    public void teleToLocation(int x, int y, int z, int heading) {
        teleToLocation(x, y, z, heading, getInstanceWorld());
    }

    public void teleToLocation(int x, int y, int z, int heading, boolean randomOffset) {
        teleToLocation(x, y, z, heading, (randomOffset) ? Config.MAX_OFFSET_ON_TELEPORT : 0, getInstanceWorld());
    }

    public void teleToLocation(int x, int y, int z, int heading, boolean randomOffset, Instance instance) {
        teleToLocation(x, y, z, heading, (randomOffset) ? Config.MAX_OFFSET_ON_TELEPORT : 0, instance);
    }

    public void teleToLocation(int x, int y, int z, int heading, int randomOffset) {
        teleToLocation(x, y, z, heading, randomOffset, getInstanceWorld());
    }

    public void teleToLocation(int x, int y, int z, int heading, int randomOffset, Instance instance) {
        if (Config.OFFSET_ON_TELEPORT_ENABLED && (randomOffset > 0)) {
            x += Rnd.get(-randomOffset, randomOffset);
            y += Rnd.get(-randomOffset, randomOffset);
        }
        teleToLocation(x, y, z, heading, instance);
    }

    public void teleToLocation(ILocational loc) {
        teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading());
    }

    public void teleToLocation(ILocational loc, Instance instance) {
        teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), instance);
    }

    public void teleToLocation(ILocational loc, int randomOffset) {
        teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset);
    }

    public void teleToLocation(ILocational loc, int randomOffset, Instance instance) {
        teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset, instance);
    }

    public void teleToLocation(ILocational loc, boolean randomOffset) {
        teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), (randomOffset) ? Config.MAX_OFFSET_ON_TELEPORT : 0);
    }

    public void teleToLocation(ILocational loc, boolean randomOffset, Instance instance) {
        teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset, instance);
    }

    public void teleToLocation(TeleportWhereType teleportWhere) {
        teleToLocation(teleportWhere, getInstanceWorld());
    }

    public void teleToLocation(TeleportWhereType teleportWhere, Instance instance) {
        teleToLocation(MapRegionManager.getInstance().getTeleToLocation(this, teleportWhere), true, instance);
    }

    public void doAutoAttack(Creature target) {
        final long stamp = attackLock.tryWriteLock();
        if (stamp == 0) {
            return;
        }
        try {
            if (isNull(target) || (isAttackingDisabled() && !GameUtils.isSummon(this)) || !target.isTargetable()) {
                return;
            }

            if (!isAlikeDead()) {
                if ((GameUtils.isNpc(this) && target.isAlikeDead()) || !isInSurroundingRegion(target) || target.isDead()) {
                    getAI().setIntention(AI_INTENTION_ACTIVE);
                    sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }

                if (checkTransformed(transformation -> !transformation.canAttack())) {
                    sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }

            var player = getActingPlayer();
            if (nonNull(player)) {
                if (player.isInObserverMode()) {
                    sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
                    sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                } else if (player.isSiegeFriend(target)) {
                    sendPacket(SystemMessageId.FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE);
                    sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                // Checking if target has moved to peace zone
                else if (target.isInsidePeaceZone(player)) {
                    getAI().setIntention(AI_INTENTION_ACTIVE);
                    sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            } else if (isInsidePeaceZone(this, target)) {
                getAI().setIntention(AI_INTENTION_ACTIVE);
                sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            stopEffectsOnAction();

            // GeoData Los Check here (or dz > 1000)
            if (!GeoEngine.getInstance().canSeeTarget(this, target)) {
                getAI().setIntention(AI_INTENTION_ACTIVE);
                sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
                sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            // BOW and CROSSBOW checks
            final Weapon weaponItem = getActiveWeaponItem();
            if (nonNull(weaponItem)) {
                if (!weaponItem.isAttackWeapon()) {
                    if (weaponItem.getItemType() == WeaponType.FISHING_ROD) {
                        sendPacket(SystemMessageId.YOU_LOOK_ODDLY_AT_THE_FISHING_POLE_IN_DISBELIEF_AND_REALIZE_THAT_YOU_CAN_T_ATTACK_ANYTHING_WITH_THIS);
                    } else {
                        sendPacket(SystemMessageId.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
                    }
                    sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }

                // Ranged weapon checks.
                if (weaponItem.getItemType().isRanged() && !checkRangedAttackCondition(weaponItem, target)) {
                    sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }

            // Mobius: Do not move when attack is launched.
            if (isMoving()) {
                stopMove(getLocation());
            }

            final WeaponType weaponType = getAttackType();
            final boolean isTwoHanded = (weaponItem != null) && (weaponItem.getBodyPart() == BodyPart.TWO_HAND);
            final int timeAtk = Formulas.calculateTimeBetweenAttacks(stats.getPAtkSpd());
            final int timeToHit = Formulas.calculateTimeToHit(timeAtk, weaponType, isTwoHanded, false);
            attackEndTime = System.nanoTime() + (TimeUnit.MILLISECONDS.toNanos(timeAtk));

            setHeading(calculateHeadingFrom(this, target));

            // Get the Attack Reuse Delay of the Weapon
            final Attack attack = generateAttackTargetData(target, weaponItem, weaponType);
            boolean crossbow = false;
            switch (weaponType) {
                case CROSSBOW:
                case TWO_HAND_CROSSBOW:
                    crossbow = true;
                case BOW: {
                    final int reuse = Formulas.calculateReuseTime(this, weaponItem);
                    onStartRangedAttack(crossbow, reuse);
                    hitTask = ThreadPool.schedule(() -> onHitTimeNotDual(weaponItem, attack, timeToHit, timeAtk), timeToHit);
                    break;
                }
                case DUAL:
                case FIST:
                case DUAL_BLUNT:
                case DUAL_DAGGER: {
                    final int timeToHit2 = Formulas.calculateTimeToHit(timeAtk, weaponType, isTwoHanded, true) - timeToHit;
                    hitTask = ThreadPool.schedule(() -> onFirstHitTimeForDual(weaponItem, attack, timeToHit, timeAtk, timeToHit2), timeToHit);
                    break;
                }
                default: {
                    hitTask = ThreadPool.schedule(() -> onHitTimeNotDual(weaponItem, attack, timeToHit, timeAtk), timeToHit);
                    break;
                }
            }

            // If the Server->Client packet Attack contains at least 1 hit, send the Server->Client packet Attack
            // to the Creature AND to all Player in the _KnownPlayers of the Creature
            if (attack.hasHits()) {
                broadcastPacket(attack);
            }

            // Flag the attacker if it's a Player outside a PvP area
            if (player != null) {
                AttackStanceTaskManager.getInstance().addAttackStanceTask(player);
                player.updatePvPStatus(target);
            }
        } finally {
            attackLock.unlockWrite(stamp);
        }
    }

    protected void onStartRangedAttack(boolean crossbow, int reuse) {
        disableRangedAttackEndTime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(reuse);
    }

    protected boolean checkRangedAttackCondition(Weapon weaponItem, Creature target) {
        return disableRangedAttackEndTime <= System.nanoTime();
    }

    private Attack generateAttackTargetData(Creature target, Weapon weapon, WeaponType weaponType) {
        final boolean isDual = (WeaponType.DUAL == weaponType) || (WeaponType.DUAL_BLUNT == weaponType) || (WeaponType.DUAL_DAGGER == weaponType) || (WeaponType.FIST == weaponType);
        final Attack attack = new Attack(this, target);
        boolean shotConsumed = false;

        // Calculate the main target hit.
        Hit hit = generateHit(target, weapon, shotConsumed, isDual);
        attack.addHit(hit);
        shotConsumed = hit.isShotUsed();

        // Second hit for the dual attack.
        if (isDual) {
            hit = generateHit(target, weapon, shotConsumed, isDual);
            attack.addHit(hit);
        }

        // H5 Changes: without Polearm Mastery (skill 216) max simultaneous attacks is 3 (1 by default + 2 in skill 3599).
        int attackCountMax = (int) stats.getValue(Stat.ATTACK_COUNT_MAX, 1);
        if ((attackCountMax > 1) && !(stats.getValue(Stat.PHYSICAL_POLEARM_TARGET_SINGLE, 0) > 0)) {
            final double headingAngle = convertHeadingToDegree(getHeading());
            final int maxRadius = stats.getPhysicalAttackRadius();
            final int physicalAttackAngle = stats.getPhysicalAttackAngle();

            World.getInstance().forVisibleObjectsInRange(this, Creature.class, maxRadius, attackCountMax, creature -> canBeAttacked(target, headingAngle, physicalAttackAngle, creature),
                    creature -> attack.addHit(generateHit(creature, weapon, attack.isShotUsed(), false)));
        }


        return attack;
    }

    private boolean canBeAttacked(Creature target, double headingAngle, int physicalAttackAngle, Creature creature) {
        return !creature.equals(target) && !creature.isAlikeDead() && creature.isAutoAttackable(this) && Math.abs(calculateDirectionTo(creature) -headingAngle) <= physicalAttackAngle;
    }

    private Hit generateHit(Creature target, Weapon weapon, boolean shotConsumed, boolean halfDamage) {
        int damage = 0;
        byte shld = 0;
        boolean crit = false;
        boolean miss = Formulas.calcHitMiss(this, target);

        if (!shotConsumed) {
            shotConsumed = !miss && isChargedShot(ShotType.SOULSHOTS);
        }

        // Check if hit isn't missed
        if (!miss) {
            shld = Formulas.calcShldUse(this, target);
            crit = Formulas.calcCrit(stats.getCriticalHit(), this, target, null);
            damage = (int) Formulas.calcAutoAttackDamage(this, target, shld, crit, shotConsumed);
            if (halfDamage) {
                damage /= 2;
            }
        }

        return new Hit(target, damage, miss, crit, shld, shotConsumed ? nonNull(weapon) ? weapon.getItemGrade().ordinal() : 0 : -1);
    }

    public void doCast(Skill skill) {
        doCast(skill, null, false, false, SkillCastingType.NORMAL);
    }

    public void doCast(Skill skill, SkillCastingType castingType) {
        doCast(skill, null, false, false, castingType);
    }

    public void doCast(Skill skill, Item item, boolean ctrlPressed, boolean shiftPressed) {
        doCast(skill, item, ctrlPressed, shiftPressed, SkillCastingType.NORMAL);
    }

    /**
     * Manage the casting task (casting and interrupt time, re-use delay...) and display the casting bar and animation on client.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Verify the possibility of the the cast : skill is a spell, caster isn't muted...</li>
     * <li>Get the list of all targets (ex : area effects) and define the L2Charcater targeted (its stats will be used in calculation)</li>
     * <li>Calculate the casting time (base + modifier of MAtkSpd), interrupt time and re-use delay</li>
     * <li>Send a Server->Client packet MagicSkillUser (to display casting animation), a packet SetupGauge (to display casting bar) and a system message</li>
     * <li>Disable all skills during the casting time (create a task EnableAllSkills)</li>
     * <li>Disable the skill during the re-use delay (create a task EnableSkill)</li>
     * <li>Create a task MagicUseTask (that will call method onMagicUseTimer) to launch the Magic Skill at the end of the casting time</li>
     * </ul>
     *
     * @param skill        The L2Skill to use
     * @param item         The referenced item of this skill cast
     * @param ctrlPressed  if the player has pressed ctrl key during casting, aka force use.
     * @param shiftPressed if the player has pressed shift key during casting, aka dont move.
     * @param castingType  The SkillCastingType of the skill.
     */
    public synchronized void doCast(Skill skill, Item item, boolean ctrlPressed, boolean shiftPressed, SkillCastingType castingType) {
        final SkillCaster skillCaster = SkillCaster.castSkill(this, target, skill, item, castingType, ctrlPressed, shiftPressed);
        if (isNull(skillCaster) && isPlayer(this)) {
            sendPacket(ActionFailed.of(castingType));
            getAI().setIntention(AI_INTENTION_ACTIVE);
        }
    }

    /**
     * Gets the item reuse time stamps map.
     *
     * @return the item reuse time stamps map
     */
    public final IntMap<TimeStamp> getItemReuseTimeStamps() {
        return reuseTimeStampsItems;
    }

    /**
     * Adds a item reuse time stamp.
     *
     * @param item  the item
     * @param reuse the reuse
     */
    public final void addTimeStampItem(Item item, long reuse) {
        addTimeStampItem(item, reuse, -1);
    }

    /**
     * Adds a item reuse time stamp.<br>
     * Used for restoring purposes.
     *
     * @param item    the item
     * @param reuse   the reuse
     * @param systime the system time
     */
    public final void addTimeStampItem(Item item, long reuse, long systime) {
        reuseTimeStampsItems.put(item.getObjectId(), new TimeStamp(item, reuse, systime));
    }

    /**
     * Gets the item remaining reuse time for a given item object ID.
     *
     * @param itemObjId the item object ID
     * @return if the item has a reuse time stamp, the remaining time, otherwise -1
     */
    public final synchronized long getItemRemainingReuseTime(int itemObjId) {
        final TimeStamp reuseStamp = reuseTimeStampsItems.get(itemObjId);
        return reuseStamp != null ? reuseStamp.getRemaining() : -1;
    }

    /**
     * Gets the item remaining reuse time for a given shared reuse item group.
     *
     * @param group the shared reuse item group
     * @return if the shared reuse item group has a reuse time stamp, the remaining time, otherwise -1
     */
    public final long getReuseDelayOnGroup(int group) {
        if ((group > 0) && !reuseTimeStampsItems.isEmpty()) {
            final long currentTime = System.currentTimeMillis();
            for (TimeStamp ts : reuseTimeStampsItems.values()) {
                if (ts.getSharedReuseGroup() == group) {
                    final long stamp = ts.getStamp();
                    if (currentTime < stamp) {
                        return max(stamp - currentTime, 0);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Gets the skill reuse time stamps map.
     *
     * @return the skill reuse time stamps map
     */
    public final LongMap<TimeStamp> getSkillReuseTimeStamps() {
        return reuseTimeStampsSkills;
    }

    /**
     * Adds the skill reuse time stamp.
     *
     * @param skill the skill
     * @param reuse the delay
     */
    public final void addTimeStamp(Skill skill, long reuse) {
        addTimeStamp(skill, reuse, -1);
    }

    /**
     * Adds the skill reuse time stamp.<br>
     * Used for restoring purposes.
     *
     * @param skill   the skill
     * @param reuse   the reuse
     * @param systime the system time
     */
    public final void addTimeStamp(Skill skill, long reuse, long systime) {
        reuseTimeStampsSkills.put(skill.getReuseHashCode(), new TimeStamp(skill, reuse, systime));
    }

    /**
     * Removes a skill reuse time stamp.
     *
     * @param skill the skill to remove
     */
    public final synchronized void removeTimeStamp(Skill skill) {
        reuseTimeStampsSkills.remove(skill.getReuseHashCode());
    }

    /**
     * Removes all skill reuse time stamps.
     */
    public final synchronized void resetTimeStamps() {
        reuseTimeStampsSkills.clear();
    }

    /**
     * Gets the skill remaining reuse time for a given skill hash code.
     *
     * @param hashCode the skill hash code
     * @return if the skill has a reuse time stamp, the remaining time, otherwise -1
     */
    public final synchronized long getSkillRemainingReuseTime(long hashCode) {
        final TimeStamp reuseStamp = reuseTimeStampsSkills.get(hashCode);
        return reuseStamp != null ? reuseStamp.getRemaining() : -1;
    }

    /**
     * Verifies if the skill is under reuse time.
     *
     * @param hashCode the skill hash code
     * @return {@code true} if the skill is under reuse time, {@code false} otherwise
     */
    public final synchronized boolean hasSkillReuse(long hashCode) {
        return falseIfNullOrElse(reuseTimeStampsSkills.get(hashCode), TimeStamp::hasNotPassed);
    }

    /**
     * Gets the skill reuse time stamp.
     *
     * @param hashCode the skill hash code
     * @return if the skill has a reuse time stamp, the skill reuse time stamp, otherwise {@code null}
     */
    public final synchronized TimeStamp getSkillReuseTimeStamp(long hashCode) {
        return reuseTimeStampsSkills.get(hashCode);
    }

    /**
     * Enables a skill.
     *
     * @param skill the skill to enable
     */
    public void enableSkill(Skill skill) {
        if (skill == null) {
            return;
        }
        disabledSkills.remove(skill.getReuseHashCode());
    }

    /**
     * Disables a skill for a given time.<br>
     * If delay is lesser or equal than zero, skill will be disabled "forever".
     *
     * @param skill the skill to disable
     * @param delay delay in milliseconds
     */
    public void disableSkill(Skill skill, long delay) {
        disabledSkills.put(skill.getReuseHashCode(), delay > 0 ? System.currentTimeMillis() + delay : Long.MAX_VALUE);
    }

    /**
     * Removes all the disabled skills.
     */
    public void resetDisabledSkills() {
        disabledSkills.clear();
    }

    /**
     * Verifies if the skill is disabled.
     *
     * @param skill the skill
     * @return {@code true} if the skill is disabled, {@code false} otherwise
     */
    public boolean isSkillDisabled(Skill skill) {
        if (skill == null) {
            return false;
        }

        if (allSkillsDisabled || (!skill.canCastWhileDisabled() && isAllSkillsDisabled())) {
            return true;
        }

        if (isAffected(EffectFlag.CONDITIONAL_BLOCK_ACTIONS) && !isBlockedActionsAllowedSkill(skill)) {
            return true;
        }

        final long hashCode = skill.getReuseHashCode();
        if (hasSkillReuse(hashCode)) {
            return true;
        }

        if (disabledSkills.isEmpty()) {
            return false;
        }
        final Long stamp = disabledSkills.get(hashCode);
        if (stamp == null) {
            return false;
        }
        if (stamp < System.currentTimeMillis()) {
            disabledSkills.remove(hashCode);
            return false;
        }

        return true;
    }

    /**
     * Disables all skills.
     */
    public void disableAllSkills() {
        allSkillsDisabled = true;
    }

    /**
     * Enables all skills, except those under reuse time or previously disabled.
     */
    public void enableAllSkills() {
        allSkillsDisabled = false;
    }

    /**
     * Kill the Creature.
     *
     * @param killer The Creature who killed it
     * @return false if the creature hasn't been killed.
     */
    public boolean doDie(Creature killer) {
        synchronized (this) {
            if (isDead) {
                return false;
            }

            final TerminateReturn returnBack = EventDispatcher.getInstance().notifyEvent(new OnCreatureDeath(killer, this), this, TerminateReturn.class);
            if (nonNull(returnBack) && returnBack.terminate()) {
                return false;
            }
            setCurrentHp(0);
            isDead = true;
        }

        status.stopHpMpRegeneration();
        broadcastStatusUpdate();
        stopMove(null);

        onDie(killer);
        if (hasAI()) {
            getAI().notifyEvent(CtrlEvent.EVT_DEAD);
        }
        return true;
    }

    protected void onDie(Creature killer) {
        EventDispatcher.getInstance().notifyEventAsync(new OnCreatureKilled(killer, this), killer);
        ZoneManager.getInstance().getRegion(this).onDeath(this);

        forgetTarget();
        if (isChannelized()) {
            getSkillChannelized().abortChannelization();
        }
    }

    public void forgetTarget() {
        abortAttack();
        abortCast();
        setTarget(null);
    }

    @Override
    public boolean decayMe()
    {
        if (hasAI())
        {
            getAI().stopAITask();
        }
        return super.decayMe();
    }

    @Override
    public boolean deleteMe() {
        if (hasAI()) {
            getAI().stopAITask();
        }

        if ((summoner != null)) {
            summoner.removeSummonedNpc(getObjectId());
        }

        effectList.stopAllEffectsWithoutExclusions(false, false);

        // Cancel all timers related to this Creature
        TimersManager.getInstance().cancelTimers(getObjectId());

        cancelBuffFinishTask();

        // Set world region to null.
        setWorldRegion(null);
        return true;
    }

    public void doRevive() {
        if (!isDead) {
            return;
        }
        if (!isTeleporting) {
            setIsPendingRevive(false);
            setIsDead(false);

            var characterSettings = getSettings(CharacterSettings.class);

            var restore = characterSettings.restoreCPPercent() * stats.getMaxCp();
            if(restore > status.getCurrentCp()) {
                status.setCurrentCp(restore);
            }

            restore = characterSettings.restoreHPPercent() * stats.getMaxHp();
            if(restore > status.getCurrentHp()) {
                status.setCurrentHp(restore);
            }

            restore = characterSettings.restoreMPPercent() * stats.getMaxMp();
            if(restore > status.getCurrentMp()) {
                status.setCurrentMp(restore);
            }

            // Start broadcast status
            broadcastPacket(new Revive(this));

            ZoneManager.getInstance().getRegion(this).onRevive(this);
        } else {
            setIsPendingRevive(true);
        }
    }

    public void doRevive(double revivePower) {
        doRevive();
    }

    public CreatureAI getAI() {
        if (isNull(ai) ) {
            synchronized (this) {
                if (isNull(ai)) {
                    this.ai = initAI();
                }
            }
        }
        return ai;
    }

    public void setAI(CreatureAI newAI) {
        final CreatureAI oldAI = ai;
        if ((oldAI != newAI) && (oldAI instanceof AttackableAI)) {
            oldAI.stopAITask();
        }
        ai = newAI;
    }

    /**
     * Initialize this creature's AI.<br>
     * OOP approach to be overridden in child classes.
     *
     * @return the new AI
     */
    protected CreatureAI initAI() {
        return new CreatureAI(this);
    }

    public void detachAI() {
        if (GameUtils.isWalker(this)) {
            return;
        }
        setAI(null);
    }

    /**
     * Verifies if this creature has an AI,
     *
     * @return {@code true} if this creature has an AI, {@code false} otherwise
     */
    public boolean hasAI() {
        return ai != null;
    }

    /**
     * @return True if the Creature is RaidBoss or his minion.
     */
    public boolean isRaid() {
        return false;
    }

    /**
     * @return True if the Creature is minion.
     */
    public boolean isMinion() {
        return false;
    }

    /**
     * @return True if the Creature is minion of RaidBoss.
     */
    public boolean isRaidMinion() {
        return false;
    }

    public final boolean isControlBlocked() {
        return isAffected(EffectFlag.BLOCK_CONTROL);
    }

    /**
     * @return True if the Creature can't use its skills (ex : stun, sleep...).
     */
    public final boolean isAllSkillsDisabled() {
        return allSkillsDisabled || hasBlockActions();
    }

    /**
     * @return True if the Creature can't attack (stun, sleep, attackEndTime, fakeDeath, paralyze, attackMute).
     */
    public boolean isAttackingDisabled() {
        return hasBlockActions() || isAttackingNow() || isAlikeDead() || isPhysicalAttackMuted() || aiDisabled;
    }

    public final boolean isConfused() {
        return isAffected(EffectFlag.CONFUSED);
    }

    public boolean isAlikeDead() {
        return isDead;
    }

    public final boolean isDead() {
        return isDead;
    }

    public final void setIsDead(boolean value) {
        isDead = value;
    }

    public boolean isImmobilized() {
        return isImmobilized;
    }

    public void setIsImmobilized(boolean value) {
        isImmobilized = value;
    }

    public final boolean isMuted() {
        return isAffected(EffectFlag.MUTED);
    }

    public final boolean isPhysicalMuted() {
        return isAffected(EffectFlag.PSYCHICAL_MUTED);
    }

    public final boolean isPhysicalAttackMuted() {
        return isAffected(EffectFlag.PSYCHICAL_ATTACK_MUTED);
    }

    /**
     * @return True if the Creature can't move (stun, root, sleep, overload, paralyzed).
     */
    public boolean isMovementDisabled() {
        // check for isTeleporting to prevent teleport cheating (if appear packet not received)
        return hasBlockActions() || isRooted() || isOverloaded || isImmobilized || isAlikeDead() || isTeleporting;
    }

    public final boolean isOverloaded() {
        return isOverloaded;
    }

    public final void setIsOverloaded(boolean value) {
        isOverloaded = value;
    }

    public final boolean isPendingRevive() {
        return isDead && isPendingRevive;
    }

    public final void setIsPendingRevive(boolean value) {
        isPendingRevive = value;
    }

    public final boolean isDisarmed() {
        return isAffected(EffectFlag.DISARMED);
    }

    /**
     * @return the summon
     */
    public Summon getPet() {
        return null;
    }

    /**
     * @return the summon
     */
    public Map<Integer, Summon> getServitors() {
        return Collections.emptyMap();
    }

    public Summon getServitor(int objectId) {
        return null;
    }

    public final boolean hasSummon() {
        return hasPet() || !getServitors().isEmpty();
    }

    /**
     * @return {@code true} if the character has a pet, {@code false} otherwise
     */
    public final boolean hasPet() {
        return getPet() != null;
    }

    public final boolean hasServitor(int objectId) {
        return getServitors().containsKey(objectId);
    }

    /**
     * @return {@code true} if the character has a servitor, {@code false} otherwise
     */
    public final boolean hasServitors() {
        return !getServitors().isEmpty();
    }

    public void removeServitor(int objectId) {
        getServitors().remove(objectId);
    }

    public final boolean isRooted() {
        return isAffected(EffectFlag.ROOTED);
    }

    /**
     * @return True if the Creature is running.
     */
    public boolean isRunning() {
        return running;
    }

    private void setIsRunning(boolean value) {
        if (running == value) {
            return;
        }
        running = value;
        if (stats.getRunSpeed() != 0) {
            broadcastPacket(new ChangeMoveType(this));
        }
        if (isPlayer(this)) {
            getActingPlayer().broadcastUserInfo();
        } else if (GameUtils.isSummon(this)) {
            broadcastStatusUpdate();
        } else if (GameUtils.isNpc(this)) {
            World.getInstance().forEachVisibleObject(this, Player.class, player ->
            {
                if (!isVisibleFor(player)) {
                    return;
                }

                if (stats.getRunSpeed() == 0) {
                    player.sendPacket(new ServerObjectInfo((Npc) this, player));
                } else {
                    player.sendPacket(new NpcInfo((Npc) this));
                }
            });
        }
    }

    /**
     * Set the Creature movement type to run and send Server->Client packet ChangeMoveType to all others Player.
     */
    public final void setRunning() {
        setIsRunning(true);
    }

    public final boolean hasBlockActions() {
        return blockActions || isAffected(EffectFlag.BLOCK_ACTIONS) || isAffected(EffectFlag.CONDITIONAL_BLOCK_ACTIONS);
    }

    public final void setBlockActions(boolean blockActions) {
        this.blockActions = blockActions;
    }

    public final boolean isBetrayed() {
        return isAffected(EffectFlag.BETRAYED);
    }

    public final boolean isTeleporting() {
        return isTeleporting;
    }

    public void setIsTeleporting(boolean value) {
        isTeleporting = value;
    }

    public void setIsInvul(boolean b) {
        isInvul = b;
    }

    @Override
    public boolean isInvul() {
        return isInvul || isTeleporting;
    }

    public boolean isUndying() {
        return isUndying || isInvul() || isAffected(EffectFlag.IGNORE_DEATH) || isInsideZone(ZoneType.UNDYING);
    }

    public void setUndying(boolean undying) {
        isUndying = undying;
    }

    public boolean isHpBlocked() {
        return isInvul() || isAffected(EffectFlag.HP_BLOCK);
    }

    public boolean isMpBlocked() {
        return isInvul() || isAffected(EffectFlag.MP_BLOCK);
    }

    public boolean isBuffBlocked() {
        return isAffected(EffectFlag.BUFF_BLOCK);
    }

    public boolean isDebuffBlocked() {
        return isInvul() || isAffected(EffectFlag.DEBUFF_BLOCK);
    }

    public boolean isUndead() {
        return false;
    }

    public boolean isResurrectionBlocked() {
        return isAffected(EffectFlag.BLOCK_RESURRECTION);
    }

    public final boolean isFlying() {
        return isFlying;
    }

    public final void setIsFlying(boolean mode) {
        isFlying = mode;
    }

    public CreatureStats getStats() {
        return stats;
    }

    public final void setStat(CreatureStats value) {
        stats = value;
    }

    /**
     * Initializes the CharStat class of the WorldObject, is overwritten in classes that require a different CharStat Type.<br>
     * Removes the need for instanceof checks.
     */
    public void initCharStat() {
        stats = new CreatureStats(this);
    }

    public CreatureStatus getStatus() {
        return status;
    }

    public final void setStatus(CreatureStatus value) {
        status = value;
    }

    /**
     * Initializes the CharStatus class of the WorldObject, is overwritten in classes that require a different CharStatus Type.<br>
     * Removes the need for instanceof checks.
     */
    public void initCharStatus() {
        status = new CreatureStatus(this);
    }

    public CreatureTemplate getTemplate() {
        return template;
    }

    protected final void setTemplate(CreatureTemplate template) {
        this.template = template;
    }

    /**
     * @return the Title of the Creature.
     */
    public final String getTitle() {
        // Champion titles
        if (isChampion()) {
            return Config.CHAMP_TITLE;
        }
        // Custom level titles
        if (Config.SHOW_NPC_LVL && isMonster(this)) {
            String t = "Lv " + getLevel() + (((Monster) this).isAggressive() ? "*" : "");
            if (title != null) {
                t += " " + title;
            }
            return t;
        }
        // Set trap title
        if (GameUtils.isTrap(this) && (((Trap) this).getOwner() != null)) {
            title = ((Trap) this).getOwner().getName();
        }
        return title != null ? title : "";
    }

    public final void setTitle(String value) {
        if (value == null) {
            title = "";
        } else {
            title = value.length() > 21 ? value.substring(0, 20) : value;
        }
    }

    /**
     * Set the Creature movement type to walk and send Server->Client packet ChangeMoveType to all others Player.
     */
    public final void setWalking() {
        setIsRunning(false);
    }

    /**
     * Active the abnormal effect Fake Death flag, notify the Creature AI and send Server->Client UserInfo/CharInfo packet.
     */
    public final void startFakeDeath() {
        if (!isPlayer(this)) {
            return;
        }

        // Aborts any attacks/casts if fake dead
        abortAttack();
        abortCast();
        stopMove(null);
        getAI().notifyEvent(CtrlEvent.EVT_FAKE_DEATH);
        broadcastPacket(ChangeWaitType.startFakeDeath(this));
    }

    public final void startParalyze() {
        // Aborts any attacks/casts if paralyzed
        abortAttack();
        abortCast();
        stopMove(null);
        getAI().notifyEvent(CtrlEvent.EVT_ACTION_BLOCKED);
    }

    /**
     * Stop all active skills effects in progress on the Creature.
     */
    public void stopAllEffects() {
        effectList.stopAllEffects(true);
    }

    /**
     * Stops all effects, except those that last through death.
     */
    public void stopAllEffectsExceptThoseThatLastThroughDeath() {
        effectList.stopAllEffectsExceptThoseThatLastThroughDeath();
    }

    /**
     * Stop and remove the effects corresponding to the skill ID.
     *  @param removed if {@code true} the effect will be set as removed, and a system message will be sent
     * @param skillId the skill Id
     * @return true if the effect was stopped
     */
    public boolean stopSkillEffects(boolean removed, int skillId) {
        return effectList.stopSkillEffects(removed, skillId);
    }

    public void stopSkillEffects(Skill skill) {
        effectList.stopSkillEffects(true, skill.getId());
    }

    public final void stopEffects(EffectFlag effectFlag) {
        effectList.stopEffects(effectFlag);
    }

    /**
     * Exits all buffs effects of the skills with "removedOnAnyAction" set.<br>
     * Called on any action except movement (attack, cast).
     */
    public final void stopEffectsOnAction() {
        effectList.stopEffectsOnAction();
    }

    /**
     * Exits all buffs effects of the skills with "removedOnDamage" set.<br>
     * Called on decreasing HP and mana burn.
     */
    public final void stopEffectsOnDamage() {
        effectList.stopEffectsOnDamage();
    }

    public final void stopFakeDeath(boolean removeEffects) {
        if (removeEffects) {
            stopEffects(EffectFlag.FAKE_DEATH);
        }
        broadcastPacket(ChangeWaitType.stopFakeDeath(this));
        // TODO: Temp hack: players see FD on ppl that are moving: Teleport to someone who uses FD - if he gets up he will fall down again for that client -
        // even tho he is actually standing... Probably bad info in CharInfo packet?
        broadcastPacket(new Revive(this));
    }

    /**
     * Stop all block actions (stun) effects.<br>
     *
     * @param removeEffects {@code true} removes all block actions effects, {@code false} only notifies AI to think.
     */
    public final void stopStunning(boolean removeEffects) {
        if (removeEffects) {
            effectList.stopEffects(AbnormalType.STUN);
        }

        if (!isPlayer(this)) {
            getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }

    public final void startStunning() {
        abortAttack();
        abortCast();
        stopMove(null);
        getAI().notifyEvent(CtrlEvent.EVT_ACTION_BLOCKED);
        if (!GameUtils.isSummon(this)) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
        updateAbnormalVisualEffects();
    }

    public final void stopTransformation(boolean removeEffects) {
        if (removeEffects) {
            effectList.stopEffects(AbnormalType.TRANSFORM);
            effectList.stopEffects(AbnormalType.CHANGEBODY);
        }

        if (nonNull(transform)) {
            untransform();
        }

        if (!isPlayer(this)) {
            getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
        updateAbnormalVisualEffects();
    }

    /**
     * Updates the visual abnormal state of this character. <br>
     */
    public void updateAbnormalVisualEffects() {
        // overridden
    }

    /**
     * Update active skills in progress (In Use and Not In Use because stacked) icons on client.<br>
     * <B><U>Concept</U>:</B><br>
     * All active skills effects in progress (In Use and Not In Use because stacked) are represented by an icon on the client.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This     method ONLY UPDATE the client of the player and not clients of all players in the party.</B></FONT>
     */
    public final void updateEffectIcons() {
        updateEffectIcons(false);
    }

    public void updateEffectIcons(boolean partyOnly) {
        // overridden
    }

    public boolean isAffectedBySkill(SkillHolder skill) {
        return isAffectedBySkill(skill.getSkillId());
    }

    public boolean isAffectedBySkill(int skillId) {
        return effectList.isAffectedBySkill(skillId);
    }

    public int getAffectedSkillLevel(int skillId) {
        final BuffInfo info = effectList.getBuffInfoBySkillId(skillId);
        return info == null ? 0 : info.getSkill().getLevel();
    }

    public void broadcastModifiedStats(Set<Stat> changed) {
        if (!isSpawned()) {
            return;
        }

        if (isNullOrEmpty(changed)) {
            return;
        }

        // Don't broadcast modified stats on login.
        if (isPlayer(this) && !getActingPlayer().isOnline()) {
            return;
        }

        // If this creature was previously moving, but now due to stat change can no longer move, broadcast StopMove packet.
        if (isMoving() && (stats.getMoveSpeed() <= 0)) {
            stopMove(null);
        }

        if (GameUtils.isSummon(this)) {
            final Summon summon = (Summon) this;
            if (summon.getOwner() != null) {
                summon.updateAndBroadcastStatus(1);
            }
        } else {
            final boolean broadcastFull = true;
            final StatusUpdate su = new StatusUpdate(this);
            UserInfo info = null;
            if (isPlayer(this)) {
                info = new UserInfo(getActingPlayer(), false);
                info.addComponentType(UserInfoType.SLOTS, UserInfoType.ENCHANTLEVEL);
            }
            if (info != null) {
                for (Stat stat : changed) {
                    addStatInfoType(su, info, stat);
                }
            }

            if (isPlayer(this)) {
                final Player player = getActingPlayer();
                player.refreshOverloaded(true);
                sendPacket(info);

                if (broadcastFull) {
                    player.broadcastCharInfo();
                } else if (su.hasUpdates()) {
                    broadcastPacket(su);
                }
                if (hasServitors() && hasAbnormalType(AbnormalType.ABILITY_CHANGE)) {
                    getServitors().values().forEach(Summon::broadcastStatusUpdate);
                }
            } else if (GameUtils.isNpc(this)) {
                if (broadcastFull) {
                    World.getInstance().forEachVisibleObject(this, Player.class, player ->
                    {
                        if (!isVisibleFor(player)) {
                            return;
                        }

                        if (stats.getRunSpeed() == 0) {
                            player.sendPacket(new ServerObjectInfo((Npc) this, player));
                        } else {
                            player.sendPacket(new NpcInfo((Npc) this));
                        }
                    });
                } else if (su.hasUpdates()) {
                    broadcastPacket(su);
                }
            } else if (su.hasUpdates()) {
                broadcastPacket(su);
            }
        }
    }

    private void addStatInfoType(StatusUpdate su, UserInfo info, Stat stat) {
        switch (stat) {
            case SPEED, RUN_SPEED, WALK_SPEED, SWIM_RUN_SPEED, SWIM_WALK_SPEED, FLY_RUN_SPEED,
                    FLY_WALK_SPEED -> info.addComponentType(UserInfoType.MULTIPLIER);

            case PHYSICAL_ATTACK_SPEED -> info.addComponentType(UserInfoType.MULTIPLIER, UserInfoType.STATS);

            case PHYSICAL_ATTACK, PHYSICAL_DEFENCE, EVASION_RATE, ACCURACY, CRITICAL_RATE, MAGIC_CRITICAL_RATE, MAGIC_EVASION_RATE, ACCURACY_MAGIC,
                    MAGIC_ATTACK, MAGIC_ATTACK_SPEED, MAGICAL_DEFENCE, HIT_AT_NIGHT -> info.addComponentType(UserInfoType.STATS);

            case MAX_CP -> addVitalStatInfoType(su, info, StatusUpdateType.MAX_CP, stats.getMaxCp());
            case MAX_HP -> addVitalStatInfoType(su, info, StatusUpdateType.MAX_HP, stats.getMaxHp());
            case MAX_MP -> addVitalStatInfoType(su, info, StatusUpdateType.MAX_CP, stats.getMaxMp());

            case STAT_STR, STAT_CON, STAT_DEX, STAT_INT, STAT_WIT, STAT_MEN ->
                    info.addComponentType(UserInfoType.BASE_STATS, UserInfoType.STATS_ABILITIES, UserInfoType.STATS_POINTS);

            case FIRE_RES, WATER_RES, WIND_RES, EARTH_RES, HOLY_RES, DARK_RES -> info.addComponentType(UserInfoType.ELEMENTALS);

            case FIRE_POWER, WATER_POWER, WIND_POWER, EARTH_POWER, HOLY_POWER, DARK_POWER -> info.addComponentType(UserInfoType.ATK_ELEMENTAL);

            case ELEMENTAL_SPIRIT_EARTH_ATTACK, ELEMENTAL_SPIRIT_EARTH_DEFENSE, ELEMENTAL_SPIRIT_FIRE_ATTACK, ELEMENTAL_SPIRIT_FIRE_DEFENSE,
                    ELEMENTAL_SPIRIT_WATER_ATTACK, ELEMENTAL_SPIRIT_WATER_DEFENSE, ELEMENTAL_SPIRIT_WIND_ATTACK, ELEMENTAL_SPIRIT_WIND_DEFENSE -> info.addComponentType(UserInfoType.SPIRITS);
        }
    }

    private void addVitalStatInfoType(StatusUpdate su, UserInfo info, StatusUpdateType updateType, int maxStat) {
        if (isPlayer(this)) {
            info.addComponentType(UserInfoType.MAX_HPCPMP);
        } else {
            su.addUpdate(updateType, maxStat);
        }
    }

    public final int getXdestination() {
        final MoveData m = move;

        if (m != null) {
            return m._xDestination;
        }

        return getX();
    }

    /**
     * @return the Y destination of the Creature or the Y position if not in movement.
     */
    public final int getYdestination() {
        final MoveData m = move;

        if (m != null) {
            return m._yDestination;
        }

        return getY();
    }

    /**
     * @return the Z destination of the Creature or the Z position if not in movement.
     */
    public final int getZdestination() {
        final MoveData m = move;

        if (m != null) {
            return m._zDestination;
        }

        return getZ();
    }

    /**
     * @return True if the Creature is in combat.
     */
    public boolean isInCombat() {
        return hasAI() && getAI().isAutoAttacking();
    }

    /**
     * @return True if the Creature is moving.
     */
    public final boolean isMoving() {
        return move != null;
    }

    /**
     * @return True if the Creature is travelling a calculated path.
     */
    public final boolean isOnGeodataPath() {
        final MoveData m = move;
        if (isNull(m) || m.onGeodataPathIndex == -1) {
            return false;
        }
        return m.onGeodataPathIndex != (m.geoPath.size() - 1);
    }

    /**
     * @return True if the Creature is casting any kind of skill, including simultaneous skills like potions.
     */
    public final boolean isCastingNow() {
        return !skillCasters.isEmpty();
    }

    public final boolean isCastingNow(SkillCastingType skillCastingType) {
        return skillCasters.containsKey(skillCastingType);
    }

    public final boolean isCastingNow(Predicate<SkillCaster> filter) {
        for (SkillCaster caster : skillCasters.values()) {
            if(filter.test(caster)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return True if the Creature is attacking.
     */
    public final boolean isAttackingNow() {
        return attackEndTime > System.nanoTime();
    }

    /**
     * Abort the attack of the Creature and send Server->Client ActionFailed packet.
     */
    public final void abortAttack() {
        if (isAttackingNow()) {
            final var actualHitTask = this.hitTask;
            if (actualHitTask != null) {
                actualHitTask.cancel(false);
                this.hitTask = null;
            }
            sendPacket(ActionFailed.STATIC_PACKET);
        }
    }

    /**
     * Abort the cast of all skills.
     */
    public final void abortAllSkillCasters()
    {
        for (SkillCaster skillCaster : getSkillCasters())
        {
            skillCaster.stopCasting(true);
            if (isPlayer(this))
            {
                getActingPlayer().setQueuedSkill(null, false, false);
            }
        }
    }

    /**
     * Abort the cast of normal non-simultaneous skills.
     *
     * @return {@code true} if a skill casting has been aborted, {@code false} otherwise.
     */
    public final boolean abortCast() {
        return abortCast(SkillCaster::isAnyNormalType);
    }

    public final boolean abortCast(Predicate<SkillCaster> filter) {
        final SkillCaster skillCaster = getSkillCaster(SkillCaster::canAbortCast, filter);
        if (skillCaster != null) {
            skillCaster.stopCasting(true);
            if (isPlayer(this)) {
                getActingPlayer().setQueuedSkill(null, false, false);
            }
            return true;
        }

        return false;
    }

    /**
     * Update the position of the Creature during a movement and return True if the movement is finished.<br>
     * <B><U>Concept</U>:</B><br>
     * At the beginning of the move action, all properties of the movement are stored in the MoveData object called <B>_move</B> of the Creature.<br>
     * The position of the start point and of the destination permit to estimated in function of the movement speed the time to achieve the destination.<br>
     * When the movement is started (ex : by MovetoLocation), this method will be called each 0.1 sec to estimate and update the Creature position on the server.<br>
     * Note, that the current server position can differe from the current client position even if each movement is straight foward.<br>
     * That's why, client send regularly a Client->Server ValidatePosition packet to eventually correct the gap on the server.<br>
     * But, it's always the server position that is used in range calculation. At the end of the estimated movement time,<br>
     * the Creature position is automatically set to the destination position even if the movement is not finished.<br>
     * <FONT COLOR=#FF0000><B><U>Caution</U>: The current Z position is obtained FROM THE CLIENT by the Client->Server ValidatePosition Packet.<br>
     * But x and y positions must be calculated to avoid that players try to modify their movement speed.</B></FONT>
     *
     * @return True if the movement is finished
     */
    public boolean updatePosition() {
        // Get movement data
        final MoveData m = move;

        if (m == null) {
            return true;
        }

        if (!isSpawned()) {
            move = null;
            if (isPlayer(this))
                sendPacket(new UserInfo(getActingPlayer(), UserInfoType.STATS));
            return true;
        }

        // Check if this is the first update
        if (m._moveTimestamp == 0) {
            m._moveTimestamp = m._moveStartTime;
            m._xAccurate = getX();
            m._yAccurate = getY();
        }

        final int gameTicks = WorldTimeController.getInstance().getGameTicks();

        // Check if the position has already been calculated
        if (m._moveTimestamp == gameTicks) {
            return false;
        }

        final int xPrev = getX();
        final int yPrev = getY();
        int zPrev = getZ(); // the z coordinate may be modified by coordinate synchronizations

        double dx;
        double dy;
        double dz;

        // the only method that can modify x,y while moving (otherwise _move would/should be set null)
        if (getSettings(GeoEngineSettings.class).isSyncMode(SyncMode.CLIENT)) {
            dx = m._xDestination - xPrev;
            dy = m._yDestination - yPrev;
        } else {  // otherwise we need saved temporary values to avoid rounding errors
            dx = m._xDestination - m._xAccurate;
            dy = m._yDestination - m._yAccurate;
        }

        // Z coordinate will follow client values
        dz = m._zDestination - zPrev;

        if (isPlayer(this) && !isFlying) {
            final double distance = Math.hypot(dx, dy);
            if (cursorKeyMovement // In case of cursor movement, avoid moving through obstacles.
                    || (distance > 3000)) // Stop movement when player has clicked far away and intersected with an obstacle.
            {
                final double angle = convertHeadingToDegree(getHeading());
                final double radian = Math.toRadians(angle);
                final double course = Math.toRadians(180);
                final double frontDistance = 10 * (stats.getMoveSpeed() / 100);
                final int x1 = (int) (Math.cos(Math.PI + radian + course) * frontDistance);
                final int y1 = (int) (Math.sin(Math.PI + radian + course) * frontDistance);
                final int x = xPrev + x1;
                final int y = yPrev + y1;
                if (!GeoEngine.getInstance().canMoveToTarget(xPrev, yPrev, zPrev, x, y, zPrev, getInstanceWorld())) {
                    move.onGeodataPathIndex = -1;
                    stopMove(getActingPlayer().getLastServerPosition());
                    cursorKeyMovementActive = false;
                    return false;
                }
            }
            // Prevent player moving on ledges.
            if ((dz > 180) && (distance < 300)) {
                move.onGeodataPathIndex = -1;
                stopMove(getActingPlayer().getLastServerPosition());
                return false;
            }
        }

        final boolean isFloating = isFlying || isInsideZone(ZoneType.WATER);
        double delta = (dx * dx) + (dy * dy);
        if ((delta < 10000) && ((dz * dz) > 2500) // close enough, allows error between client and server geodata if it cannot be avoided
                && !isFloating) {
            delta = Math.sqrt(delta);
        } else {
            delta = Math.sqrt(delta + (dz * dz));
        }

        double distFraction = Double.MAX_VALUE;
        if (delta > 1) {
            final double distPassed = (stats.getMoveSpeed() * (gameTicks - m._moveTimestamp)) / WorldTimeController.TICKS_PER_SECOND;
            distFraction = distPassed / delta;
        }

        // if (Config.DEVELOPER) LOGGER.warn("Move Ticks:" + (gameTicks - m._moveTimestamp) + ", distPassed:" + distPassed + ", distFraction:" + distFraction);

        if (distFraction > 1) {
            // Set the position of the Creature to the destination
            super.setXYZ(m._xDestination, m._yDestination, m._zDestination);
        } else {
            m._xAccurate += dx * distFraction;
            m._yAccurate += dy * distFraction;

            // Set the position of the Creature to estimated after parcial move
            super.setXYZ((int) (m._xAccurate), (int) (m._yAccurate), zPrev + (int) ((dz * distFraction) + 0.5));
        }
        revalidateZone(false);

        // Set the timer of last position update to now
        m._moveTimestamp = gameTicks;

       // broadcastPacket(new MoveToLocation(this));

        if (distFraction > 1) {
            ThreadPool.execute(() -> getAI().notifyEvent(CtrlEvent.EVT_ARRIVED));
            return true;
        }

        return false;
    }

    public void revalidateZone(boolean force) {
        // This function is called too often from movement code
        if (force) {
            zoneValidateCounter = 4;
        } else {
            zoneValidateCounter--;
            if (zoneValidateCounter < 0) {
                zoneValidateCounter = 4;
            } else {
                return;
            }
        }

        final ZoneRegion region = ZoneManager.getInstance().getRegion(this);
        if (region != null) {
            region.revalidateZones(this);
        } else // Precaution. Moved at invalid region?
        {
            World.getInstance().disposeOutOfBoundsObject(this);
        }
    }

    public void stopMove(Location loc) {
        move = null;
        cursorKeyMovement = false;

        // All data are contained in a Location object
        if (loc != null) {
            setXYZ(loc.getX(), loc.getY(), loc.getZ());
            setHeading(loc.getHeading());
            revalidateZone(true);
        }
        broadcastPacket(new StopMove(this));
        if (isPlayer(this))
            sendPacket(new UserInfo(getActingPlayer(), UserInfoType.STATS));
    }

    public boolean isShowSummonAnimation() {
        return showSummonAnimation;
    }

    /**
     * @param showSummonAnimation The showSummonAnimation to set.
     */
    public void setShowSummonAnimation(boolean showSummonAnimation) {
        this.showSummonAnimation = showSummonAnimation;
    }

    /**
     * @return the identifier of the WorldObject targeted or -1.
     */
    public final int getTargetId() {
        if (target != null) {
            return target.getObjectId();
        }
        return 0;
    }

    /**
     * @return the WorldObject targeted or null.
     */
    public final WorldObject getTarget() {
        return target;
    }

    /**
     * Target a WorldObject (add the target to the Creature _target, _knownObject and Creature to _KnownObject of the WorldObject).<br>
     * <B><U>Concept</U>:</B><br>
     * The WorldObject (including Creature) targeted is identified in <B>_target</B> of the Creature.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Set the _target of Creature to WorldObject</li>
     * <li>If necessary, add WorldObject to _knownObject of the Creature</li>
     * <li>If necessary, add Creature to _KnownObject of the WorldObject</li>
     * <li>If object==null, cancel Attak or Cast</li>
     * </ul>
     *
     * @param object L2object to target
     */
    public void setTarget(WorldObject object) {
        if ((object != null) && !object.isSpawned()) {
            object = null;
        }

        target = object;
    }

    // called from AIAccessor only

    /**
     * Calculate movement data for a move to location action and add the Creature to movingObjects of GameTimeController (only called by AI Accessor).<br>
     * <B><U>Concept</U>:</B><br>
     * At the beginning of the move action, all properties of the movement are stored in the MoveData object called <B>_move</B> of the Creature.<br>
     * The position of the start point and of the destination permit to estimated in function of the movement speed the time to achieve the destination.<br>
     * All Creature in movement are identified in <B>movingObjects</B> of GameTimeController that will call the updatePosition method of those Creature each 0.1s.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Get current position of the Creature</li>
     * <li>Calculate distance (dx,dy) between current position and destination including offset</li>
     * <li>Create and Init a MoveData object</li>
     * <li>Set the Creature _move object to MoveData object</li>
     * <li>Add the Creature to movingObjects of the GameTimeController</li>
     * <li>Create a task to notify the AI that Creature arrives at a check point of the movement</li>
     * </ul>
     * <FONT COLOR=#FF0000><B><U>Caution</U>: This method DOESN'T send Server->Client packet MoveToPawn/CharMoveToLocation.</B></FONT><br>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>AI : onIntentionMoveTo(Location), onIntentionPickUp(WorldObject), onIntentionInteract(WorldObject)</li>
     * <li>FollowTask</li>
     * </ul>
     *
     * @param x      The X position of the destination
     * @param y      The Y position of the destination
     * @param z      The Y position of the destination
     * @param offset The size of the interaction area of the Creature targeted
     */
    public void moveToLocation(int x, int y, int z, int offset) {
        // Get the Move Speed of the L2Charcater
        final double speed = stats.getMoveSpeed();
        if ((speed <= 0) || isMovementDisabled()) {
            return;
        }

        // Get current position of the Creature
        final int curX = getX();
        final int curY = getY();
        final int curZ = getZ();

        // Calculate distance (dx,dy) between current position and destination
        // TODO: improve Z axis move/follow support when dx,dy are small compared to dz
        double dx = (x - curX);
        double dy = (y - curY);
        double dz = (z - curZ);
        double distance = Math.hypot(dx, dy);

        if (!cursorKeyMovementActive && (distance > 200)) {
            return;
        }

        final boolean verticalMovementOnly = isFlying && (distance == 0) && (dz != 0);
        if (verticalMovementOnly) {
            distance = Math.abs(dz);
        }

        // Make water move short and use no geodata checks for swimming chars distance in a click can easily be over 3000.
        final boolean isInWater = isInsideZone(ZoneType.WATER);
        if (isInWater && (distance > 700)) {
            final double divider = 700 / distance;
            x = curX + (int) (divider * dx);
            y = curY + (int) (divider * dy);
            z = curZ + (int) (divider * dz);
            dx = (x - curX);
            dy = (y - curY);
            dz = (z - curZ);
            distance = Math.hypot(dx, dy);
        }

        // @formatter:off
        // Define movement angles needed
        // ^
        // |    X (x,y)
        // |   /
        // |  / distance
        // | /
        // |/ angle
        // X ---------->
        // (curx,cury)
        // @formatter:on

        double cos;
        double sin;

        // Check if a movement offset is defined or no distance to go through
        if ((offset > 0) || (distance < 1)) {
            // approximation for moving closer when z coordinates are different
            // TODO: handle Z axis movement better
            offset -= Math.abs(dz);
            if (offset < 5) {
                offset = 5;
            }

            // If no distance to go through, the movement is canceled
            if ((distance < 1) || ((distance - offset) <= 0)) {
                // Notify the AI that the Creature is arrived at destination
                getAI().notifyEvent(CtrlEvent.EVT_ARRIVED);
                return;
            }

            // Calculate movement angles needed
            sin = dy / distance;
            cos = dx / distance;

            distance -= (offset - 5); // due to rounding error, we have to move a bit closer to be in range

            // Calculate the new destination with offset included
            x = curX + (int) (distance * cos);
            y = curY + (int) (distance * sin);
        } else {
            // Calculate movement angles needed
            sin = dy / distance;
            cos = dx / distance;
        }

        // Create and Init a MoveData object
        final MoveData m = new MoveData();

        // GEODATA MOVEMENT CHECKS AND PATHFINDING
        m.onGeodataPathIndex = -1; // Initialize not on geodata path
        m.disregardingGeodata = false;

        if (!isFlying && !isInWater && !isVehicle() && !cursorKeyMovement) {
            final boolean isInVehicle = isPlayer(this) && (getActingPlayer().getVehicle() != null);
            if (isInVehicle) {
                m.disregardingGeodata = true;
            }

            var geoSettings = getSettings(GeoEngineSettings.class);
            // Movement checks.
            if (geoSettings.isEnabledPathFinding() && !(this instanceof FriendlyNpc)) {
                final double originalDistance = distance;
                final int originalX = x;
                final int originalY = y;
                final int originalZ = z;
                final int gtx = (originalX - World.MAP_MIN_X) >> 4;
                final int gty = (originalY - World.MAP_MIN_Y) >> 4;

                if (isOnGeodataPath()) {
                    try {
                        if ((gtx == move.geoPathGtx) && (gty == move.geoPathGty)) {
                            return;
                        }
                        move.onGeodataPathIndex = -1; // Set not on geodata path.
                    } catch (NullPointerException e) {
                        // nothing
                    }
                }

                if (!isInVehicle // Not in vehicle.
                        && !(isPlayer(this) && (distance > 3000)) // Should be able to click far away and move.
                        && !(isMonster(this) && (Math.abs(dz) > 100)) // Monsters can move on ledges.
                        && !(((curZ - z) > 300) && (distance < 300))) // Prohibit correcting destination if character wants to fall.
                {
                    // location different if destination wasn't reached (or just z coord is different)
                    final Location destiny = GeoEngine.getInstance().canMoveToTargetLoc(curX, curY, curZ, x, y, z, getInstanceWorld());
                    x = destiny.getX();
                    y = destiny.getY();
                    dx = x - curX;
                    dy = y - curY;
                    dz = z - curZ;
                    distance = verticalMovementOnly ? Math.pow(dz, 2) : Math.hypot(dx, dy);
                }

                // Pathfinding checks.
                if (((originalDistance - distance) > 30) && !isControlBlocked() && !isInVehicle) {
                    // Path calculation -- overrides previous movement check
                    m.geoPath = GeoEngine.getInstance().findPath(curX, curY, curZ, originalX, originalY, originalZ, getInstanceWorld());
                    if ((m.geoPath == null) || (m.geoPath.size() < 2)) // No path found
                    {
                        m.disregardingGeodata = true;

                        // Mobius: Verify destination. Prevents wall collision issues.
                        final Location newDestination = GeoEngine.getInstance().canMoveToTargetLoc(curX, curY, curZ, originalX, originalY, originalZ, getInstanceWorld());
                        x = newDestination.getX();
                        y = newDestination.getY();
                        z = newDestination.getZ();
                    } else {
                        m.onGeodataPathIndex = 0; // on first segment
                        m.geoPathGtx = gtx;
                        m.geoPathGty = gty;
                        m.geoPathAccurateTx = originalX;
                        m.geoPathAccurateTy = originalY;

                        x = m.geoPath.get(m.onGeodataPathIndex).getX();
                        y = m.geoPath.get(m.onGeodataPathIndex).getY();
                        z = m.geoPath.get(m.onGeodataPathIndex).getZ();

                        dx = x - curX;
                        dy = y - curY;
                        dz = z - curZ;
                        distance = verticalMovementOnly ? Math.pow(dz, 2) : Math.hypot(dx, dy);
                        sin = dy / distance;
                        cos = dx / distance;
                    }
                }
            }
          //  final Location destination = GeoData.getInstance().moveCheck(curX, curY, curZ, x, y, z, getInstanceId());
           // getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, destination);
            // If no distance to go through, the movement is canceled
            if ((distance < 1) && (geoSettings.isEnabledPathFinding() || GameUtils.isPlayable(this))) {
                if (GameUtils.isSummon(this)) {
                    // Do not break following owner.
                    if (getAI().getTarget() != getActingPlayer()) {
                        ((Summon) this).setFollowStatus(false);
                        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                    }
                } else {
                    getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                }
                return;
            }
        }

        // Apply Z distance for flying or swimming for correct timing calculations
        if ((isFlying || isInWater) && !verticalMovementOnly) {
            distance = Math.hypot(distance, dz);
        }

        // Calculate the number of ticks between the current position and the destination
        // One tick added for rounding reasons
        final int ticksToMove = 1 + (int) ((WorldTimeController.TICKS_PER_SECOND * distance) / speed);
        m._xDestination = x;
        m._yDestination = y;
        m._zDestination = z; // this is what was requested from client

        // Calculate and set the heading of the Creature
        m._heading = 0; // initial value for coordinate sync
        // Does not broke heading on vertical movements
        if (!verticalMovementOnly) {
            setHeading(calculateHeadingFrom(cos, sin));
        }

        m._moveStartTime = WorldTimeController.getInstance().getGameTicks();

        // Set the Creature _move object to MoveData object
        move = m;
        if (isPlayer(this))
            sendPacket(new UserInfo(getActingPlayer(), UserInfoType.STATS));


        // Add the Creature to movingObjects of the GameTimeController
        // The GameTimeController manage objects movement
        WorldTimeController.getInstance().registerMovingObject(this);

        // Create a task to notify the AI that Creature arrives at a check point of the movement
        if ((ticksToMove * WorldTimeController.MILLIS_IN_TICK) > 3000) {
            ThreadPool.schedule(new NotifyAITask(this, CtrlEvent.EVT_ARRIVED_REVALIDATE), 2000);
        }
        // the CtrlEvent.EVT_ARRIVED will be sent when the character will actually arrive to destination by GameTimeController
    }

    public boolean moveToNextRoutePoint() {
        if (!isOnGeodataPath()) {
            // Cancel the move action
            move = null;
            if (isPlayer(this))
                sendPacket(new UserInfo(getActingPlayer(), UserInfoType.STATS));
            return false;
        }

        // Get the Move Speed of the L2Charcater
        final double speed = stats.getMoveSpeed();
        if ((speed <= 0) || isMovementDisabled()) {
            // Cancel the move action
            move = null;
            if (isPlayer(this))
                sendPacket(new UserInfo(getActingPlayer(), UserInfoType.STATS));
            return false;
        }

        final MoveData md = move;
        if (md == null) {
            return false;
        }

        // Get current position of the Creature
        final int curX = getX();
        final int curY = getY();

        // Create and Init a MoveData object
        final MoveData m = new MoveData();

        // Update MoveData object
        m.onGeodataPathIndex = md.onGeodataPathIndex + 1; // next segment
        m.geoPath = md.geoPath;
        m.geoPathGtx = md.geoPathGtx;
        m.geoPathGty = md.geoPathGty;
        m.geoPathAccurateTx = md.geoPathAccurateTx;
        m.geoPathAccurateTy = md.geoPathAccurateTy;

        if (md.onGeodataPathIndex == (md.geoPath.size() - 2)) {
            m._xDestination = md.geoPathAccurateTx;
            m._yDestination = md.geoPathAccurateTy;
            m._zDestination = md.geoPath.get(m.onGeodataPathIndex).getZ();
        } else {
            m._xDestination = md.geoPath.get(m.onGeodataPathIndex).getX();
            m._yDestination = md.geoPath.get(m.onGeodataPathIndex).getY();
            m._zDestination = md.geoPath.get(m.onGeodataPathIndex).getZ();
        }

        final double distance = Math.hypot(m._xDestination - curX, m._yDestination - curY);
        // Calculate and set the heading of the Creature
        if (distance != 0) {
            setHeading(calculateHeadingFrom(curX, curY, m._xDestination, m._yDestination));
        }

        // Calculate the number of ticks between the current position and the destination
        // One tick added for rounding reasons
        final int ticksToMove = 1 + (int) ((WorldTimeController.TICKS_PER_SECOND * distance) / speed);

        m._heading = 0; // initial value for coordinate sync

        m._moveStartTime = WorldTimeController.getInstance().getGameTicks();

        // Set the Creature _move object to MoveData object
        move = m;

        // Add the Creature to movingObjects of the GameTimeController
        // The GameTimeController manage objects movement
        WorldTimeController.getInstance().registerMovingObject(this);

        // Create a task to notify the AI that Creature arrives at a check point of the movement
        if ((ticksToMove * WorldTimeController.MILLIS_IN_TICK) > 3000) {
            ThreadPool.schedule(new NotifyAITask(this, CtrlEvent.EVT_ARRIVED_REVALIDATE), 2000);
        }

        // the CtrlEvent.EVT_ARRIVED will be sent when the character will actually arrive
        // to destination by GameTimeController

        // Send a Server->Client packet CharMoveToLocation to the actor and all Player in its _knownPlayers
        broadcastPacket(new MoveToLocation(this));

        return true;
    }

    public boolean validateMovementHeading(int heading) {
        final MoveData m = move;

        if (m == null) {
            return true;
        }

        boolean result = true;
        if (m._heading != heading) {
            result = (m._heading == 0); // initial value or false
            m._heading = heading;
        }

        return result;
    }

    public void addExpAndSp(double addToExp, double addToSp) {
        // Dummy method (overridden by players and pets)
    }

    /**
     * <B><U> Overridden in </U> :</B>
     * <li>Player</li>
     *
     * @return the active weapon instance (always equiped in the right hand).
     */
    public abstract Item getActiveWeaponInstance();

    /**
     * <B><U> Overridden in </U> :</B>
     * <li>Player</li>
     *
     * @return the active weapon item (always equiped in the right hand).
     */
    public abstract Weapon getActiveWeaponItem();

    /**
     * <B><U> Overridden in </U> :</B>
     * <li>Player</li>
     *
     * @return the secondary weapon instance (always equiped in the left hand).
     */
    public abstract Item getSecondaryWeaponInstance();

    /**
     * <B><U> Overridden in </U> :</B>
     * <li>Player</li>
     *
     * @return the secondary {@link ItemTemplate} item (always equiped in the left hand).
     */
    public abstract ItemTemplate getSecondaryWeaponItem();

    /**
     * Manage hit process (called by Hit Task).<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>If the attacker/target is dead or use fake death, notify the AI with EVT_CANCEL and send a Server->Client packet ActionFailed (if attacker is a Player)</li>
     * <li>If attack isn't aborted, send a message system (critical hit, missed...) to attacker/target if they are Player</li>
     * <li>If attack isn't aborted and hit isn't missed, reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary</li>
     * <li>if attack isn't aborted and hit isn't missed, manage attack or cast break of the target (calculating rate, sending message...)</li>
     * </ul>
     *
     * @param weapon     the weapon used for the hit
     * @param attack     the attack data of targets to hit
     * @param hitTime    the time it took for this hit to occur
     * @param attackTime the time it takes for the whole attack to complete
     */
    public void onHitTimeNotDual(Weapon weapon, Attack attack, int hitTime, int attackTime) {
        if (isDead) {
            getAI().notifyEvent(CtrlEvent.EVT_CANCEL);
            return;
        }

        for (Hit hit : attack.getHits()) {
            final Creature hitTarget = ((Creature) hit.getTarget());
            if ((hitTarget == null) || hitTarget.isDead() || !isInSurroundingRegion(hitTarget)) {
                continue;
            }

            if (hit.isMiss()) {
                notifyAttackAvoid(hitTarget, false);
            } else {
                onHitTarget(hitTarget, weapon, hit);
            }
        }

        hitTask = ThreadPool.schedule(() -> onAttackFinish(attack), (long) attackTime - hitTime);
    }

    public void onFirstHitTimeForDual(Weapon weapon, Attack attack, int hitTime, int attackTime, int delayForSecondAttack) {
        if (isDead) {
            getAI().notifyEvent(CtrlEvent.EVT_CANCEL);
            return;
        }

        hitTask = ThreadPool.schedule(() -> onSecondHitTimeForDual(weapon, attack, hitTime, delayForSecondAttack, attackTime), delayForSecondAttack);

        // First dual attack is the first hit only.
        final Hit hit = attack.getHits().get(0);
        final Creature hitTarget = ((Creature) hit.getTarget());

        if ((hitTarget == null) || hitTarget.isDead() || !isInSurroundingRegion(hitTarget)) {
            getAI().notifyEvent(CtrlEvent.EVT_CANCEL);
            return;
        }

        if (hit.isMiss()) {
            notifyAttackAvoid(hitTarget, false);
        } else {
            onHitTarget(hitTarget, weapon, hit);
        }
    }

    public void onSecondHitTimeForDual(Weapon weapon, Attack attack, long hitTime1, long hitTime2, long attackTime) {
        if (isDead) {
            getAI().notifyEvent(CtrlEvent.EVT_CANCEL);
            return;
        }

        // Second dual attack is the remaining hits (first hit not included)
        for (int i = 1; i < attack.getHits().size(); i++) {
            final Hit hit = attack.getHits().get(i);
            final Creature hitTarget = ((Creature) hit.getTarget());
            if ((hitTarget == null) || hitTarget.isDead() || !isInSurroundingRegion(hitTarget)) {
                continue;
            }

            if (hit.isMiss()) {
                notifyAttackAvoid(hitTarget, false);
            } else {
                onHitTarget(hitTarget, weapon, hit);
            }
        }

        hitTask = ThreadPool.schedule(() -> onAttackFinish(attack), attackTime - (hitTime1 + hitTime2));
    }

    public void onHitTarget(Creature hitTarget, Weapon weapon, Hit hit) {
        // reduce targets HP
        doAttack(hit.getDamage(), hitTarget, null, false, false, hit.isCritical(), false);

        // Notify to scripts when the attack has been done.
        EventDispatcher.getInstance().notifyEvent(new OnCreatureAttack(this, hitTarget, null), this);
        EventDispatcher.getInstance().notifyEvent(new OnCreatureAttacked(this, hitTarget, null), hitTarget);

        if (triggerSkills != null) {
            for (OptionsSkillHolder holder : triggerSkills.values()) {
                if ((!hit.isCritical() && (holder.getSkillType() == OptionsSkillType.ATTACK)) || ((holder.getSkillType() == OptionsSkillType.CRITICAL) && hit.isCritical())) {
                    if (Rnd.get(100) < holder.getChance()) {
                        SkillCaster.triggerCast(this, hitTarget, holder.getSkill(), null, false);
                    }
                }
            }
        }

        // Launch weapon Special ability effect if available
        if (hit.isCritical() && (weapon != null)) {
            weapon.applyConditionalSkills(this, hitTarget, null, ItemSkillType.ON_CRITICAL_SKILL);
        }
    }

    private void onAttackFinish(Attack attack) {
        // Recharge any active auto-soulshot tasks for current creature after the attack has successfully hit.
        if (attack.getHits().stream().anyMatch(h -> !h.isMiss())) {
            consumeAndRechargeShots(ShotType.SOULSHOTS, attack.getHitsWithSoulshotCount());
        }
        // Notify that this character is ready to act for the next attack
        getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
    }

    /**
     * Break an attack and send Server->Client ActionFailed packet and a System Message to the Creature.
     */
    public void breakAttack() {
        if (isAttackingNow()) {
            // Abort the attack of the Creature and send Server->Client ActionFailed packet
            abortAttack();
            if (isPlayer(this)) {
                // Send a system message
                sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
            }
        }
    }

    /**
     * Break a cast and send Server->Client ActionFailed packet and a System Message to the Creature.
     */
    public void breakCast() {
        // Break only one skill at a time while casting.
        final SkillCaster skillCaster = getSkillCaster(SkillCaster::isAnyNormalType);
        if ((skillCaster != null) && skillCaster.getSkill().isMagic()) {
            // Abort the cast of the Creature and send Server->Client MagicSkillCanceld/ActionFailed packet.
            skillCaster.stopCasting(true);

            if (isPlayer(this)) {
                // Send a system message
                sendPacket(SystemMessageId.YOUR_CASTING_HAS_BEEN_INTERRUPTED);
            }
        }
    }

    /**
     * Manage Forced attack (shift + select target).<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>If Creature or target is in a town area, send a system message TARGET_IN_PEACEZONE a Server->Client packet ActionFailed</li>
     * <li>If target is confused, send a Server->Client packet ActionFailed</li>
     * <li>If Creature is a Artefact, send a Server->Client packet ActionFailed</li>
     * <li>Send a Server->Client packet MyTargetSelected to start attack and Notify AI with AI_INTENTION_ATTACK</li>
     * </ul>
     *
     * @param player The Player to attack
     */
    @Override
    public void onForcedAttack(Player player) {
        if (isInsidePeaceZone(player) && !player.getAccessLevel().allowPeaceAttack()) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.getBlockCheckerArena() != -1 || player.isConfused() || !canBeAttacked() ) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!GeoEngine.getInstance().canSeeTarget(player, this)) {
            player.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Notify AI with AI_INTENTION_ATTACK
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
    }

    public boolean isInsidePeaceZone(WorldObject attacker) {
        return isInsidePeaceZone(attacker, this);
    }

    public boolean isInsidePeaceZone(WorldObject attacker, WorldObject target) {
        final Instance instanceWorld = getInstanceWorld();
        if (isNull(target) || !( GameUtils.isPlayable(target) && GameUtils.isPlayable(attacker)) || ((instanceWorld != null) && instanceWorld.isPvP())) {
            return false;
        }

        if (Config.ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE) {
            // allows red to be attacked and red to attack flagged players
            if ((target.getActingPlayer() != null) && (target.getActingPlayer().getReputation() < 0)) {
                return false;
            }
            if ((attacker.getActingPlayer() != null) && (attacker.getActingPlayer().getReputation() < 0) && (target.getActingPlayer() != null) && (target.getActingPlayer().getPvpFlag() > 0)) {
                return false;
            }
        }

        if ((attacker.getActingPlayer() != null) && attacker.getActingPlayer().getAccessLevel().allowPeaceAttack()) {
            return false;
        }

        return (target.isInsideZone(ZoneType.PEACE) || attacker.isInsideZone(ZoneType.PEACE));
    }

    /**
     * @return true if this character is inside an active grid.
     */
    public boolean isInActiveRegion() {
        final WorldRegion region = getWorldRegion();
        return ((region != null) && (region.isActive()));
    }

    /**
     * @return True if the Creature has a Party in progress.
     */
    public boolean isInParty() {
        return false;
    }

    /**
     * @return the Party object of the Creature.
     */
    public Party getParty() {
        return null;
    }

    @Override
    public Skill addSkill(Skill newSkill) {
        Skill oldSkill = null;
        if (nonNull(newSkill)) {
            oldSkill = skills.get(newSkill.getId());

            if(nonNull(oldSkill) && newSkill.getLevel() < oldSkill.getLevel()) {
                return oldSkill;
            }

            skills.put(newSkill.getId(), newSkill);

            if (nonNull(oldSkill)) {
                if (oldSkill.isPassive()) {
                    effectList.stopSkillEffects(true, oldSkill);
                }
            }

            if (newSkill.isPassive()) {
                newSkill.applyEffects(this, this, false, true, false, 0, null);
            }
        }
        return oldSkill;
    }

    public Skill removeSkill(Skill skill, boolean cancelEffect) {
        return (skill != null) ? removeSkill(skill.getId(), cancelEffect) : null;
    }

    public Skill removeSkill(int skillId) {
        return removeSkill(skillId, true);
    }

    public Skill removeSkill(int skillId, boolean cancelEffect) {
        // Remove the skill from the Creature _skills
        final Skill oldSkill = skills.remove(skillId);
        // Remove all its Func objects from the Creature calculator set
        if (oldSkill != null) {
            // Stop casting if this skill is used right now
            abortCast(s -> s.getSkill().getId() == skillId);

            // Stop effects.
            if (cancelEffect || oldSkill.isToggle() || oldSkill.isPassive()) {
                stopSkillEffects(true, oldSkill.getId());
                stats.recalculateStats(true);
            }
        }
        return oldSkill;
    }

    /**
     * @return all skills this creature currently has.
     */
    public final Collection<Skill> getAllSkills() {
        return skills.values();
    }

    /**
     * @return the map containing this character skills.
     */
    @Override
    public IntMap<Skill> getSkills() {
        return skills;
    }

    /**
     * Return the level of a skill owned by the Creature.
     *
     * @param skillId The identifier of the L2Skill whose level must be returned
     * @return The level of the L2Skill identified by skillId
     */
    @Override
    public int getSkillLevel(int skillId) {
        return zeroIfNullOrElse(getKnownSkill(skillId), Skill::getLevel);
    }

    /**
     * @param skillId The identifier of the L2Skill to check the knowledge
     * @return the skill from the known skill.
     */
    @Override
    public Skill getKnownSkill(int skillId) {
        return skills.get(skillId);
    }

    /**
     * Return the number of buffs affecting this Creature.
     *
     * @return The number of Buffs affecting this Creature
     */
    public int getBuffCount() {
        return effectList.getBuffCount();
    }

    public int getDanceCount() {
        return effectList.getDanceCount();
    }

    public void notifyQuestEventSkillFinished(Skill skill, WorldObject target) {

    }

    public double getLevelMod() {
        final double defaultLevelMod = ((getLevel() + 89) / 100d);
        return nonNull(transform) && !transform.isStance() ? transform.getLevelMod(this) : defaultLevelMod;
    }

    /**
     * Dummy value that gets overriden in Playable.
     *
     * @return 0
     */
    public byte getPvpFlag() {
        return 0;
    }

    public void updatePvPFlag(int value) {
        // Overridden in Player
    }

    /**
     * @return a multiplier based on weapon random damage
     */
    public final double getRandomDamageMultiplier() {
        final int random = (int) stats.getValue(Stat.RANDOM_DAMAGE);
        return (1 + ((double) Rnd.get(-random, random) / 100));
    }

    public final long getAttackEndTime() {
        return attackEndTime;
    }

    protected long getRangedAttackEndTime() {
        return disableRangedAttackEndTime;
    }

    public abstract int getLevel();

    public int getAccuracy() {
        return stats.getAccuracy();
    }

    public int getMagicAccuracy() {
        return stats.getMagicAccuracy();
    }

    public int getMagicEvasionRate() {
        return stats.getMagicEvasionRate();
    }

    public final double getAttackSpeedMultiplier() {
        return stats.getAttackSpeedMultiplier();
    }

    public final double getCriticalDmg(int init) {
        return stats.getCriticalDmg(init);
    }

    public int getCriticalHit() {
        return stats.getCriticalHit();
    }

    public int getEvasionRate() {
        return stats.getEvasionRate();
    }

    public final int getMagicalAttackRange(Skill skill) {
        return stats.getMagicalAttackRange(skill);
    }

    public final int getMaxCp() {
        return stats.getMaxCp();
    }

    public final int getMaxRecoverableCp() {
        return stats.getMaxRecoverableCp();
    }

    public int getMAtk() {
        return stats.getMAtk();
    }

    public int getMAtkSpd() {
        return stats.getMAtkSpd();
    }

    public int getMaxMp() {
        return stats.getMaxMp();
    }

    public int getMaxRecoverableMp() {
        return stats.getMaxRecoverableMp();
    }

    public int getMaxHp() {
        return stats.getMaxHp();
    }

    public int getMaxRecoverableHp() {
        return stats.getMaxRecoverableHp();
    }

    public final int getMCriticalHit() {
        return stats.getMCriticalHit();
    }

    public int getMDef() {
        return stats.getMDef();
    }

    public int getPAtk() {
        return stats.getPAtk();
    }

    public int getPAtkSpd() {
        return stats.getPAtkSpd();
    }

    public int getPDef() {
        return stats.getPDef();
    }

    public final int getPhysicalAttackRange() {
        return stats.getPhysicalAttackRange();
    }

    public double getMovementSpeedMultiplier() {
        return stats.getMovementSpeedMultiplier();
    }

    public double getRunSpeed() {
        return stats.getRunSpeed();
    }

    public double getWalkSpeed() {
        return stats.getWalkSpeed();
    }

    public final double getSwimRunSpeed() {
        return stats.getSwimRunSpeed();
    }

    public final double getSwimWalkSpeed() {
        return stats.getSwimWalkSpeed();
    }

    public double getMoveSpeed() {
        return stats.getMoveSpeed();
    }

    public final int getShldDef() {
        return stats.getShldDef();
    }

    public int getSTR() {
        return stats.getSTR();
    }

    public int getDEX() {
        return stats.getDEX();
    }

    public int getCON() {
        return stats.getCON();
    }

    public int getINT() {
        return stats.getINT();
    }

    public int getWIT() {
        return stats.getWIT();
    }

    public int getMEN() {
        return stats.getMEN();
    }

    // Status - NEED TO REMOVE ONCE L2CHARTATUS IS COMPLETE
    public void addStatusListener(Creature object) {
        status.addStatusListener(object);
    }

    public void doAttack(double damage, Creature target, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect) {
        // Start attack stance and notify being attacked.
        if (target.hasAI()) {
            target.getAI().clientStartAutoAttack();
            target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, this);
        }

        getAI().clientStartAutoAttack();

        if (!reflect && !isDOT) {
            // RearDamage effect bonus.
            if (isBehind(target)) {
                damage *= stats.getValue(Stat.REAR_DAMAGE_RATE, 1);
            }

            // Counterattacks happen before damage received.
            if (!target.isDead() && (skill != null)) {
                Formulas.calcCounterAttack(this, target, skill, true);

                // Shield Deflect Magic: Reflect all damage on caster.
                if (skill.isMagic() && (target.getStats().getValue(Stat.VENGEANCE_SKILL_MAGIC_DAMAGE, 0) > Rnd.get(100))) {
                    reduceCurrentHp(damage, target, skill, isDOT, directlyToHp, critical, true, DamageType.REFLECT);
                    return;
                }
            }
        }

        if (target.isImmobilized()) {
            damage *= stats.getValue(Stat.DAMAGE_IMMOBILIZED, 1);
            damage *= target.stats.getValue(Stat.DAMAGE_TAKEN_IMMOBILIZED, 1);
        }

        // Target receives the damage.
        target.reduceCurrentHp(damage, this, skill, isDOT, directlyToHp, critical, reflect, DamageType.ATTACK);

        // Check if damage should be reflected or absorbed. When killing blow is made, the target doesn't reflect (vamp too?).
        if (!reflect && !isDOT && !target.isDead()) {
            int reflectedDamage = 0;

            // Reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
            double reflectPercent = target.getStats().getValue(Stat.REFLECT_DAMAGE_PERCENT, 0) - getStats().getValue(Stat.REFLECT_DAMAGE_PERCENT_DEFENSE, 0);
            if (reflectPercent > 0)
            {
                reflectedDamage = (int) ((reflectPercent / 100.) * damage);
                reflectedDamage = Math.min(reflectedDamage, target.getMaxHp());

                // Reflected damage is limited by P.Def/M.Def
                if ((skill != null) && skill.isMagic())
                {
                    reflectedDamage = (int) Math.min(reflectedDamage, target.getStats().getMDef() * 1.5);
                }
                else
                {
                    reflectedDamage = Math.min(reflectedDamage, target.getStats().getPDef());
                }
            }

            double absorbPercent = getStats().getValue(Stat.ABSORB_DAMAGE_PERCENT, 0) * target.getStats().getValue(Stat.ABSORB_DAMAGE_DEFENCE, 1);
            if ((absorbPercent > 0) && (Rnd.nextDouble() < stats.getValue(Stat.ABSORB_DAMAGE_CHANCE)))
            {
                int absorbDamage = (int) Math.min(absorbPercent * damage, stats.getMaxRecoverableHp() - status.getCurrentHp());
                absorbDamage = Math.min(absorbDamage, (int) target.getCurrentHp());
                if (absorbDamage > 0)
                {
                    setCurrentHp(status.getCurrentHp() + absorbDamage);
                }
            }

            // Absorb MP from the damage inflicted.
            if (skill != null) // Classic: Used to reduce skill MP consumption. See Orfen's Earring.
            {
                if (Rnd.get(10) < 3) // Classic: Static 30% change.
                {
                    absorbPercent = stats.getValue(Stat.ABSORB_MANA_DAMAGE_PERCENT, 0);
                    if (absorbPercent > 0)
                    {
                        int absorbDamage = (int) Math.min((absorbPercent / 100.) * damage, stats.getMaxRecoverableMp() - status.getCurrentMp());
                        absorbDamage = Math.min(absorbDamage, (int) target.getCurrentMp());
                        if (absorbDamage > 0)
                        {
                            setCurrentMp(status.getCurrentMp() + absorbDamage);
                        }
                    }
                }
            }

            if (reflectedDamage > 0)
            {
                target.doAttack(reflectedDamage, this, skill, isDOT, directlyToHp, critical, true);
            }
        }

        // Break casting of target during attack.
        if (!target.isRaid() && Formulas.calcAtkBreak(target, damage)) {
            target.breakAttack();
            target.breakCast();
        }
    }

    public void reduceCurrentHp(double value, Creature attacker, Skill skill, DamageType damageType) {
        reduceCurrentHp(value, attacker, skill, false, false, false, false, damageType);
    }

    public void reduceCurrentHp(double value, Creature attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect, DamageType damageType) {
        double elementalDamage = 0;

        // Calculate PvP/PvE damage received. It is a post-attack stat.
        if (nonNull(attacker)) {

            if (GameUtils.isPlayable(attacker) && GameUtils.isPlayable(this)) {
                value *= (100 + max(stats.getValue(Stat.PVP_DAMAGE_TAKEN), -80)) / 100;

            } else if (attacker.isRaid() || attacker.isRaidMinion()) {
                value *= (100 + max(stats.getValue(Stat.PVE_DAMAGE_TAKEN_RAID), -80)) / 100;
                value *= (100 + max(stats.getValue(Stat.PVE_DAMAGE_TAKEN), -80)) / 100;

            } else if (isMonster(attacker)) {
                value *= (100 + max(stats.getValue(Stat.PVE_DAMAGE_TAKEN_MONSTER), -80)) / 100;
                value *= (100 + max(stats.getValue(Stat.PVE_DAMAGE_TAKEN), -80)) / 100;
            }

            value *= (100 + max(stats.getValue(Stat.DAMAGE_TAKEN), -80)) / 100;

            if(!reflect && !isDOT) {
                elementalDamage = Formulas.calcSpiritElementalDamage(attacker, this, value);
            }

            value += elementalDamage;
        }

        if(Config.CHAMPION_ENABLE && isChampion() && Config.CHAMPION_HP > 0) {
            value /= Config.CHAMPION_HP;
        }

        final double damageCap = stats.getValue(Stat.DAMAGE_LIMIT);
        if (damageCap > 0) {
            value = Math.min(value, damageCap);
        }

        value = max(0, value);

        final var damageReturn = EventDispatcher.getInstance().notifyEvent(new OnCreatureDamageReceived(attacker, this, value, skill, critical, isDOT, reflect), this, DamageReturn.class);
        if (damageReturn != null) {
            if (damageReturn.terminate()) {
                return;
            } else if (damageReturn.override()) {
                value = damageReturn.getDamage();
            }
        }

        onReceiveDamage(attacker, skill, value, damageType);
        if (isPlayer(this)) {
            getActingPlayer().getStatus().reduceHp(value, attacker, isNull(skill) || !skill.isToggle(), isDOT, false, directlyToHp);
        } else {
            status.reduceHp(value, attacker, isNull(skill) || !skill.isToggle(), isDOT, false);
        }

        if (nonNull(attacker)) {
            attacker.sendDamageMessage(this, skill, (int) value, elementalDamage, critical, false);
            EventDispatcher.getInstance().notifyEventAsync(new OnCreatureDamageDealt(attacker, this, value, skill, critical, isDOT, reflect), attacker);
        }
    }

    protected void onReceiveDamage(Creature attacker, Skill skill, double value, DamageType damageType) {

    }

    public void reduceCurrentMp(double i) {
        status.reduceMp(i);
    }

    @Override
    public void removeStatusListener(Creature object) {
        status.removeStatusListener(object);
    }

    public void stopHpMpRegeneration() {
        status.stopHpMpRegeneration();
    }

    public final double getCurrentCp() {
        return status.getCurrentCp();
    }

    public final void setCurrentCp(double newCp) {
        status.setCurrentCp(newCp);
    }

    public final int getCurrentCpPercent() {
        return (int) ((status.getCurrentCp() * 100) / stats.getMaxCp());
    }

    public final void setCurrentCp(double newCp, boolean broadcast) {
        status.setCurrentCp(newCp, broadcast);
    }

    public final double getCurrentHp() {
        return status.getCurrentHp();
    }

    public final void setCurrentHp(double newHp) {
        status.setCurrentHp(newHp);
    }

    public final int getCurrentHpPercent() {
        return (int) ((status.getCurrentHp() * 100) / stats.getMaxHp());
    }

    public final void setCurrentHp(double newHp, boolean broadcast) {
        status.setCurrentHp(newHp, broadcast);
    }

    public final void setCurrentHpMp(double newHp, double newMp) {
        status.setCurrentHpMp(newHp, newMp);
    }

    public final double getCurrentMp() {
        return status.getCurrentMp();
    }

    public final void setCurrentMp(double newMp) {
        status.setCurrentMp(newMp);
    }

    public final int getCurrentMpPercent() {
        return (int) ((status.getCurrentMp() * 100) / stats.getMaxMp());
    }

    public final void setCurrentMp(double newMp, boolean broadcast) {
        status.setCurrentMp(newMp, broadcast);
    }

    /**
     * @return the max weight that the Creature can load.
     */
    public int getMaxLoad() {
        if (isPlayer(this) || isPet(this)) {
            // Source http://l2p.bravehost.com/weightlimit.html (May 2007)
            final double baseLoad = Math.floor(BaseStats.CON.calcBonus(this) * 69000 * getSettings(CharacterSettings.class).weightLimitMultiplier());
            return (int) stats.getValue(Stat.WEIGHT_LIMIT, baseLoad);
        }
        return 0;
    }

    public int getBonusWeightPenalty() {
        if (isPlayer(this) || isPet(this)) {
            return (int) stats.getValue(Stat.WEIGHT_PENALTY, 1);
        }
        return 0;
    }

    /**
     * @return the current weight of the Creature.
     */
    public int getCurrentLoad() {
        if (isPlayer(this) || isPet(this)) {
            return getInventory().getTotalWeight();
        }
        return 0;
    }

    public boolean isChampion() {
        return false;
    }

    public void sendDamageMessage(Creature target, Skill skill, int damage, double elementalDamage, boolean crit, boolean miss) {
        // default
    }

    public AttributeType getAttackElement() {
        return stats.getAttackElement();
    }

    public int getAttackElementValue(AttributeType attackAttribute) {
        return stats.getAttackElementValue(attackAttribute);
    }

    public int getDefenseElementValue(AttributeType defenseAttribute) {
        return stats.getDefenseElementValue(defenseAttribute);
    }

    public final void startPhysicalAttackMuted() {
        abortAttack();
    }

    public void disableCoreAI(boolean val) {
        aiDisabled = val;
    }

    public boolean isCoreAIDisabled() {
        return aiDisabled;
    }

    /**
     * @return true
     */
    public boolean giveRaidCurse() {
        return true;
    }

    /**
     * Check if target is affected with special buff
     *
     * @param flag int
     * @return boolean
     * @see EffectList#isAffected(EffectFlag)
     */
    public boolean isAffected(EffectFlag flag) {
        return effectList.isAffected(flag);
    }

    public void broadcastSocialAction(int id) {
        broadcastPacket(new SocialAction(getObjectId(), id));
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void addOverrideCond(PcCondOverride... excs) {
        for (PcCondOverride exc : excs) {
            exceptions |= exc.getMask();
        }
    }

    public void removeOverridedCond(PcCondOverride... excs) {
        for (PcCondOverride exc : excs) {
            exceptions &= ~exc.getMask();
        }
    }

    public boolean canOverrideCond(PcCondOverride excs) {
        return (exceptions & excs.getMask()) == excs.getMask();
    }

    public long getOverrideCond() {
        return exceptions;
    }

    public void setOverrideCond(long masks) {
        exceptions = masks;
    }

    public boolean isLethalable() {
        return lethalable;
    }

    public void setLethalable(boolean val) {
        lethalable = val;
    }

    public void forEachTriggerSkill(Consumer<OptionsSkillHolder> action) {
        if(nonNull(triggerSkills)) {
            triggerSkills.values().forEach(action);
        }
    }

    public void addTriggerSkill(OptionsSkillHolder holder) {
        triggerSkills().put(holder.getSkillId(), holder);
    }

    private synchronized IntMap<OptionsSkillHolder> triggerSkills() {
        if(isNull(triggerSkills)) {
            triggerSkills = new CHashIntMap<>();
        }
        return triggerSkills;
    }
    public void removeTriggerSkill(OptionsSkillHolder holder) {
        if(nonNull(triggerSkills)) {
            triggerSkills.remove(holder.getSkillId());
        }
    }

    /**
     * Dummy method overriden in {@link Player}
     *
     * @return {@code true} if current player can revive and shows 'To Village' button upon death, {@code false} otherwise.
     */
    public boolean canRevive() {
        return true;
    }

    public void setCanRevive(boolean val) {
    }

    /**
     * Dummy method overriden in {@link Attackable}
     *
     * @return {@code true} if there is a loot to sweep, {@code false} otherwise.
     */
    public boolean isSweepActive() {
        return false;
    }

    /**
     * Dummy method overriden in {@link Player}
     *
     * @return {@code true} if player is on event, {@code false} otherwise.
     */
    public boolean isOnEvent() {
        return false;
    }

    /**
     * Dummy method overriden in {@link Player}
     *
     * @return the clan id of current character.
     */
    public int getClanId() {
        return 0;
    }

    /**
     * Dummy method overriden in {@link Player}
     *
     * @return the clan of current character.
     */
    public Clan getClan() {
        return null;
    }

    /**
     * Dummy method overriden in {@link Player}
     *
     * @return {@code true} if player is in academy, {@code false} otherwise.
     */
    public boolean isAcademyMember() {
        return false;
    }

    /**
     * Dummy method overriden in {@link Player}
     *
     * @return the pledge type of current character.
     */
    public int getPledgeType() {
        return 0;
    }

    /**
     * Dummy method overriden in {@link Player}
     *
     * @return the alliance id of current character.
     */
    public int getAllyId() {
        return 0;
    }

    public void notifyAttackAvoid(Creature target, boolean isDot) {
        EventDispatcher.getInstance().notifyEventAsync(new OnCreatureAttackAvoid(this, target, isDot), target);
    }

    /**
     * @return {@link WeaponType} of current character's weapon or basic weapon type.
     */
    public final WeaponType getAttackType() {
        final Weapon weapon = getActiveWeaponItem();
        if (weapon != null) {
            return weapon.getItemType();
        }

        final WeaponType defaultWeaponType = template.getBaseAttackType();
        return nonNull(transform) ? transform.getBaseAttackType(this, defaultWeaponType) : defaultWeaponType;
    }

    public final boolean isInCategory(CategoryType type) {
        return CategoryManager.getInstance().isInCategory(type, this);
    }

    /**
     * @return the character that summoned this NPC.
     */
    public Creature getSummoner() {
        return summoner;
    }

    /**
     * @param summoner the summoner of this NPC.
     */
    public void setSummoner(Creature summoner) {
        this.summoner = summoner;
    }

    /**
     * Adds a summoned NPC.
     *
     * @param npc the summoned NPC
     */
    public final synchronized void addSummonedNpc(Npc npc) {
        if (summonedNpcs == null) {
            summonedNpcs = new CHashIntMap<>();
        }
        summonedNpcs.put(npc.getObjectId(), npc);
    }

    /**
     * Removes a summoned NPC by object ID.
     *
     * @param objectId the summoned NPC object ID
     */
    public final void removeSummonedNpc(int objectId) {
        if (summonedNpcs != null) {
            summonedNpcs.remove(objectId);
        }
    }

    /**
     * Gets the summoned NPCs.
     *
     * @return the summoned NPCs
     */
    public final Collection<Npc> getSummonedNpcs() {
        return summonedNpcs != null ? summonedNpcs.values() : Collections.emptyList();
    }

    /**
     * Resets the summoned NPCs list.
     */
    public final void resetSummonedNpcs() {
        if (summonedNpcs != null) {
            summonedNpcs.clear();
        }
    }

    public Collection<SkillCaster> getSkillCasters() {
        return skillCasters.values();
    }

    public SkillCaster addSkillCaster(SkillCastingType castingType, SkillCaster skillCaster) {
        return skillCasters.put(castingType, skillCaster);
    }

    public void removeSkillCaster(SkillCastingType castingType) {
        skillCasters.remove(castingType);
    }

    @SafeVarargs
    public final SkillCaster getSkillCaster(Predicate<SkillCaster> filter, Predicate<SkillCaster>... filters) {
        for (Predicate<SkillCaster> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }

        return skillCasters.values().stream().filter(filter).findAny().orElse(null);
    }

    /**
     * @return {@code true} if current character is casting channeling skill, {@code false} otherwise.
     */
    public final boolean isChanneling() {
        return (channelizer != null) && channelizer.isChanneling();
    }

    public final SkillChannelizer getSkillChannelizer() {
        if (channelizer == null) {
            channelizer = new SkillChannelizer(this);
        }
        return channelizer;
    }

    /**
     * @return {@code true} if current character is affected by channeling skill, {@code false} otherwise.
     */
    public final boolean isChannelized() {
        return (channelized != null) && !channelized.isChannelized();
    }

    public final SkillChannelized getSkillChannelized() {
        if (channelized == null) {
            channelized = new SkillChannelized();
        }
        return channelized;
    }

    @Override
    public Queue<AbstractEventListener> getListeners(EventType type) {
        final Queue<AbstractEventListener> objectListenres = super.getListeners(type);
        final Queue<AbstractEventListener> templateListeners = template.getListeners(type);
        final Queue<AbstractEventListener> globalListeners = GameUtils.isNpc(this) && !isMonster(this) ? Listeners.Npcs().getListeners(type) : isMonster(this) ? Listeners.Monsters().getListeners(type) : isPlayer(this) ? Listeners.players().getListeners(type) : EmptyQueue.emptyQueue();

        // Attempt to do not create collection
        if (objectListenres.isEmpty() && templateListeners.isEmpty() && globalListeners.isEmpty()) {
            return EmptyQueue.emptyQueue();
        } else if (!objectListenres.isEmpty() && templateListeners.isEmpty() && globalListeners.isEmpty()) {
            return objectListenres;
        } else if (!templateListeners.isEmpty() && objectListenres.isEmpty() && globalListeners.isEmpty()) {
            return templateListeners;
        } else if (!globalListeners.isEmpty() && objectListenres.isEmpty() && templateListeners.isEmpty()) {
            return globalListeners;
        }

        final Queue<AbstractEventListener> both = new LinkedBlockingDeque<>(objectListenres.size() + templateListeners.size() + globalListeners.size());
        both.addAll(objectListenres);
        both.addAll(templateListeners);
        both.addAll(globalListeners);
        return both;
    }

    public Race getRace() {
        return template.getRace();
    }

    @Override
    public final void setXYZ(int newX, int newY, int newZ) {
        final ZoneRegion oldZoneRegion = ZoneManager.getInstance().getRegion(this);
        final ZoneRegion newZoneRegion = ZoneManager.getInstance().getRegion(newX, newY);

        // Mobius: Prevent moving to nonexistent regions.
        if (newZoneRegion == null) {
            return;
        }

        if (oldZoneRegion != newZoneRegion) {
            oldZoneRegion.removeFromZones(this);
            newZoneRegion.revalidateZones(this);
        }

        super.setXYZ(newX, newY, newZ);
    }

    public final IntIntMap getKnownRelations() {
        return knownRelations;
    }

    @Override
    public boolean isTargetable() {
        return super.isTargetable() && !isAffected(EffectFlag.UNTARGETABLE);
    }

    public boolean isTargetingDisabled() {
        return isAffected(EffectFlag.TARGETING_DISABLED);
    }

    public boolean cannotEscape() {
        return isAffected(EffectFlag.CANNOT_ESCAPE);
    }

    /**
     * @return the amount of debuffs that player can avoid
     */
    public int getAbnormalShieldBlocks() {
        return abnormalShieldBlocks.get();
    }

    /**
     * Sets amount of debuffs that player can avoid
     */
    public void setAbnormalShieldBlocks(int amount) {
        abnormalShieldBlocks.set(amount);
    }

    public void decrementAbnormalShieldBlocks() {
        abnormalShieldBlocks.decrementAndGet();
    }

    public boolean hasAbnormalType(AbnormalType abnormalType) {
        return effectList.hasAbnormalType(abnormalType);
    }

    public void addBlockActionsAllowedSkill(int skillId) {
        blockActionsAllowedSkills.computeIfAbsent(skillId, k -> new AtomicInteger()).incrementAndGet();
    }

    public void removeBlockActionsAllowedSkill(int skillId) {
        blockActionsAllowedSkills.computeIfPresent(skillId, (k, v) -> v.decrementAndGet() != 0 ? v : null);
    }

    public boolean isBlockedActionsAllowedSkill(Skill skill) {
        return blockActionsAllowedSkills.containsKey(skill.getId());
    }

    public MoveType getMoveType() {
        if (isMoving() && running) {
            return MoveType.RUNNING;
        } else if (isMoving()) {
            return MoveType.WALKING;
        }
        return MoveType.STANDING;
    }

    protected final void computeStatusUpdate(StatusUpdate su, StatusUpdateType type) {
        final int newValue = type.getValue(this);
        statusUpdates.compute(type, (key, oldValue) ->
        {
            if ((oldValue == null) || (oldValue != newValue)) {
                su.addUpdate(type, newValue);
                return newValue;
            }
            return oldValue;
        });
    }

    protected final void addStatusUpdateValue(StatusUpdateType type) {
        statusUpdates.put(type, type.getValue(this));
    }

    protected void initStatusUpdateCache() {
        addStatusUpdateValue(StatusUpdateType.MAX_HP);
        addStatusUpdateValue(StatusUpdateType.MAX_MP);
        addStatusUpdateValue(StatusUpdateType.CUR_HP);
        addStatusUpdateValue(StatusUpdateType.CUR_MP);
    }

    /**
     * Checks if the creature has basic property resist towards mesmerizing debuffs.
     *
     * @return {@code true}.
     */
    public boolean hasBasicPropertyResist() {
        return true;
    }

    /**
     * Gets the basic property resist.
     *
     * @param basicProperty the basic property
     * @return the basic property resist
     */
    public BasicPropertyResist getBasicPropertyResist(BasicProperty basicProperty) {
        return basicPropertyResists.computeIfAbsent(basicProperty, k -> new BasicPropertyResist());
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public boolean isChargedShot(ShotType type) {
        return chargedShots.containsKey(type);
    }

    public void chargeShot(ShotType type, double bonus) {
        chargedShots.put(type, bonus);
    }

    public double chargedShotBonus(ShotType type) {
        return chargedShots.getOrDefault(type, 1d);
    }

    public void unchargeShot(ShotType type) {
        chargedShots.remove(type);
    }

    public void unchargeAllShots() {
        chargedShots.clear();
    }

    public void consumeAndRechargeShots(ShotType shotType, int targets) {

    }

    public void setCursorKeyMovement(boolean value) {
        cursorKeyMovement = value;
    }

    public boolean isCursorKeyMovementActive() {
        return cursorKeyMovementActive;
    }

    public void setCursorKeyMovementActive(boolean value) {
        cursorKeyMovementActive = value;
    }

    public double getElementalSpiritDefenseOf(ElementalType type) {
        return getElementalSpiritType() == type ? 100 : 0;
    }

    public ElementalType getElementalSpiritType() {
        return ElementalType.NONE;
    }

    public void addBuffInfoTime(BuffInfo info)
    {
        if (buffFinishTask == null)
        {
            buffFinishTask = new BuffFinishTask();
        }
        buffFinishTask.addBuffInfo(info);
    }

    public void removeBuffInfoTime(BuffInfo info)
    {
        if (buffFinishTask != null)
        {
            buffFinishTask.removeBuffInfo(info);
        }
    }

    public void cancelBuffFinishTask()
    {
        if (buffFinishTask != null)
        {
            final ScheduledFuture<?> task = buffFinishTask.getTask();
            if ((task != null) && !task.isCancelled() && !task.isDone())
            {
                task.cancel(true);
            }
            buffFinishTask = null;
        }
    }

    public int getBuffRemainTimeBySkillOrAbormalType(Skill skill) {
        return effectList.remainTimeBySkillIdOrAbnormalType(skill.getId(), skill.getAbnormalType());
    }

    /**
     * This class group all movement data.<br>
     * <B><U> Data</U> :</B>
     * <ul>
     * <li>_moveTimestamp : Last time position update</li>
     * <li>_xDestination, _yDestination, _zDestination : Position of the destination</li>
     * <li>_xMoveFrom, _yMoveFrom, _zMoveFrom : Position of the origin</li>
     * <li>_moveStartTime : Start time of the movement</li>
     * <li>_ticksToMove : Number of ticks between the start and the destination</li>
     * <li>_xSpeedTicks, _ySpeedTicks : Speed in unit/ticks</li>
     * </ul>
     */
    public static class MoveData {
        // when we retrieve x/y/z we use GameTimeControl.getGameTicks()
        // if we are moving, but move timestamp==gameticks, we don't need
        // to recalculate position
        public int _moveStartTime;
        public int _moveTimestamp; // last update
        public int _xDestination;
        public int _yDestination;
        public int _zDestination;
        public double _xAccurate; // otherwise there would be rounding errors
        public double _yAccurate;
        public int _heading;

        public boolean disregardingGeodata;
        public int onGeodataPathIndex;
        public List<Location> geoPath;
        public int geoPathAccurateTx;
        public int geoPathAccurateTy;
        public int geoPathGtx;
        public int geoPathGty;
    }
}
