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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.TrapAction;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.tasks.npc.trap.TrapTask;
import org.l2j.gameserver.model.actor.tasks.npc.trap.TrapTriggerTask;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnTrapAction;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Trap instance.
 *
 * @author Zoey76
 */
public final class Trap extends Npc {
    private static final int TICK = 1000; // 1s
    private final int _lifeTime;
    private final List<Integer> _playersWhoDetectedMe = new ArrayList<>();
    private final SkillHolder _skill;
    private boolean _hasLifeTime;
    private boolean _isInArena = false;
    private boolean _isTriggered;
    private Player _owner;
    private int _remainingTime;
    // Tasks
    private ScheduledFuture<?> _trapTask = null;

    public Trap(NpcTemplate template, int instanceId, int lifeTime) {
        super(template);
        setInstanceType(InstanceType.L2TrapInstance);
        setInstanceById(instanceId);
        setName(template.getName());
        setIsInvul(false);
        _owner = null;
        _isTriggered = false;
        _skill = getParameters().getObject("trap_skill", SkillHolder.class);
        _hasLifeTime = lifeTime >= 0;
        _lifeTime = lifeTime != 0 ? lifeTime : 30000;
        _remainingTime = _lifeTime;
        if (_skill != null) {
            _trapTask = ThreadPool.scheduleAtFixedRate(new TrapTask(this), TICK, TICK);
        }
    }

    public Trap(NpcTemplate template, Player owner, int lifeTime) {
        this(template, owner.getInstanceId(), lifeTime);
        _owner = owner;
    }

    @Override
    public void broadcastPacket(ServerPacket mov) {
        World.getInstance().forEachVisibleObject(this, Player.class, player ->
        {
            if (_isTriggered || canBeSeen(player)) {
                player.sendPacket(mov);
            }
        });
    }

    @Override
    public void broadcastPacket(ServerPacket mov, int radiusInKnownlist) {
        World.getInstance().forEachVisibleObjectInRange(this, Player.class, radiusInKnownlist, player ->
        {
            if (_isTriggered || canBeSeen(player)) {
                player.sendPacket(mov);
            }
        });
    }

    /**
     * Verify if the character can see the trap.
     *
     * @param cha the character to verify
     * @return {@code true} if the character can see the trap, {@code false} otherwise
     */
    public boolean canBeSeen(Creature cha) {
        if ((cha != null) && _playersWhoDetectedMe.contains(cha.getObjectId())) {
            return true;
        }

        if ((_owner == null) || (cha == null)) {
            return false;
        }
        if (cha == _owner) {
            return true;
        }

        if (GameUtils.isPlayer(cha)) {
            // observers can't see trap
            if (((Player) cha).inObserverMode()) {
                return false;
            }

            // olympiad competitors can't see trap
            if (_owner.isInOlympiadMode() && ((Player) cha).isInOlympiadMode() && (((Player) cha).getOlympiadSide() != _owner.getOlympiadSide())) {
                return false;
            }
        }

        if (_isInArena) {
            return true;
        }

        if (_owner.isInParty() && cha.isInParty() && (_owner.getParty().getLeaderObjectId() == cha.getParty().getLeaderObjectId())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteMe() {
        _owner = null;
        return super.deleteMe();
    }

    @Override
    public Player getActingPlayer() {
        return _owner;
    }

    @Override
    public Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public int getReputation() {
        return _owner != null ? _owner.getReputation() : 0;
    }

    /**
     * Get the owner of this trap.
     *
     * @return the owner
     */
    public Player getOwner() {
        return _owner;
    }

    @Override
    public byte getPvpFlag() {
        return _owner != null ? _owner.getPvpFlag() : 0;
    }

    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }

    public Skill getSkill() {
        return _skill.getSkill();
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return !canBeSeen(attacker);
    }

    /**
     * Checks is triggered
     *
     * @return True if trap is triggered.
     */
    public boolean isTriggered() {
        return _isTriggered;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        _isInArena = isInsideZone(ZoneType.PVP) && !isInsideZone(ZoneType.SIEGE);
        _playersWhoDetectedMe.clear();
    }

    @Override
    public void doAttack(double damage, Creature target, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect) {
        super.doAttack(damage, target, skill, isDOT, directlyToHp, critical, reflect);
        sendDamageMessage(target, skill, (int) damage, 0, critical, false);
    }

    @Override
    public void sendDamageMessage(Creature target, Skill skill, int damage, double elementalDamage, boolean crit, boolean miss) {
        if (miss || (_owner == null)) {
            return;
        }

        if (_owner.isInOlympiadMode() && GameUtils.isPlayer(target) && ((Player) target).isInOlympiadMode() && (((Player) target).getOlympiadGameId() == _owner.getOlympiadGameId())) {
            OlympiadGameManager.getInstance().notifyCompetitorDamage(getOwner(), damage);
        }

        if (target.isHpBlocked() && !GameUtils.isNpc(target)) {
            _owner.sendPacket(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
        } else {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
            sm.addString(getName());
            sm.addString(target.getName());
            sm.addInt(damage);
            sm.addPopup(target.getObjectId(), getObjectId(), (damage * -1));
            _owner.sendPacket(sm);
        }
    }

    @Override
    public void sendInfo(Player activeChar) {
        if (_isTriggered || canBeSeen(activeChar)) {
            activeChar.sendPacket(new NpcInfo(this));
        }
    }

    public void setDetected(Creature detector) {
        if (_isInArena) {
            if (GameUtils.isPlayable(detector)) {
                sendInfo(detector.getActingPlayer());
            }
            return;
        }

        if ((_owner != null) && (_owner.getPvpFlag() == 0) && (_owner.getReputation() >= 0)) {
            return;
        }

        _playersWhoDetectedMe.add(detector.getObjectId());

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnTrapAction(this, detector, TrapAction.TRAP_DETECTED), this);

        if (GameUtils.isPlayable(detector)) {
            sendInfo(detector.getActingPlayer());
        }
    }

    public void stopDecay() {
        DecayTaskManager.getInstance().cancel(this);
    }

    /**
     * Trigger the trap.
     *
     * @param target the target
     */
    public void triggerTrap(Creature target) {
        if (_trapTask != null) {
            _trapTask.cancel(true);
            _trapTask = null;
        }

        _isTriggered = true;
        broadcastPacket(new NpcInfo(this));
        setTarget(target);

        EventDispatcher.getInstance().notifyEventAsync(new OnTrapAction(this, target, TrapAction.TRAP_TRIGGERED), this);

        ThreadPool.schedule(new TrapTriggerTask(this), 500);
    }

    public void unSummon() {
        if (_trapTask != null) {
            _trapTask.cancel(true);
            _trapTask = null;
        }

        _owner = null;

        if (isSpawned() && !isDead()) {
            ZoneManager.getInstance().getRegion(this).removeFromZones(this);
            deleteMe();
        }
    }

    public boolean hasLifeTime() {
        return _hasLifeTime;
    }

    public void setHasLifeTime(boolean val) {
        _hasLifeTime = val;
    }

    public int getRemainingTime() {
        return _remainingTime;
    }

    public void setRemainingTime(int time) {
        _remainingTime = time;
    }

    public int getLifeTime() {
        return _lifeTime;
    }
}
