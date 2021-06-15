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
package org.l2j.gameserver.world.zone.type;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.TaskZoneSettings;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.model.DamageInfo.DamageType;
import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A damage zone
 *
 * @author durgus
 * @author JoeAlisson
 */
public class DamageZone extends Zone {
    private int hp;
    private int mp;

    private Castle castle;
    private int startTime;
    private int delay;

    private DamageZone(int id) {
        super(id);
        setSettings(new TaskZoneSettings());
    }

    @Override
    public TaskZoneSettings getSettings() {
        return (TaskZoneSettings) super.getSettings();
    }

    @Override
    protected boolean isAffected(Creature creature) {
        return super.isAffected(creature) && isPlayable(creature);
    }

    @Override
    protected void onEnter(Creature creature) {
        if ((getSettings().getTask() == null) && ((hp != 0) || (mp != 0))) {
            final Player player = creature.getActingPlayer();
            if (castle != null && (! (castle.getSiege().isInProgress() && (player != null) && (player.getSiegeState() != 2)))) {
                return;
            }

            synchronized (this) {
                if (getSettings().getTask() == null) {
                    getSettings().setTask(ThreadPool.scheduleAtFixedRate(new ApplyDamage(), startTime, delay));
                }
            }
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (creatures.isEmpty() && (getSettings().getTask() != null)) {
            getSettings().clear();
        }
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
            if (hp != 0) {
                creature.reduceCurrentHp(hp * multiplier, null, null, DamageType.ZONE);
            }
            if (mp != 0) {
                creature.reduceCurrentMp(mp * multiplier);
            }
        }
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var zone = new DamageZone(id);
            for(var node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                if(node.getNodeName().equals("attributes")) {
                    var attr = node.getAttributes();
                    zone.hp = reader.parseInt(attr,"hp");
                    zone.mp = reader.parseInt(attr, "mp");
                    zone.startTime = reader.parseInt(attr, "start-time");
                    zone.delay = reader.parseInt(attr, "delay");
                    zone.setEnabled(reader.parseBoolean(attr, "enabled"));

                    var castleId = reader.parseInt(attr, "castle-id");
                    zone.castle = CastleManager.getInstance().getCastleById(castleId);
                    break;
                }
            }
            return zone;
        }

        @Override
        public String type() {
            return "damage";
        }
    }
}
