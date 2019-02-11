package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;
import org.l2j.gameserver.ServerType;

import static org.l2j.commons.util.Util.isNullOrEmpty;

public class ServerSettings implements Settings {

    private byte ageLimit;
    private boolean gmOnly;
    private boolean showBrackets;
    private boolean isPvP;
    private int type;
    private boolean everyBodyIsAdmin;
    private boolean hideGMStatus;
    private boolean showGMLogin;
    private boolean saveGMEffects;
    private String charNameTemplate;
    private String clanNameTemplate;
    private String clanTitleTemplate;
    private String allyNameTemplate;
    private boolean shoutGlobal;
    private boolean tradeChatGlobal;
    private int chatRange;
    private int shoutSquareOffset;
    private boolean worldChatAllowed;
    private int worldChatPointsPerDay;
    private int premiumWorldChatPointsPerDay;
    private int useWorldChatMinLevel;
    private int premiumUseWorldChatMinLevel;
    private float rateXP;
    private float rateSP;
    private int maxDropItemsFromGroup;
    private float rateAdena;
    private float rateItems;
    private float dropChanceModifier;
    private float rateSpoil;
    private float spoilChanceModifier;
    private float rateQuestReward;
    private boolean rateQuestAffectsXpSpAdenaOnly;
    private float questRewardLimitModifier;
    private float rateQuestDrop;
    private float rateClanReputationScore;
    private int rateClanReputationScoreMaxAffected;
    private float rateXpRaidbossModifier;
    private float rateDropItemsRaidboss;
    private float dropChanceRaidbossModifier;
    private float rateDropItemsBoss;
    private float dropChanceBossModifier;
    private int[] noRateItems;
    private boolean noRateEquipment;
    private boolean noRateKeyMaterial;
    private boolean noRateRecipe;
    private float rateSiegeGuard;
    private boolean ratePartyBasedOnMinLevel;
    private float rateMobSpawn;
    private int rateMobSpawnMinLevel;
    private int rateMobSpawnMaxLevel;
    private float rateRaidRegen;
    private float rateRaidDefense;
    private float rateRaidAttack;
    private float rateEpicDefense;
    private float rateEpicAttack;
    private int serverId;
    private String internalAddress;
    private String externalAddress;
    private short port;
    private String authServerAddress;
    private int authServerPort;
    private int maximumOnlineUsers;

    @Override
    public void load(SettingsFile settingsFile) {
        serverId = settingsFile.getInteger("ServerId", 1);
        internalAddress = settingsFile.getString("InternalAddress", "127.0.0.1");
        externalAddress = settingsFile.getString("ExternalAddress", "127.0.0.1");
        port = settingsFile.getShort("Port", (short) 7777);
        authServerAddress = settingsFile.getString("AuthServerAddress", "127.0.0.1");
        authServerPort = settingsFile.getInteger("AuthServerPort", 9014);

        ageLimit = settingsFile.getByte("AgeLimit", (byte) 0);
        gmOnly = settingsFile.getBoolean("GMOnly", false);
        showBrackets = settingsFile.getBoolean("ShowBrackets", false);
        isPvP = settingsFile.getBoolean("PvPServer", false);
        parseServerType(settingsFile);

        maximumOnlineUsers = Math.max(1, settingsFile.getInteger("MaximumOnlineUsers", 10));

        everyBodyIsAdmin = settingsFile.getBoolean("EverybodyHasAdminRights", false);
        hideGMStatus = settingsFile.getBoolean("HideGMStatus", true);
        showGMLogin = settingsFile.getBoolean("ShowGMLogin", false);
        saveGMEffects = settingsFile.getBoolean("SaveGMEffects", false);

        charNameTemplate = settingsFile.getString("CnameTemplate", "[A-Za-z0-9]{3,16}");
        clanNameTemplate = settingsFile.getString("ClanNameTemplate", "[A-Za-z0-9]{3,16}");
        clanTitleTemplate = settingsFile.getString("ClanTitleTemplate", "[A-Za-z0-9]{3,16}");
        allyNameTemplate = settingsFile.getString("AllyNameTemplate", "[A-Za-z0-9]{3,16}");

        shoutGlobal = settingsFile.getBoolean("GlobalShout", false);
        tradeChatGlobal = settingsFile.getBoolean("GlobalTradeChat", false);
        chatRange = settingsFile.getInteger("ChatRange",1250);
        shoutSquareOffset = (int) Math.pow(settingsFile.getInteger("ShoutOffset", 0), 2);
        worldChatAllowed =  settingsFile.getBoolean("AllowWorldChat", false);
        if(worldChatAllowed) {
            worldChatPointsPerDay = settingsFile.getInteger("WorldChatPointsPerDay", 10);
            premiumWorldChatPointsPerDay = settingsFile.getInteger("PremiumWorldChatPointsPerDay", 20);
            useWorldChatMinLevel = settingsFile.getInteger("UseWorldChatMinLevel", 40);
            premiumUseWorldChatMinLevel = settingsFile.getInteger("PremiumUseWorldChatMinLevel", 10);
        }

        rateXP = settingsFile.getFloat("RateXp", 1.f);
        rateSP = settingsFile.getFloat("RateSp", 1.f);
        rateAdena = settingsFile.getFloat("RateAdena", 1.f);
        rateItems = settingsFile.getFloat("RateItems", 1.f);
        rateSpoil = settingsFile.getFloat("RateSpoil", 1.f);
        rateQuestReward = settingsFile.getFloat("RateQuestReward", 1.f);
        rateQuestDrop = settingsFile.getFloat("RateQuestDrop", 1.f);
        rateClanReputationScore = settingsFile.getFloat("RateClanRepScore", 1.f);
        rateClanReputationScoreMaxAffected = settingsFile.getInteger("RateClanRepScoreMaxAffected", 2);
        rateXpRaidbossModifier = settingsFile.getFloat("RateXpRaidbossModifier", 1.f);
        rateDropItemsRaidboss = settingsFile.getFloat("RateDropItemsRaidboss", 1.f);
        rateDropItemsBoss = settingsFile.getFloat("RateDropItemsBoss", 1.f);

        dropChanceModifier = settingsFile.getFloat("DropChanceModifier", 1.f);
        maxDropItemsFromGroup = settingsFile.getInteger("MaxDropItemsFromGroup", 1);
        spoilChanceModifier = settingsFile.getFloat("SpoilChanceModifier", 1.f);
        rateQuestAffectsXpSpAdenaOnly = settingsFile.getBoolean("RateQuestAffectsXpSpAdenaOnly", true);
        questRewardLimitModifier = settingsFile.getFloat("QuestRewardLimitModifier", rateQuestReward);
        dropChanceRaidbossModifier = settingsFile.getFloat("DropChanceRaidbossModifier", 1.f);
        dropChanceBossModifier = settingsFile.getFloat("DropChanceBossModifier", 1.f);
        noRateItems = settingsFile.getIntegerArray("NoRateItemsId", ",");
        noRateEquipment = settingsFile.getBoolean("NoRateEquipment", true);
        noRateKeyMaterial = settingsFile.getBoolean("NoRateKeyMaterial", true);
        noRateRecipe = settingsFile.getBoolean("NoRateRecipe", true);
        rateSiegeGuard = settingsFile.getFloat("RateSiegeGuard", 1.f);
        ratePartyBasedOnMinLevel = settingsFile.getBoolean("RatePartyBasedOnMinLevel", false);
        rateMobSpawn = settingsFile.getFloat("RateMobSpawn", 1.f);
        rateMobSpawnMinLevel = settingsFile.getInteger("RateMobSpawnMinLevel", 1);
        rateMobSpawnMaxLevel = settingsFile.getInteger("RateMobSpawnMaxLevel", 100);
        rateRaidRegen = settingsFile.getFloat("RateRaidRegen", 1.f);
        rateRaidDefense = settingsFile.getFloat("RateRaidDefense", 1.f);
        rateRaidAttack = settingsFile.getFloat("RateRaidAttack", 1.f);
        rateEpicDefense = settingsFile.getFloat("RateEpicDefense", 1.f);
        rateEpicAttack = settingsFile.getFloat("RateEpicAttack", 1.f);
    }

    private void parseServerType(SettingsFile settingsFile) {
        type = 0;
        var types = settingsFile.getStringArray("ServerType");

        for (String t : types) {
            if(isNullOrEmpty(t)){
               continue;
            }
            try {
                type |= ServerType.valueOf(t.toUpperCase()).getMask();
            } catch (Exception e) {
                // do nothing
            }

        }
    }

    public int serverId() {
        return serverId;
    }

    public String internalAddress() {
        return internalAddress;
    }

    public String externalAddress() {
        return externalAddress;
    }

    public short port() {
        return port;
    }

    public String authServerAddress() {
        return authServerAddress;
    }

    public int authServerPort() {
        return authServerPort;
    }

    public byte ageLimit() {
        return ageLimit;
    }

    public boolean isGMOnly() {
        return gmOnly;
    }

    public boolean isShowingBrackets() {
        return showBrackets;
    }

    public boolean isPvP() {
        return isPvP;
    }

    public int type() {
        return type;
    }

    public int maximumOnlineUsers() {
        return maximumOnlineUsers;
    }

    public boolean isEveryBodyIsAdmin() {
        return everyBodyIsAdmin;
    }

    public boolean isHideGMStatus() {
        return hideGMStatus;
    }

    public boolean showGMLogin() {
        return showGMLogin;
    }

    public boolean saveGMEffects() {
        return saveGMEffects;
    }

    public String charNameTemaplate() {
        return charNameTemplate;
    }

    public String clanNameTemplate() {
        return clanNameTemplate;
    }

    public String clanTitleTemplate() {
        return clanTitleTemplate;
    }

    public String allyNameTemplate() {
        return allyNameTemplate;
    }

    public boolean isShoutGlobal() {
        return shoutGlobal;
    }

    public boolean isTradeChatGlobal() {
        return tradeChatGlobal;
    }

    public int chatRange() {
        return chatRange;
    }

    public int shoutSquareOffset() {
        return shoutSquareOffset;
    }

    public boolean isWorldChatAllowed() {
        return worldChatAllowed;
    }

    public int worldChatPointsPerDay() {
        return worldChatPointsPerDay;
    }

    public int premiumWorldChatPointsPerDay() {
        return premiumWorldChatPointsPerDay;
    }

    public int useWorldChatMinLevel() {
        return useWorldChatMinLevel;
    }

    public int premiumUseWorldChatMinLevel() {
        return premiumUseWorldChatMinLevel;
    }

    public float rateXP() {
        return rateXP;
    }

    public float rateSP() {
        return rateSP;
    }

    public float rateAdena() {
        return rateAdena;
    }

    public float rateItems() {
        return rateItems;
    }

    public float rateSpoil() {
        return rateSpoil;
    }

    public float rateQuestReward() {
        return rateQuestReward;
    }

    public float rateQuestDrop() {
        return rateQuestDrop;
    }

    public int maxDropItemsFromGroup() {
        return maxDropItemsFromGroup;
    }

    public float dropChanceModifier() {
        return dropChanceModifier;
    }

    public float spoilChanceModifier() {
        return spoilChanceModifier;
    }

    public boolean isRateQuestAffectsXpSpAdenaOnly() {
        return rateQuestAffectsXpSpAdenaOnly;
    }

    public float questRewardLimitModifier() {
        return questRewardLimitModifier;
    }

    public float rateClanReputationScore() {
        return rateClanReputationScore;
    }

    public int rateClanReputationScoreMaxAffected() {
        return rateClanReputationScoreMaxAffected;
    }

    public float rateXpRaidbossModifier() {
        return rateXpRaidbossModifier;
    }

    public float rateDropItemsRaidboss() {
        return rateDropItemsRaidboss;
    }

    public float dropChanceRaidbossModifier() {
        return dropChanceRaidbossModifier;
    }

    public float rateDropItemsBoss() {
        return rateDropItemsBoss;
    }

    public float dropChanceBossModifier() {
        return dropChanceBossModifier;
    }

    public int[] noRateItems() {
        return noRateItems;
    }

    public boolean isNoRateEquipment() {
        return noRateEquipment;
    }

    public boolean isNoRateKeyMaterial() {
        return noRateKeyMaterial;
    }

    public boolean isNoRateRecipe() {
        return noRateRecipe;
    }

    public float rateSiegeGuard() {
        return rateSiegeGuard;
    }

    public boolean isRatePartyBasedOnMinLevel() {
        return ratePartyBasedOnMinLevel;
    }

    public float rateMobSpawn() {
        return rateMobSpawn;
    }

    public int rateMobSpawnMinLevel() {
        return rateMobSpawnMinLevel;
    }

    public int rateMobSpawnMaxLevel() {
        return rateMobSpawnMaxLevel;
    }

    public float rateRaidRegen() {
        return rateRaidRegen;
    }

    public float rateRaidDefense() {
        return rateRaidDefense;
    }

    public float rateRaidAttack() {
        return rateRaidAttack;
    }

    public float rateEpicDefense() {
        return rateEpicDefense;
    }

    public float rateEpicAttack() {
        return rateEpicAttack;
    }
}
