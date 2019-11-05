package handlers.itemhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class Book implements IItemHandler {

	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {

		if (!isPlayer(playable)) {
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player activeChar = (Player) playable;
		final int itemId = item.getId();
		
		final String filename = "data/html/help/" + itemId + ".htm";
		final String content = HtmCache.getInstance().getHtm(activeChar, filename);
		
		if (isNull(content)) {
			final NpcHtmlMessage html = new NpcHtmlMessage(0, item.getId());
			html.setHtml("<html><body>My Text is missing:<br>" + filename + "</body></html>");
			activeChar.sendPacket(html);
		} else {
			final NpcHtmlMessage itemReply = new NpcHtmlMessage();
			itemReply.setHtml(content);
			activeChar.sendPacket(itemReply);
		}
		
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		return true;
	}
}
