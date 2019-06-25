package org.l2j.gameserver.model.actor;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.L2AttackableAI;
import org.l2j.gameserver.ai.L2CharacterAI;
import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.data.xml.impl.ExtendDropData;
import org.l2j.gameserver.datatables.EventDroplist;
import org.l2j.gameserver.datatables.EventDroplist.DateDrop;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.DropType;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.instancemanager.PcCafePointsManager;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import org.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2ServitorInstance;
import org.l2j.gameserver.model.actor.status.AttackableStatus;
import org.l2j.gameserver.model.actor.tasks.attackable.CommandChannelTimer;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAggroRangeEnter;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.ExMagicAttackInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.util.GameUtils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class L2Attackable extends L2Npc {
    private final AtomicReference<ItemHolder> _harvestItem = new AtomicReference<>();
    private final AtomicReference<Collection<ItemHolder>> _sweepItems = new AtomicReference<>();
    // Raid
    private boolean _isRaid = false;
    private boolean _isRaidMinion = false;
    //
    private boolean _champion = false;
    private volatile Map<L2Character, AggroInfo> _aggroList = new ConcurrentHashMap<>();
    private boolean _isReturningToSpawnPoint = false;
    private boolean _canReturnToSpawnPoint = true;
    private boolean _seeThroughSilentMove = false;
    // Manor
    private boolean _seeded = false;
    private L2Seed _seed = null;
    private int _seederObjId = 0;
    // Spoil
    private int _spoilerObjectId;
    private boolean _plundered = false;
    // Over-hit
    private boolean _overhit;
    private double _overhitDamage;
    private L2Character _overhitAttacker;
    // Command channel
    private volatile L2CommandChannel _firstCommandChannelAttacked = null;
    private CommandChannelTimer _commandChannelTimer = null;
    private long _commandChannelLastAttack = 0;
    // Misc
    private boolean _mustGiveExpSp;

    /**
     * Constructor of L2Attackable (use L2Character and L2NpcInstance constructor).<br>
     * Actions:<br>
     * Call the L2Character constructor to set the _template of the L2Attackable (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)<br>
     * Set the name of the L2Attackable<br>
     * Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it.
     *
     * @param template the template to apply to the NPC.
     */
    public L2Attackable(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2Attackable);
        setIsInvul(false);
        _mustGiveExpSp = true;
    }

    @Override
    public AttackableStatus getStatus() {
        return (AttackableStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new AttackableStatus(this));
    }

    @Override
    protected L2CharacterAI initAI() {
        return new L2AttackableAI(this);
    }

    public final Map<L2Character, AggroInfo> getAggroList() {
        return _aggroList;
    }

    public final boolean isReturningToSpawnPoint() {
        return _isReturningToSpawnPoint;
    }

    public final void setisReturningToSpawnPoint(boolean value) {
        _isReturningToSpawnPoint = value;
    }

    public final boolean canReturnToSpawnPoint() {
        return _canReturnToSpawnPoint;
    }

    public final void setCanReturnToSpawnPoint(boolean value) {
        _canReturnToSpawnPoint = value;
    }

    public boolean canSeeThroughSilentMove() {
        return _seeThroughSilentMove;
    }

    public void setSeeThroughSilentMove(boolean val) {
        _seeThroughSilentMove = val;
    }

    /**
     * Use the skill if minimum checks are pass.
     *
     * @param skill the skill
     */
    public void useMagic(Skill skill) {
        if (!SkillCaster.checkUseConditions(this, skill)) {
            return;
        }

        final L2Object target = skill.getTarget(this, false, false, false);
        if (target != null) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
        }
    }

    /**
     * Reduce the current HP of the L2Attackable, update its _aggroList and launch the doDie Task if necessary.
     *
     * @param attacker The L2Character who attacks
     * @param isDOT
     * @param skill
     */
    @Override
    public void reduceCurrentHp(double value, L2Character attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect) {
        if (_isRaid && !isMinion() && (attacker != null) && (attacker.getParty() != null) && attacker.getParty().isInCommandChannel() && attacker.getParty().getCommandChannel().meetRaidWarCondition(this)) {
            if (_firstCommandChannelAttacked == null) // looting right isn't set
            {
                synchronized (this) {
                    if (_firstCommandChannelAttacked == null) {
                        _firstCommandChannelAttacked = attacker.getParty().getCommandChannel();
                        if (_firstCommandChannelAttacked != null) {
                            _commandChannelTimer = new CommandChannelTimer(this);
                            _commandChannelLastAttack = System.currentTimeMillis();
                            ThreadPoolManager.schedule(_commandChannelTimer, 10000); // check for last attack
                            _firstCommandChannelAttacked.broadcastPacket(new CreatureSay(0, ChatType.PARTYROOM_ALL, "", "You have looting rights!")); // TODO: retail msg
                        }
                    }
                }
            } else if (attacker.getParty().getCommandChannel().equals(_firstCommandChannelAttacked)) // is in same channel
            {
                _commandChannelLastAttack = System.currentTimeMillis(); // update last attack time
            }
        }

        // Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList
        if (attacker != null) {
            addDamage(attacker, (int) value, skill);

            // Check Raidboss attack. Character will be petrified if attacking a raid that's more than 8 levels lower. In retail you deal damage to raid before curse.
            if (_isRaid && giveRaidCurse() && !Config.RAID_DISABLE_CURSE) {
                if (attacker.getLevel() > (getLevel() + 8)) {
                    final Skill raidCurse = CommonSkill.RAID_CURSE2.getSkill();
                    if (raidCurse != null) {
                        raidCurse.applyEffects(this, attacker);
                    }
                }
            }
        }

        // If this L2Attackable is a L2MonsterInstance and it has spawned minions, call its minions to battle
        if (isMonster()) {
            L2MonsterInstance master = (L2MonsterInstance) this;

            if (master.hasMinions()) {
                master.getMinionList().onAssist(this, attacker);
            }

            master = master.getLeader();
            if ((master != null) && master.hasMinions()) {
                master.getMinionList().onAssist(this, attacker);
            }
        }
        // Reduce the current HP of the L2Attackable and launch the doDie Task if necessary
        super.reduceCurrentHp(value, attacker, skill, isDOT, directlyToHp, critical, reflect);
    }

    public synchronized void setMustRewardExpSp(boolean value) {
        _mustGiveExpSp = value;
    }

    public synchronized boolean getMustRewardExpSP() {
        return _mustGiveExpSp;
    }

    /**
     * Kill the L2Attackable (the corpse disappeared after 7 seconds), distribute rewards (EXP, SP, Drops...) and notify Quest Engine.<br>
     * Actions:<br>
     * Distribute Exp and SP rewards to L2PcInstance (including Summon owner) that hit the L2Attackable and to their Party members<br>
     * Notify the Quest Engine of the L2Attackable death if necessary.<br>
     * Kill the L2NpcInstance (the corpse disappeared after 7 seconds)<br>
     * Caution: This method DOESN'T GIVE rewards to L2PetInstance.
     *
     * @param killer The L2Character that has killed the L2Attackable
     */
    @Override
    public boolean doDie(L2Character killer) {
        // Kill the L2NpcInstance (the corpse disappeared after 7 seconds)
        if (!super.doDie(killer)) {
            return false;
        }

        if ((killer != null) && killer.isPlayable()) {
            // Delayed notification
            EventDispatcher.getInstance().notifyEventAsync(new OnAttackableKill(killer.getActingPlayer(), this, killer.isSummon()), this);
        }

        // Notify to minions if there are.
        if (isMonster()) {
            final L2MonsterInstance mob = (L2MonsterInstance) this;
            if ((mob.getLeader() != null) && mob.getLeader().hasMinions()) {
                final int respawnTime = Config.MINIONS_RESPAWN_TIME.containsKey(getId()) ? Config.MINIONS_RESPAWN_TIME.get(getId()) * 1000 : -1;
                mob.getLeader().getMinionList().onMinionDie(mob, respawnTime);
            }

            if (mob.hasMinions()) {
                mob.getMinionList().onMasterDie(false);
            }
        }

        return true;
    }

    /**
     * Distribute Exp and SP rewards to L2PcInstance (including Summon owner) that hit the L2Attackable and to their Party members.<br>
     * Actions:<br>
     * Get the L2PcInstance owner of the L2ServitorInstance (if necessary) and L2Party in progress.<br>
     * Calculate the Experience and SP rewards in function of the level difference.<br>
     * Add Exp and SP rewards to L2PcInstance (including Summon penalty) and to Party members in the known area of the last attacker.<br>
     * Caution : This method DOESN'T GIVE rewards to L2PetInstance.
     *
     * @param lastAttacker The L2Character that has killed the L2Attackable
     */
    @Override
    protected void calculateRewards(L2Character lastAttacker) {
        try {
            if (_aggroList.isEmpty()) {
                return;
            }

            // NOTE: Concurrent-safe map is used because while iterating to verify all conditions sometimes an entry must be removed.
            final Map<L2PcInstance, DamageDoneInfo> rewards = new ConcurrentHashMap<>();

            L2PcInstance maxDealer = null;
            long maxDamage = 0;
            long totalDamage = 0;
            // While Iterating over This Map Removing Object is Not Allowed
            // Go through the _aggroList of the L2Attackable
            for (AggroInfo info : _aggroList.values()) {
                // Get the L2Character corresponding to this attacker
                final L2PcInstance attacker = info.getAttacker().getActingPlayer();
                if (attacker != null) {
                    // Get damages done by this attacker
                    final long damage = info.getDamage();

                    // Prevent unwanted behavior
                    if (damage > 1) {
                        // Check if damage dealer isn't too far from this (killed monster)
                        if (!GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, this, attacker, true)) {
                            continue;
                        }

                        totalDamage += damage;

                        // Calculate real damages (Summoners should get own damage plus summon's damage)
                        final DamageDoneInfo reward = rewards.computeIfAbsent(attacker, DamageDoneInfo::new);
                        reward.addDamage(damage);

                        if (reward.getDamage() > maxDamage) {
                            maxDealer = attacker;
                            maxDamage = reward.getDamage();
                        }
                    }
                }
            }

            // Calculate raidboss points
            if (_isRaid && !_isRaidMinion) {
                final L2PcInstance player = (maxDealer != null) && maxDealer.isOnline() ? maxDealer : lastAttacker.getActingPlayer();
                broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL));
                final int raidbossPoints = (int) (getTemplate().getRaidPoints() * Config.RATE_RAIDBOSS_POINTS);
                final L2Party party = player.getParty();

                if (party != null) {
                    final L2CommandChannel command = party.getCommandChannel();
                    //@formatter:off
                    final List<L2PcInstance> members = command != null ?
                            command.getMembers().stream().filter(p -> p.calculateDistance3D(this) < Config.ALT_PARTY_RANGE).collect(Collectors.toList()) :
                            player.getParty().getMembers().stream().filter(p -> p.calculateDistance3D(this) < Config.ALT_PARTY_RANGE).collect(Collectors.toList());
                    //@formatter:on

                    members.forEach(p ->
                    {
                        final int points = Math.max(raidbossPoints / members.size(), 1);
                        p.increaseRaidbossPoints(points);
                        p.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_RAID_POINT_S).addInt(points));

                        if (p.isNoble()) {
                            Hero.getInstance().setRBkilled(p.getObjectId(), getId());
                        }
                    });
                } else {
                    final int points = Math.max(raidbossPoints, 1);
                    player.increaseRaidbossPoints(points);
                    player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_RAID_POINT_S).addInt(points));
                    if (player.isNoble()) {
                        Hero.getInstance().setRBkilled(player.getObjectId(), getId());
                    }
                }
            }

            // Manage Base, Quests and Sweep drops of the L2Attackable
            doItemDrop((maxDealer != null) && maxDealer.isOnline() ? maxDealer : lastAttacker);

            // Manage drop of Special Events created by GM for a defined period
            doEventDrop(lastAttacker);

            if (!getMustRewardExpSP()) {
                return;
            }

            if (!rewards.isEmpty()) {
                for (DamageDoneInfo reward : rewards.values()) {
                    if (reward == null) {
                        continue;
                    }

                    // Attacker to be rewarded
                    final L2PcInstance attacker = reward.getAttacker();

                    // Total amount of damage done
                    final long damage = reward.getDamage();

                    // Get party
                    final L2Party attackerParty = attacker.getParty();

                    // Penalty applied to the attacker's XP
                    // If this attacker have servitor, get Exp Penalty applied for the servitor.
                    float penalty = 1;

                    final Optional<L2Summon> summon = attacker.getServitors().values().stream().filter(s -> ((L2ServitorInstance) s).getExpMultiplier() > 1).findFirst();
                    if (summon.isPresent()) {
                        penalty = ((L2ServitorInstance) summon.get()).getExpMultiplier();

                    }

                    // If there's NO party in progress
                    if (attackerParty == null) {
                        // Calculate Exp and SP rewards
                        if (isInSurroundingRegion(attacker)) {
                            // Calculate the difference of level between this attacker (player or servitor owner) and the L2Attackable
                            // mob = 24, atk = 10, diff = -14 (full xp)
                            // mob = 24, atk = 28, diff = 4 (some xp)
                            // mob = 24, atk = 50, diff = 26 (no xp)
                            final double[] expSp = calculateExpAndSp(attacker.getLevel(), damage, totalDamage);
                            double exp = expSp[0];
                            double sp = expSp[1];

                            if (Config.CHAMPION_ENABLE && _champion) {
                                exp *= Config.CHAMPION_REWARDS_EXP_SP;
                                sp *= Config.CHAMPION_REWARDS_EXP_SP;
                            }

                            exp *= penalty;

                            // Check for an over-hit enabled strike
                            final L2Character overhitAttacker = _overhitAttacker;
                            if (_overhit && (overhitAttacker != null) && (overhitAttacker.getActingPlayer() != null) && (attacker == overhitAttacker.getActingPlayer())) {
                                attacker.sendPacket(SystemMessageId.OVER_HIT);
                                attacker.sendPacket(new ExMagicAttackInfo(overhitAttacker.getObjectId(), getObjectId(), ExMagicAttackInfo.OVERHIT));
                                exp += calculateOverhitExp(exp);
                            }

                            // Distribute the Exp and SP between the L2PcInstance and its L2Summon
                            if (!attacker.isDead()) {
                                exp = attacker.getStat().getValue(Stats.EXPSP_RATE, exp);
                                sp = attacker.getStat().getValue(Stats.EXPSP_RATE, sp);

                                attacker.addExpAndSp(exp, sp, useVitalityRate());
                                if (exp > 0) {
                                    final L2Clan clan = attacker.getClan();
                                    if (clan != null) {
                                        double finalExp = exp;
                                        if (useVitalityRate()) {
                                            finalExp *= attacker.getStat().getExpBonusMultiplier();
                                        }
                                        clan.addHuntingPoints(attacker, this, finalExp);
                                    }
                                    attacker.updateVitalityPoints(getVitalityPoints(attacker.getLevel(), exp, _isRaid), true, false);
                                    PcCafePointsManager.getInstance().givePcCafePoint(attacker, exp);
                                }

                                rewardAttributeExp(attacker, damage, totalDamage);
                            }
                        }
                    } else {
                        // share with party members
                        long partyDmg = 0;
                        double partyMul = 1;
                        int partyLvl = 0;

                        // Get all L2Character that can be rewarded in the party
                        final List<L2PcInstance> rewardedMembers = new ArrayList<>();
                        // Go through all L2PcInstance in the party
                        final List<L2PcInstance> groupMembers = attackerParty.isInCommandChannel() ? attackerParty.getCommandChannel().getMembers() : attackerParty.getMembers();
                        for (L2PcInstance partyPlayer : groupMembers) {
                            if ((partyPlayer == null) || partyPlayer.isDead()) {
                                continue;
                            }

                            // Get the RewardInfo of this L2PcInstance from L2Attackable rewards
                            final DamageDoneInfo reward2 = rewards.get(partyPlayer);

                            // If the L2PcInstance is in the L2Attackable rewards add its damages to party damages
                            if (reward2 != null) {
                                if (GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, this, partyPlayer, true)) {
                                    partyDmg += reward2.getDamage(); // Add L2PcInstance damages to party damages
                                    rewardedMembers.add(partyPlayer);

                                    if (partyPlayer.getLevel() > partyLvl) {
                                        if (attackerParty.isInCommandChannel()) {
                                            partyLvl = attackerParty.getCommandChannel().getLevel();
                                        } else {
                                            partyLvl = partyPlayer.getLevel();
                                        }
                                    }
                                }
                                rewards.remove(partyPlayer); // Remove the L2PcInstance from the L2Attackable rewards
                            } else if (GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, this, partyPlayer, true)) {
                                rewardedMembers.add(partyPlayer);
                                if (partyPlayer.getLevel() > partyLvl) {
                                    if (attackerParty.isInCommandChannel()) {
                                        partyLvl = attackerParty.getCommandChannel().getLevel();
                                    } else {
                                        partyLvl = partyPlayer.getLevel();
                                    }
                                }
                            }
                        }

                        // If the party didn't killed this L2Attackable alone
                        if (partyDmg < totalDamage) {
                            partyMul = ((double) partyDmg / totalDamage);
                        }

                        // Calculate Exp and SP rewards
                        final double[] expSp = calculateExpAndSp(partyLvl, partyDmg, totalDamage);
                        double exp = expSp[0];
                        double sp = expSp[1];

                        if (Config.CHAMPION_ENABLE && _champion) {
                            exp *= Config.CHAMPION_REWARDS_EXP_SP;
                            sp *= Config.CHAMPION_REWARDS_EXP_SP;
                        }

                        exp *= partyMul;
                        sp *= partyMul;

                        // Check for an over-hit enabled strike
                        // (When in party, the over-hit exp bonus is given to the whole party and splitted proportionally through the party members)
                        final L2Character overhitAttacker = _overhitAttacker;
                        if (_overhit && (overhitAttacker != null) && (overhitAttacker.getActingPlayer() != null) && (attacker == overhitAttacker.getActingPlayer())) {
                            attacker.sendPacket(SystemMessageId.OVER_HIT);
                            attacker.sendPacket(new ExMagicAttackInfo(overhitAttacker.getObjectId(), getObjectId(), ExMagicAttackInfo.OVERHIT));
                            exp += calculateOverhitExp(exp);
                        }

                        // Distribute Experience and SP rewards to L2PcInstance Party members in the known area of the last attacker
                        if (partyDmg > 0) {
                            attackerParty.distributeXpAndSp(exp, sp, rewardedMembers, partyLvl, partyDmg, this);

                            for (L2PcInstance rewardedMember : rewardedMembers) {
                                rewardAttributeExp(rewardedMember, damage, totalDamage);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private void rewardAttributeExp(L2PcInstance rewardedMember, long damage, long totalDamage) {
        if (rewardedMember.getActiveElementalSpiritType() > 0 && getAttributeExp() > 0 && getElementalType() != ElementalType.NONE) {
            var attributeExp = getAttributeExp() * damage / totalDamage;
            var spirit = rewardedMember.getElementalSpirit(getElementalType().getDominating());
            if (nonNull(spirit)) {
                spirit.addExperience(attributeExp);
            }
        }
    }

    @Override
    public void addAttackerToAttackByList(L2Character player) {
        if ((player == null) || (player == this) || getAttackByList().stream().anyMatch(o -> o.get() == player)) {
            return;
        }
        getAttackByList().add(new WeakReference<>(player));
    }

    /**
     * Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList.
     *
     * @param attacker The L2Character that gave damages to this L2Attackable
     * @param damage   The number of damages given by the attacker L2Character
     * @param skill
     */
    public void addDamage(L2Character attacker, int damage, Skill skill) {
        if (attacker == null) {
            return;
        }

        // Notify the L2Attackable AI with EVT_ATTACKED
        if (!isDead()) {
            try {
                // If monster is on walk - stop it
                if (isWalker() && !isCoreAIDisabled() && WalkingManager.getInstance().isOnWalk(this)) {
                    WalkingManager.getInstance().stopMoving(this, false, true);
                }

                getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);

                // Calculate the amount of hate this attackable receives from this attack.
                double hateValue = (damage * 100) / (getLevel() + 7);

                if (skill == null) {
                    hateValue *= attacker.getStat().getValue(Stats.HATE_ATTACK, 1);
                }

                addDamageHate(attacker, damage, (int) hateValue);

                final L2PcInstance player = attacker.getActingPlayer();
                if (player != null) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAttack(player, this, damage, skill, attacker.isSummon()), this);
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList.
     *
     * @param attacker The L2Character that gave damages to this L2Attackable
     * @param damage   The number of damages given by the attacker L2Character
     * @param aggro    The hate (=damage) given by the attacker L2Character
     */
    public void addDamageHate(L2Character attacker, int damage, int aggro) {
        if ((attacker == null) || (attacker == this)) {
            return;
        }

        L2PcInstance targetPlayer = attacker.getActingPlayer();
        final L2Character summoner = attacker.getSummoner();
        if (attacker.isNpc() && (summoner != null) && summoner.isPlayer() && !attacker.isTargetable()) {
            targetPlayer = summoner.getActingPlayer();
            attacker = summoner;
        }

        // Get the AggroInfo of the attacker L2Character from the _aggroList of the L2Attackable
        final AggroInfo ai = _aggroList.computeIfAbsent(attacker, AggroInfo::new);
        ai.addDamage(damage);

        // traps does not cause aggro
        // making this hack because not possible to determine if damage made by trap
        // so just check for triggered trap here
        if ((targetPlayer == null) || (targetPlayer.getTrap() == null) || !targetPlayer.getTrap().isTriggered()) {
            ai.addHate(aggro);
        }

        if ((targetPlayer != null) && (aggro == 0)) {
            addDamageHate(attacker, 0, 1);

            // Set the intention to the L2Attackable to AI_INTENTION_ACTIVE
            if (getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }

            // Notify to scripts
            EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAggroRangeEnter(this, targetPlayer, attacker.isSummon()), this);
        } else if ((targetPlayer == null) && (aggro == 0)) {
            aggro = 1;
            ai.addHate(1);
        }

        // Set the intention to the L2Attackable to AI_INTENTION_ACTIVE
        if ((aggro != 0) && (getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    public void reduceHate(L2Character target, int amount) {
        if (target == null) // whole aggrolist
        {
            final L2Character mostHated = getMostHated();
            if (mostHated == null) // makes target passive for a moment more
            {
                ((L2AttackableAI) getAI()).setGlobalAggro(-25);
                return;
            }

            for (AggroInfo ai : _aggroList.values()) {
                ai.addHate(amount);
            }

            amount = getHating(mostHated);
            if (amount >= 0) {
                ((L2AttackableAI) getAI()).setGlobalAggro(-25);
                clearAggroList();
                getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                setWalking();
            }
            return;
        }

        final AggroInfo ai = _aggroList.get(target);
        if (ai == null) {
            LOGGER.info("Target " + target + " not present in aggro list of " + this);
            return;
        }

        ai.addHate(amount);
        if ((ai.getHate() >= 0) && (getMostHated() == null)) {
            ((L2AttackableAI) getAI()).setGlobalAggro(-25);
            clearAggroList();
            getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            setWalking();
        }
    }

    /**
     * Clears _aggroList hate of the L2Character without removing from the list.
     *
     * @param target
     */
    public void stopHating(L2Character target) {
        if (target == null) {
            return;
        }

        final AggroInfo ai = _aggroList.get(target);
        if (ai != null) {
            ai.stopHate();
        }
    }

    /**
     * @return the most hated L2Character of the L2Attackable _aggroList.
     */
    public L2Character getMostHated() {
        if (_aggroList.isEmpty() || isAlikeDead()) {
            return null;
        }

        L2Character mostHated = null;
        int maxHate = 0;

        // While Interacting over This Map Removing Object is Not Allowed
        // Go through the aggroList of the L2Attackable
        for (AggroInfo ai : _aggroList.values()) {
            if (ai == null) {
                continue;
            }

            if (ai.checkHate(this) > maxHate) {
                mostHated = ai.getAttacker();
                maxHate = ai.getHate();
            }
        }

        return mostHated;
    }

    /**
     * @return the 2 most hated L2Character of the L2Attackable _aggroList.
     */
    public List<L2Character> get2MostHated() {
        if (_aggroList.isEmpty() || isAlikeDead()) {
            return null;
        }

        L2Character mostHated = null;
        L2Character secondMostHated = null;
        int maxHate = 0;
        final List<L2Character> result = new ArrayList<>();

        // While iterating over this map removing objects is not allowed
        // Go through the aggroList of the L2Attackable
        for (AggroInfo ai : _aggroList.values()) {
            if (ai.checkHate(this) > maxHate) {
                secondMostHated = mostHated;
                mostHated = ai.getAttacker();
                maxHate = ai.getHate();
            }
        }

        result.add(mostHated);

        final L2Character secondMostHatedFinal = secondMostHated;
        if (getAttackByList().stream().anyMatch(o -> o.get() == secondMostHatedFinal)) {
            result.add(secondMostHated);
        } else {
            result.add(null);
        }
        return result;
    }

    public List<L2Character> getHateList() {
        if (_aggroList.isEmpty() || isAlikeDead()) {
            return null;
        }

        final List<L2Character> result = new ArrayList<>();
        for (AggroInfo ai : _aggroList.values()) {
            ai.checkHate(this);

            result.add(ai.getAttacker());
        }
        return result;
    }

    /**
     * @param target The L2Character whose hate level must be returned
     * @return the hate level of the L2Attackable against this L2Character contained in _aggroList.
     */
    public int getHating(L2Character target) {
        if (_aggroList.isEmpty() || (target == null)) {
            return 0;
        }

        final AggroInfo ai = _aggroList.get(target);
        if (ai == null) {
            return 0;
        }

        if (ai.getAttacker().isPlayer()) {
            final L2PcInstance act = (L2PcInstance) ai.getAttacker();
            if (act.isInvisible() || act.isInvul() || act.isSpawnProtected()) {
                // Remove Object Should Use This Method and Can be Blocked While Interacting
                _aggroList.remove(target);
                return 0;
            }
        }

        if (!ai.getAttacker().isSpawned() || ai.getAttacker().isInvisible()) {
            _aggroList.remove(target);
            return 0;
        }

        if (ai.getAttacker().isAlikeDead()) {
            ai.stopHate();
            return 0;
        }
        return ai.getHate();
    }

    public void doItemDrop(L2Character mainDamageDealer) {
        doItemDrop(getTemplate(), mainDamageDealer);
    }

    /**
     * Manage Base, Quests and Special Events drops of L2Attackable (called by calculateRewards).<br>
     * Concept:<br>
     * During a Special Event all L2Attackable can drop extra Items.<br>
     * Those extra Items are defined in the table allNpcDateDrops of the EventDroplist.<br>
     * Each Special Event has a start and end date to stop to drop extra Items automatically.<br>
     * Actions:<br>
     * Manage drop of Special Events created by GM for a defined period.<br>
     * Get all possible drops of this L2Attackable from L2NpcTemplate and add it Quest drops.<br>
     * For each possible drops (base + quests), calculate which one must be dropped (random).<br>
     * Get each Item quantity dropped (random).<br>
     * Create this or these L2ItemInstance corresponding to each Item Identifier dropped.<br>
     * If the autoLoot mode is actif and if the L2Character that has killed the L2Attackable is a L2PcInstance, Give the item(s) to the L2PcInstance that has killed the L2Attackable.<br>
     * If the autoLoot mode isn't actif or if the L2Character that has killed the L2Attackable is not a L2PcInstance, add this or these item(s) in the world as a visible object at the position where mob was last.
     *
     * @param npcTemplate
     * @param mainDamageDealer
     */
    public void doItemDrop(L2NpcTemplate npcTemplate, L2Character mainDamageDealer) {
        if (mainDamageDealer == null) {
            return;
        }

        final L2PcInstance player = mainDamageDealer.getActingPlayer();

        // Don't drop anything if the last attacker or owner isn't L2PcInstance
        if (player == null) {
            return;
        }

        CursedWeaponsManager.getInstance().checkDrop(this, player);

        npcTemplate.getExtendDrop().stream().map(ExtendDropData.getInstance()::getExtendDropById).filter(Objects::nonNull).forEach(e -> e.reward(player, this));

        if (isSpoiled() && !_plundered) {
            _sweepItems.set(npcTemplate.calculateDrops(DropType.SPOIL, this, player));
        }

        final Collection<ItemHolder> deathItems = npcTemplate.calculateDrops(DropType.DROP, this, player);
        if (deathItems != null) {
            for (ItemHolder drop : deathItems) {
                final L2Item item = ItemTable.getInstance().getTemplate(drop.getId());
                // Check if the autoLoot mode is active
                if (Config.AUTO_LOOT_ITEM_IDS.contains(item.getId()) || isFlying() || (!item.hasExImmediateEffect() && ((!_isRaid && Config.AUTO_LOOT) || (_isRaid && Config.AUTO_LOOT_RAIDS))) || (item.hasExImmediateEffect() && Config.AUTO_LOOT_HERBS)) {
                    player.doAutoLoot(this, drop); // Give the item(s) to the L2PcInstance that has killed the L2Attackable
                } else {
                    dropItem(player, drop); // drop the item on the ground
                }

                // Broadcast message if RaidBoss was defeated
                if (_isRaid && !_isRaidMinion) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DIED_AND_DROPPED_S3_S2_S);
                    sm.addString(getName());
                    sm.addItemName(item);
                    sm.addLong(drop.getCount());
                    broadcastPacket(sm);
                }
            }
        }

        // Apply Special Item drop with random(rnd) quantity(qty) for champions.
        if (Config.CHAMPION_ENABLE && _champion && ((Config.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE > 0) || (Config.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE > 0))) {
            int champqty = Rnd.get(Config.CHAMPION_REWARD_QTY);
            final ItemHolder item = new ItemHolder(Config.CHAMPION_REWARD_ID, ++champqty);

            if ((player.getLevel() <= getLevel()) && (Rnd.get(100) < Config.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE)) {
                if (Config.AUTO_LOOT_ITEM_IDS.contains(item.getId()) || Config.AUTO_LOOT || isFlying()) {
                    player.addItem("ChampionLoot", item.getId(), item.getCount(), this, true); // Give the item(s) to the L2PcInstance that has killed the L2Attackable
                } else {
                    dropItem(player, item);
                }
            } else if ((player.getLevel() > getLevel()) && (Rnd.get(100) < Config.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE)) {
                if (Config.AUTO_LOOT_ITEM_IDS.contains(item.getId()) || Config.AUTO_LOOT || isFlying()) {
                    player.addItem("ChampionLoot", item.getId(), item.getCount(), this, true); // Give the item(s) to the L2PcInstance that has killed the L2Attackable
                } else {
                    dropItem(player, item);
                }
            }
        }
    }

    /**
     * Manage Special Events drops created by GM for a defined period.<br>
     * Concept:<br>
     * During a Special Event all L2Attackable can drop extra Items.<br>
     * Those extra Items are defined in the table allNpcDateDrops of the EventDroplist.<br>
     * Each Special Event has a start and end date to stop to drop extra Items automatically.<br>
     * Actions: <I>If an extra drop must be generated</I><br>
     * Get an Item Identifier (random) from the DateDrop Item table of this Event.<br>
     * Get the Item quantity dropped (random).<br>
     * Create this or these L2ItemInstance corresponding to this Item Identifier.<br>
     * If the autoLoot mode is actif and if the L2Character that has killed the L2Attackable is a L2PcInstance, Give the item(s) to the L2PcInstance that has killed the L2Attackable<br>
     * If the autoLoot mode isn't actif or if the L2Character that has killed the L2Attackable is not a L2PcInstance, add this or these item(s) in the world as a visible object at the position where mob was last
     *
     * @param lastAttacker The L2Character that has killed the L2Attackable
     */
    public void doEventDrop(L2Character lastAttacker) {
        if (lastAttacker == null) {
            return;
        }

        final L2PcInstance player = lastAttacker.getActingPlayer();

        // Don't drop anything if the last attacker or owner isn't L2PcInstance
        if (player == null) {
            return;
        }

        if ((player.getLevel() - getLevel()) > 9) {
            return;
        }

        // Go through DateDrop of EventDroplist allNpcDateDrops within the date range
        for (DateDrop drop : EventDroplist.getInstance().getAllDrops()) {
            if (Rnd.get(1000000) < drop.getEventDrop().getDropChance()) {
                final int itemId = drop.getEventDrop().getItemIdList()[Rnd.get(drop.getEventDrop().getItemIdList().length)];
                final long itemCount = Rnd.get(drop.getEventDrop().getMinCount(), drop.getEventDrop().getMaxCount());
                if (Config.AUTO_LOOT_ITEM_IDS.contains(itemId) || Config.AUTO_LOOT || isFlying()) {
                    player.doAutoLoot(this, itemId, itemCount); // Give the item(s) to the L2PcInstance that has killed the L2Attackable
                } else {
                    dropItem(player, itemId, itemCount); // drop the item on the ground
                }
            }
        }
    }

    /**
     * @return the active weapon of this L2Attackable (= null).
     */
    public L2ItemInstance getActiveWeapon() {
        return null;
    }

    /**
     * @param player The L2Character searched in the _aggroList of the L2Attackable
     * @return True if the _aggroList of this L2Attackable contains the L2Character.
     */
    public boolean containsTarget(L2Character player) {
        return _aggroList.containsKey(player);
    }

    /**
     * Clear the _aggroList of the L2Attackable.
     */
    public void clearAggroList() {
        _aggroList.clear();

        // clear overhit values
        _overhit = false;
        _overhitDamage = 0;
        _overhitAttacker = null;
    }

    /**
     * @return {@code true} if there is a loot to sweep, {@code false} otherwise.
     */
    @Override
    public boolean isSweepActive() {
        return _sweepItems.get() != null;
    }

    /**
     * @return a copy of dummy items for the spoil loot.
     */
    public List<L2Item> getSpoilLootItems() {
        final Collection<ItemHolder> sweepItems = _sweepItems.get();
        final List<L2Item> lootItems = new LinkedList<>();
        if (sweepItems != null) {
            for (ItemHolder item : sweepItems) {
                lootItems.add(ItemTable.getInstance().getTemplate(item.getId()));
            }
        }
        return lootItems;
    }

    /**
     * @return table containing all L2ItemInstance that can be spoiled.
     */
    public Collection<ItemHolder> takeSweep() {
        return _sweepItems.getAndSet(null);
    }

    /**
     * @return table containing all L2ItemInstance that can be harvested.
     */
    public ItemHolder takeHarvest() {
        return _harvestItem.getAndSet(null);
    }

    /**
     * Checks if the corpse is too old.
     *
     * @param attacker      the player to validate
     * @param remainingTime the time to check
     * @param sendMessage   if {@code true} will send a message of corpse too old
     * @return {@code true} if the corpse is too old
     */
    public boolean isOldCorpse(L2PcInstance attacker, int remainingTime, boolean sendMessage) {
        if (isDead() && (DecayTaskManager.getInstance().getRemainingTime(this) < remainingTime)) {
            if (sendMessage && (attacker != null)) {
                attacker.sendPacket(SystemMessageId.THE_CORPSE_IS_TOO_OLD_THE_SKILL_CANNOT_BE_USED);
            }
            return true;
        }
        return false;
    }

    /**
     * @param sweeper     the player to validate.
     * @param sendMessage sendMessage if {@code true} will send a message of sweep not allowed.
     * @return {@code true} if is the spoiler or is in the spoiler party.
     */
    public boolean checkSpoilOwner(L2PcInstance sweeper, boolean sendMessage) {
        if ((sweeper.getObjectId() != _spoilerObjectId) && !sweeper.isInLooterParty(_spoilerObjectId)) {
            if (sendMessage) {
                sweeper.sendPacket(SystemMessageId.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
            }
            return false;
        }
        return true;
    }

    /**
     * Set the over-hit flag on the L2Attackable.
     *
     * @param status The status of the over-hit flag
     */
    public void overhitEnabled(boolean status) {
        _overhit = status;
    }

    /**
     * Set the over-hit values like the attacker who did the strike and the amount of damage done by the skill.
     *
     * @param attacker The L2Character who hit on the L2Attackable using the over-hit enabled skill
     * @param damage   The amount of damage done by the over-hit enabled skill on the L2Attackable
     */
    public void setOverhitValues(L2Character attacker, double damage) {
        // Calculate the over-hit damage
        // Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
        final double overhitDmg = -(getCurrentHp() - damage);
        if (overhitDmg < 0) {
            // we didn't killed the mob with the over-hit strike. (it wasn't really an over-hit strike)
            // let's just clear all the over-hit related values
            overhitEnabled(false);
            _overhitDamage = 0;
            _overhitAttacker = null;
            return;
        }
        overhitEnabled(true);
        _overhitDamage = overhitDmg;
        _overhitAttacker = attacker;
    }

    /**
     * Return the L2Character who hit on the L2Attackable using an over-hit enabled skill.
     *
     * @return L2Character attacker
     */
    public L2Character getOverhitAttacker() {
        return _overhitAttacker;
    }

    /**
     * Return the amount of damage done on the L2Attackable using an over-hit enabled skill.
     *
     * @return double damage
     */
    public double getOverhitDamage() {
        return _overhitDamage;
    }

    /**
     * @return True if the L2Attackable was hit by an over-hit enabled skill.
     */
    public boolean isOverhit() {
        return _overhit;
    }

    /**
     * Calculate the Experience and SP to distribute to attacker (L2PcInstance, L2ServitorInstance or L2Party) of the L2Attackable.
     *
     * @param charLevel   The killer level
     * @param damage      The damages given by the attacker (L2PcInstance, L2ServitorInstance or L2Party)
     * @param totalDamage The total damage done
     * @return
     */
    private double[] calculateExpAndSp(int charLevel, long damage, long totalDamage) {
        final int levelDiff = charLevel - getLevel();
        double xp = Math.max(0, (getExpReward() * damage) / totalDamage);
        double sp = Math.max(0, (getSpReward() * damage) / totalDamage);

        // According to https://4gameforum.com/threads/483941/
        if (levelDiff > 2) {
            double mul;
            switch (levelDiff) {
                case 3: {
                    mul = 0.97;
                    break;
                }
                case 4: {
                    mul = 0.80;
                    break;
                }
                case 5: {
                    mul = 0.61;
                    break;
                }
                case 6: {
                    mul = 0.37;
                    break;
                }
                case 7: {
                    mul = 0.22;
                    break;
                }
                case 8: {
                    mul = 0.13;
                    break;
                }
                case 9: {
                    mul = 0.08;
                    break;
                }
                default: {
                    mul = 0.05;
                    break;
                }
            }
            xp *= mul;
            sp *= mul;
        }

        return new double[]
                {
                        xp,
                        sp
                };
    }

    public double calculateOverhitExp(double exp) {
        // Get the percentage based on the total of extra (over-hit) damage done relative to the total (maximum) ammount of HP on the L2Attackable
        double overhitPercentage = ((_overhitDamage * 100) / getMaxHp());

        // Over-hit damage percentages are limited to 25% max
        if (overhitPercentage > 25) {
            overhitPercentage = 25;
        }

        // Get the overhit exp bonus according to the above over-hit damage percentage
        // (1/1 basis - 13% of over-hit damage, 13% of extra exp is given, and so on...)
        return (overhitPercentage / 100) * exp;
    }

    /**
     * Return True.
     */
    @Override
    public boolean canBeAttacked() {
        return true;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        // Clear mob spoil, seed
        setSpoilerObjectId(0);

        // Clear all aggro list and overhit
        clearAggroList();

        // Clear Harvester reward
        _harvestItem.set(null);
        _sweepItems.set(null);
        _plundered = false;

        setWalking();


        // Clear mod Seeded stat
        _seeded = false;
        _seed = null;
        _seederObjId = 0;

        // check the region where this mob is, do not activate the AI if region is inactive.
        // if (!isInActiveRegion())
        // {
        // if (hasAI())
        // {
        // getAI().stopAITask();
        // }
        // }
    }

    @Override
    public void onRespawn() {
        // Reset champion state
        _champion = false;

        if (Config.CHAMPION_ENABLE) {
            // Set champion on next spawn
            if (isMonster() && !isQuestMonster() && !getTemplate().isUndying() && !_isRaid && !_isRaidMinion && (Config.CHAMPION_FREQUENCY > 0) && (getLevel() >= Config.CHAMP_MIN_LVL) && (getLevel() <= Config.CHAMP_MAX_LVL) && (Config.CHAMPION_ENABLE_IN_INSTANCES || (getInstanceId() == 0))) {
                if (Rnd.get(100) < Config.CHAMPION_FREQUENCY) {
                    _champion = true;
                    if (Config.SHOW_CHAMPION_AURA) {
                        setTeam(Team.RED);
                    }
                }
            }
        }

        // Reset the rest of NPC related states
        super.onRespawn();
    }

    /**
     * Checks if its spoiled.
     *
     * @return {@code true} if its spoiled, {@code false} otherwise
     */
    public boolean isSpoiled() {
        return _spoilerObjectId != 0;
    }

    /**
     * Gets the spoiler object ID.
     *
     * @return the spoiler object ID if its spoiled, 0 otherwise
     */
    public final int getSpoilerObjectId() {
        return _spoilerObjectId;
    }

    /**
     * Sets the spoiler object ID.
     *
     * @param spoilerObjectId spoilerObjectId the spoiler object ID
     */
    public final void setSpoilerObjectId(int spoilerObjectId) {
        _spoilerObjectId = spoilerObjectId;
    }

    /**
     * Sets state of the mob to plundered.
     * @param player
     */
    public void setPlundered(L2PcInstance player)
    {
        _plundered = true;
        _spoilerObjectId = player.getObjectId();
        _sweepItems.set(getTemplate().calculateDrops(DropType.SPOIL, this, player));
    }

    /**
     * Sets the seed parameters, but not the seed state
     *
     * @param seed   - instance {@link L2Seed} of used seed
     * @param seeder - player who sows the seed
     */
    public final void setSeeded(L2Seed seed, L2PcInstance seeder) {
        if (!_seeded) {
            _seed = seed;
            _seederObjId = seeder.getObjectId();
        }
    }

    public final int getSeederId() {
        return _seederObjId;
    }

    public final L2Seed getSeed() {
        return _seed;
    }

    public final boolean isSeeded() {
        return _seeded;
    }

    /**
     * Sets state of the mob to seeded. Parameters needed to be set before.
     *
     * @param seeder
     */
    public final void setSeeded(L2PcInstance seeder) {
        if ((_seed != null) && (_seederObjId == seeder.getObjectId())) {
            _seeded = true;

            int count = 1;
            for (int skillId : getTemplate().getSkills().keySet()) {
                switch (skillId) {
                    case 4303: // Strong type x2
                    {
                        count *= 2;
                        break;
                    }
                    case 4304: // Strong type x3
                    {
                        count *= 3;
                        break;
                    }
                    case 4305: // Strong type x4
                    {
                        count *= 4;
                        break;
                    }
                    case 4306: // Strong type x5
                    {
                        count *= 5;
                        break;
                    }
                    case 4307: // Strong type x6
                    {
                        count *= 6;
                        break;
                    }
                    case 4308: // Strong type x7
                    {
                        count *= 7;
                        break;
                    }
                    case 4309: // Strong type x8
                    {
                        count *= 8;
                        break;
                    }
                    case 4310: // Strong type x9
                    {
                        count *= 9;
                        break;
                    }
                }
            }

            // hi-lvl mobs bonus
            final int diff = getLevel() - _seed.getLevel() - 5;
            if (diff > 0) {
                count += diff;
            }
            _harvestItem.set(new ItemHolder(_seed.getCropId(), count * Config.RATE_DROP_MANOR));
        }
    }

    /**
     * Check if the server allows Random Animation.
     */
    // This is located here because L2Monster and L2FriendlyMob both extend this class. The other non-pc instances extend either L2NpcInstance or L2MonsterInstance.
    @Override
    public boolean hasRandomAnimation() {
        return ((Config.MAX_MONSTER_ANIMATION > 0) && isRandomAnimationEnabled() && !(this instanceof L2GrandBossInstance));
    }

    public CommandChannelTimer getCommandChannelTimer() {
        return _commandChannelTimer;
    }

    public void setCommandChannelTimer(CommandChannelTimer commandChannelTimer) {
        _commandChannelTimer = commandChannelTimer;
    }

    public L2CommandChannel getFirstCommandChannelAttacked() {
        return _firstCommandChannelAttacked;
    }

    public void setFirstCommandChannelAttacked(L2CommandChannel firstCommandChannelAttacked) {
        _firstCommandChannelAttacked = firstCommandChannelAttacked;
    }

    /**
     * @return the _commandChannelLastAttack
     */
    public long getCommandChannelLastAttack() {
        return _commandChannelLastAttack;
    }

    /**
     * @param channelLastAttack the _commandChannelLastAttack to set
     */
    public void setCommandChannelLastAttack(long channelLastAttack) {
        _commandChannelLastAttack = channelLastAttack;
    }

    public void returnHome() {
        clearAggroList();

        if (hasAI() && (getSpawn() != null)) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, getSpawn().getLocation());
        }
    }

    /*
     * Return vitality points decrease (if positive) or increase (if negative) based on damage. Maximum for damage = maxHp.
     */
    public int getVitalityPoints(int level, double exp, boolean isBoss) {
        if ((getLevel() <= 0) || (getExpReward() <= 0)) {
            return 0;
        }

        int points;
        if (level < 85) {
            points = Math.max((int) ((exp / 1000) * Math.max(level - getLevel(), 1)), 1);
        } else {
            points = Math.max((int) ((exp / (isBoss ? Config.VITALITY_CONSUME_BY_BOSS : Config.VITALITY_CONSUME_BY_MOB)) * Math.max(level - getLevel(), 1)), 1);
        }

        return -points;
    }

    /*
     * True if vitality rate for exp and sp should be applied
     */
    public boolean useVitalityRate() {
        return !_champion || Config.CHAMPION_ENABLE_VITALITY;
    }

    /**
     * Return True if the L2Character is RaidBoss or his minion.
     */
    @Override
    public boolean isRaid() {
        return _isRaid;
    }

    /**
     * Set this Npc as a Raid instance.
     *
     * @param isRaid
     */
    public void setIsRaid(boolean isRaid) {
        _isRaid = isRaid;
    }

    /**
     * Set this Npc as a Minion instance.
     *
     * @param val
     */
    public void setIsRaidMinion(boolean val) {
        _isRaid = val;
        _isRaidMinion = val;
    }

    @Override
    public boolean isRaidMinion() {
        return _isRaidMinion;
    }

    @Override
    public boolean isMinion() {
        return getLeader() != null;
    }

    /**
     * @return leader of this minion or null.
     */
    public L2Attackable getLeader() {
        return null;
    }

    @Override
    public boolean isChampion() {
        return _champion;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public void setTarget(L2Object object) {
        if (isDead()) {
            return;
        }

        if (object == null) {
            final L2Object target = getTarget();
            final Map<L2Character, AggroInfo> aggroList = _aggroList;
            if (target != null) {
                if (aggroList != null) {
                    aggroList.remove(target);
                }
            }
            if ((aggroList != null) && aggroList.isEmpty()) {
                if (getAI() instanceof L2AttackableAI) {
                    ((L2AttackableAI) getAI()).setGlobalAggro(-25);
                }
                setWalking();
                clearAggroList();
            }
            getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
        super.setTarget(object);
    }
}
