package handlers.admincommandhandlers;

import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class AdminLevel implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_level",
		"admin_set_level"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final WorldObject targetChar = activeChar.getTarget();
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken(); // Get actual command
		
		String val = "";
		if (st.countTokens() >= 1)
		{
			val = st.nextToken();
		}
		
		if (actualCommand.equalsIgnoreCase("admin_add_level"))
		{
			try
			{
				if (isPlayable(targetChar))
				{
					((Playable) targetChar).getStats().addLevel(Byte.parseByte(val));
				}
			}
			catch (NumberFormatException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Wrong Number Format");
			}
		}
		else if (actualCommand.equalsIgnoreCase("admin_set_level"))
		{
			final int maxLevel = LevelData.getInstance().getMaxLevel();
			try
			{
				if (!isPlayer(targetChar))
				{
					activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET); // incorrect target!
					return false;
				}
				final Player targetPlayer = (Player) targetChar;
				
				final byte lvl = Byte.parseByte(val);
				if ((lvl >= 1) && (lvl <= maxLevel))
				{
					targetPlayer.setExp(LevelData.getInstance().getExpForLevel(lvl));
					targetPlayer.getStats().setLevel(lvl);
					targetPlayer.setCurrentHpMp(targetPlayer.getMaxHp(), targetPlayer.getMaxMp());
					targetPlayer.setCurrentCp(targetPlayer.getMaxCp());
					targetPlayer.updateCharacteristicPoints();
					targetPlayer.broadcastUserInfo();
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "You must specify level between 1 and " + maxLevel + ".");
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "You must specify level between 1 and " + maxLevel + ".");
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
