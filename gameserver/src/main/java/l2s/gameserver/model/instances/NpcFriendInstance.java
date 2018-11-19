package l2s.gameserver.model.instances;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.WarehouseFunctions;

public final class NpcFriendInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public NpcFriendInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		if(val == 0)
		{
			if(getNpcId() >= 31370 && getNpcId() <= 31376 && player.getVarka() > 0 || getNpcId() >= 31377 && getNpcId() < 31384 && player.getKetra() > 0)
			{
				showChatWindow(player, "npc_friend/" + getNpcId() + "-nofriend.htm", firstTalk);
				return;
			}

			String filename = null;
			switch(getNpcId())
			{
				case 31370:
				case 31371:
				case 31373:
				case 31377:
				case 31378:
				case 31380:
				case 31553:
				case 31554:
					filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31372:
					if(player.getKetra() > 2)
						filename = "npc_friend/" + getNpcId() + "-bufflist.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31379:
					if(player.getVarka() > 2)
						filename = "npc_friend/" + getNpcId() + "-bufflist.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31374:
					if(player.getKetra() > 1)
						filename = "npc_friend/" + getNpcId() + "-warehouse.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31381:
					if(player.getVarka() > 1)
						filename = "npc_friend/" + getNpcId() + "-warehouse.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31375:
					if(player.getKetra() == 3 || player.getKetra() == 4)
						filename = "npc_friend/" + getNpcId() + "-special1.htm";
					else if(player.getKetra() == 5)
						filename = "npc_friend/" + getNpcId() + "-special2.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31382:
					if(player.getVarka() == 3 || player.getVarka() == 4)
						filename = "npc_friend/" + getNpcId() + "-special1.htm";
					else if(player.getVarka() == 5)
						filename = "npc_friend/" + getNpcId() + "-special2.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31376:
					if(player.getKetra() == 4)
						filename = "npc_friend/" + getNpcId() + "-normal.htm";
					else if(player.getKetra() == 5)
						filename = "npc_friend/" + getNpcId() + "-special.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31383:
					if(player.getVarka() == 4)
						filename = "npc_friend/" + getNpcId() + "-normal.htm";
					else if(player.getVarka() == 5)
						filename = "npc_friend/" + getNpcId() + "-special.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31555:
					if(player.getRam() == 1)
						filename = "npc_friend/" + getNpcId() + "-special1.htm";
					else if(player.getRam() == 2)
						filename = "npc_friend/" + getNpcId() + "-special2.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
					break;
				case 31556:
					if(player.getRam() == 2)
						filename = "npc_friend/" + getNpcId() + "-bufflist.htm";
					else
						filename = "npc_friend/" + getNpcId() + ".htm";
			}

			if(filename != null)
			{
				showChatWindow(player, filename, firstTalk);
				return;
			}
		}

		super.showChatWindow(player, val, firstTalk, replace);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		if(actualCommand.equalsIgnoreCase("Buff"))
		{
			if(st.countTokens() < 1)
				return;
			int val = Integer.parseInt(st.nextToken());
			int item = 0;

			switch(getNpcId())
			{
				case 31372:
					item = 7186;
					break;
				case 31379:
					item = 7187;
					break;
				case 31556:
					item = 7251;
					break;
			}

			int skill = 0;
			int level = 0;
			long count = 0;

			switch(val)
			{
				case 1:
					skill = 4359;
					level = 2;
					count = 2;
					break;
				case 2:
					skill = 4360;
					level = 2;
					count = 2;
					break;
				case 3:
					skill = 4345;
					level = 3;
					count = 3;
					break;
				case 4:
					skill = 4355;
					level = 2;
					count = 3;
					break;
				case 5:
					skill = 4352;
					level = 1;
					count = 3;
					break;
				case 6:
					skill = 4354;
					level = 3;
					count = 3;
					break;
				case 7:
					skill = 4356;
					level = 1;
					count = 6;
					break;
				case 8:
					skill = 4357;
					level = 2;
					count = 6;
					break;
			}

			if(skill != 0 && player.getInventory().destroyItemByItemId(item, count))
				player.doCast(SkillHolder.getInstance().getSkillEntry(skill, level), player, true);
			else
				showChatWindow(player, "npc_friend/" + getNpcId() + "-havenotitems.htm", false);
		}
		else if(command.startsWith("Chat"))
		{
			int val = Integer.parseInt(command.substring(5));
			String fname = "";
			fname = "npc_friend/" + getNpcId() + "-" + val + ".htm";
			if(!fname.equals(""))
				showChatWindow(player, fname, false);
		}
		else if(command.startsWith("Buy"))
		{
			int val = Integer.parseInt(command.substring(4));
			showShopWindow(player, val, false);
		}
		else if(actualCommand.equalsIgnoreCase("Sell"))
			showShopWindow(player);
		else if(command.startsWith("WithdrawP"))
			WarehouseFunctions.showRetrieveWindow(player);
		else if(command.equals("DepositP"))
			WarehouseFunctions.showDepositWindow(player);
		else
			super.onBypassFeedback(player, command);
	}
}