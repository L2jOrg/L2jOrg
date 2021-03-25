/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;

public final class RequestMagicSkillUse extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMagicSkillUse.class);
    private int skillId;
    private boolean forceAttack;
    private boolean dontMove;

    @Override
    public void readImpl() {
        skillId = readInt();
        forceAttack = readIntAsBoolean();
        dontMove = readBoolean();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        Skill skill = player.getKnownSkill(skillId);
        if (isNull(skill)) {
            if (skillId == CommonSkill.HAIR_ACCESSORY_SET.getId()) {
                skill = SkillEngine.getInstance().getSkill(skillId, 1);
            } else {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                LOGGER.warn("Skill Id {} not found in player: {}", skillId, player);
                return;
            }
        }

        if (skill.isBlockActionUseSkill()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.onActionRequest();
        player.useSkill(skill, null, forceAttack, dontMove);
    }
}
