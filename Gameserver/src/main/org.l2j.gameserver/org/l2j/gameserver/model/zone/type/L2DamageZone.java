/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.zone.type;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.model.zone.AbstractZoneSettings;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.model.zone.TaskZoneSettings;

/**
 * A damage zone
 *
 * @author durgus
 */
public class L2DamageZone extends L2ZoneType {
    private int _damageHPPerSec;
    private int _damageMPPerSec;

    private int _castleId;
    private Castle _castle;

    private int _startTask;
    private int _reuseTask;

    public L2DamageZone(int id) {
        super(id);

        // Setup default damage
        _damageHPPerSec = 200;
        _damageMPPerSec = 0;

        // Setup default start / reuse time
        _startTask = 10;
        _reuseTask = 5000;

        // no castle by default
        _castleId = 0;
        _castle = null;

        setTargetType(InstanceType.L2Playable); // default only playabale
        AbstractZoneSettings settings = ZoneManager.getSettings(getName());
        if (settings == null) {
            settings = new TaskZoneSettings();
        }
        setSettings(settings);
    }

    @Override
    public TaskZoneSettings getSettings() {
        return (TaskZoneSettings) super.getSettings();
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("dmgHPSec")) {
            _damageHPPerSec = Integer.parseInt(value);
        } else if (name.equals("dmgMPSec")) {
            _damageMPPerSec = Integer.parseInt(value);
        } else if (name.equals("castleId")) {
            _castleId = Integer.parseInt(value);
        } else if (name.equalsIgnoreCase("initialDelay")) {
            _startTask = Integer.parseInt(value);
        } else if (name.equalsIgnoreCase("reuse")) {
            _reuseTask = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature character) {
        if ((getSettings().getTask() == null) && ((_damageHPPerSec != 0) || (_damageMPPerSec != 0))) {
            final Player player = character.getActingPlayer();
            if (getCastle() != null) // Castle zone
            {
                if (!(getCastle().getSiege().isInProgress() && (player != null) && (player.getSiegeState() != 2))) // Siege and no defender
                {
                    return;
                }
            }

            synchronized (this) {
                if (getSettings().getTask() == null) {
                    getSettings().setTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new ApplyDamage(this), _startTask, _reuseTask));
                }
            }
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (_characterList.isEmpty() && (getSettings().getTask() != null)) {
            getSettings().clear();
        }
    }

    protected int getHPDamagePerSecond() {
        return _damageHPPerSec;
    }

    protected int getMPDamagePerSecond() {
        return _damageMPPerSec;
    }

    protected Castle getCastle() {
        if ((_castleId > 0) && (_castle == null)) {
            _castle = CastleManager.getInstance().getCastleById(_castleId);
        }

        return _castle;
    }

    private final class ApplyDamage implements Runnable {
        private final L2DamageZone _dmgZone;
        private final Castle _castle;

        protected ApplyDamage(L2DamageZone zone) {
            _dmgZone = zone;
            _castle = zone.getCastle();
        }

        @Override
        public void run() {
            if (!isEnabled()) {
                return;
            }

            boolean siege = false;

            if (_castle != null) {
                siege = _castle.getSiege().isInProgress();
                // castle zones active only during siege
                if (!siege) {
                    _dmgZone.getSettings().clear();
                    return;
                }
            }

            for (Creature temp : _dmgZone.getCharactersInside()) {
                if ((temp != null) && !temp.isDead()) {
                    if (siege) {
                        // during siege defenders not affected
                        final Player player = temp.getActingPlayer();
                        if ((player != null) && player.isInSiege() && (player.getSiegeState() == 2)) {
                            continue;
                        }
                    }

                    final double multiplier = 1 + (temp.getStat().getValue(Stats.DAMAGE_ZONE_VULN, 0) / 100);

                    if (getHPDamagePerSecond() != 0) {
                        temp.reduceCurrentHp(_dmgZone.getHPDamagePerSecond() * multiplier, temp, null);
                    }
                    if (getMPDamagePerSecond() != 0) {
                        temp.reduceCurrentMp(_dmgZone.getMPDamagePerSecond() * multiplier);
                    }
                }
            }
        }
    }
}
