package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.PetitionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SayPacket2;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.GmListTable;

/**
 * <p>Format: (c) d
 * <ul>
 * <li>d: Unknown</li>
 * </ul></p>
 *
 * @author n0nam3
 */
public final class RequestPetitionCancel extends L2GameClientPacket
{
	//private int _unknown;

	@Override
	protected void readImpl()
	{
		//_unknown = readD(); This is pretty much a trigger packet.
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(PetitionManager.getInstance().isPlayerInConsultation(activeChar))
		{
			if(activeChar.isGM())
				PetitionManager.getInstance().endActivePetition(activeChar);
			else
				activeChar.sendPacket(SystemMsg.YOUR_PETITION_IS_BEING_PROCESSED);
		}
		else if(PetitionManager.getInstance().isPlayerPetitionPending(activeChar))
		{
			if(PetitionManager.getInstance().cancelActivePetition(activeChar))
			{
				int numRemaining = Config.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar);

				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.THE_PETITION_WAS_CANCELED_YOU_MAY_SUBMIT_S1_MORE_PETITIONS_TODAY).addInteger(numRemaining));

				// Notify all GMs that the player's pending petition has been cancelled.
				String msgContent = activeChar.getName() + " has canceled a pending petition.";
				GmListTable.broadcastToGMs(new SayPacket2(activeChar.getObjectId(), ChatType.HERO_VOICE, "Petition System", msgContent));
			}
			else
				activeChar.sendPacket(SystemMsg.FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER);
		}
		else
			activeChar.sendPacket(SystemMsg.YOU_HAVE_NOT_SUBMITTED_A_PETITION);
	}
}
