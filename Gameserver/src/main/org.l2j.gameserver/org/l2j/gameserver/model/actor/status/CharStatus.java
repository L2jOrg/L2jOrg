package org.l2j.gameserver.model.actor.status;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.OnCreatureHpChange;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;


public class CharStatus {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CharStatus.class);
    protected static final byte REGEN_FLAG_CP = 4;
    private static final byte REGEN_FLAG_HP = 1;
    private static final byte REGEN_FLAG_MP = 2;
    private final Creature _activeChar;
    protected byte _flagsRegenActive = 0;
    private double _currentHp = 0; // Current HP of the Creature
    private double _currentMp = 0; // Current MP of the Creature
    /**
     * Array containing all clients that need to be notified about hp/mp updates of the Creature
     */
    private Set<Creature> _StatusListener;
    private Future<?> _regTask;

    public CharStatus(Creature activeChar) {
        _activeChar = activeChar;
    }

    /**
     * Add the object to the list of Creature that must be informed of HP/MP updates of this Creature.<br>
     * <B><U>Concept</U>:</B><br>
     * Each Creature owns a list called <B>_statusListener</B> that contains all Player to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this Creature.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.<br>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>Target a PC or NPC</li>
     * <ul>
     *
     * @param object Creature to add to the listener
     */
    public final void addStatusListener(Creature object) {
        if (object == _activeChar) {
            return;
        }

        getStatusListener().add(object);
    }

    /**
     * Remove the object from the list of Creature that must be informed of HP/MP updates of this Creature.<br>
     * <B><U>Concept</U>:</B><br>
     * Each Creature owns a list called <B>_statusListener</B> that contains all Player to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this Creature.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.<br>
     * <B><U>Example of use </U>:</B>
     * <ul>
     * <li>Untarget a PC or NPC</li>
     * </ul>
     *
     * @param object Creature to add to the listener
     */
    public final void removeStatusListener(Creature object) {
        getStatusListener().remove(object);
    }

    /**
     * Return the list of Creature that must be informed of HP/MP updates of this Creature.<br>
     * <B><U>Concept</U>:</B><br>
     * Each Creature owns a list called <B>_statusListener</B> that contains all Player to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this Creature.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.
     *
     * @return The list of Creature to inform or null if empty
     */
    public final Set<Creature> getStatusListener() {
        if (_StatusListener == null) {
            _StatusListener = ConcurrentHashMap.newKeySet();
        }
        return _StatusListener;
    }

    // place holder, only PcStatus has CP
    public void reduceCp(int value) {
    }

    /**
     * Reduce the current HP of the Creature and launch the doDie Task if necessary.
     *
     * @param value
     * @param attacker
     */
    public void reduceHp(double value, Creature attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    public void reduceHp(double value, Creature attacker, boolean isHpConsumption) {
        reduceHp(value, attacker, true, false, isHpConsumption);
    }

    public void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption) {
        final Creature activeChar = _activeChar;
        if (activeChar.isDead()) {
            return;
        }

        // invul handling
        if (activeChar.isHpBlocked() && !(isDOT || isHPConsumption)) {
            return;
        }

        if (attacker != null) {
            final Player attackerPlayer = attacker.getActingPlayer();
            if ((attackerPlayer != null) && attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage()) {
                return;
            }
        }

        if (!isDOT && !isHPConsumption) {
            if (awake) {
                activeChar.stopEffectsOnDamage();
            }
            if (Formulas.calcStunBreak(activeChar)) {
                activeChar.stopStunning(true);
            }
            if (Formulas.calcRealTargetBreak()) {
                _activeChar.getEffectList().stopEffects(AbnormalType.REAL_TARGET);
            }
        }

        if (value > 0) {
            setCurrentHp(Math.max(_currentHp - value, activeChar.isUndying() ? 1 : 0));
        }

        if ((activeChar.getCurrentHp() < 0.5)) // Die
        {
            activeChar.doDie(attacker);
        }
    }

    public void reduceMp(double value) {
        setCurrentMp(Math.max(_currentMp - value, 0));
    }

    /**
     * Start the HP/MP/CP Regeneration task.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Calculate the regen task period</li>
     * <li>Launch the HP/MP/CP Regeneration task with Medium priority</li>
     * </ul>
     */
    public final synchronized void startHpMpRegeneration() {
        if ((_regTask == null) && !_activeChar.isDead()) {
            // Get the Regeneration period
            final int period = Formulas.getRegeneratePeriod(_activeChar);

            // Create the HP/MP/CP Regeneration task
            _regTask = ThreadPool.scheduleAtFixedRate(this::doRegeneration, period, period);
        }
    }

    /**
     * Stop the HP/MP/CP Regeneration task.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Set the RegenActive flag to False</li>
     * <li>Stop the HP/MP/CP Regeneration task</li>
     * </ul>
     */
    public final synchronized void stopHpMpRegeneration() {
        if (_regTask != null) {
            // Stop the HP/MP/CP Regeneration task
            _regTask.cancel(false);
            _regTask = null;

            // Set the RegenActive flag to false
            _flagsRegenActive = 0;
        }
    }

    // place holder, only PcStatus has CP
    public double getCurrentCp() {
        return 0;
    }

    // place holder, only PcStatus has CP
    public void setCurrentCp(double newCp) {
    }

    // place holder, only PcStatus has CP
    public void setCurrentCp(double newCp, boolean broadcastPacket) {
    }

    public final double getCurrentHp() {
        return _currentHp;
    }

    public final void setCurrentHp(double newHp) {
        setCurrentHp(newHp, true);
    }

    /**
     * Sets the current hp of this character.
     *
     * @param newHp           the new hp
     * @param broadcastPacket if true StatusUpdate packet will be broadcasted.
     * @return @{code true} if hp was changed, @{code false} otherwise.
     */
    public boolean setCurrentHp(double newHp, boolean broadcastPacket) {
        // Get the Max HP of the Creature
        final int oldHp = (int) _currentHp;
        final double maxHp = _activeChar.getStats().getMaxHp();

        synchronized (this) {
            if (_activeChar.isDead()) {
                return false;
            }

            if (newHp >= maxHp) {
                // Set the RegenActive flag to false
                _currentHp = maxHp;
                _flagsRegenActive &= ~REGEN_FLAG_HP;

                // Stop the HP/MP/CP Regeneration task
                if (_flagsRegenActive == 0) {
                    stopHpMpRegeneration();
                }
            } else {
                // Set the RegenActive flag to true
                _currentHp = newHp;
                _flagsRegenActive |= REGEN_FLAG_HP;

                // Start the HP/MP/CP Regeneration task with Medium priority
                startHpMpRegeneration();
            }
        }

        final boolean hpWasChanged = oldHp != _currentHp;

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform
        if (hpWasChanged) {
            if (broadcastPacket) {
                _activeChar.broadcastStatusUpdate();
            }
            EventDispatcher.getInstance().notifyEventAsync(new OnCreatureHpChange(getActiveChar(), oldHp, _currentHp), getActiveChar());
        }

        return hpWasChanged;
    }

    public final void setCurrentHpMp(double newHp, double newMp) {
        boolean hpOrMpWasChanged = setCurrentHp(newHp, false);
        hpOrMpWasChanged |= setCurrentMp(newMp, false);
        if (hpOrMpWasChanged) {
            _activeChar.broadcastStatusUpdate();
        }
    }

    public final double getCurrentMp() {
        return _currentMp;
    }

    public final void setCurrentMp(double newMp) {
        setCurrentMp(newMp, true);
    }

    /**
     * Sets the current mp of this character.
     *
     * @param newMp           the new mp
     * @param broadcastPacket if true StatusUpdate packet will be broadcasted.
     * @return @{code true} if mp was changed, @{code false} otherwise.
     */
    public final boolean setCurrentMp(double newMp, boolean broadcastPacket) {
        // Get the Max MP of the Creature
        final int currentMp = (int) _currentMp;
        final int maxMp = _activeChar.getStats().getMaxMp();

        synchronized (this) {
            if (_activeChar.isDead()) {
                return false;
            }

            if (newMp >= maxMp) {
                // Set the RegenActive flag to false
                _currentMp = maxMp;
                _flagsRegenActive &= ~REGEN_FLAG_MP;

                // Stop the HP/MP/CP Regeneration task
                if (_flagsRegenActive == 0) {
                    stopHpMpRegeneration();
                }
            } else {
                // Set the RegenActive flag to true
                _currentMp = newMp;
                _flagsRegenActive |= REGEN_FLAG_MP;

                // Start the HP/MP/CP Regeneration task with Medium priority
                startHpMpRegeneration();
            }
        }

        final boolean mpWasChanged = currentMp != _currentMp;

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform
        if (mpWasChanged && broadcastPacket) {
            _activeChar.broadcastStatusUpdate();
        }

        return mpWasChanged;
    }

    protected void doRegeneration() {
        // Modify the current HP/MP of the Creature and broadcast Server->Client packet StatusUpdate
        if (!_activeChar.isDead() && ((_currentHp < _activeChar.getMaxRecoverableHp()) || (_currentMp < _activeChar.getMaxRecoverableMp()))) {
            final double newHp = _currentHp + _activeChar.getStats().getValue(Stat.REGENERATE_HP_RATE);
            final double newMp = _currentMp + _activeChar.getStats().getValue(Stat.REGENERATE_MP_RATE);
            setCurrentHpMp(newHp, newMp);
        } else {
            stopHpMpRegeneration();
        }
    }

    public Creature getActiveChar() {
        return _activeChar;
    }
}
