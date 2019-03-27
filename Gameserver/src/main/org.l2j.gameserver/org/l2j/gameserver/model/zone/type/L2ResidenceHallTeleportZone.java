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

import org.l2j.commons.util.Rnd;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.concurrent.ScheduledFuture;

/**
 * Teleport residence zone for clan hall sieges
 *
 * @author BiggBoss
 */
public class L2ResidenceHallTeleportZone extends L2ResidenceTeleportZone {
    private int _id;
    private ScheduledFuture<?> _teleTask;

    /**
     * @param id
     */
    public L2ResidenceHallTeleportZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("residenceZoneId")) {
            _id = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    public int getResidenceZoneId() {
        return _id;
    }

    public synchronized void checkTeleporTask() {
        if ((_teleTask == null) || _teleTask.isDone()) {
            _teleTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(), 30000);
        }
    }

    protected class TeleportTask implements Runnable {
        @Override
        public void run() {
            final int index = getSpawns().size() > 1 ? Rnd.get(getSpawns().size()) : 0;
            final Location loc = getSpawns().get(index);
            if (loc == null) {
                throw new NullPointerException();
            }

            for (L2PcInstance pc : getPlayersInside()) {
                if (pc != null) {
                    pc.teleToLocation(loc, false);
                }
            }
        }
    }
}
