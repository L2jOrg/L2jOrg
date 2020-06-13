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

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * Open Chest effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class OpenChest extends AbstractEffect {

    private OpenChest() {
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!(effected instanceof Chest)) {
            return;
        }

        final Player player = effector.getActingPlayer();
        final Chest chest = (Chest) effected;

        if (chest.isDead() || (player.getInstanceWorld() != chest.getInstanceWorld())) {
            return;
        }

        if (((player.getLevel() <= 77) && (Math.abs(chest.getLevel() - player.getLevel()) <= 6)) || ((player.getLevel() >= 78) && (Math.abs(chest.getLevel() - player.getLevel()) <= 5))) {
            player.broadcastSocialAction(3);
            chest.setSpecialDrop();
            chest.setMustRewardExpSp(false);
            chest.reduceCurrentHp(chest.getMaxHp(), player, skill, DamageInfo.DamageType.OTHER);
        } else {
            player.broadcastSocialAction(13);
            chest.addDamageHate(player, 0, 1);
            chest.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
        }
    }

    public static class Factory implements SkillEffectFactory {

        private static final OpenChest INSTANCE = new OpenChest();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "OpenChest";
        }
    }
}
