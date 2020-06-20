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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestMagicSkillUse extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMagicSkillUse.class);
    private int _magicId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;

    @Override
    public void readImpl() {
        _magicId = readInt(); // Identifier of the used skill
        _ctrlPressed = readInt() != 0; // True if it's a ForceAttack : Ctrl pressed
        _shiftPressed = readByte() != 0; // True if Shift pressed
    }

    @Override
    public void runImpl() {
        // Get the current Player of the player
        final Player player = client.getPlayer();
        // Get the level of the used skill
        Skill skill = player.getKnownSkill(_magicId);
        if (skill == null) {
            if ((_magicId == CommonSkill.HAIR_ACCESSORY_SET.getId()) //
                    || ((_magicId > 1565) && (_magicId < 1570))) // subClass change SkillTree
            {
                skill = SkillEngine.getInstance().getSkill(_magicId, 1);
            } else {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                if (_magicId > 0) {
                    LOGGER.warn("Skill Id {} not found in player: {}", _magicId, player);
                }
                return;
            }
        }

        // Skill is blocked from player use.
        if (skill.isBlockActionUseSkill()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.onActionRequest();
        player.useMagic(skill, null, _ctrlPressed, _shiftPressed);
    }
}
