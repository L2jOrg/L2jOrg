package l2s.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.ItemFunctions;

public class AdminGiveAll implements IAdminCommandHandler
{
	private static List<String> _l = new ArrayList<String>();
	enum Commands
	{
		admin_giveall
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		if(wordList.length >= 3)
		{
			int _id = 0;
			int _count = 0;
			try
			{
				_id = Integer.parseInt(wordList[1]);
				_count = Integer.parseInt(wordList[2]);	
			}
			catch(NumberFormatException e)
			{
				activeChar.sendMessage("only numbers");
				return false;
			}
			
			for(Player player : GameObjectsStorage.getPlayers())
			{
				if(player == null)
					continue;
				if(!checkPlayersIP(player))
					continue;
				ItemFunctions.addItem(player, _id, _count);
				player.sendMessage("You have been rewarded!");
			}
			_l.clear();
		}
		else
		{
			activeChar.sendMessage("use: //giveall itemId count");
			return false;
		}	
		return true;
	}
	
	private static boolean checkPlayersIP(Player player)
	{
		if(player == null)
			return false;
			
		if(_l.contains(player.getIP()))
			return false;
			
		_l.add(player.getIP());
		
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}