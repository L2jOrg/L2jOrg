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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.timedzone.TimeRestrictZoneEngine;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.type.TimeRestrictZone;

/**
 * @author JoeAlisson
 */
public class ConditionRestrictTimeZoneRecharge extends Condition {

    private final int zoneId;

    public ConditionRestrictTimeZoneRecharge(int zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        var zone = ZoneEngine.getInstance().getZoneById(zoneId, TimeRestrictZone.class);
        if(zone == null || !(effected instanceof Player player)) {
            return false;
        }

        var info = TimeRestrictZoneEngine.getInstance().getTimeRestrictZoneInfo(player, zone);
        return info.getRechargedTime() < zone.getRechargeTime();
    }
}
