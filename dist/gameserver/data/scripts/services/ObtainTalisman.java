package services;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

/**
 * Используется для выдачи талисманов в крепостях и замках за Knight's Epaulette.
 * @Author: SYS
 */
public class ObtainTalisman
{
	@Bypass("services.ObtainTalisman:Obtain")
	public void Obtain(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null)
			return;

		if(!npc.canBypassCheck(player))
			return;

		if(!player.isQuestContinuationPossible(false))
		{
			player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
			return;
		}

		if(ItemFunctions.getItemCount(player, 9912) < 10)
		{
			Functions.show("scripts/services/ObtainTalisman-no.htm", player, npc);
			return;
		}

		final List<Integer> talismans = new ArrayList<Integer>();

		//9914-9965
		for(int i = 9914; i <= 9965; i++)
			if(i != 9923)
				talismans.add(i);
		//10416-10424
		for(int i = 10416; i <= 10424; i++)
			talismans.add(i);
		//10518-10519
		for(int i = 10518; i <= 10519; i++)
			talismans.add(i);
		//10533-10543
		for(int i = 10533; i <= 10543; i++)
			talismans.add(i);

		ItemFunctions.deleteItem(player, 9912, 10);
		ItemFunctions.addItem(player, talismans.get(Rnd.get(talismans.size())), 1);
		Functions.show("scripts/services/ObtainTalisman.htm", player, npc);
	}
}