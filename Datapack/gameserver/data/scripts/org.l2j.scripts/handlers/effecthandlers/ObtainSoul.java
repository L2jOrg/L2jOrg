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
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter.SpawnEmitterType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public class ObtainSoul extends AbstractEffect {
    private final int power;
    private final boolean isShine;

    private ObtainSoul(StatsSet data) {
        power = data.getInt("power");
        isShine = data.getBoolean("is-shine");
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effected) || effected.isAlikeDead()) {
            return;
        }

        if(effected.hasAbnormalType(AbnormalType.KAMAEL_SPECIAL)) {
            return;
        }

        final var player = effected.getActingPlayer();
        if(isShine) {
            var souls = player.getShineSouls();
            if(souls + power >= 100) {
                player.setShineSouls((byte) 0);
                var level = player.getSkillLevel(CommonSkill.SHINE_MASTERY.getId());
                SkillCaster.triggerCast(player, player, SkillEngine.getInstance().getSkill(CommonSkill.SHINE_SIDE.getId(), level));
            } else {
                player.setShineSouls((byte) (souls + power));
            }
        } else {
            var souls = player.getShadowSouls();
            if(souls + power >= 100) {
                player.setShadowSouls((byte) 0);
                var level = player.getSkillLevel(CommonSkill.SHADOW_MASTERY.getId());
                SkillCaster.triggerCast(player, player, SkillEngine.getInstance().getSkill(CommonSkill.SHADOW_SIDE.getId(), level));
            } else {
                player.setShadowSouls((byte) (souls + power));
            }
        }
        player.sendPacket(new ExSpawnEmitter(player, isShine ? SpawnEmitterType.WHITE_SOUL : SpawnEmitterType.BLACK_SOUL));
        player.sendPacket(new EtcStatusUpdate(player));
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ObtainSoul(data);
        }

        @Override
        public String effectName() {
            return "obtain-soul";
        }
    }
}
