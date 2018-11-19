package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.utils.BypassStorage.ValidBypass;
import l2s.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author n0nam3
 * @date 22/08/2010 15:16
 */

public class RequestLinkHtml extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestLinkHtml.class);

	//Format: cS
	private String _link;

	@Override
	protected void readImpl()
	{
		_link = readS();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		ValidBypass bp = player.getBypassStorage().validate(_link);
		if(bp == null)
		{
			_log.warn(" RequestLinkHtml: Unexpected link : " + _link + "!");
			return;
		}

		String link = _link;
		int itemId = 0;

		String[] params = _link.split(".htm#");
		if(params.length >= 2)
		{
			link = params[0] + ".htm";
			itemId = !Util.isDigit(params[1]) ? -1 : Integer.parseInt(params[1]);
		}

		if(link.contains("..") || !link.endsWith(".htm") || itemId == -1)
		{
			_log.warn("RequestLinkHtml: hack? link contains prohibited characters: '" + link + "'!");
			return;
		}

		HtmlMessage msg;
		if(itemId == 0)
		{
			NpcInstance npc = player.getLastNpc();
			if(npc != null)
			{
				if(!player.checkInteractionDistance(npc))
					return;

				link = npc.correctBypassLink(player, link);

				msg = new HtmlMessage(npc);
			}
			else
				msg = new HtmlMessage(0);
		}
		else
			msg = new HtmlMessage(0).setItemId(itemId);

		msg.setFile(link);
		player.sendPacket(msg);
	}
}