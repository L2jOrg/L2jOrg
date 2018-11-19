package handler.items;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ShowXMasSeal;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;

public class Books extends SimpleItemHandler
{
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		switch(itemId)
		{
			case 5555:
				player.sendPacket(new ShowXMasSeal(5555));
				break;
			case 32777:
				player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2text\\Guide_Ad.htm"));
				break;
			case 32778:
				player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2text\\Guide_Aw.htm"));
				break;
			default:
				return false;
		}

		return true;
	}
}
