/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.communityboard;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.CommunityDAO;
import org.l2j.gameserver.data.database.data.CommunityMemo;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IWriteBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ShowBoard;

import java.util.List;
import java.util.StringTokenizer;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.*;
import static org.l2j.gameserver.network.SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED;
import static org.l2j.gameserver.network.SystemMessageId.THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED;

/**
 * @author Zoey76
 * @author JoeAlisson
 */
public class MemoBoard implements IWriteBoardHandler {

	private static final String MEMO_TEMPLATE = """
		<table border=0 cellspacing=0 cellpadding=5 WIDTH=755>
		<tr><td FIXWIDTH=5></td><td FIXWIDTH=500><a action="bypass _bbsmemo read %d">%s</a></td><td FIXWIDTH=145 align=center></td><td FIXWIDTH=75 align=center>%s</td></tr>
		</table>
		<img src="L2UI.Squaregray" width="755" height="1">
		""";

	private static final String[] COMMANDS = {
		"_bbsmemo"
	};
	
	@Override
	public boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player player) {
		if(tokens.hasMoreTokens()) {
			parseAction(tokens, player);
		} else {
			CommunityBoardHandler.separateAndSend(home(player), player);
		}
		return true;
	}

	protected void parseAction(StringTokenizer tokens, Player player) {
		switch (tokens.nextToken()){
			case "write" -> writeMemo(player);
			case "read" -> readMemo(player, parseNextInt(tokens, 0));
			case "modify" -> modifyMemo(player, parseNextInt(tokens, 0));
			case "del" -> deleteMemo(player, parseNextInt(tokens, 0));
		}
	}

	private void deleteMemo(Player player, int memoId) {
		getDAO(CommunityDAO.class).deleteMemo(player.getObjectId(), memoId);
		CommunityBoardHandler.separateAndSend(home(player), player);
	}

	private void modifyMemo(Player player, int memoId) {
		doIfNonNull(getDAO(CommunityDAO.class).findMemo(memoId, player.getObjectId()), memo -> {
			final var html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/memo_write.html").replace("%id%", String.valueOf(memoId));
			player.sendPacket(new ShowBoard(html, "1001"));
			player.sendPacket(new ShowBoard(List.of("0", "0", "0", "0", "0", "0", "0", "0", "0", "0", memo.getTitle(), "0", memo.getText(), "0", "0", "0", "0")));
		});
	}


	private void readMemo(Player player, int memoId) {
		doIfNonNull(getDAO(CommunityDAO.class).findMemo(memoId, player.getObjectId()), memo -> {
			final var html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/memo_detail.html")
					.replace("%title%", memo.getTitle())
					.replace("%player%", player.getName())
					.replace("%datetime%", Util.formatDateTime(memo.getDate()))
					.replace("%text%", memo.getText())
					.replace("%id%", String.valueOf(memo.getId()));
			CommunityBoardHandler.separateAndSend(html, player);
		});
	}

	private void writeMemo(Player player) {
		CommunityBoardHandler.separateAndSend(HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/memo_write.html").replace("%id%", "0"), player);
	}

	protected String home(Player player) {
		var data = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/memo.html");
		final var memos = getDAO(CommunityDAO.class).findMemosBasicInfo(player.getObjectId());
		if(Util.isNullOrEmpty(memos)) {
			return data.replace("%memo_list%", "");
		}
		return data.replace("%memo_list%", memosHtml(memos));
	}

	private String memosHtml(List<CommunityMemo> memos) {
		final var builder = new StringBuilder();
		for (var memo : memos) {
			builder.append(String.format(MEMO_TEMPLATE, memo.getId(), memo.getTitle(), formatDate(memo.getDate())));
		}
		return builder.append("<br>").toString();
	}

	@Override
	public boolean writeCommunityBoardCommand(Player player, String id, String arg, String title, String text, String arg5) {
		if(title.length() > 80) {
			player.sendPacket(THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
			return false;
		}

		if(text.length() > 500) {
			player.sendPacket(THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED);
			return false;
		}

		final var memoId = Integer.parseInt(id);
		if(memoId == 0) {
			getDAO(CommunityDAO.class).saveMemo(player.getObjectId(), title, text);
		} else {
			getDAO(CommunityDAO.class).updateMemo(player.getObjectId(), memoId, title, text);
		}
		CommunityBoardHandler.separateAndSend(home(player), player);
		return true;
	}

	@Override
	public String[] getCommunityBoardCommands() {
		return COMMANDS;
	}
}
