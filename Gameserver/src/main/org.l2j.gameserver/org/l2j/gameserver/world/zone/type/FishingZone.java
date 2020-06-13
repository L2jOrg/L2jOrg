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
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Fishing;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.fishing.ExAutoFishAvailable;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import java.lang.ref.WeakReference;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A fishing zone
 *
 * @author durgus
 */
public class FishingZone extends Zone {
    public FishingZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature) && (Config.ALLOW_FISHING || creature.canOverrideCond(PcCondOverride.ZONE_CONDITIONS)) && !creature.isInsideZone(ZoneType.FISHING)) {

            final WeakReference<Player> weakPlayer = new WeakReference<>(creature.getActingPlayer());

            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    final Player player = weakPlayer.get();

                    if (nonNull(player)) {

                        if (player.isInsideZone(ZoneType.FISHING)) {

                            final Fishing fishing = player.getFishing();

                            if (fishing.canFish() && !fishing.isFishing()) {
                                player.sendPacket( fishing.isAtValidLocation() ? ExAutoFishAvailable.YES : ExAutoFishAvailable.NO);
                            }
                            ThreadPool.schedule(this, 1500);
                        } else {
                            player.sendPacket(ExAutoFishAvailable.NO);
                        }
                    }
                }
            });

            creature.setInsideZone(ZoneType.FISHING, true);
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (isPlayer(character)) {
            character.setInsideZone(ZoneType.FISHING, false);
            character.sendPacket(ExAutoFishAvailable.NO);
        }
    }
}
