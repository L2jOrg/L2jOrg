package org.l2j.gameserver.network.l2.s2c;

import java.util.List;
import java.util.regex.Matcher;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.ImagesCache;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.tables.FakePlayersTable;
import org.l2j.gameserver.utils.BypassStorage.BypassType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowBoardPacket extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(ShowBoardPacket.class);

	public static L2GameServerPacket CLOSE = new ShowBoardPacket();

	private static final String[] DIRECT_BYPASS = new String[] {
			"bypass _bbshome",
			"bypass _bbsgetfav",
			"bypass _bbsloc",
			"bypass _bbsclan",
			"bypass _bbsmemo",
			"bypass _maillist_0_1_0_",
			"bypass _friendlist_0_" };

	private boolean _show;
	private String _html;
	private String _fav;

	public static void separateAndSend(String html, Player player)
	{
		if(html == null || html.isEmpty())
			return;

		String fav = "";
		if(player.getSessionVar("add_fav") != null)
			fav = "bypass _bbsaddfav_List";

		player.getBypassStorage().parseHtml(html, BypassType.BBS);

		html = html.replace("<?copyright?>", Config.BBS_COPYRIGHT);
		html = html.replace("<?total_online?>", String.valueOf(GameObjectsStorage.getPlayers().size() + FakePlayersTable.getActiveFakePlayersCount()));

		Matcher m = ImagesCache.HTML_PATTERN.matcher(html);
		while(m.find())
		{
			String imageName = m.group(1);
			int imageId = ImagesCache.getInstance().getImageId(imageName);
			html = html.replaceAll("%image:" + imageName + "%", "Crest.pledge_crest_" + Config.REQUEST_ID + "_" + imageId);
			byte[] image = ImagesCache.getInstance().getImage(imageId);
			if(image != null)
				player.sendPacket(new PledgeCrestPacket(imageId, image));
		}

		if(html.length() < 8180)
		{
			player.sendPacket(new ShowBoardPacket("101", html, fav));
			player.sendPacket(new ShowBoardPacket("102", "", fav));
			player.sendPacket(new ShowBoardPacket("103", "", fav));
		}
		else if(html.length() < 8180 * 2)
		{
			player.sendPacket(new ShowBoardPacket("101", html.substring(0, 8180), fav));
			player.sendPacket(new ShowBoardPacket("102", html.substring(8180, html.length()), fav));
			player.sendPacket(new ShowBoardPacket("103", "", fav));
		}
		else if(html.length() < 8180 * 3)
		{
			player.sendPacket(new ShowBoardPacket("101", html.substring(0, 8180), fav));
			player.sendPacket(new ShowBoardPacket("102", html.substring(8180, 8180 * 2), fav));
			player.sendPacket(new ShowBoardPacket("103", html.substring(8180 * 2, html.length()), fav));
		}
		else
			throw new IllegalArgumentException("Html is too long!");
	}

	public static void separateAndSend(String html, List<String> arg, Player player)
	{
		String fav = "";
		if(player.getSessionVar("add_fav") != null)
			fav = "bypass _bbsaddfav_List";

		player.getBypassStorage().parseHtml(html, BypassType.BBS);

		html = html.replace("<?copyright?>", Config.BBS_COPYRIGHT);

		Matcher m = ImagesCache.HTML_PATTERN.matcher(html);
		while(m.find())
		{
			String imageName = m.group(1);
			int imageId = ImagesCache.getInstance().getImageId(imageName);
			html = html.replaceAll("%image:" + imageName + "%", "Crest.crest_" + Config.REQUEST_ID + "_" + imageId);
			byte[] image = ImagesCache.getInstance().getImage(imageId);
			if(image != null)
				player.sendPacket(new PledgeCrestPacket(imageId, image));
		}

		if(html.length() < 8180)
		{
			player.sendPacket(new ShowBoardPacket("1001", html, fav));
			player.sendPacket(new ShowBoardPacket("1002", arg, fav));
		}
		else
			throw new IllegalArgumentException("Html is too long!");
	}

	private ShowBoardPacket(String id, String html, String fav)
	{
		_show = true;
		_html = id + "\u0008";
		if(html != null)
			_html += html;
		_fav = fav;
	}

	private ShowBoardPacket(String id, List<String> arg, String fav)
	{
		_show = true;
		_html = id + "\u0008";
		for(String a : arg)
			_html += a + " \u0008";
	}

	private ShowBoardPacket()
	{
		_show = false;
	}

	@Override
	protected final void writeImpl()
	{
		writeByte(_show); //c4 1 to show community 00 to hide
		if(_show)
		{
			for(String bbsBypass : DIRECT_BYPASS)
				writeString(bbsBypass);
			writeString(_fav);
			writeString(_html);
		}
	}
}