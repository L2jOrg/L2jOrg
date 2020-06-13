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
package org.l2j.gameserver.world.zone.type;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import org.l2j.gameserver.world.zone.TaskZoneSettings;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNullElseGet;
import static org.l2j.gameserver.model.DamageInfo.DamageType;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A damage zone
 *
 * @author durgus
 * @author JoeAlisson
 */
public class DamageZone extends Zone {
    private int damageHPPerSec;
    private int damageMPPerSec;

    private int castleId;
    private Castle castle;

    private int startTask;
    private int reuseTask;

    public DamageZone(int id) {
        super(id);

        // Setup default damage
        damageHPPerSec = 200;

        // Setup default start / reuse time
        startTask = 10;
        reuseTask = 5000;

        setTargetType(InstanceType.Playable); // default only playabale
        AbstractZoneSettings settings = requireNonNullElseGet(ZoneManager.getSettings(getName()), TaskZoneSettings::new);
        setSettings(settings);
    }

    @Override
    public TaskZoneSettings getSettings() {
        return (TaskZoneSettings) super.getSettings();
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("dmgHPSec")) {
            damageHPPerSec = Integer.parseInt(value);
        } else if (name.equals("dmgMPSec")) {
            damageMPPerSec = Integer.parseInt(value);
        } else if (name.equals("castleId")) {
            castleId = Integer.parseInt(value);
        } else if (name.equalsIgnoreCase("initialDelay")) {
            startTask = Integer.parseInt(value);
        } else if (name.equalsIgnoreCase("reuse")) {
            reuseTask = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        if ((getSettings().getTask() == null) && ((damageHPPerSec != 0) || (damageMPPerSec != 0))) {
            final Player player = creature.getActingPlayer();
            if (getCastle() != null) // Castle zone
            {
                if (!(getCastle().getSiege().isInProgress() && (player != null) && (player.getSiegeState() != 2))) // Siege and no defender
                {
                    return;
                }
            }

            synchronized (this) {
                if (getSettings().getTask() == null) {
                    getSettings().setTask(ThreadPool.scheduleAtFixedRate(new ApplyDamage(), startTask, reuseTask));
                }
            }
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (creatures.isEmpty() && (getSettings().getTask() != null)) {
            getSettings().clear();
        }
    }

    protected Castle getCastle() {
        if ((castleId > 0) && (castle == null)) {
            castle = CastleManager.getInstance().getCastleById(castleId);
        }

        return castle;
    }

    private final class ApplyDamage implements Runnable {

        @Override
        public void run() {
            if (!isEnabled()) {
                return;
            }

            if (nonNull(castle)) {
                if (!castle.getSiege().isInProgress()) {
                    getSettings().clear();
                    return;
                }
            }

            forEachCreature(this::doDamage, this::canReceiveDamage);
        }

        private boolean canReceiveDamage(Creature creature) {
            if(creature.isDead()) {
                return false;
            }

            return !isPlayer(creature) || !((Player) creature).isInSiege() || ((Player) creature).getSiegeState() != 2;
        }

        private void doDamage(Creature creature) {
            final double multiplier = 1 + (creature.getStats().getValue(Stat.DAMAGE_ZONE_VULN, 0) / 100);
            if (damageHPPerSec != 0) {
                creature.reduceCurrentHp(damageHPPerSec * multiplier, null, null, DamageType.ZONE);
            }
            if (damageMPPerSec != 0) {
                creature.reduceCurrentMp(damageMPPerSec * multiplier);
            }
        }
    }
}
