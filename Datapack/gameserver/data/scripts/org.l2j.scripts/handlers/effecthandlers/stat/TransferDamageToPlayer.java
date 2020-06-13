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
package handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;

import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Transfer Damage effect implementation.
 *
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class TransferDamageToPlayer extends AbstractStatAddEffect {

    private TransferDamageToPlayer(StatsSet params) {
        super(params, Stat.TRANSFER_DAMAGE_TO_PLAYER);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (isPlayable(effected) && isPlayer(effector)) {
            ((Playable) effected).setTransferDamageTo(null);
        }
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayable(effected) && isPlayer(effector)) {
            ((Playable) effected).setTransferDamageTo(effector.getActingPlayer());
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TransferDamageToPlayer(data);
        }

        @Override
        public String effectName() {
            return "TransferDamageToPlayer";
        }
    }
}