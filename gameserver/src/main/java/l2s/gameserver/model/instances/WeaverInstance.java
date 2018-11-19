package l2s.gameserver.model.instances;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

public class WeaverInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public WeaverInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();

		if(actualCommand.equalsIgnoreCase("unseal"))
		{
			int cost = Integer.parseInt(st.nextToken()); // cost
			int id = Integer.parseInt(st.nextToken()); // item id pin or pouch

			if(player.getAdena() < cost)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			if(!ItemFunctions.deleteItem(player, id, 1, true))
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}

			player.reduceAdena(cost, true);

			int chance = Rnd.get(RewardList.MAX_CHANCE);
			switch(id)
			{
				case 13898: // Sealed Magic Pin (C-Grade)
					if(chance < 350000) // Low-Grade Magic Pin (C-Grade)            35%
						ItemFunctions.addItem(player, 13902, 1, true);
					else if(chance < 550000) // Mid-Grade Magic Pin (C-Grade)       20%
						ItemFunctions.addItem(player, 13903, 1, true);
					else if(chance < 650000) // High-Grade Magic Pin (C-Grade)      10%
						ItemFunctions.addItem(player, 13904, 1, true);
					else if(chance < 730000) // Top-Grade Magic Pin (C-Grade)       8%
						ItemFunctions.addItem(player, 13905, 1, true);
					else
						informFail(player, id);
					break;
				case 13899: // Sealed Magic Pin (B-Grade)
					if(chance < 350000) // Low-Grade Magic Pin (B-Grade)            35%
						ItemFunctions.addItem(player, 13906, 1, true);
					else if(chance < 550000) // Mid-Grade Magic Pin (B-Grade)       20%
						ItemFunctions.addItem(player, 13907, 1, true);
					else if(chance < 650000) // High-Grade Magic Pin (B-Grade)      10%
						ItemFunctions.addItem(player, 13908, 1, true);
					else if(chance < 730000) // Top-Grade Magic Pin (B-Grade)       8%
						ItemFunctions.addItem(player, 13909, 1, true);
					else
						informFail(player, id);
					break;
				case 13900: // Sealed Magic Pin (A-Grade)
					if(chance < 350000) // Low-Grade Magic Pin (A-Grade)            35%
						ItemFunctions.addItem(player, 13910, 1, true);
					else if(chance < 550000) // Mid-Grade Magic Pin (B-Grade)       20%
						ItemFunctions.addItem(player, 13911, 1, true);
					else if(chance < 650000) // High-Grade Magic Pin (A-Grade)      10%
						ItemFunctions.addItem(player, 13912, 1, true);
					else if(chance < 730000) // Top-Grade Magic Pin (A-Grade)       8%
						ItemFunctions.addItem(player, 13913, 1, true);
					else
						informFail(player, id);
					break;
				case 13901: // Sealed Magic Pin (S-Grade)
					if(chance < 350000) // Low-Grade Magic Pin (S-Grade)            35%
						ItemFunctions.addItem(player, 13914, 1, true);
					else if(chance < 550000) // Mid-Grade Magic Pin (S-Grade)       20%
						ItemFunctions.addItem(player, 13915, 1, true);
					else if(chance < 650000) // High-Grade Magic Pin (S-Grade)      10%
						ItemFunctions.addItem(player, 13916, 1, true);
					else if(chance < 730000) // Top-Grade Magic Pin (S-Grade)       8%
						ItemFunctions.addItem(player, 13917, 1, true);
					else
						informFail(player, id);
					break;
				case 13918: // Sealed Magic Pouch (C-Grade)
					if(chance < 350000) // Low-Grade Magic Pouch (C-Grade)            35%
						ItemFunctions.addItem(player, 13922, 1, true);
					else if(chance < 550000) // Mid-Grade Magic Pouch (C-Grade)       20%
						ItemFunctions.addItem(player, 13923, 1, true);
					else if(chance < 650000) // High-Grade Magic Pouch (C-Grade)      10%
						ItemFunctions.addItem(player, 13924, 1, true);
					else if(chance < 730000) // Top-Grade Magic Pouch (C-Grade)       8%
						ItemFunctions.addItem(player, 13925, 1, true);
					else
						informFail(player, id);
					break;
				case 13919: // Sealed Magic Pouch (B-Grade)
					if(chance < 350000) // Low-Grade Magic Pouch (B-Grade)            35%
						ItemFunctions.addItem(player, 13926, 1, true);
					else if(chance < 550000) // Mid-Grade Magic Pouch (B-Grade)       20%
						ItemFunctions.addItem(player, 13927, 1, true);
					else if(chance < 650000) // High-Grade Magic Pouch (B-Grade)      10%
						ItemFunctions.addItem(player, 13928, 1, true);
					else if(chance < 730000) // Top-Grade Magic Pouch (B-Grade)       8%
						ItemFunctions.addItem(player, 13929, 1, true);
					else
						informFail(player, id);
					break;
				case 13920: // Sealed Magic Pouch (A-Grade)
					if(chance < 350000) // Low-Grade Magic Pouch (A-Grade)            35%
						ItemFunctions.addItem(player, 13930, 1, true);
					else if(chance < 550000) // Mid-Grade Magic Pouch (A-Grade)       20%
						ItemFunctions.addItem(player, 13931, 1, true);
					else if(chance < 650000) // High-Grade Magic Pouch (A-Grade)      10%
						ItemFunctions.addItem(player, 13932, 1, true);
					else if(chance < 730000) // Top-Grade Magic Pouch (A-Grade)       8%
						ItemFunctions.addItem(player, 13933, 1, true);
					else
						informFail(player, id);
					break;
				case 13921: // Sealed Magic Pouch (S-Grade)
					if(chance < 350000) // Low-Grade Magic Pouch (S-Grade)            35%
						ItemFunctions.addItem(player, 13934, 1, true);
					else if(chance < 550000) // Mid-Grade Magic Pouch (S-Grade)       20%
						ItemFunctions.addItem(player, 13935, 1, true);
					else if(chance < 650000) // High-Grade Magic Pouch (S-Grade)      10%
						ItemFunctions.addItem(player, 13936, 1, true);
					else if(chance < 730000) // Top-Grade Magic Pouch (S-Grade)       8%
						ItemFunctions.addItem(player, 13937, 1, true);
					else
						informFail(player, id);
					break;
				case 14902: // Sealed Magic Rune Clip (A-Grade)
					if(chance < 350000) // Low-level Magic Rune Clip (A-Grade)        35%
						ItemFunctions.addItem(player, 14906, 1, true);
					else if(chance < 550000) // Mid-level Magic Rune Clip (A-Grade)   20%
						ItemFunctions.addItem(player, 14907, 1, true);
					else if(chance < 650000) // High-level Magic Rune Clip (A-Grade)  10%
						ItemFunctions.addItem(player, 14908, 1, true);
					else if(chance < 730000) // Top-level Magic Rune Clip (A-Grade)   8%
						ItemFunctions.addItem(player, 14909, 1, true);
					else
						informFail(player, id);
					break;
				case 14903: // Sealed Magic Rune Clip (S-Grade)
					if(chance < 350000) // Low-level Magic Rune Clip (S-Grade)        35%
						ItemFunctions.addItem(player, 14910, 1, true);
					else if(chance < 550000) // Mid-level Magic Rune Clip (S-Grade)   20%
						ItemFunctions.addItem(player, 14911, 1, true);
					else if(chance < 650000) // High-level Magic Rune Clip (S-Grade)  10%
						ItemFunctions.addItem(player, 14912, 1, true);
					else if(chance < 730000) // Top-level Magic Rune Clip (S-Grade)   8%
						ItemFunctions.addItem(player, 14913, 1, true);
					else
						informFail(player, id);
					break;
				case 14904: // Sealed Magic Ornament (A-Grade)
					if(chance < 350000) // Low-grade Magic Ornament (A-Grade)         35%
						ItemFunctions.addItem(player, 14914, 1, true);
					else if(chance < 550000) // Mid-grade Magic Ornament (A-Grade)    20%
						ItemFunctions.addItem(player, 14915, 1, true);
					else if(chance < 650000) // High-grade Magic Ornament (A-Grade)   10%
						ItemFunctions.addItem(player, 14916, 1, true);
					else if(chance < 730000) // Top-grade Magic Ornament (A-Grade)    8%
						ItemFunctions.addItem(player, 14917, 1, true);
					else
						informFail(player, id);
					break;
				case 14905: // Sealed Magic Ornament (S-Grade)
					if(chance < 350000) // Low-grade Magic Ornament (S-Grade)         35%
						ItemFunctions.addItem(player, 14918, 1, true);
					else if(chance < 550000) // Mid-grade Magic Ornament (S-Grade)    20%
						ItemFunctions.addItem(player, 14919, 1, true);
					else if(chance < 650000) // High-grade Magic Ornament (S-Grade)   10%
						ItemFunctions.addItem(player, 14920, 1, true);
					else if(chance < 730000) // Top-grade Magic Ornament (S-Grade)    8%
						ItemFunctions.addItem(player, 14921, 1, true);
					else
						informFail(player, id);
					break;
				default:
					return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void informFail(Player player, int itemId)
	{
		Functions.npcSay(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
	}
}