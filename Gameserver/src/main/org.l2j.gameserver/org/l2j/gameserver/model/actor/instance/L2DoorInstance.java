package org.l2j.gameserver.model.actor.instance;


import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.L2CharacterAI;
import org.l2j.gameserver.ai.L2DoorAI;
import org.l2j.gameserver.data.xml.impl.DoorData;
import org.l2j.gameserver.enums.DoorOpenType;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.stat.DoorStat;
import org.l2j.gameserver.model.actor.status.DoorStatus;
import org.l2j.gameserver.model.actor.templates.L2DoorTemplate;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.DoorStatusUpdate;
import org.l2j.gameserver.network.serverpackets.OnEventTrigger;
import org.l2j.gameserver.network.serverpackets.StaticObject;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Future;

public final class L2DoorInstance extends L2Character {
    boolean _open = false;
    private boolean _isAttackableDoor = false;
    private boolean _isInverted = false;
    private int _meshindex = 1;
    private Future<?> _autoCloseTask;

    public L2DoorInstance(L2DoorTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2DoorInstance);
        setIsInvul(false);
        setLethalable(false);
        _open = template.isOpenByDefault();
        _isAttackableDoor = template.isAttackable();
        _isInverted = template.isInverted();
        super.setTargetable(template.isTargetable());

        if (isOpenableByTime()) {
            startTimerOpen();
        }
    }

    @Override
    protected L2CharacterAI initAI() {
        return new L2DoorAI(this);
    }

    @Override
    public void moveToLocation(int x, int y, int z, int offset) {
    }

    @Override
    public void stopMove(Location loc) {
    }

    @Override
    public void doAutoAttack(L2Character target) {
    }

    @Override
    public void doCast(Skill skill) {
    }

    private void startTimerOpen() {
        int delay = _open ? getTemplate().getOpenTime() : getTemplate().getCloseTime();
        if (getTemplate().getRandomTime() > 0) {
            delay += Rnd.get(getTemplate().getRandomTime());
        }
        ThreadPoolManager.getInstance().schedule(new TimerOpen(), delay * 1000);
    }

    @Override
    public L2DoorTemplate getTemplate() {
        return (L2DoorTemplate) super.getTemplate();
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
        setStat(new DoorStat(this));
    }

    @Override
    public DoorStat getStat() {
        return (DoorStat) super.getStat();
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

    /**
     * @return Returns the open.
     */
    public boolean isOpen() {
        return _open;
    }

    /**
     * @param open The open to set.
     */
    public void setOpen(boolean open) {
        _open = open;
        if (getChildId() > 0) {
            final L2DoorInstance sibling = getSiblingDoor(getChildId());
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
        return _isInverted;
    }

    public boolean getIsShowHp() {
        return getTemplate().isShowHp();
    }

    public int getDamage() {
        if ((getCastle() == null) && (getFort() == null)) {
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

    public final Fort getFort() {
        return FortManager.getInstance().getFort(this);
    }

    public boolean isEnemy() {
        if ((getCastle() != null) && (getCastle().getResidenceId() > 0) && getCastle().getZone().isActive() && getIsShowHp()) {
            return true;
        } else if ((getFort() != null) && (getFort().getResidenceId() > 0) && getFort().getZone().isActive() && getIsShowHp()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        // Doors can`t be attacked by NPCs
        if (!attacker.isPlayable()) {
            return false;
        } else if (_isAttackableDoor) {
            return true;
        } else if (!getIsShowHp()) {
            return false;
        }

        final L2PcInstance actingPlayer = attacker.getActingPlayer();

        // Attackable only during siege by everyone (not owner)
        final boolean isCastle = ((getCastle() != null) && (getCastle().getResidenceId() > 0) && getCastle().getZone().isActive());
        final boolean isFort = ((getFort() != null) && (getFort().getResidenceId() > 0) && getFort().getZone().isActive());

        if (isFort) {
            final L2Clan clan = actingPlayer.getClan();
            if ((clan != null) && (clan == getFort().getOwnerClan())) {
                return false;
            }
        } else if (isCastle) {
            final L2Clan clan = actingPlayer.getClan();
            if ((clan != null) && (clan.getId() == getCastle().getOwnerId())) {
                return false;
            }
        }
        return (isCastle || isFort);
    }

    /**
     * Return null.
     */
    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public void broadcastStatusUpdate(L2Character caster) {
        final Collection<L2PcInstance> knownPlayers = L2World.getInstance().getVisibleObjects(this, L2PcInstance.class);
        if ((knownPlayers == null) || knownPlayers.isEmpty()) {
            return;
        }

        final StaticObject su = new StaticObject(this, false);
        final StaticObject targetableSu = new StaticObject(this, true);
        final DoorStatusUpdate dsu = new DoorStatusUpdate(this);
        OnEventTrigger oe = null;
        if (getEmitter() > 0) {
            if (_isInverted) {
                oe = new OnEventTrigger(getEmitter(), !_open);
            } else {
                oe = new OnEventTrigger(getEmitter(), _open);
            }
        }

        for (L2PcInstance player : knownPlayers) {
            if ((player == null) || !isVisibleFor(player)) {
                continue;
            }

            if (player.isGM() || (((getCastle() != null) && (getCastle().getResidenceId() > 0)) || ((getFort() != null) && (getFort().getResidenceId() > 0)))) {
                player.sendPacket(targetableSu);
            } else {
                player.sendPacket(su);
            }

            player.sendPacket(dsu);
            if (oe != null) {
                player.sendPacket(oe);
            }
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
        final Set<Integer> set = DoorData.getInstance().getDoorsByGroup(groupName);
        L2DoorInstance first = null;
        for (Integer id : set) {
            final L2DoorInstance door = getSiblingDoor(id);
            if (first == null) {
                first = door;
            }

            if (door.isOpen() != open) {
                door.setOpen(open);
                door.broadcastStatusUpdate();
            }
        }
        if ((first != null) && open) {
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
    public void reduceCurrentHp(double value, L2Character attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect) {
        if (isWall() && !isInInstance()) {
            if (!attacker.isServitor()) {
                return;
            }

            final L2ServitorInstance servitor = (L2ServitorInstance) attacker;
            if (servitor.getTemplate().getRace() != Race.SIEGE_WEAPON) {
                return;
            }
        }
        super.reduceCurrentHp(value, attacker, skill, isDOT, directlyToHp, critical, reflect);
    }

    @Override
    public boolean doDie(L2Character killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        final boolean isFort = ((getFort() != null) && (getFort().getResidenceId() > 0) && getFort().getSiege().isInProgress());
        final boolean isCastle = ((getCastle() != null) && (getCastle().getResidenceId() > 0) && getCastle().getSiege().isInProgress());

        if (isFort || isCastle) {
            broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_CASTLE_GATE_HAS_BEEN_DESTROYED));
        } else {
            openMe();
        }

        return true;
    }

    @Override
    public void sendInfo(L2PcInstance activeChar) {
        if (isVisibleFor(activeChar)) {
            if (getEmitter() > 0) {
                if (_isInverted) {
                    activeChar.sendPacket(new OnEventTrigger(getEmitter(), !_open));
                } else {
                    activeChar.sendPacket(new OnEventTrigger(getEmitter(), _open));
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
    private L2DoorInstance getSiblingDoor(int doorId) {
        final Instance inst = getInstanceWorld();
        return (inst != null) ? inst.getDoor(doorId) : DoorData.getInstance().getDoor(doorId);
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
        _autoCloseTask = ThreadPoolManager.getInstance().schedule(new AutoClose(), getTemplate().getCloseTime() * 1000);
    }

    @Override
    public boolean isDoor() {
        return true;
    }

    class AutoClose implements Runnable {
        @Override
        public void run() {
            if (_open) {
                closeMe();
            }
        }
    }

    class TimerOpen implements Runnable {
        @Override
        public void run() {
            if (_open) {
                closeMe();
            } else {
                openMe();
            }

            int delay = _open ? getTemplate().getCloseTime() : getTemplate().getOpenTime();
            if (getTemplate().getRandomTime() > 0) {
                delay += Rnd.get(getTemplate().getRandomTime());
            }
            ThreadPoolManager.getInstance().schedule(this, delay * 1000);
        }
    }
}
