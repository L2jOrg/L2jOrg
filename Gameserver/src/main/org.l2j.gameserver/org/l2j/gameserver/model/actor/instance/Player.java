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
package org.l2j.gameserver.model.actor.instance;

import io.github.joealisson.primitive.*;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.commons.util.collection.LimitedQueue;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ItemsAutoDestroy;
import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.ai.*;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.cache.WarehouseCacheManager;
import org.l2j.gameserver.data.database.dao.ElementalSpiritDAO;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.database.data.*;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.data.xml.impl.*;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.engine.autoplay.AutoPlaySettings;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.*;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.model.DamageInfo.DamageType;
import org.l2j.gameserver.model.actor.*;
import org.l2j.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.impl.CaptchaRequest;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.actor.status.PlayerStatus;
import org.l2j.gameserver.model.actor.tasks.character.NotifyAITask;
import org.l2j.gameserver.model.actor.tasks.player.*;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.entity.*;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.*;
import org.l2j.gameserver.model.holders.*;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.*;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.model.item.container.*;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.skills.*;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.MoveType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.variables.AccountVariables;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.ChangeAccessLevel;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionInfo;
import org.l2j.gameserver.network.serverpackets.friend.FriendStatus;
import org.l2j.gameserver.network.serverpackets.html.AbstractHtmlPacket;
import org.l2j.gameserver.network.serverpackets.item.ItemList;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMode;
import org.l2j.gameserver.network.serverpackets.pvpbook.ExNewPk;
import org.l2j.gameserver.network.serverpackets.sessionzones.TimedHuntingZoneExit;
import org.l2j.gameserver.settings.AttendanceSettings;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.taskmanager.SaveTaskManager;
import org.l2j.gameserver.util.*;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.WaterZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.*;
import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static org.l2j.gameserver.model.item.BodyPart.*;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * This class represents all player characters in the world.<br>
 * There is always a client-thread connected to this (except if a player-store is activated upon logout).
 *
 * @author JoeAlisson
 */
public final class Player extends Playable {

    private final PlayerData data;
    private final PlayerAppearance appearance;
    private final Map<ShotType, Integer> activeSoulShots = new EnumMap<>(ShotType.class);
    private final LimitedQueue<DamageInfo> lastDamages = new LimitedQueue<>(30);

    private ElementalSpirit[] spirits;
    private ElementalType activeElementalSpiritType;
    private AutoPlaySettings autoPlaySettings;
    private PlayerVariableData variables;
    private PlayerStatsData statsData;
    private IntMap<CostumeData> costumes = Containers.emptyIntMap();
    private ScheduledFuture<?> _timedHuntingZoneFinishTask = null;
    private IntMap<CostumeCollectionData> costumesCollections  = Containers.emptyIntMap();
    private CostumeCollectionData activeCostumesCollection = CostumeCollectionData.DEFAULT;
    private IntSet teleportFavorites;

    private byte vipTier;
    private int rank;
    private int rankRace;
    private byte shineSouls;
    private byte shadowSouls;
    private int additionalSoulshot;

    private Player(PlayerData playerData, PlayerTemplate template) {
        super(playerData.getCharId(), template);
        this.data = playerData;
        setName(playerData.getName());
        setInstanceType(InstanceType.L2PcInstance);
        initCharStatusUpdateValues();

        appearance = new PlayerAppearance(this, playerData.getFace(), playerData.getHairColor(), playerData.getHairStyle(), playerData.isFemale());

        getAI();

        radar = new Radar(this);

        Arrays.fill(htmlActionCaches, new LinkedList<>());
        running = true;
        setAccessLevel(playerData.getAccessLevel(), false, false);
    }

    public void deleteShortcuts(Predicate<Shortcut> filter) {
        shortcuts.deleteShortcuts(filter);
    }

    public void forEachShortcut(Consumer<Shortcut> action) {
        shortcuts.forEachShortcut(action);
    }

    public void initElementalSpirits() {
        if(nonNull(spirits)) {
            return;
        }

        tryLoadSpirits();

        if(isNull(spirits)) {
            var types = ElementalType.values();
            spirits = new ElementalSpirit[types.length -1]; // exclude None

            for (ElementalType type : types) {
                if(ElementalType.NONE == type) {
                    continue;
                }

                var spirit = new ElementalSpirit(type, this);
                spirits[type.getId() -1] = spirit;
                spirit.save();
            }

        }

        if(isNull(activeElementalSpiritType)) {
            activeElementalSpiritType = ElementalType.FIRE;
        }
    }

    private void tryLoadSpirits() {
        var spiritsData = getDAO(ElementalSpiritDAO.class).findByPlayerId(getObjectId());
        if (!spiritsData.isEmpty()) {
            spirits = new ElementalSpirit[ElementalType.values().length - 1]; // exclude None

            for (ElementalSpiritData spiritData : spiritsData) {
                spirits[spiritData.getType() - 1] = new ElementalSpirit(spiritData, this);
                if (spiritData.isInUse()) {
                    activeElementalSpiritType = ElementalType.of(spiritData.getType());
                }
            }
        }
    }

    public void setAccessLevel(int level, boolean broadcast, boolean updateInDb) {
        this.accessLevel = AdminData.getInstance().getAccessLevelOrDefault(level);

        appearance.setNameColor(this.accessLevel.getNameColor());
        appearance.setTitleColor(this.accessLevel.getTitleColor());

        if (broadcast) {
            broadcastUserInfo();
        }

        if (updateInDb) {
            getDAO(PlayerDAO.class).updateAccessLevel(objectId, accessLevel.getLevel());
        }

        PlayerNameTable.getInstance().addName(this);

        if (level > 0) {
            LOGGER.warn("{} access level set for player {} ! Just a warning to be careful ;)", this.accessLevel.getName(), this);
        }
    }

    public ElementalSpirit getElementalSpirit(ElementalType type) {
        if(isNull(spirits) || isNull(type) || type == ElementalType.NONE) {
            return null;
        }
        return spirits[type.getId() -1];
    }

    public void changeElementalSpirit(byte element) {
        activeElementalSpiritType = ElementalType.of(element);
        var userInfo =  new UserInfo(this, false);
        userInfo.addComponentType(UserInfoType.SPIRITS);
        sendPacket(userInfo);
    }

    public double getActiveElementalSpiritAttack() {
        return getStats().getElementalSpiritPower(activeElementalSpiritType, zeroIfNullOrElse(getElementalSpirit(activeElementalSpiritType), ElementalSpirit::getAttack));
    }

    public double getFireSpiritDefense() {
        return getElementalSpiritDefenseOf(ElementalType.FIRE);
    }

    public double getWaterSpiritDefense() {
        return getElementalSpiritDefenseOf(ElementalType.WATER);
    }

    public double getWindSpiritDefense() {
        return getElementalSpiritDefenseOf(ElementalType.WIND);
    }

    public double getEarthSpiritDefense() {
        return getElementalSpiritDefenseOf(ElementalType.EARTH);
    }

    public double getElementalSpiritDefenseOf(ElementalType type) {
        return getStats().getElementalSpiritDefense(type, zeroIfNullOrElse(getElementalSpirit(type), ElementalSpirit::getDefense));
    }

    public double getElementalSpiritCritRate() {
        return getStats().getElementalSpiritCriticalRate(zeroIfNullOrElse(getElementalSpirit(activeElementalSpiritType), ElementalSpirit::getCriticalRate));
    }

    public double getElementalSpiritCritDamage() {
        return getStats().getElementalSpiritCriticalDamage(zeroIfNullOrElse(getElementalSpirit(activeElementalSpiritType), ElementalSpirit::getCriticalDamage));
    }

    public double getElementalSpiritXpBonus() {
        return getStats().getElementalSpiritXpBonus();
    }

    public byte getActiveElementalSpiritType() {
        return (byte) zeroIfNullOrElse(activeElementalSpiritType, ElementalType::getId);
    }

    public ElementalSpirit[] getSpirits() {
        return spirits;
    }

    public byte getVipTier() {
        return vipTier;
    }

    public void setVipTier(byte vipTier) {
        this.vipTier = vipTier;
    }

    public long getVipPoints() {
        return getClient().getVipPoints();
    }

    public void updateVipPoints(long points) {
        getClient().updateVipPoints(points);
    }

    public void setNCoins(int coins) {
        getClient().setCoin(coins);
    }

    public int getNCoins() {
        return getClient().getCoin();
    }

    public void updateNCoins(int coins) {
        getClient().updateCoin(coins);
    }

    public long getRustyCoin() {
        return inventory.getRustyCoin();
    }

    public long getSilverCoin() {
        return inventory.getSilverCoin();
    }

    public long getVipTierExpiration() {
        return getClient().getVipTierExpiration();
    }

    public void setVipTierExpiration(long expiration) {
        getClient().setVipTierExpiration(expiration);
    }

    public long getLCoins() { return inventory.getLCoin(); }

    public void addLCoins(long count) { inventory.addLCoin(count); }

    public boolean isInBattle() {
        return AttackStanceTaskManager.getInstance().hasAttackStanceTask(this);
    }

    public void setAutoPlaySettings(AutoPlaySettings autoPlaySettings) {
        this.autoPlaySettings = autoPlaySettings;
    }

    public AutoPlaySettings getAutoPlaySettings() {
        return autoPlaySettings;
    }

    public int getShortcutAmount() {
        return shortcuts.getAmount();
    }

    public void setActiveAutoShortcut(int room, boolean active) {
        shortcuts.setActive(room, active);
    }

    public Shortcut nextAutoShortcut() {
        return shortcuts.nextAutoShortcut();
    }

    public void resetNextAutoShortcut() {
        shortcuts.resetNextAutoShortcut();
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public void setRankRace(int rankRace) {
        this.rankRace = rankRace;
    }

    public int getRankRace() {
        return rankRace;
    }

    public int getRevengeUsableLocation() {
        return variables.getRevengeLocations();
    }

    public int getRevengeUsableTeleport() {
        return variables.getRevengeTeleports();
    }

    public void useRevengeLocation() {
        variables.useRevengeLocation();
    }

    public void useRevengeTeleport() {
        variables.useRevengeTeleport();
    }

    public void resetRevengeData() {
        variables.resetRevengeData();
    }

    public LocalDate getCreateDate() {
        return data.getCreateDate();
    }

    public PlayerStatsData getStatsData() {
        return statsData;
    }

    public void updateCharacteristicPoints() {
        statsData.setPoints(LevelData.getInstance().getCharacteristicPoints(getLevel()));
        getDAO(PlayerDAO.class).save(statsData);
    }

    public void enableAutoSoulShot(ShotType type, int itemId) {
        activeSoulShots.put(type, itemId);
        sendPacket(new ExAutoSoulShot(itemId, true, type.getClientType()), getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addItemName(itemId));
        rechargeShot(type);
    }

    public boolean rechargeShot(ShotType type) {
        var itemId = activeSoulShots.get(type);
        if(nonNull(itemId)) {
            var item = inventory.getItemByItemId(itemId);
            if(isNull(item)) {
                disableAutoShot(type);
                return false;
            }
            return falseIfNullOrElse(ItemHandler.getInstance().getHandler(item.getEtcItem()), handler -> handler.useItem(this, item, false));
        }
        return false;
    }

    public void disableSummonAutoShot() {
        disableAutoShot(ShotType.BEAST_SOULSHOTS);
        disableAutoShot(ShotType.BEAST_SPIRITSHOTS);
    }

    public boolean isAutoShotEnabled(ShotType type) {
        return activeSoulShots.containsKey(type);
    }

    public void disableAutoShot(ShotType type) {
        doIfNonNull(activeSoulShots.remove(type), itemId -> sendDisableShotPackets(type, itemId));
        switch (type) {
            case SOULSHOTS, SPIRITSHOTS -> unchargeShot(type);
            case BEAST_SOULSHOTS, BEAST_SPIRITSHOTS -> {
                var shotType = type == ShotType.BEAST_SOULSHOTS ? ShotType.SOULSHOTS : ShotType.SPIRITSHOTS;
                doIfNonNull(getPet(), pet -> pet.unchargeShot(shotType));
                getServitors().values().forEach(s -> s.unchargeShot(shotType));
            }
        }
    }

    public void disableAutoShots() {
        activeSoulShots.forEach(this::sendDisableShotPackets);
        activeSoulShots.clear();
        unchargeAllShots();
        doIfNonNull(getPet(), Creature::unchargeAllShots);
        getServitors().values().forEach(Creature::unchargeAllShots);
    }

    private void sendDisableShotPackets(ShotType type, int itemId) {
        sendPacket(new ExAutoSoulShot(itemId, false, type.getClientType()), getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(itemId));
    }

    @Override
    public void consumeAndRechargeShots(ShotType type, int targets) {
        if(!activeSoulShots.containsKey(type) || isNull(getActiveWeaponInstance())) {
            return;
        }

        var shotsCount = getActiveWeaponItem().getConsumeShotsCount();
        if(targets >= 4 && targets <= 8) {
            shotsCount <<=  1;
        } else if(targets >= 9 && targets <= 14) {
            shotsCount *= 3;
        } else if(targets >= 15) {
            shotsCount <<= 2;
        }
        if(!consumeAndRechargeShotCount(type, shotsCount)) {
            unchargeShot(type);
        }
    }

    public boolean consumeAndRechargeShotCount(ShotType type, int count) {
        if(count < 1) {
            return rechargeShot(type);
        }

        var itemId = activeSoulShots.get(type);
        Item item;
        if(nonNull(itemId) && nonNull(item = inventory.getItemByItemId(itemId))) {
            var consume = Math.min(count, item.getCount());
            destroyItemWithoutTrace("Consume", item.getObjectId(), consume, this, false);
            if (consume < count) {
                disableAutoShot(type);
                sendNotEnoughShotMessage(type);
                return false;
            }
            return rechargeShot(type);
        }
        return false;
    }

    private void sendNotEnoughShotMessage(ShotType type) {
        var message = switch (type) {
            case SPIRITSHOTS -> SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT;
            case SOULSHOTS -> SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT;
            case BEAST_SOULSHOTS -> SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR;
            case BEAST_SPIRITSHOTS -> SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_SERVITOR;
        };
        sendPacket(message);
    }

    public void setAdditionalSoulshot(int jewel) {
        this.additionalSoulshot = jewel;
    }

    public int getAdditionalSoulshot() {
        return additionalSoulshot;
    }

    public void setShineSouls(byte souls) {
        shineSouls = souls;
    }

    public byte getShineSouls() {
        return shineSouls;
    }

    public void setShadowSouls(byte shadowSouls) {
        this.shadowSouls = shadowSouls;
    }

    public byte getShadowSouls() {
        return shadowSouls;
    }

    public CostumeData addCostume(int costumeId) {
        if(costumes.equals(Containers.emptyIntMap())) {
            costumes = new HashIntMap<>();
        }
        var costume = costumes.computeIfAbsent(costumeId, id -> CostumeData.of(id, this));
        costume.increaseAmount();
        return costume;
    }

    public void forEachCostume(Consumer<CostumeData> action) {
        costumes.values().forEach(action);
    }

    public int getCostumeAmount() {
        return costumes.size();
    }

    public CostumeData getCostume(int id) {
        return costumes.get(id);
    }

    public void removeCostume(int id) {
        costumes.remove(id);
        getDAO(PlayerDAO.class).removeCostume(objectId, id);
    }

    public boolean setActiveCostumesCollection(int collectionId) {
        var collection = costumesCollections.get(collectionId);
        if(nonNull(collection)) {
            this.activeCostumesCollection = collection;
            activeCostumesCollection.updateReuseTime();
            return true;
        }
        return false;
    }

    public CostumeCollectionData getActiveCostumeCollection() {
        return activeCostumesCollection;
    }

    public void addCostumeCollection(int collectionId) {
        if(costumesCollections.equals(Containers.emptyIntMap())) {
            costumesCollections = new HashIntMap<>();
        }
        costumesCollections.computeIfAbsent(collectionId, id -> CostumeCollectionData.of(this, id));
    }

    public void removeCostumeCollection(int collectionId) {
        if(costumesCollections.isEmpty()) {
            return;
        }
        var collection = costumesCollections.remove(collectionId);
        if(activeCostumesCollection.equals(collection)) {
            activeCostumesCollection = CostumeCollectionData.DEFAULT;
        }
    }
    
    public int getCostumeCollectionAmount() {
        return costumesCollections.size();
    }

    public void addTeleportFavorite(int teleportId) {
        teleportFavorites.add(teleportId);
    }

    public void removeTeleportFavorite(int teleportId) {
        teleportFavorites.remove(teleportId);
    }

    public IntSet getTeleportFavorites() {
        return teleportFavorites;
    }

    public Collection<Item> getDepositableItems(WarehouseType type) {
        return inventory.getDepositableItems(type);
    }

    @Override
    protected void onReceiveDamage(Creature attacker, Skill skill, double value, DamageType damageType) {
        if (nonNull(tamedBeast)) {
            for (TamedBeast tamedBeast : tamedBeast) {
                tamedBeast.onOwnerGotAttacked(attacker);
            }
        }

        lastDamages.add(DamageInfo.of(attacker == this ? null : attacker, skill, value, damageType));
        super.onReceiveDamage(attacker, skill, value, damageType);
    }

    @Override
    protected boolean checkRangedAttackCondition(Weapon weapon, Creature target) {
        if(!super.checkRangedAttackCondition(weapon, target)) {
            ThreadPool.schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT), 1000);
            return false;
        }

        if (!inventory.findAmmunitionForCurrentWeapon()) {
            getAI().setIntention(AI_INTENTION_ACTIVE);
            sendPacket(SystemMessageId.YOU_HAVE_RUN_OUT_OF_ARROWS);
            return false;
        }

        if (target.isInsidePeaceZone(this)) {
            getAI().setIntention(AI_INTENTION_ACTIVE);
            sendPacket(SystemMessageId.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            return false;
        }

        int mpConsume = isAffected(EffectFlag.CHEAPSHOT) ? 0 : weapon.getMpConsume();
        if (getCurrentMp() < mpConsume) {
            ThreadPool.schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT), 1000);
            sendPacket(SystemMessageId.NOT_ENOUGH_MP);
            return false;
        }

        if (mpConsume > 0) {
            reduceCurrentMp(mpConsume);
        }
        return true;
    }

    @Override
    protected void onStartRangedAttack(boolean isCrossBow, int reuse) {
        inventory.reduceAmmunitionCount();

        if (isCrossBow) {
            sendPacket(SystemMessageId.YOUR_CROSSBOW_IS_PREPARING_TO_FIRE);
        }
        sendPacket(new SetupGauge(getObjectId(), SetupGauge.RED, reuse));
    }

    public static Player create(PlayerData playerData, PlayerTemplate template) {
        final Player player = new Player(playerData, template);
        player.setRecomLeft(20);
        if (player.createDb()) {
            if (getSettings(GeneralSettings.class).cachePlayersName()) {
                PlayerNameTable.getInstance().addName(player);
            }
            player.variables = PlayerVariableData.init(player.getObjectId());
            getDAO(PlayerVariablesDAO.class).save(player.variables);

            player.statsData = PlayerStatsData.init(player.getObjectId());
            getDAO(PlayerDAO.class).save(player.statsData);
            return player;
        }
        return null;
    }

    // Unchecked

    // TODO: This needs to be better integrated and saved/loaded
    private final Radar radar;

    public static final int ID_NONE = -1;
    public static final int REQUEST_TIMEOUT = 15;

    // Character Skill SQL String Definitions:
    private static final String RESTORE_SKILLS_FOR_CHAR = "SELECT skill_id,skill_level,skill_sub_level FROM character_skills WHERE charId=? AND class_index=?";
    private static final String UPDATE_CHARACTER_SKILL_LEVEL = "UPDATE character_skills SET skill_level=?, skill_sub_level=?  WHERE skill_id=? AND charId=? AND class_index=?";
    private static final String ADD_NEW_SKILLS = "REPLACE INTO character_skills (charId,skill_id,skill_level,skill_sub_level,class_index) VALUES (?,?,?,?,?)";
    private static final String DELETE_SKILL_FROM_CHAR = "DELETE FROM character_skills WHERE skill_id=? AND charId=? AND class_index=?";
    private static final String DELETE_CHAR_SKILLS = "DELETE FROM character_skills WHERE charId=? AND class_index=?";

    // Character Skill Save SQL String Definitions:
    private static final String ADD_SKILL_SAVE = "INSERT INTO character_skills_save (charId,skill_id,skill_level,skill_sub_level,remaining_time,reuse_delay,systime,restore_type,class_index,buff_index) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,skill_sub_level,remaining_time, reuse_delay, systime, restore_type FROM character_skills_save WHERE charId=? AND class_index=? ORDER BY buff_index ASC";
    private static final String DELETE_SKILL_SAVE = "DELETE FROM character_skills_save WHERE charId=? AND class_index=?";

    // Character Item Reuse Time String Definition:
    private static final String ADD_ITEM_REUSE_SAVE = "INSERT INTO character_item_reuse_save (charId,itemId,itemObjId,reuseDelay,systime) VALUES (?,?,?,?,?)";
    private static final String RESTORE_ITEM_REUSE_SAVE = "SELECT charId,itemId,itemObjId,reuseDelay,systime FROM character_item_reuse_save WHERE charId=?";
    private static final String DELETE_ITEM_REUSE_SAVE = "DELETE FROM character_item_reuse_save WHERE charId=?";

    // Character Character SQL String Definitions:
    private static final String INSERT_CHARACTER = "INSERT INTO characters (account_name,charId,char_name,level,maxHp,curHp,maxCp,curCp,maxMp,curMp,face,hairStyle,hairColor,sex,exp,sp,reputation,fame,raidbossPoints,pvpkills,pkkills,clanid,race,classid,cancraft,title,title_color,online,clan_privs,wantspeace,base_class,nobless,power_grade,vitality_points,createDate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_CHARACTER = "UPDATE characters SET level=?,maxHp=?,curHp=?,maxCp=?,curCp=?,maxMp=?,curMp=?,face=?,hairStyle=?,hairColor=?,sex=?,heading=?,x=?,y=?,z=?,exp=?,expBeforeDeath=?,sp=?,reputation=?,fame=?,raidbossPoints=?,pvpkills=?,pkkills=?,clanid=?,race=?,classid=?,title=?,title_color=?,online=?,clan_privs=?,wantspeace=?,base_class=?,onlinetime=?,nobless=?,power_grade=?,subpledge=?,lvl_joined_academy=?,apprentice=?,sponsor=?,clan_join_expiry_time=?,clan_create_expiry_time=?,char_name=?,bookmarkslot=?,vitality_points=?,language=?,pccafe_points=? WHERE charId=?";

    // Character Teleport Bookmark:
    private static final String INSERT_TP_BOOKMARK = "INSERT INTO character_tpbookmark (charId,Id,x,y,z,icon,tag,name) values (?,?,?,?,?,?,?,?)";
    private static final String UPDATE_TP_BOOKMARK = "UPDATE character_tpbookmark SET icon=?,tag=?,name=? where charId=? AND Id=?";
    private static final String RESTORE_TP_BOOKMARK = "SELECT Id,x,y,z,icon,tag,name FROM character_tpbookmark WHERE charId=?";
    private static final String DELETE_TP_BOOKMARK = "DELETE FROM character_tpbookmark WHERE charId=? AND Id=?";
    // Character Subclass SQL String Definitions:
    private static final String RESTORE_CHAR_SUBCLASSES = "SELECT class_id,exp,sp,level,vitality_points,class_index,dual_class FROM character_subclasses WHERE charId=? ORDER BY class_index ASC";
    private static final String ADD_CHAR_SUBCLASS = "INSERT INTO character_subclasses (charId,class_id,exp,sp,level,vitality_points,class_index,dual_class) VALUES (?,?,?,?,?,?,?,?)";
    private static final String UPDATE_CHAR_SUBCLASS = "UPDATE character_subclasses SET exp=?,sp=?,level=?,vitality_points=?,class_id=?,dual_class=? WHERE charId=? AND class_index =?";
    private static final String DELETE_CHAR_SUBCLASS = "DELETE FROM character_subclasses WHERE charId=? AND class_index=?";
    // Character Henna SQL String Definitions:
    private static final String RESTORE_CHAR_HENNAS = "SELECT slot,symbol_id FROM character_hennas WHERE charId=? AND class_index=?";
    private static final String ADD_CHAR_HENNA = "INSERT INTO character_hennas (charId,symbol_id,slot,class_index) VALUES (?,?,?,?)";
    private static final String DELETE_CHAR_HENNA = "DELETE FROM character_hennas WHERE charId=? AND slot=? AND class_index=?";
    private static final String DELETE_CHAR_HENNAS = "DELETE FROM character_hennas WHERE charId=? AND class_index=?";

    // Character Recipe List Save
    private static final String DELETE_CHAR_RECIPE_SHOP = "DELETE FROM character_recipeshoplist WHERE charId=?";
    private static final String INSERT_CHAR_RECIPE_SHOP = "REPLACE INTO character_recipeshoplist (`charId`, `recipeId`, `price`, `index`) VALUES (?, ?, ?, ?)";
    private static final String RESTORE_CHAR_RECIPE_SHOP = "SELECT * FROM character_recipeshoplist WHERE charId=? ORDER BY `index`";

    private static final String COND_OVERRIDE_KEY = "cond_override";
    // during fall validations will be disabled for 1000 ms.
    private static final int FALLING_VALIDATION_DELAY = 1000;
    // Training Camp
    private static final String TRAINING_CAMP_VAR = "TRAINING_CAMP";
    private static final String TRAINING_CAMP_DURATION = "TRAINING_CAMP_DURATION";
    // Attendance Reward system
    private static final String ATTENDANCE_DATE_VAR = "ATTENDANCE_DATE";
    private static final String ATTENDANCE_INDEX_VAR = "ATTENDANCE_INDEX";

    private final ReentrantLock _subclassLock = new ReentrantLock();
    private final ContactList _contactList = new ContactList(this);
    private final Map<Integer, TeleportBookmark> _tpbookmarks = new ConcurrentSkipListMap<>();
    /**
     * The table containing all RecipeList of the Player
     */
    private final Map<Integer, RecipeList> _dwarvenRecipeBook = new ConcurrentSkipListMap<>();
    private final Map<Integer, RecipeList> _commonRecipeBook = new ConcurrentSkipListMap<>();
    /**
     * Premium Items
     */
    private final Map<Integer, PremiumItem> _premiumItems = new ConcurrentSkipListMap<>();
    /**
     * Stored from last ValidatePosition
     **/
    private final Location _lastServerPosition = new Location(0, 0, 0);
    private final PlayerInventory inventory = new PlayerInventory(this);
    private final PlayerFreight _freight = new PlayerFreight(this);
    /**
     * The table containing all Quests began by the Player
     */
    private final Map<String, QuestState> _quests = new ConcurrentHashMap<>();
    /**
     * The list containing all shortCuts of this player.
     */
    private final Shortcuts shortcuts = new Shortcuts(this);

    private final MacroList macros = new MacroList(this);
    private final Set<Player> _snoopListener = ConcurrentHashMap.newKeySet();
    private final Set<Player> _snoopedPlayer = ConcurrentHashMap.newKeySet();
    /**
     * Hennas
     */
    private final Henna[] _henna = new Henna[3];
    private final Map<BaseStats, Integer> _hennaBaseStats = new ConcurrentHashMap<>();
    private final Map<Integer, ScheduledFuture<?>> _hennaRemoveSchedules = new ConcurrentHashMap<>(3);
    // client radar
    // charges
    private final AtomicInteger _charges = new AtomicInteger();
    private final Request _request = new Request(this);
    private final Map<Integer, String> _chars = new ConcurrentSkipListMap<>();
    /**
     * Player's cubics.
     */
    private final Map<Integer, CubicInstance> _cubics = new ConcurrentSkipListMap<>();
    /**
     * new race ticket
     **/
    private final int _race[] = new int[2];
    private final BlockList _blockList = new BlockList(this);
    /**
     * Last Html Npcs, 0 = last html was not bound to an npc
     */
    private final int[] _htmlActionOriginObjectIds = new int[HtmlActionScope.values().length];
    /**
     * Bypass validations
     */
    @SuppressWarnings("unchecked")
    private final LinkedList<String>[] htmlActionCaches = new LinkedList[HtmlActionScope.values().length];
    private final Fishing _fishing = new Fishing(this);
    private final Set<Integer> _whisperers = ConcurrentHashMap.newKeySet();
    /**
     * list of character friends
     */
    private final IntSet friends = CHashIntMap.newKeySet();
    protected int _activeClass;
    protected int _classIndex = 0;
    protected Future<?> _mountFeedTask;
    /**
     * Recommendation Two Hours bonus
     **/
    protected boolean _recoTwoHoursGiven = false;
    protected boolean _inventoryDisable = false;

    private GameClient _client;
    private String _ip = "N/A";

    private String _lang = null;
    private String _htmlPrefix = null;
    private volatile boolean _isOnline = false;
    private long _onlineTime;
    private long _onlineBeginTime;
    private long _uptime;
    /**
     * data for mounted pets
     */
    private int _controlItemId;
    private PetData _data;
    private PetLevelData _leveldata;
    private int _curFeed;
    private ScheduledFuture<?> _dismountTask;
    private boolean _petItems = false;
    /**
     * The list of sub-classes this character has.
     */
    private final IntMap<SubClass> _subClasses = new CHashIntMap<>();

    /**
     * The number of player killed during a PvP (the player killed was PvP Flagged)
     */
    private int _pvpKills;
    /**
     * The PK counter of the Player (= Number of non PvP Flagged player killed)
     */
    private int _pkKills;
    /**
     * The PvP Flag state of the Player (0=White, 1=Purple)
     */
    private byte _pvpFlag;
    /**
     * The Fame of this Player
     */
    private int _fame;
    private ScheduledFuture<?> _fameTask;

    private volatile ScheduledFuture<?> _teleportWatchdog;
    /**
     * The Siege state of the Player
     */
    private byte _siegeState = 0;
    /**
     * The id of castle/fort which the Player is registered for siege
     */
    private int _siegeSide = 0;
    private int _curWeightPenalty = 0;
    private int _lastCompassZone; // the last compass zone update send to the client
    private int _bookmarkslot = 0; // The Teleport Bookmark Slot
    private boolean _canFeed;
    private boolean _isInSiege;
    private boolean _isInHideoutSiege = false;
    /**
     * Olympiad
     */
    private boolean _inOlympiadMode = false;
    private boolean _OlympiadStart = false;
    private int _olympiadGameId = -1;
    private int _olympiadSide = -1;
    /**
     * Duel
     */
    private boolean _isInDuel = false;
    private boolean _startingDuel = false;
    private int _duelState = Duel.DUELSTATE_NODUEL;
    private int _duelId = 0;
    private SystemMessageId _noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;

    private Vehicle _vehicle = null;
    private Location _inVehiclePosition;
    private MountType _mountType = MountType.NONE;
    private int _mountNpcId;
    private int _mountLevel;
    /**
     * Store object used to summon the strider you are mounting
     **/
    private int mountObjectID = 0;
    private AdminTeleportType _teleportType = AdminTeleportType.NORMAL;
    private boolean _inCrystallize;
    private boolean _isCrafting;
    private long _offlineShopStart = 0;
    /**
     * True if the Player is sitting
     */
    private boolean _waitTypeSitting = false;
    /**
     * Location before entering Observer Mode
     */
    private Location _lastLoc;
    private boolean _observerMode = false;
    /**
     * The number of recommendation obtained by the Player
     */
    private int _recomHave; // how much I was recommended by others
    /**
     * The number of recommendation that the Player can give
     */
    private int _recomLeft; // how many recommendations I can give to others
    /**
     * Recommendation task
     **/
    private ScheduledFuture<?> _recoGiveTask;
    private ScheduledFuture<?> _onlineTimeUpdateTask;
    private PlayerWarehouse _warehouse;
    private PlayerRefund _refund;
    private PrivateStoreType privateStoreType = PrivateStoreType.NONE;
    private TradeList activeTradeList;
    private Warehouse activeWarehouse;
    private volatile Map<Integer, ManufactureItem> _manufactureItems;

    // Clan related attributes
    private String _storeName = "";
    private TradeList _sellList;
    private TradeList _buyList;
    // Multisell
    private PreparedMultisellListHolder _currentMultiSell = null;
    private boolean _noble = false;
    private boolean _hero = false;


    /**
     * The L2FolkInstance corresponding to the last Folk which one the player talked.
     */
    private Npc _lastFolkNpc = null;
    /**
     * Last NPC Id talked on a quest
     */
    private int _questNpcObject = 0;
    /**
     * Used for simulating Quest onTalk
     */
    private boolean _simulatedTalking = false;
    /**
     * The Pet of the Player
     */
    private Pet pet = null;
    /**
     * Servitors of the Player
     */
    private volatile Map<Integer, Summon> _servitors = null;
    /**
     * The L2Agathion of the Player
     */
    private int _agathionId = 0;
    // apparently, a Player CAN have both a summon AND a tamed beast at the same time!!
    // after Freya players can control more than one tamed beast
    private volatile Set<TamedBeast> tamedBeast = null;

    private MatchingRoom _matchingRoom;
    /**
     * The Clan Identifier of the Player
     */
    private int clanId;
    /**
     * The Clan object of the Player
     */
    private Clan _clan;

    private volatile EnumIntBitmask<ClanPrivilege> _clanPrivileges = new EnumIntBitmask<>(ClanPrivilege.class, false);
    /**
     * Player's pledge class (knight, Baron, etc.)
     */
    private int _pledgeClass = 0;

    private ScheduledFuture<?> _chargeTask = null;
    // Absorbed Souls
    private int _souls = 0;
    private ScheduledFuture<?> _soulTask = null;
    // WorldPosition used by TARGET_SIGNET_GROUND
    private Location _currentSkillWorldPosition;
    private AccessLevel accessLevel;

    // private byte _updateKnownCounter = 0;
    private boolean messageRefusing = false; // message refusal mode
    private boolean _silenceMode = false; // silence mode
    private List<Integer> _silenceModeExcluded; // silence mode
    private boolean _dietMode = false; // ignore weight penalty
    private boolean tradeRefusing = false; // Trade refusal

    private Party _party;
    // this is needed to find the inviting player for Party response
    // there can only be one active party request at once
    private Player activeRequester;
    private long requestExpireTime = 0;
    // Used for protection after teleport
    private long _spawnProtectEndTime = 0;
    private long _teleportProtectEndTime = 0;
    private volatile Map<Integer, ExResponseCommissionInfo> _lastCommissionInfos;
    @SuppressWarnings("rawtypes")
    private volatile Map<Class<? extends AbstractEvent>, AbstractEvent<?>> _events;
    private boolean _isOnCustomEvent = false;
    // protects a char from aggro mobs when getting up from fake death
    private long _recentFakeDeathEndTime = 0;
    /**
     * The fists Weapon of the Player (used when no weapon is equipped)
     */
    private Weapon _fistsWeaponItem;
    private volatile Map<Class<? extends AbstractRequest>, AbstractRequest> requests;

    /**
     * Event parameters
     */
    private PlayerEventHolder eventStatus = null;
    private byte _handysBlockCheckerEventArena = -1;
    private volatile Map<Integer, Skill> _transformSkills;
    private ScheduledFuture<?> _taskRentPet;
    private ScheduledFuture<?> _taskWater;
    private ScheduledFuture<?> _skillListRefreshTask;
    /**
     * Origin of the last incoming html action request.<br>
     * This can be used for htmls continuing the conversation with an npc.
     */
    private int _lastHtmlActionOriginObjId;

    /**
     * Skills queued because a skill is already in progress
     */
    private SkillUseHolder _queuedSkill;
    private boolean _canRevive = true;
    private int _reviveRequested = 0;
    private double _revivePower = 0;
    private boolean _revivePet = false;
    private double _cpUpdateIncCheck = .0;
    private double _cpUpdateDecCheck = .0;
    private double _cpUpdateInterval = .0;
    private double _mpUpdateIncCheck = .0;
    private double _mpUpdateDecCheck = .0;
    private double _mpUpdateInterval = .0;
    private double _originalCp = .0;
    private double _originalHp = .0;
    private double _originalMp = .0;
    /**
     * Char Coords from Client
     */
    private int _clientX;
    private int _clientY;
    private int _clientZ;
    private int _clientHeading;
    private volatile long _fallingTimestamp = 0;
    private volatile int _fallingDamage = 0;
    private Future<?> _fallingDamageTask = null;
    private int _multiSocialTarget = 0;
    private int _multiSociaAction = 0;
    private MovieHolder _movieHolder = null;
    private String _adminConfirmCmd = null;
    private volatile long _lastItemAuctionInfoRequest = 0;
    private Future<?> _PvPRegTask;
    private long _pvpFlagLasts;
    private long _notMoveUntil = 0;
    /**
     * Map containing all custom skills of this player.
     */
    private Map<Integer, Skill> _customSkills = null;
    private volatile int _actionMask;
    private int _questZoneId = -1;

    // Save responder name for log it
    private String _lastPetitionGmName = null;
    private boolean hasCharmOfCourage = false;
    // Selling buffs system
    private boolean _isSellingBuffs = false;
    private List<SellBuffHolder> _sellingBuffs = null;
    /**
     * List of all QuestState instance that needs to be notified of this Player's or its pet's death
     */
    private volatile Set<QuestState> _notifyQuestOfDeathList;
    private ScheduledFuture<?> _taskWarnUserTakeBreak;

    @Override
    protected void initCharStatusUpdateValues() {
        super.initCharStatusUpdateValues();

        _cpUpdateInterval = getMaxCp() / MAX_STATUS_BAR_PX;
        _cpUpdateIncCheck = getMaxCp();
        _cpUpdateDecCheck = getMaxCp() - _cpUpdateInterval;

        _mpUpdateInterval = getMaxMp() / MAX_STATUS_BAR_PX;
        _mpUpdateIncCheck = getMaxMp();
        _mpUpdateDecCheck = getMaxMp() - _mpUpdateInterval;
    }


    /**
     * Retrieve a Player from the characters table of the database and add it in _allObjects of the L2world (call restore method).<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Retrieve the Player from the characters table of the database</li>
     * <li>Add the Player object in _allObjects</li>
     * <li>Set the x,y,z position of the Player and make it invisible</li>
     * <li>Update the overloaded status of the Player</li>
     * </ul>
     *
     * @param objectId Identifier of the object to initialized
     * @return The Player loaded from the database
     */
    public static Player load(int objectId) {
        return restore(objectId);
    }

    /**
     * Retrieve a Player from the characters table of the database and add it in _allObjects of the L2world. <B><U> Actions</U> :</B>
     * <li>Retrieve the Player from the characters table of the database</li>
     * <li>Add the Player object in _allObjects</li>
     * <li>Set the x,y,z position of the Player and make it invisible</li>
     * <li>Update the overloaded status of the Player</li>
     *
     * @param objectId Identifier of the object to initialized
     * @return The Player loaded from the database
     */
    private static Player restore(int objectId) {
        var playerDAO = getDAO(PlayerDAO.class);
        var character = playerDAO.findById(objectId);
        if(isNull(character)) {
            return null;
        }
        var template = PlayerTemplateData.getInstance().getTemplate(character.getClassId());
        Player player = new Player(character, template);
        player.variables = getDAO(PlayerVariablesDAO.class).findById(objectId);
        player.statsData = playerDAO.findPlayerStatsData(objectId);

        player.costumes = playerDAO.findCostumes(objectId);
        doIfNonNull(playerDAO.findPlayerCostumeCollection(objectId), c -> player.activeCostumesCollection = c);

        if(isNull(player.statsData)) { // TODO remove late, just temp fix to already created players
            player.statsData = PlayerStatsData.init(objectId);
            player.updateCharacteristicPoints();
        }

        player.teleportFavorites = playerDAO.findTeleportFavorites(objectId);

        player.setHeading(character.getHeading());
        player.getStats().setExp(character.getExp());
        player.getStats().setLevel(character.getLevel());
        player.getStats().setSp(character.getSp());
        player.setReputation(character.getReputation());
        player.setFame(character.getFame());
        player.setPvpKills(character.getPvP());
        player.setPkKills(character.getPk());
        player.setOnlineTime(character.getOnlineTime());
        player.setNoble(character.isNobless());
        player.getStats().setVitalityPoints(character.getVitalityPoints());

        player.setHero(Hero.getInstance().isHero(objectId));

        if(player.getLevel() >= 40) {
            player.initElementalSpirits();
        }

        if (character.getClanId() > 0) {
            player.setClan(ClanTable.getInstance().getClan(character.getClanId()));
        }

        if (player.getClan() != null) {
            if (player.getClan().getLeaderId() != player.getObjectId()) {
                if (player.getPowerGrade() == 0) {
                    player.setPowerGrade(5);
                }
                player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
            } else {
                player.getClanPrivileges().setAll();
                player.setPowerGrade(1);
            }
            player.setPledgeClass(ClanMember.calculatePledgeClass(player));
        } else {
            if (player.isNoble()) {
                player.setPledgeClass(5);
            }

            if (player.isHero()) {
                player.setPledgeClass(8);
            }

            player.getClanPrivileges().clear();
        }

        player.setTitle(character.getTitle());

        if (character.getTitleColor() != PlayerAppearance.DEFAULT_TITLE_COLOR) {
            player.getAppearance().setTitleColor(character.getTitleColor());
        }

        player.setFistsWeaponItem(player.findFistsWeaponItem());
        player.setUptime(System.currentTimeMillis());
        player.setClassIndex(0);

        if (restoreSubClassData(player)) {
            if (character.getClassId() != player.getBaseClass()) {
                for (SubClass subClass : player.getSubClasses().values()) {
                    if (subClass.getClassId() == character.getClassId()) {
                        player.setClassIndex(subClass.getClassIndex());
                    }
                }
            }
        }

        if ((player.getClassIndex() == 0) && (character.getClassId() != player.getBaseClass())) {
            // Subclass in use but doesn't exist in DB -
            // a possible restart-while-modifysubclass cheat has been attempted.
            // Switching to use base class
            player.setClassId(player.getBaseClass());
            LOGGER.warn("Player {} reverted to base class. Possibly has tried a relogin exploit while subclassing.", player);
        } else {
            player._activeClass = character.getClassId();
        }

        player.setXYZInvisible(character.getX(), character.getY(), character.getZ());
        player.setLastServerPosition(character.getX(), character.getY(), character.getZ());

        player.setBookMarkSlot(character.getBookMarkSlot());
        player.setLang(character.getLanguage());

        // TODO this info should stay on GameClient, since it was already loaded
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT charId, char_name FROM characters WHERE account_name=? AND charId<>?")) {
            // Retrieve the Player from the characters table of the database
            stmt.setString(1, character.getAccountName());
            stmt.setInt(2, objectId);

            ResultSet chars = stmt.executeQuery();
            while (chars.next()) {
                player._chars.put(chars.getInt("charId"), chars.getString("char_name"));
            }

            if (player.isGM()) {
                final long masks = player.getVariables().getLong(COND_OVERRIDE_KEY, PcCondOverride.getAllExceptionsMask());
                player.setOverrideCond(masks);
            }

            // Retrieve from the database all items of this Player and add them to _inventory
            player.getInventory().restore();
            // Retrieve from the database all secondary data of this Player
            // Note that Clan, Noblesse and Hero skills are given separately and not here.
            // Retrieve from the database all skills of this Player and add them to _skills
            player.restoreCharData();

            // Reward auto-get skills and all available skills if auto-learn skills is true.
            player.rewardSkills();

            player.getFreight().restore();
            if (!Config.WAREHOUSE_CACHE) {
                player.getWarehouse();
            }

            player.restoreItemReuse();

            // Restore player shortcuts
            player.restoreShortCuts();

            // Initialize status update cache
            player.initStatusUpdateCache();

            // Restore current Cp, HP and MP values
            player.setCurrentCp(character.getCurrentCp());
            player.setCurrentHp(character.getCurrentHp());
            player.setCurrentMp(character.getCurrentMp());

            player.setOriginalCpHpMp(character.getCurrentCp(), character.getCurrentHp(), character.getCurrentMp());

            if (character.getCurrentHp() < 0.5) {
                player.setIsDead(true);
                player.stopHpMpRegeneration();
            }

            // Restore pet if exists in the world
            player.setPet(World.getInstance().findPet(player.getObjectId()));
            final Summon pet = player.getPet();
            if (pet != null) {
                pet.setOwner(player);
            }

            if (player.hasServitors()) {
                for (Summon summon : player.getServitors().values()) {
                    summon.setOwner(player);
                }
            }

            // Recalculate all stats
            player.getStats().recalculateStats(false);

            // Update the overloaded status of the Player
            player.refreshOverloaded(false);
            player.restoreFriendList();

            player.loadRecommendations();
            player.startRecoGiveTask();
            player.startOnlineTimeUpdateTask();

            player.setOnlineStatus(true, false);
            SaveTaskManager.getInstance().registerPlayer(player);
        } catch (Exception e) {
            LOGGER.error("Failed loading character.", e);
        }
        return player;
    }

    /**
     * Restores sub-class data for the Player, used to check the current class index for the character.
     *
     * @param player
     * @return
     */
    private static boolean restoreSubClassData(Player player) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_SUBCLASSES)) {
            statement.setInt(1, player.getObjectId());
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    final SubClass subClass = new SubClass();
                    subClass.setClassId(rset.getInt("class_id"));
                    subClass.setIsDualClass(rset.getBoolean("dual_class"));
                    subClass.setVitalityPoints(rset.getInt("vitality_points"));
                    subClass.setLevel(rset.getByte("level"));
                    subClass.setExp(rset.getLong("exp"));
                    subClass.setSp(rset.getLong("sp"));
                    subClass.setClassIndex(rset.getInt("class_index"));

                    // Enforce the correct indexing of _subClasses against their class indexes.
                    player.getSubClasses().put(subClass.getClassIndex(), subClass);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not restore classes for " + player.getName() + ": " + e.getMessage(), e);
        }
        return true;
    }

    public long getPvpFlagLasts() {
        return _pvpFlagLasts;
    }

    public void setPvpFlagLasts(long time) {
        _pvpFlagLasts = time;
    }

    public void startPvPFlag() {
        updatePvPFlag(1);

        if (_PvPRegTask == null) {
            _PvPRegTask = ThreadPool.scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
        }
    }

    public void stopPvpRegTask() {
        if (_PvPRegTask != null) {
            _PvPRegTask.cancel(true);
            _PvPRegTask = null;
        }
    }

    public void stopPvPFlag() {
        stopPvpRegTask();

        updatePvPFlag(0);

        _PvPRegTask = null;
    }

    public boolean isSellingBuffs() {
        return _isSellingBuffs;
    }

    public void setIsSellingBuffs(boolean val) {
        _isSellingBuffs = val;
    }

    public List<SellBuffHolder> getSellingBuffs() {
        if (_sellingBuffs == null) {
            _sellingBuffs = new ArrayList<>();
        }
        return _sellingBuffs;
    }

    public String getAccountName() {
        return _client == null ? data.getAccountName(): _client.getAccountName();
    }

    public String getAccountNamePlayer() {
        return data.getAccountName();
    }

    public Map<Integer, String> getAccountChars() {
        return _chars;
    }

    public int getRelation(Player target) {
        final Clan clan = getClan();
        final Party party = getParty();
        final Clan targetClan = target.getClan();

        int result = 0;

        if (clan != null) {
            result |= RelationChanged.RELATION_CLAN_MEMBER;
            if (clan == target.getClan()) {
                result |= RelationChanged.RELATION_CLAN_MATE;
            }
            if (getAllyId() != 0) {
                result |= RelationChanged.RELATION_ALLY_MEMBER;
            }
        }
        if (isClanLeader()) {
            result |= RelationChanged.RELATION_LEADER;
        }
        if ((party != null) && (party == target.getParty())) {
            result |= RelationChanged.RELATION_HAS_PARTY;
            for (int i = 0; i < party.getMembers().size(); i++) {
                if (party.getMembers().get(i) != this) {
                    continue;
                }
                switch (i) {
                    case 0: {
                        result |= RelationChanged.RELATION_PARTYLEADER; // 0x10
                        break;
                    }
                    case 1: {
                        result |= RelationChanged.RELATION_PARTY4; // 0x8
                        break;
                    }
                    case 2: {
                        result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x7
                        break;
                    }
                    case 3: {
                        result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2; // 0x6
                        break;
                    }
                    case 4: {
                        result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY1; // 0x5
                        break;
                    }
                    case 5: {
                        result |= RelationChanged.RELATION_PARTY3; // 0x4
                        break;
                    }
                    case 6: {
                        result |= RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x3
                        break;
                    }
                    case 7: {
                        result |= RelationChanged.RELATION_PARTY2; // 0x2
                        break;
                    }
                    case 8: {
                        result |= RelationChanged.RELATION_PARTY1; // 0x1
                        break;
                    }
                }
            }
        }
        if (_siegeState != 0) {
            result |= RelationChanged.RELATION_INSIEGE;
            if (getSiegeState() != target.getSiegeState()) {
                result |= RelationChanged.RELATION_ENEMY;
            } else {
                result |= RelationChanged.RELATION_ALLY;
            }
            if (_siegeState == 1) {
                result |= RelationChanged.RELATION_ATTACKER;
            }
        }
        if ((clan != null) && (targetClan != null)) {
            if ((target.getPledgeType() != Clan.SUBUNIT_ACADEMY) && (getPledgeType() != Clan.SUBUNIT_ACADEMY)) {
                ClanWar war = clan.getWarWith(target.getClan().getId());
                if (war != null) {
                    switch (war.getState()) {
                        case DECLARATION, BLOOD_DECLARATION -> {
                            result |= RelationChanged.RELATION_DECLARED_WAR;
                        }
                        case MUTUAL -> {
                            result |= RelationChanged.RELATION_DECLARED_WAR;
                            result |= RelationChanged.RELATION_MUTUAL_WAR;
                        }
                    }
                }
            }
        }
        if (_handysBlockCheckerEventArena != -1) {
            result |= RelationChanged.RELATION_INSIEGE;
            final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(getBlockCheckerArena());
            if (holder.getPlayerTeam(this) == 0) {
                result |= RelationChanged.RELATION_ENEMY;
            } else {
                result |= RelationChanged.RELATION_ALLY;
            }
            result |= RelationChanged.RELATION_ATTACKER;
        }
        return result;
    }

    @Override
    public final PlayerStats getStats() {
        return (PlayerStats) super.getStats();
    }

    @Override
    public void initCharStat() {
        setStat(new PlayerStats(this));
    }

    @Override
    public final PlayerStatus getStatus() {
        return (PlayerStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new PlayerStatus(this));
    }

    public final PlayerAppearance getAppearance() {
        return appearance;
    }

    public final boolean isHairAccessoryEnabled() {
        return getVariables().getBoolean(PlayerVariables.HAIR_ACCESSORY_VARIABLE_NAME, true);
    }

    public final void setHairAccessoryEnabled(boolean enabled) {
        getVariables().set(PlayerVariables.HAIR_ACCESSORY_VARIABLE_NAME, enabled);
    }

    public final PlayerTemplate getBaseTemplate() {
        return PlayerTemplateData.getInstance().getTemplate(data.getBaseClass());
    }

    /**
     * @return the PlayerTemplate link to the Player.
     */
    @Override
    public final PlayerTemplate getTemplate() {
        return (PlayerTemplate) super.getTemplate();
    }

    /**
     * @param newclass
     */
    public void setTemplate(ClassId newclass) {
        super.setTemplate(PlayerTemplateData.getInstance().getTemplate(newclass));
    }

    @Override
    protected CreatureAI initAI() {
        return new PlayerAI(this);
    }

    /**
     * Return the Level of the Player.
     */
    @Override
    public final int getLevel() {
        return getStats().getLevel();
    }

    public boolean isInStoreMode() {
        return privateStoreType != PrivateStoreType.NONE;
    }

    public boolean isCrafting() {
        return _isCrafting;
    }

    public void setIsCrafting(boolean isCrafting) {
        _isCrafting = isCrafting;
    }

    /**
     * @return a table containing all Common RecipeList of the Player.
     */
    public RecipeList[] getCommonRecipeBook() {
        return _commonRecipeBook.values().toArray(RecipeList[]::new);
    }

    /**
     * @return a table containing all Dwarf RecipeList of the Player.
     */
    public RecipeList[] getDwarvenRecipeBook() {
        return _dwarvenRecipeBook.values().toArray(RecipeList[]::new);
    }

    /**
     * Add a new L2RecipList to the table _commonrecipebook containing all RecipeList of the Player
     *
     * @param recipe   The RecipeList to add to the _recipebook
     * @param saveToDb
     */
    public void registerCommonRecipeList(RecipeList recipe, boolean saveToDb) {
        _commonRecipeBook.put(recipe.getId(), recipe);

        if (saveToDb) {
            insertNewRecipeData(recipe.getId(), false);
        }
    }

    /**
     * Add a new L2RecipList to the table _recipebook containing all RecipeList of the Player
     *
     * @param recipe   The RecipeList to add to the _recipebook
     * @param saveToDb
     */
    public void registerDwarvenRecipeList(RecipeList recipe, boolean saveToDb) {
        _dwarvenRecipeBook.put(recipe.getId(), recipe);

        if (saveToDb) {
            insertNewRecipeData(recipe.getId(), true);
        }
    }

    /**
     * @param recipeId The Identifier of the RecipeList to check in the player's recipe books
     * @return {@code true}if player has the recipe on Common or Dwarven Recipe book else returns {@code false}
     */
    public boolean hasRecipeList(int recipeId) {
        return _dwarvenRecipeBook.containsKey(recipeId) || _commonRecipeBook.containsKey(recipeId);
    }

    /**
     * Tries to remove a L2RecipList from the table _DwarvenRecipeBook or from table _CommonRecipeBook, those table contain all RecipeList of the Player
     *
     * @param recipeId The Identifier of the RecipeList to remove from the _recipebook
     */
    public void unregisterRecipeList(int recipeId) {
        if (_dwarvenRecipeBook.remove(recipeId) != null) {
            deleteRecipeData(recipeId, true);
        } else if (_commonRecipeBook.remove(recipeId) != null) {
            deleteRecipeData(recipeId, false);
        } else {
            LOGGER.warn("Attempted to remove unknown RecipeList: " + recipeId);
        }

        shortcuts.deleteShortcuts(s -> s.getShortcutId() == recipeId && s.getType() == ShortcutType.RECIPE);
    }

    private void insertNewRecipeData(int recipeId, boolean isDwarf) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO character_recipebook (charId, id, classIndex, type) values(?,?,?,?)")) {
            statement.setInt(1, getObjectId());
            statement.setInt(2, recipeId);
            statement.setInt(3, isDwarf ? _classIndex : 0);
            statement.setInt(4, isDwarf ? 1 : 0);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.warn("SQL exception while inserting recipe: " + recipeId + " from character " + getObjectId(), e);
        }
    }

    private void deleteRecipeData(int recipeId, boolean isDwarf) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_recipebook WHERE charId=? AND id=? AND classIndex=?")) {
            statement.setInt(1, getObjectId());
            statement.setInt(2, recipeId);
            statement.setInt(3, isDwarf ? _classIndex : 0);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.warn("SQL exception while deleting recipe: " + recipeId + " from character " + getObjectId(), e);
        }
    }

    /**
     * @return the Id for the last talked quest NPC.
     */
    public int getLastQuestNpcObject() {
        return _questNpcObject;
    }

    public void setLastQuestNpcObject(int npcId) {
        _questNpcObject = npcId;
    }

    public boolean isSimulatingTalking() {
        return _simulatedTalking;
    }

    public void setSimulatedTalking(boolean value) {
        _simulatedTalking = value;
    }

    /**
     * @param quest The name of the quest
     * @return the QuestState object corresponding to the quest name.
     */
    public QuestState getQuestState(String quest) {
        return _quests.get(quest);
    }

    /**
     * Add a QuestState to the table _quest containing all quests began by the Player.
     *
     * @param qs The QuestState to add to _quest
     */
    public void setQuestState(QuestState qs) {
        _quests.put(qs.getQuestName(), qs);
    }

    /**
     * Verify if the player has the quest state.
     *
     * @param quest the quest state to check
     * @return {@code true} if the player has the quest state, {@code false} otherwise
     */
    public boolean hasQuestState(String quest) {
        return _quests.containsKey(quest);
    }

    /**
     * Remove a QuestState from the table _quest containing all quests began by the Player.
     *
     * @param quest The name of the quest
     */
    public void delQuestState(String quest) {
        _quests.remove(quest);
    }

    /**
     * @return List of {@link QuestState}s of the current player.
     */
    public Collection<QuestState> getAllQuestStates() {
        return _quests.values();
    }

    /**
     * @return a table containing all Quest in progress from the table _quests.
     */
    public List<Quest> getAllActiveQuests() {
        //@formatter:off
        return _quests.values().stream()
                .filter(QuestState::isStarted)
                .map(QuestState::getQuest)
                .filter(Objects::nonNull)
                .filter(q -> q.getId() > 1)
                .collect(Collectors.toList());
        //@formatter:on
    }

    public void processQuestEvent(String questName, String event) {
        final Quest quest = QuestManager.getInstance().getQuest(questName);
        if ((quest == null) || (event == null) || event.isEmpty()) {
            return;
        }

        final Npc target = _lastFolkNpc;

        if ((target != null) && MathUtil.isInsideRadius2D(this, target, Npc.INTERACTION_DISTANCE)) {
            quest.notifyEvent(event, target, this);
        } else if (_questNpcObject > 0) {
            final WorldObject object = World.getInstance().findObject(getLastQuestNpcObject());

            if (GameUtils.isNpc(object) && MathUtil.isInsideRadius2D(this, object, Npc.INTERACTION_DISTANCE)) {
                final Npc npc = (Npc) object;
                quest.notifyEvent(event, npc, this);
            }
        }
    }

    /**
     * Add QuestState instance that is to be notified of Player's death.
     *
     * @param qs The QuestState that subscribe to this event
     */
    public void addNotifyQuestOfDeath(QuestState qs) {
        if (qs == null) {
            return;
        }

        if (!getNotifyQuestOfDeath().contains(qs)) {
            getNotifyQuestOfDeath().add(qs);
        }
    }

    /**
     * Remove QuestState instance that is to be notified of Player's death.
     *
     * @param qs The QuestState that subscribe to this event
     */
    public void removeNotifyQuestOfDeath(QuestState qs) {
        if ((qs == null) || (_notifyQuestOfDeathList == null)) {
            return;
        }

        _notifyQuestOfDeathList.remove(qs);
    }

    /**
     * @return a list of QuestStates which registered for notify of death of this Player.
     */
    public final Set<QuestState> getNotifyQuestOfDeath() {
        if (_notifyQuestOfDeathList == null) {
            synchronized (this) {
                if (_notifyQuestOfDeathList == null) {
                    _notifyQuestOfDeathList = ConcurrentHashMap.newKeySet();
                }
            }
        }

        return _notifyQuestOfDeathList;
    }

    public final boolean isNotifyQuestOfDeathEmpty() {
        return (_notifyQuestOfDeathList == null) || _notifyQuestOfDeathList.isEmpty();
    }


    public Shortcut getShortcut(int room) {
        return shortcuts.getShortcut(room);
    }

    /**
     * Add a L2shortCut to the Player _shortCuts
     *
     * @param shortcut
     */
    public void registerShortCut(Shortcut shortcut) {
        shortcuts.registerShortCut(shortcut);
        sendPacket(new ShortCutRegister(shortcut));
    }

    /**
     * Updates the shortcut bars with the new skill.
     *
     * @param skillId       the skill Id to search and update.
     * @param skillLevel    the skill level to update.
     * @param skillSubLevel the skill sub level to update.
     */
    public void updateShortCuts(int skillId, int skillLevel, int skillSubLevel) {
        shortcuts.updateShortCuts(skillId, skillLevel, skillSubLevel);
    }

    public void deleteShortcut(int room) {
        shortcuts.deleteShortcut(room);
    }

    public void registerMacro(Macro macro) {
        macros.registerMacro(macro);
    }

    /**
     * @param id the macro Id to delete.
     */
    public void deleteMacro(int id) {
        macros.deleteMacro(id);
    }

    /**
     * @return all L2Macro of the Player.
     */
    public MacroList getMacros() {
        return macros;
    }

    /**
     * Get the siege state of the Player.
     *
     * @return 1 = attacker, 2 = defender, 0 = not involved
     */
    public byte getSiegeState() {
        return _siegeState;
    }

    /**
     * Set the siege state of the Player.
     *
     * @param siegeState 1 = attacker, 2 = defender, 0 = not involved
     */
    public void setSiegeState(byte siegeState) {
        _siegeState = siegeState;
    }

    public boolean isRegisteredOnThisSiegeField(int val) {
        if ((_siegeSide != val) && ((_siegeSide < 81) || (_siegeSide > 89))) {
            return false;
        }
        return true;
    }

    public int getSiegeSide() {
        return _siegeSide;
    }

    /**
     * Set the siege Side of the Player.
     *
     * @param val
     */
    public void setSiegeSide(int val) {
        _siegeSide = val;
    }

    @Override
    public byte getPvpFlag() {
        return _pvpFlag;
    }

    /**
     * Set the PvP Flag of the Player.
     *
     * @param pvpFlag
     */
    public void setPvpFlag(int pvpFlag) {
        _pvpFlag = (byte) pvpFlag;
    }

    @Override
    public void updatePvPFlag(int value) {
        if (_pvpFlag == value) {
            return;
        }
        setPvpFlag(value);

        final StatusUpdate su = new StatusUpdate(this);
        computeStatusUpdate(su, StatusUpdateType.PVP_FLAG);
        if (su.hasUpdates()) {
            broadcastPacket(su);
            sendPacket(su);
        }

        // If this player has a pet update the pets pvp flag as well
        if (hasSummon()) {
            final RelationChanged rc = new RelationChanged();
            final Summon pet = this.pet;
            if (pet != null) {
                rc.addRelation(pet, getRelation(this), false);
            }
            if (hasServitors()) {
                getServitors().values().forEach(s -> rc.addRelation(s, getRelation(this), false));
            }
            sendPacket(rc);
        }

        World.getInstance().forEachVisibleObject(this, Player.class, player ->
        {
            if (!isVisibleFor(player)) {
                return;
            }

            final int relation = getRelation(player);
            final Integer oldrelation = getKnownRelations().get(player.getObjectId());
            if ((oldrelation == null) || (oldrelation != relation)) {
                final RelationChanged rc = new RelationChanged();
                rc.addRelation(this, relation, isAutoAttackable(player));
                if (hasSummon()) {
                    final Summon pet = this.pet;
                    if (pet != null) {
                        rc.addRelation(pet, relation, isAutoAttackable(player));
                    }
                    if (hasServitors()) {
                        getServitors().values().forEach(s -> rc.addRelation(s, relation, isAutoAttackable(player)));
                    }
                }
                player.sendPacket(rc);
                getKnownRelations().put(player.getObjectId(), relation);
            }
        });
    }

    @Override
    public void revalidateZone(boolean force) {
        // Cannot validate if not in a world region (happens during teleport)
        if (getWorldRegion() == null) {
            return;
        }

        // This function is called too often from movement code
        if (force) {
            _zoneValidateCounter = 4;
        } else {
            _zoneValidateCounter--;
            if (_zoneValidateCounter < 0) {
                _zoneValidateCounter = 4;
            } else {
                return;
            }
        }

        ZoneManager.getInstance().getRegion(this).revalidateZones(this);

        if (Config.ALLOW_WATER) {
            checkWaterState();
        }

        if (isInsideZone(ZoneType.ALTERED)) {
            if (_lastCompassZone == ExSetCompassZoneCode.ALTEREDZONE) {
                return;
            }
            _lastCompassZone = ExSetCompassZoneCode.ALTEREDZONE;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.ALTEREDZONE);
            sendPacket(cz);
        } else if (isInsideZone(ZoneType.SIEGE)) {
            if (_lastCompassZone == ExSetCompassZoneCode.SIEGEWARZONE2) {
                return;
            }
            _lastCompassZone = ExSetCompassZoneCode.SIEGEWARZONE2;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.SIEGEWARZONE2);
            sendPacket(cz);
        } else if (isInsideZone(ZoneType.PVP)) {
            if (_lastCompassZone == ExSetCompassZoneCode.PVPZONE) {
                return;
            }
            _lastCompassZone = ExSetCompassZoneCode.PVPZONE;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.PVPZONE);
            sendPacket(cz);
        } else if (isInsideZone(ZoneType.PEACE)) {
            if (_lastCompassZone == ExSetCompassZoneCode.PEACEZONE) {
                return;
            }
            _lastCompassZone = ExSetCompassZoneCode.PEACEZONE;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.PEACEZONE);
            sendPacket(cz);
        } else {
            if (_lastCompassZone == ExSetCompassZoneCode.GENERALZONE) {
                return;
            }
            if (_lastCompassZone == ExSetCompassZoneCode.SIEGEWARZONE2) {
                updatePvPStatus();
            }
            _lastCompassZone = ExSetCompassZoneCode.GENERALZONE;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.GENERALZONE);
            sendPacket(cz);
        }
    }

    /**
     * @return True if the Player can Craft Dwarven Recipes.
     */
    public boolean hasDwarvenCraft() {
        return getSkillLevel(CommonSkill.CREATE_DWARVEN.getId()) >= 1;
    }

    public int getDwarvenCraft() {
        return getSkillLevel(CommonSkill.CREATE_DWARVEN.getId());
    }

    /**
     * @return True if the Player can Craft Dwarven Recipes.
     */
    public boolean hasCommonCraft() {
        return getSkillLevel(CommonSkill.CREATE_COMMON.getId()) >= 1;
    }

    public int getCommonCraft() {
        return getSkillLevel(CommonSkill.CREATE_COMMON.getId());
    }

    /**
     * @return the PK counter of the Player.
     */
    public int getPkKills() {
        return _pkKills;
    }

    /**
     * Set the PK counter of the Player.
     *
     * @param pkKills
     */
    public void setPkKills(int pkKills) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPKChanged(this, _pkKills, pkKills), this);
        _pkKills = pkKills;
    }

    /**
     * @return the number of recommendation obtained by the Player.
     */
    public int getRecomHave() {
        return _recomHave;
    }

    /**
     * Set the number of recommendation obtained by the Player (Max : 255).
     *
     * @param value
     */
    public void setRecomHave(int value) {
        _recomHave = min(Math.max(value, 0), 255);
    }

    /**
     * Increment the number of recommendation obtained by the Player (Max : 255).
     */
    protected void incRecomHave() {
        if (_recomHave < 255) {
            _recomHave++;
        }
    }

    /**
     * @return the number of recommendation that the Player can give.
     */
    public int getRecomLeft() {
        return _recomLeft;
    }

    /**
     * Set the number of recommendation obtained by the Player (Max : 255).
     *
     * @param value
     */
    public void setRecomLeft(int value) {
        _recomLeft = min(Math.max(value, 0), 255);
    }

    /**
     * Increment the number of recommendation that the Player can give.
     */
    protected void decRecomLeft() {
        if (_recomLeft > 0) {
            _recomLeft--;
        }
    }

    public void giveRecom(Player target) {
        target.incRecomHave();
        decRecomLeft();
    }

    public void setInitialReputation(int reputation) {
        super.setReputation(reputation);
    }

    /**
     * Set the reputation of the PlayerInstance and send a Server->Client packet StatusUpdate (broadcast).
     *
     * @param reputation
     */
    @Override
    public void setReputation(int reputation) {
        // Notify to scripts.
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerReputationChanged(this, getReputation(), reputation), this);

        if (reputation > Config.MAX_REPUTATION) // Max count of positive reputation
        {
            reputation = Config.MAX_REPUTATION;
        }

        if (getReputation() == reputation) {
            return;
        }

        if ((getReputation() >= 0) && (reputation < 0)) {
            World.getInstance().forEachVisibleObject(this, Guard.class, object ->
            {
                if (object.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                    object.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                }
            });
        }

        super.setReputation(reputation);

        sendPacket(getSystemMessage(SystemMessageId.YOUR_REPUTATION_HAS_BEEN_CHANGED_TO_S1).addInt(getReputation()));
        broadcastReputation();
    }

    public int getWeightPenalty() {
        if (_dietMode) {
            return 0;
        }
        return _curWeightPenalty;
    }

    /**
     * Update the overloaded status of the Player.
     *
     * @param broadcast TODO
     */
    public void refreshOverloaded(boolean broadcast) {
        final int maxLoad = getMaxLoad();
        if (maxLoad > 0) {
            final long weightproc = (((getCurrentLoad() - getBonusWeightPenalty()) * 1000) / getMaxLoad());
            int newWeightPenalty;
            if ((weightproc < 500) || _dietMode) {
                newWeightPenalty = 0;
            } else if (weightproc < 666) {
                newWeightPenalty = 1;
            } else if (weightproc < 800) {
                newWeightPenalty = 2;
            } else if (weightproc < 1000) {
                newWeightPenalty = 3;
            } else {
                newWeightPenalty = 4;
            }

            if (_curWeightPenalty != newWeightPenalty) {
                _curWeightPenalty = newWeightPenalty;
                if ((newWeightPenalty > 0) && !_dietMode) {
                    addSkill(SkillEngine.getInstance().getSkill(CommonSkill.WEIGHT_PENALTY.getId(), newWeightPenalty));
                    setIsOverloaded(getCurrentLoad() > maxLoad);
                } else {
                    removeSkill(getKnownSkill(4270), false, true);
                    setIsOverloaded(false);
                }
                if (broadcast) {
                    sendPacket(new EtcStatusUpdate(this));
                    broadcastUserInfo();
                }
            }
        }
    }

    public void useEquippableItem(Item item, boolean abortAttack) {
        Set<Item> modifiedItems;
        final int oldInvLimit = getInventoryLimit();
        SystemMessage sm;

        if (item.isEquipped()) {
            var bodyPart = BodyPart.fromEquippedPaperdoll(item);
            if (bodyPart.isAnyOf(TALISMAN, BROOCH_JEWEL, AGATHION, ARTIFACT)) {
                modifiedItems = inventory.unEquipItemInSlotAndRecord(InventorySlot.fromId(item.getLocationSlot()));
            } else {
                modifiedItems = inventory.unEquipItemInBodySlotAndRecord(bodyPart);
            }

            if (item.getEnchantLevel() > 0) {
                sm = getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(item.getEnchantLevel()).addItemName(item);
            } else {
                sm = getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED).addItemName(item);
            }
            sendPacket(sm);
        } else {
            modifiedItems = inventory.equipItemAndRecord(item);

            if (item.isEquipped()) {
                if (item.getEnchantLevel() > 0) {
                    sm = getSystemMessage(SystemMessageId.EQUIPPED_S1_S2).addInt(item.getEnchantLevel());
                } else {
                    sm = getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
                }
                sm.addItemName(item);
                sendPacket(sm);

                if (item.getBodyPart().isAnyOf(RIGHT_HAND, TWO_HAND)) {
                    rechargeShot(ShotType.SOULSHOTS);
                    rechargeShot(ShotType.SPIRITSHOTS);
                }
            } else {
                sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
            }
        }

        broadcastUserInfo();
        sendInventoryUpdate(new InventoryUpdate(modifiedItems));

        if (abortAttack) {
            abortAttack();
        }

        if (getInventoryLimit() != oldInvLimit) {
            sendPacket(new ExStorageMaxCount(this));
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerEquipItem(this, item), this);
    }

    /**
     * @return the the PvP Kills of the Player (Number of player killed during a PvP).
     */
    public int getPvpKills() {
        return _pvpKills;
    }

    /**
     * Set the the PvP Kills of the Player (Number of player killed during a PvP).
     *
     * @param pvpKills
     */
    public void setPvpKills(int pvpKills) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPvPChanged(this, _pvpKills, pvpKills), this);
        _pvpKills = pvpKills;
    }

    /**
     * @return the Fame of this Player
     */
    public int getFame() {
        return _fame;
    }

    /**
     * Set the Fame of this L2PcInstane
     *
     * @param fame
     */
    public void setFame(int fame) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFameChanged(this, _fame, fame), this);
        _fame = Math.min(fame, Config.MAX_PERSONAL_FAME_POINTS);
    }

    /**
     * @return the Raidboss points of this PlayerInstance
     */
    public int getRaidbossPoints() {
        return data.getRaidBossPoints();
    }


    public void setRaidbossPoints(int points) {
        data.setRaidbossPoints(points);
    }

    public void increaseRaidbossPoints(int increasePoints) {
        setRaidbossPoints(data.getRaidBossPoints() + increasePoints);
    }

    /**
     * @return the ClassId object of the Player contained in PlayerTemplate.
     */
    public ClassId getClassId() {
        return getTemplate().getClassId();
    }

    /**
     * Set the template of the Player.
     *
     * @param Id The Identifier of the PlayerTemplate to set to the Player
     */
    public void setClassId(int Id) {
        if (!_subclassLock.tryLock()) {
            return;
        }

        try {
            if ((getLvlJoinedAcademy() != 0) && (_clan != null) && CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, Id)) {
                if (getLvlJoinedAcademy() <= 16) {
                    _clan.addReputationScore(Config.JOIN_ACADEMY_MAX_REP_SCORE, true);
                } else if (getLvlJoinedAcademy() >= 39) {
                    _clan.addReputationScore(Config.JOIN_ACADEMY_MIN_REP_SCORE, true);
                } else {
                    _clan.addReputationScore((Config.JOIN_ACADEMY_MAX_REP_SCORE - ((getLvlJoinedAcademy() - 16) * 20)), true);
                }
                setLvlJoinedAcademy(0);
                // oust pledge member from the academy, cuz he has finished his 2nd class transfer
                final SystemMessage msg = getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED);
                msg.addPcName(this);
                _clan.broadcastToOnlineMembers(msg);
                _clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(getName()));
                _clan.removeClanMember(getObjectId(), 0);
                sendPacket(SystemMessageId.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_YOU_CAN_NOW_JOIN_A_CLAN_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES);

                // receive graduation gift
                inventory.addItem("Gift", 8181, 1, this, null); // give academy circlet
            }
            if (isSubClassActive()) {
                getSubClasses().get(_classIndex).setClassId(Id);
            }
            setTarget(this);
            broadcastPacket(new MagicSkillUse(this, 5103, 1, 1000, 0));
            setClassTemplate(Id);
            if (getClassId().level() == 3) {
                sendPacket(SystemMessageId.CONGRATULATIONS_YOU_VE_COMPLETED_YOUR_THIRD_CLASS_TRANSFER_QUEST);
            } else {
                sendPacket(SystemMessageId.CONGRATULATIONS_YOU_VE_COMPLETED_A_CLASS_TRANSFER);
            }

            // Remove class permitted hennas.
            for (int slot = 1; slot < 4; slot++)
            {
                final Henna henna = getHenna(slot);
                if ((henna != null) && !henna.isAllowedClass(getClassId()))
                {
                    removeHenna(slot);
                }
            }

            // Update class icon in party and clan
            if (isInParty()) {
                _party.broadcastPacket(new PartySmallWindowUpdate(this, true));
            }

            if (_clan != null) {
                _clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
            }

            sendPacket(new ExSubjobInfo(this, SubclassInfoType.CLASS_CHANGED));

            // Add AutoGet skills and normal skills and/or learnByFS depending on configurations.
            rewardSkills();

            if (!canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
                checkPlayerSkills();
            }

            notifyFriends(FriendStatus.CLASS);
        } finally {
            _subclassLock.unlock();
        }
    }

    /**
     * @return the Experience of the Player.
     */
    public long getExp() {
        return getStats().getExp();
    }

    /**
     * Set the Experience value of the Player.
     *
     * @param exp
     */
    public void setExp(long exp) {
        if (exp < 0) {
            exp = 0;
        }

        getStats().setExp(exp);
    }

    /**
     * @return the fists weapon of the Player (used when no weapon is equipped).
     */
    public Weapon getFistsWeaponItem() {
        return _fistsWeaponItem;
    }

    /**
     * Set the fists weapon of the Player (used when no weapon is equiped).
     *
     * @param weaponItem The fists Weapon to set to the Player
     */
    public void setFistsWeaponItem(Weapon weaponItem) {
        _fistsWeaponItem = weaponItem;
    }

    public Weapon findFistsWeaponItem() {
        var classId = getClassId();
        var fistWeaponId = switch (classId.getRace()) {
            case HUMAN -> classId.isMage() ? 251 : 246;
            case ELF ->  classId.isMage() ? 249 : 244;
            case DARK_ELF -> classId.isMage() ? 250 : 245;
            case ORC -> classId.isMage() ? 252 : 248;
            case DWARF -> 247;
            default -> 246; // human fight
        };
        return (Weapon) ItemEngine.getInstance().getTemplate(fistWeaponId);

    }

    /**
     * This method reward all AutoGet skills and Normal skills if Auto-Learn configuration is true.
     */
    public void rewardSkills() {
        // Give all normal skills if activated Auto-Learn is activated, included AutoGet skills.
        if (Config.AUTO_LEARN_SKILLS) {
            giveAvailableSkills(Config.AUTO_LEARN_FS_SKILLS, true);
        } else {
            giveAvailableAutoGetSkills();
        }

        if (!canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
            checkPlayerSkills();
        }

        checkItemRestriction();
        sendSkillList();
    }

    /**
     * Re-give all skills which aren't saved to database, like Noble, Hero, Clan Skills.<br>
     */
    public void regiveTemporarySkills() {
        // Do not call this on enterworld or char load

        // Add noble skills if noble
        if (isNoble()) {
            setNoble(true);
        }

        // Add Hero skills if hero
        if (_hero) {
            setHero(true);
        }

        // Add clan skills
        if (_clan != null) {
            _clan.addSkillEffects(this);

            if ((_clan.getLevel() >= SiegeManager.getInstance().getSiegeClanMinLevel()) && isClanLeader()) {
                SiegeManager.getInstance().addSiegeSkills(this);
            }
            if (_clan.getCastleId() > 0) {
                CastleManager.getInstance().getCastleByOwner(getClan()).giveResidentialSkills(this);
            }
        }

        // Reload passive skills from armors / jewels / weapons
        inventory.reloadEquippedItems();
    }

    /**
     * Give all available skills to the player.
     *
     * @param includedByFs   if {@code true} forgotten scroll skills present in the skill tree will be added
     * @param includeAutoGet if {@code true} auto-get skills present in the skill tree will be added
     * @return the amount of new skills earned
     */
    public int giveAvailableSkills(boolean includedByFs, boolean includeAutoGet) {
        int skillCounter = 0;
        // Get available skills
        final Collection<Skill> skills = SkillTreesData.getInstance().getAllAvailableSkills(this, getTemplate().getClassId(), includedByFs, includeAutoGet);
        final List<Skill> skillsForStore = new ArrayList<>();

        for (Skill skill : skills) {
            final Skill oldSkill = getKnownSkill(skill.getId());
            if (oldSkill == skill) {
                continue;
            }

            if (getSkillLevel(skill.getId()) == 0) {
                skillCounter++;
            }

            // fix when learning toggle skills
            if (skill.isToggle() && isAffectedBySkill(skill.getId())) {
                stopSkillEffects(true, skill.getId());
            }

            // Mobius: Keep sublevel on skill level increase.
            if ((oldSkill != null) && (oldSkill.getSubLevel() > 0) && (skill.getSubLevel() == 0) && (oldSkill.getLevel() < skill.getLevel())) {
                skill = SkillEngine.getInstance().getSkill(skill.getId(), skill.getLevel());
            }

            addSkill(skill, false);
            skillsForStore.add(skill);
        }
        storeSkills(skillsForStore, -1);
        if (Config.AUTO_LEARN_SKILLS && (skillCounter > 0)) {
            sendMessage("You have learned " + skillCounter + " new skills.");
        }
        return skillCounter;
    }

    /**
     * Give all available auto-get skills to the player.
     */
    public void giveAvailableAutoGetSkills() {
        // Get available skills
        final List<SkillLearn> autoGetSkills = SkillTreesData.getInstance().getAvailableAutoGetSkills(this);
        final SkillEngine st = SkillEngine.getInstance();
        Skill skill;
        for (SkillLearn s : autoGetSkills) {
            skill = st.getSkill(s.getSkillId(), s.getSkillLevel());
            if (skill != null) {
                addSkill(skill, true);
            } else {
                LOGGER.warn("Skipping null auto-get skill for player: " + toString());
            }
        }
    }

    @Override
    public Race getRace() {
        if (!isSubClassActive()) {
            return getTemplate().getRace();
        }
        return PlayerTemplateData.getInstance().getTemplate(data.getBaseClass()).getRace();
    }

    public Radar getRadar() {
        return radar;
    }

    /**
     * @return the SP amount of the Player.
     */
    public long getSp() {
        return getStats().getSp();
    }

    /**
     * Set the SP amount of the Player.
     *
     * @param sp
     */
    public void setSp(long sp) {
        if (sp < 0) {
            sp = 0;
        }

        super.getStats().setSp(sp);
    }

    /**
     * @param castleId
     * @return true if this Player is a clan leader in ownership of the passed castle
     */
    public boolean isCastleLord(int castleId) {

        // player has clan and is the clan leader, check the castle info
        if ((_clan != null) && (_clan.getLeader().getPlayerInstance() == this)) {
            // if the clan has a castle and it is actually the queried castle, return true
            final Castle castle = CastleManager.getInstance().getCastleByOwner(_clan);
            if ((castle != null) && (castle == CastleManager.getInstance().getCastleById(castleId))) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the Clan Identifier of the Player.
     */
    @Override
    public int getClanId() {
        return clanId;
    }

    /**
     * @return the Clan Crest Identifier of the Player or 0.
     */
    public int getClanCrestId() {
        if (_clan != null) {
            return _clan.getCrestId();
        }

        return 0;
    }

    /**
     * @return The Clan CrestLarge Identifier or 0
     */
    public int getClanCrestLargeId() {
        if ((_clan != null) && ((_clan.getCastleId() != 0) || (_clan.getHideoutId() != 0))) {
            return _clan.getCrestLargeId();
        }
        return 0;
    }

    public long getClanJoinExpiryTime() {
        return data.getClanJoinExpiryTime();
    }

    public void setClanJoinExpiryTime(long time) {
        data.setClanJoinExpiryTime(time);
    }

    public long getClanCreateExpiryTime() {
        return data.getClanCreateExpiryTime();
    }

    public void setClanCreateExpiryTime(long time) {
        data.setClanCreateExpiryTime(time);
    }

    public void setOnlineTime(long time) {
        _onlineTime = time;
        _onlineBeginTime = System.currentTimeMillis();
    }

    /**
     * Return the PlayerInventory Inventory of the Player contained in _inventory.
     */
    @Override
    public PlayerInventory getInventory() {
        return inventory;
    }

    public void removeItemFromShortCut(int objectId) {
        shortcuts.deleteShortCutByObjectId(objectId);
    }

    /**
     * @return True if the Player is sitting.
     */
    public boolean isSitting() {
        return _waitTypeSitting;
    }

    /**
     * Set _waitTypeSitting to given value
     *
     * @param state
     */
    public void setIsSitting(boolean state) {
        _waitTypeSitting = state;
    }

    /**
     * Sit down the Player, set the AI Intention to AI_INTENTION_REST and send a Server->Client ChangeWaitType packet (broadcast)
     */
    public void sitDown() {
        sitDown(true);
    }

    public void sitDown(boolean checkCast) {
        if (checkCast && isCastingNow()) {
            sendPacket(YOU_CANNOT_MOVE_WHILE_CASTING);
            return;
        }

        if (!_waitTypeSitting && !isAttackingDisabled() && !isControlBlocked() && !isImmobilized() && !isFishing()) {
            breakAttack();
            setIsSitting(true);
            getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
            broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_SITTING));
            // Schedule a sit down task to wait for the animation to finish
            ThreadPool.schedule(new SitDownTask(this), 2500);
            setBlockActions(true);
        }
    }

    /**
     * Stand up the Player, set the AI Intention to AI_INTENTION_IDLE and send a Server->Client ChangeWaitType packet (broadcast)
     */
    public void standUp() {
        if (Event.isParticipant(this) && eventStatus.isSitForced()) {
            sendMessage("A dark force beyond your mortal understanding makes your knees to shake when you try to stand up...");
        } else if (_waitTypeSitting && !isInStoreMode() && !isAlikeDead()) {
            if (getEffectList().isAffected(EffectFlag.RELAXING)) {
                stopEffects(EffectFlag.RELAXING);
            }

            broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STANDING));
            // Schedule a stand up task to wait for the animation to finish
            ThreadPool.schedule(new StandUpTask(this), 2500);
        }
    }

    /**
     * @return the PcWarehouse object of the Player.
     */
    public PlayerWarehouse getWarehouse() {
        if (_warehouse == null) {
            _warehouse = new PlayerWarehouse(this);
            _warehouse.restore();
        }
        if (Config.WAREHOUSE_CACHE) {
            WarehouseCacheManager.getInstance().addCacheTask(this);
        }
        return _warehouse;
    }

    /**
     * Free memory used by Warehouse
     */
    public void clearWarehouse() {
        if (_warehouse != null) {
            _warehouse.deleteMe();
        }
        _warehouse = null;
    }

    public PlayerFreight getFreight() {
        return _freight;
    }

    /**
     * @return true if refund list is not empty
     */
    public boolean hasRefund() {
        return (_refund != null) && (_refund.getSize() > 0) && Config.ALLOW_REFUND;
    }

    /**
     * @return refund object or create new if not exist
     */
    public PlayerRefund getRefund() {
        if (_refund == null) {
            _refund = new PlayerRefund(this);
        }
        return _refund;
    }

    /**
     * Clear refund
     */
    public void clearRefund() {
        if (_refund != null) {
            _refund.deleteMe();
        }
        _refund = null;
    }

    /**
     * @return the Adena amount of the Player.
     */
    public long getAdena() {
        return inventory.getAdena();
    }

    /**
     * @return the Ancient Adena amount of the Player.
     */
    public long getAncientAdena() {
        return inventory.getAncientAdena();
    }

    /**
     * @return the Beauty Tickets of the Player.
     */
    public long getBeautyTickets() {
        return inventory.getBeautyTickets();
    }

    /**
     * Add adena to Inventory of the Player and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param count       : int Quantity of adena to be added
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     */
    public void addAdena(String process, long count, WorldObject reference, boolean sendMessage) {
        if (sendMessage) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_ADENA);
            sm.addLong(count);
            sendPacket(sm);
        }

        if (count > 0) {
            inventory.addAdena(process, count, this, reference);

            // Send update packet
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(inventory.getAdenaInstance());
                sendInventoryUpdate(iu);
            } else {
                sendItemList();
            }
        }
    }

    /**
     * Reduce adena in Inventory of the Player and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param count       : long Quantity of adena to be reduced
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successful
     */
    public boolean reduceAdena(String process, long count, WorldObject reference, boolean sendMessage) {
        if (count > inventory.getAdena()) {
            if (sendMessage) {
                sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            }
            return false;
        }

        if (count > 0) {
            final Item adenaItem = inventory.getAdenaInstance();
            if (!inventory.reduceAdena(process, count, this, reference)) {
                return false;
            }

            // Send update packet
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(adenaItem);
                sendInventoryUpdate(iu);
            } else {
                sendItemList();
            }

            if (sendMessage) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.S1_ADENA_DISAPPEARED);
                sm.addLong(count);
                sendPacket(sm);
            }
        }

        return true;
    }

    /**
     * Reduce Beauty Tickets in Inventory of the Player and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param count       : long Quantity of Beauty Tickets to be reduced
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successful
     */
    public boolean reduceBeautyTickets(String process, long count, WorldObject reference, boolean sendMessage) {
        if (count > inventory.getBeautyTickets()) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }

        if (count > 0) {
            final Item beautyTickets = inventory.getBeautyTicketsInstance();
            if (!inventory.reduceBeautyTickets(process, count, this, reference)) {
                return false;
            }

            // Send update packet
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(beautyTickets);
                sendInventoryUpdate(iu);
            } else {
                sendItemList();
            }

            if (sendMessage) {
                if (count > 1) {
                    final SystemMessage sm = getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                    sm.addItemName(Inventory.BEAUTY_TICKET_ID);
                    sm.addLong(count);
                    sendPacket(sm);
                } else {
                    final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(Inventory.BEAUTY_TICKET_ID);
                    sendPacket(sm);
                }
            }
        }

        return true;
    }

    /**
     * Add ancient adena to Inventory of the Player and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param count       : int Quantity of ancient adena to be added
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     */
    public void addAncientAdena(String process, long count, WorldObject reference, boolean sendMessage) {
        if (sendMessage) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
            sm.addItemName(CommonItem.ANCIENT_ADENA);
            sm.addLong(count);
            sendPacket(sm);
        }

        if (count > 0) {
            inventory.addAncientAdena(process, count, this, reference);

            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(inventory.getAncientAdenaInstance());
                sendInventoryUpdate(iu);
            } else {
                sendItemList();
            }
        }
    }

    /**
     * Reduce ancient adena in Inventory of the Player and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param count       : long Quantity of ancient adena to be reduced
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successful
     */
    public boolean reduceAncientAdena(String process, long count, WorldObject reference, boolean sendMessage) {
        if (count > inventory.getAncientAdena()) {
            if (sendMessage) {
                sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            }

            return false;
        }

        if (count > 0) {
            final Item ancientAdenaItem = inventory.getAncientAdenaInstance();
            if (!inventory.reduceAncientAdena(process, count, this, reference)) {
                return false;
            }

            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(ancientAdenaItem);
                sendInventoryUpdate(iu);
            } else {
                sendItemList();
            }

            if (sendMessage) {
                if (count > 1) {
                    final SystemMessage sm = getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                    sm.addItemName(CommonItem.ANCIENT_ADENA);
                    sm.addLong(count);
                    sendPacket(sm);
                } else {
                    final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(CommonItem.ANCIENT_ADENA);
                    sendPacket(sm);
                }
            }
        }

        return true;
    }

    /**
     * Adds item to inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param item        : Item to be added
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     */
    public void addItem(String process, Item item, WorldObject reference, boolean sendMessage) {
        if (item.getCount() > 0) {

            if (sendMessage) {
                if (item.getCount() > 1) {
                    sendPacket( getSystemMessage(YOU_HAVE_OBTAINED_S2_S1).addItemName(item).addLong(item.getCount()));
                } else if (item.getEnchantLevel() > 0) {
                    sendPacket(getSystemMessage(YOU_HAVE_OBTAINED_A_S1_S2).addInt(item.getEnchantLevel()).addItemName(item));
                } else {
                    sendPacket(getSystemMessage(YOU_HAVE_OBTAINED_S1).addItemName(item));
                }
            }

            // Add the item to inventory
            final Item newitem = inventory.addItem(process, item, this, reference);

            // If over capacity, drop the item
            if (!canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !inventory.validateCapacity(0, item.isQuestItem()) && newitem.isDropable() && (!newitem.isStackable() || (newitem.getLastChange() != Item.MODIFIED))) {
                dropItem("InvDrop", newitem, null, true, true);
            }
        }
    }

    /**
     * Adds item to Inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param itemId      : int Item Identifier of the item to be added
     * @param count       : long Quantity of items to be added
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return
     *
     * TODO make process an enum.
     */
    public Item addItem(String process, int itemId, long count, WorldObject reference, boolean sendMessage) {
        return addItem(process, itemId, count, 0, reference, sendMessage);
    }

    public Item addItem(String process, int itemId, long count, int enchant, WorldObject reference, boolean sendMessage) {
        Item item = null;
        if (count > 0) {
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);

            if (isNull(template)) {
                LOGGER.error("Item doesn't exist so cannot be added. Item ID: {}", itemId);
                return null;
            }

            if(template.hasExImmediateEffect()) {
                final var handler = ItemHandler.getInstance().getHandler(template instanceof EtcItem etcItem ? etcItem : null);

                if (handler == null) {
                    LOGGER.warn("No item handler registered for immediate item id {}!",  template.getId());
                } else {
                    handler.useItem(this, new Item(itemId), false);
                }
            } else {
                item = inventory.addItem(process, itemId, count, this, reference);
                if(enchant > 0) {
                    item.setEnchantLevel(enchant);
                }

                // If over capacity, drop the item
                if (!canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !inventory.validateCapacity(0, template.isQuestItem()) && item.isDropable()
                        && (!item.isStackable() || (item.getLastChange() != Item.MODIFIED))) {

                    dropItem("InvDrop", item, null, true);
                }
            }

            if (sendMessage) {
                if (count > 1) {
                    if (process.equalsIgnoreCase("Sweeper") || process.equalsIgnoreCase("Quest")) {
                        sendPacket( getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S).addItemName(template).addLong(count) );
                    } else {
                        sendPacket( getSystemMessage(YOU_HAVE_OBTAINED_S2_S1).addItemName(template).addLong(count) );
                    }
                } else if (process.equalsIgnoreCase("Sweeper") || process.equalsIgnoreCase("Quest")) {
                    sendPacket( getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1).addItemName(template) );
                } else if(enchant > 0) {
                    sendPacket( getSystemMessage(YOU_HAVE_OBTAINED_A_S1_S2).addItemName(template).addInt(enchant));
                } else {
                    sendPacket( getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1).addItemName(template) );
                }
            }
        }
        return item;
    }



    /**
     * @param process     the process name
     * @param item        the item holder
     * @param reference   the reference object
     * @param sendMessage if {@code true} a system message will be sent
     */
    public Item addItem(String process, ItemHolder item, WorldObject reference, boolean sendMessage) {
        return addItem(process, item.getId(), item.getCount(), item.getEnchantment(), reference, sendMessage);
    }

    /**
     * Destroy item from inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param item        : Item to be destroyed
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successful
     */
    public boolean destroyItem(String process, Item item, WorldObject reference, boolean sendMessage) {
        return destroyItem(process, item, item.getCount(), reference, sendMessage);
    }

    /**
     * Destroy item from inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param item        : Item to be destroyed
     * @param count
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successful
     */
    public boolean destroyItem(String process, Item item, long count, WorldObject reference, boolean sendMessage) {
        item = inventory.destroyItem(process, item, count, this, reference);

        if (item == null) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }

        final InventoryUpdate playerIU = new InventoryUpdate();
        playerIU.addItem(item);
        sendInventoryUpdate(playerIU);

        // Sends message to client if requested
        if (sendMessage) {
            if (count > 1) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                sm.addItemName(item);
                sm.addLong(count);
                sendPacket(sm);
            } else {
                final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                sm.addItemName(item);
                sendPacket(sm);
            }
        }

        return true;
    }

    /**
     * Destroys item from inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param objectId    : int Item Instance identifier of the item to be destroyed
     * @param count       : int Quantity of items to be destroyed
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successful
     */
    @Override
    public boolean destroyItem(String process, int objectId, long count, WorldObject reference, boolean sendMessage) {
        final Item item = inventory.getItemByObjectId(objectId);

        if (item == null) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }

            return false;
        }
        return destroyItem(process, item, count, reference, sendMessage);
    }

    /**
     * Destroys shots from inventory without logging and only occasional saving to database. Sends a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param objectId    : int Item Instance identifier of the item to be destroyed
     * @param count       : int Quantity of items to be destroyed
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successful
     */
    public boolean destroyItemWithoutTrace(String process, int objectId, long count, WorldObject reference, boolean sendMessage) {
        final Item item = inventory.getItemByObjectId(objectId);

        if ((item == null) || (item.getCount() < count)) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }

            return false;
        }

        return destroyItem(null, item, count, reference, sendMessage);
    }

    /**
     * Destroy item from inventory by using its <B>itemId</B> and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param itemId      : int Item identifier of the item to be destroyed
     * @param count       : int Quantity of items to be destroyed
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successful
     */
    @Override
    public boolean destroyItemByItemId(String process, int itemId, long count, WorldObject reference, boolean sendMessage) {
        if (itemId == CommonItem.ADENA) {
            return reduceAdena(process, count, reference, sendMessage);
        }

        final Item item = inventory.getItemByItemId(itemId);

        if ((item == null) || (item.getCount() < count) || (inventory.destroyItemByItemId(process, itemId, count, this, reference) == null)) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }

            return false;
        }

        // Send inventory update packet
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            playerIU.addItem(item);
            sendInventoryUpdate(playerIU);
        } else {
            sendItemList();
        }

        // Sends message to client if requested
        if (sendMessage) {
            if (count > 1) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                sm.addItemName(itemId);
                sm.addLong(count);
                sendPacket(sm);
            } else {
                final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                sm.addItemName(itemId);
                sendPacket(sm);
            }
        }

        return true;
    }

    /**
     * Transfers item to another ItemContainer and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Identifier of the item to be transfered
     * @param count     : long Quantity of items to be transfered
     * @param target
     * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item or the updated item in inventory
     */
    public Item transferItem(String process, int objectId, long count, Inventory target, WorldObject reference) {
        final Item oldItem = checkItemManipulation(objectId, count, "transfer");
        if (oldItem == null) {
            return null;
        }
        final Item newItem = inventory.transferItem(process, objectId, count, target, this, reference);
        if (newItem == null) {
            return null;
        }

        // Send inventory update packet
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate playerIU = new InventoryUpdate();

            if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                playerIU.addModifiedItem(oldItem);
            } else {
                playerIU.addRemovedItem(oldItem);
            }

            sendInventoryUpdate(playerIU);
        } else {
            sendItemList();
        }

        // Send target update packet
        if (target instanceof PlayerInventory) {
            final Player targetPlayer = ((PlayerInventory) target).getOwner();

            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate playerIU = new InventoryUpdate();

                if (newItem.getCount() > count) {
                    playerIU.addModifiedItem(newItem);
                } else {
                    playerIU.addNewItem(newItem);
                }

                targetPlayer.sendPacket(playerIU);
            } else {
                targetPlayer.sendItemList();
            }
        }
        return newItem;
    }

    /**
     * Use instead of calling {@link #addItem(String, Item, WorldObject, boolean)} and {@link #destroyItemByItemId(String, int, long, WorldObject, boolean)}<br>
     * This method validates slots and weight limit, for stackable and non-stackable items.
     *
     * @param process     a generic string representing the process that is exchanging this items
     * @param reference   the (probably NPC) reference, could be null
     * @param coinId      the item Id of the item given on the exchange
     * @param cost        the amount of items given on the exchange
     * @param rewardId    the item received on the exchange
     * @param count       the amount of items received on the exchange
     * @param sendMessage if {@code true} it will send messages to the acting player
     * @return {@code true} if the player successfully exchanged the items, {@code false} otherwise
     */
    public boolean exchangeItemsById(String process, WorldObject reference, int coinId, long cost, int rewardId, long count, boolean sendMessage) {
        final PlayerInventory inv = inventory;
        if (!inv.validateCapacityByItemId(rewardId, count)) {
            if (sendMessage) {
                sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            }
            return false;
        }

        if (!inv.validateWeightByItemId(rewardId, count)) {
            if (sendMessage) {
                sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            }
            return false;
        }

        if (destroyItemByItemId(process, coinId, cost, reference, sendMessage)) {
            addItem(process, rewardId, count, reference, sendMessage);
            return true;
        }
        return false;
    }

    /**
     * Drop item from inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     String Identifier of process triggering this action
     * @param item        Item to be dropped
     * @param reference   WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage boolean Specifies whether to send message to Client about this action
     * @param protectItem whether or not dropped item must be protected temporary against other players
     * @return boolean informing if the action was successful
     */
    public boolean dropItem(String process, Item item, WorldObject reference, boolean sendMessage, boolean protectItem) {
        item = inventory.dropItem(process, item, this, reference);

        if (item == null) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }

        item.dropMe(this, (getX() + Rnd.get(50)) - 25, (getY() + Rnd.get(50)) - 25, getZ() + 20);
        final var generalSettings = getSettings(GeneralSettings.class);
        if ((generalSettings.autoDestroyItemTime() > 0) && generalSettings.destroyPlayerDroppedItem() && !generalSettings.isProtectedItem(item.getId())) {
            if (!item.isEquipable() || generalSettings.destroyEquipableItem()) {
                ItemsAutoDestroy.getInstance().addItem(item);
            }
        }

        // protection against auto destroy dropped item
        if (generalSettings.destroyPlayerDroppedItem()) {
            item.setProtected(item.isEquipable() && !generalSettings.destroyEquipableItem());
        } else {
            item.setProtected(true);
        }

        // retail drop protection
        if (protectItem) {
            item.getDropProtection().protect(this);
        }

        // Send inventory update packet
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            playerIU.addItem(item);
            sendInventoryUpdate(playerIU);
        } else {
            sendItemList();
        }

        // Sends message to client if requested
        if (sendMessage) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_DROPPED_S1);
            sm.addItemName(item);
            sendPacket(sm);
        }

        return true;
    }

    public boolean dropItem(String process, Item item, WorldObject reference, boolean sendMessage) {
        return dropItem(process, item, reference, sendMessage, false);
    }

    /**
     * Drop item from inventory by using its <B>objectID</B> and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param objectId    : int Item Instance identifier of the item to be dropped
     * @param count       : long Quantity of items to be dropped
     * @param x           : int coordinate for drop X
     * @param y           : int coordinate for drop Y
     * @param z           : int coordinate for drop Z
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @param protectItem
     * @return Item corresponding to the new item or the updated item in inventory
     *
     * TODO extract method and remove duplication
     */
    public Item dropItem(String process, int objectId, long count, int x, int y, int z, WorldObject reference, boolean sendMessage, boolean protectItem) {
        final Item invitem = inventory.getItemByObjectId(objectId);
        final Item item = inventory.dropItem(process, objectId, count, this, reference);

        if (item == null) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }

            return null;
        }

        item.dropMe(this, x, y, z);

        final var generalSettings = getSettings(GeneralSettings.class);
        if ((generalSettings.autoDestroyItemTime() > 0) && generalSettings.destroyPlayerDroppedItem() && !generalSettings.isProtectedItem(item.getId())) {
            if (!item.isEquipable() || generalSettings.destroyEquipableItem()) {
                ItemsAutoDestroy.getInstance().addItem(item);
            }
        }
        if (generalSettings.destroyPlayerDroppedItem()) {
            item.setProtected(item.isEquipable() && !generalSettings.destroyEquipableItem());
        } else {
            item.setProtected(true);
        }

        // retail drop protection
        if (protectItem) {
            item.getDropProtection().protect(this);
        }

        // Send inventory update packet
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            playerIU.addItem(invitem);
            sendInventoryUpdate(playerIU);
        } else {
            sendItemList();
        }

        // Sends message to client if requested
        if (sendMessage) {
            final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_DROPPED_S1);
            sm.addItemName(item);
            sendPacket(sm);
        }

        return item;
    }

    public Item checkItemManipulation(int objectId, long count, String action) {
        // TODO: if we remove objects that are not visible from the World, we'll have to remove this check
        if (World.getInstance().findObject(objectId) == null) {
            LOGGER.debug("player {} tried to {} item not available in World", this, action);
            return null;
        }

        final Item item = inventory.getItemByObjectId(objectId);

        if ((item == null) || (item.getOwnerId() != getObjectId())) {
            LOGGER.debug(getObjectId() + ": player tried to " + action + " item he is not owner of");
            return null;
        }

        if ((count < 0) || ((count > 1) && !item.isStackable())) {
            LOGGER.debug(getObjectId() + ": player tried to " + action + " item with invalid count: " + count);
            return null;
        }

        if (count > item.getCount()) {
            LOGGER.debug(getObjectId() + ": player tried to " + action + " more items than he owns");
            return null;
        }

        // Pet is summoned and not the item that summoned the pet AND not the buggle from strider you're mounting
        if (((pet != null) && (pet.getControlObjectId() == objectId)) || (mountObjectID == objectId)) {
            return null;
        }

        if (isProcessingItem(objectId)) {
            return null;
        }

        // We cannot put a Weapon with Augmentation in WH while casting (Possible Exploit)
        if (item.isAugmented() && isCastingNow()) {
            return null;
        }

        return item;
    }

    public boolean isSpawnProtected() {
        return _spawnProtectEndTime > 0 && _spawnProtectEndTime > System.currentTimeMillis();
    }

    public boolean isTeleportProtected() {
        return _teleportProtectEndTime > 0  && _teleportProtectEndTime > System.currentTimeMillis();
    }

    public void setSpawnProtection(boolean protect) {
        _spawnProtectEndTime = protect ? System.currentTimeMillis() + (Config.PLAYER_SPAWN_PROTECTION * 1000) : 0;
    }

    public void setTeleportProtection(boolean protect) {
        _teleportProtectEndTime = protect ? System.currentTimeMillis() + (Config.PLAYER_TELEPORT_PROTECTION * 1000) : 0;
    }

    public boolean isRecentFakeDeath() {
        return _recentFakeDeathEndTime > WorldTimeController.getInstance().getGameTicks();
    }

    /**
     * Set protection from aggro mobs when getting up from fake death, according settings.
     *
     * @param protect
     */
    public void setRecentFakeDeath(boolean protect) {
        _recentFakeDeathEndTime = protect ? WorldTimeController.getInstance().getGameTicks() + (Config.PLAYER_FAKEDEATH_UP_PROTECTION * WorldTimeController.TICKS_PER_SECOND) : 0;
    }

    public final boolean isFakeDeath() {
        return isAffected(EffectFlag.FAKE_DEATH);
    }

    @Override
    public final boolean isAlikeDead() {
        return super.isAlikeDead() || isFakeDeath();
    }

    /**
     * @return the client owner of this char.
     */
    public GameClient getClient() {
        return _client;
    }

    public void setClient(GameClient client) {
        _client = client;
        if ((_client != null) && (_client.getHostAddress() != null)) {
            _ip = _client.getHostAddress();
        }
    }

    public String getIPAddress() {
        return _ip;
    }

    public Location getCurrentSkillWorldPosition() {
        return _currentSkillWorldPosition;
    }

    public void setCurrentSkillWorldPosition(Location worldPosition) {
        _currentSkillWorldPosition = worldPosition;
    }

    @Override
    public void enableSkill(Skill skill) {
        super.enableSkill(skill);
        removeTimeStamp(skill);
    }

    /**
     * Returns true if cp update should be done, false if not
     *
     * @return boolean
     */
    private boolean needCpUpdate() {
        final double currentCp = getCurrentCp();

        if ((currentCp <= 1.0) || (getMaxCp() < MAX_STATUS_BAR_PX)) {
            return true;
        }

        if ((currentCp <= _cpUpdateDecCheck) || (currentCp >= _cpUpdateIncCheck)) {
            if (currentCp == getMaxCp()) {
                _cpUpdateIncCheck = currentCp + 1;
                _cpUpdateDecCheck = currentCp - _cpUpdateInterval;
            } else {
                final double doubleMulti = currentCp / _cpUpdateInterval;
                int intMulti = (int) doubleMulti;

                _cpUpdateDecCheck = _cpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
                _cpUpdateIncCheck = _cpUpdateDecCheck + _cpUpdateInterval;
            }

            return true;
        }

        return false;
    }

    /**
     * Returns true if mp update should be done, false if not
     *
     * @return boolean
     */
    private boolean needMpUpdate() {
        final double currentMp = getCurrentMp();

        if ((currentMp <= 1.0) || (getMaxMp() < MAX_STATUS_BAR_PX)) {
            return true;
        }

        if ((currentMp <= _mpUpdateDecCheck) || (currentMp >= _mpUpdateIncCheck)) {
            if (currentMp == getMaxMp()) {
                _mpUpdateIncCheck = currentMp + 1;
                _mpUpdateDecCheck = currentMp - _mpUpdateInterval;
            } else {
                final double doubleMulti = currentMp / _mpUpdateInterval;
                int intMulti = (int) doubleMulti;

                _mpUpdateDecCheck = _mpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
                _mpUpdateIncCheck = _mpUpdateDecCheck + _mpUpdateInterval;
            }

            return true;
        }

        return false;
    }

    /**
     * Send packet StatusUpdate with current HP,MP and CP to the Player and only current HP, MP and Level to all other Player of the Party. <B><U> Actions</U> :</B>
     * <li>Send the Server->Client packet StatusUpdate with current HP, MP and CP to this Player</li><BR>
     * <li>Send the Server->Client packet PartySmallWindowUpdate with current HP, MP and Level to all other Player of the Party</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND current HP and MP to all Player of the _statusListener</B></FONT>
     */
    @Override
    public void broadcastStatusUpdate(Creature caster) {
        final StatusUpdate su = new StatusUpdate(this);
        if (caster != null) {
            su.addCaster(caster);
        }

        computeStatusUpdate(su, StatusUpdateType.LEVEL);
        computeStatusUpdate(su, StatusUpdateType.MAX_HP);
        computeStatusUpdate(su, StatusUpdateType.CUR_HP);
        computeStatusUpdate(su, StatusUpdateType.MAX_MP);
        computeStatusUpdate(su, StatusUpdateType.CUR_MP);
        computeStatusUpdate(su, StatusUpdateType.MAX_CP);
        computeStatusUpdate(su, StatusUpdateType.CUR_CP);
        if (su.hasUpdates()) {
            broadcastPacket(su);
        }

        final boolean needCpUpdate = needCpUpdate();
        final boolean needHpUpdate = needHpUpdate();
        final boolean needMpUpdate = needMpUpdate();

        // Check if a party is in progress and party window update is usefull
        if ((_party != null) && (needCpUpdate || needHpUpdate || needMpUpdate)) {
            final PartySmallWindowUpdate partyWindow = new PartySmallWindowUpdate(this, false);
            if (needCpUpdate) {
                partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_CP);
                partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_CP);
            }
            if (needHpUpdate) {
                partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_HP);
                partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_HP);
            }
            if (needMpUpdate) {
                partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_MP);
                partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_MP);
            }
            _party.broadcastToPartyMembers(this, partyWindow);
        }

        if (_inOlympiadMode && _OlympiadStart && (needCpUpdate || needHpUpdate)) {
            final OlympiadGameTask game = OlympiadGameManager.getInstance().getOlympiadTask(getOlympiadGameId());
            if ((game != null) && game.isBattleStarted()) {
                game.getStadium().broadcastStatusUpdate(this);
            }
        }

        // In duel MP updated only with CP or HP
        if (_isInDuel && (needCpUpdate || needHpUpdate)) {
            DuelManager.getInstance().broadcastToOppositTeam(this, new ExDuelUpdateUserInfo(this));
        }
    }

    /**
     * Send a Server->Client packet UserInfo to this Player and CharInfo to all Player in its _KnownPlayers. <B><U> Concept</U> :</B> Others Player in the detection area of the Player are identified in <B>_knownPlayers</B>. In order to inform other players of this
     * Player state modifications, server just need to go through _knownPlayers to send Server->Client Packet <B><U> Actions</U> :</B>
     * <li>Send a Server->Client packet UserInfo to this Player (Public and Private Data)</li>
     * <li>Send a Server->Client packet CharInfo to all Player in _KnownPlayers of the Player (Public data only)</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : DON'T SEND UserInfo packet to other players instead of CharInfo packet. Indeed, UserInfo packet contains PRIVATE DATA as MaxHP,
     * STR, DEX...</B></FONT>
     */
    public final void broadcastUserInfo() {
        // Send user info to the current player
        sendPacket(new UserInfo(this));

        // Broadcast char info to known players
        broadcastCharInfo();
    }

    public final void broadcastUserInfo(UserInfoType... types) {
        // Send user info to the current player
        final UserInfo ui = new UserInfo(this, false);
        ui.addComponentType(types);
        sendPacket(ui);

        // Broadcast char info to all known players
        broadcastCharInfo();
    }

    public final void broadcastCharInfo() {
        var charInfo = new ExCharInfo(this);
        World.getInstance().forEachVisibleObject(this, Player.class, player ->
        {
            if (isVisibleFor(player)) {
                if (!isInvisible() || player.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS)) {
                    player.sendPacket(charInfo);
                }

                // Update relation.
                final int relation = getRelation(player);
                Integer oldRelation = getKnownRelations().get(player.getObjectId());
                if ((oldRelation == null) || (oldRelation != relation)) {
                    final RelationChanged rc = new RelationChanged();
                    rc.addRelation(this, relation, !isInsideZone(ZoneType.PEACE));
                    if (hasSummon()) {
                        final Summon pet = getPet();
                        if (pet != null) {
                            rc.addRelation(pet, relation, !isInsideZone(ZoneType.PEACE));
                        }
                        if (hasServitors()) {
                            getServitors().values().forEach(s -> rc.addRelation(s, relation, !isInsideZone(ZoneType.PEACE)));
                        }
                    }
                    player.sendPacket(rc);
                    getKnownRelations().put(player.getObjectId(), relation);
                }
            }
        });
    }

    public final void broadcastTitleInfo() {
        // Send a Server->Client packet UserInfo to this Player
        broadcastUserInfo(UserInfoType.CLAN);

        // Send a Server->Client packet TitleUpdate to all Player in _KnownPlayers of the Player
        broadcastPacket(new NicknameChanged(this));
    }

    @Override
    public final void broadcastPacket(ServerPacket mov) {
        if (mov instanceof ExCharInfo) {
            throw new IllegalArgumentException("ExCharInfo is being send via broadcastPacket. Do NOT do that! Use broadcastCharInfo() instead.");
        }

        sendPacket(mov);

        World.getInstance().forEachVisibleObject(this, Player.class, mov::sendTo, this::isVisibleFor);
    }

    @Override
    public void broadcastPacket(ServerPacket mov, int radiusInKnownlist) {

        if (mov instanceof ExCharInfo) {
            LOGGER.warn("ExCharInfo is being send via broadcastPacket. Do NOT do that! Use broadcastCharInfo() instead.");
        }

        sendPacket(mov);

        World.getInstance().forEachVisibleObjectInRange(this, Player.class, radiusInKnownlist, mov::sendTo, this::isVisibleFor);
    }

    /**
     * @return the Alliance Identifier of the Player.
     */
    @Override
    public int getAllyId() {
        return zeroIfNullOrElse(_clan, Clan::getAllyId);
    }

    public int getAllyCrestId() {
        return zeroIfNullOrElse(_clan, Clan::getAllyCrestId);
    }

    /**
     * Send a Server->Client packet StatusUpdate to the Player.
     */
    @Override
    public void sendPacket(ServerPacket... packets) {
        if (_client != null) {
            for (ServerPacket packet : packets) {
                _client.sendPacket(packet);
            }
        }
    }

    /**
     * Send SystemMessage packet.
     *
     * @param id SystemMessageId
     */
    @Override
    public void sendPacket(SystemMessageId id) {
        sendPacket(getSystemMessage(id));
    }

    /**
     * Manage Interact Task with another Player. <B><U> Actions</U> :</B>
     * <li>If the private store is a STORE_PRIVATE_SELL, send a Server->Client PrivateBuyListSell packet to the Player</li>
     * <li>If the private store is a STORE_PRIVATE_BUY, send a Server->Client PrivateBuyListBuy packet to the Player</li>
     * <li>If the private store is a STORE_PRIVATE_MANUFACTURE, send a Server->Client RecipeShopSellList packet to the Player</li>
     *
     * @param target The Creature targeted
     */
    public void doInteract(Creature target) {
        if (target == null) {
            return;
        }

        if (GameUtils.isPlayer(target)) {
            final Player targetPlayer = (Player) target;
            sendPacket(ActionFailed.STATIC_PACKET);

            if ((targetPlayer.getPrivateStoreType() == PrivateStoreType.SELL) || (targetPlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL)) {
                sendPacket(new PrivateStoreListSell(this, targetPlayer));
            } else if (targetPlayer.getPrivateStoreType() == PrivateStoreType.BUY) {
                sendPacket(new PrivateStoreListBuy(this, targetPlayer));
            } else if (targetPlayer.getPrivateStoreType() == PrivateStoreType.MANUFACTURE) {
                sendPacket(new RecipeShopSellList(this, targetPlayer));
            }
        } else // _interactTarget=null should never happen but one never knows ^^;
        {
            target.onAction(this);
        }
    }

    /**
     * Manages AutoLoot Task.<br>
     * <ul>
     * <li>Send a system message to the player.</li>
     * <li>Add the item to the player's inventory.</li>
     * <li>Send a Server->Client packet InventoryUpdate to this player with NewItem (use a new slot) or ModifiedItem (increase amount).</li>
     * <li>Send a Server->Client packet StatusUpdate to this player with current weight.</li>
     * </ul>
     * <font color=#FF0000><B><U>Caution</U>: If a party is in progress, distribute the items between the party members!</b></font>
     *
     * @param target    the NPC dropping the item
     * @param itemId    the item ID
     * @param itemCount the item count
     */
    public void doAutoLoot(Attackable target, int itemId, long itemCount) {
        if (isInParty() && !ItemEngine.getInstance().getTemplate(itemId).hasExImmediateEffect()) {
            _party.distributeItem(this, itemId, itemCount, false, target);
        } else if (itemId == CommonItem.ADENA) {
            addAdena("Loot", itemCount, target, true);
        } else {
            addItem("Loot", itemId, itemCount, target, true);
        }
    }

    /**
     * Method overload for {@link Player#doAutoLoot(Attackable, int, long)}
     *
     * @param target the NPC dropping the item
     * @param item   the item holder
     */
    public void doAutoLoot(Attackable target, ItemHolder item) {
        doAutoLoot(target, item.getId(), item.getCount());
    }

    /**
     * Manage Pickup Task. <B><U> Actions</U> :</B>
     * <li>Send a Server->Client packet StopMove to this Player</li>
     * <li>Remove the Item from the world and send server->client GetItem packets</li>
     * <li>Send a System Message to the Player : YOU_PICKED_UP_S1_ADENA or YOU_PICKED_UP_S1_S2</li>
     * <li>Add the Item to the Player inventory</li>
     * <li>Send a Server->Client packet InventoryUpdate to this Player with NewItem (use a new slot) or ModifiedItem (increase amount)</li>
     * <li>Send a Server->Client packet StatusUpdate to this Player with current weight</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : If a Party is in progress, distribute Items between party members</B></FONT>
     *
     * @param object The Item to pick up
     */
    @Override
    public void doPickupItem(WorldObject object) {
        if (isAlikeDead() || isFakeDeath()) {
            return;
        }

        // Set the AI Intention to AI_INTENTION_IDLE
        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);

        // Check if the WorldObject to pick up is a Item
        if (!GameUtils.isItem(object)) {
            // dont try to pickup anything that is not an item :)
            LOGGER.warn(this + " trying to pickup wrong target." + getTarget());
            return;
        }

        final Item target = (Item) object;

        // Send a Server->Client packet ActionFailed to this Player
        sendPacket(ActionFailed.STATIC_PACKET);

        // Send a Server->Client packet StopMove to this Player
        final StopMove sm = new StopMove(this);
        sendPacket(sm);

        SystemMessage smsg = null;
        synchronized (target) {
            // Check if the target to pick up is visible
            if (!target.isSpawned()) {
                // Send a Server->Client packet ActionFailed to this Player
                sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            if (!target.getDropProtection().tryPickUp(this)) {
                sendPacket(ActionFailed.STATIC_PACKET);
                smsg = getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                smsg.addItemName(target);
                sendPacket(smsg);
                return;
            }

            if ((!isInParty() || (_party.getDistributionType() == PartyDistributionType.FINDERS_KEEPERS)) && !inventory.validateCapacity(target)) {
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
                return;
            }

            if (isInvul() && !canOverrideCond(PcCondOverride.ITEM_CONDITIONS)) {
                sendPacket(ActionFailed.STATIC_PACKET);
                smsg = getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                smsg.addItemName(target);
                sendPacket(smsg);
                return;
            }

            if ((target.getOwnerId() != 0) && (target.getOwnerId() != getObjectId()) && !isInLooterParty(target.getOwnerId())) {
                if (target.getId() == CommonItem.ADENA) {
                    smsg = getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    smsg.addLong(target.getCount());
                } else if (target.getCount() > 1) {
                    smsg = getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S2_S1_S);
                    smsg.addItemName(target);
                    smsg.addLong(target.getCount());
                } else {
                    smsg = getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    smsg.addItemName(target);
                }
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(smsg);
                return;
            }

            if ((target.getItemLootShedule() != null) && ((target.getOwnerId() == getObjectId()) || isInLooterParty(target.getOwnerId()))) {
                target.resetOwnerTimer();
            }

            // Remove the Item from the world and send server->client GetItem packets
            target.pickupMe(this);
            if (getSettings(GeneralSettings.class).saveDroppedItems()) {
                ItemsOnGroundManager.getInstance().removeObject(target);
            }
        }

        // Auto use herbs - pick up
        if (target.getTemplate().hasExImmediateEffect()) {
            final IItemHandler handler = ItemHandler.getInstance().getHandler(target.getEtcItem());
            if (handler == null) {
                LOGGER.warn("No item handler registered for item ID: " + target.getId() + ".");
            } else {
                handler.useItem(this, target, false);
            }
            ItemEngine.getInstance().destroyItem("Consume", target, this, null);
        } else {
            // if item is instance of L2ArmorType or L2WeaponType broadcast an "Attention" system message
            if ((target.getItemType() instanceof ArmorType) || (target.getItemType() instanceof WeaponType)) {
                if (target.getEnchantLevel() > 0) {
                    smsg = getSystemMessage(SystemMessageId.ATTENTION_C1_HAS_PICKED_UP_S2_S3);
                    smsg.addPcName(this);
                    smsg.addInt(target.getEnchantLevel());
                    smsg.addItemName(target.getId());
                    broadcastPacket(smsg, 1400);
                } else {
                    smsg = getSystemMessage(SystemMessageId.ATTENTION_C1_HAS_PICKED_UP_S2);
                    smsg.addPcName(this);
                    smsg.addItemName(target.getId());
                    broadcastPacket(smsg, 1400);
                }
            }

            // Check if a Party is in progress
            if (isInParty()) {
                _party.distributeItem(this, target);
            } else if ((target.getId() == CommonItem.ADENA) && (inventory.getAdenaInstance() != null)) {
                addAdena("Pickup", target.getCount(), null, true);
                ItemEngine.getInstance().destroyItem("Pickup", target, this, null);
            } else {
                addItem("Pickup", target, null, true);
                // Auto-Equip arrows/bolts if player has a bow/crossbow and player picks up arrows/bolts.
                final Item weapon = inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
                if (weapon != null) {
                    final EtcItem etcItem = target.getEtcItem();
                    if (etcItem != null) {
                        final EtcItemType itemType = etcItem.getItemType();
                        if (((weapon.getItemType() == WeaponType.BOW) && (itemType == EtcItemType.ARROW)) || (((weapon.getItemType() == WeaponType.CROSSBOW) || (weapon.getItemType() == WeaponType.TWO_HAND_CROSSBOW)) && (itemType == EtcItemType.BOLT))) {
                            inventory.findAmmunitionForCurrentWeapon();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void doAutoAttack(Creature target) {
        super.doAutoAttack(target);
        setRecentFakeDeath(false);
    }

    @Override
    public void doCast(Skill skill) {
        super.doCast(skill);
        setRecentFakeDeath(false);
    }

    public boolean canOpenPrivateStore() {
        return !_isSellingBuffs && !isAlikeDead() && !_inOlympiadMode && !isMounted() && !isInsideZone(ZoneType.NO_STORE) && !isCastingNow();
    }

    public void tryOpenPrivateBuyStore() {
        // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
        if (canOpenPrivateStore()) {
            if ((privateStoreType == PrivateStoreType.BUY) || (privateStoreType == PrivateStoreType.BUY_MANAGE)) {
                setPrivateStoreType(PrivateStoreType.NONE);
            }
            if (privateStoreType == PrivateStoreType.NONE) {
                if (_waitTypeSitting) {
                    standUp();
                }
                setPrivateStoreType(PrivateStoreType.BUY_MANAGE);
                sendPacket(new PrivateStoreManageListBuy(1, this));
                sendPacket(new PrivateStoreManageListBuy(2, this));
            }
        } else {
            if (isInsideZone(ZoneType.NO_STORE)) {
                sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
            }
            sendPacket(ActionFailed.STATIC_PACKET);
        }
    }

    public final PreparedMultisellListHolder getMultiSell() {
        return _currentMultiSell;
    }

    public final void setMultiSell(PreparedMultisellListHolder list) {
        _currentMultiSell = list;
    }

    /**
     * Set a target. <B><U> Actions</U> :</B>
     * <ul>
     * <li>Remove the Player from the _statusListener of the old target if it was a Creature</li>
     * <li>Add the Player to the _statusListener of the new target if it's a Creature</li>
     * <li>Target the new WorldObject (add the target to the Player _target, _knownObject and Player to _KnownObject of the WorldObject)</li>
     * </ul>
     *
     * @param newTarget The WorldObject to target
     */
    @Override
    public void setTarget(WorldObject newTarget) {
        if (newTarget != null) {
            final boolean isInParty = (GameUtils.isPlayer(newTarget) && isInParty() && _party.containsPlayer(newTarget.getActingPlayer()));

            // Prevents /target exploiting
            if (!isInParty && (Math.abs(newTarget.getZ() - getZ()) > 1000)) {
                newTarget = null;
            }

            // Check if the new target is visible
            if ((newTarget != null) && !isInParty && !newTarget.isSpawned()) {
                newTarget = null;
            }

            // vehicles cant be targeted
            if (!isGM() && (newTarget instanceof Vehicle)) {
                newTarget = null;
            }
        }

        // Get the current target
        final WorldObject oldTarget = getTarget();

        if (oldTarget != null) {
            if (oldTarget.equals(newTarget)) // no target change?
            {
                // Validate location of the target.
                if (newTarget.getObjectId() != getObjectId()) {
                    sendPacket(new ValidateLocation(newTarget));
                }
                return;
            }

            // Remove the target from the status listener.
            oldTarget.removeStatusListener(this);
        }

        if (GameUtils.isCreature(newTarget)) {
            final Creature target = (Creature) newTarget;

            // Validate location of the new target.
            if (newTarget.getObjectId() != getObjectId()) {
                sendPacket(new ValidateLocation(target));
            }

            // Show the client his new target.
            sendPacket(new MyTargetSelected(this, target));

            // Register target to listen for hp changes.
            target.addStatusListener(this);

            // Send max/current hp.
            final StatusUpdate su = new StatusUpdate(target);
            su.addUpdate(StatusUpdateType.MAX_HP, target.getMaxHp());
            su.addUpdate(StatusUpdateType.CUR_HP, (int) target.getCurrentHp());
            sendPacket(su);

            // To others the new target, and not yourself!
            Broadcast.toKnownPlayers(this, new TargetSelected(getObjectId(), newTarget.getObjectId(), getX(), getY(), getZ()));

            // Send buffs
            sendPacket(new ExAbnormalStatusUpdateFromTarget(target));
        }

        // Target was removed?
        if ((newTarget == null) && (getTarget() != null)) {
            broadcastPacket(new TargetUnselected(this));
        }

        // Target the new WorldObject (add the target to the Player _target, _knownObject and Player to _KnownObject of the WorldObject)
        super.setTarget(newTarget);
    }

    /**
     * Return the active weapon instance (always equiped in the right hand).
     */
    @Override
    public Item getActiveWeaponInstance() {
        return inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
    }

    /**
     * Return the active weapon item (always equiped in the right hand).
     */
    @Override
    public Weapon getActiveWeaponItem() {
        final Item weapon = getActiveWeaponInstance();
        if (isNull(weapon)) {
            return _fistsWeaponItem;
        }

        return (Weapon) weapon.getTemplate();
    }

    public Item getChestArmorInstance() {
        return inventory.getPaperdollItem(InventorySlot.CHEST);
    }

    public Item getLegsArmorInstance() {
        return inventory.getPaperdollItem(InventorySlot.LEGS);
    }

    public Armor getActiveChestArmorItem() {
        final Item armor = getChestArmorInstance();

        if (armor == null) {
            return null;
        }

        return (Armor) armor.getTemplate();
    }

    public Armor getActiveLegsArmorItem() {
        final Item legs = getLegsArmorInstance();

        if (legs == null) {
            return null;
        }

        return (Armor) legs.getTemplate();
    }

    public boolean isWearingHeavyArmor() {
        final Item legs = getLegsArmorInstance();
        final Item armor = getChestArmorInstance();

        if ((armor != null) && (legs != null)) {
            if ((legs.getItemType() == ArmorType.HEAVY) && (armor.getItemType() == ArmorType.HEAVY)) {
                return true;
            }
        }
        if (armor != null) {
            if (((inventory.getPaperdollItem(CHEST.slot()).getBodyPart() == BodyPart.FULL_ARMOR) && (armor.getItemType() == ArmorType.HEAVY))) {
                return true;
            }
        }
        return false;
    }

    public boolean isWearingLightArmor() {
        final Item legs = getLegsArmorInstance();
        final Item armor = getChestArmorInstance();

        if ((armor != null) && (legs != null)) {
            if ((legs.getItemType() == ArmorType.LIGHT) && (armor.getItemType() == ArmorType.LIGHT)) {
                return true;
            }
        }
        if (armor != null) {
            if (((inventory.getPaperdollItem(CHEST.slot()).getBodyPart() == BodyPart.FULL_ARMOR) && (armor.getItemType() == ArmorType.LIGHT))) {
                return true;
            }
        }
        return false;
    }

    public boolean isWearingMagicArmor() {
        final Item legs = getLegsArmorInstance();
        final Item armor = getChestArmorInstance();

        if ((armor != null) && (legs != null)) {
            if ((legs.getItemType() == ArmorType.MAGIC) && (armor.getItemType() == ArmorType.MAGIC)) {
                return true;
            }
        }
        if (armor != null) {
            if (((inventory.getPaperdollItem(BodyPart.CHEST.slot()).getBodyPart() == BodyPart.FULL_ARMOR) && (armor.getItemType() == ArmorType.MAGIC))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the secondary weapon instance (always equiped in the left hand).
     */
    @Override
    public Item getSecondaryWeaponInstance() {
        return inventory.getPaperdollItem(InventorySlot.LEFT_HAND);
    }

    /**
     * Return the secondary ItemTemplate item (always equiped in the left hand).<BR>
     * Arrows, Shield..<BR>
     */
    @Override
    public ItemTemplate getSecondaryWeaponItem() {
        final Item item = inventory.getPaperdollItem(InventorySlot.LEFT_HAND);
        if (item != null) {
            return item.getTemplate();
        }
        return null;
    }

    /**
     * Kill the Creature, Apply Death Penalty, Manage gain/loss Karma and Item Drop. <B><U> Actions</U> :</B>
     * <li>Reduce the Experience of the Player in function of the calculated Death Penalty</li>
     * <li>If necessary, unsummon the Pet of the killed Player</li>
     * <li>Manage Karma gain for attacker and Karam loss for the killed Player</li>
     * <li>If the killed Player has Karma, manage Drop Item</li>
     * <li>Kill the Player</li>
     *
     * @param killer
     */
    @Override
    public boolean doDie(Creature killer) {
        if (nonNull(killer)) {
            if (!super.doDie(killer)) {
                return false;
            }

            var pk = killer.getActingPlayer();

            if (nonNull(pk)) {
                onKilledByPlayer(pk);
            }

            // Clear resurrect xp calculation
            data.setExpBeforeDeath(0);
            Collection<Item> droppedItems = onDieDropItem(killer);
            sendPacket(new ExDieInfo(lastDamages, droppedItems));
            

            final boolean insidePvpZone = isInsideZone(ZoneType.PVP) || isInsideZone(ZoneType.SIEGE);

            if (!insidePvpZone && (pk != null)) {
                final Clan pkClan = pk.getClan();
                if ((pkClan != null) && (_clan != null) && !isAcademyMember() && !(pk.isAcademyMember())) {
                    final ClanWar clanWar = _clan.getWarWith(pkClan.getId());
                    if ((clanWar != null) && AntiFeedManager.getInstance().check(killer, this)) {
                        clanWar.onKill(pk, this);
                    }
                }
            }
            // If player is Lucky shouldn't get penalized.
            if (!isLucky() && !insidePvpZone) {
                calculateDeathExpPenalty(killer);
            }
        }

        if (isMounted()) {
            stopFeed();
        }
        synchronized (this) {
            if (isFakeDeath()) {
                stopFakeDeath(true);
            }
        }

        // Unsummon Cubics
        if (!_cubics.isEmpty()) {
            _cubics.values().forEach(CubicInstance::deactivate);
            _cubics.clear();
        }

        if (_agathionId != 0) {
            setAgathionId(0);
        }

        stopRentPet();
        stopWaterTask();

        if (hasCharmOfCourage) {
            if (isInSiege()) {
                reviveRequest(this, null, false, 0);
            }
            hasCharmOfCourage = false;
            sendPacket(new EtcStatusUpdate(this));
        }

        doIfNonNull(getInstanceWorld(), instance -> instance.onDeath(this));

        AntiFeedManager.getInstance().setLastDeathTime(getObjectId());
        // FIXME: Karma reduction tempfix.
        if (getReputation() < 0) {
            final int newRep = getReputation() - (getReputation() / 4);
            setReputation(newRep < -20 ? newRep : 0);
        }
        if(autoPlaySettings.isActive()) {
            AutoPlayEngine.getInstance().stopAutoPlay(this);
        }
        return true;
    }

    private void onKilledByPlayer(Player killer) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPvPKill(killer, this), this);

        if (Event.isParticipant(killer)) {
            killer.getEventStatus().addKill(this);
        } else {
            sendPacket(new ExNewPk(killer));
            getDAO(PlayerDAO.class).updatePlayerKiller(objectId, killer.objectId, Instant.now().getEpochSecond());
        }

        // pvp/pk item rewards
        if (!(Config.DISABLE_REWARDS_IN_INSTANCES && getInstanceId() != 0) && !(Config.DISABLE_REWARDS_IN_PVP_ZONES && isInsideZone(ZoneType.PVP))) {
            if (Config.REWARD_PVP_ITEM && _pvpFlag != 0) {
                killer.addItem("PvP Item Reward", Config.REWARD_PVP_ITEM_ID, Config.REWARD_PVP_ITEM_AMOUNT, this, Config.REWARD_PVP_ITEM_MESSAGE);
            }

            if (Config.REWARD_PK_ITEM && _pvpFlag == 0) {
                killer.addItem("PK Item Reward", Config.REWARD_PK_ITEM_ID, Config.REWARD_PK_ITEM_AMOUNT, this, Config.REWARD_PK_ITEM_MESSAGE);
            }
        }

        // announce pvp/pk
        if (Config.ANNOUNCE_PK_PVP && !killer.isGM()) {
            String msg = "";
            if (_pvpFlag == 0) {
                msg = Config.ANNOUNCE_PK_MSG.replace("$killer", killer.getName()).replace("$target", getName());

                if (Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE) {
                    Broadcast.toAllOnlinePlayers( getSystemMessage(SystemMessageId.S1).addString(msg));
                } else {
                    Broadcast.toAllOnlinePlayers(msg, false);
                }
            } else {
                msg = Config.ANNOUNCE_PVP_MSG.replace("$killer", killer.getName()).replace("$target", getName());
                if (Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE) {
                    Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.S1).addString(msg));
                } else {
                    Broadcast.toAllOnlinePlayers(msg, false);
                }
            }
        }
    }

    private Collection<Item> onDieDropItem(Creature killer) {
        if (Event.isParticipant(this) || (killer == null)) {
            return Collections.emptyList();
        }

        final Player pk = killer.getActingPlayer();
        if (getReputation() >= 0 && nonNull(pk) && falseIfNullOrElse(pk.getClan(), c -> c.isAtWarWith(_clan))) {
            return Collections.emptyList();
        }

        if ( (!isInsideZone(ZoneType.PVP) || isNull(pk)) && (!isGM() || Config.KARMA_DROP_GM)) {
            boolean isKarmaDrop = false;
            int dropEquip = 0;
            int dropEquipWeapon = 0;
            int rateDropItem = 0;
            int dropLimit = 0;
            double dropPercent = 0;

            // Classic calculation.
            if (GameUtils.isPlayable(killer) && (getReputation() < 0) && (_pkKills >= Config.KARMA_PK_LIMIT)) {
                isKarmaDrop = true;
                dropPercent = Config.KARMA_RATE_DROP * getStats().getValue(Stat.REDUCE_DEATH_PENALTY_BY_PVP, 1);
                dropEquip = Config.KARMA_RATE_DROP_EQUIP;
                dropEquipWeapon = Config.KARMA_RATE_DROP_EQUIP_WEAPON;
                rateDropItem = Config.KARMA_RATE_DROP_ITEM;
                dropLimit = Config.KARMA_DROP_LIMIT;
            } else if (GameUtils.isNpc(killer)) {
                dropPercent = Config.PLAYER_RATE_DROP * (killer.isRaid() ? getStats().getValue(Stat.REDUCE_DEATH_PENALTY_BY_RAID, 1) : getStats().getValue(Stat.REDUCE_DEATH_PENALTY_BY_MOB, 1));
                dropEquip = Config.PLAYER_RATE_DROP_EQUIP;
                dropEquipWeapon = Config.PLAYER_RATE_DROP_EQUIP_WEAPON;
                rateDropItem = Config.PLAYER_RATE_DROP_ITEM;
                dropLimit = Config.PLAYER_DROP_LIMIT;
            }


            if (Rnd.chance(dropPercent)) {
                int dropCount = 0;
                int itemDropPercent;
                List<Item> droppedItems =  new ArrayList<>(dropLimit);

                for (Item itemDrop : inventory.getItems()) {
                    // Don't drop
                    if (itemDrop.isTimeLimitedItem() || // Dont drop Time Limited Items
                            !itemDrop.isDropable() || (itemDrop.getId() == CommonItem.ADENA) || // Adena
                            (itemDrop.getTemplate().getType2() == ItemTemplate.TYPE2_QUEST) || // Quest Items
                            ((pet != null) && (pet.getControlObjectId() == itemDrop.getId())) || // Control Item of active pet
                            (Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_ITEMS, itemDrop.getId()) >= 0) || // Item listed in the non droppable item list
                            (Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS, itemDrop.getId()) >= 0 // Item listed in the non droppable pet item list
                            )) {
                        continue;
                    }

                    if (itemDrop.isEquipped()) {
                        // Set proper chance according to Item type of equipped Item
                        itemDropPercent = itemDrop.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? dropEquipWeapon : dropEquip;
                        inventory.unEquipItemInSlot(InventorySlot.fromId(itemDrop.getLocationSlot()));
                    } else {
                        itemDropPercent = rateDropItem; // Item in inventory
                    }

                    // NOTE: Each time an item is dropped, the chance of another item being dropped gets lesser (dropCount * 2)
                    if (Rnd.chance(itemDropPercent)) {
                        dropItem("DieDrop", itemDrop, killer, true);
                        droppedItems.add(itemDrop);
                        if (isKarmaDrop) {
                            LOGGER.warn("{} has karma and dropped {} {}", this, itemDrop.getCount(), itemDrop);
                        } else {
                            LOGGER.warn("{} dropped {} {}", this, itemDrop.getCount(), itemDrop);
                        }

                        if (++dropCount >= dropLimit) {
                            break;
                        }
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    public void onPlayeableKill(Playable killedPlayable) {
        final Player killedPlayer = killedPlayable.getActingPlayer();

        // Avoid nulls && check if player != killedPlayer
        if ((killedPlayer == null) || (this == killedPlayer)) {
            return;
        }

        // Duel support
        if (isInDuel() && killedPlayer.isInDuel()) {
            return;
        }

        // Do nothing if both players are in PVP zone
        if (isInsideZone(ZoneType.PVP) && killedPlayer.isInsideZone(ZoneType.PVP)) {
            return;
        }

        // If both players are in SIEGE zone just increase siege kills/deaths
        if (isInsideZone(ZoneType.SIEGE) && killedPlayer.isInsideZone(ZoneType.SIEGE)) {
            if (!isSiegeFriend(killedPlayable)) {
                final Clan targetClan = killedPlayer.getClan();
                if ((_clan != null) && (targetClan != null)) {
                    _clan.addSiegeKill();
                    targetClan.addSiegeDeath();
                }
            }
            return;
        }

        if (checkIfPvP(killedPlayer)) {
            // Check if player should get + rep
            if (killedPlayer.getReputation() < 0) {
                final int levelDiff = killedPlayer.getLevel() - getLevel();
                if ((getReputation() >= 0) && (levelDiff < 11) && (levelDiff > -11)) // TODO: Time check, same player can't be killed again in 8 hours
                {
                    setReputation(getReputation() + Config.REPUTATION_INCREASE);
                }
            }

            setPvpKills(_pvpKills + 1);

            updatePvpTitleAndColor(true);
        } else if ((getReputation() > 0) && (_pkKills == 0)) {
            setReputation(0);
            setPkKills(1);
        } else {
            // Calculate new karma and increase pk count
            setReputation(getReputation() - Formulas.calculateKarmaGain(getPkKills(), GameUtils.isSummon(killedPlayable)));
            setPkKills(getPkKills() + 1);
        }

        final UserInfo ui = new UserInfo(this, false);
        ui.addComponentType(UserInfoType.SOCIAL);
        sendPacket(ui);
        checkItemRestriction();
    }

    public void updatePvpTitleAndColor(boolean broadcastInfo) {
        if (Config.PVP_COLOR_SYSTEM_ENABLED)
        {
            if ((_pvpKills >= (Config.PVP_AMOUNT1)) && (_pvpKills < (Config.PVP_AMOUNT2))) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT1 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT1);
            } else if ((_pvpKills >= (Config.PVP_AMOUNT2)) && (_pvpKills < (Config.PVP_AMOUNT3))) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT2 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT2);
            } else if ((_pvpKills >= (Config.PVP_AMOUNT3)) && (_pvpKills < (Config.PVP_AMOUNT4))) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT3 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT3);
            } else if ((_pvpKills >= (Config.PVP_AMOUNT4)) && (_pvpKills < (Config.PVP_AMOUNT5))) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT4 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT4);
            } else if (_pvpKills >= (Config.PVP_AMOUNT5)) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT5 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT5);
            }

            if (broadcastInfo) {
                broadcastTitleInfo();
            }
        }
    }

    public void updatePvPStatus() {
        if (isInsideZone(ZoneType.PVP)) {
            return;
        }
        setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_NORMAL_TIME);

        if (_pvpFlag == 0) {
            startPvPFlag();
        }
    }

    public void updatePvPStatus(Creature target) {
        final Player player_target = target.getActingPlayer();
        if (player_target == null) {
            return;
        }

        if (this == player_target) {
            return;
        }

        if (_isInDuel && (player_target.getDuelId() == getDuelId())) {
            return;
        }
        if ((!isInsideZone(ZoneType.PVP) || !player_target.isInsideZone(ZoneType.PVP)) && (player_target.getReputation() >= 0)) {
            if (checkIfPvP(player_target)) {
                setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_PVP_TIME);
            } else {
                setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_NORMAL_TIME);
            }
            if (_pvpFlag == 0) {
                startPvPFlag();
            }
        }
    }

    /**
     * @return {@code true} if player has Lucky effect and is level 9 or less
     */
    public boolean isLucky() {
        return (getLevel() <= 9) && isAffectedBySkill(CommonSkill.LUCKY.getId());
    }

    /**
     * Restore the specified % of experience this Player has lost and sends a Server->Client StatusUpdate packet.
     *
     * @param restorePercent
     */
    public void restoreExp(double restorePercent) {
        if (data.getExpBeforeDeath() > 0) {
            // Restore the specified % of lost experience.
            getStats().addExp(Math.round(((data.getExpBeforeDeath() - getExp()) * restorePercent) / 100));
            data.setExpBeforeDeath(0);
        }
    }

    /**
     * Reduce the Experience (and level if necessary) of the Player in function of the calculated Death Penalty.<BR>
     * <B><U> Actions</U> :</B>
     * <li>Calculate the Experience loss</li>
     * <li>Set the value of _expBeforeDeath</li>
     * <li>Set the new Experience value of the Player and Decrease its level if necessary</li>
     * <li>Send a Server->Client StatusUpdate packet with its new Experience</li>
     *
     * @param killer
     */
    public void calculateDeathExpPenalty(Creature killer) {
        final int lvl = getLevel();

        var levelData = LevelData.getInstance();

        float percentLost = levelData.getXpPercentLost(getLevel());

        if (killer != null) {
            if (killer.isRaid()) {
                percentLost *= getStats().getValue(Stat.REDUCE_EXP_LOST_BY_RAID, 1);
            } else if (GameUtils.isMonster(killer)) {
                percentLost *= getStats().getValue(Stat.REDUCE_EXP_LOST_BY_MOB, 1);
            } else if (GameUtils.isPlayable(killer)) {
                percentLost *= getStats().getValue(Stat.REDUCE_EXP_LOST_BY_PVP, 1);
            }
        }

        if (getReputation() < 0) {
            percentLost *= Config.RATE_KARMA_EXP_LOST;
        }

        // Calculate the Experience loss
        long lostExp = 0;
        if (!Event.isParticipant(this)) {
            if (lvl < LevelData.getInstance().getMaxLevel()) {
                lostExp = Math.round(((getStats().getExpForLevel(lvl + 1) - getStats().getExpForLevel(lvl)) * percentLost) / 100);
            } else {
                lostExp = Math.round(((getStats().getExpForLevel(LevelData.getInstance().getMaxLevel()) - getStats().getExpForLevel(LevelData.getInstance().getMaxLevel() - 1)) * percentLost) / 100);
            }
        }

        if (GameUtils.isPlayable(killer) && atWarWith(killer.getActingPlayer())) {
            lostExp /= 4.0;
        }

        data.setExpBeforeDeath(getExp());
        getStats().removeExp(lostExp);
    }

    /**
     * Stop the HP/MP/CP Regeneration task. <B><U> Actions</U> :</B>
     * <li>Set the RegenActive flag to False</li>
     * <li>Stop the HP/MP/CP Regeneration task</li>
     */
    public void stopAllTimers() {
        stopHpMpRegeneration();
        stopWarnUserTakeBreak();
        stopWaterTask();
        stopFeed();
        clearPetData();
        storePetFood(_mountNpcId);
        stopRentPet();
        stopPvpRegTask();
        stopSoulTask();
        stopChargeTask();
        stopFameTask();
        stopRecoGiveTask();
        stopOnlineTimeUpdateTask();
    }

    @Override
    public Pet getPet() {
        return pet;
    }

    /**
     * Set the summoned Pet of the Player.
     *
     * @param pet
     */
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public Map<Integer, Summon> getServitors() {
        return _servitors == null ? Collections.emptyMap() : _servitors;
    }

    public Summon getAnyServitor() {
        return getServitors().values().stream().findAny().orElse(null);
    }

    public Summon getFirstServitor() {
        return getServitors().values().stream().findFirst().orElse(null);
    }

    @Override
    public Summon getServitor(int objectId) {
        return getServitors().get(objectId);
    }

    public List<Summon> getServitorsAndPets() {
        final List<Summon> summons = new ArrayList<>();
        summons.addAll(getServitors().values());

        if (pet != null) {
            summons.add(pet);
        }

        return summons;
    }

    /**
     * @return any summoned trap by this player or null.
     */
    public Trap getTrap() {
        return getSummonedNpcs().stream().filter(GameUtils::isTrap).map(Trap.class::cast).findAny().orElse(null);
    }

    public void addServitor(Summon servitor) {
        if (_servitors == null) {
            synchronized (this) {
                if (_servitors == null) {
                    _servitors = new ConcurrentHashMap<>(1);
                }
            }
        }
        _servitors.put(servitor.getObjectId(), servitor);
    }

    /**
     * @return the Summon of the Player or null.
     */
    public Set<TamedBeast> getTrainedBeasts() {
        return tamedBeast;
    }

    /**
     * Set the Summon of the Player.
     *
     * @param tamedBeast
     */
    public void addTrainedBeast(TamedBeast tamedBeast) {
        if (this.tamedBeast == null) {
            synchronized (this) {
                if (this.tamedBeast == null) {
                    this.tamedBeast = ConcurrentHashMap.newKeySet();
                }
            }
        }
        this.tamedBeast.add(tamedBeast);
    }

    /**
     * @return the Player requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
     */
    public Request getRequest() {
        return _request;
    }

    /**
     * @return the Player requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
     */
    public Player getActiveRequester() {
        var requester = activeRequester;
        if (nonNull(requester)) {
            if (requester.isRequestExpired() && isNull(activeTradeList)) {
                activeRequester = null;
            }
        }
        return activeRequester;
    }

    /**
     * Set the Player requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
     *
     * @param requester
     */
    public void setActiveRequester(Player requester) {
        activeRequester = requester;
    }

    /**
     * @return True if a transaction is in progress.
     */
    public boolean isProcessingRequest() {
        return nonNull(getActiveRequester()) || requestExpireTime > WorldTimeController.getInstance().getGameTicks();
    }

    /**
     * @return True if a transaction is in progress.
     */
    public boolean isProcessingTransaction() {
        return nonNull(getActiveRequester()) || nonNull(activeTradeList) || requestExpireTime > WorldTimeController.getInstance().getGameTicks();
    }

    /**
     * Select the Warehouse to be used in next activity.
     *
     * @param partner
     */
    public void onTransactionRequest(Player partner) {
        requestExpireTime = WorldTimeController.getInstance().getGameTicks() + (REQUEST_TIMEOUT * WorldTimeController.TICKS_PER_SECOND);
        partner.setActiveRequester(this);
    }

    /**
     * Return true if last request is expired.
     *
     * @return
     */
    public boolean isRequestExpired() {
        return requestExpireTime <= WorldTimeController.getInstance().getGameTicks();
    }

    /**
     * Select the Warehouse to be used in next activity.
     */
    public void onTransactionResponse() {
        requestExpireTime = 0;
    }

    public Warehouse getActiveWarehouse() {
        return activeWarehouse;
    }

    /**
     * Select the Warehouse to be used in next activity.
     *
     * @param warehouse
     */
    public void setActiveWarehouse(Warehouse warehouse) {
        activeWarehouse = warehouse;
    }

    /**
     * @return active TradeList.
     */
    public TradeList getActiveTradeList() {
        return activeTradeList;
    }

    private void onTradeStart(Player partner) {
        activeTradeList = new TradeList(this, partner);
        sendPacket(TradeStart.partnerInfo(this, partner));
        sendPacket(TradeStart.itemsInfo(this));
        sendPacket(getSystemMessage(SystemMessageId.YOU_BEGIN_TRADING_WITH_C1).addPcName(partner));
    }

    public void onTradeConfirm(Player partner) {
        sendPacket(TradeOtherDone.STATIC_PACKET);
        sendPacket(getSystemMessage(SystemMessageId.C1_HAS_CONFIRMED_THE_TRADE).addPcName(partner));
    }

    private void onTradeCancel(Player partner) {
        if (isNull(activeTradeList)) {
            return;
        }

        activeTradeList.lock();
        activeTradeList = null;

        sendPacket(TradeDone.CANCELLED);
        sendPacket(getSystemMessage(SystemMessageId.C1_HAS_CANCELLED_THE_TRADE).addPcName(partner));
    }

    public void onTradeFinish(boolean successfull) {
        activeTradeList = null;
        sendPacket(TradeDone.COMPLETED);
        if (successfull) {
            sendPacket(SystemMessageId.YOUR_TRADE_WAS_SUCCESSFUL);
        }
    }

    public void startTrade(Player partner) {
        onTradeStart(partner);
        partner.onTradeStart(this);
    }

    public void cancelActiveTrade() {
        if (isNull(activeTradeList)) {
            return;
        }

        var partner = activeTradeList.getPartner();
        if (nonNull(partner)) {
            partner.onTradeCancel(this);
        }
        onTradeCancel(this);
    }

    public boolean hasManufactureShop() {
        return (_manufactureItems != null) && !_manufactureItems.isEmpty();
    }

    /**
     * Get the manufacture items map of this player.
     *
     * @return the the manufacture items map
     */
    public Map<Integer, ManufactureItem> getManufactureItems() {
        if (_manufactureItems == null) {
            synchronized (this) {
                if (_manufactureItems == null) {
                    _manufactureItems = Collections.synchronizedMap(new LinkedHashMap<>());
                }
            }
        }
        return _manufactureItems;
    }

    /**
     * Get the store name, if any.
     *
     * @return the store name
     */
    public String getStoreName() {
        return _storeName;
    }

    /**
     * Set the store name.
     *
     * @param name the store name to set
     */
    public void setStoreName(String name) {
        _storeName = name == null ? "" : name;
    }

    /**
     * @return the _buyList object of the Player.
     */
    public TradeList getSellList() {
        if (_sellList == null) {
            _sellList = new TradeList(this);
        }
        return _sellList;
    }

    /**
     * @return the _buyList object of the Player.
     */
    public TradeList getBuyList() {
        if (_buyList == null) {
            _buyList = new TradeList(this);
        }
        return _buyList;
    }

    /**
     * <B><U> Values </U> :</B>
     * <li>0 : STORE_PRIVATE_NONE</li>
     * <li>1 : STORE_PRIVATE_SELL</li>
     * <li>2 : sellmanage</li><BR>
     * <li>3 : STORE_PRIVATE_BUY</li><BR>
     * <li>4 : buymanage</li><BR>
     * <li>5 : STORE_PRIVATE_MANUFACTURE</li><BR>
     *
     * @return the Private Store type of the Player.
     */
    public PrivateStoreType getPrivateStoreType() {
        return privateStoreType;
    }

    /**
     * Set the Private Store type of the Player. <B><U> Values </U> :</B>
     * <li>0 : STORE_PRIVATE_NONE</li>
     * <li>1 : STORE_PRIVATE_SELL</li>
     * <li>2 : sellmanage</li><BR>
     * <li>3 : STORE_PRIVATE_BUY</li><BR>
     * <li>4 : buymanage</li><BR>
     * <li>5 : STORE_PRIVATE_MANUFACTURE</li><BR>
     *
     * @param privateStoreType
     */
    public void setPrivateStoreType(PrivateStoreType privateStoreType) {
        this.privateStoreType = privateStoreType;

        if (Config.OFFLINE_DISCONNECT_FINISHED && (privateStoreType == PrivateStoreType.NONE) && ((_client == null) || _client.isDetached())) {
            Disconnection.of(this).storeMe().deleteMe();
        }
    }

    /**
     * @return the _clan object of the Player.
     */
    @Override
    public Clan getClan() {
        return _clan;
    }

    /**
     * Set the _clan object, _clanId, _clanLeader Flag and title of the Player.
     *
     * @param clan
     */
    public void setClan(Clan clan) {
        _clan = clan;

        if (clan == null) {
            setTitle("");
            clanId = 0;
            _clanPrivileges = new EnumIntBitmask<>(ClanPrivilege.class, false);
            setPledgeType(0);
            setPowerGrade(0);
            setLvlJoinedAcademy(0);
            setApprentice(0);
            setSponsor(0);
            activeWarehouse = null;
            return;
        }

        if (!clan.isMember(getObjectId())) {
            // char has been kicked from clan
            setClan(null);
            return;
        }

        clanId = clan.getId();
    }

    /**
     * @return True if the Player is the leader of its clan.
     */
    public boolean isClanLeader() {
        if (_clan == null) {
            return false;
        }
        return getObjectId() == _clan.getLeaderId();
    }

    /**
     * Disarm the player's weapon.
     *
     * @return {@code true} if the player was disarmed or doesn't have a weapon to disarm, {@code false} otherwise.
     */
    public boolean disarmWeapons() {
        // If there is no weapon to disarm then return true.
        final Item wpn = inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
        if (wpn == null) {
            return true;
        }

        var modified = inventory.unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
        final InventoryUpdate iu = new InventoryUpdate();
        for (Item itm : modified) {
            iu.addModifiedItem(itm);
        }

        sendInventoryUpdate(iu);
        abortAttack();
        broadcastUserInfo();

        if (modified.size() > 0) {
            var unequipped =  modified.iterator().next();
            final SystemMessage sm;
            if (unequipped.getEnchantLevel() > 0) {
                sm = getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(unequipped.getEnchantLevel());
            } else {
                sm = getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
            }
            sm.addItemName(unequipped);
            sendPacket(sm);
        }
        return true;
    }

    /**
     * Disarm the player's shield.
     *
     * @return {@code true}.
     */
    public boolean disarmShield() {
        final Item sld = inventory.getPaperdollItem(InventorySlot.LEFT_HAND);
        if (sld != null) {
            var modified = inventory.unEquipItemInBodySlotAndRecord(sld.getBodyPart());
            final InventoryUpdate iu = new InventoryUpdate();
            for (Item itm : modified) {
                iu.addModifiedItem(itm);
            }
            sendInventoryUpdate(iu);

            abortAttack();
            broadcastUserInfo();

            var iterator = modified.iterator();
            if (iterator.hasNext()) {
                var unequipped = iterator.next();
                SystemMessage sm;
                if (unequipped.getEnchantLevel() > 0) {
                    sm = getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(unequipped.getEnchantLevel());
                } else {
                    sm = getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                }
                sm.addItemName(unequipped);
                sendPacket(sm);
            }
        }
        return true;
    }

    public boolean mount(Summon pet) {
        if (!Config.ALLOW_MOUNTS_DURING_SIEGE && isInsideZone(ZoneType.SIEGE)) {
            return false;
        }

        if (!disarmWeapons() || !disarmShield() || isTransformed()) {
            return false;
        }

        getEffectList().stopAllToggles();
        setMount(pet.getId(), pet.getLevel());
        setMountObjectID(pet.getControlObjectId());
        clearPetData();
        startFeed(pet.getId());
        broadcastPacket(new Ride(this));

        // Notify self and others about speed change
        broadcastUserInfo();

        pet.unSummon(this);
        return true;
    }

    public boolean mount(int npcId, int controlItemObjId, boolean useFood) {
        if (!disarmWeapons() || !disarmShield() || isTransformed()) {
            return false;
        }

        getEffectList().stopAllToggles();
        setMount(npcId, getLevel());
        clearPetData();
        setMountObjectID(controlItemObjId);
        broadcastPacket(new Ride(this));

        // Notify self and others about speed change
        broadcastUserInfo();
        if (useFood) {
            startFeed(npcId);
        }
        return true;
    }

    public boolean mountPlayer(Summon pet) {
        if ((pet != null) && pet.isMountable() && !isMounted() && !isBetrayed()) {
            if (isDead()) {
                // A strider cannot be ridden when dead
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.A_MOUNT_CANNOT_BE_RIDDEN_WHEN_DEAD);
                return false;
            } else if (pet.isDead()) {
                // A dead strider cannot be ridden.
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.A_DEAD_MOUNT_CANNOT_BE_RIDDEN);
                return false;
            } else if (pet.isInCombat() || pet.isRooted()) {
                // A strider in battle cannot be ridden
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.A_MOUNT_IN_BATTLE_CANNOT_BE_RIDDEN);
                return false;

            } else if (isInCombat()) {
                // A strider cannot be ridden while in battle
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.A_MOUNT_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE);
                return false;
            } else if (_waitTypeSitting) {
                // A strider can be ridden only when standing
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.A_MOUNT_CAN_BE_RIDDEN_ONLY_WHEN_STANDING);
                return false;
            } else if (isFishing()) {
                // You can't mount, dismount, break and drop items while fishing
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
                return false;
            } else if (isTransformed()) {
                // no message needed, player while transformed doesn't have mount action
                sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            } else if (inventory.getItemByItemId(9819) != null) {
                sendPacket(ActionFailed.STATIC_PACKET);
                // FIXME: Wrong Message
                sendMessage("You cannot mount a steed while holding a flag.");
                return false;
            } else if (pet.isHungry()) {
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.A_HUNGRY_MOUNT_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
                return false;
            } else if (!GameUtils.checkIfInRange(200, this, pet, true)) {
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.YOU_ARE_TOO_FAR_AWAY_FROM_YOUR_MOUNT_TO_RIDE);
                return false;
            } else if (!pet.isDead() && !isMounted()) {
                mount(pet);
            }
        } else if (isRentedPet()) {
            stopRentPet();
        } else if (isMounted()) {
            if ((_mountType == MountType.WYVERN) && isInsideZone(ZoneType.NO_LANDING)) {
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION);
                return false;
            } else if (isHungry()) {
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.A_HUNGRY_MOUNT_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
                return false;
            } else {
                dismount();
            }
        }
        return true;
    }

    public boolean dismount() {
        WaterZone water = null;
        for (Zone zone : ZoneManager.getInstance().getZones(getX(), getY(), getZ() - 300)) {
            if (zone instanceof WaterZone) {
                water = (WaterZone) zone;
            }
        }
        if (water == null) {
            if (!isInWater() && (getZ() > 10000)) {
                sendPacket(SystemMessageId.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION);
                sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
            if ((GeoEngine.getInstance().getHeight(getX(), getY(), getZ()) + 300) < getZ()) {
                sendPacket(SystemMessageId.YOU_CANNOT_DISMOUNT_FROM_THIS_ELEVATION);
                sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
        } else {
            ThreadPool.schedule(() ->
            {
                if (isInWater()) {
                    broadcastUserInfo();
                }
            }, 1500);
        }

        final boolean wasFlying = isFlying();
        sendPacket(new SetupGauge(3, 0, 0));
        final int petId = _mountNpcId;
        setMount(0, 0);
        stopFeed();
        clearPetData();
        if (wasFlying) {
            removeSkill(CommonSkill.WYVERN_BREATH.getSkill());
        }
        broadcastPacket(new Ride(this));
        setMountObjectID(0);
        storePetFood(petId);
        // Notify self and others about speed change
        broadcastUserInfo();
        return true;
    }

    public long getUptime() {
        return System.currentTimeMillis() - _uptime;
    }

    public void setUptime(long time) {
        _uptime = time;
    }

    /**
     * Return True if the Player is invulnerable.
     */
    @Override
    public boolean isInvul() {
        return super.isInvul() || isTeleportProtected();
    }

    /**
     * Return True if the Player has a Party in progress.
     */
    @Override
    public boolean isInParty() {
        return _party != null;
    }

    /**
     * Set the _party object of the Player AND join it.
     *
     * @param party
     */
    public void joinParty(Party party) {
        if (party != null) {
            // First set the party otherwise this wouldn't be considered
            // as in a party into the Creature.updateEffectIcons() call.
            _party = party;
            party.addPartyMember(this);
        }
    }

    /**
     * Manage the Leave Party task of the Player.
     */
    public void leaveParty() {
        if (isInParty()) {
            _party.removePartyMember(this, Party.MessageType.DISCONNECTED);
            _party = null;
        }
    }

    /**
     * Return the _party object of the Player.
     */
    @Override
    public Party getParty() {
        return _party;
    }

    /**
     * Set the _party object of the Player (without joining it).
     *
     * @param party
     */
    public void setParty(Party party) {
        _party = party;
    }

    public boolean isInCommandChannel() {
        return isInParty() && _party.isInCommandChannel();
    }

    public CommandChannel getCommandChannel() {
        return (isInCommandChannel()) ? _party.getCommandChannel() : null;
    }

    /**
     * Return True if the Player is a GM.
     */
    @Override
    public boolean isGM() {
        return accessLevel.isGm();
    }


    public void setAccountAccesslevel(int level) {
        AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(getAccountName(), level, 0));
    }

    /**
     * @return the _accessLevel of the Player.
     */
    @Override
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Update Stats of the Player client side by sending Server->Client packet UserInfo/StatusUpdate to this Player and CharInfo/StatusUpdate to all Player in its _KnownPlayers (broadcast).
     *
     * @param broadcastType
     */
    public void updateAndBroadcastStatus(int broadcastType) {
        refreshOverloaded(true);
        // Send a Server->Client packet UserInfo to this Player and CharInfo to all Player in its _KnownPlayers (broadcast)
        if (broadcastType == 1) {
            sendPacket(new UserInfo(this));
        }
        if (broadcastType == 2) {
            broadcastUserInfo();
        }
    }

    /**
     * Send a Server->Client StatusUpdate packet with Karma to the Player and all Player to inform (broadcast).
     */
    public void broadcastReputation() {
        broadcastUserInfo(UserInfoType.SOCIAL);

        World.getInstance().forEachVisibleObject(this, Player.class, player ->
        {
            if (!isVisibleFor(player)) {
                return;
            }

            final int relation = getRelation(player);
            final Integer oldrelation = getKnownRelations().get(player.getObjectId());
            if ((oldrelation == null) || (oldrelation != relation)) {
                final RelationChanged rc = new RelationChanged();
                rc.addRelation(this, relation, !isInsideZone(ZoneType.PEACE));
                if (hasSummon()) {
                    if (pet != null) {
                        rc.addRelation(pet, relation, !isInsideZone(ZoneType.PEACE));
                    }
                    if (hasServitors()) {
                        getServitors().values().forEach(s -> rc.addRelation(s, relation, !isInsideZone(ZoneType.PEACE)));
                    }
                }
                player.sendPacket(rc);
                getKnownRelations().put(player.getObjectId(), relation);
            }
        });
    }

    /**
     * Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout).
     *
     * @param isOnline
     * @param updateInDb
     */
    public void setOnlineStatus(boolean isOnline, boolean updateInDb) {
        if (_isOnline != isOnline) {
            _isOnline = isOnline;
        }

        // Update the characters table of the database with online status and lastAccess (called when login and logout)
        if (updateInDb) {
            updateOnlineStatus();
        }
    }

    /**
     * Update the characters table of the database with online status and lastAccess of this Player (called when login and logout).
     */
    public void updateOnlineStatus() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE charId=?")) {
            statement.setInt(1, isOnlineInt());
            statement.setLong(2, System.currentTimeMillis());
            statement.setInt(3, getObjectId());
            statement.execute();
        } catch (Exception e) {
            LOGGER.error("Failed updating character online status.", e);
        }
    }

    private boolean createDb() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_CHARACTER)) {
            statement.setString(1, data.getAccountName());
            statement.setInt(2, getObjectId());
            statement.setString(3, getName());
            statement.setInt(4, getLevel());
            statement.setInt(5, getMaxHp());
            statement.setDouble(6, getCurrentHp());
            statement.setInt(7, getMaxCp());
            statement.setDouble(8, getCurrentCp());
            statement.setInt(9, getMaxMp());
            statement.setDouble(10, getCurrentMp());
            statement.setInt(11, appearance.getFace());
            statement.setInt(12, appearance.getHairStyle());
            statement.setInt(13, appearance.getHairColor());
            statement.setInt(14, appearance.isFemale() ? 1 : 0);
            statement.setLong(15, getExp());
            statement.setLong(16, getSp());
            statement.setInt(17, getReputation());
            statement.setInt(18, _fame);
            statement.setInt(19, getRaidbossPoints());
            statement.setInt(20, _pvpKills);
            statement.setInt(21, _pkKills);
            statement.setInt(22, clanId);
            statement.setInt(23, getRace().ordinal());
            statement.setInt(24, data.getClassId());
            statement.setInt(25, hasDwarvenCraft() ? 1 : 0);
            statement.setString(26, getTitle());
            statement.setInt(27, appearance.getTitleColor());
            statement.setInt(28, isOnlineInt());
            statement.setInt(29, _clanPrivileges.getBitmask());
            statement.setBoolean(30, wantsPeace());
            statement.setInt(31, data.getBaseClass());
            statement.setInt(32, isNoble() ? 1 : 0);
            statement.setLong(33, 0);
            statement.setInt(34, PlayerStats.MIN_VITALITY_POINTS);
            statement.setObject(35, data.getCreateDate());
            statement.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("Could not insert char data: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Restores:
     * <ul>
     * <li>Skills</li>
     * <li>Macros</li>
     * <li>Henna</li>
     * <li>Teleport Bookmark</li>
     * <li>Recipe Book</li>
     * <li>Recipe Shop List (If configuration enabled)</li>
     * <li>Premium Item List</li>
     * <li>Pet Inventory Items</li>
     * </ul>
     */
    private void restoreCharData() {
        // Retrieve from the database all skills of this Player and add them to _skills.
        restoreSkills();

        // Retrieve from the database all macroses of this Player and add them to _macros.
        macros.restoreMe();

        // Retrieve from the database all henna of this Player and add them to _henna.
        restoreHenna();

        // Retrieve from the database all teleport bookmark of this Player and add them to _tpbookmark.
        restoreTeleportBookmark();

        // Retrieve from the database the recipe book of this Player.
        restoreRecipeBook(true);

        // Restore Recipe Shop list.
        if (Config.STORE_RECIPE_SHOPLIST) {
            restoreRecipeShopList();
        }

        // Load Premium Item List.
        loadPremiumItemList();

        // Restore items in pet inventory.
        restorePetInventoryItems();
    }

    /**
     * Restores:
     * <ul>
     * <li>Short-cuts</li>
     * </ul>
     */
    private void restoreShortCuts() {
        // Retrieve from the database all shortCuts of this Player and add them to _shortCuts.
        shortcuts.restoreMe();
    }

    /**
     * Restore recipe book data for this Player.
     *
     * @param loadCommon
     */
    private void restoreRecipeBook(boolean loadCommon) {
        final String sql = loadCommon ? "SELECT id, type, classIndex FROM character_recipebook WHERE charId=?" : "SELECT id FROM character_recipebook WHERE charId=? AND classIndex=? AND type = 1";
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, getObjectId());
            if (!loadCommon) {
                statement.setInt(2, _classIndex);
            }

            try (ResultSet rset = statement.executeQuery()) {
                _dwarvenRecipeBook.clear();

                RecipeList recipe;
                final RecipeData rd = RecipeData.getInstance();
                while (rset.next()) {
                    recipe = rd.getRecipeList(rset.getInt("id"));
                    if (loadCommon) {
                        if (rset.getInt(2) == 1) {
                            if (rset.getInt(3) == _classIndex) {
                                registerDwarvenRecipeList(recipe, false);
                            }
                        } else {
                            registerCommonRecipeList(recipe, false);
                        }
                    } else {
                        registerDwarvenRecipeList(recipe, false);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Could not restore recipe book data:" + e.getMessage(), e);
        }
    }

    public Map<Integer, PremiumItem> getPremiumItemList() {
        return _premiumItems;
    }

    private void loadPremiumItemList() {
        final String sql = "SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?";
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, getObjectId());
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    final int itemNum = rset.getInt("itemNum");
                    final int itemId = rset.getInt("itemId");
                    final long itemCount = rset.getLong("itemCount");
                    final String itemSender = rset.getString("itemSender");
                    _premiumItems.put(itemNum, new PremiumItem(itemId, itemCount, itemSender));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Could not restore premium items: " + e.getMessage(), e);
        }
    }

    public void updatePremiumItem(int itemNum, long newcount) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=? ")) {
            statement.setLong(1, newcount);
            statement.setInt(2, getObjectId());
            statement.setInt(3, itemNum);
            statement.execute();
        } catch (Exception e) {
            LOGGER.error("Could not update premium items: " + e.getMessage(), e);
        }
    }

    public void deletePremiumItem(int itemNum) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_premium_items WHERE charId=? AND itemNum=? ")) {
            statement.setInt(1, getObjectId());
            statement.setInt(2, itemNum);
            statement.execute();
        } catch (Exception e) {
            LOGGER.error("Could not delete premium item: " + e);
        }
    }

    /**
     * Update Player stats in the characters table of the database.
     *
     * @param storeActiveEffects
     */
    public synchronized void store(boolean storeActiveEffects) {
        storeCharBase();
        storeCharSub();
        storeEffect(storeActiveEffects);
        storeItemReuseDelay();
        if (Config.STORE_RECIPE_SHOPLIST) {
            storeRecipeShopList();
        }

        final PlayerVariables vars = getScript(PlayerVariables.class);
        if (vars != null) {
            vars.storeMe();
        }

        final AccountVariables aVars = getScript(AccountVariables.class);
        if (aVars != null) {
            aVars.storeMe();
        }

        if(nonNull(spirits)) {
            for (ElementalSpirit spirit : spirits) {
                if(nonNull(spirit)) {
                    spirit.save();
                }
            }

            if(nonNull(activeElementalSpiritType)) {
                getDAO(ElementalSpiritDAO.class).updateActiveSpirit(getObjectId(), activeElementalSpiritType.getId());
            }
        }

        shortcuts.storeMe();
        getDAO(PlayerVariablesDAO.class).save(variables);

        final var playerDAO = getDAO(PlayerDAO.class);
        playerDAO.save(statsData);

        if(!costumes.isEmpty()) {
            playerDAO.save(costumes.values());
        }

        if(CostumeCollectionData.DEFAULT.equals(activeCostumesCollection)) {
            playerDAO.deleteCostumeCollection(objectId);
        } else {
            playerDAO.save(activeCostumesCollection);
        }

        playerDAO.removeTeleportFavorites(objectId);
        if(Util.isNotEmpty(teleportFavorites)) {
            playerDAO.saveTeleportFavorites(objectId, teleportFavorites);
        }

        storeRecommendations();
        if (Config.UPDATE_ITEMS_ON_CHAR_STORE) {
            inventory.updateDatabase();
            getWarehouse().updateDatabase();
        }
    }

    @Override
    public void storeMe() {
        store(true);
    }

    private void storeCharBase() {
        // Get the exp, level, and sp of base class to store in base table
        final long exp = getStats().getBaseExp();
        final int level = getStats().getBaseLevel();
        final long sp = getStats().getBaseSp();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_CHARACTER)) {
            statement.setInt(1, level);
            statement.setInt(2, getMaxHp());
            statement.setDouble(3, getCurrentHp());
            statement.setInt(4, getMaxCp());
            statement.setDouble(5, getCurrentCp());
            statement.setInt(6, getMaxMp());
            statement.setDouble(7, getCurrentMp());
            statement.setInt(8, appearance.getFace());
            statement.setInt(9, appearance.getHairStyle());
            statement.setInt(10, appearance.getHairColor());
            statement.setInt(11, appearance.isFemale() ? 1 : 0);
            statement.setInt(12, getHeading());
            statement.setInt(13, _lastLoc != null ? _lastLoc.getX() : getX());
            statement.setInt(14, _lastLoc != null ? _lastLoc.getY() : getY());
            statement.setInt(15, _lastLoc != null ? _lastLoc.getZ() : getZ());
            statement.setLong(16, exp);
            statement.setLong(17, data.getExpBeforeDeath());
            statement.setLong(18, sp);
            statement.setInt(19, getReputation());
            statement.setInt(20, _fame);
            statement.setInt(21, getRaidbossPoints());
            statement.setInt(22, _pvpKills);
            statement.setInt(23, _pkKills);
            statement.setInt(24, clanId);
            statement.setInt(25, getRace().ordinal());
            statement.setInt(26, getClassId().getId());
            statement.setString(27, getTitle());
            statement.setInt(28, appearance.getTitleColor());
            statement.setInt(29, isOnlineInt());
            statement.setInt(30, _clanPrivileges.getBitmask());
            statement.setBoolean(31, wantsPeace());
            statement.setInt(32, data.getBaseClass());

            long totalOnlineTime = _onlineTime;
            if (_onlineBeginTime > 0) {
                totalOnlineTime += (System.currentTimeMillis() - _onlineBeginTime) / 1000;
            }

            statement.setLong(33, totalOnlineTime);
            statement.setInt(34, isNoble() ? 1 : 0);
            statement.setInt(35, getPowerGrade());
            statement.setInt(36, getPledgeType());
            statement.setInt(37, getLvlJoinedAcademy());
            statement.setLong(38, getApprentice());
            statement.setLong(39, getSponsor());
            statement.setLong(40, getClanJoinExpiryTime());
            statement.setLong(41, getClanJoinExpiryTime());
            statement.setString(42, getName());
            statement.setInt(43, _bookmarkslot);
            statement.setInt(44, getStats().getBaseVitalityPoints());
            statement.setString(45, _lang);

            statement.setInt(46, getPcCafePoints());
            statement.setInt(47, getObjectId());

            statement.execute();
        } catch (Exception e) {
            LOGGER.warn("Could not store char base data: " + this + " - " + e.getMessage(), e);
        }
    }

    private void storeCharSub() {
        if (getTotalSubClasses() <= 0) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_CHAR_SUBCLASS)) {
            for (SubClass subClass : getSubClasses().values()) {
                statement.setLong(1, subClass.getExp());
                statement.setLong(2, subClass.getSp());
                statement.setInt(3, subClass.getLevel());
                statement.setInt(4, subClass.getVitalityPoints());
                statement.setInt(5, subClass.getClassId());
                statement.setBoolean(6, subClass.isDualClass());
                statement.setInt(7, getObjectId());
                statement.setInt(8, subClass.getClassIndex());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception e) {
            LOGGER.warn("Could not store sub class data for " + getName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void storeEffect(boolean storeEffects) {
        if (!Config.STORE_SKILL_COOLTIME) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {

            // Delete all current stored effects for char to avoid dupe
            try (PreparedStatement delete = con.prepareStatement(DELETE_SKILL_SAVE)) {
                delete.setInt(1, getObjectId());
                delete.setInt(2, _classIndex);
                delete.execute();
            }

            int buff_index = 0;
            final List<Long> storedSkills = new ArrayList<>();
            final long currentTime = System.currentTimeMillis();

            try(PreparedStatement statement = con.prepareStatement(ADD_SKILL_SAVE)) {
                // Store all effect data along with calulated remaining
                // reuse delays for matching skills. 'restore_type'= 0.
                if (storeEffects) {
                    for (BuffInfo info : getEffectList().getEffects()) {
                        if (info == null) {
                            continue;
                        }

                        final Skill skill = info.getSkill();

                        // Do not store those effects.
                        if (skill.isDeleteAbnormalOnLeave()) {
                            continue;
                        }

                        // Do not save heals.
                        if (skill.getAbnormalType() == AbnormalType.LIFE_FORCE_OTHERS) {
                            continue;
                        }

                        // Toggles are skipped, unless they are necessary to be always on.
                        if (skill.isToggle()) {
                            continue;
                        }

                        // Dances and songs are not kept in retail.
                        if (skill.isDance() && !Config.ALT_STORE_DANCES) {
                            continue;
                        }

                        if (storedSkills.contains(skill.getReuseHashCode())) {
                            continue;
                        }

                        storedSkills.add(skill.getReuseHashCode());

                        statement.setInt(1, getObjectId());
                        statement.setInt(2, skill.getId());
                        statement.setInt(3, skill.getLevel());
                        statement.setInt(4, skill.getSubLevel());
                        statement.setInt(5, info.getTime());

                        final TimeStamp t = getSkillReuseTimeStamp(skill.getReuseHashCode());
                        statement.setLong(6, (t != null) && (currentTime < t.getStamp()) ? t.getReuse() : 0);
                        statement.setDouble(7, (t != null) && (currentTime < t.getStamp()) ? t.getStamp() : 0);

                        statement.setInt(8, 0); // Store type 0, active buffs/debuffs.
                        statement.setInt(9, _classIndex);
                        statement.setInt(10, ++buff_index);
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }

                // Skills under reuse.
                for (Entry<Long, TimeStamp> ts : getSkillReuseTimeStamps().entrySet()) {
                    final long hash = ts.getKey();
                    if (storedSkills.contains(hash)) {
                        continue;
                    }

                    final TimeStamp t = ts.getValue();
                    if ((t != null) && (currentTime < t.getStamp())) {
                        storedSkills.add(hash);

                        statement.setInt(1, getObjectId());
                        statement.setInt(2, t.getSkillId());
                        statement.setInt(3, t.getSkillLvl());
                        statement.setInt(4, t.getSkillSubLvl());
                        statement.setInt(5, -1);
                        statement.setLong(6, t.getReuse());
                        statement.setDouble(7, t.getStamp());
                        statement.setInt(8, 1); // Restore type 1, skill reuse.
                        statement.setInt(9, _classIndex);
                        statement.setInt(10, ++buff_index);
                        statement.addBatch();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not store char effect data: ", e);
        }
    }

    private void storeItemReuseDelay() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps1 = con.prepareStatement(DELETE_ITEM_REUSE_SAVE);
             PreparedStatement ps2 = con.prepareStatement(ADD_ITEM_REUSE_SAVE)) {
            ps1.setInt(1, getObjectId());
            ps1.execute();

            final long currentTime = System.currentTimeMillis();
            for (TimeStamp ts : getItemReuseTimeStamps().values()) {
                if ((ts != null) && (currentTime < ts.getStamp())) {
                    ps2.setInt(1, getObjectId());
                    ps2.setInt(2, ts.getItemId());
                    ps2.setInt(3, ts.getItemObjectId());
                    ps2.setLong(4, ts.getReuse());
                    ps2.setDouble(5, ts.getStamp());
                    ps2.addBatch();
                }
            }
            ps2.executeBatch();
        } catch (Exception e) {
            LOGGER.warn("Could not store char item reuse data: ", e);
        }
    }

    /**
     * @return True if the Player is on line.
     */
    public boolean isOnline() {
        return _isOnline && getClient() != null;
    }

    public int isOnlineInt() {
        if (_isOnline && (_client != null)) {
            return _client.isDetached() ? 2 : 1;
        }
        return 0;
    }

    /**
     * Verifies if the player is in offline mode.<br>
     * The offline mode may happen for different reasons:<br>
     * Abnormally: Player gets abruptly disconnected from server.<br>
     * Normally: The player gets into offline shop mode, only available by enabling the offline shop mod.
     *
     * @return {@code true} if the player is in offline mode, {@code false} otherwise
     */
    public boolean isInOfflineMode() {
        return (_client == null) || _client.isDetached();
    }

    @Override
    public Skill addSkill(Skill newSkill) {
        addCustomSkill(newSkill);
        if(nonNull(newSkill)) {
            sendSkillList();
        }
        return super.addSkill(newSkill);
    }

    /**
     * Add a skill to the Player _skills and its Func objects to the calculator set of the Player and save update in the character_skills table of the database. <B><U> Concept</U> :</B> All skills own by a Player are identified in <B>_skills</B> <B><U> Actions</U> :</B>
     * <li>Replace oldSkill by newSkill or Add the newSkill</li>
     * <li>If an old skill has been replaced, remove all its Func objects of Creature calculator set</li>
     * <li>Add Func objects of newSkill to the calculator set of the Creature</li>
     *
     * @param newSkill The L2Skill to add to the Creature
     * @param store
     * @return The L2Skill replaced or null if just added a new L2Skill
     */
    public Skill addSkill(Skill newSkill, boolean store) {
        // Add a skill to the Player _skills and its Func objects to the calculator set of the Player
        final Skill oldSkill = addSkill(newSkill);
        // Add or update a Player skill in the character_skills table of the database
        if (store) {
            storeSkill(newSkill, oldSkill, -1);
        }
        return oldSkill;
    }

    @Override
    public Skill removeSkill(Skill skill, boolean store) {
        removeCustomSkill(skill);
        if(nonNull(skill)) {
            sendSkillList();
        }
        return store ? removeSkill(skill) : super.removeSkill(skill, true);
    }

    public Skill removeSkill(Skill skill, boolean store, boolean cancelEffect) {
        removeCustomSkill(skill);
        if(nonNull(skill)) {
            sendSkillList();
        }
        return store ? removeSkill(skill) : super.removeSkill(skill, cancelEffect);
    }

    /**
     * Remove a skill from the Creature and its Func objects from calculator set of the Creature and save update in the character_skills table of the database. <B><U> Concept</U> :</B> All skills own by a Creature are identified in <B>_skills</B> <B><U> Actions</U> :</B>
     * <li>Remove the skill from the Creature _skills</li>
     * <li>Remove all its Func objects from the Creature calculator set</li> <B><U> Overridden in </U> :</B>
     * <li>Player : Save update in the character_skills table of the database</li>
     *
     * @param skill The L2Skill to remove from the Creature
     * @return The L2Skill removed
     */
    public Skill removeSkill(Skill skill) {
        removeCustomSkill(skill);

        // Remove a skill from the Creature and its stats
        final Skill oldSkill = super.removeSkill(skill, true);
        if (oldSkill != null) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(DELETE_SKILL_FROM_CHAR)) {
                // Remove or update a Player skill from the character_skills table of the database
                statement.setInt(1, oldSkill.getId());
                statement.setInt(2, getObjectId());
                statement.setInt(3, _classIndex);
                statement.execute();
            } catch (Exception e) {
                LOGGER.warn("Error could not delete skill: " + e.getMessage(), e);
            }
        }

        if (getTransformationId() > 0) {
            return oldSkill;
        }

        if (nonNull(skill) &&  !(skill.getId() >= 3080 && skill.getId() <= 3259)) { // exclude item skills ?! it's all ?
            deleteShortcuts(s -> s.getShortcutId() == skill.getId() && s.getType() == ShortcutType.SKILL);
        }
        return oldSkill;
    }

    /**
     * Add or update a Player skill in the character_skills table of the database.<br>
     * If newClassIndex > -1, the skill will be stored with that class index, not the current one.
     *
     * @param newSkill
     * @param oldSkill
     * @param newClassIndex
     */
    private void storeSkill(Skill newSkill, Skill oldSkill, int newClassIndex) {
        final int classIndex = (newClassIndex > -1) ? newClassIndex : _classIndex;
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            if ((oldSkill != null) && (newSkill != null)) {
                try (PreparedStatement ps = con.prepareStatement(UPDATE_CHARACTER_SKILL_LEVEL)) {
                    ps.setInt(1, newSkill.getLevel());
                    ps.setInt(2, newSkill.getSubLevel());
                    ps.setInt(3, oldSkill.getId());
                    ps.setInt(4, getObjectId());
                    ps.setInt(5, classIndex);
                    ps.execute();
                }
            } else if (newSkill != null) {
                try (PreparedStatement ps = con.prepareStatement(ADD_NEW_SKILLS)) {
                    ps.setInt(1, getObjectId());
                    ps.setInt(2, newSkill.getId());
                    ps.setInt(3, newSkill.getLevel());
                    ps.setInt(4, newSkill.getSubLevel());
                    ps.setInt(5, classIndex);
                    ps.execute();
                }
            }
            // else
            // {
            // LOGGER.warn("Could not store new skill, it's null!");
            // }
        } catch (Exception e) {
            LOGGER.warn("Error could not store char skills: " + e.getMessage(), e);
        }
    }

    /**
     * Adds or updates player's skills in the database.
     *
     * @param newSkills     the list of skills to store
     * @param newClassIndex if newClassIndex > -1, the skills will be stored for that class index, not the current one
     */
    private void storeSkills(List<Skill> newSkills, int newClassIndex) {
        if (newSkills.isEmpty()) {
            return;
        }

        final int classIndex = (newClassIndex > -1) ? newClassIndex : _classIndex;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(ADD_NEW_SKILLS)) {
            for (Skill addSkill : newSkills) {
                ps.setInt(1, getObjectId());
                ps.setInt(2, addSkill.getId());
                ps.setInt(3, addSkill.getLevel());
                ps.setInt(4, addSkill.getSubLevel());
                ps.setInt(5, classIndex);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            LOGGER.warn("Error could not store char skills: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve from the database all skills of this Player and add them to _skills.
     */
    private void restoreSkills() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR)) {
            // Retrieve all skills of this Player from the database
            statement.setInt(1, getObjectId());
            statement.setInt(2, _classIndex);
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    final int id = rset.getInt("skill_id");
                    final int level = rset.getInt("skill_level");

                    final Skill skill = SkillEngine.getInstance().getSkill(id, level);

                    if (skill == null) {
                        LOGGER.warn("Skipped null skill id: {} level: {} while restoring player skills for player: {}", id, level, this);
                        continue;
                    }

                    addSkill(skill);

                    if (Config.SKILL_CHECK_ENABLE && (!canOverrideCond(PcCondOverride.SKILL_CONDITIONS) || Config.SKILL_CHECK_GM)) {
                        if (!SkillTreesData.getInstance().isSkillAllowed(this, skill)) {
                            GameUtils.handleIllegalPlayerAction(this, "Player " + getName() + " has invalid skill " + skill.getName() + " (" + skill.getId() + "/" + skill.getLevel() + "), class:" + ClassListData.getInstance().getClass(getClassId()).getClassName(), IllegalActionPunishmentType.BROADCAST);
                            if (Config.SKILL_CHECK_REMOVE) {
                                removeSkill(skill);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not restore character " + this + " skills: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve from the database all skill effects of this Player and add them to the player.
     */
    @Override
    public void restoreEffects() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_SKILL_SAVE)) {
            statement.setInt(1, getObjectId());
            statement.setInt(2, _classIndex);
            try (ResultSet rset = statement.executeQuery()) {
                final long currentTime = System.currentTimeMillis();
                while (rset.next()) {
                    final int remainingTime = rset.getInt("remaining_time");
                    final long reuseDelay = rset.getLong("reuse_delay");
                    final long systime = rset.getLong("systime");
                    final int restoreType = rset.getInt("restore_type");

                    final Skill skill = SkillEngine.getInstance().getSkill(rset.getInt("skill_id"), rset.getInt("skill_level"));
                    if (skill == null) {
                        continue;
                    }

                    final long time = systime - currentTime;
                    if (time > 10) {
                        disableSkill(skill, time);
                        addTimeStamp(skill, reuseDelay, systime);
                    }

                    // Restore Type 1 The remaning skills lost effect upon logout but were still under a high reuse delay.
                    if (restoreType > 0) {
                        continue;
                    }

                    // Restore Type 0 These skill were still in effect on the character upon logout.
                    // Some of which were self casted and might still have had a long reuse delay which also is restored.
                    skill.applyEffects(this, this, false, remainingTime);
                }
            }
            // Remove previously restored skills
            try (PreparedStatement delete = con.prepareStatement(DELETE_SKILL_SAVE)) {
                delete.setInt(1, getObjectId());
                delete.setInt(2, _classIndex);
                delete.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.warn("Could not restore " + this + " active effect data: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve from the database all Item Reuse Time of this Player and add them to the player.
     */
    private void restoreItemReuse() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_ITEM_REUSE_SAVE);
             PreparedStatement delete = con.prepareStatement(DELETE_ITEM_REUSE_SAVE)) {
            statement.setInt(1, getObjectId());
            try (ResultSet rset = statement.executeQuery()) {
                int itemId;
                long reuseDelay;
                long systime;
                boolean isInInventory;
                long remainingTime;
                final long currentTime = System.currentTimeMillis();

                while (rset.next()) {
                    itemId = rset.getInt("itemId");
                    reuseDelay = rset.getLong("reuseDelay");
                    systime = rset.getLong("systime");
                    isInInventory = true;

                    // Using item Id
                    Item item = inventory.getItemByItemId(itemId);
                    if (item == null) {
                        item = getWarehouse().getItemByItemId(itemId);
                        isInInventory = false;
                    }

                    if ((item != null) && (item.getId() == itemId) && (item.getReuseDelay() > 0)) {
                        remainingTime = systime - currentTime;
                        // Hardcoded to 10 seconds.
                        if (remainingTime > 10) {
                            addTimeStampItem(item, reuseDelay, systime);

                            if (isInInventory && item.isEtcItem()) {
                                final int group = item.getSharedReuseGroup();
                                if (group > 0) {
                                    sendPacket(new ExUseSharedGroupItem(itemId, group, (int) remainingTime, (int) reuseDelay));
                                }
                            }
                        }
                    }
                }
            }

            // Delete item reuse.
            delete.setInt(1, getObjectId());
            delete.executeUpdate();
        } catch (Exception e) {
            LOGGER.warn("Could not restore " + this + " Item Reuse data: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve from the database all Henna of this Player, add them to _henna and calculate stats of the Player.
     */
    private void restoreHenna() {
        for (int i = 1; i < 4; i++) {
            _henna[i - 1] = null;
        }

        for (Entry<Integer, ScheduledFuture<?>> entry : _hennaRemoveSchedules.entrySet())
        {
            final ScheduledFuture<?> task = entry.getValue();
            if ((task != null) && !task.isCancelled() && !task.isDone())
            {
                task.cancel(true);
            }
            _hennaRemoveSchedules.remove(entry.getKey());
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_HENNAS)) {
            statement.setInt(1, getObjectId());
            statement.setInt(2, _classIndex);
            try (ResultSet rset = statement.executeQuery()) {
                int slot;
                int symbolId;
                final long currentTime = System.currentTimeMillis();
                while (rset.next()) {
                    slot = rset.getInt("slot");
                    if ((slot < 1) || (slot > 3)) {
                        continue;
                    }

                    symbolId = rset.getInt("symbol_id");
                    if (symbolId == 0) {
                        continue;
                    }

                    final Henna henna = HennaData.getInstance().getHenna(symbolId);

                    // Task for henna duration
                    if (henna.getDuration() > 0) {
                        final long remainingTime = getVariables().getLong("HennaDuration" + slot, currentTime) - currentTime;
                        if (remainingTime < 0) {
                            removeHenna(slot);
                            continue;
                        }
                        _hennaRemoveSchedules.put(slot, ThreadPool.schedule(new HennaDurationTask(this, slot), currentTime + remainingTime));
                    }

                    _henna[slot - 1] = henna;

                    // Reward henna skills
                    for (Skill skill : henna.getSkills()) {
                        addSkill(skill, false);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed restoing character " + this + " hennas.", e);
        }

        // Calculate henna modifiers of this player.
        recalcHennaStats();
    }

    /**
     * @return the number of Henna empty slot of the Player.
     */
    public int getHennaEmptySlots() {
        int totalSlots = 0;
        if (getClassId().level() == 1) {
            totalSlots = 2;
        } else if (getClassId().level() > 1) {
            totalSlots = 3;
        }

        for (int i = 0; i < 3; i++) {
            if (_henna[i] != null) {
                totalSlots--;
            }
        }

        if (totalSlots <= 0) {
            return 0;
        }

        return totalSlots;
    }

    /**
     * Remove a Henna of the Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this Player.
     *
     * @param slot
     * @return
     */
    public boolean removeHenna(int slot) {
        if ((slot < 1) || (slot > 3)) {
            return false;
        }

        final Henna henna = _henna[slot - 1];
        if (henna == null) {
            return false;
        }

        _henna[slot - 1] = null;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_CHAR_HENNA)) {
            statement.setInt(1, getObjectId());
            statement.setInt(2, slot);
            statement.setInt(3, _classIndex);
            statement.execute();
        } catch (Exception e) {
            LOGGER.error("Failed removing character henna.", e);
        }

        // Calculate Henna modifiers of this Player
        recalcHennaStats();

        // Send Server->Client HennaInfo packet to this Player
        sendPacket(new HennaInfo(this));

        // Send Server->Client UserInfo packet to this Player
        final UserInfo ui = new UserInfo(this, false);
        ui.addComponentType(UserInfoType.BASE_STATS, UserInfoType.MAX_HPCPMP, UserInfoType.STATS, UserInfoType.SPEED);
        sendPacket(ui);

        final long remainingTime = getVariables().getLong("HennaDuration" + slot, 0) - System.currentTimeMillis();
        if ((henna.getDuration() < 0) || (remainingTime > 0)) {
            // Add the recovered dyes to the player's inventory and notify them.
            if (henna.getCancelFee() > 0) {
                reduceAdena("Henna", henna.getCancelFee(), this, false);
            }
            if (henna.getCancelCount() > 0) {
                inventory.addItem("Henna", henna.getDyeItemId(), henna.getCancelCount(), this, null);
                final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                sm.addItemName(henna.getDyeItemId());
                sm.addLong(henna.getCancelCount());
                sendPacket(sm);
            }
        }
        sendPacket(SystemMessageId.THE_SYMBOL_HAS_BEEN_DELETED);

        // Remove henna duration task
        if (henna.getDuration() > 0) {
            getVariables().remove("HennaDuration" + slot);
            if (_hennaRemoveSchedules.get(slot) != null) {
                _hennaRemoveSchedules.get(slot).cancel(false);
                _hennaRemoveSchedules.remove(slot);
            }
        }

        // Remove henna skills
        for (Skill skill : henna.getSkills()) {
            removeSkill(skill, false);
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerHennaRemove(this, henna), this);
        return true;
    }

    /**
     * Add a Henna to the Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this Player.
     *
     * @param henna the henna to add to the player.
     * @return {@code true} if the henna is added to the player, {@code false} otherwise.
     */
    public boolean addHenna(Henna henna) {
        for (int i = 1; i < 4; i++) {
            if (_henna[i - 1] == null) {
                _henna[i - 1] = henna;

                // Calculate Henna modifiers of this Player
                recalcHennaStats();

                try (Connection con = DatabaseFactory.getInstance().getConnection();
                     PreparedStatement statement = con.prepareStatement(ADD_CHAR_HENNA)) {
                    statement.setInt(1, getObjectId());
                    statement.setInt(2, henna.getDyeId());
                    statement.setInt(3, i);
                    statement.setInt(4, _classIndex);
                    statement.execute();
                } catch (Exception e) {
                    LOGGER.error("Failed saving character henna.", e);
                }

                // Task for henna duration
                if (henna.getDuration() > 0) {
                    getVariables().set("HennaDuration" + i, System.currentTimeMillis() + (henna.getDuration() * 60000));
                    _hennaRemoveSchedules.put(i, ThreadPool.schedule(new HennaDurationTask(this, i), System.currentTimeMillis() + (henna.getDuration() * 60000)));
                }

                // Reward henna skills
                for (Skill skill : henna.getSkills()) {
                    addSkill(skill, false);
                }

                // Send Server->Client HennaInfo packet to this Player
                sendPacket(new HennaInfo(this));

                // Send Server->Client UserInfo packet to this Player
                final UserInfo ui = new UserInfo(this, false);
                ui.addComponentType(UserInfoType.BASE_STATS, UserInfoType.MAX_HPCPMP, UserInfoType.STATS, UserInfoType.SPEED);
                sendPacket(ui);

                // Notify to scripts
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerHennaAdd(this, henna), this);
                return true;
            }
        }
        return false;
    }

    /**
     * Calculate Henna modifiers of this Player.
     */
    private void recalcHennaStats() {
        _hennaBaseStats.clear();
        for (Henna henna : _henna) {
            if (henna == null) {
                continue;
            }

            for (Entry<BaseStats, Integer> entry : henna.getBaseStats().entrySet()) {
                _hennaBaseStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
    }

    /**
     * @param slot the character inventory henna slot.
     * @return the Henna of this Player corresponding to the selected slot.
     */
    public Henna getHenna(int slot) {
        if ((slot < 1) || (slot > 3)) {
            return null;
        }
        return _henna[slot - 1];
    }

    /**
     * @return {@code true} if player has at least 1 henna symbol, {@code false} otherwise.
     */
    public boolean hasHennas() {
        for (Henna henna : _henna) {
            if (henna != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the henna holder for this player.
     */
    public Henna[] getHennaList() {
        return _henna;
    }

    /**
     * @param stat
     * @return the henna bonus of specified base stat
     */
    public int getHennaValue(BaseStats stat) {
        return _hennaBaseStats.getOrDefault(stat, 0);
    }

    /**
     * @return map of all henna base stats bonus
     */
    public Map<BaseStats, Integer> getHennaBaseStats() {
        return _hennaBaseStats;
    }

    /**
     * Checks if the player has basic property resist towards mesmerizing debuffs.
     *
     * @return {@code true} if the player has resist towards mesmerizing debuffs, {@code false} otherwise
     */
    @Override
    public boolean hasBasicPropertyResist() {
        return false;
    }

    public boolean canLogout() {
        if (hasItemRequest() || hasRequest(CaptchaRequest.class)) {
            return false;
        }

        if (isSubClassLocked()) {
            LOGGER.warn("Player {} tried to restart/logout during class change.", getName());
            return false;
        }

        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(this) && !(isGM() && Config.GM_RESTART_FIGHTING)) {
            return false;
        }

        return !isBlockedFromExit();

    }

    /**
     * Return True if the Player is autoAttackable.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Check if the attacker isn't the Player Pet</li>
     * <li>Check if the attacker is Monster</li>
     * <li>If the attacker is a Player, check if it is not in the same party</li>
     * <li>Check if the Player has Karma</li>
     * <li>If the attacker is a Player, check if it is not in the same siege clan (Attacker, Defender)</li>
     * </ul>
     */
    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (isNull(attacker)) {
            return false;
        }

        // Check if the attacker isn't the Player Pet
        if ((attacker == this) || (attacker == pet) || attacker.hasServitor(attacker.getObjectId())) {
            return false;
        }

        // Friendly mobs doesnt attack players
        if (attacker instanceof FriendlyMob) {
            return false;
        }

        // Check if the attacker is a Monster
        if (GameUtils.isMonster(attacker)) {
            return true;
        }

        // is AutoAttackable if both players are in the same duel and the duel is still going on
        if (GameUtils.isPlayable(attacker) && (_duelState == Duel.DUELSTATE_DUELLING) && (getDuelId() == attacker.getActingPlayer().getDuelId())) {
            return true;
        }

        // Check if the attacker is not in the same party. NOTE: Party checks goes before oly checks in order to prevent patry member autoattack at oly.
        if (isInParty() && _party.getMembers().contains(attacker)) {
            return false;
        }

        // Check if the attacker is in olympia and olympia start
        if (GameUtils.isPlayer(attacker) && attacker.getActingPlayer().isInOlympiadMode()) {
            return _inOlympiadMode && _OlympiadStart && (((Player) attacker).getOlympiadGameId() == getOlympiadGameId());
        }

        if (_isOnCustomEvent && (getTeam() == attacker.getTeam())) {
            return false;
        }

        // CoC needs this check?
        if (isOnEvent()) {
            return true;
        }

        // Check if the attacker is a Playable
        if (GameUtils.isPlayable(attacker)) {
            if (isInsideZone(ZoneType.PEACE)) {
                return false;
            }

            // Get Player
            final Player attackerPlayer = attacker.getActingPlayer();
            final Clan clan = getClan();
            final Clan attackerClan = attackerPlayer.getClan();
            if (clan != null) {
                final Siege siege = SiegeManager.getInstance().getSiege(this);
                if (siege != null) {
                    // Check if a siege is in progress and if attacker and the Player aren't in the Defender clan
                    if (siege.checkIsDefender(attackerClan) && siege.checkIsDefender(clan)) {
                        return false;
                    }

                    // Check if a siege is in progress and if attacker and the Player aren't in the Attacker clan
                    if (siege.checkIsAttacker(attackerClan) && siege.checkIsAttacker(clan)) {
                        return false;
                    }
                }

                // Check if clan is at war
                if ((attackerClan != null) && (!wantsPeace()) && (!attackerPlayer.wantsPeace()) && !isAcademyMember()) {
                    final ClanWar war = attackerClan.getWarWith(getClanId());
                    if ((war != null) && (war.getState() == ClanWarState.MUTUAL)) {
                        return true;
                    }
                }
            }

            // Check if the Player is in an arena, but NOT siege zone. NOTE: This check comes before clan/ally checks, but after party checks.
            // This is done because in arenas, clan/ally members can autoattack if they arent in party.
            if ((isInsideZone(ZoneType.PVP) && attackerPlayer.isInsideZone(ZoneType.PVP)) && !(isInsideZone(ZoneType.SIEGE) && attackerPlayer.isInsideZone(ZoneType.SIEGE))) {
                return true;
            }

            // Check if the attacker is not in the same clan
            if ((clan != null) && clan.isMember(attacker.getObjectId())) {
                return false;
            }

            // Check if the attacker is not in the same ally
            if (GameUtils.isPlayer(attacker) && (getAllyId() != 0) && (getAllyId() == attackerPlayer.getAllyId())) {
                return false;
            }

            // Now check again if the Player is in pvp zone, but this time at siege PvP zone, applying clan/ally checks
            if (isInsideZone(ZoneType.PVP) && attackerPlayer.isInsideZone(ZoneType.PVP) && isInsideZone(ZoneType.SIEGE) && attackerPlayer.isInsideZone(ZoneType.SIEGE)) {
                return true;
            }

            if(getPvpFlag() > 0) {
                return true;
            }

        }

        if (attacker instanceof Defender) {
            if (_clan != null) {
                final Siege siege = SiegeManager.getInstance().getSiege(this);
                return ((siege != null) && siege.checkIsAttacker(_clan));
            }
        }

        if (attacker instanceof Guard) {
            return (getReputation() < 0); // Guards attack only PK players.
        }

        // Check if the Player has Karma
        if ((getReputation() < 0) || (_pvpFlag > 0)) {
            return true;
        }

        return false;
    }

    /**
     * Check if the active L2Skill can be casted.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Check if the skill isn't toggle and is offensive</li>
     * <li>Check if the target is in the skill cast range</li>
     * <li>Check if the skill is Spoil type and if the target isn't already spoiled</li>
     * <li>Check if the caster owns enought consummed Item, enough HP and MP to cast the skill</li>
     * <li>Check if the caster isn't sitting</li>
     * <li>Check if all skills are enabled and this skill is enabled</li>
     * <li>Check if the caster own the weapon needed</li>
     * <li>Check if the skill is active</li>
     * <li>Check if all casting conditions are completed</li>
     * <li>Notify the AI with AI_INTENTION_CAST and target</li>
     * </ul>
     *
     * @param skill    The L2Skill to use
     * @param forceUse used to force ATTACK on players
     * @param dontMove used to prevent movement, if not in range
     */
    @Override
    public boolean useMagic(Skill skill, Item item, boolean forceUse, boolean dontMove) {
        // Passive skills cannot be used.
        if (skill.isPassive()) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // If Alternate rule Karma punishment is set to true, forbid skill Return to player with Karma
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (getReputation() < 0) && skill.hasAnyEffectType(EffectType.TELEPORT)) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // players mounted on pets cannot use any toggle skills
        if (skill.isToggle() && isMounted()) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // ************************************* Check Player State *******************************************

        // Abnormal effects(ex : Stun, Sleep...) are checked in Creature useMagic()
        if (!skill.canCastWhileDisabled() && (isControlBlocked() || hasBlockActions())) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // Check if the player is dead
        if (isDead()) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // Check if fishing and trying to use non-fishing skills.
        if (isFishing() && !skill.hasAnyEffectType(EffectType.FISHING, EffectType.FISHING_START)) {
            sendPacket(SystemMessageId.ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME);
            return false;
        }

        if (_observerMode) {
            sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        if (isSkillDisabled(skill)) {
            final SystemMessage sm;
            if (hasSkillReuse(skill.getReuseHashCode())) {
                final int remainingTime = (int) (getSkillRemainingReuseTime(skill.getReuseHashCode()) / 1000);
                final int hours = remainingTime / 3600;
                final int minutes = (remainingTime % 3600) / 60;
                final int seconds = (remainingTime % 60);
                if (hours > 0) {
                    sm = getSystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    sm.addSkillName(skill);
                    sm.addInt(hours);
                    sm.addInt(minutes);
                } else if (minutes > 0) {
                    sm = getSystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    sm.addSkillName(skill);
                    sm.addInt(minutes);
                } else {
                    sm = getSystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    sm.addSkillName(skill);
                }

                sm.addInt(seconds);
            } else {
                sm = getSystemMessage(SystemMessageId.S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE);
                sm.addSkillName(skill);
            }

            sendPacket(sm);
            return false;
        }

        // Check if the caster is sitting
        if (_waitTypeSitting) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_ACTIONS_AND_SKILLS_WHILE_THE_CHARACTER_IS_SITTING);
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // Check if the skill type is toggle and disable it, unless the toggle is necessary to be on.
        if (skill.isToggle()) {
            if (isAffectedBySkill(skill.getId())) {
                stopSkillEffects(true, skill.getId());
                sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
        }

        // Check if the player uses "Fake Death" skill
        // Note: do not check this before TOGGLE reset
        if (isFakeDeath()) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // ************************************* Check Target *******************************************
        final Location worldPosition = _currentSkillWorldPosition;
        if ((skill.getTargetType() == TargetType.GROUND) && (worldPosition == null)) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // Create and set a WorldObject containing the target of the skill
        final WorldObject target = skill.getTarget(this, forceUse, dontMove, true);
        // Check the validity of the target
        if (target == null) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // Check if all casting conditions are completed
        if (!skill.checkCondition(this, target)) {
            sendPacket(ActionFailed.STATIC_PACKET);

            // Upon failed conditions, next action is called.
            if ((skill.getNextAction() != NextActionType.NONE) && (target != this) && target.isAutoAttackable(this)) {
                if ((getAI().getNextIntention() == null) || (getAI().getNextIntention().getCtrlIntention() != CtrlIntention.AI_INTENTION_MOVE_TO)) {
                    if (skill.getNextAction() == NextActionType.ATTACK) {
                        getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
                    } else if (skill.getNextAction() == NextActionType.CAST) {
                        getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target, item, false, false);
                    }
                }
            }

            return false;
        }

        // If a skill is currently being used, queue this one if this is not the same
        // In case of double casting, check if both slots are occupied, then queue skill.
        if (isCastingNow(SkillCaster::isAnyNormalType) || (isCastingNow(s -> s.getCastingType() == SkillCastingType.NORMAL) && isCastingNow(s -> s.getCastingType() == SkillCastingType.NORMAL_SECOND))) {
            // Do not queue skill if called by an item.
            if (item == null) {
                // Create a new SkillDat object and queue it in the player _queuedSkill
                setQueuedSkill(skill, item, forceUse, dontMove);
            }
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        if (_queuedSkill != null) {
            setQueuedSkill(null, null, false, false);
        }

        // Notify the AI with AI_INTENTION_CAST and target
        getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target, item, forceUse, dontMove);
        return true;
    }

    public boolean isInLooterParty(int LooterId) {
        final Player looter = World.getInstance().findPlayer(LooterId);

        // if Player is in a CommandChannel
        if (isInParty() && _party.isInCommandChannel() && (looter != null)) {
            return _party.getCommandChannel().getMembers().contains(looter);
        }

        if (isInParty() && (looter != null)) {
            return _party.getMembers().contains(looter);
        }

        return false;
    }

    /**
     * @return True if the Player is a Mage.
     */
    public boolean isMageClass() {
        return getClassId().isMage();
    }

    public boolean isMounted() {
        return _mountType != MountType.NONE;
    }

    public boolean checkLandingState() {
        // Check if char is in a no landing zone
        if (isInsideZone(ZoneType.NO_LANDING)) {
            return true;
        } else
            // if this is a castle that is currently being sieged, and the rider is NOT a castle owner
            // he cannot land.
            // castle owner is the leader of the clan that owns the castle where the pc is
            if (isInsideZone(ZoneType.SIEGE) && !((getClan() != null) && (CastleManager.getInstance().getCastle(this) == CastleManager.getInstance().getCastleByOwner(getClan())) && (this == getClan().getLeader().getPlayerInstance()))) {
                return true;
            }

        return false;
    }

    // returns false if the change of mount type fails.
    public void setMount(int npcId, int npcLevel) {
        final MountType type = MountType.findByNpcId(npcId);
        switch (type) {
            case NONE: // None
            {
                setIsFlying(false);
                break;
            }
            case STRIDER: // Strider
            {
                if (isNoble()) {
                    addSkill(CommonSkill.STRIDER_SIEGE_ASSAULT.getSkill(), false);
                }
                break;
            }
            case WYVERN: // Wyvern
            {
                setIsFlying(true);
                break;
            }
        }

        _mountType = type;
        _mountNpcId = npcId;
        _mountLevel = npcLevel;
    }

    /**
     * @return the type of Pet mounted (0 : none, 1 : Strider, 2 : Wyvern, 3: Wolf).
     */
    public MountType getMountType() {
        return _mountType;
    }

    @Override
    public final void stopAllEffects() {
        super.stopAllEffects();
        updateAndBroadcastStatus(2);
    }

    @Override
    public final void stopAllEffectsExceptThoseThatLastThroughDeath() {
        super.stopAllEffectsExceptThoseThatLastThroughDeath();
        updateAndBroadcastStatus(2);
    }

    public final void stopCubics() {
        if (!_cubics.isEmpty()) {
            _cubics.values().forEach(CubicInstance::deactivate);
            _cubics.clear();
        }
    }

    public final void stopCubicsByOthers() {
        if (!_cubics.isEmpty()) {
            boolean broadcast = false;
            for (CubicInstance cubic : _cubics.values()) {
                if (cubic.isGivenByOther()) {
                    cubic.deactivate();
                    _cubics.remove(cubic.getTemplate().getId());
                    broadcast = true;
                }
            }
            if (broadcast) {
                sendPacket(new ExUserInfoCubic(this));
                broadcastUserInfo();
            }
        }
    }

    /**
     * Send a Server->Client packet UserInfo to this Player and CharInfo to all Player in its _KnownPlayers.<br>
     * <B><U>Concept</U>:</B><br>
     * Others Player in the detection area of the Player are identified in <B>_knownPlayers</B>.<br>
     * In order to inform other players of this Player state modifications, server just need to go through _knownPlayers to send Server->Client Packet<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Send a Server->Client packet UserInfo to this Player (Public and Private Data)</li>
     * <li>Send a Server->Client packet CharInfo to all Player in _KnownPlayers of the Player (Public data only)</li>
     * </ul>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : DON'T SEND UserInfo packet to other players instead of CharInfo packet. Indeed, UserInfo packet contains PRIVATE DATA as MaxHP, STR, DEX...</B></FONT>
     */
    @Override
    public void updateAbnormalVisualEffects() {
        sendPacket(new ExUserInfoAbnormalVisualEffect(this));
        broadcastCharInfo();
    }

    /**
     * Disable the Inventory and create a new task to enable it after 1.5s.
     *
     * @param val
     */
    public void setInventoryBlockingStatus(boolean val) {
        _inventoryDisable = val;
        if (val) {
            ThreadPool.schedule(new InventoryEnableTask(this), 1500);
        }
    }

    /**
     * @return True if the Inventory is disabled.
     */
    public boolean isInventoryDisabled() {
        return _inventoryDisable;
    }

    /**
     * Add a cubic to this player.
     *
     * @param cubic
     * @return the old cubic for this cubic ID if any, otherwise {@code null}
     */
    public CubicInstance addCubic(CubicInstance cubic) {
        return _cubics.put(cubic.getTemplate().getId(), cubic);
    }

    /**
     * Get the player's cubics.
     *
     * @return the cubics
     */
    public Map<Integer, CubicInstance> getCubics() {
        return _cubics;
    }

    /**
     * Get the player cubic by cubic ID, if any.
     *
     * @param cubicId the cubic ID
     * @return the cubic with the given cubic ID, {@code null} otherwise
     */
    public CubicInstance getCubicById(int cubicId) {
        return _cubics.get(cubicId);
    }

    /**
     * @return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).
     */
    public int getEnchantEffect() {
        final Item wpn = getActiveWeaponInstance();

        if (wpn == null) {
            return 0;
        }

        return min(127, wpn.getEnchantLevel());
    }

    /**
     * @return the _lastFolkNpc of the Player corresponding to the last Folk wich one the player talked.
     */
    public Npc getLastFolkNPC() {
        return _lastFolkNpc;
    }

    /**
     * Set the _lastFolkNpc of the Player corresponding to the last Folk wich one the player talked.
     *
     * @param folkNpc
     */
    public void setLastFolkNPC(Npc folkNpc) {
        _lastFolkNpc = folkNpc;
    }

    public EnumIntBitmask<ClanPrivilege> getClanPrivileges() {
        return _clanPrivileges;
    }

    public void setClanPrivileges(EnumIntBitmask<ClanPrivilege> clanPrivileges) {
        _clanPrivileges = clanPrivileges.clone();
    }

    public boolean hasClanPrivilege(ClanPrivilege privilege) {
        return _clanPrivileges.has(privilege);
    }

    public int getPledgeClass() {
        return _pledgeClass;
    }

    // baron etc
    public void setPledgeClass(int classId) {
        _pledgeClass = classId;
        checkItemRestriction();
    }

    @Override
    public int getPledgeType() {
        return data.getSubPledge();
    }

    public void setPledgeType(int typeId) {
        data.setSubPledge(typeId);
    }

    public int getApprentice() {
        return data.getApprentice();
    }

    public void setApprentice(int apprenticeId) {
        data.setApprentice(apprenticeId);
    }

    public int getSponsor() {
        return data.getSponsor();
    }

    public void setSponsor(int sponsorId) {
        data.setSponsor(sponsorId);
    }

    public int getBookMarkSlot() {
        return _bookmarkslot;
    }

    public void setBookMarkSlot(int slot) {
        _bookmarkslot = slot;
        sendPacket(new ExGetBookMarkInfoPacket(this));
    }

    @Override
    public void sendMessage(String message) {
        sendPacket(SystemMessage.sendString(message));
    }

    public void setObserving(boolean state) {
        _observerMode = state;
        setTarget(null);
        setBlockActions(state);
        setIsInvul(state);
        setInvisible(state);
        if (hasAI() && !state) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
    }

    public void enterObserverMode(Location loc) {
        setLastLocation();

        // Remove Hide.
        getEffectList().stopEffects(AbnormalType.HIDE);

        setObserving(true);
        sendPacket(new ObservationMode(loc));

        teleToLocation(loc, false);

        broadcastUserInfo();
    }

    public void setLastLocation() {
        _lastLoc = new Location(getX(), getY(), getZ());
    }

    public void unsetLastLocation() {
        _lastLoc = null;
    }

    public void enterOlympiadObserverMode(Location loc, int id) {
        if (pet != null) {
            pet.unSummon(this);
        }

        if (hasServitors()) {
            getServitors().values().forEach(s -> s.unSummon(this));
        }

        // Remove Hide.
        getEffectList().stopEffects(AbnormalType.HIDE);

        if (!_cubics.isEmpty()) {
            _cubics.values().forEach(CubicInstance::deactivate);
            _cubics.clear();
            sendPacket(new ExUserInfoCubic(this));
        }

        if (_party != null) {
            _party.removePartyMember(this, Party.MessageType.EXPELLED);
        }

        _olympiadGameId = id;
        if (_waitTypeSitting) {
            standUp();
        }
        if (!_observerMode) {
            setLastLocation();
        }

        _observerMode = true;
        setTarget(null);
        setIsInvul(true);
        setInvisible(true);
        setInstance(OlympiadGameManager.getInstance().getOlympiadTask(id).getStadium().getInstance());
        teleToLocation(loc, false);
        sendPacket(new ExOlympiadMode(3));

        broadcastUserInfo();
    }

    public void leaveObserverMode() {
        setTarget(null);
        setInstance(null);
        teleToLocation(_lastLoc, false);
        unsetLastLocation();
        sendPacket(new ObservationReturn(getLocation()));

        setBlockActions(false);
        if (!isGM()) {
            setInvisible(false);
            setIsInvul(false);
        }
        if (hasAI()) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }

        setFalling(); // prevent receive falling damage
        _observerMode = false;

        broadcastUserInfo();
    }

    public void leaveOlympiadObserverMode() {
        if (_olympiadGameId == -1) {
            return;
        }
        _olympiadGameId = -1;
        _observerMode = false;
        setTarget(null);
        sendPacket(new ExOlympiadMode(0));
        setInstance(null);
        teleToLocation(_lastLoc, true);
        if (!isGM()) {
            setInvisible(false);
            setIsInvul(false);
        }
        if (hasAI()) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
        unsetLastLocation();
        broadcastUserInfo();
    }

    public int getOlympiadSide() {
        return _olympiadSide;
    }

    public void setOlympiadSide(int i) {
        _olympiadSide = i;
    }

    public int getOlympiadGameId() {
        return _olympiadGameId;
    }

    public void setOlympiadGameId(int id) {
        _olympiadGameId = id;
    }

    public Location getLastLocation() {
        return _lastLoc;
    }

    public boolean inObserverMode() {
        return _observerMode;
    }

    public AdminTeleportType getTeleMode() {
        return _teleportType;
    }

    public void setTeleMode(AdminTeleportType type) {
        _teleportType = type;
    }

    public int getRace(int i) {
        return _race[i];
    }

    public boolean isMessageRefusing() {
        return messageRefusing;
    }

    public void setMessageRefusing(boolean mode) {
        messageRefusing = mode;
        sendPacket(new EtcStatusUpdate(this));
    }

    public boolean getDietMode() {
        return _dietMode;
    }

    public void setDietMode(boolean mode) {
        _dietMode = mode;
    }

    public boolean isTradeRefusing() {
        return tradeRefusing;
    }

    public void setTradeRefusing(boolean mode) {
        tradeRefusing = mode;
    }

    public BlockList getBlockList() {
        return _blockList;
    }

    /**
     * @param player
     * @return returns {@code true} if player is target player cannot accepting messages from the current player, {@code false} otherwise
     */
    public boolean isBlocked(Player player) {
        return player.getBlockList().isBlockAll() || player.getBlockList().isInBlockList(this);
    }

    /**
     * @param player
     * @return returns {@code true} if player is target player can accepting messages from the current player, {@code false} otherwise
     */
    public boolean isNotBlocked(Player player) {
        return !player.getBlockList().isBlockAll() && !player.getBlockList().isInBlockList(this);
    }

    public void setIsInOlympiadMode(boolean b) {
        _inOlympiadMode = b;
    }

    public void setIsOlympiadStart(boolean b) {
        _OlympiadStart = b;
    }

    public boolean isOlympiadStart() {
        return _OlympiadStart;
    }

    public boolean isHero() {
        return _hero;
    }

    public void setHero(boolean hero) {
        if (hero && (data.getBaseClass() == _activeClass)) {
            for (Skill skill : SkillTreesData.getInstance().getHeroSkillTree()) {
                addSkill(skill, false); // Don't persist hero skills into database
            }
        } else {
            for (Skill skill : SkillTreesData.getInstance().getHeroSkillTree()) {
                removeSkill(skill, false, true); // Just remove skills from non-hero players
            }
        }
        _hero = hero;

        sendSkillList();
    }

    public boolean isInOlympiadMode() {
        return _inOlympiadMode;
    }

    public boolean isInDuel() {
        return _isInDuel;
    }

    public void setStartingDuel() {
        _startingDuel = true;
    }

    public int getDuelId() {
        return _duelId;
    }

    public int getDuelState() {
        return _duelState;
    }

    public void setDuelState(int mode) {
        _duelState = mode;
    }

    /**
     * Sets up the duel state using a non 0 duelId.
     *
     * @param duelId 0=not in a duel
     */
    public void setIsInDuel(int duelId) {
        if (duelId > 0) {
            _isInDuel = true;
            _duelState = Duel.DUELSTATE_DUELLING;
            _duelId = duelId;
        } else {
            if (_duelState == Duel.DUELSTATE_DEAD) {
                enableAllSkills();
                getStatus().startHpMpRegeneration();
            }
            _isInDuel = false;
            _duelState = Duel.DUELSTATE_NODUEL;
            _duelId = 0;
        }
        _startingDuel = false;
    }

    /**
     * This returns a SystemMessage stating why the player is not available for duelling.
     *
     * @return S1_CANNOT_DUEL... message
     */
    public SystemMessage getNoDuelReason() {
        final SystemMessage sm = getSystemMessage(_noDuelReason);
        sm.addPcName(this);
        _noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
        return sm;
    }

    /**
     * Checks if this player might join / start a duel.<br>
     * To get the reason use getNoDuelReason() after calling this function.
     *
     * @return true if the player might join/start a duel.
     */
    public boolean canDuel() {
        if (isInCombat() || isJailed()) {
            _noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
            return false;
        }
        if (isDead() || isAlikeDead() || ((getCurrentHp() < (getMaxHp() / 2d)) || (getCurrentMp() < (getMaxMp() / 2d)))) {
            _noDuelReason = SystemMessageId.C1_S_HP_OR_MP_IS_BELOW_50_AND_CANNOT_DUEL;
            return false;
        }
        if (_isInDuel || _startingDuel) {
            _noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL;
            return false;
        }
        if (_inOlympiadMode) {
            _noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_OR_THE_CEREMONY_OF_CHAOS;
            return false;
        }
        if (isOnEvent()) // custom event message
        {
            _noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
            return false;
        }

        if (privateStoreType != PrivateStoreType.NONE) {
            _noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE;
            return false;
        }
        if (isMounted() || isInBoat()) {
            _noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_FENRIR_OR_STRIDER;
            return false;
        }
        if (isFishing()) {
            _noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING;
            return false;
        }
        if (isInsideZone(ZoneType.PVP) || isInsideZone(ZoneType.PEACE) || isInsideZone(ZoneType.SIEGE)) {
            _noDuelReason = SystemMessageId.C1_IS_IN_AN_AREA_WHERE_DUEL_IS_NOT_ALLOWED_AND_YOU_CANNOT_APPLY_FOR_A_DUEL;
            return false;
        }
        return true;
    }

    public boolean isNoble() {
        return _noble;
    }

    public void setNoble(boolean val) {
        if (val) {
            SkillTreesData.getInstance().getNobleSkillAutoGetTree().forEach(skill -> addSkill(skill, false));
        } else {
            SkillTreesData.getInstance().getNobleSkillTree().forEach(skill -> removeSkill(skill, false, true));
        }
        _noble = val;
        sendSkillList();
    }

    public int getLvlJoinedAcademy() {
        return data.getLevelJoinedAcademy();
    }

    public void setLvlJoinedAcademy(int lvl) {
        data.setLevelJoinedAcademy(lvl);
    }

    @Override
    public boolean isAcademyMember() {
        return getLvlJoinedAcademy() > 0;
    }

    @Override
    public void setTeam(Team team) {
        super.setTeam(team);
        broadcastUserInfo();
        if (pet != null) {
            pet.broadcastStatusUpdate();
        }
        if (hasServitors()) {
            getServitors().values().forEach(Summon::broadcastStatusUpdate);
        }
    }

    public boolean wantsPeace() {
        return data.wantsPeace();
    }

    public void sendSkillList() {
        if (_skillListRefreshTask == null) {
            _skillListRefreshTask = ThreadPool.schedule(() -> {
                sendSkillList(0);
                _skillListRefreshTask = null;
            }, 1000);
        }
    }

    public void sendSkillList(int lastLearnedSkillId) {
        boolean isDisabled = false;
        final SkillList sl = new SkillList();

        for (Skill s : getSkillList()) {
            if (_clan != null) {
                isDisabled = s.isClanSkill() && (_clan.getReputationScore() < 0);
            }

            sl.addSkill(s.getDisplayId(), s.getReuseDelayGroup(), s.getDisplayLevel(), s.getSubLevel(), s.isPassive(), isDisabled, s.isEnchantable());
        }
        if (lastLearnedSkillId > 0) {
            sl.setLastLearnedSkillId(lastLearnedSkillId);
        }
        sendPacket(sl);
        sendPacket(new AcquireSkillList(this));
    }

    /**
     * 1. Add the specified class ID as a subclass (up to the maximum number of <b>three</b>) for this character.<BR>
     * 2. This method no longer changes the active _classIndex of the player. This is only done by the calling of setActiveClass() method as that should be the only way to do so.
     *
     * @param classId
     * @param classIndex
     * @param isDualClass
     * @return boolean subclassAdded
     */
    public boolean addSubClass(int classId, int classIndex, boolean isDualClass) {
        if (!_subclassLock.tryLock()) {
            return false;
        }

        try {
            if ((getTotalSubClasses() == Config.MAX_SUBCLASS) || (classIndex == 0)) {
                return false;
            }

            if (getSubClasses().containsKey(classIndex)) {
                return false;
            }

            // Note: Never change _classIndex in any method other than setActiveClass().

            final SubClass newClass = new SubClass();
            newClass.setClassId(classId);
            newClass.setClassIndex(classIndex);
            newClass.setVitalityPoints(PlayerStats.MAX_VITALITY_POINTS);
            if (isDualClass) {
                newClass.setIsDualClass(true);
                newClass.setExp(LevelData.getInstance().getExpForLevel(Config.BASE_DUALCLASS_LEVEL));
                newClass.setLevel(Config.BASE_DUALCLASS_LEVEL);
            }

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(ADD_CHAR_SUBCLASS)) {
                // Store the basic info about this new sub-class.
                statement.setInt(1, getObjectId());
                statement.setInt(2, newClass.getClassId());
                statement.setLong(3, newClass.getExp());
                statement.setLong(4, newClass.getSp());
                statement.setInt(5, newClass.getLevel());
                statement.setInt(6, newClass.getVitalityPoints());
                statement.setInt(7, newClass.getClassIndex());
                statement.setBoolean(8, newClass.isDualClass());
                statement.execute();
            } catch (Exception e) {
                LOGGER.warn("WARNING: Could not add character sub class for " + getName() + ": " + e.getMessage(), e);
                return false;
            }

            // Commit after database INSERT incase exception is thrown.
            getSubClasses().put(newClass.getClassIndex(), newClass);

            final ClassId subTemplate = ClassId.getClassId(classId);
            final var skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(subTemplate);
            final Map<Integer, Skill> prevSkillList = new HashMap<>();
            for (SkillLearn skillInfo : skillTree.values()) {
                if (skillInfo.getGetLevel() <= newClass.getLevel()) {
                    final Skill prevSkill = prevSkillList.get(skillInfo.getSkillId());
                    final Skill newSkill = SkillEngine.getInstance().getSkill(skillInfo.getSkillId(), skillInfo.getSkillLevel());

                    if (((prevSkill != null) && (prevSkill.getLevel() > newSkill.getLevel())) || SkillTreesData.getInstance().isRemoveSkill(subTemplate, skillInfo.getSkillId())) {
                        continue;
                    }

                    prevSkillList.put(newSkill.getId(), newSkill);
                    storeSkill(newSkill, prevSkill, classIndex);
                }
            }
            return true;
        } finally {
            _subclassLock.unlock();
        }
    }

    /**
     * 1. Completely erase all existance of the subClass linked to the classIndex.<br>
     * 2. Send over the newClassId to addSubClass() to create a new instance on this classIndex.<br>
     * 3. Upon Exception, revert the player to their BaseClass to avoid further problems.
     *
     * @param classIndex  the class index to delete
     * @param newClassId  the new class Id
     * @param isDualClass is subclass dualclass
     * @return {@code true} if the sub-class was modified, {@code false} otherwise
     */
    public boolean modifySubClass(int classIndex, int newClassId, boolean isDualClass) {
        if (!_subclassLock.tryLock()) {
            return false;
        }

        try {
            // Notify to scripts before class is removed.
            if (!getSubClasses().isEmpty()) // also null check
            {
                final int classId = getSubClasses().get(classIndex).getClassId();
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerProfessionCancel(this, classId), this);
            }

            final SubClass subClass = getSubClasses().get(classIndex);
            if (subClass == null) {
                return false;
            }

            if (subClass.isDualClass()) {
                getVariables().remove(PlayerVariables.ABILITY_POINTS_DUAL_CLASS);
                getVariables().remove(PlayerVariables.ABILITY_POINTS_USED_DUAL_CLASS);
                int revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_1_DUAL_CLASS, 0);
                if (revelationSkill != 0) {
                    removeSkill(revelationSkill);
                }
                revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_2_DUAL_CLASS, 0);
                if (revelationSkill != 0) {
                    removeSkill(revelationSkill);
                }
            }

            // Remove after stats are recalculated.
            getSubClasses().remove(classIndex);

            shortcuts.deleteShortcuts();

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement deleteHennas = con.prepareStatement(DELETE_CHAR_HENNAS);

                 PreparedStatement deleteSkillReuse = con.prepareStatement(DELETE_SKILL_SAVE);
                 PreparedStatement deleteSkills = con.prepareStatement(DELETE_CHAR_SKILLS);
                 PreparedStatement deleteSubclass = con.prepareStatement(DELETE_CHAR_SUBCLASS)) {
                // Remove all henna info stored for this sub-class.
                deleteHennas.setInt(1, getObjectId());
                deleteHennas.setInt(2, classIndex);
                deleteHennas.execute();

                // Remove all effects info stored for this sub-class.
                deleteSkillReuse.setInt(1, getObjectId());
                deleteSkillReuse.setInt(2, classIndex);
                deleteSkillReuse.execute();

                // Remove all skill info stored for this sub-class.
                deleteSkills.setInt(1, getObjectId());
                deleteSkills.setInt(2, classIndex);
                deleteSkills.execute();

                // Remove all basic info stored about this sub-class.
                deleteSubclass.setInt(1, getObjectId());
                deleteSubclass.setInt(2, classIndex);
                deleteSubclass.execute();
            } catch (Exception e) {
                LOGGER.warn("Could not modify sub class for " + getName() + " to class index " + classIndex + ": " + e.getMessage(), e);
                return false;
            }
        } finally {
            _subclassLock.unlock();
        }

        return addSubClass(newClassId, classIndex, isDualClass);
    }

    public boolean isSubClassActive() {
        return _classIndex > 0;
    }

    public boolean isDualClassActive() {

        if (!isSubClassActive() || _subClasses.isEmpty()) {
            return false;
        }

        final SubClass subClass = _subClasses.get(_classIndex);

        return nonNull(subClass) && subClass.isDualClass();
    }

    public boolean hasDualClass() {
        return getSubClasses().values().stream().anyMatch(SubClass::isDualClass);
    }

    public SubClass getDualClass() {
        return getSubClasses().values().stream().filter(SubClass::isDualClass).findFirst().orElse(null);
    }

    public void setDualClass(int classIndex) {
        if (isSubClassActive()) {
            getSubClasses().get(_classIndex).setIsDualClass(true);
        }
    }

    public IntMap<SubClass> getSubClasses() {
        return _subClasses;
    }

    public int getTotalSubClasses() {
        return getSubClasses().size();
    }

    public int getBaseClass() {
        return data.getBaseClass();
    }

    public void setBaseClass(int baseClass) {
        data.setBaseClass(baseClass);
    }

    public int getActiveClass() {
        return _activeClass;
    }

    public int getClassIndex() {
        return _classIndex;
    }

    protected void setClassIndex(int classIndex) {
        _classIndex = classIndex;
    }

    private void setClassTemplate(int classId) {
        _activeClass = classId;

        final PlayerTemplate pcTemplate = PlayerTemplateData.getInstance().getTemplate(classId);
        if (pcTemplate == null) {
            LOGGER.error("Missing template for classId: " + classId);
            throw new Error();
        }
        // Set the template of the Player
        setTemplate(pcTemplate);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerProfessionChange(this, pcTemplate, isSubClassActive()), this);
    }

    /**
     * Changes the character's class based on the given class index.<br>
     * An index of zero specifies the character's original (base) class, while indexes 1-3 specifies the character's sub-classes respectively.<br>
     * <font color="00FF00"/>WARNING: Use only on subclase change</font>
     *
     * @param classIndex
     * @return
     */
    public void setActiveClass(int classIndex) {
        if (!_subclassLock.tryLock()) {
            return;
        }

        try {
            // Cannot switch or change subclasses while transformed
            if (isTransformed()) {
                return;
            }

            // Remove active item skills before saving char to database
            // because next time when choosing this class, weared items can
            // be different
            inventory.forEachEquippedItem(item -> item.getAugmentation().removeBonus(this), Item::isAugmented);

            // abort any kind of cast.
            abortCast();

            if (isChannelized()) {
                getSkillChannelized().abortChannelization();
            }

            // 1. Call store() before modifying _classIndex to avoid skill effects rollover.
            // 2. Register the correct _classId against applied 'classIndex'.
            store(Config.SUBCLASS_STORE_SKILL_COOLTIME);

            if (_sellingBuffs != null) {
                _sellingBuffs.clear();
            }

            resetTimeStamps();

            // clear charges
            _charges.set(0);
            stopChargeTask();

            if (hasServitors()) {
                getServitors().values().forEach(s -> s.unSummon(this));
            }

            if (classIndex == 0) {
                setClassTemplate(data.getBaseClass());
            } else {
                try {
                    setClassTemplate(getSubClasses().get(classIndex).getClassId());
                } catch (Exception e) {
                    LOGGER.warn("Could not switch " + getName() + "'s sub class to class index " + classIndex + ": " + e.getMessage(), e);
                    return;
                }
            }
            _classIndex = classIndex;

            if (isInParty()) {
                _party.recalculatePartyLevel();
            }

            // Update the character's change in class status.
            // 1. Remove any active cubics from the player.
            // 2. Renovate the characters table in the database with the new class info, storing also buff/effect data.
            // 3. Remove all existing skills.
            // 4. Restore all the learned skills for the current class from the database.
            // 5. Restore effect/buff data for the new class.
            // 6. Restore henna data for the class, applying the new stat modifiers while removing existing ones.
            // 7. Reset HP/MP/CP stats and send Server->Client character status packet to reflect changes.
            // 8. Restore shortcut data related to this class.
            // 9. Resend a class change animation effect to broadcast to all nearby players.
            for (Skill oldSkill : getAllSkills()) {
                removeSkill(oldSkill, false, true);
            }

            stopAllEffectsExceptThoseThatLastThroughDeath();
            stopAllEffects();
            stopCubics();

            restoreRecipeBook(false);

            restoreSkills();
            rewardSkills();
            regiveTemporarySkills();

            // Prevents some issues when changing between subclases that shares skills
            resetDisabledSkills();

            restoreEffects();

            sendPacket(new EtcStatusUpdate(this));

            for (int i = 0; i < 4; i++) {
                _henna[i] = null;
            }

            restoreHenna();
            sendPacket(new HennaInfo(this));

            if (getCurrentHp() > getMaxHp()) {
                setCurrentHp(getMaxHp());
            }
            if (getCurrentMp() > getMaxMp()) {
                setCurrentMp(getMaxMp());
            }
            if (getCurrentCp() > getMaxCp()) {
                setCurrentCp(getMaxCp());
            }

            refreshOverloaded(true);
            broadcastUserInfo();

            // Clear resurrect xp calculation
            data.setExpBeforeDeath(0);

            shortcuts.restoreMe();
            sendPacket(new ShortCutInit());

            broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));
            sendPacket(new SkillCoolTime(this));
            sendPacket(new ExStorageMaxCount(this));

            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSubChange(this), this);
        } finally {
            _subclassLock.unlock();
        }
    }

    public boolean isSubClassLocked() {
        return _subclassLock.isLocked();
    }

    public void stopWarnUserTakeBreak() {
        if (_taskWarnUserTakeBreak != null) {
            _taskWarnUserTakeBreak.cancel(true);
            _taskWarnUserTakeBreak = null;
        }
    }

    public void startWarnUserTakeBreak() {
        if (_taskWarnUserTakeBreak == null) {
            _taskWarnUserTakeBreak = ThreadPool.scheduleAtFixedRate(new WarnUserTakeBreakTask(this), 3600000, 3600000);
        }
    }

    public void stopRentPet() {
        if (_taskRentPet != null) {
            // if the rent of a wyvern expires while over a flying zone, tp to down before unmounting
            if (checkLandingState() && (_mountType == MountType.WYVERN)) {
                teleToLocation(TeleportWhereType.TOWN);
            }

            if (dismount()) // this should always be true now, since we teleported already
            {
                _taskRentPet.cancel(true);
                _taskRentPet = null;
            }
        }
    }

    public boolean isRentedPet() {
        if (_taskRentPet != null) {
            return true;
        }

        return false;
    }

    public void stopWaterTask() {
        if (_taskWater != null) {
            _taskWater.cancel(false);
            _taskWater = null;
            sendPacket(new SetupGauge(getObjectId(), 2, 0));
        }
    }

    public void startWaterTask() {
        if (!isDead() && (_taskWater == null)) {
            final int timeinwater = (int) getStats().getValue(Stat.BREATH, 60000);

            sendPacket(new SetupGauge(getObjectId(), 2, timeinwater));
            _taskWater = ThreadPool.scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000);
        }
    }

    public boolean isInWater() {
        if (_taskWater != null) {
            return true;
        }

        return false;
    }

    public void checkWaterState() {
        if (isInsideZone(ZoneType.WATER)) {
            startWaterTask();
        } else {
            stopWaterTask();
        }
    }

    public void onEnter() {
        startWarnUserTakeBreak();
        if (isGM() && !Config.GM_STARTUP_BUILDER_HIDE) {
            // Bleah, see L2J custom below.
            if (isInvul()) {
                sendMessage("Entering world in Invulnerable mode.");
            }
            if (isInvisible()) {
                sendMessage("Entering world in Invisible mode.");
            }
            if (_silenceMode) {
                sendMessage("Entering world in Silence mode.");
            }
        }

        inventory.applyItemSkills();
        if (Config.STORE_SKILL_COOLTIME) {
            restoreEffects();
        }

        // TODO : Need to fix that hack!
        if (!isDead()) {
            setCurrentCp(_originalCp);
            setCurrentHp(_originalHp);
            setCurrentMp(_originalMp);
        }

        if (isAlikeDead()) // dead or fake dead
        {
            // no broadcast needed since the player will already spawn dead to others
            sendPacket(new Die(this));
        }

        revalidateZone(true);

        notifyFriends(FriendStatus.ONLINE);
        if (!canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
            checkPlayerSkills();
        }

        try {
            for (Zone zone : ZoneManager.getInstance().getZones(this)) {
                zone.onPlayerLoginInside(this);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogin(this), this);

        if (isMentee()) {
            // Notify to scripts
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeStatus(this, true), this);
        } else if (isMentor()) {
            // Notify to scripts
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMentorStatus(this, true), this);
        }
    }

    public long getLastAccess() {
        return data.getLastAccess();
    }

    @Override
    public void doRevive() {
        super.doRevive();
        sendPacket(new EtcStatusUpdate(this));
        _revivePet = false;
        _reviveRequested = 0;
        _revivePower = 0;

        if (isMounted()) {
            startFeed(_mountNpcId);
        }

        // Notify instance
        final Instance instance = getInstanceWorld();
        if (instance != null) {
            instance.doRevive(this);
        }
        lastDamages.clear();
    }

    @Override
    public void doRevive(double revivePower) {
        doRevive();
        restoreExp(revivePower);
    }

    public void reviveRequest(Player reviver, Skill skill, boolean isPet, int power) {
        if (isResurrectionBlocked()) {
            return;
        }

        if (_reviveRequested == 1) {
            if (_revivePet == isPet) {
                reviver.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
            } else if (isPet) {
                reviver.sendPacket(SystemMessageId.A_PET_CANNOT_BE_RESURRECTED_WHILE_IT_S_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING); // A pet cannot be resurrected while it's owner is in the process of resurrecting.
            } else {
                reviver.sendPacket(SystemMessageId.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
            }
            return;
        }
        if ((isPet && (pet != null) && pet.isDead()) || (!isPet && isDead())) {
            _reviveRequested = 1;
            _revivePower = Formulas.calculateSkillResurrectRestorePercent(power, reviver);
            _revivePet = isPet;

            if (hasCharmOfCourage()) {
                final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW);
                dlg.addTime(60000);
                sendPacket(dlg);
                return;
            }

            final long restoreExp = Math.round(((data.getExpBeforeDeath() - getExp()) * _revivePower) / 100);
            final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT);
            dlg.addPcName(reviver);
            dlg.addLong(restoreExp);
            dlg.addInt(power);
            sendPacket(dlg);
        }
    }

    public void reviveAnswer(int answer) {
        if ((_reviveRequested != 1) || (!isDead() && !_revivePet) || (_revivePet && (pet != null) && !pet.isDead())) {
            return;
        }

        if (answer == 1) {
            if (!_revivePet) {
                if (_revivePower != 0) {
                    doRevive(_revivePower);
                } else {
                    doRevive();
                }
            } else if (pet != null) {
                if (_revivePower != 0) {
                    pet.doRevive(_revivePower);
                } else {
                    pet.doRevive();
                }
            }
        }
        _reviveRequested = 0;
        _revivePower = 0;
    }

    public boolean isReviveRequested() {
        return (_reviveRequested == 1);
    }

    public boolean isRevivingPet() {
        return _revivePet;
    }

    public void removeReviving() {
        _reviveRequested = 0;
        _revivePower = 0;
    }

    public void onActionRequest() {
        if (isSpawnProtected()) {
            setSpawnProtection(false);

            if (!isInsideZone(ZoneType.PEACE)) {
                sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS);
            }

            if (getSettings(CharacterSettings.class).restoreSummonOnReconnect() && !hasSummon()) {
                if(PlayerSummonTable.getInstance().getServitors().containsKey(getObjectId())) {
                    PlayerSummonTable.getInstance().restoreServitor(this);
                } else if(PlayerSummonTable.getInstance().getPets().containsKey(getObjectId())) {
                    PlayerSummonTable.getInstance().restorePet(this);
                }
            }

        }
        if (isTeleportProtected()) {
            setTeleportProtection(false);
            if (!isInsideZone(ZoneType.PEACE)) {
                sendMessage("Teleport spawn protection ended.");
            }
        }
    }

    @Override
    public void teleToLocation(ILocational loc, boolean allowRandomOffset) {
        if ((_vehicle != null) && !_vehicle.isTeleporting()) {
            setVehicle(null);
        }

        if (isFlyingMounted() && (loc.getZ() < -1005)) {
            super.teleToLocation(loc.getX(), loc.getY(), -1005, loc.getHeading());
        }
        super.teleToLocation(loc, allowRandomOffset);
    }

    @Override
    public final void onTeleported() {
        super.onTeleported();

        setLastServerPosition(getX(), getY(), getZ());

        // Force a revalidation
        revalidateZone(true);

        checkItemRestriction();

        if ((Config.PLAYER_TELEPORT_PROTECTION > 0) && !_inOlympiadMode) {
            setTeleportProtection(true);
        }

        // Trained beast is lost after teleport
        if (tamedBeast != null) {
            for (TamedBeast tamedBeast : tamedBeast) {
                tamedBeast.deleteMe();
            }
            tamedBeast.clear();
        }

        // Modify the position of the pet if necessary
        if (pet != null) {
            pet.setFollowStatus(false);
            pet.teleToLocation(getLocation(), false);
            ((SummonAI) pet.getAI()).setStartFollowController(true);
            pet.setFollowStatus(true);
            pet.setInstance(getInstanceWorld());
            pet.updateAndBroadcastStatus(0);
        }

        getServitors().values().forEach(s ->
        {
            s.setFollowStatus(false);
            s.teleToLocation(getLocation(), false);
            ((SummonAI) s.getAI()).setStartFollowController(true);
            s.setFollowStatus(true);
            s.setInstance(getInstanceWorld());
            s.updateAndBroadcastStatus(0);
        });

        // Close time limited zone window.
        if (!isInTimedHuntingZone())
        {
            stopTimedHuntingZoneTask();
        }

        // show movie if available
        if (_movieHolder != null) {
            sendPacket(new ExStartScenePlayer(_movieHolder.getMovie()));
        }
        if(nonNull(autoPlaySettings) && autoPlaySettings.isActive()) {
            AutoPlayEngine.getInstance().stopAutoPlay(this);
        }
    }

    @Override
    public void setIsTeleporting(boolean teleport) {
        setIsTeleporting(teleport, true);
    }

    public void setIsTeleporting(boolean teleport, boolean useWatchDog) {
        super.setIsTeleporting(teleport);
        if (!useWatchDog) {
            return;
        }
        if (teleport) {
            if ((_teleportWatchdog == null) && (Config.TELEPORT_WATCHDOG_TIMEOUT > 0)) {
                synchronized (this) {
                    if (_teleportWatchdog == null) {
                        _teleportWatchdog = ThreadPool.schedule(new TeleportWatchdogTask(this), Config.TELEPORT_WATCHDOG_TIMEOUT * 1000);
                    }
                }
            }
        } else if (_teleportWatchdog != null) {
            _teleportWatchdog.cancel(false);
            _teleportWatchdog = null;
        }
    }

    public void setLastServerPosition(int x, int y, int z) {
        _lastServerPosition.setXYZ(x, y, z);
    }

    public Location getLastServerPosition() {
        return _lastServerPosition;
    }

    @Override
    public void addExpAndSp(double addToExp, double addToSp) {
        getStats().addExpAndSp(addToExp, addToSp, false);
    }

    public void addExpAndSp(double addToExp, double addToSp, boolean useVitality) {
        getStats().addExpAndSp(addToExp, addToSp, useVitality);
    }

    public void removeExpAndSp(long removeExp, long removeSp) {
        getStats().removeExpAndSp(removeExp, removeSp, true);
    }

    public void broadcastSnoop(ChatType type, String name, String _text) {
        if (!_snoopListener.isEmpty()) {
            final Snoop sn = new Snoop(getObjectId(), getName(), type, name, _text);

            for (Player pci : _snoopListener) {
                if (pci != null) {
                    pci.sendPacket(sn);
                }
            }
        }
    }

    public void addSnooper(Player pci) {
        if (!_snoopListener.contains(pci)) {
            _snoopListener.add(pci);
        }
    }

    public void removeSnooper(Player pci) {
        _snoopListener.remove(pci);
    }

    public void addSnooped(Player pci) {
        if (!_snoopedPlayer.contains(pci)) {
            _snoopedPlayer.add(pci);
        }
    }

    public void removeSnooped(Player pci) {
        _snoopedPlayer.remove(pci);
    }

    public void addHtmlAction(HtmlActionScope scope, String action) {
        htmlActionCaches[scope.ordinal()].add(action);
    }

    public void clearHtmlActions(HtmlActionScope scope) {
        htmlActionCaches[scope.ordinal()].clear();
    }

    public void setHtmlActionOriginObjectId(HtmlActionScope scope, int npcObjId) {
        if (npcObjId < 0) {
            throw new IllegalArgumentException();
        }

        _htmlActionOriginObjectIds[scope.ordinal()] = npcObjId;
    }

    public int getLastHtmlActionOriginId() {
        return _lastHtmlActionOriginObjId;
    }

    public void setLastHtmlActionOriginId(int objId) {
        _lastHtmlActionOriginObjId = objId;
    }

    private boolean validateHtmlAction(Iterable<String> actionIter, String action) {
        for (String cachedAction : actionIter) {
            if (cachedAction.charAt(cachedAction.length() - 1) == AbstractHtmlPacket.VAR_PARAM_START_CHAR) {
                if (action.startsWith(cachedAction.substring(0, cachedAction.length() - 1).trim())) {
                    return true;
                }
            } else if (cachedAction.equals(action)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the HTML action was sent in a HTML packet.<br>
     * If the HTML action was not sent for whatever reason, -1 is returned.<br>
     * Otherwise, the NPC object ID or 0 is returned.<br>
     * 0 means the HTML action was not bound to an NPC<br>
     * and no range checks need to be made.
     *
     * @param action the HTML action to check
     * @return NPC object ID, 0 or -1
     */
    public int validateHtmlAction(String action) {
        for (int i = 0; i < htmlActionCaches.length; ++i) {
            if (validateHtmlAction(htmlActionCaches[i], action)) {
                _lastHtmlActionOriginObjId = _htmlActionOriginObjectIds[i];
                return _lastHtmlActionOriginObjId;
            }
        }

        return -1;
    }

    /**
     * Performs following tests:
     * <ul>
     * <li>Inventory contains item</li>
     * <li>Item owner id == owner id</li>
     * <li>It isnt pet control item while mounting pet or pet summoned</li>
     * <li>It isnt active enchant item</li>
     * <li>It isnt wear item</li>
     * </ul>
     *
     * @param objectId item object id
     * @param action   just for login purpose
     * @return
     */
    public boolean validateItemManipulation(int objectId, String action) {
        final Item item = inventory.getItemByObjectId(objectId);

        if (isNull(item) || item.getOwnerId() != getObjectId()) {
            LOGGER.debug("player {} tried to {} item he is not owner of", this, action);
            return false;
        }

        // Pet is summoned and not the item that summoned the pet AND not the buggle from strider you're mounting
        if (( nonNull(pet) && pet.getControlObjectId() == objectId) || mountObjectID == objectId) {
            return false;
        }

        return !isProcessingItem(objectId);
    }

    /**
     * @return Returns the inBoat.
     */
    public boolean isInBoat() {
        return (_vehicle != null) && _vehicle.isBoat();
    }

    /**
     * @return
     */
    public Boat getBoat() {
        return (Boat) _vehicle;
    }

    public boolean isInShuttle() {
        return _vehicle instanceof Shuttle;
    }

    public Shuttle getShuttle() {
        return (Shuttle) _vehicle;
    }

    public Vehicle getVehicle() {
        return _vehicle;
    }

    public void setVehicle(Vehicle v) {
        if ((v == null) && (_vehicle != null)) {
            _vehicle.removePassenger(this);
        }

        _vehicle = v;
    }

    public boolean isInVehicle() {
        return _vehicle != null;
    }

    public boolean isInCrystallize() {
        return _inCrystallize;
    }

    public void setInCrystallize(boolean inCrystallize) {
        _inCrystallize = inCrystallize;
    }

    public Location getInVehiclePosition() {
        return _inVehiclePosition;
    }

    public void setInVehiclePosition(Location pt) {
        _inVehiclePosition = pt;
    }

    /**
     * Manage the delete task of a Player (Leave Party, Unsummon pet, Save its inventory in the database, Remove it from the world...).<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>If the Player is in observer mode, set its position to its position before entering in observer mode</li>
     * <li>Set the online Flag to True or False and update the characters table of the database with online status and lastAccess</li>
     * <li>Stop the HP/MP/CP Regeneration task</li>
     * <li>Cancel Crafting, Attack or Cast</li>
     * <li>Remove the Player from the world</li>
     * <li>Stop Party and Unsummon Pet</li>
     * <li>Update database with items in its inventory and remove them from the world</li>
     * <li>Remove all WorldObject from _knownObjects and _knownPlayer of the Creature then cancel Attak or Cast and notify AI</li>
     * <li>Close the connection with the client</li>
     * </ul>
     * <br>
     * Remember this method is not to be used to half-ass disconnect players! This method is dedicated only to erase the player from the world.<br>
     * If you intend to disconnect a player please use {@link Disconnection}
     */
    @Override
    public boolean deleteMe() {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogout(this), this);
        AutoPlayEngine.getInstance().stopTasks(this);
        try {
            for (Zone zone : ZoneManager.getInstance().getZones(this)) {
                zone.onPlayerLogoutInside(this);
            }
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        // Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout)
        try {
            if (!_isOnline) {
                LOGGER.error("deleteMe() called on offline character " + this, new RuntimeException());
            }
            setOnlineStatus(false, true);
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        try {
            if (Config.ENABLE_BLOCK_CHECKER_EVENT && (_handysBlockCheckerEventArena != -1)) {
                HandysBlockCheckerManager.getInstance().onDisconnect(this);
            }
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        try {
            _isOnline = false;
            abortAttack();
            abortCast();
            stopMove(null);
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        try {
            if (_matchingRoom != null) {
                _matchingRoom.deleteMember(this, false);
            }
            MatchingRoomManager.getInstance().removeFromWaitingList(this);
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        try {
            if (isFlying()) {
                removeSkill(SkillEngine.getInstance().getSkill(CommonSkill.WYVERN_BREATH.getId(), 1));
            }
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        // Recommendations must be saved before task (timer) is canceled
        try {
            storeRecommendations();
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }
        // Stop the HP/MP/CP Regeneration task (scheduled tasks)
        try {
            stopAllTimers();
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        try {
            setIsTeleporting(false);
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        // Stop crafting, if in progress
        try {
            RecipeController.getInstance().requestMakeItemAbort(this);
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        // Cancel Attak or Cast
        try {
            setTarget(null);
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        if (isChannelized()) {
            getSkillChannelized().abortChannelization();
        }

        // Stop all toggles.
        getEffectList().stopAllToggles();

        // Remove from world regions zones
        ZoneManager.getInstance().getRegion(this).removeFromZones(this);

        // Remove the Player from the world
        try {
            decayMe();
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        // If a Party is in progress, leave it (and festival party)
        if (isInParty()) {
            try {
                leaveParty();
            } catch (Exception e) {
                LOGGER.error("deleteMe()", e);
            }
        }

        if (OlympiadManager.getInstance().isRegistered(this) || (getOlympiadGameId() != -1)) {
            OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
        }

        // If the Player has Pet, unsummon it
        if (hasSummon()) {
            try {
                Summon pet = this.pet;
                if (pet != null) {
                    pet.setRestoreSummon(true);
                    pet.unSummon(this);
                    // Dead pet wasn't unsummoned, broadcast npcinfo changes (pet will be without owner name - means owner offline)
                    pet = this.pet;
                    if (pet != null) {
                        pet.broadcastNpcInfo(0);
                    }
                }

                getServitors().values().forEach(s ->
                {
                    s.setRestoreSummon(true);
                    s.unSummon(this);
                });
            } catch (Exception e) {
                LOGGER.error("deleteMe()", e);
            } // returns pet to control item
        }

        if (_clan != null) {
            // set the status for pledge member list to OFFLINE
            try {
                final ClanMember clanMember = _clan.getClanMember(getObjectId());
                if (clanMember != null) {
                    clanMember.setPlayerInstance(null);
                }
            } catch (Exception e) {
                LOGGER.error("deleteMe()", e);
            }
        }

        if (getActiveRequester() != null) {
            // deals with sudden exit in the middle of transaction
            setActiveRequester(null);
            cancelActiveTrade();
        }

        // If the Player is a GM, remove it from the GM List
        if (isGM()) {
            try {
                AdminData.getInstance().deleteGm(this);
            } catch (Exception e) {
                LOGGER.error("deleteMe()", e);
            }
        }

        try {
            // Check if the Player is in observer mode to set its position to its position
            // before entering in observer mode
            if (_observerMode) {
                setLocationInvisible(_lastLoc);
            }

            if (_vehicle != null) {
                _vehicle.oustPlayer(this);
            }
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        // remove player from instance
        final Instance inst = getInstanceWorld();
        if (inst != null) {
            try {
                inst.onPlayerLogout(this);
            } catch (Exception e) {
                LOGGER.error("deleteMe()", e);
            }
        }

        try {
            stopCubics();
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        // Update database with items in its inventory and remove them from the world
        try {
            inventory.deleteMe();
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        // Update database with items in its warehouse and remove them from the world
        try {
            clearWarehouse();
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }
        if (Config.WAREHOUSE_CACHE) {
            WarehouseCacheManager.getInstance().remCacheTask(this);
        }

        try {
            _freight.deleteMe();
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        try {
            clearRefund();
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }

        if (clanId > 0) {
            _clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
            _clan.broadcastToOnlineMembers(new ExPledgeCount(_clan));
            // ClanTable.getInstance().getClan(getClanId()).broadcastToOnlineMembers(new PledgeShowMemberListAdd(this));
        }

        for (Player player : _snoopedPlayer) {
            player.removeSnooper(this);
        }

        for (Player player : _snoopListener) {
            player.removeSnooped(this);
        }

        if (isMentee()) {
            // Notify to scripts
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeStatus(this, false), this);
        } else if (isMentor()) {
            // Notify to scripts
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMentorStatus(this, false), this);
        }

        // we store all data from players who are disconnected while in an event in order to restore it in the next login
        if (Event.isParticipant(this)) {
            Event.savePlayerEventStatus(this);
        }

        try {
            notifyFriends(FriendStatus.OFFLINE);
            _blockList.playerLogout();
        } catch (Exception e) {
            LOGGER.warn("Exception on deleteMe() notifyFriends: " + e.getMessage(), e);
        }

        // Stop all passives and augment options
        getEffectList().stopAllPassives(false, false);
        getEffectList().stopAllOptions(false, false);

        SaveTaskManager.getInstance().remove(this);

        return super.deleteMe();
    }

    public int getInventoryLimit() {
        int ivlim = Config.INVENTORY_MAXIMUM_NO_DWARF;
        if (isGM()) {
            ivlim = Config.INVENTORY_MAXIMUM_GM;
        } else if (getRace() == Race.DWARF) {
            ivlim = Config.INVENTORY_MAXIMUM_DWARF;
        }
        return ivlim + (int) getStats().getValue(Stat.INVENTORY_NORMAL, 0);
    }

    public int getWareHouseLimit() {
        int whlim;
        if (getRace() == Race.DWARF) {
            whlim = Config.WAREHOUSE_SLOTS_DWARF;
        } else {
            whlim = Config.WAREHOUSE_SLOTS_NO_DWARF;
        }

        whlim += (int) getStats().getValue(Stat.STORAGE_PRIVATE, 0);

        return whlim;
    }

    public int getPrivateSellStoreLimit() {
        int pslim;

        if (getRace() == Race.DWARF) {
            pslim = Config.MAX_PVTSTORESELL_SLOTS_DWARF;
        } else {
            pslim = Config.MAX_PVTSTORESELL_SLOTS_OTHER;
        }

        pslim += (int) getStats().getValue(Stat.TRADE_SELL, 0);

        return pslim;
    }

    public int getPrivateBuyStoreLimit() {
        int pblim;

        if (getRace() == Race.DWARF) {
            pblim = Config.MAX_PVTSTOREBUY_SLOTS_DWARF;
        } else {
            pblim = Config.MAX_PVTSTOREBUY_SLOTS_OTHER;
        }
        pblim += (int) getStats().getValue(Stat.TRADE_BUY, 0);

        return pblim;
    }

    public int getDwarfRecipeLimit() {
        int recdlim = Config.DWARF_RECIPE_LIMIT;
        recdlim += (int) getStats().getValue(Stat.RECIPE_DWARVEN, 0);
        return recdlim;
    }

    public int getCommonRecipeLimit() {
        int recclim = Config.COMMON_RECIPE_LIMIT;
        recclim += (int) getStats().getValue(Stat.RECIPE_COMMON, 0);
        return recclim;
    }

    /**
     * @return Returns the mountNpcId.
     */
    public int getMountNpcId() {
        return _mountNpcId;
    }

    /**
     * @return Returns the mountLevel.
     */
    public int getMountLevel() {
        return _mountLevel;
    }

    public int getMountObjectID() {
        return mountObjectID;
    }

    public void setMountObjectID(int newID) {
        mountObjectID = newID;
    }

    public SkillUseHolder getQueuedSkill() {
        return _queuedSkill;
    }

    /**
     * Create a new SkillDat object and queue it in the player _queuedSkill.
     *
     * @param queuedSkill
     * @param item
     * @param ctrlPressed
     * @param shiftPressed
     */
    public void setQueuedSkill(Skill queuedSkill, Item item, boolean ctrlPressed, boolean shiftPressed) {
        if (queuedSkill == null) {
            _queuedSkill = null;
            return;
        }
        _queuedSkill = new SkillUseHolder(queuedSkill, item, ctrlPressed, shiftPressed);
    }

    /**
     * @return {@code true} if player is jailed, {@code false} otherwise.
     */
    public boolean isJailed() {
        return PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL) || PunishmentManager.getInstance().hasPunishment(getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.JAIL) || PunishmentManager.getInstance().hasPunishment(getIPAddress(), PunishmentAffect.IP, PunishmentType.JAIL);
    }

    /**
     * @return {@code true} if player is chat banned, {@code false} otherwise.
     */
    public boolean isChatBanned() {
        return PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN) || PunishmentManager.getInstance().hasPunishment(getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.CHAT_BAN) || PunishmentManager.getInstance().hasPunishment(getIPAddress(), PunishmentAffect.IP, PunishmentType.CHAT_BAN);
    }

    public void startFameTask(long delay, int fameFixRate) {
        if ((getLevel() < 40) || (getClassId().level() < 2)) {
            return;
        }
        if (_fameTask == null) {
            _fameTask = ThreadPool.scheduleAtFixedRate(new FameTask(this, fameFixRate), delay, delay);
        }
    }

    public void stopFameTask() {
        if (_fameTask != null) {
            _fameTask.cancel(false);
            _fameTask = null;
        }
    }

    public int getPowerGrade() {
        return data.getPowerGrade();
    }

    public void setPowerGrade(int power) {
        data.setPowerGrade(power);
    }

    public int getChargedSouls() {
        return _souls;
    }

    /**
     * Increase Souls
     *
     * @param count
     */
    public void increaseSouls(int count) {
        _souls += count;
        final SystemMessage sm = getSystemMessage(SystemMessageId.YOUR_SOUL_COUNT_HAS_INCREASED_BY_S1_IT_IS_NOW_AT_S2);
        sm.addInt(count);
        sm.addInt(_souls);
        sendPacket(sm);
        restartSoulTask();
        sendPacket(new EtcStatusUpdate(this));
    }

    /**
     * Decreases existing Souls.
     *
     * @param count
     * @param skill
     * @return
     */
    public boolean decreaseSouls(int count, Skill skill) {
        _souls -= count;

        if (_souls < 0) {
            _souls = 0;
        }

        if (_souls == 0) {
            stopSoulTask();
        } else {
            restartSoulTask();
        }

        sendPacket(new EtcStatusUpdate(this));
        return true;
    }

    /**
     * Clear out all Souls from this Player
     */
    public void clearSouls() {
        _souls = 0;
        stopSoulTask();
        sendPacket(new EtcStatusUpdate(this));
    }

    /**
     * Starts/Restarts the SoulTask to Clear Souls after 10 Mins.
     */
    private void restartSoulTask() {
        if (_soulTask != null) {
            _soulTask.cancel(false);
            _soulTask = null;
        }
        _soulTask = ThreadPool.schedule(new ResetSoulsTask(this), 600000);

    }

    /**
     * Stops the Clearing Task.
     */
    public void stopSoulTask() {
        if (_soulTask != null) {
            _soulTask.cancel(false);
            _soulTask = null;
        }
    }

    @Override
    public Player getActingPlayer() {
        return this;
    }

    @Override
    public void sendDamageMessage(Creature target, Skill skill, int damage, double elementalDamage, boolean crit, boolean miss) {
        // Check if hit is missed
        if (miss) {
            if (skill == null) {
                if (GameUtils.isPlayer(target)) {
                    final SystemMessage sm = getSystemMessage(SystemMessageId.C1_HAS_EVADED_C2_S_ATTACK);
                    sm.addPcName(target.getActingPlayer());
                    sm.addString(getName());
                    target.sendPacket(sm);
                }
                final SystemMessage sm = getSystemMessage(SystemMessageId.C1_S_ATTACK_WENT_ASTRAY);
                sm.addPcName(this);
                sendPacket(sm);
            } else {
                sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.EVADED));
            }
            return;
        }

        // Check if hit is critical
        if (crit) {
            if ((skill == null) || !skill.isMagic()) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.C1_LANDED_A_CRITICAL_HIT);
                sm.addPcName(this);
                sendPacket(sm);
            } else {
                sendPacket(SystemMessageId.M_CRITICAL);
            }

            if (skill != null) {
                sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.CRITICAL));
            }
        }

        if (isInOlympiadMode() && GameUtils.isPlayer(target) && target.getActingPlayer().isInOlympiadMode() && (target.getActingPlayer().getOlympiadGameId() == getOlympiadGameId())) {
            OlympiadGameManager.getInstance().notifyCompetitorDamage(this, damage);
        }

        SystemMessage sm = null;

        if ((target.isHpBlocked() && !GameUtils.isNpc(target)) || (GameUtils.isPlayer(target) && target.isAffected(EffectFlag.DUELIST_FURY) && !isAffected(EffectFlag.FACEOFF))) {
            sm = getSystemMessage(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
        } else if (GameUtils.isDoor(target) || target instanceof ControlTower) {
            sm = getSystemMessage(SystemMessageId.YOU_HIT_FOR_S1_DAMAGE);
            sm.addInt(damage);
        } else if (this != target){
            if(elementalDamage != 0) {
                sm = getSystemMessage(S1_HAS_INFLICTED_S3_S4_ATTRIBUTE_DAMGE_DAMAGE_TO_S2);
            } else {
                sm = getSystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
            }
            sm.addPcName(this);
            sm.addString(target.getName());
            sm.addInt(damage);
            if(elementalDamage != 0) {
                sm.addInt((int) elementalDamage);
            }
            sm.addPopup(target.getObjectId(), getObjectId(), -damage);
        }
        if(sm != null) {
            sendPacket(sm);
        }
    }

    /**
     * @return
     */
    public int getAgathionId() {
        return _agathionId;
    }

    /**
     * @param npcId
     */
    public void setAgathionId(int npcId) {
        _agathionId = npcId;
    }

    public int getVitalityPoints() {
        return getStats().getVitalityPoints();
    }

    public void setVitalityPoints(int points, boolean quiet) {
        getStats().setVitalityPoints(points, quiet);
    }

    public void updateVitalityPoints(int points, boolean useRates, boolean quiet) {
        getStats().updateVitalityPoints(points, useRates, quiet);
    }

    public void checkItemRestriction() {
        for (InventorySlot slot : InventorySlot.values()) {
            var item = inventory.getPaperdollItem(slot);
            if(nonNull(item) && !item.getTemplate().checkCondition(this, this, false)) {
                inventory.unEquipItemInSlot(slot);

                final InventoryUpdate iu = new InventoryUpdate();
                iu.addModifiedItem(item);
                sendInventoryUpdate(iu);

                SystemMessage sm;
                if (item.getBodyPart() == BodyPart.BACK) {
                    sendPacket(SystemMessageId.YOUR_CLOAK_HAS_BEEN_UNEQUIPPED_BECAUSE_YOUR_ARMOR_SET_IS_NO_LONGER_COMPLETE);
                    return;
                }

                if (item.getEnchantLevel() > 0) {
                    sm = getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(item.getEnchantLevel()).addItemName(item);
                } else {
                    sm = getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED).addItemName(item);
                }
                sendPacket(sm);
            }
        }
    }

    public void addTransformSkill(Skill skill) {
        if (_transformSkills == null) {
            synchronized (this) {
                if (_transformSkills == null) {
                    _transformSkills = new HashMap<>();
                }
            }
        }
        _transformSkills.put(skill.getId(), skill);
    }

    public boolean hasTransformSkill(Skill skill) {
        return (_transformSkills != null) && (_transformSkills.get(skill.getId()) == skill);
    }

    public boolean hasTransformSkills() {
        return (_transformSkills != null);
    }

    public Collection<Skill> getAllTransformSkills() {
        final Map<Integer, Skill> transformSkills = _transformSkills;
        return transformSkills != null ? transformSkills.values() : Collections.emptyList();
    }

    public synchronized void removeAllTransformSkills() {
        _transformSkills = null;
    }

    /**
     * @param skillId the id of the skill that this player might have.
     * @return {@code skill} object refered to this skill id that this player has, {@code null} otherwise.
     */
    @Override
    public final Skill getKnownSkill(int skillId) {
        final Map<Integer, Skill> transformSkills = _transformSkills;
        return transformSkills != null ? transformSkills.getOrDefault(skillId, super.getKnownSkill(skillId)) : super.getKnownSkill(skillId);
    }

    /**
     * @return all visible skills that appear on Alt+K for this player.
     */
    public Collection<Skill> getSkillList() {
        Collection<Skill> currentSkills = getAllSkills();

        if (isTransformed()) {
            final Map<Integer, Skill> transformSkills = _transformSkills;
            if (transformSkills != null) {
                // Include transformation skills and those skills that are allowed during transformation.
                currentSkills = currentSkills.stream().filter(Skill::allowOnTransform).collect(Collectors.toList());

                // Revelation skills.
                if (isDualClassActive())
                {
                    int revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_1_DUAL_CLASS, 0);
                    if (revelationSkill != 0)
                    {
                        addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                    }
                    revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_2_DUAL_CLASS, 0);
                    if (revelationSkill != 0)
                    {
                        addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                    }
                }
                else if (!isSubClassActive())
                {
                    int revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_1_MAIN_CLASS, 0);
                    if (revelationSkill != 0)
                    {
                        addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                    }
                    revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_2_MAIN_CLASS, 0);
                    if (revelationSkill != 0)
                    {
                        addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                    }
                }
                // Include transformation skills.
                currentSkills.addAll(transformSkills.values());
            }
        }

        //@formatter:off
        return currentSkills.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlockActionUseSkill()) // Skills that are blocked from player use are not shown in skill list.
                .collect(Collectors.toList());
        //@formatter:on
    }

    protected void startFeed(int npcId) {
        _canFeed = npcId > 0;
        if (!isMounted()) {
            return;
        }
        if (hasPet()) {
            setCurrentFeed(pet.getCurrentFed());
            _controlItemId = pet.getControlObjectId();
            sendPacket(new SetupGauge(3, (_curFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume()));
            if (!isDead()) {
                _mountFeedTask = ThreadPool.scheduleAtFixedRate(new PetFeedTask(this), 10000, 10000);
            }
        } else if (_canFeed) {
            setCurrentFeed(getMaxFeed());
            final SetupGauge sg = new SetupGauge(3, (_curFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume());
            sendPacket(sg);
            if (!isDead()) {
                _mountFeedTask = ThreadPool.scheduleAtFixedRate(new PetFeedTask(this), 10000, 10000);
            }
        }
    }

    public void stopFeed() {
        if (_mountFeedTask != null) {
            _mountFeedTask.cancel(false);
            _mountFeedTask = null;
        }
    }

    private void clearPetData() {
        _data = null;
    }

    public final PetData getPetData(int npcId) {
        if (_data == null) {
            _data = PetDataTable.getInstance().getPetData(npcId);
        }
        return _data;
    }

    private PetLevelData getPetLevelData(int npcId) {
        if (_leveldata == null) {
            _leveldata = PetDataTable.getInstance().getPetData(npcId).getPetLevelData(getMountLevel());
        }
        return _leveldata;
    }

    public int getCurrentFeed() {
        return _curFeed;
    }

    public void setCurrentFeed(int num) {
        final boolean lastHungryState = isHungry();
        _curFeed = num > getMaxFeed() ? getMaxFeed() : num;
        final SetupGauge sg = new SetupGauge(3, (_curFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume());
        sendPacket(sg);
        // broadcast move speed change when strider becomes hungry / full
        if (lastHungryState != isHungry()) {
            broadcastUserInfo();
        }
    }

    public int getFeedConsume() {
        // if pet is attacking
        if (isAttackingNow()) {
            return getPetLevelData(_mountNpcId).getPetFeedBattle();
        }
        return getPetLevelData(_mountNpcId).getPetFeedNormal();
    }

    private int getMaxFeed() {
        return getPetLevelData(_mountNpcId).getPetMaxFeed();
    }

    public boolean isHungry() {
        return hasPet() && _canFeed && (_curFeed < ((getPetData(_mountNpcId).getHungryLimit() / 100f) * getPetLevelData(_mountNpcId).getPetMaxFeed()));
    }

    public void enteredNoLanding(int delay) {
        _dismountTask = ThreadPool.schedule(new DismountTask(this), delay * 1000);
    }

    public void exitedNoLanding() {
        if (_dismountTask != null) {
            _dismountTask.cancel(true);
            _dismountTask = null;
        }
    }

    public void storePetFood(int petId) {
        if ((_controlItemId != 0) && (petId != 0)) {
            String req;
            req = "UPDATE pets SET fed=? WHERE item_obj_id = ?";
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(req)) {
                statement.setInt(1, _curFeed);
                statement.setInt(2, _controlItemId);
                statement.executeUpdate();
                _controlItemId = 0;
            } catch (Exception e) {
                LOGGER.error("Failed to store Pet [NpcId: " + petId + "] data", e);
            }
        }
    }

    public void setIsInSiege(boolean b) {
        _isInSiege = b;
    }

    public boolean isInSiege() {
        return _isInSiege;
    }

    /**
     * @param isInHideoutSiege sets the value of {@link #_isInHideoutSiege}.
     */
    public void setIsInHideoutSiege(boolean isInHideoutSiege) {
        _isInHideoutSiege = isInHideoutSiege;
    }

    /**
     * @return the value of {@link #_isInHideoutSiege}, {@code true} if the player is participing on a Hideout Siege, otherwise {@code false}.
     */
    public boolean isInHideoutSiege() {
        return _isInHideoutSiege;
    }

    public FloodProtectors getFloodProtectors() {
        return _client.getFloodProtectors();
    }

    public boolean isFlyingMounted() {
        return checkTransformed(Transform::isFlying);
    }

    /**
     * Returns the Number of Charges this Player got.
     *
     * @return
     */
    public int getCharges() {
        return _charges.get();
    }

    public void setCharges(int count) {
        restartChargeTask();
        _charges.set(count);
    }

    public boolean decreaseCharges(int count) {
        if (_charges.get() < count) {
            return false;
        }

        // Charge clear task should be reset every time a charge is decreased and stopped when charges become 0.
        if (_charges.addAndGet(-count) == 0) {
            stopChargeTask();
        } else {
            restartChargeTask();
        }

        sendPacket(new EtcStatusUpdate(this));
        return true;
    }

    public void clearCharges() {
        _charges.set(0);
        sendPacket(new EtcStatusUpdate(this));
    }

    /**
     * Starts/Restarts the ChargeTask to Clear Charges after 10 Mins.
     */
    private void restartChargeTask() {
        if (_chargeTask != null) {
            _chargeTask.cancel(false);
            _chargeTask = null;
        }
        _chargeTask = ThreadPool.schedule(new ResetChargesTask(this), 600000);
    }

    /**
     * Stops the Charges Clearing Task.
     */
    public void stopChargeTask() {
        if (_chargeTask != null) {
            _chargeTask.cancel(false);
            _chargeTask = null;
        }
    }

    public void teleportBookmarkModify(int id, int icon, String tag, String name) {
        final TeleportBookmark bookmark = _tpbookmarks.get(id);
        if (bookmark != null) {
            bookmark.setIcon(icon);
            bookmark.setTag(tag);
            bookmark.setName(name);

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(UPDATE_TP_BOOKMARK)) {
                statement.setInt(1, icon);
                statement.setString(2, tag);
                statement.setString(3, name);
                statement.setInt(4, getObjectId());
                statement.setInt(5, id);
                statement.execute();
            } catch (Exception e) {
                LOGGER.warn("Could not update character teleport bookmark data: " + e.getMessage(), e);
            }
        }

        sendPacket(new ExGetBookMarkInfoPacket(this));
    }

    public void teleportBookmarkDelete(int id) {
        if (_tpbookmarks.remove(id) != null) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(DELETE_TP_BOOKMARK)) {
                statement.setInt(1, getObjectId());
                statement.setInt(2, id);
                statement.execute();
            } catch (Exception e) {
                LOGGER.warn("Could not delete character teleport bookmark data: " + e.getMessage(), e);
            }

            sendPacket(new ExGetBookMarkInfoPacket(this));
        }
    }

    public void teleportBookmarkGo(int id) {
        if (!teleportBookmarkCondition(0)) {
            return;
        }
        if (inventory.getInventoryItemCount(13016, 0) == 0) {
            sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM);
            return;
        }
        final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
        sm.addItemName(13016);
        sendPacket(sm);

        final TeleportBookmark bookmark = _tpbookmarks.get(id);
        if (bookmark != null) {
            destroyItem("Consume", inventory.getItemByItemId(13016).getObjectId(), 1, null, false);
            teleToLocation(bookmark, false);
        }
        sendPacket(new ExGetBookMarkInfoPacket(this));
    }

    public boolean teleportBookmarkCondition(int type) {
        if (isInCombat()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
            return false;
        } else if (_isInSiege || (_siegeState != 0)) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_CLAN_HALL_SIEGE);
            return false;
        } else if (_isInDuel || _startingDuel) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
            return false;
        } else if (isFlying()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
            return false;
        } else if (_inOlympiadMode) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
            return false;
        } else if (hasBlockActions() && hasAbnormalType(AbnormalType.PARALYZE)) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_PETRIFIED_OR_PARALYZED_STATE);
            return false;
        } else if (isDead()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD);
            return false;
        } else if (isInWater()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
            return false;
        } else if ((type == 1) && (isInsideZone(ZoneType.SIEGE) || isInsideZone(ZoneType.CLAN_HALL) || isInsideZone(ZoneType.JAIL) || isInsideZone(ZoneType.CASTLE) || isInsideZone(ZoneType.NO_SUMMON_FRIEND) || isInsideZone(ZoneType.FORT))) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
            return false;
        } else if (isInsideZone(ZoneType.NO_BOOKMARK) || isInBoat()) {
            if (type == 0) {
                sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA);
            } else if (type == 1) {
                sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
            }
            return false;
        }
        /*
         * TODO: Instant Zone still not implemented else if (isInsideZone(ZoneId.INSTANT)) { sendPacket(SystemMessage.getSystemMessage(2357)); return; }
         */
        else {
            return true;
        }
    }

    public void teleportBookmarkAdd(int x, int y, int z, int icon, String tag, String name) {
        if (!teleportBookmarkCondition(1)) {
            return;
        }

        if (_tpbookmarks.size() >= _bookmarkslot) {
            sendPacket(SystemMessageId.YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION);
            return;
        }

        if (inventory.getInventoryItemCount(20033, 0) == 0) {
            sendPacket(SystemMessageId.YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG);
            return;
        }

        int id;
        for (id = 1; id <= _bookmarkslot; ++id) {
            if (!_tpbookmarks.containsKey(id)) {
                break;
            }
        }
        _tpbookmarks.put(id, new TeleportBookmark(id, x, y, z, icon, tag, name));

        destroyItem("Consume", inventory.getItemByItemId(20033).getObjectId(), 1, null, false);

        final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
        sm.addItemName(20033);
        sendPacket(sm);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_TP_BOOKMARK)) {
            statement.setInt(1, getObjectId());
            statement.setInt(2, id);
            statement.setInt(3, x);
            statement.setInt(4, y);
            statement.setInt(5, z);
            statement.setInt(6, icon);
            statement.setString(7, tag);
            statement.setString(8, name);
            statement.execute();
        } catch (Exception e) {
            LOGGER.warn("Could not insert character teleport bookmark data: " + e.getMessage(), e);
        }
        sendPacket(new ExGetBookMarkInfoPacket(this));
    }

    public void restoreTeleportBookmark() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_TP_BOOKMARK)) {
            statement.setInt(1, getObjectId());
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    _tpbookmarks.put(rset.getInt("Id"), new TeleportBookmark(rset.getInt("Id"), rset.getInt("x"), rset.getInt("y"), rset.getInt("z"), rset.getInt("icon"), rset.getString("tag"), rset.getString("name")));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed restoring character teleport bookmark.", e);
        }
    }

    @Override
    public void sendInfo(Player player) {
        if(!isInvisible() || player.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS)) {
            player.sendPacket(new ExCharInfo(this));
        }

        if (isInBoat() && isInvisible()) {
            setXYZ(getBoat().getLocation());
            player.sendPacket(new GetOnVehicle(getObjectId(), getBoat().getObjectId(), _inVehiclePosition));
        }

        final int relation1 = getRelation(player);
        final RelationChanged rc1 = new RelationChanged();
        rc1.addRelation(this, relation1, !isInsideZone(ZoneType.PEACE));
        if (hasSummon()) {
            if (pet != null) {
                rc1.addRelation(pet, relation1, !isInsideZone(ZoneType.PEACE));
            }
            if (hasServitors()) {
                getServitors().values().forEach(s -> rc1.addRelation(s, relation1, !isInsideZone(ZoneType.PEACE)));
            }
        }
        player.sendPacket(rc1);

        final int relation2 = player.getRelation(this);
        final RelationChanged rc2 = new RelationChanged();
        rc2.addRelation(player, relation2, !player.isInsideZone(ZoneType.PEACE));
        if (player.hasSummon()) {
            if (pet != null) {
                rc2.addRelation(pet, relation2, !player.isInsideZone(ZoneType.PEACE));
            }
            if (hasServitors()) {
                getServitors().values().forEach(s -> rc2.addRelation(s, relation2, !player.isInsideZone(ZoneType.PEACE)));
            }
        }
        sendPacket(rc2);

        switch (privateStoreType) {
            case SELL: {
                player.sendPacket(new PrivateStoreMsgSell(this));
                break;
            }
            case PACKAGE_SELL: {
                player.sendPacket(new ExPrivateStoreSetWholeMsg(this));
                break;
            }
            case BUY: {
                player.sendPacket(new PrivateStoreMsgBuy(this));
                break;
            }
            case MANUFACTURE: {
                player.sendPacket(new RecipeShopMsg(this));
                break;
            }
        }
    }

    public void playMovie(MovieHolder holder) {
        if (_movieHolder != null) {
            return;
        }
        abortAttack();
        // abortCast(); Confirmed in retail, playing a movie does not abort cast.
        stopMove(null);
        setMovieHolder(holder);
        if (!isTeleporting()) {
            sendPacket(new ExStartScenePlayer(holder.getMovie()));
        }
    }

    public void stopMovie() {
        sendPacket(new ExStopScenePlayer(_movieHolder.getMovie()));
        setMovieHolder(null);
    }

    public boolean isAllowedToEnchantSkills() {
        if (isSubClassLocked()) {
            return false;
        }
        if (isTransformed()) {
            return false;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(this)) {
            return false;
        }
        if (isCastingNow()) {
            return false;
        }

        return !isInBoat();
    }

    public int getBirthdays() {
        return (int) ChronoUnit.YEARS.between(data.getCreateDate(), LocalDate.now());
    }

    public IntSet getFriendList() {
        return friends;
    }

    public void restoreFriendList() {
        friends.clear();
        friends.addAll(getDAO(PlayerDAO.class).findFriendsById(getObjectId()));
    }

    public void notifyFriends(int type) {
        final FriendStatus pkt = new FriendStatus(this, type);
        var word = World.getInstance();
        friends.stream().mapToObj(word::findPlayer).filter(Objects::nonNull).forEach(pkt::sendTo);
    }

    /**
     * Verify if this player is in silence mode.
     *
     * @return the {@code true} if this player is in silence mode, {@code false} otherwise
     */
    public boolean isSilenceMode() {
        return _silenceMode;
    }

    /**
     * Set the silence mode.
     *
     * @param mode the value
     */
    public void setSilenceMode(boolean mode) {
        _silenceMode = mode;
        if (_silenceModeExcluded != null) {
            _silenceModeExcluded.clear(); // Clear the excluded list on each setSilenceMode
        }
        sendPacket(new EtcStatusUpdate(this));
    }

    /**
     * While at silenceMode, checks if this player blocks PMs for this user
     *
     * @param playerObjId the player object Id
     * @return {@code true} if the given Id is not excluded and this player is in silence mode, {@code false} otherwise
     */
    public boolean isSilenceMode(int playerObjId) {
        if (getSettings(ChatSettings.class).silenceModeExclude() && _silenceMode && nonNull(_silenceModeExcluded)) {
            return !_silenceModeExcluded.contains(playerObjId);
        }
        return _silenceMode;
    }

    /**
     * Add a player to the "excluded silence mode" list.
     *
     * @param playerObjId the player's object Id
     */
    public void addSilenceModeExcluded(int playerObjId) {
        if (_silenceModeExcluded == null) {
            _silenceModeExcluded = new ArrayList<>(1);
        }
        _silenceModeExcluded.add(playerObjId);
    }

    private void storeRecipeShopList() {
        if (hasManufactureShop()) {
            try (Connection con = DatabaseFactory.getInstance().getConnection()) {
                try (PreparedStatement st = con.prepareStatement(DELETE_CHAR_RECIPE_SHOP)) {
                    st.setInt(1, getObjectId());
                    st.execute();
                }

                try (PreparedStatement st = con.prepareStatement(INSERT_CHAR_RECIPE_SHOP)) {
                    final AtomicInteger slot = new AtomicInteger(1);
                    con.setAutoCommit(false);
                    for (ManufactureItem item : _manufactureItems.values()) {
                        st.setInt(1, getObjectId());
                        st.setInt(2, item.getRecipeId());
                        st.setLong(3, item.getCost());
                        st.setInt(4, slot.getAndIncrement());
                        st.addBatch();
                    }
                    st.executeBatch();
                    con.commit();
                }
            } catch (Exception e) {
                LOGGER.error("Could not store recipe shop for playerId " + getObjectId() + ": ", e);
            }
        }
    }

    private void restoreRecipeShopList() {
        if (_manufactureItems != null) {
            _manufactureItems.clear();
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_RECIPE_SHOP)) {
            statement.setInt(1, getObjectId());
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    getManufactureItems().put(rset.getInt("recipeId"), new ManufactureItem(rset.getInt("recipeId"), rset.getLong("price")));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Could not restore recipe shop list data for playerId: " + getObjectId(), e);
        }
    }

    @Override
    public double getCollisionRadius() {
        if (isMounted() && (_mountNpcId > 0)) {
            return NpcData.getInstance().getTemplate(getMountNpcId()).getfCollisionRadius();
        }

        final double defaultCollisionRadius = appearance.isFemale() ? getBaseTemplate().getFCollisionRadiusFemale() : getBaseTemplate().getfCollisionRadius();
        return getTransformation().map(transform -> transform.getCollisionRadius(this, defaultCollisionRadius)).orElse(defaultCollisionRadius);
    }

    @Override
    public double getCollisionHeight() {
        if (isMounted() && (_mountNpcId > 0)) {
            return NpcData.getInstance().getTemplate(getMountNpcId()).getfCollisionHeight();
        }

        final double defaultCollisionHeight = appearance.isFemale() ? getBaseTemplate().getFCollisionHeightFemale() : getBaseTemplate().getfCollisionHeight();
        return getTransformation().map(transform -> transform.getCollisionHeight(this, defaultCollisionHeight)).orElse(defaultCollisionHeight);
    }

    public final void setClientX(int val) {
        _clientX = val;
    }

    public final void setClientY(int val) {
        _clientY = val;
    }

    public final int getClientZ() {
        return _clientZ;
    }

    public final void setClientZ(int val) {
        _clientZ = val;
    }

    public final void setClientHeading(int val) {
        _clientHeading = val;
    }

    /**
     * @param z
     * @return true if character falling now on the start of fall return false for correct coord sync!
     */
    public final boolean isFalling(int z) {
        if (isDead() || isFlying() || isFlyingMounted() || isInsideZone(ZoneType.WATER)) {
            return false;
        }

        if (_fallingTimestamp > 0 && System.currentTimeMillis() < _fallingTimestamp) {
            return true;
        }

        final int deltaZ = getZ() - z;
        if (deltaZ <= getBaseTemplate().getSafeFallHeight()) {
            _fallingTimestamp = 0;
            return false;
        }

        // If there is no geodata loaded for the place we are client Z correction might cause falling damage.
        if (!GeoEngine.getInstance().hasGeo(getX(), getY())) {
            _fallingTimestamp = 0;
            return false;
        }

        if (_fallingDamage == 0) {
            _fallingDamage = (int) Formulas.calcFallDam(this, deltaZ);
        }
        if (_fallingDamageTask != null) {
            _fallingDamageTask.cancel(true);
        }
        _fallingDamageTask = ThreadPool.schedule(() ->
        {
            if ((_fallingDamage > 0) && !isInvul()) {
                reduceCurrentHp(min(_fallingDamage, getCurrentHp() - 1), this, null, false, true, false, false, DamageType.FALL);
                sendPacket(getSystemMessage(SystemMessageId.YOU_RECEIVED_S1_FALLING_DAMAGE).addInt(_fallingDamage));
            }
            _fallingDamage = 0;
            _fallingDamageTask = null;
        }, 1500);

        // Prevent falling under ground.
        sendPacket(new ValidateLocation(this));

        setFalling();

        return false;
    }

    /**
     * Set falling timestamp
     */
    public final void setFalling() {
        _fallingTimestamp = System.currentTimeMillis() + FALLING_VALIDATION_DELAY;
    }

    /**
     * @return the _movie
     */
    public MovieHolder getMovieHolder() {
        return _movieHolder;
    }

    public void setMovieHolder(MovieHolder movie) {
        _movieHolder = movie;
    }

    /**
     * Update last item auction request timestamp to current
     */
    public void updateLastItemAuctionRequest() {
        _lastItemAuctionInfoRequest = System.currentTimeMillis();
    }

    /**
     * @return true if receiving item auction requests<br>
     * (last request was in 2 seconds before)
     */
    public boolean isItemAuctionPolling() {
        return (System.currentTimeMillis() - _lastItemAuctionInfoRequest) < 2000;
    }

    @Override
    public boolean isMovementDisabled() {
        return super.isMovementDisabled() || (_movieHolder != null) || _fishing.isFishing();
    }

    public String getHtmlPrefix() {
        if (!Config.MULTILANG_ENABLE) {
            return null;
        }

        return _htmlPrefix;
    }

    public String getLang() {
        return _lang;
    }

    public boolean setLang(String lang) {
        boolean result = false;
        if (Config.MULTILANG_ENABLE) {
            if (Config.MULTILANG_ALLOWED.contains(lang)) {
                _lang = lang;
                result = true;
            } else {
                _lang = Config.MULTILANG_DEFAULT;
            }

            _htmlPrefix = _lang.equals("en") ? "" : "data/lang/" + _lang + "/";
        } else {
            _lang = null;
            _htmlPrefix = null;
        }

        return result;
    }

    public long getOfflineStartTime() {
        return _offlineShopStart;
    }

    public void setOfflineStartTime(long time) {
        _offlineShopStart = time;
    }

    public int getPcCafePoints() {
        return data.getPcCafePoints();
    }

    public void setPcCafePoints(int count) {
        data.setPcCafePoints(min(count, Config.PC_CAFE_MAX_POINTS));
    }

    /**
     * Check all player skills for skill level. If player level is lower than skill learn level - 9, skill level is decreased to next possible level.
     */
    public void checkPlayerSkills() {
        SkillLearn learn;
        for (Entry<Integer, Skill> e : getSkills().entrySet()) {
            learn = SkillTreesData.getInstance().getClassSkill(e.getKey(), e.getValue().getLevel() % 100, getClassId());
            if (learn != null) {
                final int lvlDiff = e.getKey() == CommonSkill.EXPERTISE.getId() ? 0 : 9;
                if (getLevel() < (learn.getGetLevel() - lvlDiff)) {
                    deacreaseSkillLevel(e.getValue(), lvlDiff);
                }
            }
        }
    }

    private void deacreaseSkillLevel(Skill skill, int lvlDiff) {
        int nextLevel = -1;
        final var skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(getClassId());
        for (SkillLearn sl : skillTree.values()) {
            if ((sl.getSkillId() == skill.getId()) && (nextLevel < sl.getSkillLevel()) && (getLevel() >= (sl.getGetLevel() - lvlDiff))) {
                nextLevel = sl.getSkillLevel(); // next possible skill level
            }
        }

        if (nextLevel == -1) {
            LOGGER.info("Removing skill " + skill + " from player " + toString());
            removeSkill(skill, true); // there is no lower skill
        } else {
            LOGGER.info("Decreasing skill " + skill + " to " + nextLevel + " for player " + toString());
            addSkill(SkillEngine.getInstance().getSkill(skill.getId(), nextLevel), true); // replace with lower one
        }
    }

    public boolean canMakeSocialAction() {
        return ((privateStoreType == PrivateStoreType.NONE) && (getActiveRequester() == null) && !isAlikeDead() && !isAllSkillsDisabled() && !isCastingNow() && (getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE));
    }

    public void setMultiSocialAction(int id, int targetId) {
        _multiSociaAction = id;
        _multiSocialTarget = targetId;
    }

    public int getMultiSociaAction() {
        return _multiSociaAction;
    }

    public int getMultiSocialTarget() {
        return _multiSocialTarget;
    }

    public Collection<TeleportBookmark> getTeleportBookmarks() {
        return _tpbookmarks.values();
    }

    public int getBookmarkslot() {
        return _bookmarkslot;
    }

    public int getQuestInventoryLimit() {
        return Config.INVENTORY_MAXIMUM_QUEST_ITEMS;
    }

    public boolean canAttackCharacter(Creature cha) {
        if (GameUtils.isAttackable(cha)) {
            return true;
        } else if (GameUtils.isPlayable(cha)) {
            if (cha.isInsideZone(ZoneType.PVP) && !cha.isInsideZone(ZoneType.SIEGE)) {
                return true;
            }

            final Player target =  GameUtils.isSummon( cha)  ? ((Summon) cha).getOwner() : (Player) cha;

            if (isInDuel() && target.isInDuel() && (target.getDuelId() == getDuelId())) {
                return true;
            } else if (isInParty() && target.isInParty()) {
                if (getParty() == target.getParty()) {
                    return false;
                }
                if (((getParty().getCommandChannel() != null) || (target.getParty().getCommandChannel() != null)) && (getParty().getCommandChannel() == target.getParty().getCommandChannel())) {
                    return false;
                }
            } else if ((getClan() != null) && (target.getClan() != null)) {
                if (getClanId() == target.getClanId()) {
                    return false;
                }
                if (((getAllyId() > 0) || (target.getAllyId() > 0)) && (getAllyId() == target.getAllyId())) {
                    return false;
                }
                if (getClan().isAtWarWith(target.getClan().getId()) && target.getClan().isAtWarWith(getClan().getId())) {
                    return true;
                }
            } else if ((getClan() == null) || (target.getClan() == null)) {
                if ((target.getPvpFlag() == 0) && (target.getReputation() >= 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Test if player inventory is under 90% capacity
     *
     * @param includeQuestInv check also quest inventory
     * @return
     */
    public boolean isInventoryUnder90(boolean includeQuestInv) {
        return (inventory.getSize(item -> !item.isQuestItem() || includeQuestInv) <= (getInventoryLimit() * 0.9));
    }

    /**
     * Test if player inventory is under 80% capacity
     *
     * @param includeQuestInv check also quest inventory
     * @return
     */
    public boolean isInventoryUnder80(boolean includeQuestInv) {
        return (inventory.getSize(item -> !item.isQuestItem() || includeQuestInv) <= (getInventoryLimit() * 0.8));
    }

    public boolean havePetInvItems() {
        return _petItems;
    }

    public void setPetInvItems(boolean haveit) {
        _petItems = haveit;
    }

    /**
     * Restore Pet's inventory items from database.
     */
    private void restorePetInventoryItems() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT object_id FROM `items` WHERE `owner_id`=? AND (`loc`='PET' OR `loc`='PET_EQUIP') LIMIT 1;")) {
            statement.setInt(1, getObjectId());
            try (ResultSet rset = statement.executeQuery()) {
                setPetInvItems(rset.next() && (rset.getInt("object_id") > 0));
            }
        } catch (Exception e) {
            LOGGER.error("Could not check Items in Pet Inventory for playerId: " + getObjectId(), e);
        }
    }

    public String getAdminConfirmCmd() {
        return _adminConfirmCmd;
    }

    public void setAdminConfirmCmd(String adminConfirmCmd) {
        _adminConfirmCmd = adminConfirmCmd;
    }

    public int getBlockCheckerArena() {
        return _handysBlockCheckerEventArena;
    }

    public void setBlockCheckerArena(byte arena) {
        _handysBlockCheckerEventArena = arena;
    }

    /**
     * Load Player Recommendations data.
     */
    private void loadRecommendations() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT rec_have, rec_left FROM character_reco_bonus WHERE charId = ?")) {
            statement.setInt(1, getObjectId());
            try (ResultSet rset = statement.executeQuery()) {
                if (rset.next()) {
                    setRecomHave(rset.getInt("rec_have"));
                    setRecomLeft(rset.getInt("rec_left"));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Could not restore Recommendations for player: " + getObjectId(), e);
        }
    }

    /**
     * Update Player Recommendations data.
     */
    public void storeRecommendations() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("REPLACE INTO character_reco_bonus (charId,rec_have,rec_left,time_left) VALUES (?,?,?,?)")) {
            ps.setInt(1, getObjectId());
            ps.setInt(2, _recomHave);
            ps.setInt(3, _recomLeft);
            ps.setLong(4, 0);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Could not update Recommendations for player: " + getObjectId(), e);
        }
    }

    public void startRecoGiveTask() {
        // Create task to give new recommendations
        _recoGiveTask = ThreadPool.scheduleAtFixedRate(new RecoGiveTask(this), 7200000, 3600000);

        // Store new data
        storeRecommendations();
    }

    public void stopRecoGiveTask() {
        if (_recoGiveTask != null) {
            _recoGiveTask.cancel(false);
            _recoGiveTask = null;
        }
    }

    public boolean isRecoTwoHoursGiven() {
        return _recoTwoHoursGiven;
    }

    public void setRecoTwoHoursGiven(boolean val) {
        _recoTwoHoursGiven = val;
    }

    public String getLastPetitionGmName() {
        return _lastPetitionGmName;
    }

    public void setLastPetitionGmName(String gmName) {
        _lastPetitionGmName = gmName;
    }

    public ContactList getContactList() {
        return _contactList;
    }

    public void setEventStatus() {
        eventStatus = new PlayerEventHolder(this);
    }

    public PlayerEventHolder getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(PlayerEventHolder pes) {
        eventStatus = pes;
    }

    public long getNotMoveUntil() {
        return _notMoveUntil;
    }

    public void updateNotMoveUntil() {
        _notMoveUntil = System.currentTimeMillis() + Config.PLAYER_MOVEMENT_BLOCK_TIME;
    }

    /**
     * @param skillId the display skill Id
     * @return the custom skill
     */
    public final Skill getCustomSkill(int skillId) {
        return (_customSkills != null) ? _customSkills.get(skillId) : null;
    }

    /**
     * Add a skill level to the custom skills map.
     *
     * @param skill the skill to add
     */
    private void addCustomSkill(Skill skill) {
        if ((skill != null) && (skill.getDisplayId() != skill.getId())) {
            if (_customSkills == null) {
                _customSkills = new ConcurrentSkipListMap<>();
            }
            _customSkills.put(skill.getDisplayId(), skill);
        }
    }

    /**
     * Remove a skill level from the custom skill map.
     *
     * @param skill the skill to remove
     */
    private void removeCustomSkill(Skill skill) {
        if ((skill != null) && (_customSkills != null) && (skill.getDisplayId() != skill.getId())) {
            _customSkills.remove(skill.getDisplayId());
        }
    }

    /**
     * @return {@code true} if current player can revive and shows 'To Village' button upon death, {@code false} otherwise.
     */
    @Override
    public boolean canRevive() {
        if (_events != null) {
            for (AbstractEvent<?> listener : _events.values()) {
                if (listener.isOnEvent(this) && !listener.canRevive(this)) {
                    return false;
                }
            }
        }
        return _canRevive;
    }

    /**
     * This method can prevent from displaying 'To Village' button upon death.
     *
     * @param val
     */
    @Override
    public void setCanRevive(boolean val) {
        _canRevive = val;
    }

    public boolean isOnCustomEvent() {
        return _isOnCustomEvent;
    }

    public void setOnCustomEvent(boolean value) {
        _isOnCustomEvent = value;
    }

    /**
     * @return {@code true} if player is on event, {@code false} otherwise.
     */
    @Override
    public boolean isOnEvent() {
        if (_isOnCustomEvent) {
            return true;
        }
        if (_events != null) {
            for (AbstractEvent<?> listener : _events.values()) {
                if (listener.isOnEvent(this)) {
                    return true;
                }
            }
        }
        return super.isOnEvent();
    }

    public boolean isBlockedFromExit() {
        if (_isOnCustomEvent) {
            return true;
        }
        if (_events != null) {
            for (AbstractEvent<?> listener : _events.values()) {
                if (listener.isOnEvent(this) && listener.isBlockingExit(this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBlockedFromDeathPenalty() {
        if (_isOnCustomEvent) {
            return true;
        }
        if (_events != null) {
            for (AbstractEvent<?> listener : _events.values()) {
                if (listener.isOnEvent(this) && listener.isBlockingDeathPenalty(this)) {
                    return true;
                }
            }
        }
        return isAffected(EffectFlag.PROTECT_DEATH_PENALTY);
    }

    public void setOriginalCpHpMp(double cp, double hp, double mp) {
        _originalCp = cp;
        _originalHp = hp;
        _originalMp = mp;
    }

    @Override
    public void addOverrideCond(PcCondOverride... excs) {
        super.addOverrideCond(excs);
        getVariables().set(COND_OVERRIDE_KEY, Long.toString(_exceptions));
    }

    @Override
    public void removeOverridedCond(PcCondOverride... excs) {
        super.removeOverridedCond(excs);
        getVariables().set(COND_OVERRIDE_KEY, Long.toString(_exceptions));
    }

    /**
     * @return {@code true} if {@link PlayerVariables} instance is attached to current player's scripts, {@code false} otherwise.
     */
    public boolean hasVariables() {
        return getScript(PlayerVariables.class) != null;
    }

    /**
     * @return {@link PlayerVariables} instance containing parameters regarding player.
     */
    public PlayerVariables getVariables() {
        final PlayerVariables vars = getScript(PlayerVariables.class);
        return vars != null ? vars : addScript(new PlayerVariables(getObjectId()));
    }

    /**
     * @return {@code true} if {@link AccountVariables} instance is attached to current player's scripts, {@code false} otherwise.
     */
    public boolean hasAccountVariables() {
        return getScript(AccountVariables.class) != null;
    }

    /**
     * @return {@link AccountVariables} instance containing parameters regarding player.
     */
    public AccountVariables getAccountVariables() {
        final AccountVariables vars = getScript(AccountVariables.class);
        return vars != null ? vars : addScript(new AccountVariables(getAccountName()));
    }

    @Override
    public int getId() {
        return objectId;
    }

    public boolean isPartyBanned() {
        return PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN);
    }

    /**
     * @param act
     * @return {@code true} if action was added successfully, {@code false} otherwise.
     */
    public boolean addAction(PlayerAction act) {
        if (!hasAction(act)) {
            _actionMask |= act.getMask();
            return true;
        }
        return false;
    }

    /**
     * @param act
     * @return {@code true} if action was removed successfully, {@code false} otherwise.
     */
    public boolean removeAction(PlayerAction act) {
        if (hasAction(act)) {
            _actionMask &= ~act.getMask();
            return true;
        }
        return false;
    }

    /**
     * @param act
     * @return {@code true} if action is present, {@code false} otherwise.
     */
    public boolean hasAction(PlayerAction act) {
        return (_actionMask & act.getMask()) == act.getMask();
    }

    /**
     * Set true/false if character got Charm of Courage
     *
     * @param val true/false
     */
    public void setCharmOfCourage(boolean val) {
        hasCharmOfCourage = val;
    }

    /**
     * @return {@code true} if effect is present, {@code false} otherwise.
     */
    public boolean hasCharmOfCourage() {
        return hasCharmOfCourage;

    }

    /**
     * @param target the target
     * @return {@code true} if this player got war with the target, {@code false} otherwise.
     */
    public boolean atWarWith(Playable target) {
        if (target == null) {
            return false;
        }
        if ((_clan != null) && !isAcademyMember()) // Current player
        {
            if ((target.getClan() != null) && !target.isAcademyMember()) // Target player
            {
                return _clan.isAtWarWith(target.getClan());
            }
        }
        return false;
    }

    /**
     * @return the beauty shop hair, or his normal if not changed.
     */
    public int getVisualHair() {
        return getVariables().getInt("visualHairId", appearance.getHairStyle());
    }

    /**
     * Sets the beauty shop hair
     *
     * @param hairId
     */
    public void setVisualHair(int hairId) {
        getVariables().set("visualHairId", hairId);
    }

    /**
     * @return the beauty shop hair color, or his normal if not changed.
     */
    public int getVisualHairColor() {
        return getVariables().getInt("visualHairColorId", appearance.getHairColor());
    }

    /**
     * Sets the beauty shop hair color
     *
     * @param colorId
     */
    public void setVisualHairColor(int colorId) {
        getVariables().set("visualHairColorId", colorId);
    }

    /**
     * @return the beauty shop modified face, or his normal if not changed.
     */
    public int getVisualFace() {
        return getVariables().getInt("visualFaceId", appearance.getFace());
    }

    /**
     * Sets the beauty shop modified face
     *
     * @param faceId
     */
    public void setVisualFace(int faceId) {
        getVariables().set("visualFaceId", faceId);
    }

    /**
     * @return {@code true} if player has mentees, {@code false} otherwise
     */
    public boolean isMentor() {
        return MentorManager.getInstance().isMentor(getObjectId());
    }

    /**
     * @return {@code true} if player has mentor, {@code false} otherwise
     */
    public boolean isMentee() {
        return MentorManager.getInstance().isMentee(getObjectId());
    }

    /**
     * @return how much ability points player has spend on learning skills.
     */
    public int getAbilityPointsUsed() {
        return getVariables().getInt(isDualClassActive() ? PlayerVariables.ABILITY_POINTS_USED_DUAL_CLASS : PlayerVariables.ABILITY_POINTS_USED_MAIN_CLASS, 0);
    }

    /**
     * @return The amount of times player can use world chat
     */
    public int getWorldChatPoints() {
        return (int) getStats().getValue(Stat.WORLD_CHAT_POINTS, Config.WORLD_CHAT_POINTS_PER_DAY);
    }

    /**
     * @return The amount of times player has used world chat
     */
    public int getWorldChatUsed() {
        return getVariables().getInt(PlayerVariables.WORLD_CHAT_VARIABLE_NAME, 0);
    }

    /**
     * Sets the amount of times player can use world chat
     *
     * @param timesUsed how many times world chat has been used up until now.
     */
    public void setWorldChatUsed(int timesUsed) {
        getVariables().set(PlayerVariables.WORLD_CHAT_VARIABLE_NAME, timesUsed);
    }

    /**
     * @return Side of the player.
     */
    public CastleSide getPlayerSide() {
        if ((_clan == null) || (_clan.getCastleId() == 0)) {
            return CastleSide.NEUTRAL;
        }
        return CastleManager.getInstance().getCastleById(getClan().getCastleId()).getSide();
    }

    /**
     * @return {@code true} if player is on Dark side, {@code false} otherwise.
     */
    public boolean isOnDarkSide() {
        return getPlayerSide() == CastleSide.DARK;
    }

    /**
     * @return {@code true} if player is on Light side, {@code false} otherwise.
     */
    public boolean isOnLightSide() {
        return getPlayerSide() == CastleSide.LIGHT;
    }

    /**
     * @return the maximum amount of points that player can use
     */
    public int getMaxSummonPoints() {
        return (int) getStats().getValue(Stat.MAX_SUMMON_POINTS, 0);
    }

    /**
     * @return the amount of points that player used
     */
    public int getSummonPoints() {
        return getServitors().values().stream().mapToInt(Summon::getSummonPoints).sum();
    }

    /**
     * @param request
     * @return {@code true} if the request was registered successfully, {@code false} otherwise.
     */
    public boolean addRequest(AbstractRequest request) {
        if (requests == null) {
            synchronized (this) {
                if (requests == null) {
                    requests = new ConcurrentHashMap<>();
                }
            }
        }
        return canRequest(request) && (requests.putIfAbsent(request.getClass(), request) == null);
    }

    public boolean canRequest(AbstractRequest request) {
        return (requests != null) && requests.values().stream().allMatch(request::canWorkWith);
    }

    /**
     * @param clazz
     * @return {@code true} if request was successfully removed, {@code false} in case processing set is not created or not containing the request.
     */
    public boolean removeRequest(Class<? extends AbstractRequest> clazz) {
        return (requests != null) && (requests.remove(clazz) != null);
    }

    /**
     * @param <T>
     * @param requestClass
     * @return object that is instance of {@code requestClass} param, {@code null} if not instance or not set.
     */
    public <T extends AbstractRequest> T getRequest(Class<T> requestClass) {
        return requests != null ? requestClass.cast(requests.get(requestClass)) : null;
    }

    /**
     * @return {@code true} if player has any processing request set, {@code false} otherwise.
     */
    public boolean hasRequests() {
        return (requests != null) && !requests.isEmpty();
    }

    public boolean hasItemRequest() {
        return nonNull(requests) && requests.values().stream().anyMatch(AbstractRequest::isItemRequest);
    }

    /**
     * @param requestClass
     * @param classes
     * @return {@code true} if player has the provided request and processing it, {@code false} otherwise.
     */
    @SafeVarargs
    public final boolean hasRequest(Class<? extends AbstractRequest> requestClass, Class<? extends AbstractRequest>... classes) {
        if (requests != null) {
            for (Class<? extends AbstractRequest> clazz : classes) {
                if (requests.containsKey(clazz)) {
                    return true;
                }
            }
            return requests.containsKey(requestClass);
        }
        return false;
    }

    /**
     * @param objectId
     * @return {@code true} if item object id is currently in use by some request, {@code false} otherwise.
     */
    public boolean isProcessingItem(int objectId) {
        return nonNull(requests) && requests.values().stream().anyMatch(req -> req.isUsing(objectId));
    }

    /**
     * Removing all requests associated with the item object id provided.
     *
     * @param objectId
     */
    public void removeRequestsThatProcessesItem(int objectId) {
        if (requests != null) {
            requests.values().removeIf(req -> req.isUsing(objectId));
        }
    }

    /**
     * Gets the last commission infos.
     *
     * @return the last commission infos
     */
    public Map<Integer, ExResponseCommissionInfo> getLastCommissionInfos() {
        if (_lastCommissionInfos == null) {
            synchronized (this) {
                if (_lastCommissionInfos == null) {
                    _lastCommissionInfos = new ConcurrentHashMap<>();
                }
            }
        }
        return _lastCommissionInfos;
    }

    /**
     * Gets the whisperers.
     *
     * @return the whisperers
     */
    public Set<Integer> getWhisperers() {
        return _whisperers;
    }

    public MatchingRoom getMatchingRoom() {
        return _matchingRoom;
    }

    public void setMatchingRoom(MatchingRoom matchingRoom) {
        _matchingRoom = matchingRoom;
    }

    public boolean isInMatchingRoom() {
        return _matchingRoom != null;
    }

    public int getVitalityItemsUsed() {
        return getVariables().getInt(PlayerVariables.VITALITY_ITEMS_USED_VARIABLE_NAME, 0);
    }

    public void setVitalityItemsUsed(int used) {
        final PlayerVariables vars = getVariables();
        vars.set(PlayerVariables.VITALITY_ITEMS_USED_VARIABLE_NAME, used);
        vars.storeMe();
    }

    @Override
    public boolean isVisibleFor(Player player) {
        return (super.isVisibleFor(player) || ((player.getParty() != null) && (player.getParty() == getParty())));
    }

    /**
     * Gets the Quest zone ID.
     *
     * @return int the quest zone ID
     */
    public int getQuestZoneId() {
        return _questZoneId;
    }

    /**
     * Set the Quest zone ID.
     *
     * @param id the quest zone ID
     */
    public void setQuestZoneId(int id) {
        _questZoneId = id;
    }

    public void sendInventoryUpdate(InventoryUpdate iu) {
        sendPacket(iu, new ExAdenaInvenCount(this), new ExBloodyCoinCount(), new ExUserInfoInvenWeight(this));
    }

    public void sendItemList() {
        ItemList.sendList(this);
        sendPacket(new ExQuestItemList(1, this));
        sendPacket(new ExQuestItemList(2, this));
        sendPacket(new ExAdenaInvenCount(this));
        sendPacket(new ExUserInfoInvenWeight(this));
        sendPacket(new ExBloodyCoinCount());
    }

    /**
     * @param event
     * @return {@code true} if event is successfuly registered, {@code false} in case events map is not initialized yet or event is not registered
     */
    public boolean registerOnEvent(AbstractEvent<?> event) {
        if (_events == null) {
            synchronized (this) {
                if (_events == null) {
                    _events = new ConcurrentHashMap<>();
                }
            }
        }
        return _events.putIfAbsent(event.getClass(), event) == null;
    }

    /**
     * @param event
     * @return {@code true} if event is successfuly removed, {@code false} in case events map is not initialized yet or event is not registered
     */
    public boolean removeFromEvent(AbstractEvent<?> event) {
        if (_events == null) {
            return false;
        }
        return _events.remove(event.getClass()) != null;
    }

    /**
     * @param <T>
     * @param clazz
     * @return the event instance or null in case events map is not initialized yet or event is not registered
     */
    public <T extends AbstractEvent<?>> T getEvent(Class<T> clazz) {
        if (_events == null) {
            return null;
        }

        return _events.values().stream().filter(event -> clazz.isAssignableFrom(event.getClass())).map(clazz::cast).findFirst().orElse(null);
    }

    /**
     * @return the first event that player participates on or null if he doesn't
     */
    public AbstractEvent<?> getEvent() {
        if (_events == null) {
            return null;
        }

        return _events.values().stream().findFirst().orElse(null);
    }

    /**
     * @param clazz
     * @return {@code true} if player is registered on specified event, {@code false} in case events map is not initialized yet or event is not registered
     */
    public boolean isOnEvent(Class<? extends AbstractEvent<?>> clazz) {
        if (_events == null) {
            return false;
        }

        return _events.containsKey(clazz);
    }

    public Fishing getFishing() {
        return _fishing;
    }

    public boolean isFishing() {
        return _fishing.isFishing();
    }

    @Override
    public MoveType getMoveType() {
        if (_waitTypeSitting) {
            return MoveType.SITTING;
        }
        return super.getMoveType();
    }

    private void startOnlineTimeUpdateTask() {
        if (_onlineTimeUpdateTask != null) {
            stopOnlineTimeUpdateTask();
        }

        _onlineTimeUpdateTask = ThreadPool.scheduleAtFixedRate(this::updateOnlineTime, 60 * 1000, 60 * 1000);
    }

    private void updateOnlineTime() {
        if (_clan != null) {
            _clan.addMemberOnlineTime(this);
        }
    }

    private void stopOnlineTimeUpdateTask() {
        if (_onlineTimeUpdateTask != null) {
            _onlineTimeUpdateTask.cancel(true);
            _onlineTimeUpdateTask = null;
        }
    }

    public GroupType getGroupType() {
        return isInParty() ? (_party.isInCommandChannel() ? GroupType.COMMAND_CHANNEL : GroupType.PARTY) : GroupType.NONE;
    }

    @Override
    protected void initStatusUpdateCache() {
        super.initStatusUpdateCache();
        addStatusUpdateValue(StatusUpdateType.LEVEL);
        addStatusUpdateValue(StatusUpdateType.MAX_CP);
        addStatusUpdateValue(StatusUpdateType.CUR_CP);
    }

    public TrainingHolder getTraingCampInfo() {
        final String info = getAccountVariables().getString(TRAINING_CAMP_VAR, null);
        if (info == null) {
            return null;
        }
        return new TrainingHolder(Integer.parseInt(info.split(";")[0]), Integer.parseInt(info.split(";")[1]), Integer.parseInt(info.split(";")[2]), Long.parseLong(info.split(";")[3]), Long.parseLong(info.split(";")[4]));
    }

    public void setTraingCampInfo(TrainingHolder holder) {
        getAccountVariables().set(TRAINING_CAMP_VAR, holder.getObjectId() + ";" + holder.getClassIndex() + ";" + holder.getLevel() + ";" + holder.getStartTime() + ";" + holder.getEndTime());
    }

    public void removeTraingCampInfo() {
        getAccountVariables().remove(TRAINING_CAMP_VAR);
    }

    public long getTraingCampDuration() {
        return getAccountVariables().getLong(TRAINING_CAMP_DURATION, 0);
    }

    public void setTraingCampDuration(long duration) {
        getAccountVariables().set(TRAINING_CAMP_DURATION, duration);
    }

    public void resetTraingCampDuration() {
        getAccountVariables().remove(TRAINING_CAMP_DURATION);
    }

    public boolean isInTraingCamp() {
        return falseIfNullOrElse(getTraingCampInfo(), t -> t.getEndTime() > System.currentTimeMillis());
    }

    public AttendanceInfoHolder getAttendanceInfo() {
        // Get reset time.
        final Calendar calendar = Calendar.getInstance();
        if ((calendar.get(Calendar.HOUR_OF_DAY) < 6) && (calendar.get(Calendar.MINUTE) < 30)) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get last player reward time.
        final long receiveDate;
        int rewardIndex;
        if (getSettings(AttendanceSettings.class).shareAccount()) {
            receiveDate = getAccountVariables().getLong(ATTENDANCE_DATE_VAR, 0);
            rewardIndex = getAccountVariables().getInt(ATTENDANCE_INDEX_VAR, 0);
        } else {
            receiveDate = getVariables().getLong(ATTENDANCE_DATE_VAR, 0);
            rewardIndex = getVariables().getInt(ATTENDANCE_INDEX_VAR, 0);
        }

        // Check if player can receive reward today.
        boolean canBeRewarded = false;
        if (calendar.getTimeInMillis() > receiveDate) {
            canBeRewarded = true;
            // Reset index if max is reached.
            if (rewardIndex >= (AttendanceRewardData.getInstance().getRewardsCount() - 1)) {
                rewardIndex = 0;
            }
        }

        return new AttendanceInfoHolder(rewardIndex, canBeRewarded);
    }

    public void setAttendanceInfo(int rewardIndex) {

        final Calendar nextReward = Calendar.getInstance();
        nextReward.set(Calendar.MINUTE, 30);
        if (nextReward.get(Calendar.HOUR_OF_DAY) >= 6)
        {
            nextReward.add(Calendar.DATE, 1);
        }
        nextReward.set(Calendar.HOUR_OF_DAY, 6);

        if (getSettings(AttendanceSettings.class).shareAccount()) {
            getAccountVariables().set(ATTENDANCE_DATE_VAR, nextReward.getTimeInMillis());
            getAccountVariables().set(ATTENDANCE_INDEX_VAR, rewardIndex);
        } else {
            getVariables().set(ATTENDANCE_DATE_VAR, nextReward.getTimeInMillis());
            getVariables().set(ATTENDANCE_INDEX_VAR, rewardIndex);
        }
    }

    public boolean isFriend(Player player) {
        return friends.contains(player.getObjectId());
    }

    public boolean isInSameClan(Player player) {
        return clanId > 0 && clanId == player.getClanId();
    }

    public boolean isInSameAlly(Player player) {
        var ally = getAllyId();
        return ally > 0 && player.getAllyId() == ally;
    }

    public boolean hasMentorRelationship(Player player) {
        return nonNull(MentorManager.getInstance().getMentee(objectId, player.getObjectId())) || nonNull(MentorManager.getInstance().getMentee(player.getObjectId(), objectId));
    }

    public boolean isSiegeFriend(WorldObject target)
    {
        // If i'm natural or not in siege zone, not friends.
        if ((_siegeState == 0) || !isInsideZone(ZoneType.SIEGE))
        {
            return false;
        }

        // If target isn't a player, is self, isn't on same siege or not on same state, not friends.
        var targetPlayer = target.getActingPlayer();
        if ((targetPlayer == null) || (targetPlayer == this) || (targetPlayer.getSiegeSide() != _siegeSide) || (_siegeState != targetPlayer.getSiegeState()))
        {
            return false;
        }

        // Attackers are considered friends only if castle has no owner.
        if (_siegeState == 1)
        {
            final Castle castle = CastleManager.getInstance().getCastleById(_siegeSide);
            if (castle == null)
            {
                return false;
            }
            return castle.getOwner() == null;
        }

        // Both are defenders, friends.
        return true;
    }
    public boolean isInTimedHuntingZone()
    {
        return isInTimedHuntingZone(2); // Storm Isle
    }

    public boolean isInTimedHuntingZone(int zoneId)
    {
        final int x = ((getX() - World.MAP_MIN_X) >> 15) + World.TILE_X_MIN;
        final int y = ((getY() - World.MAP_MIN_Y) >> 15) + World.TILE_Y_MIN;

        switch (zoneId)
        {
            case 2: // Ancient Pirates' Tomb.
            {
                return (x == 20) && (y == 15);
            }
        }
        return false;
    }

    public void startTimedHuntingZone(int zoneId, long delay)
    {
        // Stop previous task.
        stopTimedHuntingZoneTask();

        // TODO: Delay window.
        // sendPacket(new TimedHuntingZoneEnter((int) (delay / 60 / 1000)));
        sendMessage("You have " + (delay / 60 / 1000) + " minutes left for this timed zone.");
        _timedHuntingZoneFinishTask = ThreadPool.schedule(() ->
        {
            if ((isOnlineInt() > 0) && isInTimedHuntingZone(zoneId))
            {
                sendPacket(TimedHuntingZoneExit.STATIC_PACKET);
                abortCast();
                stopMove(null);
                teleToLocation(MapRegionManager.getInstance().getTeleToLocation(this, TeleportWhereType.TOWN));
            }
        }, delay);
    }

    public void stopTimedHuntingZoneTask()
    {
        if ((_timedHuntingZoneFinishTask != null) && !_timedHuntingZoneFinishTask.isCancelled() && !_timedHuntingZoneFinishTask.isDone())
        {
            _timedHuntingZoneFinishTask.cancel(true);
            _timedHuntingZoneFinishTask = null;
        }
        sendPacket(TimedHuntingZoneExit.STATIC_PACKET);
    }

    public long getTimedHuntingZoneRemainingTime()
    {
        if ((_timedHuntingZoneFinishTask != null) && !_timedHuntingZoneFinishTask.isCancelled() && !_timedHuntingZoneFinishTask.isDone())
        {
            return _timedHuntingZoneFinishTask.getDelay(TimeUnit.MILLISECONDS);
        }
        return 0;
    }
}
