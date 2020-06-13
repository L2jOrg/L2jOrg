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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isMonster;

/**
 * Spoil effect implementation.
 * @author _drunk_, Ahmed, Zoey76
 * @author JoeAlisson
 */
public final class Spoil extends AbstractEffect {

    private Spoil() {
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
        final int lvlDifference = (effected.getLevel() - (skill.getMagicLevel() > 0 ? skill.getMagicLevel() : effector.getLevel()));
        final double lvlModifier = Math.pow(1.3, lvlDifference);
        float targetModifier = 1;

        if (isAttackable(effected) && !effected.isRaid() && !effected.isRaidMinion() && (effected.getLevel() >= Config.MIN_NPC_LVL_MAGIC_PENALTY) && (effector.getActingPlayer() != null) && ((effected.getLevel() - effector.getActingPlayer().getLevel()) >= 3)) {
            final int lvlDiff = effected.getLevel() - effector.getActingPlayer().getLevel() - 2;
            if (lvlDiff >= Config.NPC_SKILL_CHANCE_PENALTY.size()) {
                targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(Config.NPC_SKILL_CHANCE_PENALTY.size() - 1);
            } else {
                targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(lvlDiff);
            }
        }
        return Rnd.get(100) < (100 - Math.round((float) (lvlModifier * targetModifier)));
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isMonster(effected) || effected.isDead()) {
            effector.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        final Monster target = (Monster) effected;
        if (target.isSpoiled()) {
            effector.sendPacket(SystemMessageId.IT_HAS_ALREADY_BEEN_SPOILED);
            return;
        }

        target.setSpoilerObjectId(effector.getObjectId());
        effector.sendPacket(SystemMessageId.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
        target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, effector);
    }

    public static class Factory implements SkillEffectFactory {

        public static final Spoil INSTANCE = new Spoil();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Spoil";
        }
    }
}
