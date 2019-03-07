/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.others.ClanHallAuctioneer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.l2jmobius.gameserver.data.xml.impl.ClanHallData;
import com.l2jmobius.gameserver.instancemanager.ClanHallAuctionManager;
import com.l2jmobius.gameserver.model.ClanPrivilege;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.clanhallauction.Bidder;
import com.l2jmobius.gameserver.model.clanhallauction.ClanHallAuction;
import com.l2jmobius.gameserver.model.entity.ClanHall;
import com.l2jmobius.gameserver.model.html.PageBuilder;
import com.l2jmobius.gameserver.model.html.PageResult;
import com.l2jmobius.gameserver.model.html.formatters.BypassParserFormatter;
import com.l2jmobius.gameserver.model.html.pagehandlers.NextPrevPageHandler;
import com.l2jmobius.gameserver.model.html.styles.ButtonsStyle;
import com.l2jmobius.gameserver.model.itemcontainer.Inventory;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmobius.gameserver.util.BypassParser;

import ai.AbstractNpcAI;

/**
 * Clan Hall Auctioneer AI.
 * @author Sdw
 */
public final class ClanHallAuctioneer extends AbstractNpcAI
{
	// NPC
	private static final int AUCTIONEER = 30767; // Auctioneer
	
	public ClanHallAuctioneer()
	{
		addStartNpc(AUCTIONEER);
		addTalkId(AUCTIONEER);
		addFirstTalkId(AUCTIONEER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "ClanHallAuctioneer.html":
			{
				htmltext = event;
				break;
			}
			case "map":
			{
				htmltext = getHtm(player, "ClanHallAuctioneer-map.html");
				htmltext = htmltext.replace("%MAP%", npc.getParameters().getString("fnAgitMap", "gludio"));
				htmltext = htmltext.replace("%TOWN_NAME%", npc.getCastle().getName());
				break;
			}
			case "cancelBid":
			{
				final L2Clan clan = player.getClan();
				if (clan == null)
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
					return htmltext;
				}
				
				if (!player.isClanLeader() || (clan.getLevel() < 2))
				{
					player.sendPacket(SystemMessageId.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_ABOVE_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
					return htmltext;
				}
				
				final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionByClan(clan);
				if (clanHallAuction == null)
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
					return htmltext;
				}
				
				// THE_CLAN_DOES_NOT_OWN_A_CLAN_HALL
				
				htmltext = getHtm(player, "ClanHallAuctioneer-cancelBid.html");
				htmltext = htmltext.replaceAll("%myBid%", String.valueOf(clanHallAuction.getClanBid(clan)));
				htmltext = htmltext.replaceAll("%myBidRemain%", String.valueOf(clanHallAuction.getClanBid(clan) * 9));
				break;
			}
			case "cancel":
			{
				final L2Clan clan = player.getClan();
				if (clan == null)
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
					return htmltext;
				}
				
				if (!player.isClanLeader() || (clan.getLevel() < 2))
				{
					player.sendPacket(SystemMessageId.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_ABOVE_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
					return htmltext;
				}
				
				final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionByClan(clan);
				if (clanHallAuction == null)
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
					return htmltext;
				}
				
				// THE_CLAN_DOES_NOT_OWN_A_CLAN_HALL
				
				clanHallAuction.removeBid(clan);
				
				player.sendPacket(SystemMessageId.YOU_HAVE_CANCELED_YOUR_BID);
				break;
			}
			case "rebid":
			{
				if (player.hasClanPrivilege(ClanPrivilege.CH_AUCTION))
				{
					final L2Clan clan = player.getClan();
					final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionByClan(clan);
					if (clanHallAuction != null)
					{
						final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
						
						htmltext = getHtm(player, "ClanHallAuctioneer-bid2.html");
						htmltext = htmltext.replaceAll("%id%", String.valueOf(clanHallAuction.getClanHallId()));
						htmltext = htmltext.replaceAll("%minBid%", String.valueOf(clanHallAuction.getHighestBid()));
						htmltext = htmltext.replaceAll("%myBid%", String.valueOf(clanHallAuction.getClanBid(clan)));
						htmltext = htmltext.replace("%auctionEnd%", builder.appendPattern("dd/MM/yyyy HH").appendLiteral(" hour ").appendPattern("mm").appendLiteral(" minutes").toFormatter().format(Instant.ofEpochMilli(System.currentTimeMillis() + clanHallAuction.getRemaingTime()).atZone(ZoneId.systemDefault())));
					}
				}
				else
				{
					player.sendPacket(SystemMessageId.YOU_MUST_HAVE_RIGHTS_TO_A_CLAN_HALL_AUCTION_IN_ORDER_TO_MAKE_A_BID_FOR_PROVISIONAL_CLAN_HALL); // FIX ME
				}
				break;
			}
			case "my_auction":
			{
				final L2Clan clan = player.getClan();
				if (clan == null)
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
					return htmltext;
				}
				
				if (!player.isClanLeader() || (clan.getLevel() < 2))
				{
					player.sendPacket(SystemMessageId.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_ABOVE_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
					return htmltext;
				}
				
				final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionByClan(clan);
				if (clanHallAuction == null)
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
					return htmltext;
				}
				
				// THE_CLAN_DOES_NOT_OWN_A_CLAN_HALL
				
				final ClanHall clanHall = ClanHallData.getInstance().getClanHallById(clanHallAuction.getClanHallId());
				final L2Clan owner = clanHall.getOwner();
				final long remainingTime = clanHallAuction.getRemaingTime();
				final Instant endTime = Instant.ofEpochMilli(System.currentTimeMillis() + remainingTime);
				
				final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
				htmltext = getHtm(player, "ClanHallAuctioneer-bidInfo.html");
				htmltext = htmltext.replaceAll("%id%", String.valueOf(clanHall.getResidenceId()));
				htmltext = htmltext.replace("%owner%", owner != null ? owner.getName() : "");
				htmltext = htmltext.replace("%clanLeader%", owner != null ? owner.getLeaderName() : "");
				htmltext = htmltext.replace("%rent%", String.valueOf(clanHall.getLease()));
				htmltext = htmltext.replace("%grade%", String.valueOf(clanHall.getGrade().getGradeValue()));
				htmltext = htmltext.replace("%minBid%", String.valueOf(clanHallAuction.getHighestBid()));
				htmltext = htmltext.replace("%myBid%", String.valueOf(clanHallAuction.getClanBid(clan)));
				htmltext = htmltext.replace("%bidNumber%", String.valueOf(clanHallAuction.getBidCount()));
				htmltext = htmltext.replace("%auctionEnd%", builder.appendPattern("dd/MM/yyyy HH").appendLiteral(" hour ").appendPattern("mm").appendLiteral(" minutes").toFormatter().format(endTime.atZone(ZoneId.systemDefault())));
				htmltext = htmltext.replace("%hours%", String.valueOf(TimeUnit.MILLISECONDS.toHours(remainingTime)));
				htmltext = htmltext.replace("%minutes%", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(remainingTime % 3600000)));
				break;
			}
			default:
			{
				if (event.startsWith("auctionList"))
				{
					processClanHallBypass(player, npc, new BypassParser(event));
					return htmltext;
				}
				else if (event.startsWith("bid"))
				{
					processBidBypass(player, npc, new BypassParser(event));
					return htmltext;
				}
				else if (event.startsWith("listBidder"))
				{
					processBiddersBypass(player, npc, new BypassParser(event));
					return htmltext;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "ClanHallAuctioneer.html";
	}
	
	private void processClanHallBypass(L2PcInstance player, L2Npc npc, BypassParser parser)
	{
		final int page = parser.getInt("page", 0);
		final int clanHallId = parser.getInt("id", 0);
		
		if (clanHallId > 0)
		{
			final ClanHall clanHall = ClanHallData.getInstance().getClanHallById(clanHallId);
			if (clanHall != null)
			{
				final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHallId);
				final L2Clan owner = clanHall.getOwner();
				final long remainingTime = clanHallAuction.getRemaingTime();
				final Instant endTime = Instant.ofEpochMilli(System.currentTimeMillis() + remainingTime);
				final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setHtml(getHtm(player, "ClanHallAuctioneer-info.html"));
				
				html.replace("%id%", clanHall.getResidenceId());
				html.replace("%owner%", owner != null ? owner.getName() : "");
				html.replace("%clanLeader%", owner != null ? owner.getLeaderName() : "");
				html.replace("%rent%", clanHall.getLease());
				html.replace("%grade%", clanHall.getGrade().getGradeValue());
				html.replace("%minBid%", clanHallAuction.getHighestBid());
				html.replace("%bidNumber%", clanHallAuction.getBidCount());
				html.replace("%auctionEnd%", builder.appendPattern("dd/MM/yyyy HH").appendLiteral(" hour ").appendPattern("mm").appendLiteral(" minutes").toFormatter().format(endTime.atZone(ZoneId.systemDefault())));
				html.replace("%hours%", TimeUnit.MILLISECONDS.toHours(remainingTime));
				html.replace("%minutes%", TimeUnit.MILLISECONDS.toMinutes(remainingTime % 3600000));
				player.sendPacket(html);
			}
		}
		else
		{
			final List<ClanHall> clanHalls = ClanHallData.getInstance().getFreeAuctionableHall();
			if (clanHalls.isEmpty())
			{
				player.sendPacket(SystemMessageId.THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION);
			}
			else
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId(), getHtm(player, "ClanHallAuctioneer-list.html"));
				//@formatter:off
				final PageResult result = PageBuilder.newBuilder(clanHalls, 8, "bypass -h Quest ClanHallAuctioneer auctionList")
					.currentPage(page)
					.pageHandler(NextPrevPageHandler.INSTANCE)
					.formatter(BypassParserFormatter.INSTANCE)
					.style(ButtonsStyle.INSTANCE)
					.bodyHandler((pages, clanHall, sb) ->
				{
					final ClanHallAuction auction = ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHall.getResidenceId());
					if(auction == null)
					{
						System.out.println(clanHall.getResidenceId());
						return;
					}
					sb.append("<tr><td width=50><font color=\"aaaaff\">&^");
					sb.append(clanHall.getResidenceId());
					sb.append(";</font></td><td width=100><a action=\"bypass -h Quest ClanHallAuctioneer auctionList id=");
					sb.append(clanHall.getResidenceId());
					sb.append("\"><font color=\"ffffaa\">&%");
					sb.append(clanHall.getResidenceId());
					sb.append(";[0]</font></a></td><td width=50>");
					sb.append(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Instant.ofEpochMilli(System.currentTimeMillis() + auction.getRemaingTime()).atZone(ZoneId.systemDefault())));
					sb.append("</td><td width=70 align=right><font color=\"aaffff\">");
					sb.append(auction.getHighestBid());
					sb.append("</font></td></tr>");
				}).build();
				//@formatter:on
				html.replace("%pages%", result.getPages() > 0 ? result.getPagerTemplate() : "");
				html.replace("%agitList%", result.getBodyTemplate().toString());
				player.sendPacket(html);
			}
		}
	}
	
	private void processBidBypass(L2PcInstance player, L2Npc npc, BypassParser parser)
	{
		final int clanHallId = parser.getInt("id", 0);
		final long bid = parser.getLong("bid", 0);
		
		if (clanHallId > 0)
		{
			final ClanHall clanHall = ClanHallData.getInstance().getClanHallById(clanHallId);
			if (clanHall == null)
			{
				return;
			}
			final L2Clan clan = player.getClan();
			if (clan == null)
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
				return;
			}
			
			if (!player.isClanLeader() || (clan.getLevel() < 2))
			{
				player.sendPacket(SystemMessageId.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_ABOVE_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
				return;
			}
			final ClanHall playerClanHall = ClanHallData.getInstance().getClanHallByClan(clan);
			if (playerClanHall != null)
			{
				player.sendPacket(SystemMessageId.EMPTY_462);
				return;
			}
			
			if (ClanHallAuctionManager.getInstance().checkForClanBid(clanHallId, clan))
			{
				player.sendPacket(SystemMessageId.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
				return;
			}
			if (bid == 0)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setHtml(getHtm(player, "ClanHallAuctioneer-bid1.html"));
				html.replace("%clanAdena%", clan.getWarehouse().getAdena());
				html.replace("%minBid%", ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHallId).getHighestBid());
				html.replace("%id%", clanHall.getResidenceId());
				player.sendPacket(html);
			}
			else
			{
				player.sendPacket(SystemMessageId.YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION);
				if (bid > Inventory.MAX_ADENA)
				{
					player.sendPacket(SystemMessageId.THE_HIGHEST_BID_IS_OVER_999_9_BILLION_THEREFORE_YOU_CANNOT_PLACE_A_BID);
					return;
				}
				final ClanHallAuction auction = ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHallId);
				if (bid < auction.getHighestBid())
				{
					player.sendPacket(SystemMessageId.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
					return;
				}
				else if (clan.getWarehouse().destroyItemByItemId("Clan Hall Auction", Inventory.ADENA_ID, bid, player, null) == null)
				{
					player.sendPacket(SystemMessageId.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					return;
				}
				
				final Optional<Bidder> bidder = auction.getHighestBidder();
				if (bidder.isPresent())
				{
					auction.returnAdenas(bidder.get());
					final L2PcInstance leader = bidder.get().getClan().getLeader().getPlayerInstance();
					if ((leader != null) && leader.isOnline())
					{
						leader.sendPacket(SystemMessageId.YOU_WERE_OUTBID_THE_NEW_HIGHEST_BID_IS_S1_ADENA);
					}
				}
				
				auction.addBid(player.getClan(), bid);
				
				player.sendPacket(SystemMessageId.YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED);
				
			}
		}
	}
	
	private void processBiddersBypass(L2PcInstance player, L2Npc npc, BypassParser parser)
	{
		final int page = parser.getInt("page", 0);
		final int clanHallId = parser.getInt("id", 0);
		if (clanHallId > 0)
		{
			final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHallId);
			if (clanHallAuction == null)
			{
				return;
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId(), getHtm(player, "ClanHallAuctioneer-bidderList.html"));
			//@formatter:off
			final PageResult result = PageBuilder.newBuilder(clanHallAuction.getBids().values().stream().sorted(Comparator.comparingLong(Bidder::getTime).reversed()).collect(Collectors.toList()), 10, "bypass -h Quest ClanHallAuctioneer auctionList")
				.currentPage(page)
				.pageHandler(NextPrevPageHandler.INSTANCE)
				.formatter(BypassParserFormatter.INSTANCE)
				.style(ButtonsStyle.INSTANCE)
				.bodyHandler((pages, bidder, sb) ->
			{
				sb.append("<tr><td width=100>");
				sb.append(bidder.getClanName());
				sb.append("</td><td width=100>");
				sb.append(bidder.getBid());
				sb.append("</td><td width=70>");
				sb.append(bidder.getFormattedTime());
				sb.append("</td></tr>");
			}).build();
			//@formatter:on
			html.replace("%pages%", result.getPages() > 0 ? result.getPagerTemplate() : "");
			html.replace("%bidderList%", result.getBodyTemplate().toString());
			html.replace("%id%", clanHallAuction.getClanHallId());
			player.sendPacket(html);
		}
	}
	
	public static void main(String[] args)
	{
		new ClanHallAuctioneer();
	}
}