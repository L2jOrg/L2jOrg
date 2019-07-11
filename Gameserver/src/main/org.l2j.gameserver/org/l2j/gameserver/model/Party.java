package org.l2j.gameserver.model;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.GameTimeController;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.instancemanager.PcCafePointsManager;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * This class serves as a container for player parties.
 *
 * @author nuocnam
 */
public class Party extends AbstractPlayerGroup {
    private static final Logger LOGGER = LoggerFactory.getLogger(Party.class);

    // @formatter:off
    private static final double[] BONUS_EXP_SP =
            {
                    1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 2.0
            };
    // @formatter:on

    private static final Duration PARTY_POSITION_BROADCAST_INTERVAL = Duration.ofSeconds(12);
    private static final Duration PARTY_DISTRIBUTION_TYPE_REQUEST_TIMEOUT = Duration.ofSeconds(15);
    private static final int[] TACTICAL_SYS_STRINGS =
            {
                    0,
                    2664,
                    2665,
                    2666,
                    2667
            };
    private static Map<Integer, Creature> _tacticalSigns = null;
    private final List<Player> _members = new CopyOnWriteArrayList<>();
    protected PartyMemberPosition _positionPacket;
    private boolean _pendingInvitation = false;
    private long _pendingInviteTimeout;
    private int _partyLvl = 0;
    private volatile PartyDistributionType _distributionType = PartyDistributionType.FINDERS_KEEPERS;
    private volatile PartyDistributionType _changeRequestDistributionType;
    private volatile Future<?> _changeDistributionTypeRequestTask = null;
    private volatile Set<Integer> _changeDistributionTypeAnswers = null;
    private int _itemLastLoot = 0;
    private CommandChannel _commandChannel = null;
    private Future<?> _positionBroadcastTask = null;
    private boolean _disbanding = false;

    /**
     * Construct a new Party object with a single member - the leader.
     *
     * @param leader                the leader of this party
     * @param partyDistributionType the item distribution rule of this party
     */
    public Party(Player leader, PartyDistributionType partyDistributionType) {
        _members.add(leader);
        _partyLvl = leader.getLevel();
        _distributionType = partyDistributionType;
        L2World.getInstance().incrementParty();
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
        _pendingInviteTimeout = GameTimeController.getInstance().getGameTicks() + (Player.REQUEST_TIMEOUT * GameTimeController.TICKS_PER_SECOND);
    }

    /**
     * Check if a player invitation request is expired.
     *
     * @return {@code true} if time is expired, {@code false} otherwise
     * @see Player#isRequestExpired()
     */
    public boolean isInvitationRequestExpired() {
        return (_pendingInviteTimeout <= GameTimeController.getInstance().getGameTicks());
    }

    /**
     * Get a random member from this party.
     *
     * @param itemId the ID of the item for which the member must have inventory space
     * @param target the object of which the member must be within a certain range (must not be null)
     * @return a random member from this party or {@code null} if none of the members have inventory space for the specified item
     */
    private Player getCheckedRandomMember(int itemId, Creature target) {
        final List<Player> availableMembers = new ArrayList<>();
        for (Player member : _members) {
            if (member.getInventory().validateCapacityByItemId(itemId) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true)) {
                availableMembers.add(member);
            }
        }
        return !availableMembers.isEmpty() ? availableMembers.get(Rnd.get(availableMembers.size())) : null;
    }

    /**
     * get next item looter
     *
     * @param ItemId
     * @param target
     * @return
     */
    private Player getCheckedNextLooter(int ItemId, Creature target) {
        for (int i = 0; i < getMemberCount(); i++) {
            if (++_itemLastLoot >= getMemberCount()) {
                _itemLastLoot = 0;
            }
            Player member;
            try {
                member = _members.get(_itemLastLoot);
                if (member.getInventory().validateCapacityByItemId(ItemId) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true)) {
                    return member;
                }
            } catch (Exception e) {
                // continue, take another member if this just logged off
            }
        }

        return null;
    }

    /**
     * get next item looter
     *
     * @param player
     * @param ItemId
     * @param spoil
     * @param target
     * @return
     */
    private Player getActualLooter(Player player, int ItemId, boolean spoil, Creature target) {
        Player looter = null;

        switch (_distributionType) {
            case RANDOM: {
                if (!spoil) {
                    looter = getCheckedRandomMember(ItemId, target);
                }
                break;
            }
            case RANDOM_INCLUDING_SPOIL: {
                looter = getCheckedRandomMember(ItemId, target);
                break;
            }
            case BY_TURN: {
                if (!spoil) {
                    looter = getCheckedNextLooter(ItemId, target);
                }
                break;
            }
            case BY_TURN_INCLUDING_SPOIL: {
                looter = getCheckedNextLooter(ItemId, target);
                break;
            }
        }

        return looter != null ? looter : player;
    }

    /**
     * Broadcasts UI update and User Info for new party leader.
     */
    public void broadcastToPartyMembersNewLeader() {
        for (Player member : _members) {
            if (member != null) {
                member.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
                member.sendPacket(new PartySmallWindowAll(member, this));
                member.broadcastUserInfo();
            }
        }
    }

    /**
     * Send a Server->Client packet to all other Player of the Party.<BR>
     * <BR>
     *
     * @param player
     * @param msg
     */
    public void broadcastToPartyMembers(Player player, ServerPacket msg) {
        for (Player member : _members) {
            if ((member != null) && (member.getObjectId() != player.getObjectId())) {
                member.sendPacket(msg);
            }
        }
    }

    /**
     * adds new member to party
     *
     * @param player
     */
    public void addPartyMember(Player player) {
        if (_members.contains(player)) {
            return;
        }

        if (_changeRequestDistributionType != null) {
            finishLootRequest(false); // cancel on invite
        }

        // add player to party
        _members.add(player);

        // sends new member party window for all members
        // we do all actions before adding member to a list, this speeds things up a little
        player.sendPacket(new PartySmallWindowAll(player, this));

        // sends pets/summons of party members
        for (Player pMember : _members) {
            if (pMember != null) {
                final Summon pet = pMember.getPet();
                if (pet != null) {
                    player.sendPacket(new ExPartyPetWindowAdd(pet));
                }
                pMember.getServitors().values().forEach(s -> player.sendPacket(new ExPartyPetWindowAdd(s)));
            }
        }

        SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_JOINED_S1_S_PARTY);
        msg.addString(getLeader().getName());
        player.sendPacket(msg);

        msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_JOINED_THE_PARTY);
        msg.addString(player.getName());
        broadcastPacket(msg);

        _members.stream().filter(member -> member != player).forEach(member -> member.sendPacket(new PartySmallWindowAdd(player, this)));

        // send the position of all party members to the new party member
        // player.sendPacket(new PartyMemberPosition(this));
        // send the position of the new party member to all party members (except the new one - he knows his own position)
        // broadcastToPartyMembers(player, new PartyMemberPosition(this));

        // if member has pet/summon add it to other as well
        final Summon pet = player.getPet();
        if (pet != null) {
            broadcastPacket(new ExPartyPetWindowAdd(pet));
        }

        player.getServitors().values().forEach(s -> broadcastPacket(new ExPartyPetWindowAdd(s)));

        // adjust party level
        if (player.getLevel() > _partyLvl) {
            _partyLvl = player.getLevel();
        }

        // update partySpelled
        Summon summon;
        for (Player member : _members) {
            if (member != null) {
                member.updateEffectIcons(true); // update party icons only
                summon = member.getPet();
                member.broadcastUserInfo();
                if (summon != null) {
                    summon.updateEffectIcons();
                }
                member.getServitors().values().forEach(Summon::updateEffectIcons);
            }
        }

        // open the CCInformationwindow
        if (isInCommandChannel()) {
            player.sendPacket(ExOpenMPCC.STATIC_PACKET);
        }

        if (_positionBroadcastTask == null) {
            _positionBroadcastTask = ThreadPoolManager.scheduleAtFixedRate(() ->
            {
                if (_positionPacket == null) {
                    _positionPacket = new PartyMemberPosition(this);
                } else {
                    _positionPacket.reuse(this);
                }
                broadcastPacket(_positionPacket);
            }, PARTY_POSITION_BROADCAST_INTERVAL.toMillis() / 2, PARTY_POSITION_BROADCAST_INTERVAL.toMillis());
        }
        applyTacticalSigns(player, false);
        L2World.getInstance().incrementPartyMember();
    }

    private Map<Integer, Creature> getTacticalSigns() {
        if (_tacticalSigns == null) {
            synchronized (this) {
                if (_tacticalSigns == null) {
                    _tacticalSigns = new ConcurrentHashMap<>(1);
                }
            }
        }
        return _tacticalSigns;
    }

    public void applyTacticalSigns(Player player, boolean remove) {
        if (_tacticalSigns == null) {
            return;
        }

        _tacticalSigns.forEach((key, value) -> player.sendPacket(new ExTacticalSign(value, remove ? 0 : key)));
    }

    public void addTacticalSign(Player activeChar, int tacticalSignId, Creature target) {
        final Creature tacticalTarget = getTacticalSigns().get(tacticalSignId);

        if (tacticalTarget == null) {
            // if the new sign is applied to an existing target, remove the old sign from map
            _tacticalSigns.values().remove(target);

            // Add the new sign
            _tacticalSigns.put(tacticalSignId, target);

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_USED_S3_ON_C2);
            sm.addPcName(activeChar);
            sm.addString(target.getName());
            sm.addSystemString(TACTICAL_SYS_STRINGS[tacticalSignId]);

            _members.forEach(m ->
            {
                m.sendPacket(new ExTacticalSign(target, tacticalSignId));
                m.sendPacket(sm);
            });
        } else if (tacticalTarget == target) {
            // Sign already assigned
            // If the sign is applied on the same target, remove it
            _tacticalSigns.remove(tacticalSignId);
            _members.forEach(m -> m.sendPacket(new ExTacticalSign(tacticalTarget, 0)));
        } else {
            // Otherwise, delete the old sign, and apply it to the new target
            _tacticalSigns.replace(tacticalSignId, target);

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_USED_S3_ON_C2);
            sm.addPcName(activeChar);
            sm.addString(target.getName());
            sm.addSystemString(TACTICAL_SYS_STRINGS[tacticalSignId]);

            _members.forEach(m ->
            {
                m.sendPacket(new ExTacticalSign(tacticalTarget, 0));
                m.sendPacket(new ExTacticalSign(target, tacticalSignId));
                m.sendPacket(sm);
            });
        }
    }

    public void setTargetBasedOnTacticalSignId(Player player, int tacticalSignId) {
        if (_tacticalSigns == null) {
            return;
        }

        final Creature tacticalTarget = _tacticalSigns.get(tacticalSignId);
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
        if (_members.contains(player)) {
            final boolean isLeader = isLeader(player);
            if (!_disbanding) {
                if ((_members.size() == 2) || (isLeader && !Config.ALT_LEAVE_PARTY_LEADER && (type != MessageType.DISCONNECTED))) {
                    disbandParty();
                    return;
                }
            }

            _members.remove(player);
            recalculatePartyLevel();

            if (player.isInDuel()) {
                DuelManager.getInstance().onRemoveFromParty(player);
            }

            try {
                // Channeling a player!
                if (player.isChanneling() && (player.getSkillChannelizer().hasChannelized())) {
                    player.abortCast();
                } else if (player.isChannelized()) {
                    player.getSkillChannelized().abortChannelization();
                }
            } catch (Exception e) {
                LOGGER.warn("", e);
            }

            SystemMessage msg;
            if (type == MessageType.EXPELLED) {
                player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY);
                msg = SystemMessage.getSystemMessage(SystemMessageId.C1_WAS_EXPELLED_FROM_THE_PARTY);
                msg.addString(player.getName());
                broadcastPacket(msg);
            } else if ((type == MessageType.LEFT) || (type == MessageType.DISCONNECTED)) {
                player.sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY);
                msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_LEFT_THE_PARTY);
                msg.addString(player.getName());
                broadcastPacket(msg);
            }

            L2World.getInstance().decrementPartyMember();

            // UI update.
            player.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
            player.setParty(null);
            broadcastPacket(new PartySmallWindowDelete(player));
            final Summon pet = player.getPet();
            if (pet != null) {
                broadcastPacket(new ExPartyPetWindowDelete(pet));
            }
            player.getServitors().values().forEach(s -> player.sendPacket(new ExPartyPetWindowDelete(s)));

            // Close the CCInfoWindow
            if (isInCommandChannel()) {
                player.sendPacket(ExCloseMPCC.STATIC_PACKET);
            }
            if (isLeader && (_members.size() > 1) && (Config.ALT_LEAVE_PARTY_LEADER || (type == MessageType.DISCONNECTED))) {
                msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BECOME_THE_PARTY_LEADER);
                msg.addString(getLeader().getName());
                broadcastPacket(msg);
                broadcastToPartyMembersNewLeader();
            } else if (_members.size() == 1) {
                if (isInCommandChannel()) {
                    // delete the whole command channel when the party who opened the channel is disbanded
                    if (_commandChannel.getLeader().getObjectId() == getLeader().getObjectId()) {
                        _commandChannel.disbandChannel();
                    } else {
                        _commandChannel.removeParty(this);
                    }
                }

                if (getLeader() != null) {
                    getLeader().setParty(null);
                    if (getLeader().isInDuel()) {
                        DuelManager.getInstance().onRemoveFromParty(getLeader());
                    }
                }
                if (_changeDistributionTypeRequestTask != null) {
                    _changeDistributionTypeRequestTask.cancel(true);
                    _changeDistributionTypeRequestTask = null;
                }
                if (_positionBroadcastTask != null) {
                    _positionBroadcastTask.cancel(false);
                    _positionBroadcastTask = null;
                }
                _members.clear();
            }
            applyTacticalSigns(player, true);
        }
    }

    /**
     * Disperse a party and send a message to all its members.
     */
    public void disbandParty() {
        _disbanding = true;
        if (_members != null) {
            broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_PARTY_HAS_DISPERSED));
            for (Player member : _members) {
                if (member != null) {
                    removePartyMember(member, MessageType.NONE);
                }
            }
        }
        L2World.getInstance().decrementParty();
    }

    /**
     * Change party leader (used for string arguments)
     *
     * @param name the name of the player to set as the new party leader
     */
    public void changePartyLeader(String name) {
        setLeader(getPlayerByName(name));
    }

    /**
     * finds a player in the party by name
     *
     * @param name
     * @return
     */
    private Player getPlayerByName(String name) {
        for (Player member : _members) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }

    /**
     * distribute item(s) to party members
     *
     * @param player
     * @param item
     */
    public void distributeItem(Player player, Item item) {
        if (item.getId() == CommonItem.ADENA) {
            distributeAdena(player, item.getCount(), player);
            ItemTable.getInstance().destroyItem("Party", item, player, null);
            return;
        }

        final Player target = getActualLooter(player, item.getId(), false, player);
        target.addItem("Party", item, player, true);

        // Send messages to other party members about reward
        if (item.getCount() > 1) {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2);
            msg.addString(target.getName());
            msg.addItemName(item);
            msg.addLong(item.getCount());
            broadcastToPartyMembers(target, msg);
        } else {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2);
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
            final SystemMessage msg = spoil ? SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2_S_BY_USING_SWEEPER) : SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2);
            msg.addString(looter.getName());
            msg.addItemName(itemId);
            msg.addLong(itemCount);
            broadcastToPartyMembers(looter, msg);
        } else {
            final SystemMessage msg = spoil ? SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2_BY_USING_SWEEPER) : SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2);
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

    /**
     * distribute adena to party members
     *
     * @param player
     * @param adena
     * @param target
     */
    public void distributeAdena(Player player, long adena, Creature target) {
        // Check the number of party members that must be rewarded
        // (The party member must be in range to receive its reward)
        final List<Player> toReward = new LinkedList<>();
        for (Player member : _members) {
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
                double exp = member.getStat().getValue(Stats.EXPSP_RATE, xpReward * preCalculation);
                double sp = member.getStat().getValue(Stats.EXPSP_RATE, spReward * preCalculation);

                exp = calculateExpSpPartyCutoff(member.getActingPlayer(), topLvl, exp, sp, target.useVitalityRate());
                if (exp > 0) {
                    final Clan clan = member.getClan();
                    if (clan != null) {
                        double finalExp = exp;
                        if (target.useVitalityRate()) {
                            finalExp *= member.getStat().getExpBonusMultiplier();
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
        for (Player member : _members) {
            if (member == null) {
                _members.remove(member);
                continue;
            }

            if (member.getLevel() > newLevel) {
                newLevel = member.getLevel();
            }
        }
        _partyLvl = newLevel;
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
        return _partyLvl;
    }

    public PartyDistributionType getDistributionType() {
        return _distributionType;
    }

    public boolean isInCommandChannel() {
        return _commandChannel != null;
    }

    public CommandChannel getCommandChannel() {
        return _commandChannel;
    }

    public void setCommandChannel(CommandChannel channel) {
        _commandChannel = channel;
    }

    /**
     * @return the leader of this party
     */
    @Override
    public Player getLeader() {
        return _members.get(0);
    }

    @Override
    public void setLeader(Player player) {
        if ((player != null) && !player.isInDuel()) {
            if (_members.contains(player)) {
                if (isLeader(player)) {
                    player.sendPacket(SystemMessageId.SLOW_DOWN_YOU_ARE_ALREADY_THE_PARTY_LEADER);
                } else {
                    // Swap party members
                    final Player temp = getLeader();
                    final int p1 = _members.indexOf(player);
                    _members.set(0, player);
                    _members.set(p1, temp);

                    SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BECOME_THE_PARTY_LEADER);
                    msg.addString(getLeader().getName());
                    broadcastPacket(msg);
                    broadcastToPartyMembersNewLeader();
                    if (isInCommandChannel() && _commandChannel.isLeader(temp)) {
                        _commandChannel.setLeader(getLeader());
                        msg = SystemMessage.getSystemMessage(SystemMessageId.COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_C1);
                        msg.addString(_commandChannel.getLeader().getName());
                        _commandChannel.broadcastPacket(msg);
                    }
                }
            } else {
                player.sendPacket(SystemMessageId.YOU_MAY_ONLY_TRANSFER_PARTY_LEADERSHIP_TO_ANOTHER_MEMBER_OF_THE_PARTY);
            }
        }
    }

    public synchronized void requestLootChange(PartyDistributionType partyDistributionType) {
        if (_changeRequestDistributionType != null) {
            return;
        }
        _changeRequestDistributionType = partyDistributionType;
        _changeDistributionTypeAnswers = new HashSet<>();
        _changeDistributionTypeRequestTask = ThreadPoolManager.getInstance().schedule(() -> finishLootRequest(false), PARTY_DISTRIBUTION_TYPE_REQUEST_TIMEOUT.toMillis());

        broadcastToPartyMembers(getLeader(), new ExAskModifyPartyLooting(getLeader().getName(), partyDistributionType));

        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.REQUESTING_APPROVAL_FOR_CHANGING_PARTY_LOOT_TO_S1);
        sm.addSystemString(partyDistributionType.getSysStringId());
        getLeader().sendPacket(sm);
    }

    public synchronized void answerLootChangeRequest(Player member, boolean answer) {
        if (_changeRequestDistributionType == null) {
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
        if (_changeRequestDistributionType == null) {
            return;
        }
        if (_changeDistributionTypeRequestTask != null) {
            _changeDistributionTypeRequestTask.cancel(false);
            _changeDistributionTypeRequestTask = null;
        }
        if (success) {
            broadcastPacket(new ExSetPartyLooting(1, _changeRequestDistributionType));
            _distributionType = _changeRequestDistributionType;
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_LOOT_WAS_CHANGED_TO_S1);
            sm.addSystemString(_changeRequestDistributionType.getSysStringId());
            broadcastPacket(sm);
        } else {
            broadcastPacket(new ExSetPartyLooting(0, _distributionType));
            broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_LOOT_CHANGE_WAS_CANCELLED));
        }
        _changeRequestDistributionType = null;
        _changeDistributionTypeAnswers = null;
    }

    /**
     * @return a list of all members of this party
     */
    @Override
    public List<Player> getMembers() {
        return _members;
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
