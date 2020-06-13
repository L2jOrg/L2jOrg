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
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;

import java.util.function.Consumer;

import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * @author JoeAlisson
 */
public final class TriggerSkillByChargeShot extends AbstractEffect {
    private final SkillHolder skill;
    private final ShotType type;
    private final boolean forBeast;
    private final boolean blessed;

    private TriggerSkillByChargeShot(StatsSet data) {
        skill = new SkillHolder(data.getInt("skill"), data.getInt("power", 1));
        type = data.getEnum("type", ShotType.class);
        forBeast = data.getBoolean("for-beast");
        blessed = data.getBoolean("blessed");
    }

    private void onChargeShotEvent(OnPlayeableChargeShots event) {
        if(event.getShotType() != type || forBeast != isSummon(event.getPlayable()) || blessed != event.isBlessed()) {
            return;
        }
        var triggerSkill = skill.getSkill();
        var playable = event.getPlayable();
        var target = triggerSkill.getTarget(playable, false, false, false);
        SkillCaster.triggerCast(playable, (Creature) target, triggerSkill);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.removeListenerIf(EventType.ON_PLAYER_CHARGE_SHOTS, listener -> listener.getOwner() == this);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.addListener(new ConsumerEventListener(effected, EventType.ON_PLAYER_CHARGE_SHOTS, (Consumer<OnPlayeableChargeShots>) this::onChargeShotEvent, this));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TriggerSkillByChargeShot(data);
        }

        @Override
        public String effectName() {
            return "trigger-skill-by-charge-shot";
        }
    }
}
