package services;

import org.l2j.gameserver.handler.bypass.Bypass;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.utils.Functions;
import org.l2j.gameserver.utils.ItemFunctions;

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