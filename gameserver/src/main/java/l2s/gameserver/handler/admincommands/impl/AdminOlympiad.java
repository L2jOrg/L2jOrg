package l2s.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2s.gameserver.model.entity.olympiad.OlympiadManager;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;

public class AdminOlympiad implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_oly_save,
		admin_add_oly_points,
		admin_oly_start,
		admin_add_hero,
		admin_oly_stop
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		switch(command)
		{
			case admin_oly_save:
			{
				if(!Config.ENABLE_OLYMPIAD)
					return false;

				try
				{
					OlympiadDatabase.save();
				}
				catch(Exception e)
				{

				}
				activeChar.sendMessage("olympaid data saved.");
				break;
			}
			case admin_add_oly_points:
			{
				if(wordList.length < 3)
				{
					activeChar.sendMessage("Command syntax: //add_oly_points <char_name> <point_to_add>");
					activeChar.sendMessage("This command can be applied only for online players.");
					return false;
				}

				Player player = World.getPlayer(wordList[1]);
				if(player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					return false;
				}

				int pointToAdd;

				try
				{
					pointToAdd = Integer.parseInt(wordList[2]);
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Please specify integer value for olympiad points.");
					return false;
				}

				int curPoints = Olympiad.getParticipantPoints(player.getObjectId());
				Olympiad.manualSetParticipantPoints(player.getObjectId(), curPoints + pointToAdd);
				int newPoints = Olympiad.getParticipantPoints(player.getObjectId());

				activeChar.sendMessage("Added " + pointToAdd + " points to character " + player.getName());
				activeChar.sendMessage("Old points: " + curPoints + ", new points: " + newPoints);
				break;
			}
			case admin_oly_start:
			{
				Olympiad._manager = new OlympiadManager();
				Olympiad._inCompPeriod = true;

				new Thread(Olympiad._manager).start();

				Announcements.announceToAll(SystemMsg.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_GRAND_OLYMPIAD_MANAGER__BATTLES_IN_THE_GRAND_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE);
				break;
			}
			case admin_oly_stop:
			{
				Olympiad._inCompPeriod = false;
				Announcements.announceToAll(SystemMsg.MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM);
				try
				{
					OlympiadDatabase.save();
				}
				catch(Exception e)
				{

				}

				break;
			}
			case admin_add_hero:
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Command syntax: //add_hero <char_name>");
					activeChar.sendMessage("This command can be applied only for online players.");
					return false;
				}

				Player player = World.getPlayer(wordList[1]);
				if(player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					return false;
				}

				StatsSet hero = new StatsSet();
				hero.set(Hero.CLASS_ID, player.getBaseClassId());
				hero.set(Hero.CHAR_ID, player.getObjectId());
				hero.set(Hero.CHAR_NAME, player.getName());

				List<StatsSet> heroesToBe = new ArrayList<StatsSet>();
				heroesToBe.add(hero);

				Hero.getInstance().computeNewHeroes(heroesToBe);

				activeChar.sendMessage("Hero status added to player " + player.getName());
				break;
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