package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.actor.stat.ControllableAirShipStats;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.DeleteObject;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.concurrent.Future;

public class ControllableAirShip extends AirShip {
    private static final int HELM = 13556;
    private static final int LOW_FUEL = 40;
    private final int _ownerId;
    private int _fuel = 0;
    private int _maxFuel = 0;
    private int _helmId;
    private Player _captain = null;

    private Future<?> _consumeFuelTask;
    private Future<?> _checkTask;

    public ControllableAirShip(CreatureTemplate template, int ownerId) {
        super(template);
        setInstanceType(InstanceType.L2ControllableAirShipInstance);
        _ownerId = ownerId;
        _helmId = IdFactory.getInstance().getNextId(); // not forget to release !
    }

    @Override
    public ControllableAirShipStats getStats() {
        return (ControllableAirShipStats) super.getStats();
    }

    @Override
    public void initCharStat() {
        setStat(new ControllableAirShipStats(this));
    }

    @Override
    public boolean canBeControlled() {
        return super.canBeControlled() && !isInDock();
    }

    @Override
    public boolean isOwner(Player player) {
        if (_ownerId == 0) {
            return false;
        }

        return (player.getClanId() == _ownerId) || (player.getObjectId() == _ownerId);
    }

    @Override
    public int getOwnerId() {
        return _ownerId;
    }

    @Override
    public boolean isCaptain(Player player) {
        return (_captain != null) && (player == _captain);
    }

    @Override
    public int getCaptainId() {
        return _captain != null ? _captain.getObjectId() : 0;
    }

    @Override
    public int getHelmObjectId() {
        return _helmId;
    }

    @Override
    public int getHelmItemId() {
        return HELM;
    }

    @Override
    public boolean setCaptain(Player player) {
        if (player == null) {
            _captain = null;
        } else {
            if ((_captain == null) && (player.getAirShip() == this)) {
                final int x = player.getInVehiclePosition().getX() - 0x16e;
                final int y = player.getInVehiclePosition().getY();
                final int z = player.getInVehiclePosition().getZ() - 0x6b;
                if (((x * x) + (y * y) + (z * z)) > 2500) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR);
                    return false;
                }
                // TODO: Missing message ID: 2739 Message: You cannot control the helm because you do not meet the requirements.
                else if (player.isInCombat()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_BATTLE);
                    return false;
                } else if (player.isSitting()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_SITTING_POSITION);
                    return false;
                } else if (player.hasBlockActions() && player.hasAbnormalType(AbnormalType.PARALYZE)) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_YOU_ARE_PETRIFIED);
                    return false;
                } else if (player.isCursedWeaponEquipped()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_A_CURSED_WEAPON_IS_EQUIPPED);
                    return false;
                } else if (player.isFishing()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_FISHING);
                    return false;
                } else if (player.isDead() || player.isFakeDeath()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHEN_YOU_ARE_DEAD);
                    return false;
                } else if (player.isCastingNow()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_USING_A_SKILL);
                    return false;
                } else if (player.isTransformed()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_TRANSFORMED);
                    return false;
                } else if (player.isCombatFlagEquipped()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_HOLDING_A_FLAG);
                    return false;
                } else if (player.isInDuel()) {
                    player.sendPacket(SystemMessageId.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_DUEL);
                    return false;
                }
                _captain = player;
                player.broadcastUserInfo();
            } else {
                return false;
            }
        }
        updateAbnormalVisualEffects();
        return true;
    }

    @Override
    public int getFuel() {
        return _fuel;
    }

    @Override
    public void setFuel(int f) {

        final int old = _fuel;
        if (f < 0) {
            _fuel = 0;
        } else if (f > _maxFuel) {
            _fuel = _maxFuel;
        } else {
            _fuel = f;
        }

        if ((_fuel == 0) && (old > 0)) {
            broadcastToPassengers(SystemMessage.getSystemMessage(SystemMessageId.THE_AIRSHIP_S_FUEL_EP_HAS_RUN_OUT_THE_AIRSHIP_S_SPEED_WILL_BE_GREATLY_DECREASED_IN_THIS_CONDITION));
        } else if (_fuel < LOW_FUEL) {
            broadcastToPassengers(SystemMessage.getSystemMessage(SystemMessageId.THE_AIRSHIP_S_FUEL_EP_WILL_SOON_RUN_OUT));
        }
    }

    @Override
    public int getMaxFuel() {
        return _maxFuel;
    }

    @Override
    public void setMaxFuel(int mf) {
        _maxFuel = mf;
    }

    @Override
    public void oustPlayer(Player player) {
        if (player == _captain) {
            setCaptain(null); // no need to broadcast userinfo here
        }

        super.oustPlayer(player);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        _checkTask = ThreadPool.scheduleAtFixedRate(new CheckTask(), 60000, 10000);
        _consumeFuelTask = ThreadPool.scheduleAtFixedRate(new ConsumeFuelTask(), 60000, 60000);
    }

    @Override
    public boolean deleteMe() {
        if (!super.deleteMe()) {
            return false;
        }

        if (_checkTask != null) {
            _checkTask.cancel(false);
            _checkTask = null;
        }
        if (_consumeFuelTask != null) {
            _consumeFuelTask.cancel(false);
            _consumeFuelTask = null;
        }

        broadcastPacket(new DeleteObject(_helmId));
        return true;
    }

    @Override
    public void refreshID() {
        super.refreshID();
        IdFactory.getInstance().releaseId(_helmId);
        _helmId = IdFactory.getInstance().getNextId();
    }

    @Override
    public void sendInfo(Player activeChar) {
        super.sendInfo(activeChar);
        if (_captain != null) {
            _captain.sendInfo(activeChar);
        }
    }

    protected final class ConsumeFuelTask implements Runnable {
        @Override
        public void run() {
            int fuel = getFuel();
            if (fuel > 0) {
                fuel -= 10;
                if (fuel < 0) {
                    fuel = 0;
                }

                setFuel(fuel);
                updateAbnormalVisualEffects();
            }
        }
    }

    protected final class CheckTask implements Runnable {
        @Override
        public void run() {
            if (isSpawned() && isEmpty() && !isInDock()) {
                // deleteMe() can't be called from CheckTask because task should not cancel itself
                ThreadPool.execute(new DecayTask());
            }
        }
    }

    protected final class DecayTask implements Runnable {
        @Override
        public void run() {
            deleteMe();
        }
    }
}
