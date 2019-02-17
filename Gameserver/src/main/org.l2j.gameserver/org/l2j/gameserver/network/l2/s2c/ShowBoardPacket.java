package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.ImagesCache;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.utils.BypassStorage.BypassType;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.regex.Matcher;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.STRING_EMPTY;
import static org.l2j.commons.util.Util.isNullOrEmpty;

@StaticPacket
public class ShowBoardPacket extends L2GameServerPacket {

	private static final int MAX_PACKET_SIZE = 8180;
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

	private ShowBoardPacket(String id, String html, String fav) {
		_show = true;
		_html = String.format("%s%s%s", id, "\u0008", nonNull(html) ? html : "");
		_fav = fav;
	}

	private ShowBoardPacket(String id, List<String> arg, String fav) {
		this(id, arg.stream().reduce("", (identity, next) -> String.format("%s%s%s",identity, next, "\u0008")), fav);
	}

	private ShowBoardPacket() {
		_show = false;
		_html = "";
		_fav = "";
	}

	public static void separateAndSend(String html, Player player) {
		if(isNullOrEmpty(html)) {
			return;
		}

		String fav = hasBBSFavorite(player);
		html = substituteVariables(html, player);

		if(html.length() < MAX_PACKET_SIZE) {
			player.sendPacket(new ShowBoardPacket("101", html, fav));
			player.sendPacket(new ShowBoardPacket("102", "", fav));
			player.sendPacket(new ShowBoardPacket("103", "", fav));
		} else if(html.length() < MAX_PACKET_SIZE * 2) {
			player.sendPacket(new ShowBoardPacket("101", html.substring(0, MAX_PACKET_SIZE), fav));
			player.sendPacket(new ShowBoardPacket("102", html.substring(MAX_PACKET_SIZE), fav));
			player.sendPacket(new ShowBoardPacket("103", "", fav));
		} else if(html.length() < MAX_PACKET_SIZE * 3) {
			player.sendPacket(new ShowBoardPacket("101", html.substring(0, MAX_PACKET_SIZE), fav));
			player.sendPacket(new ShowBoardPacket("102", html.substring(MAX_PACKET_SIZE, MAX_PACKET_SIZE * 2), fav));
			player.sendPacket(new ShowBoardPacket("103", html.substring(MAX_PACKET_SIZE * 2), fav));
		} else {
			throw new IllegalArgumentException("Html is too long!");
		}
	}

	private static String substituteVariables(String html, Player player) {
		player.getBypassStorage().parseHtml(html, BypassType.BBS);

		html = html.replace("<?copyright?>", Config.BBS_COPYRIGHT).replace("<?total_online?>", String.valueOf(GameObjectsStorage.getPlayers().size()));

		Matcher m = ImagesCache.HTML_PATTERN.matcher(html);
		while (m.find()) {
			String imageName = m.group(1);
			int imageId = ImagesCache.getInstance().getImageId(imageName);
			html = html.replaceAll("%image:" + imageName + "%", "Crest.pledge_crest_" + getSettings(ServerSettings.class).serverId() + "_" + imageId);
			byte[] image = ImagesCache.getInstance().getImage(imageId);
			if (image != null)
				player.sendPacket(new PledgeCrestPacket(imageId, image));
		}
		return html;
	}

	private static String hasBBSFavorite(Player player) {
		if (nonNull(player.getSessionVar("add_fav")))
			return "bypass _bbsaddfav_List";
		return STRING_EMPTY;
	}

	public static void separateAndSend(String html, List<String> arg, Player player) {
		String fav = hasBBSFavorite(player);
		html = substituteVariables(html, player);

		if(html.length() < MAX_PACKET_SIZE) {
			player.sendPacket(new ShowBoardPacket("1001", html, fav));
			player.sendPacket(new ShowBoardPacket("1002", arg, fav));
		}
		else {
			throw new IllegalArgumentException("Html is too long!");
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.put((byte) (_show ? 0x01 : 0x00)); //c4 1 to show community 00 to hide
		if(_show)
		{
			for(String bbsBypass : DIRECT_BYPASS)
				writeString(bbsBypass, buffer);
			writeString(_fav, buffer);
			writeString(_html, buffer);
		}
	}

    @Override
    protected int size(GameClient client) {
        return _fav.length() * 2 + _html.length() * 2 + 14 * 23;
    }
}