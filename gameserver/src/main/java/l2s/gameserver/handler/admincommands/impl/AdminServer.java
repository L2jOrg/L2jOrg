package l2s.gameserver.handler.admincommands.impl;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2s.gameserver.ai.CharacterAI;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.WorldRegion;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * This class handles following admin commands: - help path = shows
 * admin/path file to char, should not be used by GM's directly
 */
public class AdminServer implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_server,
		admin_check_actor,
		admin_setvar,
		admin_set_ai_interval,
		admin_spawn2
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		switch(command)
		{
			case admin_server:
				try
				{
					String val = fullString.substring(13);
					showHelpPage(activeChar, val);
				}
				catch(StringIndexOutOfBoundsException e)
				{
					// case of empty filename
				}
				break;
			case admin_check_actor:
				GameObject obj = activeChar.getTarget();
				if(obj == null)
				{
					activeChar.sendMessage("target == null");
					return false;
				}

				if(!obj.isCreature())
				{
					activeChar.sendMessage("target is not a character");
					return false;
				}

				Creature target = (Creature) obj;
				CharacterAI ai = target.getAI();
				if(ai == null)
				{
					activeChar.sendMessage("ai == null");
					return false;
				}

				Creature actor = ai.getActor();
				if(actor == null)
				{
					activeChar.sendMessage("actor == null");
					return false;
				}

				activeChar.sendMessage("actor: " + actor);
				break;
			case admin_setvar:
				if(wordList.length != 3)
				{
					activeChar.sendMessage("Incorrect argument count!!!");
					return false;
				}
				ServerVariables.set(wordList[1], wordList[2]);
				activeChar.sendMessage("Value changed.");
				break;
			case admin_set_ai_interval:
				if(wordList.length != 2)
				{
					activeChar.sendMessage("Incorrect argument count!!!");
					return false;
				}
				int interval = Integer.parseInt(wordList[1]);
				int count = 0;
				int count2 = 0;
				for(final NpcInstance npc : GameObjectsStorage.getNpcs())
				{
					if(npc == null || npc instanceof RaidBossInstance)
						continue;
					final CharacterAI char_ai = npc.getAI();
					if(char_ai instanceof DefaultAI)
						try
						{
							final java.lang.reflect.Field field = DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
							field.setAccessible(true);
							field.set(char_ai, interval);

							if(char_ai.isActive())
							{
								char_ai.stopAITask();
								count++;
								WorldRegion region = npc.getCurrentRegion();
								if(region != null && region.isActive())
								{
									char_ai.startAITask();
									count2++;
								}
							}
						}
						catch(Exception e)
						{

						}
				}
				activeChar.sendMessage(count + " AI stopped, " + count2 + " AI started");
				break;
			case admin_spawn2: // Игнорирует запрет на спавн рейдбоссов
				StringTokenizer st = new StringTokenizer(fullString, " ");
				try
				{
					st.nextToken();
					String id = st.nextToken();
					int respawnTime = 30;
					int mobCount = 1;
					if(st.hasMoreTokens())
						mobCount = Integer.parseInt(st.nextToken());
					if(st.hasMoreTokens())
						respawnTime = Integer.parseInt(st.nextToken());
					spawnMonster(activeChar, id, respawnTime, mobCount);
				}
				catch(Exception e)
				{}
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	// PUBLIC & STATIC so other classes from package can include it directly
	public static void showHelpPage(Player targetChar, String filename)
	{
		HtmlMessage adminReply = new HtmlMessage(5);
		adminReply.setFile("admin/" + filename);
		targetChar.sendPacket(adminReply);
	}

	private void spawnMonster(Player activeChar, String monsterId, int respawnTime, int mobCount)
	{
		GameObject target = activeChar.getTarget();
		if(target == null)
			target = activeChar;

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher regexp = pattern.matcher(monsterId);
		NpcTemplate template;
		if(regexp.matches())
		{
			// First parameter was an ID number
			int monsterTemplate = Integer.parseInt(monsterId);
			template = NpcHolder.getInstance().getTemplate(monsterTemplate);
		}
		else
		{
			// First parameter wasn't just numbers so go by name not ID
			monsterId = monsterId.replace('_', ' ');
			template = NpcHolder.getInstance().getTemplateByName(monsterId);
		}

		if(template == null)
		{
			activeChar.sendMessage("Incorrect monster template.");
			return;
		}

		try
		{
			SimpleSpawner spawn = new SimpleSpawner(template);
			spawn.setLoc(target.getLoc());
			spawn.setAmount(mobCount);
			spawn.setHeading(activeChar.getHeading());
			spawn.setRespawnDelay(respawnTime);
			spawn.setReflection(activeChar.getReflection());
			spawn.init();
			if(respawnTime == 0)
				spawn.stopRespawn();
			activeChar.sendMessage("Created " + template.name + " on " + target.getObjectId() + ".");
		}
		catch(Exception e)
		{
			activeChar.sendMessage("Target is not ingame.");
		}
	}
}