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
package org.l2j.gameserver.model.actor.instance;

import io.github.joealisson.primitive.*;
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
import org.l2j.gameserver.data.database.dao.*;
import org.l2j.gameserver.data.database.data.*;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.gameserver.data.xml.impl.*;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.engine.autoplay.AutoPlaySettings;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.item.*;
import org.l2j.gameserver.engine.item.shop.multisell.PreparedMultisellList;
import org.l2j.gameserver.engine.olympiad.OlympiadMode;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.enums.*;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.DamageInfo.DamageType;
import org.l2j.gameserver.model.actor.*;
import org.l2j.gameserver.model.actor.appearance.Appearance;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.actor.status.PlayerStatus;
import org.l2j.gameserver.model.actor.tasks.character.NotifyAITask;
import org.l2j.gameserver.model.actor.tasks.player.*;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.SocialStatus;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Duel;
import org.l2j.gameserver.model.entity.Event;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.*;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.holders.*;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.*;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.model.item.container.*;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.MoveType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.ChangeAccessLevel;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionInfo;
import org.l2j.gameserver.network.serverpackets.friend.FriendStatus;
import org.l2j.gameserver.network.serverpackets.html.AbstractHtmlPacket;
import org.l2j.gameserver.network.serverpackets.item.ItemList;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.pvpbook.ExNewPk;
import org.l2j.gameserver.network.serverpackets.sessionzones.TimedHuntingZoneExit;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.settings.FeatureSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.taskmanager.SaveTaskManager;
import org.l2j.gameserver.util.*;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.WaterZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.*;
import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static org.l2j.gameserver.model.item.BodyPart.*;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * This class represents all player characters in the world.<br>
 * There is always a client-thread connected to this.
 *
 * @author JoeAlisson
 * && fixes by Bru7aLMike
 */
public final class Player extends Playable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    private final GameClient client;
    private final PlayerData data;
    private final Appearance appearance;
    private final AccountData account;
    private final ContactList contacts = new ContactList(this);
    private final IntMap<String> accountPlayers = new HashIntMap<>();
    private final IntMap<RecipeList> dwarvenRecipes = new CHashIntMap<>();
    private final IntMap<RecipeList> commonRecipes = new CHashIntMap<>();
    private final Map<ShotType, Integer> activeSoulShots = new EnumMap<>(ShotType.class);
    private final LimitedQueue<DamageInfo> lastDamages = new LimitedQueue<>(30);

    private IntSet teleportFavorites;
    private ElementalType activeElementalSpiritType;
    private ElementalSpirit[] spirits;
    private PlayerStatsData statsData;
    private AutoPlaySettings autoPlaySettings;
    private PlayerVariableData variables;
    private ScheduledFuture<?> _timedHuntingZoneFinishTask;
    private IntMap<CostumeData> costumes = Containers.emptyIntMap();
    private CostumeCollectionData activeCostumesCollection;
    private IntMap<CostumeCollectionData> costumesCollections = Containers.emptyIntMap();

    private int rank;
    private int rankRace;
    private int additionalSoulshot;
    private byte vipTier;
    private byte shineSouls;
    private byte shadowSouls;

    Player(GameClient client, PlayerData playerData, PlayerTemplate template) {
        super(playerData.getCharId(), template);
        this.data = playerData;
        this.client = client;
        this.account = client.getAccount();
        setName(playerData.getName());
        setInstanceType(InstanceType.L2PcInstance);
        initCharStatusUpdateValues();

        appearance = new Appearance(this, playerData);

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

    public double getElementalSpiritCriticRate() {
        return getStats().getElementalSpiritCriticalRate(zeroIfNullOrElse(getElementalSpirit(activeElementalSpiritType), ElementalSpirit::getCriticalRate));
    }

    public double getElementalSpiritCriticDamage() {
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
        return account.getVipPoints();
    }

    public void updateVipPoints(long points) {
        if(points == 0) {
            return;
        }
        var currentVipTier = VipEngine.getInstance().getVipTier(getVipPoints());
        account.updateVipPoints(points);
        var newTier = VipEngine.getInstance().getVipTier(getVipPoints());
        if(newTier != currentVipTier) {
            vipTier = newTier;
            if(newTier > 0) {
                account.setVipTierExpiration(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());
                VipEngine.getInstance().manageTier(this);
            } else {
                account.setVipTierExpiration(0);
            }
        }
        sendPacket(new ReceiveVipInfo());
    }

    public void setNCoins(int coins) {
        account.setCoins(coins);
    }

    public int getNCoins() {
        return account.getCoin();
    }

    public void updateNCoins(int coins) {
        account.updateCoins(coins);
    }

    public long getGoldCoin() {
        return inventory.getGoldCoin();
    }

    public long getSilverCoin() {
        return inventory.getSilverCoin();
    }

    public long getVipTierExpiration() {
        return account.getVipTierExpiration();
    }

    public void setVipTierExpiration(long expiration) {
        account.setVipTierExpiration(expiration);
    }

    public long getLCoins() { return inventory.getLCoin(); }
    // TODO Remove
    public void addLCoins(long count) { inventory.addLCoin(count); }

    public boolean isInBattle() {
        return AttackStanceTaskManager.getInstance().hasAttackStanceTask(this);
    }

    public boolean teleportInBattle() {
        return !isInBattle() && CharacterSettings.teleportInBattle();
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
        shortcuts.setActiveShortcut(room, active);
    }

    public Shortcut nextAutoShortcut() {
        return shortcuts.nextAutoShortcut();
    }

    public Shortcut nextAutoSummonShortcut() {
        return shortcuts.nextAutoSummonShortcut();
    }

    public void resetNextAutoShortcut() {
        shortcuts.resetNextAutoShortcut();
    }

    public Set<Shortcut> getActiveAutoSupplies() {
        return shortcuts.getSuppliesShortcuts();
    }

    public void setActiveAutoSupplyShortcut(int room, boolean activate) {
        shortcuts.setActiveSupplyShortcut(room, activate);
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

    public boolean isHairAccessoryEnabled() {
        return variables.isHairAccessoryEnabled();
    }

    /**
     * @return The amount of times player has used world chat
     */
    public int getWorldChatUsed() {
        return variables.getWorldChatUsed();
    }

    public int getVitalityItemsUsed() {
        return variables.getVitalityItemsUsed();
    }

    private int getAbilityPointsMainClassUsed() {
        return variables.getAbilityPointsMainClassUsed();
    }

    private int getRevelationSkillMainClass1() {
        return variables.getRevelationSkillMainClass1();
    }

    private int getRevelationSkillMainClass2() {
        return variables.getRevelationSkillMainClass2();
    }

    private String getExtendDrop() {
        return variables.getExtendDrop();
    }

    public int getFortuneTelling() {
        return variables.getFortuneTelling();
    }

    public boolean isFortuneTellingBlackCat() {
        return variables.isFortuneTellingBlackCat();
    }

    public long getHuntingZoneResetTime(int zoneId) {
        String[] timeZones = variables.getHuntingZoneResetTime().split(";");

        for(int i = 0 ; i < timeZones.length; i = i + 2) {
            if (timeZones[i].equalsIgnoreCase("" + zoneId))
                return Long.parseLong(timeZones[i + 1]);
        }

        return 0;
    }

    public int getAutoCp() {
        return variables.getAutoCp();
    }

    public int getAutoHp() {
        return variables.getAutoHp();
    }

    public int getAutoMp() {
        return variables.getAutoMp();
    }

    public boolean getExpOff() {
        return variables.getExpOff();
    }

    public boolean isItemsRewarded() {
        return variables.isItemsRewarded();
    }

    private long getHennaDuration(int slot) {
        return switch (slot) {
            case 1 -> variables.getHenna1Duration();
            case 2 -> variables.getHenna2Duration();
            case 3 -> variables.getHenna3Duration();
            default -> 0;
        };
    }

    /**
     * @return the beauty shop hair, or his normal if not changed.
     */
    public int getVisualHair() {
        return variables.getVisualHairId();
    }

    /**
     * @return the beauty shop hair color, or his normal if not changed.
     */
    public int getVisualHairColor() {
        return variables.getVisualHairColorId();
    }

    /**
     * @return the beauty shop modified face, or his normal if not changed.
     */
    public int getVisualFace() {
        return variables.getVisualFaceId();
    }

    public int getInstanceRestore() {
        return variables.getInstanceRestore();
    }

    public int getClaimedClanRewards(int defaultValue) {
        return variables.getClaimedClanRewards() != 0 ? variables.getClaimedClanRewards() : defaultValue;
    }

    public String getCondOverrideKey() {
        return isGM() ? parseCondOverrideOrDefault() : variables.getCondOverrideKey();
    }

    private String parseCondOverrideOrDefault() {
        return isNull(variables.getCondOverrideKey()) || variables.getCondOverrideKey().isBlank() ? Long.toString(PcCondOverride.getAllExceptionsMask()) : variables.getCondOverrideKey();
    }

    public int getMonsterReturn() {
        return variables.getMonsterReturn();
    }

    public String getUiKeyMapping() {
        return variables.getUiKeyMapping();
    }

    public void setHairAccessoryEnabled(boolean hairAccessory_Enabled) {
        variables.setHairAccessoryEnabled(hairAccessory_Enabled);
    }

    public int getLampExp()
    {
        return variables.getLampExp();
    }

    public int getLampCount()
    {
        return variables.getLampCount();
    }

    public void setLampExp(int exp)
    {
        variables.setLampXp(exp);
    }

    public void setLampCount(int count)
    {
        variables.setLampCount(count);
    }


    /**
     * Sets the amount of times player can use world chat
     *
     * @param timesUsed how many times world chat has been used up until now.
     */
    public void setWorldChatUsed(int timesUsed) {
        variables.setWorldChatUsed(timesUsed);
    }

    public void setExtendDrop(String extendDrop) {
        variables.setExtendDrop(extendDrop);
    }

    public void setFortuneTelling(int fortuneTelling) {
        variables.setFortuneTelling(fortuneTelling);
    }

    public void setFortuneTellingBlackCat(boolean fortuneTellingBlackCat) {
        variables.setFortuneTellingBlackCat(fortuneTellingBlackCat);
    }

    public void setHuntingZoneResetTime(int zoneId, long huntingZoneResetTime) {
        if(variables.getHuntingZoneResetTime().equalsIgnoreCase("")) {
            variables.setHuntingZoneResetTime(zoneId + ";" + huntingZoneResetTime);
        } else {
            variables.setHuntingZoneResetTime(variables.getHuntingZoneResetTime() + ";" + zoneId + ";" + huntingZoneResetTime);
        }
    }

    public void setAutoCp(int autoCp) {
        variables.setAutoCp(autoCp);
    }

    public void setAutoHp(int autoHp) {
        variables.setAutoHp(autoHp);
    }

    public void setAutoMp(int autoMp) {
        variables.setAutoMp(autoMp);
    }

    public void setExpOff(boolean expOff) {
        variables.setExpOff(expOff);
    }

    public void setItemsRewarded(boolean itemsRewarded) {
        variables.setItemsRewarded(itemsRewarded);
    }

    private void setHennaDuration(long hennaDuration, int slot) {
        switch (slot) {
            case 1 -> variables.setHenna1Duration(hennaDuration);
            case 2 -> variables.setHenna2Duration(hennaDuration);
            case 3 -> variables.setHenna3Duration(hennaDuration);
        }
    }

    public void setVisualHair(int visualHairId) {
        variables.setVisualHairId(visualHairId);
    }

    public void setVisualHairColor(int visualHairColorId) {
        variables.setVisualHairColorId(visualHairColorId);
    }

    public void setVisualFace(int visualFaceId) {
        variables.setVisualFaceId(visualFaceId);
    }

    public void setInstanceRestore(int instanceRestore) {
        variables.setInstanceRestore(instanceRestore);
    }

    public void setClaimedClanRewards(int claimedClanRewards) {
        variables.setClaimedClanRewards(claimedClanRewards);
    }

    private void setCondOverrideKey(String condOverrideKey) {
        variables.setCondOverrideKey(condOverrideKey);
    }

    public void setMonsterReturn(int monsterReturn) {
        variables.setMonsterReturn(monsterReturn);
    }

    public void setUiKeyMapping(String uiKeyMapping) {
        variables.setUiKeyMapping(uiKeyMapping);
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

    public void updateExtendDrop(int id, long count) {
        StringBuilder result = new StringBuilder();
        final String data = getExtendDrop();
        if (data.isEmpty()) {
            result = new StringBuilder(id + "," + count);
        } else if (data.contains(";")) {
            for (String s : data.split(";")) {
                final String[] drop = s.split(",");
                if (drop[0].equals(Integer.toString(id))) {
                    continue;
                }

                result.append(";").append(s);
            }
            result = new StringBuilder(result.substring(1));
        } else {
            result = new StringBuilder(id + "," + count);
        }
        variables.setExtendDrop(result.toString());
    }

    public long getExtendDropCount(int id) {
        final String data = getExtendDrop();
        for (String s : data.split(";")) {
            final String[] drop = s.split(",");
            if (drop[0].equals(Integer.toString(id))) {
                return Long.parseLong(drop[1]);
            }
        }
        return 0;
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

    public boolean tryEnableActualAutoShot(ShotType type) {
        var itemId = activeSoulShots.get(type);
        var weapon = getActiveWeaponInstance();
        if(isNull(itemId) || isNull(weapon)) {
            return false;
        }

        var shotsCount = weapon.getConsumeShotsCount();
        var item = inventory.getItemByItemId(itemId);

        if(isNull(item) || shotsCount > item.getCount()) {
            return false;
        }

        sendPacket(new ExAutoSoulShot(itemId, true, type));
        return true;
    }

    public void enableAutoSoulShot(ShotType type, int itemId) {
        if(itemId > 0) {
            activeSoulShots.put(type, itemId);
            rechargeShot(type);
            sendPackets(getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addItemName(itemId));
        } else {
            activeSoulShots.remove(type);
        }
        sendPacket(new ExAutoSoulShot(itemId, true, type));
        variables.updateActiveShot(type, itemId);
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
        variables.updateActiveShot(type, 0);
    }

    private void sendDisableShotPackets(ShotType type, int itemId) {
        sendPackets(new ExAutoSoulShot(itemId, false, type), getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(itemId));
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

    public int getSavedSoulshot() {
        return variables.getSoulshot();
    }

    public int getSavedSpiritshot() {
        return variables.getSpiritshot();
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

    public void setCostumes(IntMap<CostumeData> costumes) {
        this.costumes = costumes;
    }

    public void setActiveCostumeCollection(CostumeCollectionData collection) {
        this.activeCostumesCollection = collection;
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
        super.onStartRangedAttack(isCrossBow, reuse);
    }

    void setVariables(PlayerVariableData variables) {
        this.variables = variables;
    }

    void setStatsData(PlayerStatsData statsData) {
        this.statsData = statsData;
    }

    void setTeleportFavorites(IntSet teleports) {
        this.teleportFavorites = teleports;
    }

    // Unchecked

    // TODO: This needs to be better integrated and saved/loaded
    private final Radar radar;

    public static final int ID_NONE = -1;
    public static final int REQUEST_TIMEOUT = 15;

    private static final int FALLING_VALIDATION_DELAY = 1000;

    private final Henna[] hennas = new Henna[3];
    private final Location lastServerPosition = new Location(0, 0, 0);
    private final Shortcuts shortcuts = new Shortcuts(this);
    private final MacroList macros = new MacroList(this);
    private final Set<Player> snoopListener = ConcurrentHashMap.newKeySet();
    private final Set<Player> snoopedPlayer = ConcurrentHashMap.newKeySet();
    private final ReentrantLock classLock = new ReentrantLock();
    private final AtomicInteger charges = new AtomicInteger();
    private final PlayerFreight freight = new PlayerFreight(this);
    private final PlayerInventory inventory = new PlayerInventory(this);
    private final IntMap<CubicInstance> cubics = new CHashIntMap<>();
    private final IntMap<TeleportBookmark> teleportBookmarks = new CHashIntMap<>();
    private final Map<String, QuestState> quests = new ConcurrentHashMap<>();
    private final Map<BaseStats, Integer> hennaBaseStats = new ConcurrentHashMap<>();
    private final IntMap<ScheduledFuture<?>> hennaRemoveSchedules = new CHashIntMap<>(3);
    private final Request request = new Request(this);
    private final BlockList blockList = new BlockList(this);

    private final int[] htmlActionOriginObjectIds = new int[HtmlActionScope.values().length];
    @SuppressWarnings("unchecked")
    private final LinkedList<String>[] htmlActionCaches = new LinkedList[HtmlActionScope.values().length];
    private final Fishing fishing = new Fishing(this);
    private final IntSet whispers = CHashIntMap.newKeySet();
    private final IntSet friends = CHashIntMap.newKeySet();

    protected Future<?> mountFeedTask;
    protected boolean recommendTwoHoursGiven;
    protected boolean inventoryDisable;

    private volatile boolean isOnline;

    private String lang;
    private PetTemplate petTemplate;
    private PetLevelData petLevelData;
    private ScheduledFuture<?> dismountTask;
    private ScheduledFuture<?> fameTask;

    private int controlItemId;
    private int currentFeed;
    private int weightPenalty;
    private int lastCompassZone;
    private int bookmarkSlot;
    private long uptime;
    private byte pvpFlag;
    private boolean petItems;
    private boolean canFeed;

    private int siegeSide;
    private byte siegeState;
    private boolean isInSiege;

    private OlympiadMode olympiadMode = OlympiadMode.NONE;
    private int olympiadMatchId = -1;
    private int olympiadSide = -1;
    private boolean olympiadStart;

    private SystemMessageId noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
    private int duelState = Duel.DUELSTATE_NODUEL;
    private int duelId;
    private boolean isInDuel;
    private boolean startingDuel;

    private Vehicle vehicle;
    private Location inVehiclePosition;
    private MountType mountType = MountType.NONE;
    private int mountNpcId;
    private int mountLevel;

    private Location lastLoc;
    private AdminTeleportType teleportType = AdminTeleportType.NORMAL;

    private int mountObjectID;
    private boolean inCrystallize;
    private boolean isCrafting;
    private boolean sitting;
    private boolean inObserverMode;

    private ScheduledFuture<?> recommendGiveTask;
    private int recommend;
    private int recommendLeft;

    private String storeName = "";
    private TradeList sellList;
    private TradeList buyList;
    private TradeList activeTradeList;
    private Warehouse activeWarehouse;
    private PlayerRefund refund;
    private PlayerWarehouse warehouse;
    private PrivateStoreType privateStoreType = PrivateStoreType.NONE;
    private ScheduledFuture<?> onlineTimeUpdateTask;
    private IntMap<ManufactureItem> manufactureItems;
    private IntMap<Summon> servitors;
    private Set<TamedBeast> tamedBeast;
    private EnumIntBitmask<ClanPrivilege> clanPrivileges = new EnumIntBitmask<>(ClanPrivilege.class, false);

    private Pet pet;
    private Npc lastFolkNpc;
    private Clan clan;
    private MatchingRoom matchingRoom;
    private ScheduledFuture<?> chargeTask;
    private PreparedMultisellList currentMultiSell;
    private int questNpcObject;
    private int agathionId;
    private SocialStatus socialStatus = SocialStatus.VAGABOND;
    private boolean hero;

    private ScheduledFuture<?> soulTask;
    private int souls;

    private Location currentSkillWorldPosition;
    private AccessLevel accessLevel;
    private IntList silenceModeExcluded;

    private boolean messageRefusing;
    private boolean silenceMode ;
    private boolean dietMode;
    private boolean tradeRefusing;

    private Party party;
    private Player activeRequester;
    private long requestExpireTime;

    private long spawnProtectEndTime;
    private long teleportProtectEndTime;
    private IntMap<ExResponseCommissionInfo> lastCommissionInfos;

    private boolean isOnCustomEvent;

    private Weapon fistsWeaponItem;
    private volatile Map<Class<? extends AbstractRequest>, AbstractRequest> requests;

    private PlayerEventHolder eventStatus;
    private byte handysBlockCheckerEventArena = -1;
    private volatile IntMap<Skill> transformSkills;
    private ScheduledFuture<?> taskRentPet;
    private ScheduledFuture<?> taskWater;
    private ScheduledFuture<?> skillListRefreshTask;

    private SkillUseHolder queuedSkill;
    private int reviveRequested;
    private double revivePower;
    private boolean revivePet;
    private double cpUpdateIncCheck;
    private double cpUpdateDecCheck;
    private double cpUpdateInterval;
    private double mpUpdateIncCheck;
    private double mpUpdateDecCheck;
    private double mpUpdateInterval;
    private double originalCp;
    private double originalHp;
    private double originalMp;

    private int clientZ;
    private Future<?> fallingDamageTask;
    private volatile long fallingTimestamp;
    private volatile int fallingDamage;

    private String adminConfirmCmd;
    private MovieHolder movieHolder;
    private Future<?> pvPRegTask;
    private int multiSocialTarget;
    private int multiSocialAction;
    private volatile long lastItemAuctionInfoRequest;
    private long pvpFlagLasts;
    private long notMoveUntil;

    private IntMap<Skill> customSkills;
    private int actionMask;
    private int questZoneId = -1;

    private String lastPetitionGmName;
    private boolean hasCharmOfCourage;

    private boolean isSellingBuffs;
    private List<SellBuffHolder> sellingBuffs;
    private Set<QuestState> notifyQuestOfDeathList;
    private ScheduledFuture<?> taskWarnUserTakeBreak;

    @Override
    protected void initCharStatusUpdateValues() {
        super.initCharStatusUpdateValues();

        cpUpdateInterval = getMaxCp() / MAX_STATUS_BAR_PX;
        cpUpdateIncCheck = getMaxCp();
        cpUpdateDecCheck = getMaxCp() - cpUpdateInterval;

        mpUpdateInterval = getMaxMp() / MAX_STATUS_BAR_PX;
        mpUpdateIncCheck = getMaxMp();
        mpUpdateDecCheck = getMaxMp() - mpUpdateInterval;
    }

    public long getPvpFlagLasts() {
        return pvpFlagLasts;
    }

    private void setPvpFlagLasts(long time) {
        pvpFlagLasts = time;
    }

    public void startPvPFlag() {
        updatePvPFlag(1);

        if (pvPRegTask == null) {
            pvPRegTask = ThreadPool.scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
        }
    }

    private void stopPvpRegTask() {
        if (pvPRegTask != null) {
            pvPRegTask.cancel(true);
            pvPRegTask = null;
        }
    }

    public void stopPvPFlag() {
        stopPvpRegTask();

        updatePvPFlag(0);

        pvPRegTask = null;
    }

    public boolean isSellingBuffs() {
        return isSellingBuffs;
    }

    public void setIsSellingBuffs(boolean val) {
        isSellingBuffs = val;
    }

    public List<SellBuffHolder> getSellingBuffs() {
        if (sellingBuffs == null) {
            sellingBuffs = new ArrayList<>();
        }
        return sellingBuffs;
    }

    public String getAccountName() {
        return data.getAccountName();
    }

    public IntMap<String> getAccountChars() {
        return accountPlayers;
    }

    public int getRelation(Player target) {
        int result = clanRelation(target);
        result |= partyRelation(target);
        result |= siegeRelation(target);
        return result | handysBlockRelation();
    }

    private int handysBlockRelation() {
        int result = 0;
        if (handysBlockCheckerEventArena != -1) {
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

    private int siegeRelation(Player target) {
        int result = 0;
        if (siegeState != 0) {
            result |= RelationChanged.RELATION_INSIEGE;

            if (getSiegeState() != target.getSiegeState()) {
                result |= RelationChanged.RELATION_ENEMY;
            } else {
                result |= RelationChanged.RELATION_ALLY;
            }

            if (siegeState == 1) {
                result |= RelationChanged.RELATION_ATTACKER;
            }
        }
        return result;
    }

    private int partyRelation(Player target) {
        int result = 0;
        if (nonNull(party) && party == target.getParty()) {

            result |= RelationChanged.RELATION_HAS_PARTY;

            int partyIndex = party.getMembers().indexOf(this);

            result |= switch (partyIndex) {
                case 0 -> RelationChanged.RELATION_PARTYLEADER;
                case 1 -> RelationChanged.RELATION_PARTY4;
                case 2 -> RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1;
                case 3 -> RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2;
                case 4 -> RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY1;
                case 5 -> RelationChanged.RELATION_PARTY3;
                case 6 -> RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1;
                case 7 -> RelationChanged.RELATION_PARTY2;
                case 8 -> RelationChanged.RELATION_PARTY1;
                default -> 0;
            };
        }
        return result;
    }

    private int clanRelation(Player target) {
        int result = 0;
        if (nonNull(clan)) {
            result |= RelationChanged.RELATION_CLAN_MEMBER;

            if (getAllyId() != 0) {
                result |= RelationChanged.RELATION_ALLY_MEMBER;
            }

            if (isClanLeader()) {
                result |= RelationChanged.RELATION_LEADER;
            }

            final Clan targetClan = target.getClan();
            if (clan == targetClan) {
                result |= RelationChanged.RELATION_CLAN_MATE;
            }

            result |= warRelation(target, targetClan);
        }

        return result;
    }

    private int warRelation(Player target, Clan targetClan) {
        int result = 0;
        if(nonNull(targetClan)) {
            ClanWar war = clan.getWarWith(target.getClan().getId());
            if (nonNull(war)) {
                switch (war.getState()) {
                    case DECLARATION, BLOOD_DECLARATION -> result |= RelationChanged.RELATION_DECLARED_WAR;
                    case MUTUAL -> {
                        result |= RelationChanged.RELATION_DECLARED_WAR;
                        result |= RelationChanged.RELATION_MUTUAL_WAR;
                    }
                }
            }
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

    public final Appearance getAppearance() {
        return appearance;
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
        return isCrafting;
    }

    public void setIsCrafting(boolean isCrafting) {
        this.isCrafting = isCrafting;
    }

    /**
     * @return a table containing all Common RecipeList of the Player.
     */
    public RecipeList[] getCommonRecipeBook() {
        return commonRecipes.values().toArray(RecipeList[]::new);
    }

    /**
     * @return a table containing all Dwarf RecipeList of the Player.
     */
    public RecipeList[] getDwarvenRecipeBook() {
        return dwarvenRecipes.values().toArray(RecipeList[]::new);
    }

    public void registerRecipe(RecipeList recipe) {
        if(recipe.isDwarvenRecipe()) {
            dwarvenRecipes.put(recipe.getId(), recipe);
        } else {
            commonRecipes.put(recipe.getId(), recipe);
        }
        getDAO(PlayerDAO.class).addRecipe(objectId, recipe.getId());
    }

    /**
     * @param recipeId The Identifier of the RecipeList to check in the player's recipe books
     * @return {@code true}if player has the recipe on Common or Dwarven Recipe book else returns {@code false}
     */
    public boolean hasRecipeList(int recipeId) {
        return dwarvenRecipes.containsKey(recipeId) || commonRecipes.containsKey(recipeId);
    }

    /**
     * Tries to remove a recipe list from the table _DwarvenRecipeBook or from table _CommonRecipeBook, those table contain all RecipeList of the Player
     *
     * @param recipeId The Identifier of the RecipeList to remove from the recipe book
     */
    public void unregisterRecipeList(int recipeId) {
        boolean removed = nonNull(dwarvenRecipes.remove(recipeId)) || nonNull(commonRecipes.remove(recipeId));
        if(removed) {
            shortcuts.deleteShortcuts(s -> s.getShortcutId() == recipeId && s.getType() == ShortcutType.RECIPE);
            getDAO(PlayerDAO.class).deleteRecipe(objectId, recipeId);
        } else {
            LOGGER.warn("Attempted to remove unknown RecipeList {}", recipeId);
        }
    }

    private int getLastQuestNpcObject() {
        return questNpcObject;
    }

    public void setLastQuestNpcObject(int npcId) {
        questNpcObject = npcId;
    }

    /**
     * @param quest The name of the quest
     * @return the QuestState object corresponding to the quest name.
     */
    public QuestState getQuestState(String quest) {
        return quests.get(quest);
    }

    /**
     * Add a QuestState to the table _quest containing all quests began by the Player.
     *
     * @param qs The QuestState to add to _quest
     */
    public void setQuestState(QuestState qs) {
        quests.put(qs.getQuestName(), qs);
    }

    /**
     * Verify if the player has the quest state.
     *
     * @param quest the quest state to check
     * @return {@code true} if the player has the quest state, {@code false} otherwise
     */
    public boolean hasQuestState(String quest) {
        return quests.containsKey(quest);
    }

    /**
     * Remove a QuestState from the table _quest containing all quests began by the Player.
     *
     * @param quest The name of the quest
     */
    public void delQuestState(String quest) {
        quests.remove(quest);
    }

    /**
     * @return List of {@link QuestState}s of the current player.
     */
    public Collection<QuestState> getAllQuestStates() {
        return quests.values();
    }

    /**
     * @return a table containing all Quest in progress from the table _quests.
     */
    public List<Quest> getAllActiveQuests() {
        //@formatter:off
        return quests.values().stream()
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

        final Npc target = lastFolkNpc;

        if ((target != null) && MathUtil.isInsideRadius2D(this, target, Npc.INTERACTION_DISTANCE)) {
            quest.notifyEvent(event, target, this);
        } else if (questNpcObject > 0) {
            final WorldObject object = World.getInstance().findObject(getLastQuestNpcObject());

            if (GameUtils.isNpc(object) && MathUtil.isInsideRadius2D(this, object, Npc.INTERACTION_DISTANCE)) {
                final Npc npc = (Npc) object;
                quest.notifyEvent(event, npc, this);
            }
        }
    }

    /**
     * Remove QuestState instance that is to be notified of Player's death.
     *
     * @param qs The QuestState that subscribe to this event
     */
    public void removeNotifyQuestOfDeath(QuestState qs) {
        if ((qs == null) || (notifyQuestOfDeathList == null)) {
            return;
        }

        notifyQuestOfDeathList.remove(qs);
    }

    /**
     * @return a list of QuestStates which registered for notify of death of this Player.
     */
    public final Set<QuestState> getNotifyQuestOfDeath() {
        if (notifyQuestOfDeathList == null) {
            initNotifyQuestOfDeathList();
        }

        return notifyQuestOfDeathList;
    }

    private synchronized void initNotifyQuestOfDeathList() {
        if (notifyQuestOfDeathList == null) {
            notifyQuestOfDeathList = ConcurrentHashMap.newKeySet();
        }
    }

    public final boolean isNotifyQuestOfDeathEmpty() {
        return (notifyQuestOfDeathList == null) || notifyQuestOfDeathList.isEmpty();
    }

    public Shortcut getShortcut(int room) {
        return shortcuts.getShortcut(room);
    }

    public void registerShortCut(Shortcut shortcut) {
        shortcuts.registerShortCut(shortcut);
    }

    /**
     * Updates the shortcut bars with the new skill.
     *
     * @param skillId       the skill Id to search and update.
     * @param skillLevel    the skill level to update.
     */
    private void updateShortCuts(int skillId, int skillLevel) {
        shortcuts.updateShortCuts(skillId, skillLevel);
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
        return siegeState;
    }

    /**
     * Set the siege state of the Player.
     *
     * @param siegeState 1 = attacker, 2 = defender, 0 = not involved
     */
    public void setSiegeState(byte siegeState) {
        this.siegeState = siegeState;
    }

    public boolean isRegisteredOnThisSiegeField(int val) {
        return (siegeSide == val) || ((siegeSide >= 81) && (siegeSide <= 89));
    }

    public int getSiegeSide() {
        return siegeSide;
    }

    public void setSiegeSide(int val) {
        siegeSide = val;
    }

    @Override
    public byte getPvpFlag() {
        return pvpFlag;
    }

    private void setPvpFlag(int pvpFlag) {
        this.pvpFlag = (byte) pvpFlag;
    }

    @Override
    public void updatePvPFlag(int value) {
        if (pvpFlag == value) {
            return;
        }
        setPvpFlag(value);

        final StatusUpdate su = new StatusUpdate(this);
        computeStatusUpdate(su, StatusUpdateType.PVP_FLAG);
        if (su.hasUpdates()) {
            broadcastPacket(su);
        }
        updateRelations();
    }

    private void updateRelations() {
        World.getInstance().forEachVisibleObject(this, Player.class, this::updateRelation);
    }

    public void updateRelation(Player player) {
        if (!isVisibleFor(player)) {
            return;
        }
        int relation = getRelation(player);
        int oldRelation = getKnownRelations().getOrDefault(player.getObjectId(), -1);
        if (oldRelation == -1 || oldRelation != relation) {
            sendRelationChanged(player, relation);
            getKnownRelations().put(player.getObjectId(), relation);
        }
    }

    private void sendRelationChanged(Player player, int relation) {
        var relationChanged = new RelationChanged();
        relationChanged.addRelation(this, relation, isAutoAttackable(player));

        if (nonNull(pet)) {
            relationChanged.addRelation(pet, relation, isAutoAttackable(player));
        }

        for (Summon s : getServitors().values()) {
            relationChanged.addRelation(s, relation, isAutoAttackable(player));
        }
        player.sendPacket(relationChanged);
    }

    @Override
    public void revalidateZone(boolean force) {
        // Cannot validate if not in a world region (happens during teleport)
        if (getWorldRegion() == null) {
            return;
        }

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

        ZoneEngine.getInstance().getRegion(this).revalidateZones(this);

        if (Config.ALLOW_WATER) {
            checkWaterState();
        }

        if (isInsideZone(ZoneType.ALTERED)) {
            if (lastCompassZone == ExSetCompassZoneCode.ALTEREDZONE) {
                return;
            }
            lastCompassZone = ExSetCompassZoneCode.ALTEREDZONE;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.ALTEREDZONE);
            sendPacket(cz);
        } else if (isInsideZone(ZoneType.SIEGE)) {
            if (lastCompassZone == ExSetCompassZoneCode.SIEGEWARZONE2) {
                return;
            }
            lastCompassZone = ExSetCompassZoneCode.SIEGEWARZONE2;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.SIEGEWARZONE2);
            sendPacket(cz);
        } else if (isInsideZone(ZoneType.PVP)) {
            if (lastCompassZone == ExSetCompassZoneCode.PVPZONE) {
                return;
            }
            lastCompassZone = ExSetCompassZoneCode.PVPZONE;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.PVPZONE);
            sendPacket(cz);
        } else if (isInsideZone(ZoneType.PEACE)) {
            if (lastCompassZone == ExSetCompassZoneCode.PEACEZONE) {
                return;
            }
            lastCompassZone = ExSetCompassZoneCode.PEACEZONE;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.PEACEZONE);
            sendPacket(cz);
        } else {
            if (lastCompassZone == ExSetCompassZoneCode.GENERALZONE) {
                return;
            }
            if (lastCompassZone == ExSetCompassZoneCode.SIEGEWARZONE2) {
                updatePvPStatus();
            }
            lastCompassZone = ExSetCompassZoneCode.GENERALZONE;
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
        return data.getPk();
    }

    public void setPkKills(int pkKills) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPKChanged(this, data.getPk(), pkKills), this);
        data.setPk(pkKills);
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int value) {
        recommend = min(Math.max(value, 0), 255);
    }

    private void increaseRecommend() {
        if (recommend < 255) {
            recommend++;
        }
    }

    /**
     * @return the number of recommendation that the Player can give.
     */
    public int getRecommendLeft() {
        return recommendLeft;
    }

    /**
     * Set the number of recommendation obtained by the Player (Max : 255).
     */
    public void setRecommendLeft(int value) {
        recommendLeft = min(Math.max(value, 0), 255);
    }

    /**
     * Increment the number of recommendation that the Player can give.
     */
    private void decreaseRecommendLeft() {
        if (recommendLeft > 0) {
            recommendLeft--;
        }
    }

    public void giveRecommend(Player target) {
        target.increaseRecommend();
        decreaseRecommendLeft();
    }

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

        if (reputation <= -6500) {
            stopSkillEffects(CommonSkill.REPUTATION_2.getSkill());
            CommonSkill.REPUTATION_3.getSkill().applyEffects(this, this);
        }
        else if (reputation <= -3620) {
            stopSkillEffects(CommonSkill.REPUTATION_1.getSkill());
            CommonSkill.REPUTATION_2.getSkill().applyEffects(this, this);
        }
        else if (reputation <= -2180) {
            CommonSkill.REPUTATION_1.getSkill().applyEffects(this, this);
        }
        else if (reputation >= 0){
            stopSkillEffects(CommonSkill.REPUTATION_1.getSkill());
            stopSkillEffects(CommonSkill.REPUTATION_2.getSkill());
            stopSkillEffects(CommonSkill.REPUTATION_3.getSkill());
        }

        data.setReputation(reputation);

        sendPacket(getSystemMessage(SystemMessageId.YOUR_REPUTATION_HAS_BEEN_CHANGED_TO_S1).addInt(getReputation()));
        broadcastReputation();
    }

    @Override
    public int getReputation() {
        return data.getReputation();
    }


    public int getWeightPenalty() {
        if (dietMode) {
            return 0;
        }
        return weightPenalty;
    }

    /**
     * Update the overloaded status of the Player.
     *
     * @param broadcast TODO
     */
    public void refreshOverloaded(boolean broadcast) {
        final int maxLoad = getMaxLoad();
        if (maxLoad > 0) {
            final long weightProc = (((getCurrentLoad() - getBonusWeightPenalty()) * 1000L) / getMaxLoad());
            int newWeightPenalty;
            if ((weightProc < 500) || dietMode) {
                newWeightPenalty = 0;
            } else if (weightProc < 666) {
                newWeightPenalty = 1;
            } else if (weightProc < 800) {
                newWeightPenalty = 2;
            } else if (weightProc < 1000) {
                newWeightPenalty = 3;
            } else {
                newWeightPenalty = 4;
            }

            if (weightPenalty != newWeightPenalty) {
                weightPenalty = newWeightPenalty;
                if (newWeightPenalty > 0) {
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
        return data.getPvP();
    }

    /**
     * Set the the PvP Kills of the Player (Number of player killed during a PvP)._pvpKills,
     */
    public void setPvpKills(int pvpKills) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPvPChanged(this, data.getPvP(), pvpKills), this);
        data.setPvP(pvpKills);
    }

    public int getFame() {
        return data.getFame();
    }

    public void setFame(int fame) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFameChanged(this, data.getFame(), fame), this);
        data.setFame(Math.min(fame, CharacterSettings.maxFame()));
    }

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
        if (!classLock.tryLock()) {
            return;
        }

        try {
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
                party.broadcastPacket(new PartySmallWindowUpdate(this, true));
            }

            if (clan != null) {
                clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
            }

            sendPacket(new ExSubjobInfo(this, SubclassInfoType.CLASS_CHANGED));

            // Add AutoGet skills and normal skills and/or learnByFS depending on configurations.
            rewardSkills();

            if (!canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
                checkPlayerSkills();
            }

            notifyFriends(FriendStatus.CLASS);
        } finally {
            classLock.unlock();
        }
    }

    public long getExp() {
        return getStats().getExp();
    }


    /**
     * Set the fists weapon of the Player (used when no weapon is equipped).
     *
     * @param weaponItem The fists Weapon to set to the Player
     */
    void setFistsWeaponItem(Weapon weaponItem) {
        fistsWeaponItem = weaponItem;
    }

    Weapon findFistsWeaponItem() {
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
        if (CharacterSettings.autoLearnSkillEnabled()) {
            giveAvailableSkills(CharacterSettings.autoLearnSkillFSEnabled(), true);
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

            if ((oldSkill != null) && (oldSkill.getSubLevel() > 0) && (skill.getSubLevel() == 0) && (oldSkill.getLevel() < skill.getLevel())) {
                skill = SkillEngine.getInstance().getSkill(skill.getId(), skill.getLevel());
            }

            addSkill(skill, false);
            skillsForStore.add(skill);
        }
        storeSkills(skillsForStore);
        if (skillCounter > 0 && CharacterSettings.autoLearnSkillEnabled()) {
            sendMessage("You have learned " + skillCounter + " new skills.");
        }
        return skillCounter;
    }

    private void giveAvailableAutoGetSkills() {
        // Get available skills
        final List<SkillLearn> autoGetSkills = SkillTreesData.getInstance().getAvailableAutoGetSkills(this);
        final SkillEngine st = SkillEngine.getInstance();
        Skill skill;
        for (SkillLearn s : autoGetSkills) {
            skill = st.getSkill(s.getSkillId(), s.getSkillLevel());
            if (skill != null) {
                addSkill(skill, true);
            } else {
                LOGGER.warn("Skipping null auto-get skill for {}", this);
            }
        }
    }

    @Override
    public Race getRace() {
        return getTemplate().getRace();
    }

    public Radar getRadar() {
        return radar;
    }

    public long getSp() {
        return getStats().getSp();
    }

    public void setSp(long sp) {
        if (sp < 0) {
            sp = 0;
        }

        super.getStats().setSp(sp);
    }

    /**
     * @return the Clan Identifier of the Player.
     */
    @Override
    public int getClanId() {
        return data.getClanId();
    }

    /**
     * @return the Clan Crest Identifier of the Player or 0.
     */
    public int getClanCrestId() {
        if (clan != null) {
            return clan.getCrestId();
        }

        return 0;
    }

    /**
     * @return The Clan CrestLarge Identifier or 0
     */
    public int getClanCrestLargeId() {
        if ((clan != null) && ((clan.getCastleId() != 0) || (clan.getHideoutId() != 0))) {
            return clan.getCrestLargeId();
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
        return sitting;
    }

    public void setIsSitting(boolean state) {
        sitting = state;
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

        if (!sitting && !isAttackingDisabled() && !isControlBlocked() && !isImmobilized() && !isFishing()) {
            breakAttack();
            setIsSitting(true);
            getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
            broadcastPacket(ChangeWaitType.sitting(this));
            // Schedule a sit down task to wait for the animation to finish
            ThreadPool.schedule(new SitDownTask(this), 2500);
            setBlockActions(true);
        }
    }

    public void standUp() {
        if (Event.isParticipant(this) && eventStatus.isSitForced()) {
            sendMessage("A dark force beyond your mortal understanding makes your knees to shake when you try to stand up...");
        } else if (sitting && !isInStoreMode() && !isAlikeDead()) {
            if (getEffectList().isAffected(EffectFlag.RELAXING)) {
                stopEffects(EffectFlag.RELAXING);
            }

            broadcastPacket(ChangeWaitType.standing(this));
            ThreadPool.schedule(new StandUpTask(this), 2500);
        }
    }

    public PlayerWarehouse getWarehouse() {
        if (isNull(warehouse)) {
            synchronized (this) {
                if(isNull(warehouse)) {
                    warehouse = new PlayerWarehouse(this);
                    warehouse.restore();
                }
            }
        }
        if (Config.WAREHOUSE_CACHE) {
            WarehouseCacheManager.getInstance().addCacheTask(this);
        }
        return warehouse;
    }

    /**
     * Free memory used by Warehouse
     */
    public void clearWarehouse() {
        if (warehouse != null) {
            warehouse.deleteMe();
        }
        warehouse = null;

        if (Config.WAREHOUSE_CACHE) {
            WarehouseCacheManager.getInstance().remCacheTask(this);
        }
    }

    public PlayerFreight getFreight() {
        return freight;
    }

    /**
     * @return true if refund list is not empty
     */
    public boolean hasRefund() {
        return (refund != null) && (refund.getSize() > 0) && Config.ALLOW_REFUND;
    }

    /**
     * @return refund object or create new if not exist
     */
    public PlayerRefund getRefund() {
        if (refund == null) {
            refund = new PlayerRefund(this);
        }
        return refund;
    }

    /**
     * Clear refund
     */
    public void clearRefund() {
        if (refund != null) {
            refund.deleteMe();
        }
        refund = null;
    }

    /**
     * @return the Adena amount of the Player.
     */
    public long getAdena() {
        return inventory.getAdena();
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
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addItem(inventory.getAdenaInstance());
            sendInventoryUpdate(iu);
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
                final SystemMessage sm;
                if (count > 1) {
                    sm = getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED).addItemName(Inventory.BEAUTY_TICKET_ID).addLong(count);
                } else {
                    sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(Inventory.BEAUTY_TICKET_ID);
                }
                sendPacket(sm);
            }
        }
        return true;
    }


    public Item addItem(String process, int itemId, long count, int enchant, WorldObject reference, boolean sendMessage) {
        return addItem(process, itemId, count, enchant, reference, sendMessage, true);
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
            final Item newItem = inventory.addItem(process, item, this, reference);

            // If over capacity, drop the item
            if (!canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !inventory.validateCapacity(0, item.isQuestItem()) && newItem.isDropable() && (!newItem.isStackable() || (newItem.getLastChange() != ItemChangeType.MODIFIED))) {
                dropItem("InvDrop", newItem, null, true);
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

    public Item addItem(String process, int itemId, long count, int enchant, WorldObject reference, boolean sendMessage, boolean sendUpdate) {
        Item item = null;
        if (count > 0) {
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);

            if (isNull(template)) {
                LOGGER.error("Item doesn't exist so cannot be added. Item ID: {}", itemId);
                return null;
            }

            if(template.hasExImmediateEffect()) {
                useAutoConsumeItem(template);
            } else {
                item = addItemToInventory(process, itemId, count, enchant, reference, sendUpdate, template);
            }

            if (sendMessage) {
                sendObtainItemMessage(process, count, enchant, template);
            }
        }
        return item;
    }

    private Item addItemToInventory(String process, int itemId, long count, int enchant, WorldObject reference, boolean sendUpdate, ItemTemplate template) {
        Item item;
        item = inventory.addItem(process, itemId, count, this, reference, sendUpdate);
        if(enchant > 0) {
            item.changeEnchantLevel(enchant);
        }

        // If over capacity, drop the item
        if (!canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !inventory.validateCapacity(0, template.isQuestItem()) && item.isDropable()
                && (!item.isStackable() || (item.getLastChange() != ItemChangeType.MODIFIED))) {

            dropItem("InvDrop", item, null);
        }
        return item;
    }

    private void useAutoConsumeItem(ItemTemplate template) {
        final var handler = ItemHandler.getInstance().getHandler(template instanceof EtcItem etcItem ? etcItem : null);

        if (handler == null) {
            LOGGER.warn("No item handler registered for immediate item id {}!",  template.getId());
        } else {
            handler.useItem(this, ItemEngine.getInstance().createTempItem(template), false);
        }
    }

    private void sendObtainItemMessage(String process, long count, int enchant, ItemTemplate template) {
        SystemMessage message = null;
        if (count > 1) {
            SystemMessageId messageId;
            if (process.equalsIgnoreCase("Sweeper") || process.equalsIgnoreCase("Quest")) {
                messageId = SystemMessageId.YOU_HAVE_EARNED_S2_S1_S;
            } else {
                messageId = YOU_HAVE_OBTAINED_S2_S1;
            }
            message = getSystemMessage(messageId).addItemName(template).addLong(count);
        } else if (process.equalsIgnoreCase("Sweeper") || process.equalsIgnoreCase("Quest")) {
            sendPacket( getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1).addItemName(template) );
        } else if(enchant > 0) {
            sendPacket( getSystemMessage(YOU_HAVE_OBTAINED_A_S1_S2).addItemName(template).addInt(enchant));
        } else {
            sendPacket( getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1).addItemName(template) );
        }
        sendPacket(message);
    }

    /**
     * Destroy item from inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param item        : Item to be destroyed
     * @param count       : amount to be destroyed
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

        sendInventoryUpdate(new InventoryUpdate(item));

        if (sendMessage) {
            final SystemMessage sm;
            if (count > 1) {
                sm = getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED).addItemName(item).addLong(count);
            } else {
                sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(item);
            }
            sendPacket(sm);
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

        return destroyItem(process, item, count, reference, sendMessage);
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

        final InventoryUpdate playerIU = new InventoryUpdate();
        playerIU.addItem(item);
        sendInventoryUpdate(playerIU);

        if (sendMessage) {
            final SystemMessage sm;
            if (count > 1) {
                sm = getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED).addItemName(itemId).addLong(count);
            } else {
                sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(itemId);
            }
            sendPacket(sm);
        }

        return true;
    }

    /**
     * Transfers item to another ItemContainer and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Identifier of the item to be transferred
     * @param count     : long Quantity of items to be transferred
     * @param target    : the Inventory which the item will be transfer to
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
     * Drop item from inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     String Identifier of process triggering this action
     * @param item        Item to be dropped
     * @param reference   WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param protectItem whether or not dropped item must be protected temporary against other players
     */
    private void dropItem(String process, Item item, WorldObject reference, boolean protectItem) {
        item = inventory.dropItem(process, item, this, reference);

        if (item == null) {
            sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            return;
        }

        item.dropMe(this, (getX() + Rnd.get(50)) - 25, (getY() + Rnd.get(50)) - 25, getZ() + 20);
        if (GeneralSettings.autoDestroyItemTime() > 0 && GeneralSettings.destroyPlayerDroppedItem() && !GeneralSettings.isProtectedItem(item.getId())
                && (!item.isEquipable() || GeneralSettings.destroyEquipableItem())) {

            ItemsAutoDestroy.getInstance().addItem(item);
        }

        // protection against auto destroy dropped item
        if (GeneralSettings.destroyPlayerDroppedItem()) {
            item.setProtected(item.isEquipable() && !GeneralSettings.destroyEquipableItem());
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
        final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_DROPPED_S1);
        sm.addItemName(item);
        sendPacket(sm);
    }

    private void dropItem(String process, Item item, WorldObject reference) {
        dropItem(process, item, reference, false);
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
     * @param protectItem : apply dropped item protection
     * @return Item corresponding to the new item or the updated item in inventory
     *
     * TODO extract method and remove duplication
     */
    public Item dropItem(String process, int objectId, long count, int x, int y, int z, WorldObject reference, boolean sendMessage, boolean protectItem) {
        final Item ownedItem = inventory.getItemByObjectId(objectId);
        final Item item = inventory.dropItem(process, objectId, count, this, reference);

        if (item == null) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }

            return null;
        }

        item.dropMe(this, x, y, z);

        if ((GeneralSettings.autoDestroyItemTime() > 0) && GeneralSettings.destroyPlayerDroppedItem() && !GeneralSettings.isProtectedItem(item.getId())
            && (!item.isEquipable() || GeneralSettings.destroyEquipableItem())) {

            ItemsAutoDestroy.getInstance().addItem(item);

        }
        if (GeneralSettings.destroyPlayerDroppedItem()) {
            item.setProtected(item.isEquipable() && !GeneralSettings.destroyEquipableItem());
        } else {
            item.setProtected(true);
        }

        // retail drop protection
        if (protectItem) {
            item.getDropProtection().protect(this);
        }

        sendInventoryUpdate( new InventoryUpdate(ownedItem));

        if (sendMessage) {
            sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_DROPPED_S1).addItemName(item));
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
        return spawnProtectEndTime > 0 && spawnProtectEndTime > System.currentTimeMillis();
    }

    public boolean isTeleportProtected() {
        return teleportProtectEndTime > 0  && teleportProtectEndTime > System.currentTimeMillis();
    }

    public void setSpawnProtection(boolean protect) {
        spawnProtectEndTime = protect ? System.currentTimeMillis() + (CharacterSettings.spawnProtection() * 1000L) : 0;
    }

    private void setTeleportProtection(boolean protect) {
        teleportProtectEndTime = protect ? System.currentTimeMillis() + (CharacterSettings.teleportProtection() * 1000L) : 0;
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
        return client;
    }

    public String getIPAddress() {
        return client.getHostAddress();
    }

    public Location getCurrentSkillWorldPosition() {
        return currentSkillWorldPosition;
    }

    public void setCurrentSkillWorldPosition(Location worldPosition) {
        currentSkillWorldPosition = worldPosition;
    }

    @Override
    public void enableSkill(Skill skill) {
        super.enableSkill(skill);
        removeTimeStamp(skill);
    }

    private boolean needCpUpdate() {
        final double currentCp = getCurrentCp();

        if ((currentCp <= 1.0) || (getMaxCp() < MAX_STATUS_BAR_PX)) {
            return true;
        }

        if ((currentCp <= cpUpdateDecCheck) || (currentCp >= cpUpdateIncCheck)) {
            if (currentCp == getMaxCp()) {
                cpUpdateIncCheck = currentCp + 1;
                cpUpdateDecCheck = currentCp - cpUpdateInterval;
            } else {
                final double doubleMulti = currentCp / cpUpdateInterval;
                int intMulti = (int) doubleMulti;

                cpUpdateDecCheck = cpUpdateInterval * (doubleMulti < intMulti ? --intMulti : intMulti);
                cpUpdateIncCheck = cpUpdateDecCheck + cpUpdateInterval;
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

        if ((currentMp <= mpUpdateDecCheck) || (currentMp >= mpUpdateIncCheck)) {
            if (currentMp == getMaxMp()) {
                mpUpdateIncCheck = currentMp + 1;
                mpUpdateDecCheck = currentMp - mpUpdateInterval;
            } else {
                final double doubleMulti = currentMp / mpUpdateInterval;
                int intMulti = (int) doubleMulti;

                mpUpdateDecCheck = mpUpdateInterval * (doubleMulti < intMulti ?   --intMulti : intMulti);
                mpUpdateIncCheck = mpUpdateDecCheck + mpUpdateInterval;
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

        if ((party != null) && (needCpUpdate || needHpUpdate || needMpUpdate)) {
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
            party.broadcastToPartyMembers(this, partyWindow);
        }

        // In duel MP updated only with CP or HP
        if (isInDuel && (needCpUpdate || needHpUpdate)) {
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
        sendPacket(new UserInfo(this, types));
        broadcastCharInfo();
    }

    public final void broadcastCharInfo() {
        var charInfo = new ExCharInfo(this);
        checkBroadcast(charInfo);
        World.getInstance().forEachVisibleObject(this, Player.class, player -> sendPacketAndUpdateRelation(charInfo, player), this::isVisibleFor);
    }

    private void sendPacketAndUpdateRelation(ServerPacket packet, Player player) {
        player.sendPacket(packet);
        updateRelation(player);
    }

    public final void broadcastTitleInfo() {
        broadcastUserInfo(UserInfoType.CLAN);
        broadcastPacket(new NicknameChanged(this));
    }

    @Override
    public final void broadcastPacket(ServerPacket packet) {
        if (packet instanceof ExCharInfo) {
            throw new IllegalArgumentException("ExCharInfo is being send via broadcastPacket. Do NOT do that! Use broadcastCharInfo() instead.");
        }
        sendPacket(packet);
        super.broadcastPacket(packet);
    }

    @Override
    public void broadcastPacket(ServerPacket packet, int radius) {
        if (packet instanceof ExCharInfo) {
            LOGGER.warn("ExCharInfo is being send via broadcastPacket. Do NOT do that! Use broadcastCharInfo() instead.");
        }
        sendPacket(packet);
        super.broadcastPacket(packet, radius);
    }

    /**
     * @return the Alliance Identifier of the Player.
     */
    @Override
    public int getAllyId() {
        return zeroIfNullOrElse(clan, Clan::getAllyId);
    }

    public int getAllyCrestId() {
        return zeroIfNullOrElse(clan, Clan::getAllyCrestId);
    }

    /**
     * Send a Server->Client packet StatusUpdate to the Player.
     */
    @Override
    public void sendPacket(ServerPacket packet) {
        client.sendPacket(packet);
    }

    public void sendPackets(ServerPacket... packets) {
        client.sendPackets(packets);
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
            party.distributeItem(this, itemId, itemCount, false, target);
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
        if (isAlikeDead()) {
            return;
        }

        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);

        if(!(object instanceof Item target)) {
            LOGGER.warn("{} trying to pickup wrong target {}", this, object);
            return;
        }

        sendPacket(new StopMove(this));
        synchronized (target) {
            if (!canPickUpItem(target)) {
                sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            if (target.getItemLootShedule() != null && (target.getOwnerId() == getObjectId() || isInLooterParty(target.getOwnerId()))) {
                target.resetOwnerTimer();
            }

            target.pickupMe(this);
            if (GeneralSettings.saveDroppedItems()) {
                ItemsOnGroundManager.getInstance().removeObject(target);
            }
        }

        onPickupItem(target);
    }

    private void onPickupItem(Item target) {
        if (target.getTemplate().hasExImmediateEffect()) {
            pickUpAutoConsumeItem(target);
        } else {
            if (target.getItemType() instanceof ArmorType || target.getItemType() instanceof WeaponType) {
                broadcastPickUpEquipment(target);
            }

            if (isInParty()) {
                party.distributeItem(this, target);
            } else if ((target.getId() == CommonItem.ADENA) && (inventory.getAdenaInstance() != null)) {
                addAdena("Pickup", target.getCount(), null, true);
                ItemEngine.getInstance().destroyItem("Pickup", target, this, null);
            } else {
                addItem("Pickup", target, null, true);
                checkPickupAmmunition(target);
            }
        }
    }

    private void checkPickupAmmunition(Item target) {
        final Item weapon = inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
        if (weapon != null) {
            final EtcItem etcItem = target.getEtcItem();
            if (etcItem != null) {
                final EtcItemType itemType = etcItem.getItemType();
                if ((weapon.getItemType() == WeaponType.BOW && itemType == EtcItemType.ARROW) || ((weapon.getItemType() == WeaponType.CROSSBOW || weapon.getItemType() == WeaponType.TWO_HAND_CROSSBOW) && itemType == EtcItemType.BOLT)) {
                    inventory.findAmmunitionForCurrentWeapon();
                }
            }
        }
    }

    private boolean canPickUpItem(Item target) {
        var canPickup = true;
        if (!target.isSpawned()) {
            canPickup = false;
        } else if(!checkItemOwnerProtection(target)){
            sendPacket(buildFailedToPickupMessage(target));
            canPickup = false;
        }
        else if ((!isInParty() || party.getDistributionType() == PartyDistributionType.FINDERS_KEEPERS) && !inventory.validateCapacity(target)) {
            sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            canPickup = false;
        }
        return canPickup;
    }

    private boolean checkItemOwnerProtection(Item target) {
        if (!(target.getDropProtection().tryPickUp(this) || isInLooterParty(target.getOwnerId()) )) {
           return false;
        }
        return !isInvulnerable() || canOverrideCond(PcCondOverride.ITEM_CONDITIONS);
    }

    private void broadcastPickUpEquipment(Item target) {
        SystemMessage message;
        if (target.getEnchantLevel() > 0) {
            message = getSystemMessage(SystemMessageId.ATTENTION_C1_HAS_PICKED_UP_S2_S3).addPcName(this).addInt(target.getEnchantLevel());
        } else {
            message = getSystemMessage(SystemMessageId.ATTENTION_C1_HAS_PICKED_UP_S2).addPcName(this);
        }
        message.addItemName(target.getId());
        broadcastPacket(message, 1400);
    }

    private void pickUpAutoConsumeItem(Item target) {
        final IItemHandler handler = ItemHandler.getInstance().getHandler(target.getEtcItem());
        if (handler == null) {
            LOGGER.warn("No item handler registered for item ID: {}.", target.getId());
        } else {
            handler.useItem(this, target, false);
        }
        ItemEngine.getInstance().destroyItem("Consume", target, this, null);
    }

    private SystemMessage buildFailedToPickupMessage(Item target) {
        SystemMessage message;
        if (target.getId() == CommonItem.ADENA) {
            message = getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA).addLong(target.getCount());
        } else if (target.getCount() > 1) {
            message = getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S2_S1_S).addItemName(target).addLong(target.getCount());
        } else {
            message = getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1).addItemName(target);
        }
        return message;
    }

    public boolean canOpenPrivateStore() {
        return !isSellingBuffs && !isAlikeDead() && !isInOlympiadMode() && !isMounted() && !isInsideZone(ZoneType.NO_STORE) && !isCastingNow();
    }

    public void tryOpenPrivateBuyStore() {
        // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
        if (canOpenPrivateStore()) {
            if ((privateStoreType == PrivateStoreType.BUY) || (privateStoreType == PrivateStoreType.BUY_MANAGE)) {
                setPrivateStoreType(PrivateStoreType.NONE);
            }
            if (privateStoreType == PrivateStoreType.NONE) {
                if (sitting) {
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

    public final PreparedMultisellList getMultiSell() {
        return currentMultiSell;
    }

    public final void setMultiSell(PreparedMultisellList list) {
        currentMultiSell = list;
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
        newTarget = validateNewTarget(newTarget);

        final WorldObject oldTarget = getTarget();

        if (oldTarget != null) {
            if (oldTarget.equals(newTarget)) {
                if (newTarget.getObjectId() != getObjectId()) {
                    sendPacket(new ValidateLocation(newTarget));
                }
                return;
            }

            oldTarget.removeStatusListener(this);
        }

        if (newTarget instanceof Creature target) {
            onCreatureTargetSelected(target);
        }

        if (newTarget == null && getTarget() != null) {
            broadcastPacket(new TargetUnselected(this));
        }
        super.setTarget(newTarget);
    }

    private void onCreatureTargetSelected(Creature target) {
        if (target.getObjectId() != getObjectId()) {
            sendPacket(new ValidateLocation(target));
        }

        sendPacket(new MyTargetSelected(this, target));
        target.addStatusListener(this);
        sendPacket(new StatusUpdate(target).addUpdate(StatusUpdateType.MAX_HP, target.getMaxHp()).addUpdate(StatusUpdateType.CUR_HP, (int) target.getCurrentHp()));
        Broadcast.toKnownPlayers(this, new TargetSelected(getObjectId(), target.getObjectId(), getX(), getY(), getZ()));
        sendPacket(new ExAbnormalStatusUpdateFromTarget(target));
    }

    private WorldObject validateNewTarget(WorldObject newTarget) {
        if (newTarget != null) {
            final boolean isInParty = (GameUtils.isPlayer(newTarget) && isInParty() && party.isMember(newTarget.getActingPlayer()));

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
        return newTarget;
    }

    /**
     * Return the active weapon instance (always equipped in the right hand).
     */
    @Override
    public Item getActiveWeaponInstance() {
        return inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
    }

    /**
     * Return the active weapon item (always equipped in the right hand).
     */
    @Override
    public Weapon getActiveWeaponItem() {
        final Item weapon = getActiveWeaponInstance();
        if (isNull(weapon)) {
            return fistsWeaponItem;
        }

        return (Weapon) weapon.getTemplate();
    }


    /**
     * Return the secondary ItemTemplate item (always equipped in the left hand).<BR>
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
     * <li>Manage Karma gain for attacker and Karma loss for the killed Player</li>
     * <li>If the killed Player has Karma, manage Drop Item</li>
     * <li>Kill the Player</li>
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

            applyDeathPenalty(killer, pk);
        }

        if (getReputation() < 0) {
            final int newRep = getReputation() - (getReputation() / 4);
            setReputation(newRep < -20 ? newRep : 0);
        }

        stopCurrentTasks();
        notifySummons();

        if (hasCharmOfCourage) {
            if (isInSiege()) {
                reviveRequest(this, false, 0);
            }
            hasCharmOfCourage = false;
            sendPacket(new EtcStatusUpdate(this));
        }

        doIfNonNull(getInstanceWorld(), instance -> instance.onDeath(this));
        AntiFeedManager.getInstance().setLastDeathTime(getObjectId());
        return true;
    }

    private void notifySummons() {
        if (isMounted()) {
            stopFeed();
        }

        if (!cubics.isEmpty()) {
            cubics.values().forEach(CubicInstance::deactivate);
            cubics.clear();
        }

        if (agathionId != 0) {
            setAgathionId(0);
        }

        stopRentPet();
    }

    private void stopCurrentTasks() {
        if (isFakeDeath()) {
            stopFakeDeath(true);
        }

        if(autoPlaySettings.isActive()) {
            AutoPlayEngine.getInstance().stopAutoPlay(this);
        }
        stopWaterTask();
    }

    private void applyDeathPenalty(Creature killer, Player pk) {
        data.setExpBeforeDeath(0);
        Collection<Item> droppedItems = deathPenaltyDropItems(killer);
        sendPacket(new ExDieInfo(lastDamages, droppedItems));

        final boolean insidePvpZone = isInsideZone(ZoneType.PVP) || isInsideZone(ZoneType.SIEGE);

        if (!insidePvpZone && (pk != null)) {
            final Clan pkClan = pk.getClan();
            if (pkClan != null && clan != null) {
                final ClanWar clanWar = clan.getWarWith(pkClan.getId());
                if ((clanWar != null) && AntiFeedManager.getInstance().check(killer, this)) {
                    clanWar.onKill(pk, this);
                }
            }
        }

        if (!isLucky() && !insidePvpZone) {
            calculateDeathExpPenalty(killer);
        }
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
            if (Config.REWARD_PVP_ITEM && pvpFlag != 0) {
                killer.addItem("PvP Item Reward", Config.REWARD_PVP_ITEM_ID, Config.REWARD_PVP_ITEM_AMOUNT, this, Config.REWARD_PVP_ITEM_MESSAGE);
            }

            if (Config.REWARD_PK_ITEM && pvpFlag == 0) {
                killer.addItem("PK Item Reward", Config.REWARD_PK_ITEM_ID, Config.REWARD_PK_ITEM_AMOUNT, this, Config.REWARD_PK_ITEM_MESSAGE);
            }
        }

        if (Config.ANNOUNCE_PK_PVP && !killer.isGM()) {
            String msg;
            if (pvpFlag == 0) {
                msg = Config.ANNOUNCE_PK_MSG.replace("$killer", killer.getName()).replace("$target", getName());
            } else {
                msg = Config.ANNOUNCE_PVP_MSG.replace("$killer", killer.getName()).replace("$target", getName());
            }

            if (Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE) {
                Broadcast.toAllOnlinePlayers( getSystemMessage(SystemMessageId.S1).addString(msg));
            } else {
                Broadcast.toAllOnlinePlayers(msg, false);
            }
        }
    }

    private Collection<Item> deathPenaltyDropItems(Creature killer) {
        if (Event.isParticipant(this) || (killer == null)) {
            return Collections.emptyList();
        }

        final Player pk = killer.getActingPlayer();
        if (getReputation() >= 0 && nonNull(pk) && falseIfNullOrElse(pk.getClan(), c -> c.isAtWarWith(clan))) {
            return Collections.emptyList();
        }

        if ( (!isInsideZone(ZoneType.PVP) || isNull(pk)) && (!isGM() || Config.KARMA_DROP_GM)) {
            return onDieDropItems(killer);
        }

        return Collections.emptyList();
    }

    private Collection<Item> onDieDropItems(Creature killer) {
        var isKarmaDrop = isPlayable(killer) && (getReputation() < 0) && (data.getPk() >= Config.KARMA_PK_LIMIT);
        var dropPercent = calculateDropPercent(killer, isKarmaDrop);

        if (Rnd.chance(dropPercent)) {
            int dropEquip = isKarmaDrop ? Config.KARMA_RATE_DROP_EQUIP : Config.PLAYER_RATE_DROP_EQUIP;
            int dropEquipWeapon = isKarmaDrop ? Config.KARMA_RATE_DROP_EQUIP_WEAPON : Config.PLAYER_RATE_DROP_EQUIP_WEAPON;
            int rateDropItem = isKarmaDrop ? Config.KARMA_RATE_DROP_ITEM : Config.PLAYER_RATE_DROP_ITEM;
            int dropLimit = isKarmaDrop ? Config.KARMA_DROP_LIMIT : Config.PLAYER_DROP_LIMIT;

            return dropItems(killer, isKarmaDrop, dropEquip, dropEquipWeapon, rateDropItem, dropLimit);
        }
        return Collections.emptyList();
    }

    private Collection<Item> dropItems(Creature killer, boolean isKarmaDrop, int dropEquip, int dropEquipWeapon, int rateDropItem, int dropLimit) {
        Collection<Item> droppedItems = new ArrayList<>();
        int dropCount = 0;
        int itemDropPercent;

        for (Item itemDrop : inventory.getItems()) {
            if (isUndropable(itemDrop)) {
                continue;
            }

            if (itemDrop.isEquipped()) {
                itemDropPercent = itemDrop.getType2() == ItemTemplate.TYPE2_WEAPON ? dropEquipWeapon : dropEquip;
                inventory.unEquipItemInSlot(InventorySlot.fromId(itemDrop.getLocationSlot()));
            } else {
                itemDropPercent = rateDropItem;
            }

            if (Rnd.chance(itemDropPercent)) {
                dropItem("DieDrop", itemDrop, killer);
                droppedItems.add(itemDrop);
                LOGGER.info("{} has dropped {} {}. With karma {}", this, itemDrop.getCount(), itemDrop, isKarmaDrop);
                if (++dropCount >= dropLimit) {
                    break;
                }
            }
        }
        return droppedItems;
    }

    private boolean isUndropable(Item itemDrop) {
        return itemDrop.isTimeLimitedItem() || // Dont drop Time Limited Items
                !itemDrop.isDropable() || (itemDrop.getId() == CommonItem.ADENA) || // Adena
                (itemDrop.getType2() == ItemTemplate.TYPE2_QUEST) || // Quest Items
                ((pet != null) && (pet.getControlObjectId() == itemDrop.getId())) || // Control Item of active pet
                (Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_ITEMS, itemDrop.getId()) >= 0) || // Item listed in the non droppable item list
                (Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS, itemDrop.getId()) >= 0 // Item listed in the non droppable pet item list
                );
    }

    private double calculateDropPercent(Creature killer, boolean isKarmaDrop) {
        var dropPercent = 0.;
        if(isKarmaDrop) {
            dropPercent =  Config.KARMA_RATE_DROP * getStats().getValue(Stat.REDUCE_DEATH_PENALTY_BY_PVP, 1);
        } else if(isNpc(killer)) {
            var penaltyStat  = killer.isRaid() ? Stat.REDUCE_DEATH_PENALTY_BY_RAID : Stat.REDUCE_DEATH_PENALTY_BY_MOB;
            dropPercent = Config.PLAYER_RATE_DROP * getStats().getValue(penaltyStat, 1);
        }
        return dropPercent;
    }

    public void onPlayeableKill(Playable killedPlayable) {
        final Player killedPlayer = killedPlayable.getActingPlayer();

        if (!hasKillReputationGain(killedPlayer)) {
            return;
        }

        calculatePvPReputation(killedPlayer, isSummon(killedPlayable));
        sendPacket(new UserInfo(this, UserInfoType.SOCIAL));
        checkItemRestriction();
    }

    private boolean hasKillReputationGain(Player killedPlayer) {
        if (killedPlayer == null || this == killedPlayer) {
            return false;
        }

        if ((isInDuel() && killedPlayer.isInDuel()) || killInSiege(killedPlayer)) {
            return false;
        }

        return !(isInsideZone(ZoneType.PVP) && killedPlayer.isInsideZone(ZoneType.PVP));
    }

    private boolean killInSiege(Player killedPlayer) {
        if (isInsideZone(ZoneType.SIEGE) && killedPlayer.isInsideZone(ZoneType.SIEGE)) {
            if (!isSiegeFriend(killedPlayer)) {
                final Clan targetClan = killedPlayer.getClan();
                if (clan != null && targetClan != null) {
                    clan.addSiegeKill();
                    targetClan.addSiegeDeath();
                }
            }
            return true;
        }
        return false;
    }

    private void calculatePvPReputation( Player killedPlayer, boolean isSummon) {
        if (checkIfPvP(killedPlayer)) {
            if (killedPlayer.getReputation() < 0) {
                final int levelDiff = killedPlayer.getLevel() - getLevel();
                if ((getReputation() >= 0) && (levelDiff < 11) && (levelDiff > -11)) // TODO: Time check, same player can't be killed again in 8 hours
                {
                    setReputation(getReputation() + Config.REPUTATION_INCREASE);
                }
            }

            setPvpKills(data.getPvP() + 1);

            updatePvpTitleAndColor(true);
        } else if (getReputation() > 0 && data.getPk() == 0) {
            setReputation(0);
            setPkKills(1);
        } else {
            setReputation(getReputation() - Formulas.calculateKarmaGain(getPkKills(), isSummon));
            setPkKills(getPkKills() + 1);
        }
    }

    public void updatePvpTitleAndColor(boolean broadcastInfo) {
        if (Config.PVP_COLOR_SYSTEM_ENABLED) {
            final var pvpKills = data.getPvP();
            if (pvpKills >= Config.PVP_AMOUNT1 && data.getPvP() < Config.PVP_AMOUNT2) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT1 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT1);
            } else if (pvpKills >= Config.PVP_AMOUNT2 && pvpKills < Config.PVP_AMOUNT3) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT2 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT2);
            } else if (pvpKills >= Config.PVP_AMOUNT3 && pvpKills < Config.PVP_AMOUNT4) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT3 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT3);
            } else if (pvpKills >= Config.PVP_AMOUNT4 && pvpKills < Config.PVP_AMOUNT5) {
                setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT4 + " \u00AE");
                appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT4);
            } else if (pvpKills >= Config.PVP_AMOUNT5) {
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

        if (pvpFlag == 0) {
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

        if (isInDuel && (player_target.getDuelId() == getDuelId())) {
            return;
        }
        if ((!isInsideZone(ZoneType.PVP) || !player_target.isInsideZone(ZoneType.PVP)) && (player_target.getReputation() >= 0)) {
            if (checkIfPvP(player_target)) {
                setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_PVP_TIME);
            } else {
                setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_NORMAL_TIME);
            }
            if (pvpFlag == 0) {
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
     * @param restorePercent the exp percentage that will be restored
     */
    public void restoreExp(double restorePercent) {
        if (data.getExpBeforeDeath() > 0) {
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
     * @param killer the killer
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
            } else if (isPlayable(killer)) {
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

        if (isPlayable(killer) && atWarWith(killer.getActingPlayer())) {
            lostExp /= 4.0;
        }

        data.setExpBeforeDeath(getExp());
        getStats().removeExp(lostExp);
    }

    private void stopAllTimers() {
        stopHpMpRegeneration();
        stopWarnUserTakeBreak();
        stopWaterTask();
        stopFeed();
        clearPetData();
        storePetFood(mountNpcId);
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

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public IntMap<Summon> getServitors() {
        return servitors == null ? Containers.emptyIntMap() : servitors;
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
        final List<Summon> summons = new ArrayList<>(getServitors().values());

        if (pet != null) {
            summons.add(pet);
        }

        return summons;
    }

    public Trap getTrap() {
        for (Npc summonedNpc : getSummonedNpcs()) {
            if(summonedNpc instanceof Trap trap) {
                return trap;
            }
        }
        return null;
    }

    public void addServitor(Summon servitor) {
        if (servitors == null) {
            initServitors();
        }
        servitors.put(servitor.getObjectId(), servitor);
    }

    private synchronized void initServitors() {
        if (servitors == null) {
            servitors = new CHashIntMap<>(1);
        }
    }

    /**
     * @return the Summon of the Player or null.
     */
    public Set<TamedBeast> getTrainedBeasts() {
        return tamedBeast;
    }

    public void addTrainedBeast(TamedBeast tamedBeast) {
        if (this.tamedBeast == null) {
            initTamedBeasts();
        }
        this.tamedBeast.add(tamedBeast);
    }

    private synchronized void initTamedBeasts() {
        if (this.tamedBeast == null) {
            this.tamedBeast = ConcurrentHashMap.newKeySet();
        }
    }

    /**
     * @return the Player requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
     */
    public Request getRequest() {
        return request;
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
     * @param requester the player that did the requester
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
     * @param partner the Player that will participate in the transaction
     */
    public void onTransactionRequest(Player partner) {
        requestExpireTime = WorldTimeController.getInstance().getGameTicks() + (REQUEST_TIMEOUT * WorldTimeController.TICKS_PER_SECOND);
        partner.setActiveRequester(this);
    }

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

    public void onTradeFinish(boolean successful) {
        activeTradeList = null;
        sendPacket(TradeDone.COMPLETED);
        if (successful) {
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
        return Util.isNotEmpty(manufactureItems);
    }

    /**
     * Get the manufacture items map of this player.
     *
     * @return the the manufacture items map
     */
    public IntMap<ManufactureItem> getManufactureItems() {
        if (manufactureItems == null) {
            initManufactureItems();
        }
        return manufactureItems;
    }

    private synchronized void initManufactureItems() {
        if (manufactureItems == null) {
            manufactureItems = new CHashIntMap<>();
        }
    }

    /**
     * Get the store name, if any.
     *
     * @return the store name
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * Set the store name.
     *
     * @param name the store name to set
     */
    public void setStoreName(String name) {
        storeName = name == null ? "" : name;
    }

    /**
     * @return the _buyList object of the Player.
     */
    public TradeList getSellList() {
        if (sellList == null) {
            sellList = new TradeList(this);
        }
        return sellList;
    }

    /**
     * @return the _buyList object of the Player.
     */
    public TradeList getBuyList() {
        if (buyList == null) {
            buyList = new TradeList(this);
        }
        return buyList;
    }

    public PrivateStoreType getPrivateStoreType() {
        return privateStoreType;
    }

    public void setPrivateStoreType(PrivateStoreType privateStoreType) {
        this.privateStoreType = privateStoreType;
    }

    @Override
    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;

        if (clan == null) {
            setTitle("");
            data.setClanId(0);
            clanPrivileges = new EnumIntBitmask<>(ClanPrivilege.class, false);
            setPowerGrade(0);
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

        data.setClanId(clan.getId());
    }

    /**
     * @return True if the Player is the leader of its clan.
     */
    public boolean isClanLeader() {
        if (clan == null) {
            return false;
        }
        return getObjectId() == clan.getLeaderId();
    }

    /**
     * Disarm the player's weapon.
     *
     * @return {@code true} if the player was disarmed or doesn't have a weapon to disarm, {@code false} otherwise.
     */
    public boolean disarmWeapons() {
        final Item wpn = inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
        if (wpn == null) {
            return true;
        }

        var modified = inventory.unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
        if(modified.isEmpty()) {
            return false;
        }
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
    private boolean disarmShield() {
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

    public boolean mount(Summon summon) {
        var success = false;
        if (summon != null && summon.isMountable() && !isMounted() && !isBetrayed()) {
            success = canMount(summon) && doMount(summon);
        } else if (isRentedPet()) {
            stopRentPet();
            success = true;
        } else if (isMounted()) {
            success = canUnMount() && dismount();
        }
        return success;
    }

    private boolean mount(int petId, int level, int controlObjectId, boolean useFood) {
        if (!disarmWeapons() || !disarmShield() || isTransformed()) {
            return false;
        }

        getEffectList().stopAllToggles();
        setMount(petId, level);
        setMountObjectID(controlObjectId);
        clearPetData();
        broadcastPacket(new Ride(this));
        broadcastUserInfo();
        if (useFood) {
            startFeed(petId);
        }
        return true;
    }

    public boolean mount(int npcId, int controlItemObjId, boolean useFood) {
        return mount(npcId, controlItemObjId, getLevel(), useFood);
    }

    private boolean canUnMount() {
        if (mountType == MountType.WYVERN && isInsideZone(ZoneType.NO_LANDING)) {
            sendPacket(SystemMessageId.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION);
            return false;
        } else if (isHungry()) {
            sendPacket(SystemMessageId.A_HUNGRY_MOUNT_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
            return false;
        }
        return true;
    }

    private boolean canMount(Summon pet) {
        if (!FeatureSettings.allowRideInSiege() && isInsideZone(ZoneType.SIEGE)) {
            return false;
        }
        var canMount = !isTransformed();
        if (isDead()) {
            sendPacket(SystemMessageId.A_MOUNT_CANNOT_BE_RIDDEN_WHEN_DEAD);
            canMount = false;
        } else if (isInCombat()) {
            sendPacket(SystemMessageId.A_MOUNT_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE);
            canMount = false;
        } else if (sitting) {
            sendPacket(SystemMessageId.A_MOUNT_CAN_BE_RIDDEN_ONLY_WHEN_STANDING);
            canMount = false;
        } else if (isFishing()) {
            sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            canMount = false;
        }
        return canMount && canBeMounted(pet);
    }

    private boolean canBeMounted(Summon pet) {
        var canBeMounted = true;
        if (pet.isDead()) {
            sendPacket(SystemMessageId.A_DEAD_MOUNT_CANNOT_BE_RIDDEN);
            canBeMounted = false;
        } else if (pet.isInCombat() || pet.isRooted()) {
            sendPacket(SystemMessageId.A_MOUNT_IN_BATTLE_CANNOT_BE_RIDDEN);
            canBeMounted = false;
        }
        else if (pet.isHungry()) {
            sendPacket(SystemMessageId.A_HUNGRY_MOUNT_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
            canBeMounted = false;
        } else if (!GameUtils.checkIfInRange(200, this, pet, true)) {
            sendPacket(SystemMessageId.YOU_ARE_TOO_FAR_AWAY_FROM_YOUR_MOUNT_TO_RIDE);
            canBeMounted = false;
        }
        return canBeMounted;
    }

    private boolean doMount(Summon pet) {
        var mounted = mount(pet.getId(), pet.getLevel(), pet.getControlObjectId(), true);
        if(mounted) {
            pet.unSummon(this);
        }
        return mounted;
    }

    public boolean dismount() {
        WaterZone water = ZoneEngine.getInstance().findFirstZone(getX(), getY(), getZ() - 300, WaterZone.class);
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
            ThreadPool.schedule(() -> {
                if (isInWater()) {
                    broadcastUserInfo();
                }
            }, 1500);
        }

        final boolean wasFlying = isFlying();
        sendPacket(new SetupGauge(3, 0, 0));
        final int petId = mountNpcId;
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
        return System.currentTimeMillis() - uptime;
    }

    void setUptime(long time) {
        uptime = time;
    }

    
    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable() || isTeleportProtected();
    }

    /**
     * Return True if the Player has a Party in progress.
     */
    @Override
    public boolean isInParty() {
        return party != null;
    }

    public void joinParty(Party party) {
        if (party != null) {
            // First set the party otherwise this wouldn't be considered
            // as in a party into the Creature.updateEffectIcons() call.
            this.party = party;
            party.addPartyMember(this);
        }
    }

    /**
     * Manage the Leave Party task of the Player.
     */
    public void leaveParty() {
        if (isInParty()) {
            party.removePartyMember(this, Party.MessageType.DISCONNECTED);
            party = null;
        }
    }

    @Override
    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public boolean isInCommandChannel() {
        return isInParty() && party.isInCommandChannel();
    }

    public CommandChannel getCommandChannel() {
        return (isInCommandChannel()) ? party.getCommandChannel() : null;
    }

    @Override
    public boolean isGM() {
        return accessLevel.isGM();
    }


    public void setAccountAccessLevel(int level) {
        AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(getAccountName(), level, 0));
    }

    @Override
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    private void updateAndBroadcastStatus() {
        refreshOverloaded(true);
        broadcastUserInfo();
    }

    public void broadcastReputation() {
        broadcastUserInfo(UserInfoType.SOCIAL);
    }

    public void setOnlineStatus(boolean isOnline, boolean updateInDb) {
        this.isOnline = isOnline;
        if (updateInDb) {
            getDAO(PlayerDAO.class).updateOnlineStatus(objectId, isOnline(), System.currentTimeMillis());
        }
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
    void restoreCharData() {
        restoreSkills();
        macros.restoreMe();
        restoreHennas();
        restoreTeleportBookmark();
        restoreRecipeBook();

        if (CharacterSettings.storeRecipeShopList()) {
            restoreRecipeShopList();
        }

        restorePetInventoryItems();
        contacts.restore();
    }

    private void restoreShortCuts() {
        shortcuts.restoreMe();
        sendPacket(new ShortCutInit());
        forEachShortcut(s -> {
            if(s.isActive()) {
                client.sendPacket(new ExActivateAutoShortcut(s.getClientId(), true));
            }
        });
    }

    private void restoreRecipeBook() {
        getDAO(PlayerDAO.class).findAllRecipes(objectId).forEach(this::restoreRecipe);
    }

    private void restoreRecipe(int recipeId) {
        var recipe = RecipeData.getInstance().getRecipeList(recipeId);
        if(recipe.isDwarvenRecipe()) {
            dwarvenRecipes.put(recipeId, recipe);
        } else {
            commonRecipes.put(recipeId, recipe);
        }
    }

    @Override
    public void storeMe() {
        store(true);
    }

    /**
     * Update Player stats in the characters table of the database.
     *
     * @param storeActiveEffects if true the current effects will be stored
     */
    public synchronized void store(boolean storeActiveEffects) {
        storeCharBase();
        storeEffect(storeActiveEffects);
        storeItemReuseDelay();
        if (CharacterSettings.storeRecipeShopList()) {
            storeRecipeShopList();
        }

        storeElementalSpirits();

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
            if(nonNull(warehouse)) {
                warehouse.updateDatabase();
            }
        }
        shortcuts.storeMe();
    }

    private void storeElementalSpirits() {
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
    }

    private void storeCharBase() {
        data.setExperience(getStats().getBaseExp());
        data.setLevel(getStats().getBaseLevel());
        data.setSp(getStats().getBaseSp());

        data.setMaxHp(getMaxHp());
        data.setHp(getCurrentHp());
        data.setMaxCp(getMaxCp());
        data.setCp(getCurrentCp());
        data.setMaxMp(getMaxMp());
        data.setMp(getCurrentMp());

        Location loc =  isNull(lastLoc) ? getLocation() : lastLoc;
        data.setX(loc.getX());
        data.setY(loc.getY());
        data.setZ(loc.getZ());
        data.setHeading(getHeading());

        data.setRace(getRace().ordinal());
        data.setBaseClass(getClassId().getId());
        data.setTitle(getTitle());
        data.setOnline(isOnline);
        data.setClanPrivileges(clanPrivileges.getBitmask());
        data.setName(getName());
        data.setBookMarkSlot(bookmarkSlot);
        data.setVitalityPoints(getStats().getBaseVitalityPoints());
        data.setLanguage(lang);

        if (uptime > 0) {
            data.addOnlineTime((System.currentTimeMillis() - uptime) / 1000);
        }

        getDAO(PlayerDAO.class).save(data);
    }

    @Override
    public void storeEffect(boolean storeEffects) {
        getDAO(PlayerDAO.class).deleteSavedSkills(objectId);

        if(storeEffects) {
            getDAO(ReuseInfoDAO.class).saveBuffInfoReuse(this, getEffectList().getEffects());
        }
    }

    private void storeItemReuseDelay() {
        getDAO(PlayerDAO.class).deleteSavedItemReuse(objectId);
        getDAO(ReuseInfoDAO.class).saveItemReuse(objectId, getItemReuseTimeStamps().values());
    }

    public boolean isOnline() {
        return isOnline;
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
     * @param store true if the skill should be stored
     * @return The L2Skill replaced or null if just added a new L2Skill
     */
    public Skill addSkill(Skill newSkill, boolean store) {
        // Add a skill to the Player _skills and its Func objects to the calculator set of the Player
        final Skill oldSkill = addSkill(newSkill);
        // Add or update a Player skill in the character_skills table of the database
        if (store) {
            storeSkill(newSkill);
        }
        updateShortCuts(newSkill.getId(), newSkill.getLevel());
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
            getDAO(PlayerDAO.class).deleteSkill(objectId, oldSkill.getId());
        }

        if (getTransformationId() > 0) {
            return oldSkill;
        }

        if (nonNull(skill) &&  !(skill.getId() >= 3080 && skill.getId() <= 3259)) { // exclude item skills ?! it's all ?
            deleteShortcuts(s -> s.getShortcutId() == skill.getId() && s.getType() == ShortcutType.SKILL);
        }
        return oldSkill;
    }

    private void storeSkill(Skill newSkill) {
        if(nonNull(newSkill)) {
            getDAO(PlayerDAO.class).saveSkill(objectId, newSkill.getId(), newSkill.getLevel());
        }
    }

    /**
     * Adds or updates player's skills in the database.
     *  @param newSkills     the list of skills to store
     *
     */
    private void storeSkills(List<Skill> newSkills) {
        if (newSkills.isEmpty()) {
            return;
        }
        getDAO(SkillsDAO.class).save(objectId, newSkills);
    }

    /**
     * Retrieve from the database all skills of this Player and add them to _skills.
     */
    private void restoreSkills() {
        for (Skill skill : getDAO(PlayerDAO.class).findSkills(objectId)) {
            if (skill == null) {
                LOGGER.warn("Skipped null skill while restoring player skills for player: {}", this);
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

    /**
     * Retrieve from the database all skill effects of this Player and add them to the player.
     */
    @Override
    public void restoreEffects() {
        var playerDAO = getDAO(PlayerDAO.class);
        playerDAO.findSavedSkill(objectId, this::restoreEffect);
        playerDAO.deleteSavedSkills(objectId);
    }

    private void restoreEffect(ResultSet resultSet) {
        try {
            var currentTime = System.currentTimeMillis();
            while (resultSet.next()) {
                final int remainingTime = resultSet.getInt("remaining_time");
                final long reuseDelay = resultSet.getLong("reuse_delay");
                final long sysTime = resultSet.getLong("systime");
                final int restoreType = resultSet.getInt("restore_type");

                final Skill skill = SkillEngine.getInstance().getSkill(resultSet.getInt("skill_id"), resultSet.getInt("skill_level"));
                if (skill == null) {
                    return;
                }

                final long time = sysTime - currentTime;
                if (time > 10) {
                    disableSkill(skill, time);
                    addTimeStamp(skill, reuseDelay, sysTime);
                }

                // Restore Type 1 The remaining skills lost effect upon logout but were still under a high reuse delay.
                if (restoreType > 0) {
                    return;
                }

                skill.applyEffects(this, this, false, remainingTime);
            }
        } catch (SQLException e) {
            LOGGER.warn("Could not restore {} active effect data ", this, e);
        }
    }

    private void restoreItemReuse() {
        var playerDAO = getDAO(PlayerDAO.class);
        playerDAO.findSavedItemReuse(objectId, this::restoreItemReuse);
        playerDAO.deleteSavedItemReuse(objectId);
    }

    private void restoreItemReuse(ResultSet resultSet) {
        try {
            var currentTime = System.currentTimeMillis();
            while (resultSet.next()) {
                int itemId = resultSet.getInt("itemId");
                long reuseDelay = resultSet.getLong("reuseDelay");
                long sysTime = resultSet.getLong("systime");
                boolean isInInventory = true;

                Item item = inventory.getItemByItemId(itemId);
                if (item == null) {
                    item = getWarehouse().getItemByItemId(itemId);
                    isInInventory = false;
                }

                if ((item != null) && (item.getId() == itemId) && (item.getReuseDelay() > 0)) {
                    long remainingTime = sysTime - currentTime;
                    if (remainingTime > 10) {
                        addTimeStamp((int) remainingTime, itemId, isInInventory, reuseDelay, sysTime, item);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Could not restore {} Item Reuse data: ", this, e);
        }
    }

    private void addTimeStamp(int remainingTime, int itemId, boolean isInInventory, long reuseDelay, long sysTime, Item item) {
        addTimeStampItem(item, reuseDelay, sysTime);

        if (isInInventory && item.isEtcItem()) {
            final int group = item.getSharedReuseGroup();
            if (group > 0) {
                sendPacket(new ExUseSharedGroupItem(itemId, group, remainingTime, (int) reuseDelay));
            }
        }
    }

    private void restoreHennas() {
        for (int i = 1; i < 4; i++) {
            hennas[i - 1] = null;
        }

        for (var entry : hennaRemoveSchedules.entrySet())
        {
            final ScheduledFuture<?> task = entry.getValue();
            if ((task != null) && !task.isCancelled() && !task.isDone())
            {
                task.cancel(true);
            }
            hennaRemoveSchedules.remove(entry.getKey());
        }

        getDAO(PlayerDAO.class).findHennas(objectId, this::restoreHenna);
        recalculateHennaStats();
    }

    private void restoreHenna(ResultSet resultSet) {
        var currentTime = System.currentTimeMillis();
        try {
            while (resultSet.next()) {
                int slot = resultSet.getInt("slot");
                int symbolId = resultSet.getInt("symbol_id");

                final Henna henna = HennaData.getInstance().getHenna(symbolId);

                if (henna.getDuration() > 0) {
                    final long remainingTime = getHennaDuration(slot) - currentTime;
                    if (remainingTime < 0) {
                        removeHenna(slot);
                        return;
                    }
                    hennaRemoveSchedules.put(slot, ThreadPool.schedule(new HennaDurationTask(this, slot), currentTime + remainingTime));
                }

                hennas[slot - 1] = henna;

                for (Skill skill : henna.getSkills()) {
                    addSkill(skill, false);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed restoring {}'s hennas.", this, e);
        }
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
            if (hennas[i] != null) {
                totalSlots--;
            }
        }

        return Math.max(totalSlots, 0);

    }

    /**
     * Remove a Henna of the Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this Player.
     *
     */
    public void removeHenna(int slot) {
        if ((slot < 1) || (slot > 3)) {
            return;
        }

        final Henna henna = hennas[slot - 1];
        if (henna == null) {
            return;
        }

        hennas[slot - 1] = null;

        getDAO(PlayerDAO.class).deleteHenna(objectId, slot);

        // Calculate Henna modifiers of this Player
        recalculateHennaStats();

        // Send Server->Client HennaInfo packet to this Player
        sendPacket(new HennaInfo(this));

        // Send Server->Client UserInfo packet to this Player
        final UserInfo ui = new UserInfo(this, false);
        ui.addComponentType(UserInfoType.BASE_STATS, UserInfoType.MAX_HPCPMP, UserInfoType.STATS, UserInfoType.SPEED);
        sendPacket(ui);

        final long remainingTime = getHennaDuration(slot) - System.currentTimeMillis();
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
            setHennaDuration(0, slot);
            if (hennaRemoveSchedules.get(slot) != null) {
                hennaRemoveSchedules.get(slot).cancel(false);
                hennaRemoveSchedules.remove(slot);
            }
        }

        // Remove henna skills
        for (Skill skill : henna.getSkills()) {
            removeSkill(skill, false);
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerHennaRemove(this, henna), this);
    }

    /**
     * Add a Henna to the Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this Player.
     *
     * @param henna the henna to add to the player.
     * @return {@code true} if the henna is added to the player, {@code false} otherwise.
     */
    public boolean addHenna(Henna henna) {
        for (int slot = 1; slot < 4; slot++) {
            if (hennas[slot - 1] == null) {
                hennas[slot - 1] = henna;

                // Calculate Henna modifiers of this Player
                recalculateHennaStats();
                getDAO(PlayerDAO.class).saveHenna(objectId, henna.getDyeId(), slot);

                // Task for henna duration
                if (henna.getDuration() > 0) {
                    setHennaDuration(System.currentTimeMillis() + (henna.getDuration() * 60000L), slot);
                    hennaRemoveSchedules.put(slot, ThreadPool.schedule(new HennaDurationTask(this, slot), System.currentTimeMillis() + (henna.getDuration() * 60000L)));
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
    private void recalculateHennaStats() {
        hennaBaseStats.clear();
        for (Henna henna : hennas) {
            if (henna == null) {
                continue;
            }

            for (Entry<BaseStats, Integer> entry : henna.getBaseStats().entrySet()) {
                hennaBaseStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
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
        return hennas[slot - 1];
    }

    /**
     * @return the henna holder for this player.
     */
    public Henna[] getHennaList() {
        return hennas;
    }

    public int getHennaValue(BaseStats stat) {
        return hennaBaseStats.getOrDefault(stat, 0);
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
        if (isSelfAttacking(attacker)) {
            return false;
        }

        if (GameUtils.isMonster(attacker)) {
            return true;
        }

        return checkAttackerInstance(attacker);
    }

    private boolean checkAttackerInstance(Creature attacker) {
        if (attacker instanceof Defender && clan != null) {
            final Siege siege = SiegeManager.getInstance().getSiege(this);
            return ((siege != null) && siege.checkIsAttacker(clan));
        } 
         
        if (attacker instanceof Guard || attacker instanceof FriendlyMob) {
            return getReputation() < 0; // Guards attack only PK players.
        }

        return cabBeAttacked(attacker);
    }

    private boolean cabBeAttacked(Creature attacker) {
        var canAttack = getReputation() < 0 || pvpFlag > 0;
        if(isPlayable(attacker)) {
            final var attackerPlayer = attacker.getActingPlayer();
            if(attackerPlayer.isInOlympiadMode()) {
                canAttack = isInOlympiadMode() && olympiadStart && attackerPlayer.getOlympiadMatchId() == getOlympiadMatchId();
            } else if(duelState == Duel.DUELSTATE_DUELLING && getDuelId() == attackerPlayer.getDuelId()) {
                canAttack = true;
            } else if (isInSameGroup(attacker, attackerPlayer)) {
                canAttack = false;
            } else if (isOnEvent()) {
                canAttack = true;
            } else if (isInsideZone(ZoneType.PEACE)) {
                canAttack = false;
            } else if (isEnemy(attackerPlayer)) {
                canAttack = true;
            } else if (isAlly(attacker, attackerPlayer)) {
                canAttack = false;
            } else if (isInPvp(attackerPlayer)) {
                canAttack = true;
            }
        }
        return canAttack;
    }

    private boolean isEnemy(Player attackerPlayer) {
        var enemy = false;
        if(nonNull(clan)) {
            final Clan attackerClan = attackerPlayer.getClan();
            enemy =  isOppositeSiegeSide(attackerClan) || isMutualWar(attackerPlayer, attackerClan);
        }
        return enemy || isInSingleBattleZone(attackerPlayer);
    }

    private boolean isInSingleBattleZone(Player attackerPlayer) {
        return isInsideZone(ZoneType.PVP) && attackerPlayer.isInsideZone(ZoneType.PVP) && !(isInsideZone(ZoneType.SIEGE) && attackerPlayer.isInsideZone(ZoneType.SIEGE));
    }

    private boolean isInSameGroup(Creature attacker, Player attackerPlayer) {
        if(isInParty() && party.isMember(attackerPlayer)) {
            return true;
        }

        return isOnCustomEvent && getTeam() == attacker.getTeam();
    }

    private boolean isInPvp(Player attackerPlayer) {
        if (isInsideZone(ZoneType.PVP) && attackerPlayer.isInsideZone(ZoneType.PVP) && isInsideZone(ZoneType.SIEGE) && attackerPlayer.isInsideZone(ZoneType.SIEGE)) {
            return true;
        }

        return getPvpFlag() > 0;
    }

    private boolean isAlly(Creature attacker, Player attackerPlayer) {
        if (nonNull(clan) && clan.isMember(attackerPlayer.getObjectId())) {
            return true;
        }

        return GameUtils.isPlayer(attacker) && getAllyId() != 0 && getAllyId() == attackerPlayer.getAllyId();
    }

    private boolean isMutualWar(Player attackerPlayer, Clan attackerClan) {
        if (nonNull(attackerClan) && (!wantsPeace()) && (!attackerPlayer.wantsPeace())) {
            final ClanWar war = attackerClan.getWarWith(getClanId());
            return nonNull(war) && war.getState() == ClanWarState.MUTUAL;
        }
        return false;
    }

    private boolean isOppositeSiegeSide(Clan attackerClan) {
        final Siege siege = SiegeManager.getInstance().getSiege(this);
        if (nonNull(siege)) {
            if (siege.checkIsDefender(attackerClan) && siege.checkIsAttacker(clan)) {
                return true;
            }
            return siege.checkIsAttacker(attackerClan) && siege.checkIsDefender(clan);
        }
        return false;
    }

    private boolean isSelfAttacking(Creature attacker) {
        if (isNull(attacker)) {
            return true;
        }
        return attacker == this || attacker == pet || attacker.hasServitor(attacker.getObjectId());
    }

    /**
     * Check if the active L2Skill can be casted.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Check if the skill isn't toggle and is offensive</li>
     * <li>Check if the target is in the skill cast range</li>
     * <li>Check if the skill is Spoil type and if the target isn't already spoiled</li>
     * <li>Check if the caster owns enough consumed Item, enough HP and MP to cast the skill</li>
     * <li>Check if the caster isn't sitting</li>
     * <li>Check if all skills are enabled and this skill is enabled</li>
     * <li>Check if the caster own the weapon needed</li>
     * <li>Check if the skill is active</li>
     * <li>Check if all casting conditions are completed</li>
     * <li>Notify the AI with AI_INTENTION_CAST and target</li>
     * </ul>
     *
     * @param skill    The Skill to use
     * @param forceUse used to force ATTACK on players
     * @param dontMove used to prevent movement, if not in range
     */
    @Override
    public boolean useSkill(Skill skill, Item item, boolean forceUse, boolean dontMove) {
        if (!checkUseSkill(skill, item)) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        final WorldObject target = skill.getTarget(this, forceUse, dontMove, true);

        if (isNull(target)) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        if (isInOlympiadMode() && isPlayable(target) && ( (!isOlympiadStart() && skill.isBad()) || getOlympiadMatchId() != target.getActingPlayer().getOlympiadMatchId())) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        if (!checkCastSkillConditions(skill, item, forceUse, dontMove, target)) {
            return false;
        }
        queuedSkill = null;
        getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target, item, forceUse, dontMove);
        return true;
    }

    private boolean checkCastSkillConditions(Skill skill, Item item, boolean forceUse, boolean dontMove, WorldObject target) {
        // Check if all casting conditions are completed
        if (!skill.checkCondition(this, target)) {
            sendPacket(ActionFailed.STATIC_PACKET);

            // Upon failed conditions, next action is called.
            if (skill.getNextAction() != NextActionType.NONE && target != this && target.isAutoAttackable(this)) {
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
        if ((isCastingNow(SkillCastingType.NORMAL) || isCastingNow(SkillCastingType.NORMAL_SECOND))) {
            if (isNull(item)) {
                setQueuedSkill(skill, forceUse, dontMove);
            }
            sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        return true;
    }

    private boolean checkUseSkill(Skill skill, Item item) {
        if (skill.isPassive() || !hasUseSkillState(skill, item)) {
            return false;
        }

        if (!checkToggleSkill(skill)) {
            return false;
        }

        if (isSkillDisabled(skill)) {
            sendSkillReuseTimeMessage(skill);
            return false;
        }

        if(!SkillCaster.checkSkillConsume(this, skill)) {
            return false;
        }
        return !isNull(currentSkillWorldPosition) || skill.getTargetType() != TargetType.GROUND;
    }

    private boolean hasUseSkillState(Skill skill, Item item) {
        var canUseSkill = true;
        if (inObserverMode) {
            sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
            canUseSkill = false;
        } else if (sitting && item == null) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_ACTIONS_AND_SKILLS_WHILE_THE_CHARACTER_IS_SITTING);
            canUseSkill = false;
        } else if (isFishing() && !skill.hasAnyEffectType(EffectType.FISHING, EffectType.FISHING_START)) {
            sendPacket(SystemMessageId.ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME);
            canUseSkill = false;
        } else if (!CharacterSettings.allowPKTeleport() && getReputation() < 0 && skill.hasAnyEffectType(EffectType.TELEPORT)) {
            canUseSkill = false;
        } else if (!skill.canCastWhileDisabled() && (isControlBlocked() || hasBlockActions())) {
            canUseSkill = false;
        }
        return canUseSkill && !isDead();
    }

    private boolean checkToggleSkill(Skill skill) {
        if (skill.isToggle() && (isMounted() || stopSkillEffects(true, skill.getId())) ) {
            return false;
        }
        return !isFakeDeath();
    }

    private void sendSkillReuseTimeMessage(Skill skill) {
        final SystemMessage sm;
        if (hasSkillReuse(skill.getReuseHashCode())) {
            final int remainingTime = (int) (getSkillRemainingReuseTime(skill.getReuseHashCode()) / 1000);
            final int hours = remainingTime / 3600;
            final int minutes = (remainingTime % 3600) / 60;
            final int seconds = (remainingTime % 60);
            if (hours > 0) {
                sm = getSystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME)
                        .addSkillName(skill).addInt(hours).addInt(minutes);
            } else if (minutes > 0) {
                sm = getSystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME)
                        .addSkillName(skill).addInt(minutes);
            } else {
                sm = getSystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME).addSkillName(skill);
            }
            sm.addInt(seconds);
        } else {
            sm = getSystemMessage(SystemMessageId.S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE).addSkillName(skill);
        }

        sendPacket(sm);
    }

    public boolean isInLooterParty(int LooterId) {
        final Player looter = World.getInstance().findPlayer(LooterId);

        // if Player is in a CommandChannel
        if (isInParty() && party.isInCommandChannel() && (looter != null)) {
            return party.getCommandChannel().getMembers().contains(looter);
        }

        if (isInParty() && (looter != null)) {
            return party.getMembers().contains(looter);
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
        return mountType != MountType.NONE;
    }

    private boolean checkLandingState() {
        // Check if char is in a no landing zone
        if (isInsideZone(ZoneType.NO_LANDING)) {
            return true;
        }
        // if this is a castle that is currently being siege, and the rider is NOT a castle owner
        // he cannot land.
        // castle owner is the leader of the clan that owns the castle where the pc is
        return isInsideZone(ZoneType.SIEGE) && !((getClan() != null) && (CastleManager.getInstance().getCastle(this) == CastleManager.getInstance().getCastleByOwner(getClan())) && (this == getClan().getLeader().getPlayerInstance()));
    }

    private void setMount(int npcId, int npcLevel) {
        final MountType type = MountType.findByNpcId(npcId);
        switch (type) {
            case NONE -> setIsFlying(false);
            case STRIDER -> addStriderAssaultSkill();
            case WYVERN -> setIsFlying(true);
        }

        mountType = type;
        mountNpcId = npcId;
        mountLevel = npcLevel;
    }

    private void addStriderAssaultSkill() {
        if (isNoble()) {
            addSkill(CommonSkill.STRIDER_SIEGE_ASSAULT.getSkill(), false);
        }
    }

    /**
     * @return the type of Pet mounted (0 : none, 1 : Strider, 2 : Wyvern, 3: Wolf).
     */
    public MountType getMountType() {
        return mountType;
    }

    @Override
    public final void stopAllEffects() {
        super.stopAllEffects();
        updateAndBroadcastStatus();
    }

    @Override
    public final void stopAllEffectsExceptThoseThatLastThroughDeath() {
        super.stopAllEffectsExceptThoseThatLastThroughDeath();
        updateAndBroadcastStatus();
    }

    private void stopCubics() {
        if (!cubics.isEmpty()) {
            cubics.values().forEach(CubicInstance::deactivate);
            cubics.clear();
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
     */
    public void setInventoryBlockingStatus(boolean val) {
        inventoryDisable = val;
        if (val) {
            ThreadPool.schedule(new InventoryEnableTask(this), 1500);
        }
    }

    /**
     * @return True if the Inventory is disabled.
     */
    public boolean isInventoryDisabled() {
        return inventoryDisable;
    }

    public void addCubic(CubicInstance cubic) {
        cubics.put(cubic.getTemplate().getId(), cubic);
    }

    /**
     * Get the player's cubics.
     *
     * @return the cubics
     */
    public IntMap<CubicInstance> getCubics() {
        return cubics;
    }

    /**
     * Get the player cubic by cubic ID, if any.
     *
     * @param cubicId the cubic ID
     * @return the cubic with the given cubic ID, {@code null} otherwise
     */
    public CubicInstance getCubicById(int cubicId) {
        return cubics.get(cubicId);
    }

    /**
     * @return the _lastFolkNpc of the Player corresponding to the last Folk which one the player talked.
     */
    public Npc getLastFolkNPC() {
        return lastFolkNpc;
    }

    public void setLastFolkNPC(Npc folkNpc) {
        lastFolkNpc = folkNpc;
    }

    public EnumIntBitmask<ClanPrivilege> getClanPrivileges() {
        return clanPrivileges;
    }

    public void setClanPrivileges(EnumIntBitmask<ClanPrivilege> clanPrivileges) {
        this.clanPrivileges = clanPrivileges.clone();
    }

    public boolean hasClanPrivilege(ClanPrivilege privilege) {
        return clanPrivileges.has(privilege);
    }

    public SocialStatus getSocialStatus() {
        return socialStatus;
    }

    public void setSocialStatus(SocialStatus status) {
        socialStatus = status;
        checkItemRestriction();
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
        return bookmarkSlot;
    }

    public void setBookMarkSlot(int slot) {
        bookmarkSlot = slot;
        sendPacket(new ExGetBookMarkInfoPacket(this));
    }

    @Override
    public void sendMessage(String message) {
        sendPacket(SystemMessage.sendString(message));
    }

    public void setObserving(boolean state) {
        inObserverMode = state;
        setTarget(null);
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

    private void setLastLocation() {
        lastLoc = new Location(getX(), getY(), getZ());
    }

    private void unsetLastLocation() {
        lastLoc = null;
    }

    public void leaveObserverMode() {
        setTarget(null);
        setInstance(null);
        teleToLocation(lastLoc, false);
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
        inObserverMode = false;

        broadcastUserInfo();
    }

    public int getOlympiadSide() {
        return olympiadSide;
    }

    public void setOlympiadSide(int i) {
        olympiadSide = i;
    }

    public int getOlympiadMatchId() {
        return olympiadMatchId;
    }

    public void setOlympiadMatchId(int id) {
        olympiadMatchId = id;
    }

    public boolean isInObserverMode() {
        return inObserverMode;
    }

    public AdminTeleportType getTeleMode() {
        return teleportType;
    }

    public void setTeleMode(AdminTeleportType type) {
        teleportType = type;
    }

    public boolean isMessageRefusing() {
        return messageRefusing;
    }

    public void setMessageRefusing(boolean mode) {
        messageRefusing = mode;
        sendPacket(new EtcStatusUpdate(this));
    }

    public boolean getDietMode() {
        return dietMode;
    }

    public void setDietMode(boolean mode) {
        dietMode = mode;
    }

    public boolean isTradeRefusing() {
        return tradeRefusing;
    }

    public void setTradeRefusing(boolean mode) {
        tradeRefusing = mode;
    }

    public BlockList getBlockList() {
        return blockList;
    }

    /**
     * @return returns {@code true} if player is target player can accepting messages from the current player, {@code false} otherwise
     */
    public boolean isNotBlocked(Player player) {
        return !player.getBlockList().isBlockAll() && !player.getBlockList().isInBlockList(this);
    }

    public void setOlympiadMode(OlympiadMode mode) {
        olympiadMode = mode;
    }

    public boolean isInOlympiadMode() {
        return olympiadMode != OlympiadMode.NONE;
    }

    public void setIsOlympiadStart(boolean b) {
        olympiadStart = b;
    }

    public boolean isOlympiadStart() {
        return olympiadStart;
    }

    public boolean isHero() {
        return hero;
    }

    public void setHero(boolean hero) {
        if (hero && (data.getBaseClass() == data.getClassId())) {
            for (Skill skill : SkillTreesData.getInstance().getHeroSkillTree()) {
                addSkill(skill, false); // Don't persist hero skills into database
            }
        } else {
            for (Skill skill : SkillTreesData.getInstance().getHeroSkillTree()) {
                removeSkill(skill, false, true); // Just remove skills from non-hero players
            }
        }
        this.hero = hero;
        sendSkillList();
    }

    public boolean isInDuel() {
        return isInDuel;
    }

    public void setStartingDuel() {
        startingDuel = true;
    }

    public int getDuelId() {
        return duelId;
    }

    public int getDuelState() {
        return duelState;
    }

    public void setDuelState(int mode) {
        duelState = mode;
    }

    /**
     * Sets up the duel state using a non 0 duelId.
     *
     * @param duelId 0=not in a duel
     */
    public void setIsInDuel(int duelId) {
        if (duelId > 0) {
            isInDuel = true;
            duelState = Duel.DUELSTATE_DUELLING;
            this.duelId = duelId;
        } else {
            if (duelState == Duel.DUELSTATE_DEAD) {
                enableAllSkills();
                getStatus().startHpMpRegeneration();
            }
            isInDuel = false;
            duelState = Duel.DUELSTATE_NODUEL;
            this.duelId = 0;
        }
        startingDuel = false;
    }

    /**
     * This returns a SystemMessage stating why the player is not available for duelling.
     *
     * @return S1_CANNOT_DUEL... message
     */
    public SystemMessage getNoDuelReason() {
        final SystemMessage sm = getSystemMessage(noDuelReason);
        sm.addPcName(this);
        noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
        return sm;
    }

    /**
     * Checks if this player might join / start a duel.<br>
     * To get the reason use getNoDuelReason() after calling this function.
     *
     * @return true if the player might join/start a duel.
     */
    public boolean canDuel() {
        if (!checkDuelStatus()) {
            return false;
        }

        if (privateStoreType != PrivateStoreType.NONE) {
            noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE;
            return false;
        }

        if (isMounted() || isInBoat()) {
            noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_FENRIR_OR_STRIDER;
            return false;
        }

        return checkDuelActivities();
    }

    private boolean checkDuelActivities() {
        if (isOnEvent()) // custom event message
        {
            noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
            return false;
        }
        if (isInDuel || startingDuel) {
            noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL;
            return false;
        }
        if (isInOlympiadMode()) {
            noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_OR_THE_CEREMONY_OF_CHAOS;
            return false;
        }

        if (isFishing()) {
            noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING;
            return false;
        }
        if (isInsideZone(ZoneType.PVP) || isInsideZone(ZoneType.PEACE) || isInsideZone(ZoneType.SIEGE)) {
            noDuelReason = SystemMessageId.C1_IS_IN_AN_AREA_WHERE_DUEL_IS_NOT_ALLOWED_AND_YOU_CANNOT_APPLY_FOR_A_DUEL;
            return false;
        }
        return true;
    }

    private boolean checkDuelStatus() {
        if (isInCombat() || isJailed()) {
            noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
            return false;
        }
        if (isDead() || isAlikeDead() || ((getCurrentHp() < (getMaxHp() / 2d)) || (getCurrentMp() < (getMaxMp() / 2d)))) {
            noDuelReason = SystemMessageId.C1_S_HP_OR_MP_IS_BELOW_50_AND_CANNOT_DUEL;
            return false;
        }
        return true;
    }

    public boolean isNoble() {
        return data.isNobless();
    }

    public void setNoble(boolean val) {
        if (val) {
            SkillTreesData.getInstance().getNobleSkillAutoGetTree().forEach(skill -> addSkill(skill, false));
        } else {
            SkillTreesData.getInstance().getNobleSkillTree().forEach(skill -> removeSkill(skill, false, true));
        }
        data.setNobless(val);
        sendSkillList();
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

    private boolean wantsPeace() {
        return data.wantsPeace();
    }

    public void sendSkillList() {
        if (skillListRefreshTask == null) {
            skillListRefreshTask = ThreadPool.schedule(() -> {
                sendSkillList(0);
                skillListRefreshTask = null;
            }, 1000);
        }
    }

    public void sendSkillList(int lastLearnedSkillId) {
        boolean isDisabled = false;
        final SkillList sl = new SkillList();

        for (Skill s : getSkillList()) {
            if (clan != null) {
                isDisabled = s.isClanSkill() && (clan.getReputationScore() < 0);
            }

            sl.addSkill(s.getDisplayId(), s.getReuseDelayGroup(), s.getDisplayLevel(), s.getSubLevel(), s.isPassive(), isDisabled, s.isEnchantable());
        }
        if (lastLearnedSkillId > 0) {
            sl.setLastLearnedSkillId(lastLearnedSkillId);
        }
        sendPacket(sl);
        sendPacket(new AcquireSkillList(this));
    }

    public int getBaseClass() {
        return data.getBaseClass();
    }

    public void setBaseClass(int baseClass) {
        data.setBaseClass(baseClass);
    }

    public int getActiveClass() {
        return data.getClassId();
    }

    private void setClassTemplate(int classId) {
        data.setClassId(classId);

        final PlayerTemplate pcTemplate = PlayerTemplateData.getInstance().getTemplate(classId);
        if (pcTemplate == null) {
            LOGGER.error("Missing template for classId: " + classId);
            throw new Error();
        }
        // Set the template of the Player
        setTemplate(pcTemplate);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerProfessionChange(this, pcTemplate), this);
    }

    public boolean isClassLocked() {
        return classLock.isLocked();
    }

    public void stopWarnUserTakeBreak() {
        if (taskWarnUserTakeBreak != null) {
            taskWarnUserTakeBreak.cancel(true);
            taskWarnUserTakeBreak = null;
        }
    }

    private void startWarnUserTakeBreak() {
        if (taskWarnUserTakeBreak == null) {
            taskWarnUserTakeBreak = ThreadPool.scheduleAtFixedRate(new WarnUserTakeBreakTask(this), 3600000, 3600000);
        }
    }

    public void stopRentPet() {
        if (taskRentPet != null) {
            // if the rent of a wyvern expires while over a flying zone, tp to down before unmounting
            if (checkLandingState() && (mountType == MountType.WYVERN)) {
                teleToLocation(TeleportWhereType.TOWN);
            }

            if (dismount()) // this should always be true now, since we teleported already
            {
                taskRentPet.cancel(true);
                taskRentPet = null;
            }
        }
    }

    public boolean isRentedPet() {
        return nonNull(taskRentPet);
    }

    public void stopWaterTask() {
        if (taskWater != null) {
            taskWater.cancel(false);
            taskWater = null;
            sendPacket(new SetupGauge(getObjectId(), 2, 0));
        }
    }

    private void startWaterTask() {
        if (!isDead() && (taskWater == null)) {
            final int timeInWater = (int) getStats().getValue(Stat.BREATH, 60000);

            sendPacket(new SetupGauge(getObjectId(), 2, timeInWater));
            taskWater = ThreadPool.scheduleAtFixedRate(new WaterTask(this), timeInWater, 1000);
        }
    }

    public boolean isInWater() {
        return nonNull(taskWater);
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
        restoreItemReuse();
        restoreShortCuts();
        restoreEffects();

        // TODO : Need to fix that hack!
        if (!isDead()) {
            setCurrentCp(originalCp);
            setCurrentHp(originalHp);
            setCurrentMp(originalMp);
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

        ZoneEngine.getInstance().forEachZone(this, z -> z.onPlayerLoginInside(this));
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogin(this), this);
    }

    public long getLastAccess() {
        return data.getLastAccess();
    }

    @Override
    public void doRevive() {
        super.doRevive();
        sendPacket(new EtcStatusUpdate(this));
        revivePet = false;
        reviveRequested = 0;
        revivePower = 0;

        if (isMounted()) {
            startFeed(mountNpcId);
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

    public void reviveRequest(Player reviver, boolean isPet, int power) {
        if (isResurrectionBlocked()) {
            return;
        }

        if (reviveRequested == 1) {
            if (revivePet == isPet) {
                reviver.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
            } else if (isPet) {
                reviver.sendPacket(SystemMessageId.A_PET_CANNOT_BE_RESURRECTED_WHILE_IT_S_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING); // A pet cannot be resurrected while it's owner is in the process of resurrecting.
            } else {
                reviver.sendPacket(SystemMessageId.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
            }
            return;
        }
        if ((isPet && (pet != null) && pet.isDead()) || (!isPet && isDead())) {
            reviveRequested = 1;
            revivePower = Formulas.calculateSkillResurrectRestorePercent(power, reviver);
            revivePet = isPet;

            if (hasCharmOfCourage()) {
                final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW);
                dlg.addTime(60000);
                sendPacket(dlg);
                return;
            }

            final long restoreExp = Math.round(((data.getExpBeforeDeath() - getExp()) * revivePower) / 100);
            final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT);
            dlg.addPcName(reviver);
            dlg.addLong(restoreExp);
            dlg.addInt(power);
            sendPacket(dlg);
        }
    }

    public void reviveAnswer(int answer) {
        if ((reviveRequested != 1) || (!isDead() && !revivePet) || (revivePet && (pet != null) && !pet.isDead())) {
            return;
        }

        if (answer == 1) {
            if (!revivePet) {
                if (revivePower != 0) {
                    doRevive(revivePower);
                } else {
                    doRevive();
                }
            } else if (pet != null) {
                if (revivePower != 0) {
                    pet.doRevive(revivePower);
                } else {
                    pet.doRevive();
                }
            }
        }
        reviveRequested = 0;
        revivePower = 0;
    }

    public boolean isReviveRequested() {
        return (reviveRequested == 1);
    }

    public boolean isRevivingPet() {
        return revivePet;
    }

    public void removeReviving() {
        reviveRequested = 0;
        revivePower = 0;
    }

    public void onActionRequest() {
        if (isSpawnProtected()) {
            setSpawnProtection(false);

            if (!isInsideZone(ZoneType.PEACE)) {
                sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS);
            }

            if (CharacterSettings.restoreSummonOnReconnect() && !hasSummon()) {
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
    public void teleToLocation(ILocational loc, int randomOffset) {
        if ((vehicle != null) && !vehicle.isTeleporting()) {
            setVehicle(null);
        }

        if (isFlyingMounted() && (loc.getZ() < -1005)) {
            super.teleToLocation(loc.getX(), loc.getY(), -1005, loc.getHeading());
        }
        super.teleToLocation(loc, randomOffset);
    }

    @Override
    public void teleToLocation(ILocational loc, boolean allowRandomOffset) {
        if ((vehicle != null) && !vehicle.isTeleporting()) {
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

        if ((CharacterSettings.teleportProtection() > 0) && !isInOlympiadMode() && !getActingPlayer().isInBattle()) {
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

        if(nonNull(servitors)) {
            for (Summon summon : servitors.values()) {
                summon.setFollowStatus(false);
                summon.teleToLocation(getLocation(), false);
                ((SummonAI) summon.getAI()).setStartFollowController(true);
                summon.setFollowStatus(true);
                summon.setInstance(getInstanceWorld());
                summon.updateAndBroadcastStatus(0);
            }
        }

        // show movie if available
        if (movieHolder != null) {
            sendPacket(new ExStartScenePlayer(movieHolder.getMovie()));
        }
        if(nonNull(autoPlaySettings) && autoPlaySettings.isActive()) {
            AutoPlayEngine.getInstance().stopAutoPlay(this);
        }
    }

    public void setLastServerPosition(int x, int y, int z) {
        lastServerPosition.setXYZ(x, y, z);
    }

    public Location getLastServerPosition() {
        return lastServerPosition;
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
        if (!snoopListener.isEmpty()) {
            final Snoop sn = new Snoop(getObjectId(), getName(), type, name, _text);
            if(snoopListener.size() > 1) {
                sn.sendInBroadcast(true);
            }

            for (Player pci : snoopListener) {
                if (pci != null) {
                    pci.sendPacket(sn);
                }
            }
        }
    }

    public void addSnooper(Player pci) {
        snoopListener.add(pci);
    }

    public void removeSnooper(Player pci) {
        snoopListener.remove(pci);
    }

    public void addSnooped(Player pci) {
        snoopedPlayer.add(pci);
    }

    public void removeSnooped(Player pci) {
        snoopedPlayer.remove(pci);
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

        htmlActionOriginObjectIds[scope.ordinal()] = npcObjId;
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
                return htmlActionOriginObjectIds[i];
            }
        }

        return -1;
    }

    /**
     * Performs following tests:
     * <ul>
     * <li>Inventory contains item</li>
     * <li>Item owner id == owner id</li>
     * <li>It isn't pet control item while mounting pet or pet summoned</li>
     * <li>It isn't active enchant item</li>
     * <li>It isn't wear item</li>
     * </ul>
     *
     * @param objectId item object id
     * @param action   just for login purpose
     */
    public boolean validateItemManipulation(int objectId, String action) {
        final Item item = inventory.getItemByObjectId(objectId);

        if (isNull(item) || item.getOwnerId() != getObjectId()) {
            LOGGER.debug("player {} tried to {} item he is not owner of", this, action);
            return false;
        }

        if (( nonNull(pet) && pet.getControlObjectId() == objectId) || mountObjectID == objectId) {
            return false;
        }

        return !isProcessingItem(objectId);
    }

    /**
     * @return Returns the inBoat.
     */
    public boolean isInBoat() {
        return (vehicle != null) && vehicle.isBoat();
    }

    public Boat getBoat() {
        return (Boat) vehicle;
    }

    public boolean isInShuttle() {
        return vehicle instanceof Shuttle;
    }

    public Shuttle getShuttle() {
        return (Shuttle) vehicle;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle v) {
        if ((v == null) && (vehicle != null)) {
            vehicle.removePassenger(this);
        }

        vehicle = v;
    }

    public boolean isInVehicle() {
        return vehicle != null;
    }

    public boolean isInCrystallize() {
        return inCrystallize;
    }

    public void setInCrystallize(boolean inCrystallize) {
        this.inCrystallize = inCrystallize;
    }

    public Location getInVehiclePosition() {
        return inVehiclePosition;
    }

    public void setInVehiclePosition(Location pt) {
        inVehiclePosition = pt;
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
     * <li>Remove all WorldObject from _knownObjects and _knownPlayer of the Creature then cancel Attack or Cast and notify AI</li>
     * <li>Close the connection with the client</li>
     * </ul>
     * <br>
     * Remember this method is not to be used to half-ass disconnect players! This method is dedicated only to erase the player from the world.<br>
     * If you intend to disconnect a player please use {@link Disconnection}
     */
    @Override
    public boolean deleteMe() {
        AutoPlayEngine.getInstance().stopTasks(this);
        ZoneEngine.getInstance().forEachZone(this, z -> z.onPlayerLogoutInside(this));

        setOnlineStatus(false, true);
        stopActionsInProgress();

        storeRecommendations();

        if (isGM()) {
            AdminData.getInstance().deleteGm(this);
        }

        if (inObserverMode) {
            setLocationInvisible(lastLoc);
        }

        if (vehicle != null) {
            vehicle.oustPlayer(this);
        }

        removeFromWorld();
        cleanUpItems();

        notifyClanLogout();
        removeSnoop();

        if (Event.isParticipant(this)) {
            Event.savePlayerEventStatus(this);
        }

        notifyFriends(FriendStatus.OFFLINE);
        blockList.playerLogout();

        stopEffects();

        SaveTaskManager.getInstance().remove(this);
        return super.deleteMe();
    }

    private void stopEffects() {
        stopCubics();
        getEffectList().stopAllToggles();
        getEffectList().stopAllPassives(false, false);
        getEffectList().stopAllOptions(false, false);
    }

    private void removeFromWorld() {
        ZoneEngine.getInstance().removeFromZones(this);
        decayMe();
        leaveParty();
        unSummonServitors();

        final Instance inst = getInstanceWorld();
        if (inst != null) {
            inst.onPlayerLogout(this);
        }
    }

    private void cleanUpItems() {
        inventory.deleteMe();
        clearWarehouse();

        freight.deleteMe();
        clearRefund();
    }

    private void stopActionsInProgress() {
        if (Config.ENABLE_BLOCK_CHECKER_EVENT && (handysBlockCheckerEventArena != -1)) {
            HandysBlockCheckerManager.getInstance().onDisconnect(this);
        }

        forgetTarget();
        stopMove(null);
        leaveMatchingRoom();

        if (isFlying()) {
            removeSkill(SkillEngine.getInstance().getSkill(CommonSkill.WYVERN_BREATH.getId(), 1));
        }


        stopAllTimers();
        setIsTeleporting(false);
        RecipeController.getInstance().requestMakeItemAbort(this);

        if (isChannelized()) {
            getSkillChannelized().abortChannelization();
        }

        setActiveRequester(null);
        cancelActiveTrade();
    }

    private void notifyClanLogout() {
        if (clan != null) {
            clan.onMemberLogout(this);
            clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
            clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
        }
    }

    private void removeSnoop() {
        for (Player player : snoopedPlayer) {
            player.removeSnooper(this);
        }

        for (Player player : snoopListener) {
            player.removeSnooped(this);
        }
    }

    private void unSummonServitors() {
        if (hasSummon()) {
                Summon pet = this.pet;
                if (pet != null) {
                    pet.setRestoreSummon(true);
                    pet.unSummon(this);
                    // Dead pet wasn't un summoned, broadcast npc info changes (pet will be without owner name - means owner offline)
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
        }
    }

    private void leaveMatchingRoom() {
        if (matchingRoom != null) {
            matchingRoom.deleteMember(this, false);
        }
        MatchingRoomManager.getInstance().removeFromWaitingList(this);
    }

    public int getInventoryLimit() {
        int limit = CharacterSettings.maxSlotInventory(this);
        return limit + (int) getStats().getValue(Stat.INVENTORY_NORMAL, 0);
    }

    public int getWareHouseLimit() {
        int limit = CharacterSettings.maxSlotWarehouse(getRace());
        return limit + (int) getStats().getValue(Stat.STORAGE_PRIVATE, 0);
    }

    public int getPrivateSellStoreLimit() {
        int limit = CharacterSettings.maxSlotStoreSell(getRace());
        return limit + (int) getStats().getValue(Stat.TRADE_SELL, 0);
    }

    public int getPrivateBuyStoreLimit() {
        var limit =  CharacterSettings.maxSlotStoreBuy(getRace());
        return limit + (int) getStats().getValue(Stat.TRADE_BUY, 0);
    }

    public int getDwarfRecipeLimit() {
        return CharacterSettings.dwarfRecipeLimit() + (int) getStats().getValue(Stat.RECIPE_DWARVEN, 0);
    }

    public int getCommonRecipeLimit() {
        return CharacterSettings.recipeLimit() + (int) getStats().getValue(Stat.RECIPE_COMMON, 0);
    }

    /**
     * @return Returns the mountNpcId.
     */
    public int getMountNpcId() {
        return mountNpcId;
    }

    /**
     * @return Returns the mountLevel.
     */
    public int getMountLevel() {
        return mountLevel;
    }

    private void setMountObjectID(int newID) {
        mountObjectID = newID;
    }

    public SkillUseHolder getQueuedSkill() {
        return queuedSkill;
    }

    public void setQueuedSkill(Skill queuedSkill, boolean ctrlPressed, boolean shiftPressed) {
        if (queuedSkill == null) {
            this.queuedSkill = null;
            return;
        }
        this.queuedSkill = new SkillUseHolder(queuedSkill, null, ctrlPressed, shiftPressed);
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
        if (fameTask == null) {
            fameTask = ThreadPool.scheduleAtFixedRate(new FameTask(this, fameFixRate), delay, delay);
        }
    }

    public void stopFameTask() {
        if (fameTask != null) {
            fameTask.cancel(false);
            fameTask = null;
        }
    }

    public int getPowerGrade() {
        return data.getPowerGrade();
    }

    public void setPowerGrade(int power) {
        data.setPowerGrade(power);
    }

    public int getChargedSouls() {
        return souls;
    }

    public void increaseSouls(int count) {
        souls += count;
        final SystemMessage sm = getSystemMessage(SystemMessageId.YOUR_SOUL_COUNT_HAS_INCREASED_BY_S1_IT_IS_NOW_AT_S2);
        sm.addInt(count);
        sm.addInt(souls);
        sendPacket(sm);
        restartSoulTask();
        sendPacket(new EtcStatusUpdate(this));
    }

    public boolean decreaseSouls(int count) {
        souls -= count;

        if (souls < 0) {
            souls = 0;
        }

        if (souls == 0) {
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
        souls = 0;
        stopSoulTask();
        sendPacket(new EtcStatusUpdate(this));
    }

    private void restartSoulTask() {
        if (soulTask != null) {
            soulTask.cancel(false);
            soulTask = null;
        }
        soulTask = ThreadPool.schedule(new ResetSoulsTask(this), 600000);

    }

    private void stopSoulTask() {
        if (soulTask != null) {
            soulTask.cancel(false);
            soulTask = null;
        }
    }

    @Override
    public Player getActingPlayer() {
        return this;
    }

    @Override
    public void sendDamageMessage(Creature target, Skill skill, int damage, double elementalDamage, boolean critic, boolean miss) {
        if (miss) {
            sendMissMessage(target, skill);
            return;
        }

        if (critic) {
            sendCriticalMessage(target, skill);
        }

        SystemMessage sm = getDamageMessage(target, damage, elementalDamage);
        if(sm != null) {
            sendPacket(sm);
        }
    }

    private SystemMessage getDamageMessage(Creature target, int damage, double elementalDamage) {
        SystemMessage sm = null;

        if ((target.isHpBlocked() && !GameUtils.isNpc(target)) || (GameUtils.isPlayer(target) && target.isAffected(EffectFlag.DUELIST_FURY) && !isAffected(EffectFlag.FACEOFF))) {
            sm = getSystemMessage(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);

        } else if (GameUtils.isDoor(target) || target instanceof ControlTower) {
            sm = getSystemMessage(SystemMessageId.YOU_HIT_FOR_S1_DAMAGE);
            sm.addInt(damage);

        } else if (this != target){
            sm = creatureDamageMessage(target, damage, elementalDamage);

        }
        return sm;
    }

    private SystemMessage creatureDamageMessage(Creature target, int damage, double elementalDamage) {
        SystemMessage sm;
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
        return sm;
    }

    private void sendCriticalMessage(Creature target, Skill skill) {
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

    private void sendMissMessage(Creature target, Skill skill) {
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
    }

    public int getAgathionId() {
        return agathionId;
    }

    public void setAgathionId(int npcId) {
        agathionId = npcId;
    }

    public int getVitalityPoints() {
        return getStats().getVitalityPoints();
    }

    public void setVitalityPoints(int points, boolean quiet) {
        getStats().setVitalityPoints(points, quiet);
    }

    public void updateVitalityPoints(int points, boolean useRates) {
        getStats().updateVitalityPoints(points, useRates);
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
        if (isNull(transformSkills)) {
            synchronized (this) {
                if (isNull(transformSkills)) {
                    transformSkills = new HashIntMap<>();
                }
            }
        }
        transformSkills.put(skill.getId(), skill);
    }

    public boolean hasTransformSkills() {
        return nonNull(transformSkills);
    }

    public synchronized void removeAllTransformSkills() {
        transformSkills = null;
    }

    /**
     * @param skillId the id of the skill that this player might have.
     * @return {@code skill} object referred to this skill id that this player has, {@code null} otherwise.
     */
    @Override
    public final Skill getKnownSkill(int skillId) {
        Skill skill = null;
        if(nonNull(transformSkills)) {
            skill = transformSkills.get(skillId);
        }
        return isNull(skill) ? super.getKnownSkill(skillId) : skill;
    }

    /**
     * @return all visible skills that appear on Alt+K for this player.
     */
    public Collection<Skill> getSkillList() {
        Collection<Skill> currentSkills = getAllSkills();

        if (isTransformed()) {
            if (nonNull(transformSkills)) {
                // Include transformation skills and those skills that are allowed during transformation.
                currentSkills = currentSkills.stream().filter(Skill::allowOnTransform).collect(Collectors.toList());

                int revelationSkill = getRevelationSkillMainClass1();
                if (revelationSkill != 0)
                {
                    addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                }
                revelationSkill = getRevelationSkillMainClass2();
                if (revelationSkill != 0)
                {
                    addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
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

    private void startFeed(int npcId) {
        canFeed = npcId > 0;
        if (!isMounted()) {
            return;
        }
        if (hasPet()) {
            setCurrentFeed(pet.getCurrentFed());
            controlItemId = pet.getControlObjectId();
            sendPacket(new SetupGauge(3, (currentFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume()));
            if (!isDead()) {
                mountFeedTask = ThreadPool.scheduleAtFixedRate(new PetFeedTask(this), 10000, 10000);
            }
        } else if (canFeed) {
            setCurrentFeed(getMaxFeed());
            final SetupGauge sg = new SetupGauge(3, (currentFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume());
            sendPacket(sg);
            if (!isDead()) {
                mountFeedTask = ThreadPool.scheduleAtFixedRate(new PetFeedTask(this), 10000, 10000);
            }
        }
    }

    public void stopFeed() {
        if (mountFeedTask != null) {
            mountFeedTask.cancel(false);
            mountFeedTask = null;
        }
    }

    private void clearPetData() {
        petTemplate = null;
    }

    public final PetTemplate getPetData(int npcId) {
        if (petTemplate == null) {
            petTemplate = PetDataTable.getInstance().getPetTemplate(npcId);
        }
        return petTemplate;
    }

    private PetLevelData getPetLevelData(int npcId) {
        if (petLevelData == null) {
            petLevelData = PetDataTable.getInstance().getPetTemplate(npcId).getPetLevelData(getMountLevel());
        }
        return petLevelData;
    }

    public int getCurrentFeed() {
        return currentFeed;
    }

    public void setCurrentFeed(int num) {
        final boolean lastHungryState = isHungry();
        currentFeed = Math.min(num, getMaxFeed());
        final SetupGauge sg = new SetupGauge(3, (currentFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume());
        sendPacket(sg);
        // broadcast move speed change when strider becomes hungry / full
        if (lastHungryState != isHungry()) {
            broadcastUserInfo();
        }
    }

    public int getFeedConsume() {
        // if pet is attacking
        if (isAttackingNow()) {
            return getPetLevelData(mountNpcId).getPetFeedBattle();
        }
        return getPetLevelData(mountNpcId).getPetFeedNormal();
    }

    private int getMaxFeed() {
        return getPetLevelData(mountNpcId).getPetMaxFeed();
    }

    public boolean isHungry() {
        return hasPet() && canFeed && (currentFeed < ((getPetData(mountNpcId).getHungryLimit() / 100f) * getPetLevelData(mountNpcId).getPetMaxFeed()));
    }

    public void enteredNoLanding(int delay) {
        dismountTask = ThreadPool.schedule(new DismountTask(this), delay * 1000L);
    }

    public void exitedNoLanding() {
        if (dismountTask != null) {
            dismountTask.cancel(true);
            dismountTask = null;
        }
    }

    private void storePetFood(int petId) {
        if (controlItemId != 0 && petId != 0) {
            getDAO(PetDAO.class).updateFed(controlItemId, currentFeed);
        }
    }

    public void setIsInSiege(boolean b) {
        isInSiege = b;
    }

    public boolean isInSiege() {
        return isInSiege;
    }

    /**
     * @return {@code true} if the player is participating on a Hideout Siege, otherwise {@code false}.
     */
    public boolean isInHideoutSiege() {
        return false;
    }

    public FloodProtectors getFloodProtectors() {
        return client.getFloodProtectors();
    }

    public boolean isFlyingMounted() {
        return checkTransformed(Transform::isFlying);
    }

    public int getCharges() {
        return charges.get();
    }

    public void setCharges(int count) {
        restartChargeTask();
        charges.set(count);
    }

    public boolean decreaseCharges(int count) {
        if (charges.get() < count) {
            return false;
        }

        // Charge clear task should be reset every time a charge is decreased and stopped when charges become 0.
        if (charges.addAndGet(-count) == 0) {
            stopChargeTask();
        } else {
            restartChargeTask();
        }

        sendPacket(new EtcStatusUpdate(this));
        return true;
    }

    public void clearCharges() {
        charges.set(0);
        sendPacket(new EtcStatusUpdate(this));
    }

    private void restartChargeTask() {
        if (chargeTask != null) {
            chargeTask.cancel(false);
            chargeTask = null;
        }
        chargeTask = ThreadPool.schedule(new ResetChargesTask(this), 600000);
    }

    /**
     * Stops the Charges Clearing Task.
     */
    private void stopChargeTask() {
        if (chargeTask != null) {
            chargeTask.cancel(false);
            chargeTask = null;
        }
    }

    public void teleportBookmarkModify(int id, int icon, String tag, String name) {
        final TeleportBookmark bookmark = teleportBookmarks.get(id);
        if (bookmark != null) {
            bookmark.setIcon(icon);
            bookmark.setTag(tag);
            bookmark.setName(name);

            getDAO(PlayerDAO.class).updateTeleportBookMark(objectId, id, icon, tag, name);
        }

        sendPacket(new ExGetBookMarkInfoPacket(this));
    }

    public void teleportBookmarkDelete(int id) {
        if (teleportBookmarks.remove(id) != null) {
            getDAO(PlayerDAO.class).deleteTeleportBookMark(objectId,id);
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

        final TeleportBookmark bookmark = teleportBookmarks.get(id);
        if (bookmark != null) {
            destroyItem("Consume", inventory.getItemByItemId(13016).getObjectId(), 1, null, false);
            teleToLocation(bookmark, false);
        }
        sendPacket(new ExGetBookMarkInfoPacket(this));
    }

    private boolean teleportBookmarkCondition(int type) {
        return checkCombatCondition() && checkEnvironmentCondition(type) && checkStatusCondition();
        /*
         * TODO: Instant Zone still not implemented else if (isInsideZone(ZoneId.INSTANT)) { sendPacket(SystemMessage.getSystemMessage(2357)); return; }
         */
    }

    private boolean checkStatusCondition() {
        boolean valid = true;
        if (hasBlockActions() && hasAbnormalType(AbnormalType.PARALYZE)) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_PETRIFIED_OR_PARALYZED_STATE);
            valid =  false;
        } else if (isDead()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD);
            valid = false;
        }
        return valid;
    }

    private boolean checkEnvironmentCondition(int type) {
        boolean valid = true;
        if (isFlying()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
            valid =  false;
        } else if (isInWater()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
            valid = false;
        } else if ((type == 1) && (isInsideZone(ZoneType.SIEGE) || isInsideZone(ZoneType.CLAN_HALL) || isInsideZone(ZoneType.JAIL) || isInsideZone(ZoneType.CASTLE) || isInsideZone(ZoneType.NO_SUMMON_FRIEND) || isInsideZone(ZoneType.FORT))) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
            valid = false;
        } else if (isInsideZone(ZoneType.NO_BOOKMARK) || isInBoat()) {
            if (type == 0) {
                sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA);
            } else if (type == 1) {
                sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
            }
            valid =  false;
        }

        return valid;
    }

    private boolean checkCombatCondition() {
        boolean valid = true;
        if (isInCombat()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
            valid = false;
        } else if (isInSiege || (siegeState != 0)) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_CLAN_HALL_SIEGE);
            valid =  false;
        } else if (isInDuel || startingDuel) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
            valid =  false;
        } else if (isInOlympiadMode()) {
            sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
            valid =  false;
        }
        return valid;
    }

    public void teleportBookmarkAdd(int x, int y, int z, int icon, String tag, String name) {
        if (!teleportBookmarkCondition(1)) {
            return;
        }

        if (teleportBookmarks.size() >= bookmarkSlot) {
            sendPacket(SystemMessageId.YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION);
            return;
        }

        if (inventory.getInventoryItemCount(20033, 0) == 0) {
            sendPacket(SystemMessageId.YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG);
            return;
        }

        int id;
        for (id = 1; id <= bookmarkSlot; ++id) {
            if (!teleportBookmarks.containsKey(id)) {
                break;
            }
        }
        var bookmark = TeleportBookmark.of(objectId, id, x, y, z, icon, tag, name);
        teleportBookmarks.put(id, bookmark);

        destroyItem("Consume", inventory.getItemByItemId(20033).getObjectId(), 1, null, false);

        final SystemMessage sm = getSystemMessage(SystemMessageId.S1_DISAPPEARED);
        sm.addItemName(20033);
        sendPacket(sm);

        getDAO(PlayerDAO.class).save(bookmark);
        sendPacket(new ExGetBookMarkInfoPacket(this));
    }

    private void restoreTeleportBookmark() {
        teleportBookmarks.putAll(getDAO(PlayerDAO.class).findTeleportBookmark(objectId));
    }

    @Override
    public void sendInfo(Player player) {
        if(!isInvisible() || player.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS)) {
            player.sendPacket(new ExCharInfo(this));
        }

        if (isInBoat() && isInvisible()) {
            setXYZ(getBoat().getLocation());
            player.sendPacket(new GetOnVehicle(getObjectId(), getBoat().getObjectId(), inVehiclePosition));
        }

        updateRelation(player);
        player.updateRelation(this);

        switch (privateStoreType) {
            case BUY -> player.sendPacket(new PrivateStoreMsgBuy(this));
            case SELL -> player.sendPacket(new PrivateStoreMsgSell(this));
            case MANUFACTURE -> player.sendPacket(new RecipeShopMsg(this));
            case PACKAGE_SELL -> player.sendPacket(new ExPrivateStoreSetWholeMsg(this));
        }
    }

    public void playMovie(MovieHolder holder) {
        if (movieHolder != null) {
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
        sendPacket(new ExStopScenePlayer(movieHolder.getMovie()));
        setMovieHolder(null);
    }

    public int getBirthdays() {
        return (int) ChronoUnit.YEARS.between(data.getCreateDate(), LocalDate.now());
    }

    public IntSet getFriendList() {
        return friends;
    }

    void restoreFriendList() {
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
        return silenceMode;
    }

    /**
     * Set the silence mode.
     *
     * @param mode the value
     */
    public void setSilenceMode(boolean mode) {
        silenceMode = mode;
        if (silenceModeExcluded != null) {
            silenceModeExcluded.clear(); // Clear the excluded list on each setSilenceMode
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
        if (ChatSettings.silenceModeExclude() && silenceMode && nonNull(silenceModeExcluded)) {
            return !silenceModeExcluded.contains(playerObjId);
        }
        return silenceMode;
    }

    /**
     * Add a player to the "excluded silence mode" list.
     *
     * @param playerObjId the player's object Id
     */
    public void addSilenceModeExcluded(int playerObjId) {
        if (silenceModeExcluded == null) {
            silenceModeExcluded = new ArrayIntList(1);
        }
        silenceModeExcluded.add(playerObjId);
    }

    private void storeRecipeShopList() {
        if (hasManufactureShop()) {
            var recipeDAO = getDAO(RecipeDAO.class);
            recipeDAO.deleteRecipeShop(objectId);
            recipeDAO.save(manufactureItems.values());
        }
    }

    private void restoreRecipeShopList() {
        manufactureItems = getDAO(RecipeDAO.class).findByPlayer(objectId);
    }

    @Override
    public double getCollisionRadius() {
        if (isMounted() && (mountNpcId > 0)) {
            return NpcData.getInstance().getTemplate(getMountNpcId()).getfCollisionRadius();
        }

        final double defaultCollisionRadius = appearance.isFemale() ? getBaseTemplate().getFCollisionRadiusFemale() : getBaseTemplate().getfCollisionRadius();
        return getTransformation().map(transform -> transform.getCollisionRadius(this, defaultCollisionRadius)).orElse(defaultCollisionRadius);
    }

    @Override
    public double getCollisionHeight() {
        if (isMounted() && (mountNpcId > 0)) {
            return NpcData.getInstance().getTemplate(getMountNpcId()).getfCollisionHeight();
        }

        final double defaultCollisionHeight = appearance.isFemale() ? getBaseTemplate().getFCollisionHeightFemale() : getBaseTemplate().getfCollisionHeight();
        return getTransformation().map(transform -> transform.getCollisionHeight(this, defaultCollisionHeight)).orElse(defaultCollisionHeight);
    }

    public final int getClientZ() {
        return clientZ;
    }

    public final void setClientZ(int val) {
        clientZ = val;
    }

    /**
     * @return true if character falling now on the start of fall return false for correct coords sync!
     */
    public final boolean isFalling(int z) {
        if (isDead() || isFlying() || isFlyingMounted() || isInsideZone(ZoneType.WATER)) {
            return false;
        }

        if (fallingTimestamp > 0 && System.currentTimeMillis() < fallingTimestamp) {
            return true;
        }

        final int deltaZ = getZ() - z;
        if (deltaZ <= getBaseTemplate().getSafeFallHeight()) {
            fallingTimestamp = 0;
            return false;
        }

        // If there is no geo data loaded for the place we are client Z correction might cause falling damage.
        if (!GeoEngine.getInstance().hasGeo(getX(), getY())) {
            fallingTimestamp = 0;
            return false;
        }

        if (fallingDamage == 0) {
            fallingDamage = (int) Formulas.calcFallDam(this, deltaZ);
        }
        if (fallingDamageTask != null) {
            fallingDamageTask.cancel(true);
        }
        fallingDamageTask = ThreadPool.schedule(() ->
        {
            if ((fallingDamage > 0) && !isInvulnerable()) {
                reduceCurrentHp(min(fallingDamage, getCurrentHp() - 1), this, null, false, true, false, false, DamageType.FALL);
                sendPacket(getSystemMessage(SystemMessageId.YOU_RECEIVED_S1_FALLING_DAMAGE).addInt(fallingDamage));
            }
            fallingDamage = 0;
            fallingDamageTask = null;
        }, 1500);

        // Prevent falling under ground.
        sendPacket(new ValidateLocation(this));

        setFalling();

        return false;
    }

    private void setFalling() {
        fallingTimestamp = System.currentTimeMillis() + FALLING_VALIDATION_DELAY;
    }

    /**
     * @return the _movie
     */
    public MovieHolder getMovieHolder() {
        return movieHolder;
    }

    private void setMovieHolder(MovieHolder movie) {
        movieHolder = movie;
    }

    /**
     * Update last item auction request timestamp to current
     */
    public void updateLastItemAuctionRequest() {
        lastItemAuctionInfoRequest = System.currentTimeMillis();
    }

    /**
     * @return true if receiving item auction requests<br>
     * (last request was in 2 seconds before)
     */
    public boolean isItemAuctionPolling() {
        return (System.currentTimeMillis() - lastItemAuctionInfoRequest) < 2000;
    }

    @Override
    public boolean isMovementDisabled() {
        return super.isMovementDisabled() || (movieHolder != null) || fishing.isFishing();
    }

    public boolean setLang(String lang) {
        boolean result = false;
        if (Config.MULTILANG_ENABLE) {
            if (Config.MULTILANG_ALLOWED.contains(lang)) {
                this.lang = lang;
                result = true;
            } else {
                this.lang = Config.MULTILANG_DEFAULT;
            }

        } else {
            this.lang = null;
        }

        return result;
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
        for (var e : getSkills().entrySet()) {
            learn = SkillTreesData.getInstance().getClassSkill(e.getKey(), e.getValue().getLevel() % 100, getClassId());
            if (learn != null) {
                final int lvlDiff = e.getKey() == CommonSkill.EXPERTISE.getId() ? 0 : 9;
                if (getLevel() < (learn.getGetLevel() - lvlDiff)) {
                    decreaseSkillLevel(e.getValue(), lvlDiff);
                }
            }
        }
    }

    private void decreaseSkillLevel(Skill skill, int lvlDiff) {
        int nextLevel = -1;
        final var skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(getClassId());
        for (SkillLearn sl : skillTree.values()) {
            if ((sl.getSkillId() == skill.getId()) && (nextLevel < sl.getSkillLevel()) && (getLevel() >= (sl.getGetLevel() - lvlDiff))) {
                nextLevel = sl.getSkillLevel(); // next possible skill level
            }
        }

        if (nextLevel == -1) {
            LOGGER.info("Removing skill {}  from {}", skill, this);
            removeSkill(skill, true); // there is no lower skill
        } else {
            LOGGER.info("Decreasing skill {} to {} for {}",  skill, nextLevel, this);
            addSkill(SkillEngine.getInstance().getSkill(skill.getId(), nextLevel), true); // replace with lower one
        }
    }

    public boolean canMakeSocialAction() {
        return ((privateStoreType == PrivateStoreType.NONE) && (getActiveRequester() == null) && !isAlikeDead() && !isAllSkillsDisabled() && !isCastingNow() && (getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE));
    }

    public void setMultiSocialAction(int id, int targetId) {
        multiSocialAction = id;
        multiSocialTarget = targetId;
    }

    public int getMultiSocialAction() {
        return multiSocialAction;
    }

    public int getMultiSocialTarget() {
        return multiSocialTarget;
    }

    public Collection<TeleportBookmark> getTeleportBookmarks() {
        return teleportBookmarks.values();
    }

    public boolean isInventoryUnder90() {
        return inventory.getSize() < getInventoryLimit() * 0.9;
    }

    public boolean isInventoryUnder80() {
        return inventory.getSize() < getInventoryLimit() * 0.8;
    }

    public boolean havePetInvItems() {
        return petItems;
    }

    public void setPetInvItems(boolean haveIt) {
        petItems = haveIt;
    }

    /**
     * Restore Pet's inventory items from database.
     */
    private void restorePetInventoryItems() {
        setPetInvItems(getDAO(ItemDAO.class).hasPetItems(objectId));
    }

    public String getAdminConfirmCmd() {
        return adminConfirmCmd;
    }

    public void setAdminConfirmCmd(String adminConfirmCmd) {
        this.adminConfirmCmd = adminConfirmCmd;
    }

    public int getBlockCheckerArena() {
        return handysBlockCheckerEventArena;
    }

    public void setBlockCheckerArena(byte arena) {
        handysBlockCheckerEventArena = arena;
    }

    void loadRecommendations() {
        IntKeyIntValue recommends = getDAO(PlayerDAO.class).findRecommends(objectId);
        if(nonNull(recommends)) {
            setRecommend(recommends.getKey());
            setRecommendLeft(recommends.getValue());
        }
    }

    private void storeRecommendations() {
        getDAO(PlayerDAO.class).saveRecommends(objectId, recommend, recommendLeft, 0);
    }

    public void startRecoGiveTask() {
        // Create task to give new recommendations
        recommendGiveTask = ThreadPool.scheduleAtFixedRate(new RecoGiveTask(this), 7200000, 3600000);

        // Store new data
        storeRecommendations();
    }

    public void stopRecoGiveTask() {
        if (recommendGiveTask != null) {
            recommendGiveTask.cancel(false);
            recommendGiveTask = null;
        }
    }

    public boolean isRecommendTwoHoursGiven() {
        return recommendTwoHoursGiven;
    }

    public void setRecommendTwoHoursGiven(boolean val) {
        recommendTwoHoursGiven = val;
    }

    public String getLastPetitionGmName() {
        return lastPetitionGmName;
    }

    public void setLastPetitionGmName(String gmName) {
        lastPetitionGmName = gmName;
    }

    public ContactList getContacts() {
        return contacts;
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
        return notMoveUntil;
    }

    public void updateNotMoveUntil() {
        notMoveUntil = System.currentTimeMillis() + CharacterSettings.npcTalkBlockingTime();
    }

    /**
     * Add a skill level to the custom skills map.
     *
     * @param skill the skill to add
     */
    private void addCustomSkill(Skill skill) {
        if ((skill != null) && (skill.getDisplayId() != skill.getId())) {
            if (customSkills == null) {
                customSkills = new CHashIntMap<>();
            }
            customSkills.put(skill.getDisplayId(), skill);
        }
    }

    /**
     * Remove a skill level from the custom skill map.
     *
     * @param skill the skill to remove
     */
    private void removeCustomSkill(Skill skill) {
        if ((skill != null) && (customSkills != null) && (skill.getDisplayId() != skill.getId())) {
            customSkills.remove(skill.getDisplayId());
        }
    }

    public boolean isOnCustomEvent() {
        return isOnCustomEvent;
    }

    public void setOnCustomEvent(boolean value) {
        isOnCustomEvent = value;
    }

    /**
     * @return {@code true} if player is on event, {@code false} otherwise.
     */
    @Override
    public boolean isOnEvent() {
        return isOnCustomEvent || super.isOnEvent();
    }

    void setOriginalCpHpMp(double cp, double hp, double mp) {
        originalCp = cp;
        originalHp = hp;
        originalMp = mp;
    }

    @Override
    public void addOverrideCond(PcCondOverride... overrides) {
        super.addOverrideCond(overrides);
        setCondOverrideKey(Long.toString(exceptions));
    }

    @Override
    public void removeOverriddenCond(PcCondOverride... overrides) {
        super.removeOverriddenCond(overrides);
        setCondOverrideKey(Long.toString(exceptions));
    }

    public void storeVariables () {
        getDAO(PlayerVariablesDAO.class).save(variables);
    }


    @Override
    public int getId() {
        return objectId;
    }

    public boolean isPartyBanned() {
        return PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN);
    }

    /**
     * @return {@code true} if action was added successfully, {@code false} otherwise.
     */
    public boolean addAction(PlayerAction act) {
        if (!hasAction(act)) {
            actionMask |= act.getMask();
            return true;
        }
        return false;
    }

    /**
     * @return {@code true} if action was removed successfully, {@code false} otherwise.
     */
    public boolean removeAction(PlayerAction act) {
        if (hasAction(act)) {
            actionMask &= ~act.getMask();
            return true;
        }
        return false;
    }

    /**
     * @return {@code true} if action is present, {@code false} otherwise.
     */
    public boolean hasAction(PlayerAction act) {
        return (actionMask & act.getMask()) == act.getMask();
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
    private boolean atWarWith(Playable target) {
        if (target == null) {
            return false;
        }
        return nonNull(clan) && nonNull(target.getClan()) && clan.isAtWarWith(target.getClan());
    }

    public int getAbilityPointsUsed() {
        return getAbilityPointsMainClassUsed();
    }

    /**
     * @return The amount of times player can use world chat
     */
    public int getWorldChatPoints() {
        return (int) getStats().getValue(Stat.WORLD_CHAT_POINTS, Config.WORLD_CHAT_POINTS_PER_DAY);
    }

    /**
     * @return Side of the player.
     */
    public CastleSide getPlayerSide() {
        if ((clan == null) || (clan.getCastleId() == 0)) {
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
     * @return the maximum amount of points that player can use
     */
    public int getMaxSummonPoints() {
        return (int) getStats().getValue(Stat.MAX_SUMMON_POINTS, 0);
    }

    /**
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
     * @return {@code true} if request was successfully removed, {@code false} in case processing set is not created or not containing the request.
     */
    public boolean removeRequest(Class<? extends AbstractRequest> clazz) {
        return (requests != null) && (requests.remove(clazz) != null);
    }

    /**
     * @return object that is instance of {@code requestClass} param, {@code null} if not instance or not set.
     */
    public <T extends AbstractRequest> T getRequest(Class<T> requestClass) {
        return requests != null ? requestClass.cast(requests.get(requestClass)) : null;
    }

    public boolean hasItemRequest() {
        if(nonNull(requests)) {
            for (var req : requests.values()) {
                if(req.isItemRequest()) {
                    return true;
                }
            }
        }
        return false;
    }

    @SafeVarargs
    public final boolean hasRequest(Class<? extends AbstractRequest> requestClass, Class<? extends AbstractRequest>... classes) {
        if (nonNull(requests)) {
            for (var clazz : classes) {
                if (requests.containsKey(clazz)) {
                    return true;
                }
            }
            return requests.containsKey(requestClass);
        }
        return false;
    }

    /**
     * @return {@code true} if item object id is currently in use by some request, {@code false} otherwise.
     */
    public boolean isProcessingItem(int objectId) {
        return nonNull(requests) && requests.values().stream().anyMatch(req -> req.isUsingItem(objectId));
    }

    /**
     * Removing all requests associated with the item object id provided.
     */
    public void removeRequestsThatProcessesItem(int objectId) {
        if (requests != null) {
            requests.values().removeIf(req -> req.isUsingItem(objectId));
        }
    }

    /**
     * Gets the last commission infos.
     *
     * @return the last commission infos
     */
    public IntMap<ExResponseCommissionInfo> getLastCommissionInfos() {
        if (lastCommissionInfos == null) {
            initLastCommissionInfos();
        }
        return lastCommissionInfos;
    }

    private synchronized void initLastCommissionInfos() {
        if (lastCommissionInfos == null) {
            lastCommissionInfos = new CHashIntMap<>();
        }
    }

    /**
     * Gets the whisperers.
     *
     * @return the whisperers
     */
    public IntSet getWhisperers() {
        return whispers;
    }

    public MatchingRoom getMatchingRoom() {
        return matchingRoom;
    }

    public void setMatchingRoom(MatchingRoom matchingRoom) {
        this.matchingRoom = matchingRoom;
    }

    public boolean isInMatchingRoom() {
        return matchingRoom != null;
    }

    @Override
    public boolean isVisibleFor(Player player) {
        return super.isVisibleFor(player) || (isInParty() && (player.getParty() == getParty()));
    }

    /**
     * Gets the Quest zone ID.
     *
     * @return int the quest zone ID
     */
    public int getQuestZoneId() {
        return questZoneId;
    }

    /**
     * Set the Quest zone ID.
     *
     * @param id the quest zone ID
     */
    public void setQuestZoneId(int id) {
        questZoneId = id;
    }

    public void sendInventoryUpdate(InventoryUpdate iu) {
        sendPackets(iu, new ExAdenaInvenCount(), new ExBloodyCoinCount(getLCoins()), new ExUserInfoInvenWeight());
    }

    public void sendItemList() {
        ItemList.sendList(this);
        sendPackets(new ExQuestItemList(1, this),
                new ExQuestItemList(2, this),
                new ExAdenaInvenCount(),
                new ExUserInfoInvenWeight(),
                new ExBloodyCoinCount(getLCoins()));
    }

    public Fishing getFishing() {
        return fishing;
    }

    public boolean isFishing() {
        return fishing.isFishing();
    }

    @Override
    public MoveType getMoveType() {
        if (sitting) {
            return MoveType.SITTING;
        }
        return super.getMoveType();
    }

    void startOnlineTimeUpdateTask() {
        if (onlineTimeUpdateTask != null) {
            stopOnlineTimeUpdateTask();
        }

        onlineTimeUpdateTask = ThreadPool.scheduleAtFixedRate(this::updateOnlineTime, 60 * 1000L, 60 * 1000L);
    }

    private void updateOnlineTime() {
        if (clan != null) {
            clan.addMemberOnlineTime(this);
        }
    }

    private void stopOnlineTimeUpdateTask() {
        if (onlineTimeUpdateTask != null) {
            onlineTimeUpdateTask.cancel(true);
            onlineTimeUpdateTask = null;
        }
    }

    public GroupType getGroupType() {
        if(!isInParty()) {
            return  GroupType.NONE;
        }
        return party.isInCommandChannel() ? GroupType.COMMAND_CHANNEL : GroupType.PARTY;
    }

    @Override
    protected void initStatusUpdateCache() {
        super.initStatusUpdateCache();
        addStatusUpdateValue(StatusUpdateType.LEVEL);
        addStatusUpdateValue(StatusUpdateType.MAX_CP);
        addStatusUpdateValue(StatusUpdateType.CUR_CP);
    }

    public boolean canReceiveAttendance() {
        return isNull(account.nextAttendance()) || LocalDateTime.now().isAfter(account.nextAttendance());
    }

    public byte lastAttendanceReward() {
        return account.lastAttendanceReward();
    }

    public int getVipAttendanceReward() {
        return account.vipAttendanceReward();
    }

    public void updateAttendance(byte rewardIndex, boolean updateVip) {
        var now = LocalDateTime.now();
        if(now.getHour() > 6 || (now.getHour() == 6 && now.getMinute() > 30) ) {
            now = now.plusDays(1);
        }

        account.setLastAttendanceReward(rewardIndex);
        account.setNextAttendance(now.withHour(6).withMinute(30).withSecond(0));
        if(rewardIndex == 1) {
            account.setVipAttendanceReward(0);
        }
        if(updateVip) {
            account.updateAttendanceVipReward((rewardIndex - 1) / 7);
        }
    }

    public boolean isFriend(Player player) {
        return friends.contains(player.getObjectId());
    }

    public boolean isInSameClan(Player player) {
        return data.getClanId() > 0 && data.getClanId() == player.getClanId();
    }

    public boolean isInSameAlly(Player player) {
        var ally = getAllyId();
        return ally > 0 && player.getAllyId() == ally;
    }

    public boolean isSiegeFriend(WorldObject target)
    {
        // If i'm natural or not in siege zone, not friends.
        if ((siegeState == 0) || !isInsideZone(ZoneType.SIEGE))
        {
            return false;
        }

        // If target isn't a player, is self, isn't on same siege or not on same state, not friends.
        var targetPlayer = target.getActingPlayer();
        if ((targetPlayer == null) || (targetPlayer == this) || (targetPlayer.getSiegeSide() != siegeSide) || (siegeState != targetPlayer.getSiegeState()))
        {
            return false;
        }

        // Attackers are considered friends only if castle has no owner.
        if (siegeState == 1)
        {
            final Castle castle = CastleManager.getInstance().getCastleById(siegeSide);
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

        if (zoneId == 2) { // Ancient Pirates' Tomb.
            return (x == 20) && (y == 15);
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
            if (isOnline() && isInTimedHuntingZone(zoneId))
            {
                sendPacket(TimedHuntingZoneExit.STATIC_PACKET);
                abortCast();
                stopMove(null);
                teleToLocation(MapRegionManager.getInstance().getTeleToLocation(this, TeleportWhereType.TOWN));
            }
        }, delay);
    }

    private void stopTimedHuntingZoneTask()
    {
        if ((_timedHuntingZoneFinishTask != null) && !_timedHuntingZoneFinishTask.isCancelled() && !_timedHuntingZoneFinishTask.isDone())
        {
            _timedHuntingZoneFinishTask.cancel(true);
            _timedHuntingZoneFinishTask = null;
        }
        sendPacket(TimedHuntingZoneExit.STATIC_PACKET);
    }

    public long getTimedHuntingZoneRemainingTime() {
        if ((_timedHuntingZoneFinishTask != null) && !_timedHuntingZoneFinishTask.isCancelled() && !_timedHuntingZoneFinishTask.isDone()) {
            return _timedHuntingZoneFinishTask.getDelay(TimeUnit.MILLISECONDS);
        }
        return 0;
    }

    @Override
    protected Queue<AbstractEventListener> globalListenerByType(EventType type) {
        return Listeners.players().getListeners(type);
    }
}
