package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.mobius.gameserver.util.Broadcast;
import org.l2j.gameserver.mobius.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Fromat:(ch) dddddc
 * @author -Wooden-
 */
public final class RequestExMagicSkillUseGround extends IClientIncomingPacket
{

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExMagicSkillUseGround.class);
    private int _x;
    private int _y;
    private int _z;
    private int _skillId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _x = packet.getInt();
        _y = packet.getInt();
        _z = packet.getInt();
        _skillId = packet.getInt();
        _ctrlPressed = packet.getInt() != 0;
        _shiftPressed = packet.get() != 0;
    }

    @Override
    public void runImpl()
    {
        // Get the current L2PcInstance of the player
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        // Get the level of the used skill
        final int level = activeChar.getSkillLevel(_skillId);
        if (level <= 0)
        {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Get the L2Skill template corresponding to the skillID received from the client
        final Skill skill = SkillData.getInstance().getSkill(_skillId, level);

        // Check the validity of the skill
        if (skill != null)
        {
            activeChar.setCurrentSkillWorldPosition(new Location(_x, _y, _z));

            // normally magicskilluse packet turns char client side but for these skills, it doesn't (even with correct target)
            activeChar.setHeading(Util.calculateHeadingFrom(activeChar.getX(), activeChar.getY(), _x, _y));
            Broadcast.toKnownPlayers(activeChar, new ValidateLocation(activeChar));

            activeChar.useMagic(skill, null, _ctrlPressed, _shiftPressed);
        }
        else
        {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            LOGGER.warn("No skill found with id " + _skillId + " and level " + level + " !!");
        }
    }
}
