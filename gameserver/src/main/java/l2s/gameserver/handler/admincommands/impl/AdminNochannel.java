package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.AdminFunctions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Util;

public class AdminNochannel implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_nochannel,
		admin_nc
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanBanChat)
			return false;

		int banChatCount = 0;
		int penaltyCount = 0;
		int banChatCountPerDay = activeChar.getPlayerAccess().BanChatCountPerDay;
		if(banChatCountPerDay > 0)
		{
			String count = activeChar.getVar("banChatCount");
			if(count != null)
				banChatCount = Integer.parseInt(count);

			String penalty = activeChar.getVar("penaltyChatCount");
			if(penalty != null)
				penaltyCount = Integer.parseInt(penalty);

			long LastBanChatDayTime = 0;
			String time = activeChar.getVar("LastBanChatDayTime");
			if(time != null)
				LastBanChatDayTime = Long.parseLong(time);

			if(LastBanChatDayTime != 0)
			{
				if(System.currentTimeMillis() - LastBanChatDayTime < 1000 * 60 * 60 * 24)
				{
					if(banChatCount >= banChatCountPerDay)
					{
						activeChar.sendMessage("В сутки, вы можете выдать не более " + banChatCount + " банов чата.");
						return false;
					}
				}
				else
				{
					int bonus_mod = banChatCount / 10;
					bonus_mod = Math.max(1, bonus_mod);
					bonus_mod = 1; // Убрать, если потребуется сделать зависимость бонуса от количества банов
					if(activeChar.getPlayerAccess().BanChatBonusId > 0 && activeChar.getPlayerAccess().BanChatBonusCount > 0)
					{
						int add_count = activeChar.getPlayerAccess().BanChatBonusCount * bonus_mod;

						ItemTemplate item = ItemHolder.getInstance().getTemplate(activeChar.getPlayerAccess().BanChatBonusId);
						activeChar.sendMessage("Бонус за модерирование: " + add_count + " " + item.getName());

						if(penaltyCount > 0) // У модератора был штраф за нарушения
						{
							activeChar.sendMessage("Штраф за нарушения: " + penaltyCount + " " + item.getName());
							activeChar.setVar("penaltyChatCount", "" + Math.max(0, penaltyCount - add_count), -1); // Уменьшаем штраф
							add_count -= penaltyCount; // Вычитаем штраф из бонуса
						}

						if(add_count > 0)
							ItemFunctions.addItem(activeChar, activeChar.getPlayerAccess().BanChatBonusId, add_count, true);
					}
					activeChar.setVar("LastBanChatDayTime", "" + System.currentTimeMillis(), -1);
					activeChar.setVar("banChatCount", "0", -1);
					banChatCount = 0;
				}
			}
			else
				activeChar.setVar("LastBanChatDayTime", "" + System.currentTimeMillis(), -1);
		}

		switch(command)
		{
			case admin_nochannel:
			case admin_nc:
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //nochannel charName [period] [reason]");
					return false;
				}
				int timeval = 30; // if no args, then 30 min default.
				if(wordList.length > 2)
					try
					{
						timeval = Integer.parseInt(wordList[2]);
					}
					catch(Exception E)
					{
						timeval = 30;
					}

				String msg = AdminFunctions.banChat(activeChar, null, wordList[1], timeval, wordList.length > 3 ? Util.joinStrings(" ", wordList, 3) : null);
				activeChar.sendMessage(msg);

				if(banChatCountPerDay > -1 && msg.startsWith("Вы забанили чат"))
				{
					banChatCount++;
					activeChar.setVar("banChatCount", "" + banChatCount, -1);
					activeChar.sendMessage("У вас осталось " + (banChatCountPerDay - banChatCount) + " банов чата.");
				}
			}
		}
		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}