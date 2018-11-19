import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

public class Util
{
	@Bypass("Util:PayPage")
	public void PayPage(Player player, NpcInstance npc, String[] param)
	{
		if(param.length < 2)
			throw new IllegalArgumentException();

		if(player == null)
			return;

		String page = param[0];
		int item = Integer.parseInt(param[1]);
		long price = Long.parseLong(param[2]);

		if(ItemFunctions.getItemCount(player, item) < price)
		{
			player.sendPacket(item == 57 ? SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		ItemFunctions.deleteItem(player, item, price);
		Functions.show(page, player);
	}

	@Bypass("Util:MakeEchoCrystal")
	public void MakeEchoCrystal(Player player, NpcInstance npc, String[] param)
	{
		if(param.length < 2)
			throw new IllegalArgumentException();

		if(player == null)
			return;

		if(!npc.canBypassCheck(player))
			return;

		int crystal = Integer.parseInt(param[0]);
		if(crystal < 4411 || crystal > 4417)
			return;

		int score = Integer.parseInt(param[1]);

		if(ItemFunctions.getItemCount(player, score) == 0)
		{
			npc.onBypassFeedback(player, "Chat 1");
			return;
		}

		if(ItemFunctions.getItemCount(player, 57) < 200)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		ItemFunctions.deleteItem(player, 57, 200);
		ItemFunctions.addItem(player, crystal, 1);
	}

	@Bypass("Util:enter_dc")
	public void enter_dc(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null)
			return;

		if(!npc.canBypassCheck(player))
			return;

		player.setVar("DCBackCoords", player.getLoc().toXYZString(), -1);
		player.teleToLocation(-114582, -152635, -6742);
	}

	@Bypass("Util:exit_dc")
	public void exit_dc(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null)
			return;

		if(!npc.canBypassCheck(player))
			return;

		String var = player.getVar("DCBackCoords");
		if(var == null || var.isEmpty())
		{
			player.teleToLocation(new Location(43768, -48232, -800), ReflectionManager.MAIN);
			return;
		}
		player.teleToLocation(Location.parseLoc(var), ReflectionManager.MAIN);
		player.unsetVar("DCBackCoords");
	}
}