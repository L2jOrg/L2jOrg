package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.concurrent.ScheduledFuture;

/**
 * @author DrHouse
 */
public class DropProtection implements Runnable {
    private static final long PROTECTED_MILLIS_TIME = 15000;
    private volatile boolean _isProtected = false;
    private L2Character _owner = null;
    private ScheduledFuture<?> _task = null;

    @Override
    public synchronized void run() {
        _isProtected = false;
        _owner = null;
        _task = null;
    }

    public boolean isProtected() {
        return _isProtected;
    }

    public L2Character getOwner() {
        return _owner;
    }

    public synchronized boolean tryPickUp(Player actor) {
        if (!_isProtected) {
            return true;
        }

        if (_owner == actor) {
            return true;
        }

        return (_owner.getParty() != null) && (_owner.getParty() == actor.getParty());

        /*
         * if (_owner.getClan() != null && _owner.getClan() == actor.getClan()) return true;
         */

    }

    public boolean tryPickUp(Pet pet) {
        return tryPickUp(pet.getOwner());
    }

    public synchronized void unprotect() {
        if (_task != null) {
            _task.cancel(false);
        }
        _isProtected = false;
        _owner = null;
        _task = null;
    }

    public synchronized void protect(L2Character character) {
        unprotect();

        _isProtected = true;
        _owner = character;

        if (_owner == null) {
            throw new NullPointerException("Trying to protect dropped item to null owner");
        }

        _task = ThreadPoolManager.getInstance().schedule(this, PROTECTED_MILLIS_TIME);
    }
}
