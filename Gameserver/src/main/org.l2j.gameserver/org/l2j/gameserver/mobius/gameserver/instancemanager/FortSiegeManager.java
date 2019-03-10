package org.l2j.gameserver.mobius.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.CombatFlag;
import org.l2j.gameserver.mobius.gameserver.model.FortSiegeSpawn;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.entity.Fort;
import org.l2j.gameserver.mobius.gameserver.model.entity.FortSiege;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FortSiegeManager {
    private static final Logger LOGGER = Logger.getLogger(FortSiegeManager.class.getName());

    private int _attackerMaxClans = 500; // Max number of clans

    // Fort Siege settings
    private Map<Integer, List<FortSiegeSpawn>> _commanderSpawnList;
    private Map<Integer, List<CombatFlag>> _flagList;
    private boolean _justToTerritory = true; // Changeable in fortsiege.properties
    private int _flagMaxCount = 1; // Changeable in fortsiege.properties
    private int _siegeClanMinLevel = 4; // Changeable in fortsiege.properties
    private int _siegeLength = 60; // Time in minute. Changeable in fortsiege.properties
    private int _countDownLength = 10; // Time in minute. Changeable in fortsiege.properties
    private int _suspiciousMerchantRespawnDelay = 180; // Time in minute. Changeable in fortsiege.properties
    private List<FortSiege> _sieges;

    protected FortSiegeManager() {
        load();
    }

    public static FortSiegeManager getInstance() {
        return SingletonHolder._instance;
    }

    public final void addSiegeSkills(L2PcInstance character) {
        character.addSkill(CommonSkill.SEAL_OF_RULER.getSkill(), false);
        character.addSkill(CommonSkill.BUILD_HEADQUARTERS.getSkill(), false);
    }

    /**
     * @param clan   The L2Clan of the player
     * @param fortid
     * @return true if the clan is registered or owner of a fort
     */
    public final boolean checkIsRegistered(L2Clan clan, int fortid) {
        if (clan == null) {
            return false;
        }

        boolean register = false;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT clan_id FROM fortsiege_clans where clan_id=? and fort_id=?")) {
            ps.setInt(1, clan.getId());
            ps.setInt(2, fortid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    register = true;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception: checkIsRegistered(): " + e.getMessage(), e);
        }
        return register;
    }

    public final void removeSiegeSkills(L2PcInstance character) {
        character.removeSkill(CommonSkill.SEAL_OF_RULER.getSkill());
        character.removeSkill(CommonSkill.BUILD_HEADQUARTERS.getSkill());
    }

    private void load() {
        final Properties siegeSettings = new Properties();
        final File file = new File(Config.FORTSIEGE_CONFIG_FILE);
        try (InputStream is = new FileInputStream(file)) {
            siegeSettings.load(is);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error while loading Fort Siege Manager settings!", e);
        }

        // Siege setting
        _justToTerritory = Boolean.parseBoolean(siegeSettings.getProperty("JustToTerritory", "true"));
        _attackerMaxClans = Integer.decode(siegeSettings.getProperty("AttackerMaxClans", "500"));
        _flagMaxCount = Integer.decode(siegeSettings.getProperty("MaxFlags", "1"));
        _siegeClanMinLevel = Integer.decode(siegeSettings.getProperty("SiegeClanMinLevel", "4"));
        _siegeLength = Integer.decode(siegeSettings.getProperty("SiegeLength", "60"));
        _countDownLength = Integer.decode(siegeSettings.getProperty("CountDownLength", "10"));
        _suspiciousMerchantRespawnDelay = Integer.decode(siegeSettings.getProperty("SuspiciousMerchantRespawnDelay", "180"));

        // Siege spawns settings
        _commanderSpawnList = new ConcurrentHashMap<>();
        _flagList = new ConcurrentHashMap<>();

        for (Fort fort : FortManager.getInstance().getForts()) {
            final List<FortSiegeSpawn> _commanderSpawns = new CopyOnWriteArrayList<>();
            final List<CombatFlag> _flagSpawns = new CopyOnWriteArrayList<>();
            for (int i = 1; i < 5; i++) {
                final String _spawnParams = siegeSettings.getProperty(fort.getName().replace(" ", "") + "Commander" + i, "");
                if (_spawnParams.isEmpty()) {
                    break;
                }
                final StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

                try {
                    final int x = Integer.parseInt(st.nextToken());
                    final int y = Integer.parseInt(st.nextToken());
                    final int z = Integer.parseInt(st.nextToken());
                    final int heading = Integer.parseInt(st.nextToken());
                    final int npc_id = Integer.parseInt(st.nextToken());

                    _commanderSpawns.add(new FortSiegeSpawn(fort.getResidenceId(), x, y, z, heading, npc_id, i));
                } catch (Exception e) {
                    LOGGER.warning("Error while loading commander(s) for " + fort.getName() + " fort.");
                }
            }

            _commanderSpawnList.put(fort.getResidenceId(), _commanderSpawns);

            for (int i = 1; i < 4; i++) {
                final String _spawnParams = siegeSettings.getProperty(fort.getName().replace(" ", "") + "Flag" + i, "");
                if (_spawnParams.isEmpty()) {
                    break;
                }
                final StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

                try {
                    final int x = Integer.parseInt(st.nextToken());
                    final int y = Integer.parseInt(st.nextToken());
                    final int z = Integer.parseInt(st.nextToken());
                    final int flag_id = Integer.parseInt(st.nextToken());

                    _flagSpawns.add(new CombatFlag(fort.getResidenceId(), x, y, z, 0, flag_id));
                } catch (Exception e) {
                    LOGGER.warning("Error while loading flag(s) for " + fort.getName() + " fort.");
                }
            }
            _flagList.put(fort.getResidenceId(), _flagSpawns);
        }
    }

    public final List<FortSiegeSpawn> getCommanderSpawnList(int _fortId) {
        return _commanderSpawnList.get(_fortId);
    }

    public final List<CombatFlag> getFlagList(int _fortId) {
        return _flagList.get(_fortId);
    }

    public final int getAttackerMaxClans() {
        return _attackerMaxClans;
    }

    public final int getFlagMaxCount() {
        return _flagMaxCount;
    }

    public final boolean canRegisterJustTerritory() {
        return _justToTerritory;
    }

    public final int getSuspiciousMerchantRespawnDelay() {
        return _suspiciousMerchantRespawnDelay;
    }

    public final FortSiege getSiege(L2Object activeObject) {
        return getSiege(activeObject.getX(), activeObject.getY(), activeObject.getZ());
    }

    public final FortSiege getSiege(int x, int y, int z) {
        for (Fort fort : FortManager.getInstance().getForts()) {
            if (fort.getSiege().checkIfInZone(x, y, z)) {
                return fort.getSiege();
            }
        }
        return null;
    }

    public final int getSiegeClanMinLevel() {
        return _siegeClanMinLevel;
    }

    public final int getSiegeLength() {
        return _siegeLength;
    }

    public final int getCountDownLength() {
        return _countDownLength;
    }

    public final List<FortSiege> getSieges() {
        if (_sieges == null) {
            _sieges = new CopyOnWriteArrayList<>();
        }
        return _sieges;
    }

    public final void addSiege(FortSiege fortSiege) {
        getSieges().add(fortSiege);
    }

    public boolean isCombat(int itemId) {
        return (itemId == 9819);
    }

    public boolean activateCombatFlag(L2PcInstance player, L2ItemInstance item) {
        if (!checkIfCanPickup(player)) {
            return false;
        }

        final Fort fort = FortManager.getInstance().getFort(player);

        final List<CombatFlag> fcf = _flagList.get(fort.getResidenceId());
        for (CombatFlag cf : fcf) {
            if (cf.getCombatFlagInstance() == item) {
                cf.activate(player, item);
            }
        }
        return true;
    }

    public boolean checkIfCanPickup(L2PcInstance player) {
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED);
        sm.addItemName(9819);
        // Cannot own 2 combat flag
        if (player.isCombatFlagEquipped()) {
            player.sendPacket(sm);
            return false;
        }

        // here check if is siege is in progress
        // here check if is siege is attacker
        final Fort fort = FortManager.getInstance().getFort(player);

        if ((fort == null) || (fort.getResidenceId() <= 0)) {
            player.sendPacket(sm);
            return false;
        } else if (!fort.getSiege().isInProgress()) {
            player.sendPacket(sm);
            return false;
        } else if (fort.getSiege().getAttackerClan(player.getClan()) == null) {
            player.sendPacket(sm);
            return false;
        }
        return true;
    }

    public void dropCombatFlag(L2PcInstance player, int fortId) {
        final Fort fort = FortManager.getInstance().getFortById(fortId);
        final List<CombatFlag> fcf = _flagList.get(fort.getResidenceId());
        for (CombatFlag cf : fcf) {
            if (cf.getPlayerObjectId() == player.getObjectId()) {
                cf.dropIt();
                if (fort.getSiege().isInProgress()) {
                    cf.spawnMe();
                }
            }
        }
    }

    private static class SingletonHolder {
        protected static final FortSiegeManager _instance = new FortSiegeManager();
    }
}
