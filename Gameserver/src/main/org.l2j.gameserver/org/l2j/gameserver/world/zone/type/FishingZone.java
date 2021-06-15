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
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.fishing.ExAutoFishAvailable;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import java.util.concurrent.ScheduledFuture;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A fishing zone
 *
 * @author durgus
 * @author JoeAlisson
 */
public class FishingZone extends Zone {

    private final Object taskLocker = new Object();
    private ScheduledFuture<?> task;
    public FishingZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (Config.ALLOW_FISHING && creature instanceof Player player && !player.isInsideZone(ZoneType.FISHING)) {
            player.setInsideZone(ZoneType.FISHING, true);
            checkFishing(player);

            synchronized (taskLocker) {
                if(task == null) {
                    task = ThreadPool.scheduleAtFixedRate(new FishingTask(), 1500, 1500);
                }
            }
        }
    }

    private void checkFishing(Player player) {
        var fishing = player.getFishing();
        if(fishing.canFish() && !fishing.isFishing()) {
            player.sendPacket(ExAutoFishAvailable.YES);
        } else  {
            player.sendPacket(ExAutoFishAvailable.NO);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.FISHING, false);
            creature.sendPacket(ExAutoFishAvailable.NO);

            synchronized (taskLocker) {
                if(isEmpty() && task != null) {
                    task.cancel(false);
                    task = null;
                }
            }
        }
    }

    private class FishingTask implements Runnable {
        @Override
        public void run() {
            forEachPlayer(FishingZone.this::checkFishing);
        }
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            return new FishingZone(id);
        }

        @Override
        public String type() {
            return "fishing";
        }
    }
}
