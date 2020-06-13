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
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.ai.DoorAI;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.DoorOpenType;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.stat.DoorStats;
import org.l2j.gameserver.model.actor.status.DoorStatus;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.DoorStatusUpdate;
import org.l2j.gameserver.network.serverpackets.OnEventTrigger;
import org.l2j.gameserver.network.serverpackets.StaticObject;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

import java.util.concurrent.Future;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.falseIfNullOrElse;

public final class Door extends Creature {
    boolean open;
    private boolean _isAttackableDoor;
    private boolean inverted;
    private int _meshindex = 1;
    private Future<?> _autoCloseTask;

    public Door(DoorTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2DoorInstance);
        setIsInvul(false);
        setLethalable(false);
        open = template.isOpenByDefault();
        _isAttackableDoor = template.isAttackable();
        inverted = template.isInverted();
        super.setTargetable(template.isTargetable());

        if (isOpenableByTime()) {
            startTimerOpen();
        }
    }

    @Override
    protected CreatureAI initAI() {
        return new DoorAI(this);
    }

    @Override
    public void moveToLocation(int x, int y, int z, int offset) {
    }

    @Override
    public void stopMove(Location loc) {
    }

    @Override
    public void doAutoAttack(Creature target) {
    }

    @Override
    public void doCast(Skill skill) {
    }

    private void startTimerOpen() {
        int delay = open ? getTemplate().getOpenTime() : getTemplate().getCloseTime();
        if (getTemplate().getRandomTime() > 0) {
            delay += Rnd.get(getTemplate().getRandomTime());
        }
        ThreadPool.schedule(new TimerOpen(), delay * 1000);
    }

    @Override
    public DoorTemplate getTemplate() {
        return (DoorTemplate) super.getTemplate();
    }

    @Override
    public final DoorStatus getStatus() {
        return (DoorStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new DoorStatus(this));
    }

    @Override
    public void initCharStat() {
        setStat(new DoorStats(this));
    }

    @Override
    public DoorStats getStats() {
        return (DoorStats) super.getStats();
    }

    /**
     * @return {@code true} if door is open-able by skill.
     */
    public final boolean isOpenableBySkill() {
        return (getTemplate().getOpenType()) == DoorOpenType.BY_SKILL;
    }

    /**
     * @return {@code true} if door is open-able by item.
     */
    public final boolean isOpenableByItem() {
        return (getTemplate().getOpenType()) == DoorOpenType.BY_ITEM;
    }

    /**
     * @return {@code true} if door is open-able by double-click.
     */
    public final boolean isOpenableByClick() {
        return (getTemplate().getOpenType()) == DoorOpenType.BY_CLICK;
    }

    /**
     * @return {@code true} if door is open-able by time.
     */
    public final boolean isOpenableByTime() {
        return (getTemplate().getOpenType()) == DoorOpenType.BY_TIME;
    }

    /**
     * @return {@code true} if door is open-able by Field Cycle system.
     */
    public final boolean isOpenableByCycle() {
        return (getTemplate().getOpenType()) == DoorOpenType.BY_CYCLE;
    }

    @Override
    public final int getLevel() {
        return getTemplate().getLevel();
    }

    /**
     * Gets the door ID.
     *
     * @return the door ID
     */
    @Override
    public int getId() {
        return getTemplate().getId();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        if (getChildId() > 0) {
            final Door sibling = getSiblingDoor(getChildId());
            if (sibling != null) {
                sibling.notifyChildEvent(open);
            } else {
                LOGGER.warn(": cannot find child id: " + getChildId());
            }
        }
    }

    public boolean getIsAttackableDoor() {
        return _isAttackableDoor;
    }

    public void setIsAttackableDoor(boolean val) {
        _isAttackableDoor = val;
    }

    public boolean isInverted() {
        return inverted;
    }

    public boolean getIsShowHp() {
        return getTemplate().isShowHp();
    }

    public int getDamage() {
        if (getCastle() == null) {
            return 0;
        }
        final int dmg = 6 - (int) Math.ceil((getCurrentHp() / getMaxHp()) * 6);
        if (dmg > 6) {
            return 6;
        }
        if (dmg < 0) {
            return 0;
        }
        return dmg;
    }

    public final Castle getCastle() {
        return CastleManager.getInstance().getCastle(this);
    }

    public boolean isEnemy() {
        return (getCastle() != null) && (getCastle().getId() > 0) && getCastle().getZone().isActive() && getIsShowHp();
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        // Doors can`t be attacked by NPCs
        if (!GameUtils.isPlayable(attacker)) {
            return false;
        } else if (_isAttackableDoor) {
            return true;
        } else if (!getIsShowHp()) {
            return false;
        }

        final Player actingPlayer = attacker.getActingPlayer();

        // Attackable only during siege by everyone (not owner)
        final boolean isCastle = ((getCastle() != null) && (getCastle().getId() > 0) && getCastle().getZone().isActive());

        if (isCastle) {
            final Clan clan = actingPlayer.getClan();
            if ((clan != null) && (clan.getId() == getCastle().getOwnerId())) {
                return false;
            }
        }
        return isCastle;
    }

    /**
     * Return null.
     */
    @Override
    public Item getActiveWeaponInstance() {
        return null;
    }

    @Override
    public Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public void broadcastStatusUpdate(Creature caster) {

        final StaticObject su = new StaticObject(this, false);
        final StaticObject targetableSu = new StaticObject(this, true);
        final DoorStatusUpdate dsu = new DoorStatusUpdate(this);
        final OnEventTrigger oe = getEmitter() <= 0 ? null : new OnEventTrigger(getEmitter(), inverted ^ open);

        World.getInstance().forAnyVisibleObject(this, Player.class, player -> sendUpdateToPlayer(player, su, targetableSu, dsu, oe), this::isVisibleFor);
    }

    private void sendUpdateToPlayer(Player player, StaticObject su, StaticObject targetableSu, DoorStatusUpdate dsu, OnEventTrigger oe) {
        if (player.isGM() ||  falseIfNullOrElse(getCastle(), c -> c.getId() > 0)) {
            player.sendPacket(targetableSu);
        } else {
            player.sendPacket(su);
        }

        player.sendPacket(dsu);
        if (oe != null) {
            player.sendPacket(oe);
        }
    }

    public final void openCloseMe(boolean open) {
        if (open) {
            openMe();
        } else {
            closeMe();
        }
    }

    public final void openMe() {
        if (getGroupName() != null) {
            manageGroupOpen(true, getGroupName());
            return;
        }
        setOpen(true);
        broadcastStatusUpdate();
        startAutoCloseTask();
    }

    public final void closeMe() {
        // remove close task
        final Future<?> oldTask = _autoCloseTask;
        if (oldTask != null) {
            _autoCloseTask = null;
            oldTask.cancel(false);
        }
        if (getGroupName() != null) {
            manageGroupOpen(false, getGroupName());
            return;
        }
        setOpen(false);
        broadcastStatusUpdate();
    }

    private void manageGroupOpen(boolean open, String groupName) {
        var doorsId = DoorDataManager.getInstance().getDoorsByGroup(groupName).iterator();
        Door first = null;

        while (doorsId.hasNext()) {
            final Door door = getSiblingDoor(doorsId.nextInt());
            if (isNull(first)) {
                first = door;
            }

            if (door.isOpen() != open) {
                door.setOpen(open);
                door.broadcastStatusUpdate();
            }
        }
        if (nonNull(first) && open) {
            first.startAutoCloseTask(); // only one from group
        }
    }

    /**
     * Door notify child about open state change
     *
     * @param open true if opened
     */
    private void notifyChildEvent(boolean open) {
        final byte openThis = open ? getTemplate().getMasterDoorOpen() : getTemplate().getMasterDoorClose();
        if (openThis == 1) {
            openMe();
        } else if (openThis == -1) {
            closeMe();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getTemplate().getId() + "](" + getObjectId() + ")";
    }

    @Override
    public String getName() {
        return getTemplate().getName();
    }

    public int getX(int i) {
        return getTemplate().getNodeX()[i];
    }

    public int getY(int i) {
        return getTemplate().getNodeY()[i];
    }

    public int getZMin() {
        return getTemplate().getNodeZ();
    }

    public int getZMax() {
        return getTemplate().getNodeZ() + getTemplate().getHeight();
    }

    public int getMeshIndex() {
        return _meshindex;
    }

    public void setMeshIndex(int mesh) {
        _meshindex = mesh;
    }

    public int getEmitter() {
        return getTemplate().getEmmiter();
    }

    public boolean isWall() {
        return getTemplate().isWall();
    }

    public String getGroupName() {
        return getTemplate().getGroupName();
    }

    public int getChildId() {
        return getTemplate().getChildDoorId();
    }

    @Override
    public void reduceCurrentHp(double value, Creature attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect, DamageInfo.DamageType damageType) {
        if (isWall() && !isInInstance()) {
            if (!attacker.isServitor()) {
                return;
            }

            final Servitor servitor = (Servitor) attacker;
            if (servitor.getTemplate().getRace() != Race.SIEGE_WEAPON) {
                return;
            }
        }
        super.reduceCurrentHp(value, attacker, skill, isDOT, directlyToHp, critical, reflect, damageType);
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        final boolean isCastle = ((getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress());

        if (isCastle) {
            broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_CASTLE_GATE_HAS_BEEN_DESTROYED));
        } else {
            openMe();
        }

        return true;
    }

    @Override
    public void sendInfo(Player activeChar) {
        if (isVisibleFor(activeChar)) {
            if (getEmitter() > 0) {
                if (inverted) {
                    activeChar.sendPacket(new OnEventTrigger(getEmitter(), !open));
                } else {
                    activeChar.sendPacket(new OnEventTrigger(getEmitter(), open));
                }
            }
            activeChar.sendPacket(new StaticObject(this, activeChar.isGM()));
        }
    }

    @Override
    public void setTargetable(boolean targetable) {
        super.setTargetable(targetable);
        broadcastStatusUpdate();
    }

    public boolean checkCollision() {
        return getTemplate().isCheckCollision();
    }

    /**
     * All doors are stored at DoorTable except instance doors
     *
     * @param doorId
     * @return
     */
    private Door getSiblingDoor(int doorId) {
        final Instance inst = getInstanceWorld();
        return (inst != null) ? inst.getDoor(doorId) : DoorDataManager.getInstance().getDoor(doorId);
    }

    private void startAutoCloseTask() {
        if ((getTemplate().getCloseTime() < 0) || isOpenableByTime()) {
            return;
        }

        final Future<?> oldTask = _autoCloseTask;
        if (oldTask != null) {
            _autoCloseTask = null;
            oldTask.cancel(false);
        }
        _autoCloseTask = ThreadPool.schedule(new AutoClose(), getTemplate().getCloseTime() * 1000);
    }

    class AutoClose implements Runnable {
        @Override
        public void run() {
            if (open) {
                closeMe();
            }
        }
    }

    class TimerOpen implements Runnable {
        @Override
        public void run() {
            if (open) {
                closeMe();
            } else {
                openMe();
            }

            int delay = open ? getTemplate().getCloseTime() : getTemplate().getOpenTime();
            if (getTemplate().getRandomTime() > 0) {
                delay += Rnd.get(getTemplate().getRandomTime());
            }
            ThreadPool.schedule(this, delay * 1000);
        }
    }
}
