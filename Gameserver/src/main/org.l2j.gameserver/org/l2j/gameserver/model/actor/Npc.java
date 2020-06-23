/*
 * Copyright © 2019-2020 L2JOrg
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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ItemsAutoDestroy;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.*;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.instancemanager.RaidBossStatus;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.model.actor.stat.NpcStats;
import org.l2j.gameserver.model.actor.status.NpcStatus;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.*;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.model.variables.NpcVariables;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.settings.RateSettings;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.TaxZone;

import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * This class represents a Non-Player-Character in the world.<br>
 * It can be a monster or a friendly character.<br>
 * It uses a template to fetch some static values.
 */
public class Npc extends Creature {
    /**
     * The interaction distance of the Folk(is used as offset in MovetoLocation method)
     */
    public static final int INTERACTION_DISTANCE = 250;
    /**
     * Maximum distance where the drop may appear given this NPC position.
     */
    public static final int RANDOM_ITEM_DROP_LIMIT = 70;
    /**
     * Minimum interval between social packets
     */
    private static final int MINIMUM_SOCIAL_INTERVAL = 6000;
    private static final String OLYMPIAD_HTML_PATH = "data/html/olympiad/";
    private final boolean _isQuestMonster = getTemplate().isQuestMonster();
    /**
     * The Spawn object that manage this Folk
     */
    private Spawn _spawn;
    /**
     * The flag to specify if this Folk is busy
     */
    private boolean _isBusy = false;
    /**
     * True if endDecayTask has already been called
     */
    private volatile boolean _isDecayed = false;
    /**
     * True if this Npc is autoattackable
     **/
    private boolean _isAutoAttackable = false;
    /**
     * Time of last social packet broadcast
     */
    private long _lastSocialBroadcast = 0;
    /**
     * Support for random animation switching
     */
    private boolean _isRandomAnimationEnabled = true;
    private boolean _isRandomWalkingEnabled = true;
    private boolean _isTalkable = getTemplate().isTalkable();
    private int _currentLHandId; // normally this shouldn't change from the template, but there exist exceptions
    private int _currentRHandId; // normally this shouldn't change from the template, but there exist exceptions
    private int _currentEnchant; // normally this shouldn't change from the template, but there exist exceptions
    private double _currentCollisionHeight; // used for npc grow effect skills
    private double _currentCollisionRadius; // used for npc grow effect skills

    private int _soulshotamount = 0;
    private int _spiritshotamount = 0;
    private int _displayEffect = 0;

    private int _killingBlowWeaponId;

    private int _cloneObjId; // Used in NpcInfo packet to clone the specified player.
    private int _clanId; // Used in NpcInfo packet to show the specified clan.

    private NpcStringId _titleString;
    private NpcStringId _nameString;

    private StatsSet _params;
    private RaidBossStatus _raidStatus;

    /**
     * Contains information about local tax payments.
     */
    private TaxZone _taxZone = null;
    private int scriptValue = 0;

    /**
     * Constructor of Folk (use Creature constructor).<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Call the Creature constructor to set the _template of the Creature (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
     * <li>Set the name of the Creature</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
     * </ul>
     *
     * @param template The NpcTemplate to apply to the NPC
     */
    public Npc(NpcTemplate template) {
        // Call the Creature constructor to set the _template of the Creature, copy skills from template to object
        // and link _calculators to NPC_STD_CALCULATOR
        super(template);
        setInstanceType(InstanceType.L2Npc);
        initCharStatusUpdateValues();
        setTargetable(getTemplate().isTargetable());

        // initialize the "current" equipment
        _currentLHandId = getTemplate().getLHandId();
        _currentRHandId = getTemplate().getRHandId();
        _currentEnchant = Config.ENABLE_RANDOM_ENCHANT_EFFECT ? Rnd.get(4, 21) : getTemplate().getWeaponEnchant();

        // initialize the "current" collisions
        _currentCollisionHeight = getTemplate().getfCollisionHeight();
        _currentCollisionRadius = getTemplate().getfCollisionRadius();

        setIsFlying(template.isFlying());
        initStatusUpdateCache();
    }

    /**
     * Send a packet SocialAction to all Player in the _KnownPlayers of the Folk and create a new RandomAnimation Task.
     *
     * @param animationId
     */
    public void onRandomAnimation(int animationId) {
        // Send a packet SocialAction to all Player in the _KnownPlayers of the Folk
        final long now = System.currentTimeMillis();
        if ((now - _lastSocialBroadcast) > MINIMUM_SOCIAL_INTERVAL) {
            _lastSocialBroadcast = now;
            broadcastPacket(new SocialAction(getObjectId(), animationId));
        }
    }

    /**
     * @return true if the server allows Random Animation.
     */
    public boolean hasRandomAnimation() {
        return ((Config.MAX_NPC_ANIMATION > 0) && _isRandomAnimationEnabled && (getAiType() != AIType.CORPSE));
    }

    /**
     * Switches random Animation state into val.
     *
     * @param val needed state of random animation
     */
    public void setRandomAnimation(boolean val) {
        _isRandomAnimationEnabled = val;
    }

    /**
     * @return {@code true}, if random animation is enabled, {@code false} otherwise.
     */
    public boolean isRandomAnimationEnabled() {
        return _isRandomAnimationEnabled;
    }

    public void setRandomWalking(boolean enabled) {
        _isRandomWalkingEnabled = enabled;
    }

    public boolean isRandomWalkingEnabled() {
        return _isRandomWalkingEnabled;
    }

    @Override
    public NpcStats getStats() {
        return (NpcStats) super.getStats();
    }

    @Override
    public void initCharStat() {
        setStat(new NpcStats(this));
    }

    @Override
    public NpcStatus getStatus() {
        return (NpcStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new NpcStatus(this));
    }

    /**
     * Return the NpcTemplate of the Folk.
     */
    @Override
    public final NpcTemplate getTemplate() {
        return (NpcTemplate) super.getTemplate();
    }

    /**
     * Gets the NPC ID.
     *
     * @return the NPC ID
     */
    @Override
    public int getId() {
        return getTemplate().getId();
    }

    @Override
    public boolean canBeAttacked() {
        return Config.ALT_ATTACKABLE_NPCS;
    }

    /**
     * Return the Level of this Folk contained in the NpcTemplate.
     */
    @Override
    public final int getLevel() {
        return getTemplate().getLevel();
    }

    /**
     * @return false.
     */
    public boolean isAggressive() {
        return false;
    }

    /**
     * @return the Aggro Range of this Folk either contained in the NpcTemplate, or overriden by spawnlist AI value.
     */
    public int getAggroRange() {
        return getTemplate().getAggroRange();
    }

    /**
     * @param npc
     * @return if both npcs have the same clan by template.
     */
    public boolean isInMyClan(Npc npc) {
        return getTemplate().isClan(npc.getTemplate().getClans());
    }

    /**
     * Return True if this Folk is undead in function of the NpcTemplate.
     */
    @Override
    public boolean isUndead() {
        return getTemplate().getRace() == Race.UNDEAD;
    }

    /**
     * Send a packet NpcInfo with state of abnormal effect to all Player in the _KnownPlayers of the Folk.
     */
    @Override
    public void updateAbnormalVisualEffects() {
        World.getInstance().forEachVisibleObject(this, Player.class, player ->
        {
            if (!isVisibleFor(player)) {
                return;
            }

            if (getRunSpeed() == 0) {
                player.sendPacket(new ServerObjectInfo(this, player));
            } else {
                player.sendPacket(new NpcInfoAbnormalVisualEffect(this));
            }
        });
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (attacker == null) {
            return false;
        }

        if (!isTargetable()) {
            return false;
        }

        if (GameUtils.isAttackable(attacker)) {
            if (isInMyClan((Npc) attacker)) {
                return false;
            }

            // Chaos NPCs attack everything except clan.
            if (((NpcTemplate) attacker.getTemplate()).isChaos()) {
                return true;
            }

            // Usually attackables attack everything they hate.
            return ((Attackable) attacker).getHating(this) > 0;
        }

        return _isAutoAttackable;
    }

    public void setAutoAttackable(boolean flag) {
        _isAutoAttackable = flag;
    }

    /**
     * @return the Identifier of the item in the left hand of this Folk contained in the NpcTemplate.
     */
    public int getLeftHandItem() {
        return _currentLHandId;
    }

    /**
     * @return the Identifier of the item in the right hand of this Folk contained in the NpcTemplate.
     */
    public int getRightHandItem() {
        return _currentRHandId;
    }

    public int getEnchantEffect() {
        return _currentEnchant;
    }

    /**
     * @return the busy status of this Folk.
     */
    public final boolean isBusy() {
        return _isBusy;
    }

    /**
     * @param isBusy the busy status of this Npc
     */
    public void setBusy(boolean isBusy) {
        _isBusy = isBusy;
    }

    /**
     * @return true if this Npc instance can be warehouse manager.
     */
    public boolean isWarehouse() {
        return false;
    }

    public boolean canTarget(Player player) {
        if (player.isControlBlocked()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (player.isLockedTarget() && (player.getLockedTarget() != this)) {
            player.sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        // TODO: More checks...

        return true;
    }

    public boolean canInteract(Player player) {
        if (player.isCastingNow()) {
            return false;
        } else if (player.isDead() || player.isFakeDeath()) {
            return false;
        } else if (player.isSitting()) {
            return false;
        } else if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            return false;
        } else if (!MathUtil.isInsideRadius3D(this, player, INTERACTION_DISTANCE)) {
            return false;
        } else if (player.getInstanceWorld() != getInstanceWorld()) {
            return false;
        } else if (_isBusy) {
            return false;
        }
        return true;
    }

    /**
     * Set another tax zone which will be used for tax payments.
     *
     * @param zone newly entered tax zone
     */
    public final void setTaxZone(TaxZone zone) {
        _taxZone = ((zone != null) && !isInInstance()) ? zone : null;
    }

    /**
     * Gets castle for tax payments.
     *
     * @return instance of {@link Castle} when NPC is inside {@link TaxZone} otherwise {@code null}
     */
    public final Castle getTaxCastle() {
        return (_taxZone != null) ? _taxZone.getCastle() : null;
    }

    /**
     * Gets castle tax rate
     *
     * @param type type of tax
     * @return tax rate when NPC is inside tax zone otherwise {@code 0}
     */
    public final double getCastleTaxRate(TaxType type) {
        final Castle castle = getTaxCastle();
        return (castle != null) ? (castle.getTaxPercent(type) / 100.0) : 0;
    }

    /**
     * Increase castle vault by specified tax amount.
     *
     * @param amount tax amount
     */
    public final void handleTaxPayment(long amount) {
        final Castle taxCastle = getTaxCastle();
        if (taxCastle != null) {
            taxCastle.addToTreasury(amount);
        }
    }

    /**
     * @return the nearest L2Castle this Folk belongs to. Otherwise null.
     */
    public final Castle getCastle() {
        return CastleManager.getInstance().findNearestCastle(this);
    }

    public final ClanHall getClanHall() {
        return ClanHallManager.getInstance().getClanHallByNpcId(getId());
    }

    /**
     * Return closest castle in defined distance
     *
     * @param maxDistance long
     * @return Castle
     */
    public final Castle getCastle(long maxDistance) {
        return CastleManager.getInstance().findNearestCastle(this, maxDistance);
    }

    /**
     * Open a quest or chat window on client with the text of the Folk in function of the command.<br>
     * <B><U> Example of use </U> :</B>
     * <ul>
     * <li>Client packet : RequestBypassToServer</li>
     * </ul>
     *
     * @param player
     * @param command The command string received from client
     */
    public void onBypassFeedback(Player player, String command) {
        if (canInteract(player))
        {
            final IBypassHandler handler = BypassHandler.getInstance().getHandler(command);
            if (handler != null) {
                handler.useBypass(command, player, this);
            } else {
                LOGGER.info(getClass().getSimpleName() + ": Unknown NPC bypass: \"" + command + "\" NpcId: " + getId());
            }
        }
    }

    /**
     * Return null (regular NPCs don't have weapons instances).
     */
    @Override
    public Item getActiveWeaponInstance() {
        return null;
    }

    /**
     * Return the weapon item equipped in the right hand of the Folk or null.
     */
    @Override
    public Weapon getActiveWeaponItem() {
        return null;
    }

    /**
     * Return null (regular NPCs don't have weapons instances).
     */
    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }

    /**
     * Return the weapon item equipped in the left hand of the Folk or null.
     */
    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }

    /**
     * <B><U Format of the pathfile</U>:</B>
     * <ul>
     * <li>if the file exists on the server (page number = 0) : <B>data/html/default/12006.htm</B> (npcId-page number)</li>
     * <li>if the file exists on the server (page number > 0) : <B>data/html/default/12006-1.htm</B> (npcId-page number)</li>
     * <li>if the file doesn't exist on the server : <B>data/html/npcdefault.htm</B> (message : "I have nothing to say to you")</li>
     * </ul>
     *
     * @param npcId The Identifier of the Folk whose text must be display
     * @param val   The number of the page to display
     * @return the pathfile of the selected HTML file in function of the npcId and of the page number.
     */
    public String getHtmlPath(int npcId, int val) {
        String pom = "";

        if (val == 0) {
            pom = Integer.toString(npcId);
        } else {
            pom = npcId + "-" + val;
        }

        final String temp = "data/html/default/" + pom + ".htm";

        if (HtmCache.getInstance().isLoadable(temp)) {
            return temp;
        }

        // If the file is not found, the standard message "I have nothing to say to you" is returned
        return "data/html/npcdefault.htm";
    }

    public void showChatWindow(Player player) {
        showChatWindow(player, 0);
    }

    /**
     * Returns true if html exists
     *
     * @param player
     * @param type
     * @return boolean
     */
    private boolean showPkDenyChatWindow(Player player, String type) {
        String html = HtmCache.getInstance().getHtm(player, "data/html/" + type + "/" + getId() + "-pk.htm");
        if (html != null) {
            html = html.replaceAll("%objectId%", String.valueOf(getObjectId()));
            player.sendPacket(new NpcHtmlMessage(getObjectId(), html));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return true;
        }
        return false;
    }

    /**
     * Open a chat window on client with the text of the Folk.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Get the text of the selected HTML file in function of the npcId and of the page number</li>
     * <li>Send a Server->Client NpcHtmlMessage containing the text of the Folk to the Player</li>
     * <li>Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet</li>
     * </ul>
     *
     * @param player The Player that talk with the Folk
     * @param val    The number of the page of the Folk to display
     */
    public void showChatWindow(Player player, int val) {
        if (!_isTalkable) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.getReputation() < 0) {
            if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (this instanceof Merchant)) {
                if (showPkDenyChatWindow(player, "merchant")) {
                    return;
                }
            } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && (this instanceof Teleporter)) {
                if (showPkDenyChatWindow(player, "teleporter")) {
                    return;
                }
            } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (this instanceof Warehouse)) {
                if (showPkDenyChatWindow(player, "warehouse")) {
                    return;
                }
            } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (this instanceof Fisherman)) {
                if (showPkDenyChatWindow(player, "fisherman")) {
                    return;
                }
            }
        }

        if (getTemplate().isType("Auctioneer") && (val == 0)) {
            return;
        }

        final int npcId = getTemplate().getId();

        String filename;
        switch (npcId) {
            case 31688: {
                if (player.isNoble()) {
                    filename = OLYMPIAD_HTML_PATH + "noble_main.htm";
                } else {
                    filename = (getHtmlPath(npcId, val));
                }
                break;
            }
            case 31690:
            case 31769:
            case 31770:
            case 31771:
            case 31772: {
                if (player.isHero() || player.isNoble()) {
                    filename = OLYMPIAD_HTML_PATH + "hero_main.htm";
                } else {
                    filename = (getHtmlPath(npcId, val));
                }
                break;
            }
            case 30298: // Blacksmith Pinter
            {
                if (player.isAcademyMember()) {
                    filename = (getHtmlPath(npcId, 1));
                } else {
                    filename = (getHtmlPath(npcId, val));
                }
                break;
            }
            default: {
                if (((npcId >= 31093) && (npcId <= 31094)) || ((npcId >= 31172) && (npcId <= 31201)) || ((npcId >= 31239) && (npcId <= 31254))) {
                    return;
                }
                // Get the text of the selected HTML file in function of the npcId and of the page number
                filename = (getHtmlPath(npcId, val));
                break;
            }
        }

        // Send a Server->Client NpcHtmlMessage containing the text of the Folk to the Player
        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(player, filename);
        html.replace("%npcname%", getName());
        html.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(html);
    }

    /**
     * Open a chat window on client with the text specified by the given file name and path, relative to the datapack root.
     *
     * @param player   The Player that talk with the Folk
     * @param filename The filename that contains the text to send
     */
    public void showChatWindow(Player player, String filename) {
        // Send a Server->Client NpcHtmlMessage containing the text of the Folk to the Player
        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(player, filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(html);

        // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    /**
     * @return the Exp Reward of this Npc (modified by RATE_XP).
     */
    public double getExpReward() {
        final Instance instance = getInstanceWorld();
        final float rateMul = instance != null ? instance.getExpRate() : getSettings(RateSettings.class).xp();
        return getTemplate().getExp() * rateMul;
    }

    /**
     * @return the SP Reward of this Npc (modified by RATE_SP).
     */
    public double getSpReward() {
        final Instance instance = getInstanceWorld();
        final float rateMul = instance != null ? instance.getSPRate() : Config.RATE_SP;
        return getTemplate().getSP() * rateMul;
    }

    public long getAttributeExp() {
        return getTemplate().getAttributeExp();
    }

    public ElementalType getElementalSpiritType() {
        return getTemplate().getElementalType();
    }


    /**
     * Kill the Folk (the corpse disappeared after 7 seconds).<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Create a DecayTask to remove the corpse of the Folk after 7 seconds</li>
     * <li>Set target to null and cancel Attack or Cast</li>
     * <li>Stop movement</li>
     * <li>Stop HP/MP/CP Regeneration task</li>
     * <li>Stop all active skills effects in progress on the Creature</li>
     * <li>Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform</li>
     * <li>Notify Creature AI</li>
     * </ul>
     *
     * @param killer The Creature who killed it
     */
    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        // normally this wouldn't really be needed, but for those few exceptions,
        // we do need to reset the weapons back to the initial template weapon.
        _currentLHandId = getTemplate().getLHandId();
        _currentRHandId = getTemplate().getRHandId();
        _currentCollisionHeight = getTemplate().getfCollisionHeight();
        _currentCollisionRadius = getTemplate().getfCollisionRadius();

        final Weapon weapon = (killer != null) ? killer.getActiveWeaponItem() : null;
        _killingBlowWeaponId = (weapon != null) ? weapon.getId() : 0;

        DecayTaskManager.getInstance().add(this);

        if (_spawn != null) {
            final NpcSpawnTemplate npcTemplate = _spawn.getNpcSpawnTemplate();
            if (npcTemplate != null) {
                npcTemplate.notifyNpcDeath(this, killer);
            }
        }

        // Apply Mp Rewards
        if ((getTemplate().getMpRewardValue() > 0) && GameUtils.isPlayable(killer)) {
            final Player killerPlayer = killer.getActingPlayer();
            new MpRewardTask(killerPlayer, this);
            for (Summon summon : killerPlayer.getServitors().values()) {
                new MpRewardTask(summon, this);
            }
            if (getTemplate().getMpRewardAffectType() == MpRewardAffectType.PARTY) {
                final Party party = killerPlayer.getParty();
                if (party != null) {
                    for (Player member : party.getMembers()) {
                        if ((member != killerPlayer) && MathUtil.isInsideRadius3D(member, getX(), getY(), getZ(), Config.ALT_PARTY_RANGE)) {
                            new MpRewardTask(member, this);
                            for (Summon summon : member.getServitors().values()) {
                                new MpRewardTask(summon, this);
                            }
                        }
                    }
                }
            }
        }

        DBSpawnManager.getInstance().updateStatus(this, true);
        return true;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        // Recharge shots
        _soulshotamount = getTemplate().getSoulShot();
        _spiritshotamount = getTemplate().getSpiritShot();
        _killingBlowWeaponId = 0;
        _isRandomAnimationEnabled = getTemplate().isRandomAnimationEnabled();
        _isRandomWalkingEnabled = getTemplate().isRandomWalkEnabled();

        if (isTeleporting()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcTeleport(this), this);
        } else {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcSpawn(this), this);
        }

        if (!isTeleporting()) {
            WalkingManager.getInstance().onSpawn(this);
        }

        if (isInsideZone(ZoneType.TAX) && (getCastle() != null) && (Config.SHOW_CREST_WITHOUT_QUEST || getCastle().isShowNpcCrest()) && (getCastle().getOwnerId() != 0)) {
            setClanId(getCastle().getOwnerId());
        }
    }

    /**
     * Invoked when the NPC is re-spawned to reset the instance variables
     */
    public void onRespawn() {
        // Make it alive
        setIsDead(false);

        // Stop all effects and recalculate stats without broadcasting.
        getEffectList().stopAllEffects(false);

        // Reset decay info
        setDecayed(false);

        // Fully heal npc and don't broadcast packet.
        setCurrentHp(getMaxHp(), false);
        setCurrentMp(getMaxMp(), false);

        // Clear script variables
        if (hasVariables()) {
            getVariables().getSet().clear();
        }

        // Reset targetable state
        setTargetable(getTemplate().isTargetable());

        // Reset summoner
        setSummoner(null);

        // Reset summoned list
        resetSummonedNpcs();

        // Reset NpcStringId for name
        _nameString = null;

        // Reset NpcStringId for title
        _titleString = null;

        // Reset parameters
        _params = null;
    }

    /**
     * Remove the Folk from the world and update its spawn object (for a complete removal use the deleteMe method).<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Remove the Folk from the world when the decay task is launched</li>
     * <li>Decrease its spawn counter</li>
     * <li>Manage Siege task (killFlag, killCT)</li>
     * </ul>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of World </B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT>
     */
    @Override
    public void onDecay() {
        if (_isDecayed) {
            return;
        }
        setDecayed(true);

        // Remove the Folk from the world when the decay task is launched
        super.onDecay();

        // Decrease its spawn counter
        if (_spawn != null) {
            _spawn.decreaseCount(this);
        }

        // Notify Walking Manager
        WalkingManager.getInstance().onDeath(this);

        // Notify DP scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnNpcDespawn(this), this);

        // Remove from instance world
        final Instance instance = getInstanceWorld();
        if (instance != null) {
            instance.removeNpc(this);
        }

        scriptValue = 0;
    }

    /**
     * Remove PROPERLY the Folk from the world.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Remove the Folk from the world and update its spawn object</li>
     * <li>Remove all WorldObject from _knownObjects and _knownPlayer of the Folk then cancel Attack or Cast and notify AI</li>
     * <li>Remove WorldObject object from _allObjects of World</li>
     * </ul>
     * <FONT COLOR=#FF0000><B><U>Caution</U>: This method DOESN'T SEND Server->Client packets to players</B></FONT><br>
     * UnAfraid: TODO: Add Listener here
     */
    @Override
    public boolean deleteMe() {
        try {
            onDecay();
        } catch (Exception e) {
            LOGGER.error("Failed decayMe().", e);
        }

        if (isChannelized()) {
            getSkillChannelized().abortChannelization();
        }

        ZoneManager.getInstance().getRegion(this).removeFromZones(this);

        return super.deleteMe();
    }

    /**
     * @return the Spawn object that manage this Folk.
     */
    public Spawn getSpawn() {
        return _spawn;
    }

    /**
     * Set the spawn of the Folk.
     *
     * @param spawn The Spawn that manage the Folk
     */
    public void setSpawn(Spawn spawn) {
        _spawn = spawn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getName() + "(" + getId() + ")[" + getObjectId() + "]";
    }

    public boolean isDecayed() {
        return _isDecayed;
    }

    public void setDecayed(boolean decayed) {
        _isDecayed = decayed;
    }

    public void endDecayTask() {
        if (!_isDecayed) {
            DecayTaskManager.getInstance().cancel(this);
            onDecay();
        }
    }

    // Two functions to change the appearance of the equipped weapons on the NPC
    // This is only useful for a few NPCs and is most likely going to be called from AI
    public void setLHandId(int newWeaponId) {
        _currentLHandId = newWeaponId;
        broadcastInfo();
    }

    public void setRHandId(int newWeaponId) {
        _currentRHandId = newWeaponId;
        broadcastInfo();
    }

    public void setLRHandId(int newLWeaponId, int newRWeaponId) {
        _currentRHandId = newRWeaponId;
        _currentLHandId = newLWeaponId;
        broadcastInfo();
    }

    public void setEnchant(int newEnchantValue) {
        _currentEnchant = newEnchantValue;
        broadcastInfo();
    }

    public boolean isShowName() {
        return getTemplate().isShowName();
    }

    @Override
    public double getCollisionHeight() {
        return _currentCollisionHeight;
    }

    public void setCollisionHeight(double height) {
        _currentCollisionHeight = height;
    }

    @Override
    public double getCollisionRadius() {
        return _currentCollisionRadius;
    }

    public void setCollisionRadius(double radius) {
        _currentCollisionRadius = radius;
    }

    @Override
    public void sendInfo(Player activeChar) {
        if (isVisibleFor(activeChar)) {
            if (getRunSpeed() == 0) {
                activeChar.sendPacket(new ServerObjectInfo(this, activeChar));
            } else {
                activeChar.sendPacket(new NpcInfo(this));
            }
        }
    }

    public void scheduleDespawn(long delay) {
        ThreadPool.schedule(() -> {
            if (!_isDecayed) {
                deleteMe();
            }
        }, delay);
    }

    @Override
    public final void notifyQuestEventSkillFinished(Skill skill, WorldObject target) {
        if (target != null) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcSkillFinished(this, target.getActingPlayer(), skill), this);
        }
    }

    @Override
    public boolean isMovementDisabled() {
        return super.isMovementDisabled() || !getTemplate().canMove() || (getAiType() == AIType.CORPSE);
    }

    public AIType getAiType() {
        return getTemplate().getAIType();
    }

    public boolean hasDisplayEffect(int val) {
        return _displayEffect == val;
    }

    public int getDisplayEffect() {
        return _displayEffect;
    }

    public void setDisplayEffect(int val) {
        if (val != _displayEffect) {
            _displayEffect = val;
            broadcastPacket(new ExChangeNpcState(getObjectId(), val));
        }
    }

    public int getColorEffect() {
        return 0;
    }

    @Override
    public void setTeam(Team team) {
        super.setTeam(team);
        broadcastInfo();
    }

    @Override
    public void consumeAndRechargeShots(ShotType shotType, int targets) {
        if (shotType == ShotType.SOULSHOTS) {
            consumeAndRechargeSoulShots();
        } else if (shotType == ShotType.SPIRITSHOTS) {
            consumeAndRechargeSpiritShots();
        }
    }

    private void consumeAndRechargeSpiritShots() {
        if(_spiritshotamount > 0 && Rnd.chance(getTemplate().getSpiritShotChance())) {
            _spiritshotamount--;
            Broadcast.toSelfAndKnownPlayersInRadius(this, new MagicSkillUse(this, this, 2061, 1, 0, 0), 600);
            chargeShot(ShotType.SPIRITSHOTS, 4);
        } else {
            unchargeShot(ShotType.SPIRITSHOTS);
        }
    }

    private void consumeAndRechargeSoulShots() {
        if(_soulshotamount > 0 && Rnd.chance(getTemplate().getSoulShotChance())) {
            _soulshotamount--;
            Broadcast.toSelfAndKnownPlayersInRadius(this, new MagicSkillUse(this, this, 2154, 1, 0, 0), 600);
            chargeShot(ShotType.SOULSHOTS, 4);
        } else {
            unchargeShot(ShotType.SOULSHOTS);
        }
    }

    /**
     * Short wrapper for backward compatibility
     *
     * @return stored script value
     */
    public int getScriptValue() {
        return scriptValue;
    }

    /**
     * Short wrapper for backward compatibility. Stores script value
     *
     * @param value value to store
     */
    public void setScriptValue(int value) {
        scriptValue = value;
    }

    /**
     * Short wrapper for backward compatibility.
     *
     * @param value value to store
     * @return {@code true} if stored script value equals given value, {@code false} otherwise
     */
    public boolean isScriptValue(int value) {
        return scriptValue == value;
    }

    /**
     * @param npc NPC to check
     * @return {@code true} if both given NPC and this NPC is in the same spawn group, {@code false} otherwise
     */
    public boolean isInMySpawnGroup(Npc npc) {
        return ((getSpawn() != null) && (npc.getSpawn() != null) && (getSpawn().getName() != null) && (getSpawn().getName().equals(npc.getSpawn().getName())));
    }

    /**
     * @return {@code true} if NPC currently located in own spawn point, {@code false} otherwise
     */
    public boolean staysInSpawnLoc() {
        return ((_spawn != null) && (_spawn.getX() == getX()) && (_spawn.getY() == getY()));
    }

    /**
     * @return {@code true} if {@link NpcVariables} instance is attached to current player's scripts, {@code false} otherwise.
     */
    public boolean hasVariables() {
        return getScript(NpcVariables.class) != null;
    }

    /**
     * @return {@link NpcVariables} instance containing parameters regarding NPC.
     */
    public NpcVariables getVariables() {
        final NpcVariables vars = getScript(NpcVariables.class);
        return vars != null ? vars : addScript(new NpcVariables());
    }

    /**
     * Send an "event" to all NPCs within given radius
     *
     * @param eventName - name of event
     * @param radius    - radius to send event
     * @param reference - WorldObject to pass, if needed
     */
    public void broadcastEvent(String eventName, int radius, WorldObject reference) {
        World.getInstance().forEachVisibleObjectInRange(this, Npc.class, radius, obj ->
        {
            if (obj.hasListener(EventType.ON_NPC_EVENT_RECEIVED)) {
                EventDispatcher.getInstance().notifyEventAsync(new OnNpcEventReceived(eventName, this, obj, reference), obj);
            }
        });
    }

    /**
     * Sends an event to a given object.
     *
     * @param eventName the event name
     * @param receiver  the receiver
     * @param reference the reference
     */
    public void sendScriptEvent(String eventName, WorldObject receiver, WorldObject reference) {
        EventDispatcher.getInstance().notifyEventAsync(new OnNpcEventReceived(eventName, this, (Npc) receiver, reference), receiver);
    }

    /**
     * Gets point in range between radiusMin and radiusMax from this NPC
     *
     * @param radiusMin miminal range from NPC (not closer than)
     * @param radiusMax maximal range from NPC (not further than)
     * @return Location in given range from this NPC
     */
    public Location getPointInRange(int radiusMin, int radiusMax) {
        if ((radiusMax == 0) || (radiusMax < radiusMin)) {
            return new Location(getX(), getY(), getZ());
        }

        final int radius = Rnd.get(radiusMin, radiusMax);
        final double angle = Rnd.nextDouble() * 2 * Math.PI;

        return new Location((int) (getX() + (radius * Math.cos(angle))), (int) (getY() + (radius * Math.sin(angle))), getZ());
    }

    /**
     * Drops an item.
     *
     * @param character the last attacker or main damage dealer
     * @param itemId    the item ID
     * @param itemCount the item count
     * @return the dropped item
     */
    public Item dropItem(Creature character, int itemId, long itemCount) {
        Item item = null;
        for (int i = 0; i < itemCount; i++) {
            // Randomize drop position.
            final int newX = (getX() + Rnd.get((RANDOM_ITEM_DROP_LIMIT * 2) + 1)) - RANDOM_ITEM_DROP_LIMIT;
            final int newY = (getY() + Rnd.get((RANDOM_ITEM_DROP_LIMIT * 2) + 1)) - RANDOM_ITEM_DROP_LIMIT;
            final int newZ = getZ() + 20;

            if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                LOGGER.error("Item doesn't exist so cannot be dropped. Item ID: " + itemId + " Quest: " + getName());
                return null;
            }

            item = ItemEngine.getInstance().createItem("Loot", itemId, itemCount, character, this);
            if (item == null) {
                return null;
            }

            if (character != null) {
                item.getDropProtection().protect(character);
            }

            item.dropMe(this, newX, newY, newZ);

            final var generalSettings = getSettings(GeneralSettings.class);
            if (!generalSettings.isProtectedItem(itemId)) {
                if (( generalSettings.autoDestroyItemTime() > 0 && !item.getTemplate().hasExImmediateEffect()) || (generalSettings.autoDestroyHerbTime() > 0 && item.getTemplate().hasExImmediateEffect())) {
                    ItemsAutoDestroy.getInstance().addItem(item);
                }
            }
            item.setProtected(false);

            // If stackable, end loop as entire count is included in 1 instance of item.
            if (item.isStackable() || !Config.MULTIPLE_ITEM_DROP) {
                break;
            }
        }
        return item;
    }

    /**
     * Method overload for {@link Attackable#dropItem(Creature, int, long)}
     *
     * @param character the last attacker or main damage dealer
     * @param item      the item holder
     * @return the dropped item
     */
    public Item dropItem(Creature character, ItemHolder item) {
        return dropItem(character, item.getId(), item.getCount());
    }

    @Override
    public final String getName() {
        return getTemplate().getName();
    }

    @Override
    public boolean isVisibleFor(Player player) {
        if (hasListener(EventType.ON_NPC_CAN_BE_SEEN)) {
            final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnNpcCanBeSeen(this, player), this, TerminateReturn.class);
            if (term != null) {
                return term.terminate();
            }
        }
        return super.isVisibleFor(player);
    }

    /**
     * Sets if the players can talk with this npc or not
     *
     * @param val {@code true} if the players can talk, {@code false} otherwise
     */
    public void setIsTalkable(boolean val) {
        _isTalkable = val;
    }

    /**
     * Checks if the players can talk to this npc.
     *
     * @return {@code true} if the players can talk, {@code false} otherwise.
     */
    public boolean isTalkable() {
        return _isTalkable;
    }

    /**
     * Checks if the NPC is a Quest Monster.
     *
     * @return {@code true} if the NPC is a Quest Monster, {@code false} otherwise.
     */
    public boolean isQuestMonster() {
        return _isQuestMonster;
    }

    /**
     * @return the id of the weapon with which player killed this npc.
     */
    public int getKillingBlowWeapon() {
        return _killingBlowWeaponId;
    }

    /**
     * Sets the weapon id with which this npc was killed.
     *
     * @param weaponId
     */
    public void setKillingBlowWeapon(int weaponId) {
        _killingBlowWeaponId = weaponId;
    }

    /**
     * @return The player's object Id this NPC is cloning.
     */
    public int getCloneObjId() {
        return _cloneObjId;
    }

    /**
     * @param cloneObjId object id of player or 0 to disable it.
     */
    public void setCloneObjId(int cloneObjId) {
        _cloneObjId = cloneObjId;
    }

    /**
     * @return The clan's object Id this NPC is displaying.
     */
    @Override
    public int getClanId() {
        return _clanId;
    }

    /**
     * @param clanObjId object id of clan or 0 to disable it.
     */
    public void setClanId(int clanObjId) {
        _clanId = clanObjId;
    }

    /**
     * Broadcasts NpcSay packet to all known players.
     *
     * @param chatType the chat type
     * @param text     the text
     */
    public void broadcastSay(ChatType chatType, String text) {
        Broadcast.toKnownPlayers(this, new NpcSay(this, chatType, text));
    }

    /**
     * Broadcasts NpcSay packet to all known players with NPC string id.
     *
     * @param chatType    the chat type
     * @param npcStringId the NPC string id
     * @param parameters  the NPC string id parameters
     */
    public void broadcastSay(ChatType chatType, NpcStringId npcStringId, String... parameters) {
        final NpcSay npcSay = new NpcSay(this, chatType, npcStringId);
        if (parameters != null) {
            for (String parameter : parameters) {
                if (parameter != null) {
                    npcSay.addStringParameter(parameter);
                }
            }
        }

        if (chatType == ChatType.NPC_GENERAL) {
            Broadcast.toKnownPlayersInRadius(this, npcSay, 1250);
        } else {
            Broadcast.toKnownPlayers(this, npcSay);
        }
    }

    /**
     * Broadcasts NpcSay packet to all known players with custom string in specific radius.
     *
     * @param chatType the chat type
     * @param text     the text
     * @param radius   the radius
     */
    public void broadcastSay(ChatType chatType, String text, int radius) {
        Broadcast.toKnownPlayersInRadius(this, new NpcSay(this, chatType, text), radius);
    }

    /**
     * Broadcasts NpcSay packet to all known players with NPC string id in specific radius.
     *
     * @param chatType    the chat type
     * @param npcStringId the NPC string id
     * @param radius      the radius
     */
    public void broadcastSay(ChatType chatType, NpcStringId npcStringId, int radius) {
        Broadcast.toKnownPlayersInRadius(this, new NpcSay(this, chatType, npcStringId), radius);
    }

    /**
     * @return the parameters of the npc merged with the spawn parameters (if there are any)
     */
    public StatsSet getParameters() {
        if (_params != null) {
            return _params;
        }

        if (_spawn != null) // Minions doesn't have Spawn object bound
        {
            final NpcSpawnTemplate npcSpawnTemplate = _spawn.getNpcSpawnTemplate();
            if ((npcSpawnTemplate != null) && (npcSpawnTemplate.getParameters() != null) && !npcSpawnTemplate.getParameters().isEmpty()) {
                final StatsSet params = getTemplate().getParameters();
                if ((params != null) && !params.getSet().isEmpty()) {
                    final StatsSet set = new StatsSet();
                    set.merge(params);
                    set.merge(npcSpawnTemplate.getParameters());
                    _params = set;
                    return set;
                }
                _params = npcSpawnTemplate.getParameters();
                return _params;
            }
        }
        _params = getTemplate().getParameters();
        return _params;
    }

    public List<Skill> getLongRangeSkills() {
        return getTemplate().getAISkills(AISkillScope.LONG_RANGE);
    }

    public List<Skill> getShortRangeSkills() {
        return getTemplate().getAISkills(AISkillScope.SHORT_RANGE);
    }

    /**
     * Verifies if the NPC can cast a skill given the minimum and maximum skill chances.
     *
     * @return {@code true} if the NPC has chances of casting a skill
     */
    public boolean hasSkillChance() {
        return Rnd.get(100) < Rnd.get(getTemplate().getMinSkillChance(), getTemplate().getMaxSkillChance());
    }

    /**
     * Initialize creature container that looks up for creatures around its owner, and notifies with onCreatureSee upon discovery.
     */
    public void initSeenCreatures() {
        initSeenCreatures(getTemplate().getAggroRange());
    }

    /**
     * @return the NpcStringId for name
     */
    public NpcStringId getNameString() {
        return _nameString;
    }

    public void setNameString(NpcStringId nameString) {
        _nameString = nameString;
    }

    /**
     * @return the NpcStringId for title
     */
    public NpcStringId getTitleString() {
        return _titleString;
    }

    public void setTitleString(NpcStringId titleString) {
        _titleString = titleString;
    }

    public void sendChannelingEffect(Creature target, int state) {
        broadcastPacket(new ExShowChannelingEffect(this, target, state));
    }

    public RaidBossStatus getRaidBossStatus() {
        return _raidStatus;
    }

    public void setRaidBossStatus(RaidBossStatus status) {
        _raidStatus = status;
    }
}
