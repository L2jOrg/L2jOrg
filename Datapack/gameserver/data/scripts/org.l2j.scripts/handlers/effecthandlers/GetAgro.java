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
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.world.World;

import java.util.Set;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Get Agro effect implementation.
 * @author Adry_85, Mobius
 * @author JoeAlisson
 */
public final class GetAgro extends AbstractEffect {
    private GetAgro() {
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.AGGRESSION;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (isAttackable(effected)) {
            effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, effector);

            // Monsters from the same clan should assist.
            final NpcTemplate template = ((Attackable) effected).getTemplate();
            final Set<Integer> clans = template.getClans();
            if (nonNull(clans)) {
                World.getInstance().forEachVisibleObjectInRange(effected, Attackable.class, template.getClanHelpRange(), attackable -> receiveHate(attackable, effector), nearby -> canReceiveHate(nearby, clans));
            }
        }
    }

    private boolean canReceiveHate(Attackable nearby, Set<Integer> clans) {
        return !nearby.isMovementDisabled() && nearby.getTemplate().isClan(clans);
    }

    private void receiveHate(Attackable attackable, Creature effector) {
        attackable.addDamageHate(effector, 1, 200);
        attackable.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, effector);
    }

    public static class Factory implements SkillEffectFactory {
        private static final GetAgro INSTANCE = new GetAgro();
        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "GetAgro";
        }
    }
}
