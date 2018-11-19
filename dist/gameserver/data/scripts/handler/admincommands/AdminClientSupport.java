package handler.admincommands;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 23:46/17.05.2011
 */
public class AdminClientSupport extends ScriptAdminCommand
{
	private static final Logger _log = LoggerFactory.getLogger(AdminClientSupport.class);

	public enum Commands
	{
		admin_setskill,
		admin_summon
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player player)
	{
		Commands c = (Commands)comm;
		GameObject target = player.getTarget();
		switch(c)
		{
			case admin_setskill:
				if(wordList.length < 3)
					return false;

				if(!player.getPlayerAccess().CanEditChar)
					return false;
				if(target == null || !target.isPlayer())
					return false;
				try
				{
					SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntryByIndex(Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]));
					if(skillEntry == null)
						return false;

					target.getPlayer().addSkill(skillEntry, true);
					target.getPlayer().sendSkillList();
					target.getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skillEntry.getId(), skillEntry.getLevel()));
				}
				catch(NumberFormatException e)
				{
					_log.info("AdminClientSupport:useAdminCommand(Enum,String[],String,L2Player): " + e, e);
					return false;
				}
				break;
			case admin_summon:
				if(wordList.length != 3)
					return false;

				if(!player.getPlayerAccess().CanEditChar)
					return false;
				try
				{
					int id = Integer.parseInt(wordList[1]);
					long count = Long.parseLong(wordList[2]);

					if(id >= 1000000)
					{
						if(target == null)
							target = player;

						NpcTemplate template = NpcHolder.getInstance().getTemplate(id - 1000000);

						for(int i = 0; i < count; i++)
						{
							NpcInstance npc = template.getNewInstance();
							npc.setSpawnedLoc(target.getLoc());
							npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);

							npc.spawnMe(npc.getSpawnedLoc());
						}
					}
					else
					{
						if(target == null)
							target = player;

						if(!target.isPlayer())
							return false;

						ItemTemplate template = ItemHolder.getInstance().getTemplate(id);
						if(template == null)
							return false;

						if(template.isStackable())
						{
							ItemInstance item = ItemFunctions.createItem(id);
							item.setCount(count);

							target.getPlayer().getInventory().addItem(item);
							target.getPlayer().sendPacket(SystemMessagePacket.obtainItems(item));
						}
						else
						{
							for(int i = 0; i < count; i++)
							{
								ItemInstance item = ItemFunctions.createItem(id);

								target.getPlayer().getInventory().addItem(item);
								target.getPlayer().sendPacket(SystemMessagePacket.obtainItems(item));
							}
						}
					}
				}
				catch(NumberFormatException e)
				{
					_log.info("AdminClientSupport:useAdminCommand(Enum,String[],String,L2Player): " + e, e);
					return false;
				}

				break;
		}
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
