package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.concurrent.ScheduledFuture;

/**
 * @author DrHouse
 */
public class DropProtection implements Runnable {
    private static final long PROTECTED_MILLIS_TIME = 15000;
    private volatile boolean _isProtected = false;
    private Creature _owner = null;
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

    public Creature getOwner() {
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

    public synchronized void protect(Creature character) {
        unprotect();

        _isProtected = true;
        _owner = character;

        if (_owner == null) {
            throw new NullPointerException("Trying to protect dropped item to null owner");
        }

        _task = ThreadPool.schedule(this, PROTECTED_MILLIS_TIME);
    }
}
