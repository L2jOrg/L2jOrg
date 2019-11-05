package handlers.itemhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JIV
 */
public class Bypass implements IItemHandler {

	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {

		if (!isPlayer(playable)) {
			return false;
		}

		final Player player = (Player) playable;
		final int itemId = item.getId();
		
		final String filename = "data/html/item/" + itemId + ".htm";
		final String content = HtmCache.getInstance().getHtm(player, filename);
		final NpcHtmlMessage html = new NpcHtmlMessage(0, item.getId());

		if (content == null) {
			html.setHtml("<html><body>My Text is missing:<br>" + filename + "</body></html>");
			player.sendPacket(html);
		} else {
			html.setHtml(content);
			html.replace("%itemId%", String.valueOf(item.getObjectId()));
			player.sendPacket(html);
		}
		return true;
	}
	
}
