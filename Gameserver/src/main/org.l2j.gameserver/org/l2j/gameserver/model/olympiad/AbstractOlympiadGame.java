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
package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Party.MessageType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * @author godson, GodKratos, Pere, DS
 */
public abstract class AbstractOlympiadGame {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractOlympiadGame.class);
    protected static final Logger LOGGER_OLYMPIAD = LoggerFactory.getLogger("olympiad");

    protected static final String POINTS = "olympiad_points";
    protected static final String COMP_DONE = "competitions_done";
    protected static final String COMP_WON = "competitions_won";
    protected static final String COMP_LOST = "competitions_lost";
    protected static final String COMP_DRAWN = "competitions_drawn";
    protected static final String COMP_DONE_WEEK = "competitions_done_week";
    protected static final String COMP_DONE_WEEK_CLASSED = "competitions_done_week_classed";
    protected static final String COMP_DONE_WEEK_NON_CLASSED = "competitions_done_week_non_classed";
    protected static final String COMP_DONE_WEEK_TEAM = "competitions_done_week_team";

    protected long _startTime = 0;
    protected boolean _aborted = false;
	protected final int _stadiumId;

    protected AbstractOlympiadGame(int id) {
        _stadiumId = id;
    }

    /**
     * Function return null if player passed all checks or SystemMessage with reason for broadcast to opponent(s).
     *
     * @param player
     * @return
     */
    protected static SystemMessage checkDefaulted(Player player) {
        if ((player == null) || !player.isOnline()) {
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED);
        }

        if ((player.getClient() == null) || player.getClient().isDetached()) {
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED);
        }

        // safety precautions
        if (player.inObserverMode()) {
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
        }

        SystemMessage sm;
        if (player.isDead()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_DEAD_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
            sm.addPcName(player);
            player.sendPacket(sm);
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
        }
        if (player.isSubClassActive()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGED_YOUR_CLASS_TO_SUBCLASS);
            sm.addPcName(player);
            player.sendPacket(sm);
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
        }
        if (!player.isInventoryUnder90(true)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_FOR_OLYMPIAD_AS_THE_INVENTORY_WEIGHT_SLOT_EXCEEDS_80);
            sm.addPcName(player);
            player.sendPacket(sm);
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
        }

        return null;
    }

    protected static boolean portPlayerToArena(Participant par, Location loc, int id, Instance instance) {
        final Player player = par.getPlayer();
        if ((player == null) || !player.isOnline()) {
            return false;
        }

        try {
            player.setLastLocation();
            if (player.isSitting()) {
                player.standUp();
            }
            player.setTarget(null);

            player.setOlympiadGameId(id);
            player.setIsInOlympiadMode(true);
            player.setIsOlympiadStart(false);
            player.setOlympiadSide(par.getSide());
            player.teleToLocation(loc, instance);
            player.sendPacket(new ExOlympiadMode(2));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
        return true;
    }

    protected static void removals(Player player, boolean removeParty) {
        try {
            if (player == null) {
                return;
            }

            // Remove Buffs
            player.stopAllEffectsExceptThoseThatLastThroughDeath();
            player.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);

            // Remove Clan Skills
            if (player.getClan() != null) {
                player.getClan().removeSkillEffects(player);
                if (player.getClan().getCastleId() > 0) {
                    CastleManager.getInstance().getCastleByOwner(player.getClan()).removeResidentialSkills(player);
                }
            }
            // Abort casting if player casting
            player.abortAttack();
            player.abortCast();

            // Force the character to be visible
            player.setInvisible(false);

            // Heal Player fully
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());

            // Remove Summon's Buffs
            if (player.hasSummon()) {
                final Summon pet = player.getPet();
                if (pet != null) {
                    pet.unSummon(player);
                }

                player.getServitors().values().forEach(s ->
                {
                    s.stopAllEffectsExceptThoseThatLastThroughDeath();
                    s.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
                    s.abortAttack();
                    s.abortCast();
                });
            }

            // stop any cubic that has been given by other player.
            player.stopCubicsByOthers();

            // Remove player from his party
            if (removeParty) {
                final Party party = player.getParty();
                if (party != null) {
                    party.removePartyMember(player, MessageType.EXPELLED);
                }
            }
            // Remove Agathion
            if (player.getAgathionId() > 0) {
                player.setAgathionId(0);
                player.broadcastUserInfo();
            }

            player.checkItemRestriction();

            // Remove shot automation
            player.disableAutoShots();

            // Discharge any active shots
            player.unchargeAllShots();

            // enable skills with cool time <= 15 minutes
            for (Skill skill : player.getAllSkills()) {
                if (skill.getReuseDelay() <= 900000) {
                    player.enableSkill(skill);
                }
            }

            player.sendSkillList();
            player.sendPacket(new SkillCoolTime(player));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    protected static void cleanEffects(Player player) {
        try {
            // prevent players kill each other
            player.setIsOlympiadStart(false);
            player.setTarget(null);
            player.abortAttack();
            player.abortCast();
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);

            if (player.isDead()) {
                player.setIsDead(false);
            }

            player.stopAllEffectsExceptThoseThatLastThroughDeath();
            player.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
            player.clearSouls();
            player.clearCharges();
            if (player.getAgathionId() > 0) {
                player.setAgathionId(0);
            }
            final Summon pet = player.getPet();
            if ((pet != null) && !pet.isDead()) {
                pet.setTarget(null);
                pet.abortAttack();
                pet.abortCast();
                pet.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                pet.stopAllEffectsExceptThoseThatLastThroughDeath();
                pet.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
            }

            player.getServitors().values().stream().filter(s -> !s.isDead()).forEach(s ->
            {
                s.setTarget(null);
                s.abortAttack();
                s.abortCast();
                s.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                s.stopAllEffectsExceptThoseThatLastThroughDeath();
                s.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
            });

            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.getStatus().startHpMpRegeneration();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    protected static void playerStatusBack(Player player) {
        try {
            if (player.isTransformed()) {
                player.untransform();
            }

            player.setIsInOlympiadMode(false);
            player.setIsOlympiadStart(false);
            player.setOlympiadSide(-1);
            player.setOlympiadGameId(-1);
            player.sendPacket(new ExOlympiadMode(0));

            // Add Clan Skills
            if (player.getClan() != null) {
                player.getClan().addSkillEffects(player);
                if (player.getClan().getCastleId() > 0) {
                    CastleManager.getInstance().getCastleByOwner(player.getClan()).giveResidentialSkills(player);
                }
                player.sendSkillList();
            }

            // heal again after adding clan skills
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.getStatus().startHpMpRegeneration();

            if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) {
                AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, player);
            }
        } catch (Exception e) {
            LOGGER.warn("playerStatusBack()", e);
        }
    }

    protected static void portPlayerBack(Player player) {
        if (player == null) {
            return;
        }
        final Location loc = player.getLastLocation();
        if (loc != null) {
            player.setIsPendingRevive(false);
            player.teleToLocation(loc, null);
            player.unsetLastLocation();
        }
    }

    public static void rewardParticipant(Player player, List<ItemHolder> list) {
        if ((player == null) || !player.isOnline() || (list == null)) {
            return;
        }

        try {
            final InventoryUpdate iu = new InventoryUpdate();
            list.forEach(holder ->
            {
                final Item item = player.getInventory().addItem("Olympiad", holder.getId(), holder.getCount(), player, null);
                if (item == null) {
                    return;
                }

                iu.addModifiedItem(item);
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                sm.addItemName(item);
                sm.addLong(holder.getCount());
                player.sendPacket(sm);
            });
            player.sendInventoryUpdate(iu);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public final boolean isAborted() {
        return _aborted;
    }

    public final int getStadiumId() {
        return _stadiumId;
    }

    protected boolean makeCompetitionStart() {
        _startTime = System.currentTimeMillis();
        return !_aborted;
    }

    protected final void addPointsToParticipant(Participant par, int points) {
        par.updateStat(POINTS, points);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_EARNED_S2_POINTS_IN_THE_OLYMPIAD_GAMES);
        sm.addString(par.getName());
        sm.addInt(points);
        broadcastPacket(sm);
    }

    protected final void removePointsFromParticipant(Participant par, int points) {
        par.updateStat(POINTS, -points);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_LOST_S2_POINTS_IN_THE_OLYMPIAD_GAMES);
        sm.addString(par.getName());
        sm.addInt(points);
        broadcastPacket(sm);
    }

    public abstract CompetitionType getType();

    public abstract String[] getPlayerNames();

    public abstract boolean containsParticipant(int playerId);

    public abstract void sendOlympiadInfo(Creature player);

    public abstract void broadcastOlympiadInfo(OlympiadStadium _stadium);

    protected abstract void broadcastPacket(ServerPacket packet);

    protected abstract boolean needBuffers();

    protected abstract boolean checkDefaulted();

    protected abstract void removals();

    protected abstract boolean portPlayersToArena(List<Location> spawns, Instance instance);

    protected abstract void cleanEffects();

    protected abstract void portPlayersBack();

    protected abstract void playersStatusBack();

    protected abstract void clearPlayers();

    protected abstract void handleDisconnect(Player player);

    protected abstract void resetDamage();

    protected abstract void addDamage(Player player, int damage);

    protected abstract boolean checkBattleStatus();

    protected abstract boolean haveWinner();

    protected abstract void validateWinner(OlympiadStadium stadium);

    protected abstract int getDivider();

    protected abstract void healPlayers();

    protected abstract void untransformPlayers();

    protected abstract void makePlayersInvul();

    protected abstract void removePlayersInvul();
}
