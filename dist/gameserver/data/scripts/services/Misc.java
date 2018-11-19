package services;

import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author pchayka
 */
public class Misc
{
	@Bypass("services.Misc:assembleAntharasCrystal")
	public void assembleAntharasCrystal(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null)
			return;

		if(!npc.canBypassCheck(player))
			return;

		if(ItemFunctions.getItemCount(player, 17266) < 1 || ItemFunctions.getItemCount(player, 17267) < 1)
		{
			Functions.show("teleporter/32864-2.htm", player);
			return;
		}
		if(ItemFunctions.deleteItem(player, 17266, 1, true) && ItemFunctions.deleteItem(player, 17267, 1, true))
		{
			ItemFunctions.addItem(player, 17268, 1, true);
			Functions.show("teleporter/32864-3.htm", player);
			return;
		}
	}
}