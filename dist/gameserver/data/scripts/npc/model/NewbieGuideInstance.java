package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class NewbieGuideInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int NEWBIE_GUIDE_HUMAN = 30598; // NPC: Гид Новичков
	private static final int NEWBIE_GUIDE_ELF = 30599; // NPC: Гид Новичков
	private static final int NEWBIE_GUIDE_DARK_ELF = 30600; // NPC: Гид Новичков
	private static final int NEWBIE_GUIDE_DWARVEN = 30601; // NPC: Гид Новичков
	private static final int NEWBIE_GUIDE_ORC = 30602; // NPC: Гид Новичков

	public NewbieGuideInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -7)
		{
			if(reply == 1)
			{
				Race race = null;
				switch(getNpcId())
				{
					case 30598:
						race = Race.HUMAN;
						break;
					case 30599:
						race = Race.ELF;
						break;
					case 30600:
						race = Race.DARKELF;
						break;
					case 30601:
						race = Race.DWARF;
						break;
					case 30602:
						race = Race.ORC;
						break;
				}

				if(race != null && player.getRace() != race)
				{
					showChatWindow(player, "default/" + getHtmlName() + "003.htm", false);
					return;
				}

				if(player.getLevel() > 20 || player.getClassLevel() != ClassLevel.NONE)
				{
					showChatWindow(player, "default/" + getHtmlName() + "002.htm", false);
					return;
				}

				if(player.isMageClass())
					showChatWindow(player, "default/" + getHtmlName() + "_m07.htm", false);
				else
					showChatWindow(player, "default/" + getHtmlName() + "_f05.htm", false);
			}
		}
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		if(val == 0)
			showChatWindow(player, "default/" + getHtmlName() + "001.htm", false);
		else
			super.showChatWindow(player, val, firstTalk, replace);
	}

	private String getHtmlName()
	{
		switch(getNpcId())
		{
			case 30598:
				return "guide_human_cnacelot";
			case 30599:
				return "guide_elf_roios";
			case 30600:
				return "guide_delf_frankia";
			case 30601:
				return "guide_dwarf_gullin";
			case 30602:
				return "guide_orc_tanai";
		}
		return null;
	}
}