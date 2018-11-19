package handler.bbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Block;
import l2s.gameserver.model.actor.instances.player.Friend;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommunityFriends extends ScriptsCommunityHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityFriends.class);

	@Override
	public void onInit()
	{
		super.onInit();

		if(Config.BBS_ENABLED)
			_log.info("CommunityBoard: Manage Friends service loaded.");
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_friendlist_",
			"_friendblocklist_",
			"_frienddelete_",
			"_frienddeleteallconfirm_",
			"_frienddeleteall_",
			"_friendblockdelete_",
			"_friendblockadd_",
			"_friendblockdeleteallconfirm_",
			"_friendblockdeleteall_"
		};
	}

	@Override
	protected void doBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = HtmCache.getInstance().getHtml(cmd.startsWith("friendbloc") ? "scripts/handler/bbs/bbs_block_list.htm" : "scripts/handler/bbs/bbs_friend_list.htm", player);
		player.setSessionVar("add_fav", null);
		if(cmd.equals("friendlist"))
		{
			String act = st.nextToken();

			html = html.replace("%friend_list%", getFriendList(player));

			if(act.equals("0"))
			{
				if(player.getSessionVar("selFriends") != null)
					player.setSessionVar("selFriends", null);

				html = html.replace("%selected_friend_list%", "");
				html = html.replace("%delete_all_msg%", "");
			}
			else if(act.equals("1"))
			{
				String objId = st.nextToken();
				String selected;
				if((selected = player.getSessionVar("selFriends")) == null)
					selected = objId + ";";
				else if(!selected.contains(objId))
					selected += objId + ";";

				player.setSessionVar("selFriends", selected);

				html = html.replace("%selected_friend_list%", getSelectedList(player));
				html = html.replace("%delete_all_msg%", "");
			}
			else if(act.equals("2"))
			{
				String objId = st.nextToken();
				String selected = player.getSessionVar("selFriends");
				if(selected != null)
				{
					selected = selected.replace(objId + ";", "");
					player.setSessionVar("selFriends", selected);
				}
				html = html.replace("%selected_friend_list%", getSelectedList(player));
				html = html.replace("%delete_all_msg%", "");
			}
		}
		else if(cmd.equals("frienddeleteallconfirm"))
		{
			html = html.replace("%friend_list%", getFriendList(player));
			html = html.replace("%selected_friend_list%", getSelectedList(player));
			html = html.replace("%delete_all_msg%", "<br>\nAre you sure you want to delete all friends from the friends list? <button value = \"OK\" action=\"bypass _frienddeleteall_\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\">");
		}
		else if(cmd.equals("frienddelete"))
		{
			String selected = player.getSessionVar("selFriends");
			if(selected != null)
				for(String objId : selected.split(";"))
					if(!objId.isEmpty())
						player.getFriendList().remove(player.getFriendList().get(Integer.parseInt(objId)).getName());
			player.setSessionVar("selFriends", null);

			html = html.replace("%friend_list%", getFriendList(player));
			html = html.replace("%selected_friend_list%", "");
			html = html.replace("%delete_all_msg%", "");
		}
		else if(cmd.equals("frienddeleteall"))
		{
			List<Friend> friends = new ArrayList<Friend>(1);
			friends.addAll(player.getFriendList().valueCollection());
			for(Friend friend : friends)
				player.getFriendList().remove(friend.getName());

			player.setSessionVar("selFriends", null);

			html = html.replace("%friend_list%", "");
			html = html.replace("%selected_friend_list%", "");
			html = html.replace("%delete_all_msg%", "");
		}
		else if(cmd.equals("friendblocklist"))
		{
			html = html.replace("%block_list%", getBlockList(player));
			html = html.replace("%delete_all_msg%", "");
		}
		else if(cmd.equals("friendblockdeleteallconfirm"))
		{
			html = html.replace("%block_list%", getBlockList(player));
			html = html.replace("%delete_all_msg%", "<br>\nDo you want to delete all characters from the block list? <button value = \"OK\" action=\"bypass _friendblockdeleteall_\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\" >");
		}
		else if(cmd.equals("friendblockdelete"))
		{
			String objId = st.nextToken();
			if(objId != null && !objId.isEmpty())
			{
				int objectId = Integer.parseInt(objId);
				Block block = player.getBlockList().get(objectId);
				if(block != null)
					player.getBlockList().remove(block.getName());
			}
			html = html.replace("%block_list%", getBlockList(player));
			html = html.replace("%delete_all_msg%", "");
		}
		else if(cmd.equals("friendblockdeleteall"))
		{
			List<Block> bl = new ArrayList<Block>(1);
			bl.addAll(player.getBlockList().valueCollection());
			for(Block block : bl)
				player.getBlockList().remove(block.getName());

			html = html.replace("%block_list%", "");
			html = html.replace("%delete_all_msg%", "");
		}

		ShowBoardPacket.separateAndSend(html, player);
	}

	@Override
	protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_block_list.htm", player);

		if("_friendblockadd_".equals(bypass) && arg3 != null && !arg3.isEmpty())
			player.getBlockList().add(arg3);

		html = html.replace("%block_list%", getBlockList(player));
		html = html.replace("%delete_all_msg%", "");
		ShowBoardPacket.separateAndSend(html, player);
	}

	private static String getFriendList(Player player)
	{
		StringBuilder friendList = new StringBuilder("");
		for(Friend friend : player.getFriendList().values())
			friendList.append("<a action=\"bypass _friendlist_1_").append(friend.getObjectId()).append("\">").append(friend.getName()).append("</a> (").append(friend.isOnline() ? "on" : "off").append(") &nbsp;");

		return friendList.toString();
	}

	private static String getSelectedList(Player player)
	{
		String selected = player.getSessionVar("selFriends");

		if(selected == null)
			return "";

		String[] sels = selected.split(";");
		StringBuilder selectedList = new StringBuilder("");
		for(String objectId : sels)
			if(!objectId.isEmpty())
				selectedList.append("<a action=\"bypass _friendlist_2_").append(objectId).append("\">").append(player.getFriendList().get(Integer.parseInt(objectId)).getName()).append("</a>;");

		return selectedList.toString();
	}

	private static String getBlockList(Player player)
	{
		StringBuilder blockList = new StringBuilder("");
		Block[] bl = player.getBlockList().values();
		for(Block block : bl)
			blockList.append(block.getName()).append("&nbsp; <a action=\"bypass _friendblockdelete_").append(block.getObjectId()).append("\">Delete</a>&nbsp;&nbsp;");

		return blockList.toString();
	}
}
