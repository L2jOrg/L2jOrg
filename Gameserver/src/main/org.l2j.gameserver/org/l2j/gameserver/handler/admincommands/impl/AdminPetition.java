package org.l2j.gameserver.handler.admincommands.impl;

import org.l2j.commons.util.Converter;
import org.l2j.gameserver.handler.admincommands.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessage;

/**
 * 
 * @author n0nam3
 * @date 21/08/2010 1:00
 *
 */
public class AdminPetition implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_view_petitions,
		admin_view_petition,
		admin_accept_petition,
		admin_reject_petition,
		admin_reset_petitions,
		admin_force_peti
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		int petitionId = Converter.stringToInt(wordList.length > 1 ? wordList[1] : "-1", -1);
		Commands command = (Commands) comm;
		switch(command)
		{
			case admin_view_petitions:
				PetitionManager.getInstance().sendPendingPetitionList(activeChar);
				break;
			case admin_view_petition:
				PetitionManager.getInstance().viewPetition(activeChar, petitionId);
				break;
			case admin_accept_petition:
				if(petitionId < 0)
				{
					activeChar.sendMessage("Usage: //accept_petition id");
					return false;
				}
				if(PetitionManager.getInstance().isPlayerInConsultation(activeChar))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.ALREADY_APPLIED_FOR_PETITION));
					return true;
				}

				if(PetitionManager.getInstance().isPetitionInProcess(petitionId))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.PETITION_UNDER_PROCESS));
					return true;
				}

				if(!PetitionManager.getInstance().acceptPetition(activeChar, petitionId))
					activeChar.sendPacket(new SystemMessage(SystemMessage.NOT_UNDER_PETITION_CONSULTATION));

				break;
			case admin_reject_petition:
				if(petitionId < 0)
				{
					activeChar.sendMessage("Usage: //accept_petition id");
					return false;
				}
				if(!PetitionManager.getInstance().rejectPetition(activeChar, petitionId))
					activeChar.sendPacket(new SystemMessage(SystemMessage.FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER));
				PetitionManager.getInstance().sendPendingPetitionList(activeChar);

				break;
			case admin_reset_petitions:
				if(PetitionManager.getInstance().isPetitionInProcess())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.PETITION_UNDER_PROCESS));
					return false;
				}
				PetitionManager.getInstance().clearPendingPetitions();
				PetitionManager.getInstance().sendPendingPetitionList(activeChar);
				break;
			case admin_force_peti:
				if(fullString.length() < 11)
				{
					activeChar.sendMessage("Usage: //force_peti text");
					return false;
				}
				try
				{
					GameObject targetChar = activeChar.getTarget();
					if(targetChar == null || !(targetChar instanceof Player))
					{
						activeChar.sendPacket(SystemMsg.INVALID_TARGET);
						return false;
					}
					Player targetPlayer = (Player) targetChar;

					petitionId = PetitionManager.getInstance().submitPetition(targetPlayer, fullString.substring(10), 9);
					PetitionManager.getInstance().acceptPetition(activeChar, petitionId);
				}
				catch(StringIndexOutOfBoundsException e)
				{
					activeChar.sendMessage("Usage: //force_peti text");
					return false;
				}
				break;
		}
		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}