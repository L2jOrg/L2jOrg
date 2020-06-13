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
package org.l2j.gameserver.model.shuttle;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class ShuttleEngine implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShuttleEngine.class);

    private static final int DELAY = 15 * 1000;

    private final Shuttle _shuttle;
    private final Door _door1;
    private final Door _door2;
    private int _cycle = 0;

    public ShuttleEngine(ShuttleData data, Shuttle shuttle) {
        _shuttle = shuttle;
        _door1 = DoorDataManager.getInstance().getDoor(data.getDoors().get(0));
        _door2 = DoorDataManager.getInstance().getDoor(data.getDoors().get(1));
    }

    // TODO: Rework me..
    @Override
    public void run() {
        try {
            if (!_shuttle.isSpawned()) {
                return;
            }
            switch (_cycle) {
                case 0: {
                    _door1.openMe();
                    _door2.closeMe();
                    _shuttle.openDoor(0);
                    _shuttle.closeDoor(1);
                    _shuttle.broadcastShuttleInfo();
                    ThreadPool.schedule(this, DELAY);
                    break;
                }
                case 1: {
                    _door1.closeMe();
                    _door2.closeMe();
                    _shuttle.closeDoor(0);
                    _shuttle.closeDoor(1);
                    _shuttle.broadcastShuttleInfo();
                    ThreadPool.schedule(this, 1000);
                    break;
                }
                case 2: {
                    _shuttle.executePath(_shuttle.getShuttleData().getRoutes().get(0));
                    break;
                }
                case 3: {
                    _door1.closeMe();
                    _door2.openMe();
                    _shuttle.openDoor(1);
                    _shuttle.closeDoor(0);
                    _shuttle.broadcastShuttleInfo();
                    ThreadPool.schedule(this, DELAY);
                    break;
                }
                case 4: {
                    _door1.closeMe();
                    _door2.closeMe();
                    _shuttle.closeDoor(0);
                    _shuttle.closeDoor(1);
                    _shuttle.broadcastShuttleInfo();
                    ThreadPool.schedule(this, 1000);
                    break;
                }
                case 5: {
                    _shuttle.executePath(_shuttle.getShuttleData().getRoutes().get(1));
                    break;
                }
            }

            _cycle++;
            if (_cycle > 5) {
                _cycle = 0;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
    }
}
