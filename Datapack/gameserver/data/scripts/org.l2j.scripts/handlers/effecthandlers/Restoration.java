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
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PetItemList;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * Restoration effect implementation.
 * @author Zoey76, Mobius
 * @author JoeAlisson
 */
public final class Restoration extends AbstractEffect {

    private final int itemId;
    private final int itemCount;
    private final int itemEnchantmentLevel;

    private Restoration(StatsSet params) {
        itemId = params.getInt("item", 0);
        itemCount = params.getInt("count", 0);
        itemEnchantmentLevel = params.getInt("enchant", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayable(effected)){
            return;
        }

        if (itemId <= 0 || itemCount <= 0) {
            effected.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
            LOGGER.warn("effect with wrong item Id/count: {}/{}!", itemId, itemCount);
            return;
        }

        if (isPlayer(effected)) {
            final Item newItem = effected.getActingPlayer().addItem("Skill", itemId, itemCount, effector, true);
            if (nonNull(newItem) && itemEnchantmentLevel > 0) {
                newItem.setEnchantLevel(itemEnchantmentLevel);
            }
        }
        else if (isPet(effected)) {
            final Item newItem = effected.getInventory().addItem("Skill", itemId, itemCount, effected.getActingPlayer(), effector);
            if (itemEnchantmentLevel > 0) {
                newItem.setEnchantLevel(itemEnchantmentLevel);
            }
            effected.getActingPlayer().sendPacket(new PetItemList(effected.getInventory().getItems()));
        }
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.EXTRACT_ITEM;
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Restoration(data);
        }

        @Override
        public String effectName() {
            return "restoration";
        }
    }
}
