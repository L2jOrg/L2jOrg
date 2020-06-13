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
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.Collection;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Sweeper effect implementation.
 * @author Zoey76
 * @author JoeAlisson
 */
public final class Sweeper extends AbstractEffect {

    private Sweeper() {
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effector) || !isAttackable(effected)) {
            return;
        }

        final Player player = effector.getActingPlayer();
        final Attackable monster = (Attackable) effected;
        if (!monster.checkSpoilOwner(player, false)) {
            return;
        }

        if (!player.getInventory().checkInventorySlotsAndWeight(monster.getSpoilLootItems(), false, false)) {
            return;
        }

        final Collection<ItemHolder> items = monster.takeSweep();
        if (nonNull(items)) {
            for (ItemHolder sweepedItem : items) {
                final Party party = player.getParty();
                if (party != null) {
                    party.distributeItem(player, sweepedItem, true, monster);
                } else {
                    player.addItem("Sweeper", sweepedItem, effected, true);
                }
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        private static final Sweeper INSTANCE = new Sweeper();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Sweeper";
        }
    }
}
