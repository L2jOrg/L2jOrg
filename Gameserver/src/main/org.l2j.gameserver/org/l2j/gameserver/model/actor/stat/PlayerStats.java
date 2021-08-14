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
package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.enums.PartySmallWindowUpdateType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.friend.FriendStatus;
import org.l2j.gameserver.network.serverpackets.mission.ExOneDayReceiveRewardList;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.PartySettings;
import org.l2j.gameserver.settings.RateSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.round;
import static java.util.Objects.isNull;
import static org.l2j.gameserver.enums.UserInfoType.CURRENT_HPMPCP_EXP_SP;
import static org.l2j.gameserver.network.serverpackets.ExUserBoostStat.BoostStatType;

public class PlayerStats extends PlayableStats {
    public static final int MAX_VITALITY_POINTS = 140000;
    public static final int MIN_VITALITY_POINTS = 0;

    private final AtomicInteger _talismanSlots = new AtomicInteger();
    private long startingXp;
    private int vitalityPoints;

    public PlayerStats(Player player) {
        super(player);
    }

    @Override
    public boolean addExp(long value) {
        final Player activeChar = getCreature();

        // Allowed to gain exp?
        if (!activeChar.getAccessLevel().canGainExp()) {
            return false;
        }

        if (!super.addExp(value)) {
            return false;
        }

        if (activeChar.getReputation() < 0 && (activeChar.isGM() || !activeChar.isInsideZone(ZoneType.PVP))) {
            final int karmaLost = Formulas.calculateKarmaLost(activeChar, value);
            if (karmaLost > 0) {
                activeChar.setReputation(Math.min((activeChar.getReputation() + karmaLost), 0));
            }
        }

        // EXP status update currently not used in retail
        activeChar.sendPacket(new UserInfo(activeChar, UserInfoType.CURRENT_HPMPCP_EXP_SP));
        return true;
    }

    public void addExpAndSp(double addToExp, double addToSp, boolean useBonuses) {
        final Player activeChar = getCreature();

        // Allowed to gain exp/sp?
        if (!activeChar.getAccessLevel().canGainExp()) {
            return;
        }

        final double baseExp = addToExp;
        final double baseSp = addToSp;

        double bonusExp = 1.;
        double bonusSp = 1.;

        if (useBonuses) {
            bonusExp = getExpBonusMultiplier();
            bonusSp = getSpBonusMultiplier();
        }

        addToExp *= bonusExp;
        addToSp *= bonusSp;

        double ratioTakenByPlayer;

        // if this player has a pet and it is in his range he takes from the owner's Exp, give the pet Exp now
        final Pet pet = activeChar.getPet();
        if ((pet != null) && GameUtils.checkIfInShortRange(PartySettings.partyRange(), activeChar, pet, false)) {
            ratioTakenByPlayer = pet.getPetLevelData().getOwnerExpTaken() / 100f;

            // only give exp/sp to the pet by taking from the owner if the pet has a non-zero, positive ratio
            // allow possible customizations that would have the pet earning more than 100% of the owner's exp/sp
            if (ratioTakenByPlayer > 1) {
                ratioTakenByPlayer = 1;
            }

            if (!pet.isDead()) {
                pet.addExpAndSp(addToExp * (1 - ratioTakenByPlayer), addToSp * (1 - ratioTakenByPlayer));
            }

            // now adjust the max ratio to avoid the owner earning negative exp/sp
            addToExp *= ratioTakenByPlayer;
            addToSp *= ratioTakenByPlayer;
        }

        final long finalExp = round(addToExp);
        final long finalSp = round(addToSp);
        final boolean expAdded = addExp(finalExp);
        final boolean spAdded = addSp(finalSp);

        SystemMessage sm;
        if (!expAdded && spAdded) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_SP);
            sm.addLong(finalSp);
        } else if (expAdded && !spAdded) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_XP);
            sm.addLong(finalExp);
        } else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_XP_BONUS_S2_AND_S3_SP_BONUS_S4);
            sm.addLong(finalExp);
            sm.addLong(round(addToExp - baseExp));
            sm.addLong(finalSp);
            sm.addLong(round(addToSp - baseSp));
        }
        activeChar.sendPacket(sm);
    }

    @Override
    public boolean removeExpAndSp(long addToExp, long addToSp) {
        return removeExpAndSp(addToExp, addToSp, true);
    }

    public boolean removeExpAndSp(long addToExp, long addToSp, boolean sendMessage) {
        final int level = getLevel();
        if (!super.removeExpAndSp(addToExp, addToSp)) {
            return false;
        }

        if (sendMessage) {
            // Send a Server->Client System Message to the Player
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_XP_HAS_DECREASED_BY_S1);
            sm.addLong(addToExp);
            getCreature().sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
            sm.addLong(addToSp);
            getCreature().sendPacket(sm);
            if (getLevel() < level) {
                getCreature().broadcastStatusUpdate();
            }
        }
        return true;
    }

    @Override
    public final boolean addLevel(byte value) {
        if (getLevel() + value > LevelData.getInstance().getMaxLevel()) {
            return false;
        }

        Player player = getCreature();
        byte oldLevel = getLevel();
        final boolean levelIncreased = super.addLevel(value);
        if (levelIncreased) {
            player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.LEVEL_UP));
            player.setCurrentCp(getMaxCp());
            player.sendPacket(SystemMessageId.YOUR_LEVEL_HAS_INCREASED);
            player.notifyFriends(FriendStatus.LEVEL);
        }

        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLevelChanged(player, oldLevel, getLevel()), player);

        player.rewardSkills();

        if (player.getClan() != null) {
            player.getClan().onMemberLevelChanged(player, oldLevel, getLevel());
        }
        if (player.isInParty()) {
            player.getParty().recalculatePartyLevel(); // Recalculate the party level
        }

        syncPetLevel(player);

        if (getLevel() >= 40) {
            player.initElementalSpirits();
        }
        player.updateCharacteristicPoints();
        player.broadcastStatusUpdate();
        player.refreshOverloaded(true);
        player.sendPackets(new UserInfo(player), new AcquireSkillList(player), new ExVoteSystemInfo(player), new ExOneDayReceiveRewardList(player, true));
        return levelIncreased;
    }

    private void syncPetLevel(Player player) {
        final Pet sPet = player.getPet();
        if (sPet != null && sPet.getPetData().isSyncLevel() && sPet.getLevel() != getLevel()) {
            final byte availableLevel = (byte) Math.min(sPet.getPetData().getMaxLevel(), getLevel());
            sPet.getStats().setLevel(availableLevel);
            sPet.getStats().getExpForLevel(availableLevel);
            sPet.setCurrentHp(sPet.getMaxHp());
            sPet.setCurrentMp(sPet.getMaxMp());
            sPet.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.LEVEL_UP));
            sPet.updateAndBroadcastStatus(1);
        }
    }

    @Override
    public boolean addSp(long value) {
        if (!super.addSp(value)) {
            return false;
        }
        getCreature().broadcastUserInfo(CURRENT_HPMPCP_EXP_SP);
        return true;
    }

    @Override
    public final long getExpForLevel(int level) {
        return LevelData.getInstance().getExpForLevel(level);
    }

    @Override
    public final Player getCreature() {
        return (Player) super.getCreature();
    }

    public final long getBaseExp() {
        return super.getExp();
    }

    public long getStartingExp() {
        return startingXp;
    }

    public void setStartingXp(long xp) {
        this.startingXp = xp;
    }

    /**
     * Gets the maximum talisman count.
     *
     * @return the maximum talisman count
     */
    public int getTalismanSlots() {
        return _talismanSlots.get();
    }

    public void addTalismanSlots(int count) {
        _talismanSlots.addAndGet(count);
    }

    @Override
    public final void setLevel(byte value) {
        if (value > LevelData.getInstance().getMaxLevel()) {
            value = LevelData.getInstance().getMaxLevel();
        }

        super.setLevel(value);
    }

    public final byte getBaseLevel() {
        return super.getLevel();
    }

    public final long getBaseSp() {
        return super.getSp();
    }

    public int getVitalityPoints() {
        return Math.min(Math.max(vitalityPoints, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
    }

    public void setVitalityPoints(int value) {
        vitalityPoints = Math.min(Math.max(value, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
    }

    public int getBaseVitalityPoints() {
        return Math.min(Math.max(vitalityPoints, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
    }

    public double getVitalityExpBonus() {
        return (getVitalityPoints() > 0) ? getValue(Stat.VITALITY_EXP_RATE, RateSettings.rateVitalityExpMul()) : 1.0;
    }

    /*
     * Set current vitality points to this value if quiet = true - does not send system messages
     */
    public void setVitalityPoints(int points, boolean quiet) {
        points = Math.min(Math.max(points, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
        if (points == getVitalityPoints()) {
            return;
        }

        if (!quiet) {
            if (points < getVitalityPoints()) {
                getCreature().sendPacket(SystemMessageId.YOUR_VITALITY_HAS_DECREASED);
            } else {
                getCreature().sendPacket(SystemMessageId.YOUR_VITALITY_HAS_INCREASED);
            }
        }

        setVitalityPoints(points);

        if (points == 0) {
            getCreature().sendPacket(SystemMessageId.YOUR_VITALITY_IS_FULLY_EXHAUSTED);
        } else if (points == MAX_VITALITY_POINTS) {
            getCreature().sendPacket(SystemMessageId.YOUR_VITALITY_IS_AT_MAXIMUM);
        }

        final Player player = getCreature();
        player.sendPacket(new ExVitalityPointInfo(getVitalityPoints()));
        player.broadcastUserInfo(UserInfoType.VITA_FAME);
        final Party party = player.getParty();
        if (party != null) {
            final PartySmallWindowUpdate partyWindow = new PartySmallWindowUpdate(player, false);
            partyWindow.addComponentType(PartySmallWindowUpdateType.VITALITY_POINTS);
            party.broadcastToPartyMembers(player, partyWindow);
        }
    }

    public void updateVitalityPoints(int points, boolean useRates) {
        if (points == 0 || !CharacterSettings.vitalityEnabled()) {
            return;
        }

        if (useRates) {
            if (getCreature().isLucky()) {
                return;
            }

            if (points < 0) // vitality consumed
            {
                final int stat = (int) getValue(Stat.VITALITY_CONSUME_RATE, 1);

                if (stat == 0) {
                    return;
                }
                if (stat < 0) {
                    points = -points;
                }
            }

            if (points > 0) {
                // vitality increased
                points *= RateSettings.rateVitalityGain();
            } else {
                // vitality decreased
                points *= RateSettings.rateVitalityLoss();
            }
        }

        if (points > 0) {
            points = Math.min(getVitalityPoints() + points, MAX_VITALITY_POINTS);
        } else {
            points = Math.max(getVitalityPoints() + points, MIN_VITALITY_POINTS);
        }

        if (Math.abs(points - getVitalityPoints()) <= 1e-6) {
            return;
        }

        setVitalityPoints(points);
    }

    public double getExpBonusMultiplier() {
        double bonus = 1.0;
        double vitality;
        double bonusExp;

        // Bonus from Vitality System
        vitality = getVitalityExpBonus();

        // Bonus exp from skills
        bonusExp = getValue(Stat.BONUS_EXP, 1);

        if (vitality > 1.0) {
            bonus += (vitality - 1);
        }

        if (bonusExp > 1) {
            bonus += (bonusExp - 1);
        }

        // Check for abnormal bonuses
        bonus = Math.max(bonus, 1);

        return bonus;
    }

    public double getSpBonusMultiplier() {
        double bonus = 1.0;
        double vitality;
        double bonusSp;

        // Bonus from Vitality System
        vitality = getVitalityExpBonus();

        // Bonus sp from skills
        bonusSp = 1 + (getValue(Stat.BONUS_SP, 0) / 100);

        if (vitality > 1.0) {
            bonus += (vitality - 1);
        }

        if (bonusSp > 1) {
            bonus += (bonusSp - 1);
        }

        return Math.max(bonus, 1);
    }

    /**
     * Gets the maximum brooch jewel count.
     *
     * @return the maximum brooch jewel count
     */
    public int getBroochJewelSlots() {
        return (int) getValue(Stat.BROOCH_JEWELS, 0);
    }

    /**
     * Gets the maximum agathion count.
     *
     * @return the maximum agathion count
     */
    public int getAgathionSlots() {
        return (int) getValue(Stat.AGATHION_SLOTS, 0);
    }

    /**
     * Gets the maximum artifact book count.
     *
     * @return the maximum artifact book count
     */
    public int getArtifactSlots() {
        return (int) getValue(Stat.ARTIFACT_SLOTS, 0);
    }

    public double getElementalSpiritXpBonus() {
        return getValue(Stat.ELEMENTAL_SPIRIT_BONUS_XP, 1);
    }

    public double getElementalSpiritPower(ElementalType type, double base) {
        return isNull(type) ? 0 : getValue(type.getAttackStat(), base);
    }

    public double getElementalSpiritCriticalRate(int base) {
        return getValue(Stat.ELEMENTAL_SPIRIT_CRITICAL_RATE, base);
    }

    public double getElementalSpiritCriticalDamage(double base) {
        return getValue(Stat.ELEMENTAL_SPIRIT_CRITICAL_DAMAGE, base);
    }

    public double getElementalSpiritDefense(ElementalType type, double base) {
        return isNull(type) ? 0 : getValue(type.getDefenseStat(), base);
    }

    public double getEnchantRateBonus() {
        return getValue(Stat.ENCHANT_RATE_BONUS, 0);
    }

    @Override
    protected void onRecalculateStats(boolean broadcast) {
        super.onRecalculateStats(broadcast);

        final Player player = getCreature();
        if (player.hasAbnormalType(AbnormalType.ABILITY_CHANGE) && player.hasServitors()) {
            player.getServitors().values().forEach(servitor -> servitor.getStats().recalculateStats(broadcast));
        }
        // TODO send only when XP_BONUS change
        player.sendPacket(new ExUserBoostStat(BoostStatType.STAT, (short) (round(getExpBonusMultiplier() * 100) - 100)));
    }
}
