package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
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
