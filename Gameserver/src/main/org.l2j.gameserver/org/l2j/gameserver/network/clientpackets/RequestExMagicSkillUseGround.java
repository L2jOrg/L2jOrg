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
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;

/**
 * Fromat:(ch) dddddc
 *
 * @author -Wooden-
 */
public final class RequestExMagicSkillUseGround extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExMagicSkillUseGround.class);
    private int _x;
    private int _y;
    private int _z;
    private int _skillId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;

    @Override
    public void readImpl() {
        _x = readInt();
        _y = readInt();
        _z = readInt();
        _skillId = readInt();
        _ctrlPressed = readInt() != 0;
        _shiftPressed = readByte() != 0;
    }

    @Override
    public void runImpl() {
        // Get the current Player of the player
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        // Get the level of the used skill
        final int level = activeChar.getSkillLevel(_skillId);
        if (level <= 0) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Get the L2Skill template corresponding to the skillID received from the client
        final Skill skill = SkillEngine.getInstance().getSkill(_skillId, level);

        // Check the validity of the skill
        if (skill != null) {
            activeChar.setCurrentSkillWorldPosition(new Location(_x, _y, _z));

            // normally magicskilluse packet turns char client side but for these skills, it doesn't (even with correct target)
            activeChar.setHeading(calculateHeadingFrom(activeChar.getX(), activeChar.getY(), _x, _y));
            Broadcast.toKnownPlayers(activeChar, new ValidateLocation(activeChar));

            activeChar.useMagic(skill, null, _ctrlPressed, _shiftPressed);
        } else {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            LOGGER.warn("No skill found with id " + _skillId + " and level " + level + " !!");
        }
    }
}
