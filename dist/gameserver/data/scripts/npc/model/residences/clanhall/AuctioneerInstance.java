package npc.model.residences.clanhall;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import l2s.commons.collections.MultiValueSet;
import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.dao.SiegeClanDAO;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2s.gameserver.model.entity.events.impl.InstantClanHallAuctionEvent;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.events.objects.AuctionSiegeClanObject;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.entity.residence.clanhall.InstantClanHall;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.clanhall.AuctionClanHall;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.Privilege;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.HtmlUtils;

/**
 * @author VISTALL
 * @date 17:40/14.06.2011
 */
public class AuctioneerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.KOREA);
	private final static long WEEK = 7 * 24 * 60 * 60 * 1000L;

	private final static int CH_PAGE_SIZE = 7;
	private final static String CH_IN_LIST =
			"\t<tr>\n" +
			"\t\t<td width=50>\n" +
			"\t\t\t<font color=\"aaaaff\">&^%id%;</font>\n" +
			"\t\t</td>\n" + "\t\t<td width=100>\n" +
			"\t\t\t<a action=\"bypass -h npc_%objectId%_info %id%\"><font color=\"ffffaa\">&%%id%;[%size%]</font></a>\n" +
			"\t\t</td>\n" +
			"\t\t<td width=50>%date%</td>\n" +
			"\t\t<td width=70 align=right>\n" +
			"\t\t\t<font color=\"aaffff\">%min_bid%</font>\n" +
			"\t\t</td>\n" +
			"\t</tr>";

	private final static int BIDDER_PAGE_SIZE = 10;
	private final static String BIDDER_IN_LIST =
			"\t<tr>\n" +
			"\t\t<td width=100><font color=\"aaaaff\">&%%id%;</font></td>\n" +
			"\t\t<td width=100><font color=\"ffffaa\">%clan_name%</font></td>\n" +
			"\t\t<td width=70>%date%</td>\n" +
			"\t</tr>";

	public AuctioneerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer tokenizer = new StringTokenizer(command.replace("\r\n", "<br1>"));
		String actualCommand = tokenizer.nextToken();
		if(actualCommand.equalsIgnoreCase("map"))
			showChatWindow(player, getMapDialog(), false, "<?locale_prefix?>", player.getNetConnection().getLanguage().getDatName());
		//=============================================================================================
		//						Выводит весь список активніх аукционов
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("list_all"))
		{
			int page = Integer.parseInt(tokenizer.nextToken());

			List<ClanHallAuctionEvent> events = new ArrayList<ClanHallAuctionEvent>();
			for(AuctionClanHall ch : ResidenceHolder.getInstance().getResidenceList(AuctionClanHall.class))
				if(ch.getSiegeEvent() != null && ch.getSiegeEvent().isInProgress())
					events.add(ch.<ClanHallAuctionEvent>getSiegeEvent());

			if(events.isEmpty())
			{
				player.sendPacket(SystemMsg.THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION);
				showChatWindow(player, 0, false);
				return;
			}

			int min = CH_PAGE_SIZE * page;
			int max = min + CH_PAGE_SIZE;
			if(min > events.size())
			{
				min = 0;
				max = min + CH_PAGE_SIZE;
			}

			if(max > events.size())
				max = events.size();

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_list_clanhalls.htm");

			StringBuilder b = new StringBuilder();
			for(int i = min; i < max; i++)
			{
				ClanHallAuctionEvent event = events.get(i);
				List<AuctionSiegeClanObject> attackers = event.getObjects(ClanHallAuctionEvent.ATTACKERS);
				Calendar siegeDate = event.getResidence().getSiegeDate();

				String out = CH_IN_LIST.replace("%id%", String.valueOf(event.getId())).replace("%min_bid%", String.valueOf(event.getResidence().getAuctionMinBid())).replace("%size%", String.valueOf(attackers.size())).replace("%date%", DATE_FORMAT.format(siegeDate.getTimeInMillis()));

				b.append(out);
			}

			msg.replace("%list%", b.toString());
			if(events.size() > max)
			{
				msg.replace("%next_button%", "<td>" + HtmlUtils.NEXT_BUTTON + "</td>");
				msg.replace("%next_bypass%", "-h npc_%objectId%_list_all " + (page + 1));
			}
			else
				msg.replace("%next_button%", StringUtils.EMPTY);

			if(page != 0)
			{
				msg.replace("%prev_button%", "<td>" + HtmlUtils.PREV_BUTTON + "</td>");
				msg.replace("%prev_bypass%", "-h npc_%objectId%_list_all " + (page - 1));
			}
			else
				msg.replace("%prev_button%", StringUtils.EMPTY);

			player.sendPacket(msg);
		}
		//=============================================================================================
		//		Выводит стандартную инфу про КХ(выбор), если єто один из биддер - есть кнопка отменить
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("info"))
		{
			String fileName = null;

			AuctionClanHall clanHall = null;
			SiegeClanObject siegeClan = null;
			if(tokenizer.hasMoreTokens())
			{
				int id = Integer.parseInt(tokenizer.nextToken());
				clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, id);

				fileName = "residence2/clanhall/agitauctioninfo.htm";
			}
			else
			{
				clanHall = player.getClan() == null ? null : player.getClan().getHasHideout() != 0 ? ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, player.getClan().getHasHideout()) : null;
				if(clanHall != null && clanHall.getSiegeEvent() != null)
				{
					if(clanHall.getSiegeEvent().isInProgress())
						fileName = "residence2/clanhall/agitsaleinfo.htm";
					else
						fileName = "residence2/clanhall/agitinfo.htm";
				}
				else
				{
					for(AuctionClanHall ch : ResidenceHolder.getInstance().getResidenceList(AuctionClanHall.class))
					{
						if(ch.getSiegeEvent() != null && (siegeClan = ch.getSiegeEvent().getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan())) != null)
						{
							clanHall = ch;
							break;
						}
					}

					if(siegeClan == null)
					{
						player.sendPacket(SystemMsg.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
						showChatWindow(player, 0, false);
						return;
					}

					fileName = "residence2/clanhall/agitbidinfo.htm";
				}
			}

			ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
			List<AuctionSiegeClanObject> attackers = auctionEvent == null ? Collections.emptyList() : auctionEvent.getObjects(ClanHallAuctionEvent.ATTACKERS);

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile(fileName);
			msg.replace("<?AGIT_NAME?>", "&%" + String.valueOf(clanHall.getId()) + ";");
			msg.replace("<?AGIT_LOCATION?>", "&^" + String.valueOf(clanHall.getId()) + ";");
			msg.replace("<?AGIT_AUCTION_COUNT?>", String.valueOf(attackers.size()));
			msg.replace("<?AGIT_SIZE?>", String.valueOf(clanHall.getGrade()));
			msg.replace("<?AGIT_LEASE?>", String.valueOf(clanHall.getRentalFee()));

			Clan owner = clanHall.getOwner();

			msg.replace("<?OWNER_PLEDGE_NAME?>", owner == null ? StringUtils.EMPTY : owner.getName());
			msg.replace("<?AGIT_OWNER_PLEDGE_NAME?>", owner == null ? StringUtils.EMPTY : owner.getName());
			msg.replace("<?OWNER_PLEDGE_MASTER?>", owner == null ? StringUtils.EMPTY : owner.getLeaderName());
			msg.replace("<?AGIT_AUCTION_DESC?>", clanHall.getAuctionDescription());
			msg.replace("<?AGIT_AUCTION_MINBID?>", String.valueOf(clanHall.getAuctionMinBid()));

			Calendar c = auctionEvent == null ? Calendar.getInstance() : auctionEvent.getResidence().getSiegeDate();

			msg.replace("<?AGIT_AUCTION_END_YY?>", String.valueOf(c.get(Calendar.YEAR)));
			msg.replace("<?AGIT_AUCTION_END_MM?>", String.valueOf(c.get(Calendar.MONTH) + 1));
			msg.replace("<?AGIT_AUCTION_END_DD?>", String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
			msg.replace("<?AGIT_AUCTION_END_HH?>", String.valueOf(c.get(Calendar.HOUR_OF_DAY)));

			int remainingTime = (int)((c.getTimeInMillis() - System.currentTimeMillis()) / 60000L);

			msg.replace("<?AGIT_AUCTION_REMAIN_HOUR?>", String.valueOf(remainingTime / 60));
			msg.replace("<?AGIT_AUCTION_REMAIN_MINUTE?>", String.valueOf(remainingTime % 60));

			msg.replace("<?AGIT_AUCTION_MYBID?>", siegeClan != null ? String.valueOf(siegeClan.getParam()) : "0");

			msg.replace("<?AGIT_LINK_CANCEL?>", "bypass -h npc_%objectId%_cancel_bid " + clanHall.getId());
			msg.replace("<?AGIT_LINK_BIDLIST?>", "bypass -h npc_%objectId%_bidder_list " + clanHall.getId() + " 0");
			msg.replace("<?AGIT_LINK_BACK?>", "bypass -h npc_%objectId%_Chat 0");
			msg.replace("<?AGIT_LINK_RE?>", "bypass -h npc_%objectId%_bid_start " + clanHall.getId());
			msg.replace("<?AGIT_LINK_BID?>", "bypass -h npc_%objectId%_bid_start " + clanHall.getId());
			msg.replace("<?AGIT_LINK_SALE1?>", "bypass -h npc_%objectId%_register_start");
			msg.replace("<?AGIT_LINK_CANCELAUCTION?>", "bypass -h npc_%objectId%_cancel_start");

			player.sendPacket(msg);
		}
		//=============================================================================================
		//						Выводит список биддеров от аукционна
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("bidder_list"))
		{
			int id = Integer.parseInt(tokenizer.nextToken());
			int page = Integer.parseInt(tokenizer.nextToken());

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, id);
			ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
			if(auctionEvent == null || !auctionEvent.isInProgress())
				return;

			List<AuctionSiegeClanObject> attackers = auctionEvent.getObjects(ClanHallAuctionEvent.ATTACKERS);

			int min = BIDDER_PAGE_SIZE * page;
			int max = min + BIDDER_PAGE_SIZE;
			if(min > attackers.size())
			{
				min = 0;
				max = min + BIDDER_PAGE_SIZE;
			}

			if(max > attackers.size())
				max = attackers.size();

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_bidder_list.htm");
			msg.replace("%id%", String.valueOf(id));

			StringBuilder b = new StringBuilder();
			for(int i = min; i < max; i++)
			{
				AuctionSiegeClanObject siegeClan = attackers.get(i);
				String t = BIDDER_IN_LIST.replace("%id%", String.valueOf(id)).replace("%clan_name%", siegeClan.getClan().getName()).replace("%date%", DATE_FORMAT.format(siegeClan.getDate()));
				b.append(t);
			}
			msg.replace("%list%", b.toString());

			if(attackers.size() > max)
			{
				msg.replace("%next_button%", "<td>" + HtmlUtils.NEXT_BUTTON + "</td>");
				msg.replace("%next_bypass%", "-h npc_%objectId%_bidder_list " + id + " " + (page + 1));
			}
			else
				msg.replace("%next_button%", StringUtils.EMPTY);

			if(page != 0)
			{
				msg.replace("%prev_button%", "<td>" + HtmlUtils.PREV_BUTTON + "</td>");
				msg.replace("%prev_bypass%", "-h npc_%objectId%_bidder_list " + id + " " + (page - 1));
			}
			else
				msg.replace("%prev_button%", StringUtils.EMPTY);

			player.sendPacket(msg);
		}
		//=============================================================================================
		//					Начало установки бидда, появляется окно для ввода, скок ставить
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("bid_start"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			int id = Integer.parseInt(tokenizer.nextToken());

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, id);
			ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
			if(auctionEvent == null || !auctionEvent.isInProgress())
				return;

			long minBid = clanHall.getAuctionMinBid();
			AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
			if(siegeClan != null)
				minBid = siegeClan.getParam();

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_bid_start.htm");
			msg.replace("%id%", String.valueOf(id));
			msg.replace("%min_bid%", String.valueOf(minBid));
			msg.replace("%clan_adena%", String.valueOf(player.getClan().getWarehouse().getCountOf(clanHall.getFeeItemId())));

			player.sendPacket(msg);
		}
		//=============================================================================================
		//					Окно портведжения бида
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("bid_next"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			int id = Integer.parseInt(tokenizer.nextToken());
			long bid = 0;
			if(tokenizer.hasMoreTokens())
			{
				try
				{
					bid = NUMBER_FORMAT.parse(tokenizer.nextToken()).longValue();
				}
				catch(ParseException e)
				{
					//
				}
			}

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, id);
			ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
			if(auctionEvent == null || !auctionEvent.isInProgress())
				return;

			if(!checkBid(player, auctionEvent, bid))
				return;

			long minBid = clanHall.getAuctionMinBid();
			AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
			if(siegeClan != null)
				minBid = siegeClan.getParam();

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_bid_confirm.htm");
			msg.replace("%id%", String.valueOf(id));
			msg.replace("%bid%", String.valueOf(bid));
			msg.replace("%min_bid%", String.valueOf(minBid));

			Calendar c = auctionEvent.getResidence().getSiegeDate();

			msg.replace("%date%", DATE_FORMAT.format(c.getTimeInMillis()));
			msg.replace("%hour%", String.valueOf(c.get(Calendar.HOUR_OF_DAY)));

			player.sendPacket(msg);
		}
		//=============================================================================================
		//					Подтверджает бин, и появляется меню КХ
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("bid_confirm"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			int id = Integer.parseInt(tokenizer.nextToken());
			final long bid = Long.parseLong(tokenizer.nextToken());


			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, id);
			ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
			if(auctionEvent == null || !auctionEvent.isInProgress())
				return;

			for(AuctionClanHall ch : ResidenceHolder.getInstance().getResidenceList(AuctionClanHall.class))
			{
				if(clanHall != ch && ch.getSiegeEvent() != null && ch.getSiegeEvent().isInProgress() && ch.getSiegeEvent().getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan()) != null)
				{
					player.sendPacket(SystemMsg.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
					onBypassFeedback(player, "bid_start " + id);
					return;
				}
			}

			if(!checkBid(player, auctionEvent, bid))
				return;

			long consumeBid = bid;
			AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
			if(siegeClan != null)
			{
				consumeBid -= siegeClan.getParam();
				if(bid <= siegeClan.getParam())
				{
					player.sendPacket(SystemMsg.THE_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_PREVIOUS_BID);
					onBypassFeedback(player, "bid_start " + auctionEvent.getId());
					return;
				}
			}

			player.getClan().getWarehouse().destroyItemByItemId(clanHall.getFeeItemId(), consumeBid);

			if(siegeClan != null)
			{
				siegeClan.setParam(bid);

				SiegeClanDAO.getInstance().update(clanHall, siegeClan);
			}
			else
			{
				siegeClan = new AuctionSiegeClanObject(ClanHallAuctionEvent.ATTACKERS, player.getClan(), bid);
				auctionEvent.addObject(ClanHallAuctionEvent.ATTACKERS, siegeClan);

				SiegeClanDAO.getInstance().insert(clanHall, siegeClan);
			}

			player.sendPacket(SystemMsg.YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED);

			onBypassFeedback(player, "info");
		}
		//=============================================================================================
		//					Открывает окно для подтверджения отказа от ставки
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("cancel_bid"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}
			int id = Integer.parseInt(tokenizer.nextToken());

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, id);
			ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
			if(auctionEvent == null || !auctionEvent.isInProgress())
				return;

			AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
			if(siegeClan == null)
				return;

			long returnVal = (long) (siegeClan.getParam() * 0.9);
			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_bid_cancel.htm");
			msg.replace("%id%", String.valueOf(id));
			msg.replace("%bid%", String.valueOf(siegeClan.getParam()));
			msg.replace("%return%", String.valueOf(returnVal));

			player.sendPacket(msg);
		}
		//=============================================================================================
		//					Подтверджает отказ от ставки, возращается 90% сумы
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("cancel_bid_confirm"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}
			int id = Integer.parseInt(tokenizer.nextToken());

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, id);
			ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
			if(auctionEvent == null || !auctionEvent.isInProgress())
				return;

			AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
			if(siegeClan == null)
				return;

			long returnVal = siegeClan.getParam() - (long)(siegeClan.getParam() * 0.1);

			player.getClan().getWarehouse().addItem(clanHall.getFeeItemId(), returnVal);
			auctionEvent.removeObject(ClanHallAuctionEvent.ATTACKERS, siegeClan);
			SiegeClanDAO.getInstance().delete(clanHall, siegeClan);

			player.sendPacket(SystemMsg.YOU_HAVE_CANCELED_YOUR_BID);
			showChatWindow(player, 0, false);
		}
		//=============================================================================================
		//					Показывает окно на подтверджения
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("register_start"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, player.getClan().getHasHideout());
			if(clanHall.getSiegeEvent() == null || clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress())
				return;

			if((clanHall.getLastSiegeDate().getTimeInMillis() + WEEK) > System.currentTimeMillis())
			{
				player.sendPacket(SystemMsg.IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION);
				onBypassFeedback(player, "info");
				return;
			}

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_clanhall_register_start.htm");
			msg.replace("%id%", String.valueOf(player.getClan().getHasHideout()));
			msg.replace("%adena%", String.valueOf(player.getClan().getWarehouse().getCountOf(clanHall.getFeeItemId())));
			msg.replace("%deposit%", String.valueOf(clanHall.getDeposit()));

			player.sendPacket(msg);
		}
		//=============================================================================================
		//					Показывает окно на ввод, инфы про КХ, и подтверджает аукцион
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("register_next"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, player.getClan().getHasHideout());
			if(clanHall.getSiegeEvent() == null || clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, 0, false);
				return;
			}

			if(player.getClan().getWarehouse().getCountOf(clanHall.getFeeItemId()) < clanHall.getDeposit())
			{
				player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
				onBypassFeedback(player, "register_start");
				return;
			}

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_clanhall_register_next.htm");
			msg.replace("%min_bid%", String.valueOf(clanHall.getBaseMinBid()));
			msg.replace("%last_bid%", String.valueOf(clanHall.getBaseMinBid()));  //TODO [VISTALL] get last bid

			player.sendPacket(msg);
		}
		//=============================================================================================
		//					Показывает окно на ввод, инфы про КХ, и подтверджает аукцион
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("register_next2"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, player.getClan().getHasHideout());
			if(clanHall.getSiegeEvent() == null || clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, 0, false);
				return;
			}

			int day = Integer.parseInt(tokenizer.nextToken());
			long bid = -1;
			String comment = StringUtils.EMPTY;
			if(tokenizer.hasMoreTokens())
				try
				{
					bid = Long.parseLong(tokenizer.nextToken());
				}
				catch(Exception e){}

			if(tokenizer.hasMoreTokens())
			{
				comment = tokenizer.nextToken();
				while(tokenizer.hasMoreTokens())
					comment += " " + tokenizer.nextToken();
			}

			comment = comment.substring(0, Math.min(comment.length(), Byte.MAX_VALUE));
			if(bid <= -1)
			{
				onBypassFeedback(player, "register_next");
				return;
			}

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, day);

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_clanhall_register_confirm.htm");
			msg.replace("%description%", comment);
			msg.replace("%day%", String.valueOf(day));
			msg.replace("%bid%", String.valueOf(bid));
			msg.replace("%base_bid%", String.valueOf(clanHall.getBaseMinBid()));
			msg.replace("%hour%", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
			msg.replace("%date%", DATE_FORMAT.format(cal.getTimeInMillis()));

			player.sendPacket(msg);
		}
		//=============================================================================================
		//					Подтверждает продажу КХ
		//=============================================================================================
		else if(actualCommand.equalsIgnoreCase("register_confirm"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, player.getClan().getHasHideout());
			if(clanHall.getSiegeEvent() == null || clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, 0, false);
				return;
			}

			if((clanHall.getLastSiegeDate().getTimeInMillis() + WEEK) > System.currentTimeMillis())
			{
				player.sendPacket(SystemMsg.IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION);
				onBypassFeedback(player, "info");
				return;
			}

			int day = Integer.parseInt(tokenizer.nextToken());
			long bid = Long.parseLong(tokenizer.nextToken());
			String comment = StringUtils.EMPTY;

			if(tokenizer.hasMoreTokens())
			{
				comment = tokenizer.nextToken();
				while(tokenizer.hasMoreTokens())
					comment += " " + tokenizer.nextToken();
			}

			if(bid <= -1)
			{
				onBypassFeedback(player, "register_next");
				return;
			}

			clanHall.setAuctionMinBid(bid);
			clanHall.setAuctionDescription(comment);
			clanHall.setAuctionLength(day);
			clanHall.setJdbcState(JdbcEntityState.UPDATED);
			clanHall.update();

			clanHall.getSiegeEvent().reCalcNextTime(false);

			onBypassFeedback(player, "info");
			player.sendPacket(SystemMsg.YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION);
		}
		else if(actualCommand.equals("cancel_start"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, player.getClan().getHasHideout());
			if(clanHall.getSiegeEvent() == null || clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || !clanHall.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, 0, false);
				return;
			}

			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("residence2/clanhall/auction_clanhall_cancel_confirm.htm");
			msg.replace("%deposit%", String.valueOf(clanHall.getDeposit()));

			player.sendPacket(msg);
		}
		else if(actualCommand.equals("cancel_confirm"))
		{
			if(!firstChecks(player))
			{
				showChatWindow(player, 0, false);
				return;
			}

			AuctionClanHall clanHall = ResidenceHolder.getInstance().getResidence(AuctionClanHall.class, player.getClan().getHasHideout());
			if(clanHall.getSiegeEvent() == null || clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || !clanHall.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, 0, false);
				return;
			}

			clanHall.getSiegeEvent().clearActions();
			clanHall.getSiegeEvent().removeState(SiegeEvent.PROGRESS_STATE);

			clanHall.getSiegeDate().setTimeInMillis(0);
			clanHall.getLastSiegeDate().setTimeInMillis(System.currentTimeMillis());
			clanHall.setAuctionDescription(StringUtils.EMPTY);
			clanHall.setAuctionLength(0);
			clanHall.setAuctionMinBid(0);
			clanHall.setJdbcState(JdbcEntityState.UPDATED);
			clanHall.update();

			ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
			List<AuctionSiegeClanObject> siegeClans = auctionEvent.removeObjects(ClanHallAuctionEvent.ATTACKERS);
			SiegeClanDAO.getInstance().delete(clanHall);

			for(AuctionSiegeClanObject $siegeClan : siegeClans)
			{
				long returnBid = $siegeClan.getParam() - (long)($siegeClan.getParam() * 0.1);

				$siegeClan.getClan().getWarehouse().addItem(clanHall.getFeeItemId(), returnBid);
			}

			clanHall.getSiegeEvent().reCalcNextTime(false);
			onBypassFeedback(player, "info");
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == 400)
		{
			bidOnInstantClanHall(player, (int) reply);
		}
		else if(ask == 300)
		{
			if(reply == 1)
			{
				boolean auctionActiveA = false;
				boolean auctionActiveB = false;

				InstantClanHall rA = ResidenceHolder.getInstance().getResidence(InstantClanHall.class, Residence.getInstantResidenceId(1));
				InstantClanHall rB = ResidenceHolder.getInstance().getResidence(InstantClanHall.class, Residence.getInstantResidenceId(2));
				InstantClanHall rC = ResidenceHolder.getInstance().getResidence(InstantClanHall.class, Residence.getInstantResidenceId(3));
				InstantClanHallAuctionEvent eA = rA.getSiegeEvent();
				InstantClanHallAuctionEvent eB = rB.getSiegeEvent();
				InstantClanHallAuctionEvent eC = rC.getSiegeEvent();

				if(eA.isInProgress() && eB.isInProgress() && eC.isInProgress())
					auctionActiveA = true;
				else
				{
					rA = ResidenceHolder.getInstance().getResidence(InstantClanHall.class, Residence.getInstantResidenceId(4));
					rB = ResidenceHolder.getInstance().getResidence(InstantClanHall.class, Residence.getInstantResidenceId(5));
					rC = ResidenceHolder.getInstance().getResidence(InstantClanHall.class, Residence.getInstantResidenceId(6));
					eA = rA.getSiegeEvent();
					eB = rB.getSiegeEvent();
					eC = rC.getSiegeEvent();

					if(eA.isInProgress() && eB.isInProgress() && eC.isInProgress())
						auctionActiveB = true;
				}

				if(auctionActiveA || auctionActiveB)
				{
					HtmlMessage msg = new HtmlMessage(this);
					msg.setFile("residence2/instant_clanhall/auction_dealer002_" + (auctionActiveA ? "a" : "b") + ".htm");
					msg.replace("<?Syear?>", String.valueOf(eA.getSiegeDate().get(Calendar.YEAR)));
					msg.replace("<?Smonth?>", String.valueOf(eA.getSiegeDate().get(Calendar.MONTH) + 1));
					msg.replace("<?Sdate?>", String.valueOf(eA.getSiegeDate().get(Calendar.DAY_OF_MONTH)));
					msg.replace("<?Shour?>", String.valueOf(eA.getSiegeDate().get(Calendar.HOUR_OF_DAY)));
					msg.replace("<?Smin?>", String.valueOf(eA.getSiegeDate().get(Calendar.MINUTE)));
					msg.replace("<?Eyear?>", String.valueOf(eA.getEndAuctionDate().get(Calendar.YEAR)));
					msg.replace("<?Emonth?>", String.valueOf(eA.getEndAuctionDate().get(Calendar.MONTH) + 1));
					msg.replace("<?Edate?>", String.valueOf(eA.getEndAuctionDate().get(Calendar.DAY_OF_MONTH)));
					msg.replace("<?Ehour?>", String.valueOf(eA.getEndAuctionDate().get(Calendar.HOUR_OF_DAY)));
					msg.replace("<?Emin?>", String.valueOf(eA.getEndAuctionDate().get(Calendar.MINUTE)));

					double chance = rA.getMaxCount() / Math.max(rA.getMaxCount(), eA.getParticipantsCount());
					int parcent = (int) chance;
					int floatPercent = (int) ((parcent - chance) * 100);
					if((floatPercent % 10) == 0)
						floatPercent /= 10;
					msg.replace("<?int_percent_A?>", String.valueOf(parcent));
					msg.replace("<?int_float_A?>", String.valueOf(floatPercent));

					chance = rB.getMaxCount() / Math.max(rB.getMaxCount(), eB.getParticipantsCount());
					parcent = (int) chance;
					floatPercent = (int) ((parcent - chance) * 100);
					if((floatPercent % 10) == 0)
						floatPercent /= 10;
					msg.replace("<?int_percent_B?>", String.valueOf(parcent));
					msg.replace("<?int_float_B?>", String.valueOf(floatPercent));

					chance = rC.getMaxCount() / Math.max(rC.getMaxCount(), eC.getParticipantsCount());
					parcent = (int) chance;
					floatPercent = (int) ((parcent - chance) * 100);
					if((floatPercent % 10) == 0)
						floatPercent /= 10;
					msg.replace("<?int_percent_C?>", String.valueOf(parcent));
					msg.replace("<?int_float_C?>", String.valueOf(floatPercent));

					player.sendPacket(msg);
				}
				else
				{
					showChatWindow(player, "residence2/instant_clanhall/auction_dealer003.htm", false);
				}
			}
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		showChatWindow(player, "residence2/clanhall/auction_dealer001.htm", firstTalk);
	}

	private boolean firstChecks(Player player)
	{
		if(player.getClan() == null || player.getClan().getLevel() < 2)
		{
			player.sendPacket(SystemMsg.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
			return false;
		}

		if(player.getClan().isPlacedForDisband())
		{
			player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
			return false;
		}

		if(!player.hasPrivilege(Privilege.CH_AUCTION))
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}

		return true;
	}

	private boolean checkBid(Player player, ClanHallAuctionEvent auctionEvent, final long bid)
	{
		long consumeBid = bid;
		AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
		if(siegeClan != null)
			consumeBid -= siegeClan.getParam();

		if(consumeBid > player.getClan().getWarehouse().getCountOf(auctionEvent.getResidence().getFeeItemId()))
		{
			player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
			onBypassFeedback(player, "bid_start " + auctionEvent.getId());
			return false;
		}

		long minBid = siegeClan == null ? auctionEvent.getResidence().getAuctionMinBid() : siegeClan.getParam();
		if(bid < minBid)
		{
			player.sendPacket(SystemMsg.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
			onBypassFeedback(player, "bid_start " + auctionEvent.getId());
			return false;
		}
		return true;
	}

	private String getMapDialog()
	{
		//"gludio", "gludin", "dion", "giran", "adena", "rune", "goddard", "schuttgart"
		return String.format("residence2/clanhall/map_agit_%s.htm", getParameters().getString("town", "gludin"));
	}

	public void bidOnInstantClanHall(Player player, int instantId)
	{
		InstantClanHall clanHall = ResidenceHolder.getInstance().getResidence(InstantClanHall.class, Residence.getInstantResidenceId(instantId));
		if(clanHall == null || clanHall.getSiegeEvent() == null || !clanHall.getSiegeEvent().isInProgress())
		{
			// Срок ставки за наградной холл не наступил.
			player.sendPacket(SystemMsg.IT_IS_NOT_THE_BIDDING_PERIOD_FOR_THE_PROVISIONAL_CLAN_HALL);
			return;
		}

		Clan clan = player.getClan();
		if(clan == null)
		{
			// Клан-владелец отсутствует. Ставки невозможны.
			player.sendPacket(SystemMsg.YOU_CANNOT_MAKE_A_BID_BECAUSE_YOU_DONT_BELONG_TO_A_CLAN);
			return;
		}

		if(clan.getHasHideout() != 0 || clanHall.getSiegeEvent().getSiegeClan(SiegeEvent.ATTACKERS, clan) != null)
		{
			// Вы уже внесли ставку за наградной холл.
			player.sendPacket(SystemMsg.YOU_ALREADY_MADE_A_BID_FOR_THE_PROVISIONAL_CLAN_HALL);
			return;
		}

		if(player.getClan().getLevel() < clanHall.getMinPledgeLevel())
		{
			// Уровень клана недостаточен для участия в аукционе.
			player.sendPacket(SystemMsg.CLAN_LEVEL_REQUIREMENTS_FOR_BIDDING_ARE_NOT_MET);
			return;
		}

		if(player.getClan().isPlacedForDisband())
		{
			// Вы уже подали заявку на расформирование клана.
			player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
			return;
		}

		if(!player.hasPrivilege(Privilege.CH_AUCTION) || clan.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) < clanHall.getRentalFee())
		{
			// Для внесения ставки за наградной холл необходимо соответствовать условиям участия в аукционе холлов.
			player.sendPacket(SystemMsg.YOU_MUST_HAVE_RIGHTS_TO_A_CLAN_HALL_AUCTION_IN_ORDER_TO_MAKE_A_BID_FOR_PROVISIONAL_CLAN_HALL);
			return;
		}

		SiegeClanObject siegeClan = new SiegeClanObject(SiegeEvent.ATTACKERS, clan, clanHall.getRentalFee());
		clanHall.getSiegeEvent().addObject(SiegeEvent.ATTACKERS, siegeClan);
		clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, clanHall.getRentalFee());
		player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_MADE_A_BID_AT_S1).addString(clan.getName()));
	}
}
