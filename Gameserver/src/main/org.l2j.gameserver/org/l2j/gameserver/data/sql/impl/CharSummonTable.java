package org.l2j.gameserver.data.sql.impl;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.model.L2PetData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.actor.instance.L2ServitorInstance;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nyaran
 */
public class CharSummonTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharSummonTable.class);
    private static final Map<Integer, Integer> _pets = new ConcurrentHashMap<>();
    private static final Map<Integer, Set<Integer>> _servitors = new ConcurrentHashMap<>();

    // SQL
    private static final String INIT_PET = "SELECT ownerId, item_obj_id FROM pets WHERE restore = 'true'";
    private static final String INIT_SUMMONS = "SELECT ownerId, summonId FROM character_summons";
    private static final String LOAD_SUMMON = "SELECT summonSkillId, summonId, curHp, curMp, time FROM character_summons WHERE ownerId = ?";
    private static final String REMOVE_SUMMON = "DELETE FROM character_summons WHERE ownerId = ? and summonId = ?";
    private static final String SAVE_SUMMON = "REPLACE INTO character_summons (ownerId,summonId,summonSkillId,curHp,curMp,time) VALUES (?,?,?,?,?,?)";

    private CharSummonTable(){
    }

    public Map<Integer, Integer> getPets() {
        return _pets;
    }

    public Map<Integer, Set<Integer>> getServitors() {
        return _servitors;
    }

    public void init() {
        if (Config.RESTORE_SERVITOR_ON_RECONNECT) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 Statement s = con.createStatement();
                 ResultSet rs = s.executeQuery(INIT_SUMMONS)) {
                while (rs.next()) {
                    _servitors.computeIfAbsent(rs.getInt("ownerId"), k -> ConcurrentHashMap.newKeySet()).add(rs.getInt("summonId"));
                }
            } catch (Exception e) {
                LOGGER.warn(": Error while loading saved servitor: " + e);
            }
        }

        if (Config.RESTORE_PET_ON_RECONNECT) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 Statement s = con.createStatement();
                 ResultSet rs = s.executeQuery(INIT_PET)) {
                while (rs.next()) {
                    _pets.put(rs.getInt("ownerId"), rs.getInt("item_obj_id"));
                }
            } catch (Exception e) {
                LOGGER.warn(": Error while loading saved pet: " + e);
            }
        }
    }

    public void removeServitor(Player activeChar, int summonObjectId) {
        _servitors.computeIfPresent(activeChar.getObjectId(), (k, v) ->
        {
            v.remove(summonObjectId);
            return !v.isEmpty() ? v : null;
        });

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(REMOVE_SUMMON)) {
            ps.setInt(1, activeChar.getObjectId());
            ps.setInt(2, summonObjectId);
            ps.execute();
        } catch (SQLException e) {
            LOGGER.warn(": Summon cannot be removed: " + e);
        }
    }

    public void restorePet(Player activeChar) {
        final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_pets.get(activeChar.getObjectId()));
        if (item == null) {
            LOGGER.warn(": Null pet summoning item for: " + activeChar);
            return;
        }
        final L2PetData petData = PetDataTable.getInstance().getPetDataByItemId(item.getId());
        if (petData == null) {
            LOGGER.warn(": Null pet data for: " + activeChar + " and summoning item: " + item);
            return;
        }
        final L2NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(petData.getNpcId());
        if (npcTemplate == null) {
            LOGGER.warn(": Null pet NPC template for: " + activeChar + " and pet Id:" + petData.getNpcId());
            return;
        }

        final L2PetInstance pet = L2PetInstance.spawnPet(npcTemplate, activeChar, item);
        if (pet == null) {
            LOGGER.warn(": Null pet instance for: " + activeChar + " and pet NPC template:" + npcTemplate);
            return;
        }

        pet.setShowSummonAnimation(true);
        pet.setTitle(activeChar.getName());

        if (!pet.isRespawned()) {
            pet.setCurrentHp(pet.getMaxHp());
            pet.setCurrentMp(pet.getMaxMp());
            pet.getStat().setExp(pet.getExpForThisLevel());
            pet.setCurrentFed(pet.getMaxFed());
        }

        pet.setRunning();

        if (!pet.isRespawned()) {
            pet.storeMe();
        }

        item.setEnchantLevel(pet.getLevel());
        activeChar.setPet(pet);
        pet.spawnMe(activeChar.getX() + 50, activeChar.getY() + 100, activeChar.getZ());
        pet.startFeed();
        pet.setFollowStatus(true);
        pet.getOwner().sendPacket(new PetItemList(pet.getInventory().getItems()));
        pet.broadcastStatusUpdate();
    }

    public void restoreServitor(Player activeChar) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(LOAD_SUMMON)) {
            ps.setInt(1, activeChar.getObjectId());
            try (ResultSet rs = ps.executeQuery()) {
                Skill skill;
                while (rs.next()) {
                    final int summonObjId = rs.getInt("summonId");
                    final int skillId = rs.getInt("summonSkillId");
                    final int curHp = rs.getInt("curHp");
                    final int curMp = rs.getInt("curMp");
                    final int time = rs.getInt("time");

                    skill = SkillData.getInstance().getSkill(skillId, activeChar.getSkillLevel(skillId));
                    if (skill == null) {
                        removeServitor(activeChar, summonObjId);
                        return;
                    }
                    skill.applyEffects(activeChar, activeChar);

                    if (activeChar.hasServitors()) {
                        final L2ServitorInstance summon = activeChar.getServitors().values().stream().map(s -> ((L2ServitorInstance) s)).filter(s -> s.getReferenceSkill() == skillId).findAny().orElse(null);
                        if(summon != null) {
                            summon.setCurrentHp(curHp);
                            summon.setCurrentMp(curMp);
                            summon.setLifeTimeRemaining(time);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Servitor cannot be restored: " + e);
        }
    }

    public void saveSummon(L2ServitorInstance summon) {
        if ((summon == null) || (summon.getLifeTimeRemaining() <= 0)) {
            return;
        }

        _servitors.computeIfAbsent(summon.getOwner().getObjectId(), k -> ConcurrentHashMap.newKeySet()).add(summon.getObjectId());

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SAVE_SUMMON)) {
            ps.setInt(1, summon.getOwner().getObjectId());
            ps.setInt(2, summon.getObjectId());
            ps.setInt(3, summon.getReferenceSkill());
            ps.setInt(4, (int) Math.round(summon.getCurrentHp()));
            ps.setInt(5, (int) Math.round(summon.getCurrentMp()));
            ps.setInt(6, summon.getLifeTimeRemaining());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn(": Failed to store summon: " + summon + " from " + summon.getOwner() + ", error: " + e);
        }
    }

    public static CharSummonTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CharSummonTable INSTANCE = new CharSummonTable();
    }
}
