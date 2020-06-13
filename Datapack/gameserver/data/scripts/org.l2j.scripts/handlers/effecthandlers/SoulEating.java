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
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter.SpawnEmitterType;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Soul Eating effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class SoulEating extends AbstractEffect {
    private final int expNeeded;
    private final int power;

    private SoulEating(StatsSet params) {
        expNeeded = params.getInt("experience");
        power = params.getInt("power");
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayer(effected)) {
            effected.addListener(new ConsumerEventListener(effected, EventType.ON_PLAYABLE_EXP_CHANGED, (OnPlayableExpChanged event) -> onExperienceReceived(event.getPlayable(), (event.getNewExp() - event.getOldExp())), this));
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (isPlayer(effected)) {
            effected.removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
        }
    }

    @Override
    public void pump(Creature effected, Skill skill)
    {
        effected.getStats().mergeAdd(Stat.MAX_SOULS, power);
    }

    private void onExperienceReceived(Playable playable, long exp) {
        // TODO: Verify logic.
        if (isPlayer(playable) && (exp >= expNeeded)) {
            final Player player = playable.getActingPlayer();
            final int maxSouls = (int) player.getStats().getValue(Stat.MAX_SOULS, 0);

            if (player.getChargedSouls() >= maxSouls) {
                playable.sendPacket(SystemMessageId.SOUL_CANNOT_BE_ABSORBED_ANYMORE);
                return;
            }

            player.increaseSouls(1);

            if (isNpc(player.getTarget())) {
                final Npc npc = (Npc) playable.getTarget();
                player.broadcastPacket(new ExSpawnEmitter(player, npc, SpawnEmitterType.BLUE_SOUL_EATEN), 500);
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new SoulEating(data);
        }

        @Override
        public String effectName() {
            return "soul-eating";
        }
    }
}
