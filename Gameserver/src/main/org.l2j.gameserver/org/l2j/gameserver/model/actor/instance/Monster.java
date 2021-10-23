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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.settings.ChampionSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.MinionList;

import java.util.Queue;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;

/**
 * This class manages all Monsters. Monster:
 * <ul>
 * <li>Minion</li>
 * <li>RaidBoss</li>
 * <li>GrandBoss</li>
 * </ul>
 */
public class Monster extends Attackable {
    private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;
    protected ScheduledFuture<?> _maintenanceTask = null;
    private Monster _master = null;
    private volatile MinionList _minionList = null;

    private boolean isChampion = false;

    /**
     * Constructor of Monster (use Creature and Folk constructor).<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Call the Creature constructor to set the _template of the Monster (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
     * <li>Set the name of the Monster</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
     * </ul>
     *
     * @param template to apply to the NPC
     */
    public Monster(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2MonsterInstance);
        setAutoAttackable(true);
    }

    /**
     * Return True if the attacker is not another Monster.
     */
    @Override
    public boolean isAutoAttackable(Creature attacker) {
        // Check if the Monster target is aggressive
        if (Config.GUARD_ATTACK_AGGRO_MOB && getTemplate().isAggressive() && (attacker instanceof Guard)) {
            return true;
        }

        if(attacker instanceof FriendlyMob && isAggressive() && !getAggroList().isEmpty() && GameUtils.isPlayable(getTarget())) {
            return true;
        }

        // Anything considers monsters friendly except Players, Attackables (Guards, Friendly NPC), Traps and EffectPoints.
        if (!GameUtils.isPlayable(attacker) && !GameUtils.isAttackable(attacker) && !(attacker instanceof Trap) && !(attacker instanceof EffectPoint)) {
            return false;
        }

        return super.isAutoAttackable(attacker);
    }

    /**
     * Return True if the Monster is Aggressive (aggroRange > 0).
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
    public void onRespawn() {
        if(isRandomChampion()) {
            isChampion = true;
            if (ChampionSettings.showAura()) {
                setTeam(Team.RED);
            }
        } else {
            isChampion = false;
        }

        super.onRespawn();
    }

    private boolean isRandomChampion() {
        return ChampionSettings.checkChampionChance(getLevel()) &&
                !isQuestMonster() &&
                !getTemplate().isUndying() &&
                !isRaid() &&
                !isRaidMinion();
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
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        if (nonNull(_master) && _master.hasMinions()) {
            final int respawnTime = Config.MINIONS_RESPAWN_TIME.containsKey(getId()) ? Config.MINIONS_RESPAWN_TIME.get(getId()) * 1000 : -1;
            _master.getMinionList().onMinionDie(this, respawnTime);
        }

        if (hasMinions()) {
            getMinionList().onMasterDie(false);
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
    public Monster getLeader() {
        return _master;
    }

    public void setLeader(Monster leader) {
        _master = leader;
    }

    public boolean hasMinions() {
        return _minionList != null;
    }

    public void setMinionList(MinionList minionList) {
        _minionList = minionList;
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

    /**
     * @return {@code true} if this Monster is not raid minion, master state otherwise.
     */
    @Override
    public boolean giveRaidCurse() {
        return (isRaidMinion() && (_master != null)) ? _master.giveRaidCurse() : super.giveRaidCurse();
    }

    @Override
    public synchronized void doCast(Skill skill, Item item, boolean ctrlPressed, boolean shiftPressed) {
        // Might need some exceptions here, but it will prevent the monster buffing player bug.
        if (!skill.isBad() && GameUtils.isPlayer(getTarget())) {
            abortAllSkillCasters();
            return;
        }
        super.doCast(skill, item, ctrlPressed, shiftPressed);
    }

    @Override
    protected double[] calculateExpAndSp(int charLevel, long damage, long totalDamage) {
        var expAndSp = super.calculateExpAndSp(charLevel, damage, totalDamage);
        if (isChampion) {
            expAndSp[0] *= ChampionSettings.expSpMultiplier();
            expAndSp[1] *= ChampionSettings.expSpMultiplier();
        }
        return expAndSp;
    }

    @Override
    protected Queue<AbstractEventListener> globalListenerByType(EventType type) {
        return Listeners.Monsters().getListeners(type);
    }

    @Override
    public boolean isChampion() {
        return isChampion;
    }

    @Override
    public boolean useVitalityRate() {
        return !isChampion || ChampionSettings.isVitalityEnabled();
    }

    @Override
    public String getTitle() {
        return  isChampion ? ChampionSettings.title() : super.getTitle();
    }
}
