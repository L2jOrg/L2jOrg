package handlers.usercommandhandlers;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Party Info user command.
 * @author Tempy
 */
public class PartyInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		81
	};
	
	@Override
	public boolean useUserCommand(int id, Player player)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		player.sendPacket(SystemMessageId.PARTY_INFORMATION);
		if (player.isInParty())
		{
			final Party party = player.getParty();
			switch (party.getDistributionType())
			{
				case FINDERS_KEEPERS:
				{
					player.sendPacket(SystemMessageId.LOOTING_METHOD_FINDERS_KEEPERS);
					break;
				}
				case RANDOM:
				{
					player.sendPacket(SystemMessageId.LOOTING_METHOD_RANDOM);
					break;
				}
				case RANDOM_INCLUDING_SPOIL:
				{
					player.sendPacket(SystemMessageId.LOOTING_METHOD_RANDOM_INCLUDING_SPOIL);
					break;
				}
				case BY_TURN:
				{
					player.sendPacket(SystemMessageId.LOOTING_METHOD_BY_TURN);
					break;
				}
				case BY_TURN_INCLUDING_SPOIL:
				{
					player.sendPacket(SystemMessageId.LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL);
					break;
				}
			}
			
			// Not used in Infinite Odissey
			// if (!party.isLeader(activeChar))
			// {
			// final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_LEADER_C1);
			// sm.addPcName(party.getLeader());
			// activeChar.sendPacket(sm);
			// }
		}
		player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
