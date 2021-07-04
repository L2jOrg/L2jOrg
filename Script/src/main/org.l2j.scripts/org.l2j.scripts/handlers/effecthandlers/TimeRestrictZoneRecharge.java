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
package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.network.serverpackets.timedzone.TimeRestrictFieldList;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.type.TimeRestrictZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author JoeAlisson
 */
public class TimeRestrictZoneRecharge extends AbstractEffect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeRestrictZoneRecharge.class);

    private final int zoneId;
    private final int time;

    private TimeRestrictZoneRecharge(int zoneId, int time) {
        this.zoneId = zoneId;
        this.time = time * 60;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        var zone = ZoneEngine.getInstance().getZoneById(zoneId, TimeRestrictZone.class);
        if(zone == null) {
            LOGGER.warn("Timed Zone with id {} not found.", zoneId);
            return;
        }

        if(effected instanceof Player player) {
            var info = zone.getPlayerZoneInfo(player);
            info.rechargeTime(time, zone.getRechargeTime());
            player.sendPacket(new TimeRestrictFieldList());
        }
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TimeRestrictZoneRecharge(data.getInt("zone"), data.getInt("power"));
        }

        @Override
        public String effectName() {
            return "time-restrict-zone-recharge";
        }
    }
}
