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
package org.l2j.gameserver.engine.timedzone;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.data.TimeRestrictZoneInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.type.TimeRestrictZone;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JoeAlisson
 */
public class TimeRestrictZoneEngine {

    private final IntMap<Map<TimeRestrictZone, TimeRestrictZoneInfo>> timedRestrictZoneInfos = new CHashIntMap<>();

    private TimeRestrictZoneEngine() {
        // singleton
    }

    public TimeRestrictZoneInfo getTimeRestrictZoneInfo(Player player, TimeRestrictZone zone) {
        return timedRestrictZoneInfos.computeIfAbsent(player.getObjectId(), i -> new HashMap<>()).computeIfAbsent(zone, z -> TimeRestrictZoneInfo.init(zone, player.getObjectId()));
    }

    public static TimeRestrictZoneEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final TimeRestrictZoneEngine INSTANCE = new TimeRestrictZoneEngine();
    }
}
