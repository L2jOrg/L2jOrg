package org.l2j.gameserver.model.shuttle;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.data.xml.impl.DoorData;
import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.model.actor.instance.L2ShuttleInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class L2ShuttleEngine implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(L2ShuttleEngine.class);

    private static final int DELAY = 15 * 1000;

    private final L2ShuttleInstance _shuttle;
    private final L2DoorInstance _door1;
    private final L2DoorInstance _door2;
    private int _cycle = 0;

    public L2ShuttleEngine(L2ShuttleData data, L2ShuttleInstance shuttle) {
        _shuttle = shuttle;
        _door1 = DoorData.getInstance().getDoor(data.getDoors().get(0));
        _door2 = DoorData.getInstance().getDoor(data.getDoors().get(1));
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
                    ThreadPoolManager.getInstance().schedule(this, DELAY);
                    break;
                }
                case 1: {
                    _door1.closeMe();
                    _door2.closeMe();
                    _shuttle.closeDoor(0);
                    _shuttle.closeDoor(1);
                    _shuttle.broadcastShuttleInfo();
                    ThreadPoolManager.getInstance().schedule(this, 1000);
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
                    ThreadPoolManager.getInstance().schedule(this, DELAY);
                    break;
                }
                case 4: {
                    _door1.closeMe();
                    _door2.closeMe();
                    _shuttle.closeDoor(0);
                    _shuttle.closeDoor(1);
                    _shuttle.broadcastShuttleInfo();
                    ThreadPoolManager.getInstance().schedule(this, 1000);
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
