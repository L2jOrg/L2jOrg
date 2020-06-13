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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.item.instance.Item;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Take Castle effect implementation.
 * @author Adry_85, St3eT
 * @author JoeAlisson
 */
public final class TakeCastle extends AbstractEffect {
    private final CastleSide side;

    private TakeCastle(StatsSet params)
    {
        side = params.getEnum("side", CastleSide.class);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effector)) {
            return;
        }

        final Castle castle = CastleManager.getInstance().getCastle(effector);
        castle.engrave(effector.getClan(), effected, side);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TakeCastle(data);
        }

        @Override
        public String effectName() {
            return "take-castle";
        }
    }
}
