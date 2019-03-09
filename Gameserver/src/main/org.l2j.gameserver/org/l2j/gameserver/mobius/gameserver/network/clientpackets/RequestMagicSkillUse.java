package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public final class RequestMagicSkillUse extends IClientIncomingPacket
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMagicSkillUse.class);
	private int _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_magicId = packet.getInt(); // Identifier of the used skill
		_ctrlPressed = packet.getInt() != 0; // True if it's a ForceAttack : Ctrl pressed
		_shiftPressed = packet.get() != 0; // True if Shift pressed
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
		Skill skill = activeChar.getKnownSkill(_magicId);
		if (skill == null)
		{
			if ((_magicId == CommonSkill.HAIR_ACCESSORY_SET.getId()) //
				|| ((_magicId > 1565) && (_magicId < 1570))) // subClass change SkillTree
			{
				skill = SkillData.getInstance().getSkill(_magicId, 1);
			}
			else
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				if (_magicId > 0)
				{
					LOGGER.warn("Skill Id " + _magicId + " not found in player: " + activeChar);
				}
				return;
			}
		}
		
		// Skill is blocked from player use.
		if (skill.isBlockActionUseSkill())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Avoid Use of Skills in AirShip.
		if (activeChar.isInAirShip())
		{
			activeChar.sendPacket(SystemMessageId.THIS_ACTION_IS_PROHIBITED_WHILE_MOUNTED_OR_ON_AN_AIRSHIP);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		activeChar.useMagic(skill, null, _ctrlPressed, _shiftPressed);
	}
}
