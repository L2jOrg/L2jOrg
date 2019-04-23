package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.util.MinionList;

import java.util.concurrent.ScheduledFuture;

/**
 * This class manages all Monsters. L2MonsterInstance:
 * <ul>
 * <li>L2MinionInstance</li>
 * <li>L2RaidBossInstance</li>
 * <li>L2GrandBossInstance</li>
 * </ul>
 */
public class L2MonsterInstance extends L2Attackable {
    private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;
    protected boolean _enableMinions = true;
    protected ScheduledFuture<?> _maintenanceTask = null;
    private L2MonsterInstance _master = null;
    private volatile MinionList _minionList = null;

    /**
     * Constructor of L2MonsterInstance (use L2Character and L2NpcInstance constructor).<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Call the L2Character constructor to set the _template of the L2MonsterInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
     * <li>Set the name of the L2MonsterInstance</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
     * </ul>
     *
     * @param template to apply to the NPC
     */
    public L2MonsterInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2MonsterInstance);
        setAutoAttackable(true);
    }

    /**
     * Return True if the attacker is not another L2MonsterInstance.
     */
    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        // Check if the L2MonsterInstance target is aggressive
        if (Config.GUARD_ATTACK_AGGRO_MOB && getTemplate().isAggressive() && (attacker instanceof L2GuardInstance)) {
            return true;
        }

        if (attacker.isMonster()) {
            return false;
        }

        // Anything considers monsters friendly except Players, Attackables (Guards, Friendly NPC), Traps and EffectPoints.
        if (!attacker.isPlayable() && !attacker.isAttackable() && !(attacker instanceof L2TrapInstance) && !(attacker instanceof L2EffectPointInstance)) {
            return false;
        }

        return super.isAutoAttackable(attacker);
    }

    /**
     * Return True if the L2MonsterInstance is Aggressive (aggroRange > 0).
     */
    @Override
    public boolean isAggressive() {
        return getTemplate().isAggressive() && !isAffected(EffectFlag.PASSIVE);
    }

    @Override
    public void onSpawn() {
        if (!isTeleporting()) {
            if (_master != null) {
                setRandomWalking(false);
                setIsRaidMinion(_master.isRaid());
                _master.getMinionList().onMinionSpawn(this);
            }

            startMaintenanceTask();
        }

        // dynamic script-based minions spawned here, after all preparations.
        super.onSpawn();
    }

    @Override
    public void onTeleported() {
        super.onTeleported();

        if (hasMinions()) {
            getMinionList().onMasterTeleported();
        }
    }

    protected int getMaintenanceInterval() {
        return MONSTER_MAINTENANCE_INTERVAL;
    }

    protected void startMaintenanceTask() {
    }

    @Override
    public boolean doDie(L2Character killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        if (_maintenanceTask != null) {
            _maintenanceTask.cancel(false); // doesn't do it?
            _maintenanceTask = null;
        }

        return true;
    }

    @Override
    public boolean deleteMe() {
        if (_maintenanceTask != null) {
            _maintenanceTask.cancel(false);
            _maintenanceTask = null;
        }

        if (hasMinions()) {
            getMinionList().onMasterDie(true);
        }

        if (_master != null) {
            _master.getMinionList().onMinionDie(this, 0);
        }

        return super.deleteMe();
    }

    @Override
    public L2MonsterInstance getLeader() {
        return _master;
    }

    public void setLeader(L2MonsterInstance leader) {
        _master = leader;
    }

    public void enableMinions(boolean b) {
        _enableMinions = b;
    }

    public boolean hasMinions() {
        return _minionList != null;
    }

    public MinionList getMinionList() {
        if (_minionList == null) {
            synchronized (this) {
                if (_minionList == null) {
                    _minionList = new MinionList(this);
                }
            }
        }
        return _minionList;
    }

    @Override
    public boolean isMonster() {
        return true;
    }

    /**
     * @return true if this L2MonsterInstance (or its master) is registered in WalkingManager
     */
    @Override
    public boolean isWalker() {
        return ((_master == null) ? super.isWalker() : _master.isWalker());
    }

    /**
     * @return {@code true} if this L2MonsterInstance is not raid minion, master state otherwise.
     */
    @Override
    public boolean giveRaidCurse() {
        return (isRaidMinion() && (_master != null)) ? _master.giveRaidCurse() : super.giveRaidCurse();
    }

    @Override
    public synchronized void doCast(Skill skill, L2ItemInstance item, boolean ctrlPressed, boolean shiftPressed) {
        // Might need some exceptions here, but it will prevent the monster buffing player bug.
        if (!skill.isBad() && (getTarget() != null) && getTarget().isPlayer()) {
            abortAllSkillCasters();
            return;
        }
        super.doCast(skill, item, ctrlPressed, shiftPressed);
    }
}
