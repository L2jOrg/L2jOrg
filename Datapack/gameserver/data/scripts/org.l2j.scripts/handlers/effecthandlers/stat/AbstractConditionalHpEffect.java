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
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureHpChange;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

/**
 * @author Mobius
 */
abstract class AbstractConditionalHpEffect extends AbstractStatEffect {

    public final int hpPercent;
    private final Map<Creature, AtomicBoolean> updates = new ConcurrentHashMap<>();

    protected AbstractConditionalHpEffect(StatsSet params, Stat stat) {
        super(params, stat);
        hpPercent = params.getInt("hp-percent", 0);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isNull(skill)) {
            return;
        }

        if (hpPercent > 0 && !updates.containsKey(effected)) {
            updates.put(effected, new AtomicBoolean(canPump(effector, effected, skill)));
            effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_HP_CHANGE, (Consumer<OnCreatureHpChange>) this::onHpChange, this));
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (isNull(skill)) {
            return;
        }

        effected.removeListenerIf(listener -> listener.getOwner() == this);
        updates.remove(effected);
    }

    @Override
    public boolean canPump(Creature effector, Creature effected, Skill skill) {
        return hpPercent <= 0 || effected.getCurrentHpPercent() <= hpPercent;
    }

    private void onHpChange(OnCreatureHpChange event) {
        final Creature creature = event.getCreature();
        final AtomicBoolean update = updates.get(creature);
        if (isNull(update)) {
            return;
        }

        if (canPump(null, creature, null)) {
            if (update.get()) {
                update.set(false);
                creature.getStats().recalculateStats(true);
            }
        } else if (!update.get()) {
            update.set(true);
            creature.getStats().recalculateStats(true);
        }
    }
}