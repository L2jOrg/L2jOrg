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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Item Effect: Gives teleport bookmark slots to the owner.
 * @author Nik
 * @author JoeAlisson
 */
public final class AddTeleportBookmarkSlot extends AbstractEffect {

    private final int power;

    private AddTeleportBookmarkSlot(StatsSet params)
    {
        power = params.getInt("power", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effected)) {
            return;
        }

        final Player player = effected.getActingPlayer();
        player.setBookMarkSlot(player.getBookMarkSlot() + power);
        player.sendPacket(SystemMessageId.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new AddTeleportBookmarkSlot(data);
        }

        @Override
        public String effectName() {
            return "AddTeleportBookmarkSlot";
        }
    }


}
