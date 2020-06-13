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
package org.l2j.gameserver.model;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.instancemanager.PcCafePointsManager;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldTimeController;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * This class serves as a container for player parties.
 *
 * @author nuocnam
 */
public class Party extends AbstractPlayerGroup {

    // @formatter:off
    private static final double[] BONUS_EXP_SP = {
        1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 2.0
    };
    // @formatter:on

    private static final Duration PARTY_POSITION_BROADCAST_INTERVAL = Duration.ofSeconds(12);
    private static final Duration PARTY_DISTRIBUTION_TYPE_REQUEST_TIMEOUT = Duration.ofSeconds(15);

    private static final int[] TACTICAL_SYS_STRINGS = {
        0,
        2664,
        2665,
        2666,
        2667
    };

    private final IntMap<Creature> tacticalSigns = new HashIntMap<>();

    private final List<Player> members = Collections.synchronizedList(new ArrayList<>());
    protected PartyMemberPosition positionPacket;
    private boolean _pendingInvitation = false;
    private long _pendingInviteTimeout;
    private int partyLvl;
    private volatile PartyDistributionType distributionType;
    private volatile PartyDistributionType changeRequestDistributionType;
    private volatile Future<?> changeDistributionTypeRequestTask = null;
    private volatile Set<Integer> _changeDistributionTypeAnswers = null;
    private int itemLastLoot = 0;
    private CommandChannel commandChannel = null;
    private Future<?> positionBroadcastTask = null;

    public Party(Player leader, PartyDistributionType partyDistributionType) {
        members.add(leader);
        partyLvl = leader.getLevel();
        distributionType = partyDistributionType;
        World.getInstance().incrementParty();
    }

    /**
     * Check if another player can start invitation process.
     *
     * @return {@code true} if this party waits for a response on an invitation, {@code false} otherwise
     */
    public boolean getPendingInvitation() {
        return _pendingInvitation;
    }

    /**
     * Set invitation process flag and store time for expiration. <br>
     * Happens when a player joins party or declines to join.
     *
     * @param val the pending invitation state to set
     */
    public void setPendingInvitation(boolean val) {
        _pendingInvitation = val;
        _pendingInviteTimeout = WorldTimeController.getInstance().getGameTicks() + (Player.REQUEST_TIMEOUT * WorldTimeController.TICKS_PER_SECOND);
    }

    /**
     * Check if a player invitation request is expired.
     *
     * @return {@code true} if time is expired, {@code false} otherwise
     * @see Player#isRequestExpired()
     */
    public boolean isInvitationRequestExpired() {
        return (_pendingInviteTimeout <= WorldTimeController.getInstance().getGameTicks());
    }

    /**
     * Get a random member from this party.
     *
     * @param itemId the ID of the item for which the member must have inventory space
     * @param target the object of which the member must be within a certain range (must not be null)
     * @return a random member from this party or {@code null} if none of the members have inventory space for the specified item
     */
    private Player getCheckedRandomMember(int itemId, Creature target) {
        return Rnd.get(members.stream()
                .filter(member -> member.getInventory().validateCapacityByItemId(itemId) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true))
                .collect(Collectors.toList()));
    }

    private Player getCheckedNextLooter(int ItemId, Creature target) {
        for (int i = 0; i < members.size(); i++) {
            if (++itemLastLoot >= members.size()) {
                itemLastLoot = 0;
            }

            var member = members.get(itemLastLoot);
            if (member.getInventory().validateCapacityByItemId(ItemId) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true)) {
                return member;
            }
        }
        return null;
    }

    private Player getActualLooter(Player player, int ItemId, boolean spoil, Creature target) {
        Player looter = null;

        switch (distributionType) {
            case RANDOM -> {
                if (!spoil) {
                    looter = getCheckedRandomMember(ItemId, target);
                }
            }
            case RANDOM_INCLUDING_SPOIL -> looter = getCheckedRandomMember(ItemId, target);
            case BY_TURN -> {
                if (!spoil) {
                    looter = getCheckedNextLooter(ItemId, target);
                }
            }
            case BY_TURN_INCLUDING_SPOIL -> looter = getCheckedNextLooter(ItemId, target);
        }

        return looter != null ? looter : player;
    }

    /**
     * Broadcasts UI update and User Info for new party leader.
     */
    public void broadcastToPartyMembersNewLeader() {
        members.forEach(member -> {
            member.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET, new PartySmallWindowAll(member, this));
            member.broadcastUserInfo();
        });
    }

    public void broadcastToPartyMembers(Player player, ServerPacket msg) {
        members.stream().filter(member -> member.getObjectId() != player.getObjectId()).forEach(msg::sendTo);
    }

    public void addPartyMember(Player player) {
        if (members.contains(player)) {
            return;
        }

        if (nonNull(changeRequestDistributionType)) {
            finishLootRequest(false); // cancel on invite
        }

        members.add(player);

        // sends new member party window for all members
        // we do all actions before adding member to a list, this speeds things up a little
        player.sendPacket(new PartySmallWindowAll(player, this));

        // sends pets/summons of party members
        for (Player member : members) {
            doIfNonNull(member.getPet(), pet ->  player.sendPacket(new ExPartyPetWindowAdd(pet)));
            member.getServitors().values().forEach(s -> player.sendPacket(new ExPartyPetWindowAdd(s)));
        }

        player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_JOINED_S1_S_PARTY).addString(getLeader().getName()));
        broadcastPacket(getSystemMessage(SystemMessageId.C1_HAS_JOINED_THE_PARTY).addString(player.getName()));

        members.stream().filter(member -> member != player).forEach(member -> member.sendPacket(new PartySmallWindowAdd(player, this)));

        // if member has pet/summon add it to other as well
        final Summon pet = player.getPet();
        if (pet != null) {
            broadcastPacket(new ExPartyPetWindowAdd(pet));
        }

        player.getServitors().values().forEach(s -> broadcastPacket(new ExPartyPetWindowAdd(s)));

        // adjust party level
        if (player.getLevel() > partyLvl) {
            partyLvl = player.getLevel();
        }

        var su = new StatusUpdate(player).addUpdate(StatusUpdateType.MAX_HP, player.getMaxHp()).addUpdate(StatusUpdateType.CUR_HP, (int) player.getCurrentHp());

        for (Player member : members) {
            member.updateEffectIcons(true); // update party icons only
            member.broadcastUserInfo();
            doIfNonNull(member.getPet(), Creature::updateEffectIcons);
            member.getServitors().values().forEach(Summon::updateEffectIcons);
            member.sendPacket(su);
        }

        // open the CCInformationwindow
        if (isInCommandChannel()) {
            player.sendPacket(ExOpenMPCC.STATIC_PACKET);
        }

        if (positionBroadcastTask == null) {
            positionBroadcastTask = ThreadPool.scheduleAtFixedRate(() -> {
                if (positionPacket == null) {
                    positionPacket = new PartyMemberPosition(this);
                } else {
                    positionPacket.reuse(this);
                }
                broadcastPacket(positionPacket);
            }, PARTY_POSITION_BROADCAST_INTERVAL.toMillis() / 2, PARTY_POSITION_BROADCAST_INTERVAL.toMillis());
        }
        applyTacticalSigns(player, false);
        World.getInstance().incrementPartyMember();
    }

    private IntMap<Creature> getTacticalSigns() {
        return tacticalSigns;
    }

    public void applyTacticalSigns(Player player, boolean remove) {
        tacticalSigns.forEach((key, value) -> player.sendPacket(new ExTacticalSign(value, remove ? 0 : key)));
    }

    public void addTacticalSign(Player activeChar, int tacticalSignId, Creature target) {
        final Creature tacticalTarget = getTacticalSigns().get(tacticalSignId);

        if (tacticalTarget == null) {
            // if the new sign is applied to an existing target, remove the old sign from map
            tacticalSigns.values().remove(target);

            // Add the new sign
            tacticalSigns.put(tacticalSignId, target);

            final SystemMessage sm = getSystemMessage(SystemMessageId.C1_USED_S3_ON_C2);
            sm.addPcName(activeChar);
            sm.addString(target.getName());
            sm.addSystemString(TACTICAL_SYS_STRINGS[tacticalSignId]);

            members.forEach(m -> m.sendPacket(new ExTacticalSign(target, tacticalSignId), sm));
        } else if (tacticalTarget == target) {
            // Sign already assigned
            // If the sign is applied on the same target, remove it
            tacticalSigns.remove(tacticalSignId);
            members.forEach(m -> m.sendPacket(new ExTacticalSign(tacticalTarget, 0)));
        } else {
            // Otherwise, delete the old sign, and apply it to the new target
            tacticalSigns.replace(tacticalSignId, target);

            final SystemMessage sm = getSystemMessage(SystemMessageId.C1_USED_S3_ON_C2).addPcName(activeChar).addString(target.getName()).addSystemString(TACTICAL_SYS_STRINGS[tacticalSignId]);

            members.forEach(m -> m.sendPacket(new ExTacticalSign(tacticalTarget, 0), new ExTacticalSign(target, tacticalSignId), sm));
        }
    }

    public void setTargetBasedOnTacticalSignId(Player player, int tacticalSignId) {
        final Creature tacticalTarget = tacticalSigns.get(tacticalSignId);
        if ((tacticalTarget != null) && !tacticalTarget.isInvisible() && tacticalTarget.isTargetable() && !player.isTargetingDisabled()) {
            player.setTarget(tacticalTarget);
        }
    }

    /**
     * Removes a party member using its name.
     *
     * @param name player the player to be removed from the party.
     * @param type the message type {@link MessageType}.
     */
    public void removePartyMember(String name, MessageType type) {
        removePartyMember(getPlayerByName(name), type);
    }

    /**
     * Removes a party member instance.
     *
     * @param player the player to be removed from the party.
     * @param type   the message type {@link MessageType}.
     */
    public void removePartyMember(Player player, MessageType type) {
        if (members.contains(player)) {
            var isLeader = isLeader(player);

            if (members.size() == 2 || (isLeader && !Config.ALT_LEAVE_PARTY_LEADER && type != MessageType.DISCONNECTED)) {
                disbandParty();
                return;
            }

            members.remove(player);
            recalculatePartyLevel();

            onPlayerLeave(player, type);

            if (isLeader) {
                broadcastPacket(getSystemMessage(SystemMessageId.C1_HAS_BECOME_THE_PARTY_LEADER).addString(getLeader().getName()));
                broadcastToPartyMembersNewLeader();
            }
        }
    }

    private void onPlayerLeave(Player player, MessageType type) {
        if (player.isInDuel()) {
            DuelManager.getInstance().onRemoveFromParty(player);
        }

        // Channeling a player!
        if (player.isChanneling() && player.getSkillChannelizer().hasChannelized()) {
            player.abortCast();
        } else if (player.isChannelized()) {
            player.getSkillChannelized().abortChannelization();
        }

        if (type == MessageType.EXPELLED) {
            player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY);
            broadcastPacket(getSystemMessage(SystemMessageId.C1_WAS_EXPELLED_FROM_THE_PARTY).addString(player.getName()));
        } else if ((type == MessageType.LEFT) || (type == MessageType.DISCONNECTED)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY);
            broadcastPacket(getSystemMessage(SystemMessageId.C1_HAS_LEFT_THE_PARTY).addString(player.getName()));
        }

        // UI update.
        player.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
        player.setParty(null);
        broadcastPacket(new PartySmallWindowDelete(player));
        doIfNonNull(player.getPet(), pet -> broadcastPacket(new ExPartyPetWindowDelete(pet)));
        player.getServitors().values().forEach(s -> player.sendPacket(new ExPartyPetWindowDelete(s)));

        // Close the CCInfoWindow
        if (isInCommandChannel()) {
            player.sendPacket(ExCloseMPCC.STATIC_PACKET);
        }

        applyTacticalSigns(player, true);
        World.getInstance().decrementPartyMember();
    }

    /**
     * Disperse a party and send a message to all its members.
     */
    public void disbandParty() {
        broadcastPacket(getSystemMessage(SystemMessageId.THE_PARTY_HAS_DISPERSED));
        members.forEach(member -> onPlayerLeave(member, MessageType.NONE));
        if (isInCommandChannel()) {
            // delete the whole command channel when the party who opened the channel is disbanded
            if (commandChannel.getLeader().getObjectId() == getLeader().getObjectId()) {
                commandChannel.disbandChannel();
            } else {
                commandChannel.removeParty(this);
            }
        }
        members.clear();
        cancelTasks();
        World.getInstance().decrementParty();
    }

    private void cancelTasks() {
        if (nonNull(changeDistributionTypeRequestTask)) {
            changeDistributionTypeRequestTask.cancel(true);
            changeDistributionTypeRequestTask = null;
        }

        if (nonNull(positionBroadcastTask)) {
            positionBroadcastTask.cancel(false);
            positionBroadcastTask = null;
        }
    }

    /**
     * Change party leader (used for string arguments)
     *
     * @param name the name of the player to set as the new party leader
     */
    public void changePartyLeader(String name) {
        setLeader(getPlayerByName(name));
    }

    private Player getPlayerByName(String name) {
        for (Player member : members) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }

    public void distributeItem(Player player, Item item) {
        if (item.getId() == CommonItem.ADENA) {
            distributeAdena(player, item.getCount(), player);
            ItemEngine.getInstance().destroyItem("Party", item, player, null);
            return;
        }

        final Player target = getActualLooter(player, item.getId(), false, player);
        target.addItem("Party", item, player, true);

        // Send messages to other party members about reward
        if (item.getCount() > 1) {
            final SystemMessage msg = getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2);
            msg.addString(target.getName());
            msg.addItemName(item);
            msg.addLong(item.getCount());
            broadcastToPartyMembers(target, msg);
        } else {
            final SystemMessage msg = getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2);
            msg.addString(target.getName());
            msg.addItemName(item);
            broadcastToPartyMembers(target, msg);
        }
    }

    /**
     * Distributes item loot between party members.
     *
     * @param player    the reference player
     * @param itemId    the item ID
     * @param itemCount the item count
     * @param spoil     {@code true} if it's spoil loot
     * @param target    the NPC target
     */
    public void distributeItem(Player player, int itemId, long itemCount, boolean spoil, Attackable target) {
        if (itemId == CommonItem.ADENA) {
            distributeAdena(player, itemCount, target);
            return;
        }

        final Player looter = getActualLooter(player, itemId, spoil, target);

        looter.addItem(spoil ? "Sweeper Party" : "Party", itemId, itemCount, target, true);

        // Send messages to other party members about reward
        if (itemCount > 1) {
            final SystemMessage msg = spoil ? getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2_S_BY_USING_SWEEPER) : getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2);
            msg.addString(looter.getName());
            msg.addItemName(itemId);
            msg.addLong(itemCount);
            broadcastToPartyMembers(looter, msg);
        } else {
            final SystemMessage msg = spoil ? getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2_BY_USING_SWEEPER) : getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2);
            msg.addString(looter.getName());
            msg.addItemName(itemId);
            broadcastToPartyMembers(looter, msg);
        }
    }

    /**
     * Method overload for {@link Party#distributeItem(Player, int, long, boolean, Attackable)}
     *
     * @param player the reference player
     * @param item   the item holder
     * @param spoil  {@code true} if it's spoil loot
     * @param target the NPC target
     */
    public void distributeItem(Player player, ItemHolder item, boolean spoil, Attackable target) {
        distributeItem(player, item.getId(), item.getCount(), spoil, target);
    }

    public void distributeAdena(Player player, long adena, Creature target) {
        // Check the number of party members that must be rewarded
        // (The party member must be in range to receive its reward)
        final List<Player> toReward = new LinkedList<>();
        for (Player member : members) {
            if (GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true)) {
                toReward.add(member);
            }
        }

        if (!toReward.isEmpty()) {
            // Now we can actually distribute the adena reward
            // (Total adena splitted by the number of party members that are in range and must be rewarded)
            final long count = adena / toReward.size();
            for (Player member : toReward) {
                member.addAdena("Party", count, player, true);
            }
        }
    }

    /**
     * Distribute Experience and SP rewards to Player Party members in the known area of the last attacker.<BR>
     * <BR>
     * <B><U> Actions</U> :</B>
     * <li>Get the Player owner of the Servitor (if necessary)</li>
     * <li>Calculate the Experience and SP reward distribution rate</li>
     * <li>Add Experience and SP to the Player</li><BR>
     *
     * @param xpReward        The Experience reward to distribute
     * @param spReward        The SP reward to distribute
     * @param rewardedMembers The list of Player to reward
     * @param topLvl
     * @param partyDmg
     * @param target
     */
    public void distributeXpAndSp(double xpReward, double spReward, List<Player> rewardedMembers, int topLvl, long partyDmg, Attackable target) {
        final List<Player> validMembers = getValidMembers(rewardedMembers, topLvl);

        xpReward *= getExpBonus(validMembers.size(), target.getInstanceWorld());
        spReward *= getSpBonus(validMembers.size(), target.getInstanceWorld());

        int sqLevelSum = 0;
        for (Player member : validMembers) {
            sqLevelSum += (member.getLevel() * member.getLevel());
        }

        for (Player member : rewardedMembers) {
            if (member.isDead()) {
                continue;
            }

            // Calculate and add the EXP and SP reward to the member
            if (validMembers.contains(member)) {
                // The servitor penalty
                float penalty = 1;

                final Summon summon = member.getServitors().values().stream().filter(s -> ((Servitor) s).getExpMultiplier() > 1).findFirst().orElse(null);
                if (summon != null) {
                    penalty = ((Servitor) summon).getExpMultiplier();
                }

                final double sqLevel = member.getLevel() * member.getLevel();
                final double preCalculation = (sqLevel / sqLevelSum) * penalty;

                // Add the XP/SP points to the requested party member
                double exp = member.getStats().getValue(Stat.EXPSP_RATE, xpReward * preCalculation);
                double sp = member.getStats().getValue(Stat.EXPSP_RATE, spReward * preCalculation);

                exp = calculateExpSpPartyCutoff(member.getActingPlayer(), topLvl, exp, sp, target.useVitalityRate());
                if (exp > 0) {
                    final Clan clan = member.getClan();
                    if (clan != null) {
                        double finalExp = exp;
                        if (target.useVitalityRate()) {
                            finalExp *= member.getStats().getExpBonusMultiplier();
                        }
                        clan.addHuntingPoints(member, target, finalExp);
                    }
                    member.updateVitalityPoints(target.getVitalityPoints(member.getLevel(), exp, target.isRaid()), true, false);
                    PcCafePointsManager.getInstance().givePcCafePoint(member, exp);
                }
            } else {
                member.addExpAndSp(0, 0);
            }
        }
    }

    private double calculateExpSpPartyCutoff(Player player, int topLvl, double addExp, double addSp, boolean vit) {
        double xp = addExp;
        double sp = addSp;
        if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("highfive")) {
            int i = 0;
            final int lvlDiff = topLvl - player.getLevel();
            for (int[] gap : Config.PARTY_XP_CUTOFF_GAPS) {
                if ((lvlDiff >= gap[0]) && (lvlDiff <= gap[1])) {
                    xp = (addExp * Config.PARTY_XP_CUTOFF_GAP_PERCENTS[i]) / 100;
                    sp = (addSp * Config.PARTY_XP_CUTOFF_GAP_PERCENTS[i]) / 100;
                    player.addExpAndSp(xp, sp, vit);
                    break;
                }
                i++;
            }
        } else {
            player.addExpAndSp(addExp, addSp, vit);
        }
        return xp;
    }

    /**
     * refresh party level
     */
    public void recalculatePartyLevel() {
        int newLevel = 0;
        for (Player member : members) {
            if (member == null) {
                members.remove(member);
                continue;
            }

            if (member.getLevel() > newLevel) {
                newLevel = member.getLevel();
            }
        }
        partyLvl = newLevel;
    }

    private List<Player> getValidMembers(List<Player> members, int topLvl) {
        final List<Player> validMembers = new ArrayList<>();
        switch (Config.PARTY_XP_CUTOFF_METHOD) {
            case "level": {
                for (Player member : members) {
                    if ((topLvl - member.getLevel()) <= Config.PARTY_XP_CUTOFF_LEVEL) {
                        validMembers.add(member);
                    }
                }
                break;
            }
            case "percentage": {
                int sqLevelSum = 0;
                for (Player member : members) {
                    sqLevelSum += (member.getLevel() * member.getLevel());
                }
                for (Player member : members) {
                    final int sqLevel = member.getLevel() * member.getLevel();
                    if ((sqLevel * 100) >= (sqLevelSum * Config.PARTY_XP_CUTOFF_PERCENT)) {
                        validMembers.add(member);
                    }
                }
                break;
            }
            case "auto": {
                int sqLevelSum = 0;
                for (Player member : members) {
                    sqLevelSum += (member.getLevel() * member.getLevel());
                }
                int i = members.size() - 1;
                if (i < 1) {
                    return members;
                }
                if (i >= BONUS_EXP_SP.length) {
                    i = BONUS_EXP_SP.length - 1;
                }
                for (Player member : members) {
                    final int sqLevel = member.getLevel() * member.getLevel();
                    if (sqLevel >= (sqLevelSum / (members.size() * members.size()))) {
                        validMembers.add(member);
                    }
                }
                break;
            }
            case "highfive": {
                validMembers.addAll(members);
                break;
            }
            case "none": {
                validMembers.addAll(members);
                break;
            }
        }
        return validMembers;
    }

    private double getBaseExpSpBonus(int membersCount) {
        int i = membersCount - 1;
        if (i < 1) {
            return 1;
        }
        if (i >= BONUS_EXP_SP.length) {
            i = BONUS_EXP_SP.length - 1;
        }

        return BONUS_EXP_SP[i];
    }

    private double getExpBonus(int membersCount, Instance instance) {
        final float rateMul = instance != null ? instance.getExpPartyRate() : Config.RATE_PARTY_XP;
        return (membersCount < 2) ? (getBaseExpSpBonus(membersCount)) : (getBaseExpSpBonus(membersCount) * rateMul);
    }

    private double getSpBonus(int membersCount, Instance instance) {
        final float rateMul = instance != null ? instance.getSPPartyRate() : Config.RATE_PARTY_SP;
        return (membersCount < 2) ? (getBaseExpSpBonus(membersCount)) : (getBaseExpSpBonus(membersCount) * rateMul);
    }

    @Override
    public int getLevel() {
        return partyLvl;
    }

    public PartyDistributionType getDistributionType() {
        return distributionType;
    }

    public boolean isInCommandChannel() {
        return commandChannel != null;
    }

    public CommandChannel getCommandChannel() {
        return commandChannel;
    }

    public void setCommandChannel(CommandChannel channel) {
        commandChannel = channel;
    }

    /**
     * @return the leader of this party
     */
    @Override
    public Player getLeader() {
        return members.get(0);
    }

    @Override
    public void setLeader(Player player) {
        if ((player != null) && !player.isInDuel()) {
            if (members.contains(player)) {
                if (isLeader(player)) {
                    player.sendPacket(SystemMessageId.SLOW_DOWN_YOU_ARE_ALREADY_THE_PARTY_LEADER);
                } else {
                    // Swap party members
                    final Player temp = getLeader();
                    final int p1 = members.indexOf(player);
                    members.set(0, player);
                    members.set(p1, temp);

                    SystemMessage msg = getSystemMessage(SystemMessageId.C1_HAS_BECOME_THE_PARTY_LEADER);
                    msg.addString(getLeader().getName());
                    broadcastPacket(msg);
                    broadcastToPartyMembersNewLeader();
                    if (isInCommandChannel() && commandChannel.isLeader(temp)) {
                        commandChannel.setLeader(getLeader());
                        msg = getSystemMessage(SystemMessageId.COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_C1);
                        msg.addString(commandChannel.getLeader().getName());
                        commandChannel.broadcastPacket(msg);
                    }
                }
            } else {
                player.sendPacket(SystemMessageId.YOU_MAY_ONLY_TRANSFER_PARTY_LEADERSHIP_TO_ANOTHER_MEMBER_OF_THE_PARTY);
            }
        }
    }

    public synchronized void requestLootChange(PartyDistributionType partyDistributionType) {
        if (changeRequestDistributionType != null) {
            return;
        }
        changeRequestDistributionType = partyDistributionType;
        _changeDistributionTypeAnswers = new HashSet<>();
        changeDistributionTypeRequestTask = ThreadPool.schedule(() -> finishLootRequest(false), PARTY_DISTRIBUTION_TYPE_REQUEST_TIMEOUT.toMillis());

        broadcastToPartyMembers(getLeader(), new ExAskModifyPartyLooting(getLeader().getName(), partyDistributionType));

        final SystemMessage sm = getSystemMessage(SystemMessageId.REQUESTING_APPROVAL_FOR_CHANGING_PARTY_LOOT_TO_S1);
        sm.addSystemString(partyDistributionType.getSysStringId());
        getLeader().sendPacket(sm);
    }

    public synchronized void answerLootChangeRequest(Player member, boolean answer) {
        if (changeRequestDistributionType == null) {
            return;
        }

        if (_changeDistributionTypeAnswers.contains(member.getObjectId())) {
            return;
        }

        if (!answer) {
            finishLootRequest(false);
            return;
        }

        _changeDistributionTypeAnswers.add(member.getObjectId());
        if (_changeDistributionTypeAnswers.size() >= (getMemberCount() - 1)) {
            finishLootRequest(true);
        }
    }

    protected synchronized void finishLootRequest(boolean success) {
        if (changeRequestDistributionType == null) {
            return;
        }
        if (changeDistributionTypeRequestTask != null) {
            changeDistributionTypeRequestTask.cancel(false);
            changeDistributionTypeRequestTask = null;
        }
        if (success) {
            broadcastPacket(new ExSetPartyLooting(1, changeRequestDistributionType));
            distributionType = changeRequestDistributionType;
            final SystemMessage sm = getSystemMessage(SystemMessageId.PARTY_LOOT_WAS_CHANGED_TO_S1);
            sm.addSystemString(changeRequestDistributionType.getSysStringId());
            broadcastPacket(sm);
        } else {
            broadcastPacket(new ExSetPartyLooting(0, distributionType));
            broadcastPacket(getSystemMessage(SystemMessageId.PARTY_LOOT_CHANGE_WAS_CANCELLED));
        }
        changeRequestDistributionType = null;
        _changeDistributionTypeAnswers = null;
    }

    /**
     * @return a list of all members of this party
     */
    @Override
    public List<Player> getMembers() {
        return members;
    }

    public boolean contains(Player player) {
        return members.contains(player);
    }

    /**
     * The message type send to the party members.
     */
    public enum MessageType {
        EXPELLED,
        LEFT,
        NONE,
        DISCONNECTED
    }
}
