package handler.bbs;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.data.htm.HtmTemplates;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.data.xml.holder.RecipeHolder;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.items.ManufactureItem;
import org.l2j.gameserver.model.items.TradeItem;
import org.l2j.gameserver.network.l2.s2c.RadarControlPacket;
import org.l2j.gameserver.network.l2.s2c.ShowBoardPacket;
import org.l2j.gameserver.templates.item.ItemGrade;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.RecipeTemplate;
import org.l2j.gameserver.templates.item.data.ChancedItemData;
import org.l2j.gameserver.utils.HtmlUtils;
import org.l2j.gameserver.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class CommunityRegion extends ScriptsCommunityHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityRegion.class);
	private static final int[][] _towns = new int[][] { {1010005, 19, 21}, {1010006, 20, 22}, {1010007, 22, 22}, {1010013, 22, 19} };
	private static final String[] _regionTypes = { "&$596;", "&$597;", "&$665;" };
	private static final String[] _elements = { "&$1622;", "&$1623;", "&$1624;", "&$1625;", "&$1626;", "&$1627;" };
	private static final String[] _grade = { "&$1291;", "&$1292;", "&$1293;", "&$1294;", "&$1295;", "S80 Grade", "S84 Grade", "R Grade", "R95 Grade", "R99 Grade"};
	private static final int SELLER_PER_PAGE = 12;

	@Override
	public void onInit()
	{
		super.onInit();

		if(Config.BBS_ENABLED)
			_log.info("CommunityBoard: Region service loaded.");
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbsloc",
			"_bbsregion_",
			"_bbsreglist_",
			"_bbsregsearch",
			"_bbsregview_",
			"_bbsregtarget_"
		};
	}

	@Override
	protected void doBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		player.setSessionVar("add_fav", null);
		if("bbsloc".equals(cmd))
		{
			String tpl = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_regiontpl.htm", player);
			StringBuilder rl = new StringBuilder("");

			for(int townId = 0; townId < _towns.length; townId++)
			{
				int[] town = _towns[townId];

				String reg = tpl.replace("%region_bypass%", "_bbsregion_" + String.valueOf(townId));
				reg = reg.replace("%region_name%", HtmlUtils.htmlNpcString(town[0]));
				reg = reg.replace("%region_desc%", "&$498;: &$1157;, &$1434;, &$645;.");
				reg = reg.replace("%region_type%", "l2ui.bbs_folder");
				int sellers = 0;

				int rx = town[1];
				int ry = town[2];
				int offset = 0;

				for(Player seller : GameObjectsStorage.getPlayers())
				{
					int tx = MapUtils.regionX(seller);
					int ty = MapUtils.regionY(seller);

					if(tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
						if(seller.getPrivateStoreType() > 0 && seller.getPrivateStoreType() != Player.STORE_OBSERVING_GAMES)
							sellers++;
				}
				reg = reg.replace("%sellers_count%", String.valueOf(sellers));
				rl.append(reg);
			}
			HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/bbs_region_list.htm", player);
			String html = tpls.get(0);
			html = html.replace("%REGION_LIST%", rl.toString());
			html = html.replace("<?tree_menu?>", tpls.get(1));

			ShowBoardPacket.separateAndSend(html, player);
		}
		else if("bbsregion".equals(cmd))
		{
			String tpl = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_regiontpl.htm", player);
			int townId = Integer.parseInt(st.nextToken());
			StringBuilder rl = new StringBuilder("");
			int[] town = _towns[townId];
			player.setSessionVar("add_fav", bypass + "&Region " + townId);

			for(int type = 0; type < _regionTypes.length; type++)
			{
				String reg = tpl.replace("%region_bypass%", "_bbsreglist_" + townId + "_" + type + "_1_0_");
				reg = reg.replace("%region_name%", _regionTypes[type]);
				reg = reg.replace("%region_desc%", _regionTypes[type] + ".");
				reg = reg.replace("%region_type%", "l2ui.bbs_board");
				int sellers = 0;

				int rx = town[1];
				int ry = town[2];
				int offset = 0;

				for(Player seller : GameObjectsStorage.getPlayers())
				{
					int tx = MapUtils.regionX(seller);
					int ty = MapUtils.regionY(seller);

					if(tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
					{
						if(type == 0 && (seller.getPrivateStoreType() == Player.STORE_PRIVATE_SELL || seller.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE))
							sellers++;
						else if(type == 1 && seller.getPrivateStoreType() == Player.STORE_PRIVATE_BUY)
							sellers++;
						else if(type == 2 && seller.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
							sellers++;
					}
				}

				reg = reg.replace("%sellers_count%", String.valueOf(sellers));
				rl.append(reg);
			}

			HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/bbs_region_list.htm", player);
			String html = tpls.get(0);
			html = html.replace("%REGION_LIST%", rl.toString());
			html = html.replace("<?tree_menu?>", tpls.get(2).replace("%TREE%", "&nbsp;>&nbsp;" + HtmlUtils.htmlNpcString(town[0])));

			ShowBoardPacket.separateAndSend(html, player);
		}
		else if("bbsreglist".equals(cmd))
		{
			int townId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			int byItem = Integer.parseInt(st.nextToken());
			String search = st.hasMoreTokens() ? st.nextToken().toLowerCase() : "";
			int[] town = _towns[townId];
			player.setSessionVar("add_fav", bypass + "&Region " + townId + " " + _regionTypes[type]);

			List<Player> sellers = getSellersList(townId, type, search, byItem == 1);

			int start = (page - 1) * SELLER_PER_PAGE;
			int end = Math.min(page * SELLER_PER_PAGE, sellers.size());

			String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_region_sellers.htm", player);

			if(page == 1)
			{
				html = html.replace("%ACTION_GO_LEFT%", "");
				html = html.replace("%GO_LIST%", "");
				html = html.replace("%NPAGE%", "1");
			}
			else
			{
				html = html.replace("%ACTION_GO_LEFT%", "bypass _bbsreglist_" + townId + "_" + type + "_" + (page - 1) + "_" + byItem + "_" + search);
				html = html.replace("%NPAGE%", String.valueOf(page));
				StringBuilder goList = new StringBuilder("");
				for(int i = page > 10 ? page - 10 : 1; i < page; i++)
					goList.append("<td><a action=\"bypass _bbsreglist_").append(townId).append("_").append(type).append("_").append(i).append("_").append(byItem).append("_").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");

				html = html.replace("%GO_LIST%", goList.toString());
			}

			int pages = Math.max(sellers.size() / SELLER_PER_PAGE, 1);
			if(sellers.size() > pages * SELLER_PER_PAGE)
				pages++;

			if(pages > page)
			{
				html = html.replace("%ACTION_GO_RIGHT%", "bypass _bbsreglist_" + townId + "_" + type + "_" + (page + 1) + "_" + byItem + "_" + search);
				int ep = Math.min(page + 10, pages);
				StringBuilder goList = new StringBuilder("");
				for(int i = page + 1; i <= ep; i++)
					goList.append("<td><a action=\"bypass _bbsreglist_").append(townId).append("_").append(type).append("_").append(i).append("_").append(byItem).append("_").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");

				html = html.replace("%GO_LIST2%", goList.toString());
			}
			else
			{
				html = html.replace("%ACTION_GO_RIGHT%", "");
				html = html.replace("%GO_LIST2%", "");
			}

			StringBuilder seller_list = new StringBuilder("");
			String tpl = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_region_stpl.htm", player);

			for(int i = start; i < end; i++)
			{
				Player seller = sellers.get(i);
				List<TradeItem> tl = seller.getTradeList();
				List<ManufactureItem> cl = seller.getCreateList();

				if(tl == null && cl == null)
					continue;

				String stpl = tpl;
				stpl = stpl.replace("%view_bypass%", "bypass _bbsregview_" + townId + "_" + type + "_" + page + "_" + seller.getObjectId() + "_" + byItem + "_" + search);
				stpl = stpl.replace("%seller_name%", seller.getName());
				String title = "-";
				if(seller.canTalkWith(player))
				{
					if(type == 0)
						title = tl != null && seller.getSellStoreName() != null && !seller.getSellStoreName().isEmpty() ? seller.getSellStoreName() : "-";
					else if(type == 1)
						title = tl != null && seller.getBuyStoreName() != null && !seller.getBuyStoreName().isEmpty() ? seller.getBuyStoreName() : "-";
					else if(type == 2 && seller.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
						title = cl != null && seller.getManufactureName() != null && !seller.getManufactureName().isEmpty() ? seller.getManufactureName() : "-";

					title = title.replace("<", "");
					title = title.replace(">", "");
					title = title.replace("&", "");
					title = title.replace("$", "");

					if(title.isEmpty())
						title = "-";
				}

				stpl = stpl.replace("%seller_title%", title);

				seller_list.append(stpl);
			}

			html = html.replace("%SELLER_LIST%", seller_list.toString());
			html = html.replace("%search_bypass%", "_bbsregsearch_" + townId + "_" + type);
			html = html.replace("%TREE%", "&nbsp;>&nbsp;<a action=\"bypass _bbsregion_" + townId + "\">" + HtmlUtils.htmlNpcString(town[0]) + "</a>&nbsp;>&nbsp;" + _regionTypes[type]);

			ShowBoardPacket.separateAndSend(html, player);
		}
		else if("bbsregview".equals(cmd))
		{
			int townId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			int objectId = Integer.parseInt(st.nextToken());
			int byItem = Integer.parseInt(st.nextToken());
			String search = st.hasMoreTokens() ? st.nextToken().toLowerCase() : "";
			int[] town = _towns[townId];

			Player seller = World.getPlayer(objectId);
			if(seller == null || seller.getPrivateStoreType() == 0)
			{
				onBypassCommand(player, "_bbsreglist_" + townId + "_" + type + "_" + page + "_" + byItem + "_" + search);
				return;
			}

			String title = "-";
			String tpl = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_region_storetpl.htm", player);
			StringBuilder sb = new StringBuilder("");

			if(type < 2)
			{
				List<TradeItem> sl = type == 0 ? seller.getSellList() : seller.getBuyList();
				List<TradeItem> tl = seller.getTradeList();

				if(sl == null || sl.isEmpty() || tl == null)
				{
					onBypassCommand(player, "_bbsreglist_" + townId + "_" + type + "_" + page + "_" + byItem + "_" + search);
					return;
				}

				if(seller.canTalkWith(player))
				{
					if(type == 0 && seller.getSellStoreName() != null && !seller.getSellStoreName().isEmpty())
						title = seller.getSellStoreName();
					else if(type == 1 && seller.getBuyStoreName() != null && !seller.getBuyStoreName().isEmpty())
						title = seller.getBuyStoreName();
				}

				for(TradeItem ti : sl)
				{
					ItemTemplate item = ItemHolder.getInstance().getTemplate(ti.getItemId());
					if(item != null)
					{
						String stpl = tpl.replace("%item_name%", item.getName() + (item.isEquipment() && ti.getEnchantLevel() > 0 ? " +" + ti.getEnchantLevel() : ""));
						stpl = stpl.replace("%item_img%", item.getIcon());
						stpl = stpl.replace("%item_count%", String.valueOf(ti.getCount()));
						stpl = stpl.replace("%item_price%", String.format("%,3d", ti.getOwnersPrice()).replace(" ", ","));

						String desc = "";
						if(item.getGrade() != ItemGrade.NONE)
						{
							desc = _grade[item.getGrade().ordinal() - 1];
							desc += item.getCrystalCount() > 0 ? (player.isLangRus() ? " Кристаллов: " : " Crystals: ") + item.getCrystalCount() + ";&nbsp;" : ";&nbsp;";
						}

						if(item.isEquipment())
							if(ti.getAttackElement() >= 0 && ti.getAttackElementValue() > 0)
								desc += "&$1620;: " + _elements[ti.getAttackElement()] + " +" + ti.getAttackElementValue();
							else if(ti.getDefenceFire() > 0 || ti.getDefenceWater() > 0 || ti.getDefenceWind() > 0 || ti.getDefenceEarth() > 0 || ti.getDefenceHoly() > 0 || ti.getDefenceUnholy() > 0)
							{
								desc += "&$1651;:";
								if(ti.getDefenceFire() > 0)
									desc += " &$1622; +" + ti.getDefenceFire() + ";&nbsp;";
								if(ti.getDefenceWater() > 0)
									desc += " &$1623; +" + ti.getDefenceWater() + ";&nbsp;";
								if(ti.getDefenceWind() > 0)
									desc += " &$1624; +" + ti.getDefenceWind() + ";&nbsp;";
								if(ti.getDefenceEarth() > 0)
									desc += " &$1625; +" + ti.getDefenceEarth() + ";&nbsp;";
								if(ti.getDefenceHoly() > 0)
									desc += " &$1626; +" + ti.getDefenceHoly() + ";&nbsp;";
								if(ti.getDefenceUnholy() > 0)
									desc += " &$1627; +" + ti.getDefenceUnholy() + ";&nbsp;";
							}
						if(item.isStackable())
							desc += player.isLangRus() ? "Стыкуемый;&nbsp;" : "Stackable;&nbsp;";
						if(item.isShadowItem())
							desc += player.isLangRus() ? "Теневой предмет;&nbsp;" : "Shadow item;&nbsp;";
						if(item.isTemporal())
							desc += player.isLangRus() ? "Временный;&nbsp;" : "Temporal;&nbsp;";

						stpl = stpl.replace("%item_desc%", desc);
						sb.append(stpl);
					}
				}
			}
			else
			{
				List<ManufactureItem> cl = seller.getCreateList();
				if(cl == null)
				{
					onBypassCommand(player, "_bbsreglist_" + townId + "_" + type + "_" + page + "_" + byItem + "_" + search);
					return;
				}

				if(!seller.canTalkWith(player) || (title = seller.getManufactureName()) == null)
					title = "-";

				for(ManufactureItem mi : cl)
				{
					RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(mi.getRecipeId() - 1);
					if(recipe == null || recipe.getProducts().length == 0)
						continue;

					ChancedItemData product = recipe.getProducts()[0];
					ItemTemplate item = ItemHolder.getInstance().getTemplate(product.getId());
					if(item == null)
						continue;

					String stpl = tpl.replace("%item_name%", item.getName());
					stpl = stpl.replace("%item_img%", item.getIcon());
					stpl = stpl.replace("%item_count%", "N/A");
					stpl = stpl.replace("%item_price%", String.format("%,3d", mi.getCost()).replace(" ", ","));

					String desc = "";
					if(item.getGrade() != ItemGrade.NONE)
						desc = _grade[item.getGrade().ordinal() - 1] + (item.getCrystalCount() > 0 ? (player.isLangRus() ? " Кристаллов: " : " Crystals: ") + item.getCrystalCount() + ";&nbsp;" : ";&nbsp;");

					if(item.isStackable())
						desc = player.isLangRus() ? "Стыкуемый;&nbsp;" : "Stackable;&nbsp;";

					stpl = stpl.replace("%item_desc%", desc);
					sb.append(stpl);
				}
			}

			String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_region_view.htm", player);

			html = html.replace("%sell_type%", _regionTypes[type]);

			title = title.replace("<", "");
			title = title.replace(">", "");
			title = title.replace("&", "");
			title = title.replace("$", "");
			if(title.isEmpty())
				title = "-";
			html = html.replace("%title%", title);
			html = html.replace("%char_name%", seller.getName());
			html = html.replace("%object_id%", String.valueOf(seller.getObjectId()));
			html = html.replace("%STORE_LIST%", sb.toString());
			html = html.replace("%list_bypass%", "_bbsreglist_" + townId + "_" + type + "_" + page + "_" + byItem + "_" + search);
			html = html.replace("%TREE%", "&nbsp;>&nbsp;<a action=\"bypass _bbsregion_" + townId + "\">" + HtmlUtils.htmlNpcString(town[0]) + "</a>&nbsp;>&nbsp;<a action=\"bypass _bbsreglist_" + townId + "_" + type + "_" + page + "_" + byItem + "_\">" + _regionTypes[type] + "</a>&nbsp;>&nbsp;" + seller.getName());

			ShowBoardPacket.separateAndSend(html, player);
		}
		else if("bbsregtarget".equals(cmd))
		{
			int objectId = Integer.parseInt(st.nextToken());
			Player seller = World.getPlayer(objectId);
			if(seller != null)
			{
				player.sendPacket(new RadarControlPacket(0, 2, seller.getLoc()));
				if(player.knowsObject(seller))
				{
					player.setTarget(seller);
					seller.broadcastRelation();
				}
			}
			else
				player.sendActionFailed();
		}

	}

	@Override
	protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		if("bbsregsearch".equals(cmd))
		{
			int townId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			String byItem = "Item".equals(arg4) ? "1" : "0";
			if(arg3 == null)
				arg3 = "";

			arg3 = arg3.replace("<", "");
			arg3 = arg3.replace(">", "");
			arg3 = arg3.replace("&", "");
			arg3 = arg3.replace("$", "");

			if(arg3.length() > 30)
				arg3 = arg3.substring(0, 30);

			onBypassCommand(player, "_bbsreglist_" + townId + "_" + type + "_1_" + byItem + "_" + arg3);
		}
	}

	private static List<Player> getSellersList(int townId, int type, String search, boolean byItem)
	{
		List<Player> list = new ArrayList<Player>();
		int town[] = _towns[townId];
		int rx = town[1];
		int ry = town[2];
		int offset = 0;

		for(Player seller : GameObjectsStorage.getPlayers())
		{
			int tx = MapUtils.regionX(seller);
			int ty = MapUtils.regionY(seller);

			if(tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
			{
				List<TradeItem> tl = seller.getTradeList();
				List<ManufactureItem> cl = seller.getCreateList();
				if(seller.getPrivateStoreType() > 0)
					if(type == 0 && tl != null && (seller.getPrivateStoreType() == Player.STORE_PRIVATE_SELL || seller.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE))
						list.add(seller);
					else if(type == 1 && tl != null && seller.getPrivateStoreType() == Player.STORE_PRIVATE_BUY)
						list.add(seller);
					else if(type == 2 && cl != null && seller.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
						list.add(seller);
			}
		}

		if(!search.isEmpty() && !list.isEmpty())
		{
			List<Player> s_list = new ArrayList<Player>();
			for(Player seller : list)
			{
				List<TradeItem> tl = seller.getTradeList();
				List<ManufactureItem> cl = seller.getCreateList();
				if(byItem)
				{
					if((type == 0 || type == 1) && tl != null)
					{
						List<TradeItem> sl = type == 0 ? seller.getSellList() : seller.getBuyList();
						if(sl != null)
							for(TradeItem ti : sl)
							{
								ItemTemplate item = ItemHolder.getInstance().getTemplate(ti.getItemId());
								if(item != null && item.getName() != null && item.getName().toLowerCase().contains(search))
								{
									s_list.add(seller);
									break;
								}
							}
					}
					else if(type == 2 && cl != null)
						for(ManufactureItem mi : cl)
						{
							RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(mi.getRecipeId() - 1);
							if(recipe != null && recipe.getProducts().length > 0)
							{
								ChancedItemData product = recipe.getProducts()[0];
								ItemTemplate item = ItemHolder.getInstance().getTemplate(product.getId());
								if(item != null && item.getName() != null && item.getName().toLowerCase().contains(search))
								{
									s_list.add(seller);
									break;
								}
							}
						}
				}
				else if(type == 0 && tl != null && seller.getSellStoreName() != null && seller.getSellStoreName().toLowerCase().contains(search))
					s_list.add(seller);
				else if(type == 1 && tl != null && seller.getBuyStoreName() != null && seller.getBuyStoreName().toLowerCase().contains(search))
					s_list.add(seller);
				else if(type == 2 && cl != null && seller.getCreateList() != null && seller.getManufactureName() != null && seller.getManufactureName().toLowerCase().contains(search))
					s_list.add(seller);
			}
			list = s_list;
		}

		if(!list.isEmpty())
		{
			Player[] players = new Player[list.size()];
			list.toArray(players);
			Arrays.sort(players, new PlayersComparator<Player>());
			list.clear();
			list.addAll(Arrays.asList(players));
		}

		return list;
	}

	private static class PlayersComparator<T> implements Comparator<T>
	{
		@Override
		public int compare(Object o1, Object o2)
		{
			if(o1 instanceof Player && o2 instanceof Player)
			{
				Player p1 = (Player) o1;
				Player p2 = (Player) o2;
				return p1.getName().compareTo(p2.getName());
			}
			return 0;
		}
	}
}
