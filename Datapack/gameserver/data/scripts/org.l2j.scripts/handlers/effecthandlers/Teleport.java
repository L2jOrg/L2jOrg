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
package handlers.effecthandlers;

import org.l2j.gameserver.data.xml.impl.TeleportEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.request.TeleportRequest;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Teleport effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class Teleport extends AbstractEffect {
    private final Location loc;

    private Teleport(StatsSet params) {
        if(params.getBoolean("use-request")) {
            loc = null;
        } else {
            loc = new Location(params.getInt("x", 0), params.getInt("y", 0), params.getInt("z", 0));
        }
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.TELEPORT;
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if(isNull(loc)) {
            var player = effected.getActingPlayer();
            var request = player.getRequest(TeleportRequest.class);
            if(nonNull(request)) {
                player.removeRequest(TeleportRequest.class);
                TeleportEngine.getInstance().getInfo(request.getTeleportId()).ifPresent(loc -> effected.teleToLocation(loc.getLocation(), true, null));
            }
        } else {
            effected.teleToLocation(loc, true, null);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Teleport(data);
        }

        @Override
        public String effectName() {
            return "teleport";
        }
    }
}
