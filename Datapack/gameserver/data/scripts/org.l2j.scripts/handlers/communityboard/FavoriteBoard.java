package handlers.communityboard;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.CommunityDAO;
import org.l2j.gameserver.data.database.data.CommunityFavorite;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IParseBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.commons.util.Util.formatDateTime;

/**
 * Favorite board.
 * @author Zoey76
 */
public class FavoriteBoard implements IParseBoardHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteBoard.class);

	private static final String[] COMMANDS = {
		"_bbsgetfav"
	};
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player player) {
		if(tokens.hasMoreTokens()) {
			switch (tokens.nextToken()) {
				case "add" -> addFavorite(player);
				case "del" -> deleteFavorite(player, Util.parseNextInt(tokens, -1));
			}
		} else {
			showFavorites(player);
		}
		return true;
	}

	protected void showFavorites(Player player) {
		final var favoriteTemplate = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/favorite_list.html");
		final var favoriteBuilder = new StringBuilder();

		getDAO(CommunityDAO.class).findFavorites(player.getObjectId()).forEach(favorite -> addFavoriteLink(favoriteTemplate, favoriteBuilder, favorite));

		final var html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/favorite.html");
		CommunityBoardHandler.separateAndSend(html.replace("%fav_list%", favoriteBuilder.toString()), player);
	}

	protected void deleteFavorite(Player player, int id) {
		if(id >= 1) {
			getDAO(CommunityDAO.class).deleteFavorite(player.getObjectId(), id);
		}
		showFavorites(player);
	}

	protected void addFavorite(Player player) {
		doIfNonNull(CommunityBoardHandler.getInstance().removeBypass(player), bypass -> {
			final String[] parts = bypass.split("&", 2);
			if (parts.length != 2) {
				LOGGER.warn("Couldn't add favorite link, {} it's not a valid bypass!", bypass);
				return;
			}

			CommunityFavorite favorite = new CommunityFavorite();
			favorite.setPlayerId(player.getObjectId());
			favorite.setTitle(parts[0].trim());
			favorite.setBypass(parts[1].trim());

			getDAO(CommunityDAO.class).save(favorite);

		});
	}

	protected void addFavoriteLink(String favoriteTemplate, StringBuilder favoriteBuilder, CommunityFavorite favorite) {
		favoriteBuilder.append(
				favoriteTemplate.replace("%fav_bypass%", favorite.getBypass())
					.replace("%fav_title%", favorite.getTitle())
					.replace("%fav_add_date%", formatDateTime(favorite.getDate()))
					.replace("%fav_id%", String.valueOf(favorite.getId()))
		);
	}
}
