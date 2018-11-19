package services;

import l2s.gameserver.Config;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.PremiumAccountHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.data.ItemData;
import l2s.gameserver.templates.premiumaccount.PremiumAccountTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.TimeUtils;
import l2s.gameserver.utils.Util;

public class RateBonus
{
	@Bypass("services.RateBonus:list")
	public void list(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.PREMIUM_ACCOUNT_ENABLED)
		{
			Functions.show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
			return;
		}

		String html = HtmCache.getInstance().getHtml("scripts/services/RateBonus.htm", player);

		StringBuilder add = new StringBuilder();
		for(PremiumAccountTemplate premiumAccount : PremiumAccountHolder.getInstance().getPremiumAccounts())
		{
			int[] delays = premiumAccount.getFeeDelays();
			for(int delay : delays)
			{
				ItemData[] feeItems = premiumAccount.getFeeItems(delay);
				if(feeItems.length > 0)
				{
					add.append("<font color=\"LEVEL\">Оплата:</font>");
					for(ItemData feeItem : feeItems)
					{
						add.append("<br1>");
						add.append(Util.formatAdena(feeItem.getCount()));
						add.append(" x ");
						add.append(HtmlUtils.htmlItemName(feeItem.getId()));
					}
					add.append("<br>");
				}

				add.append("<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h htmbypass_services.RateBonus:get ");
				add.append(premiumAccount.getType()).append(" ").append(delay).append("\">");
				add.append(premiumAccount.getName(player.getLanguage())).append(" (");
				if(delay > 0)
				{
					int days = delay / 24;
					int hours = delay % 24;
					if(days > 0)
					{
						if(player.isLangRus())
							add.append(days).append("д.");
						else
							add.append(days).append("d.");
					}
					if(days > 0 && hours > 0)
					{
						if(player.isLangRus())
							add.append(" и ");
						else
							add.append(" and ");
					}
					if(hours > 0)
					{
						if(player.isLangRus())
							add.append(hours).append("ч.");
						else
							add.append(hours).append("h.");
					}
				}
				else
					add.append(player.isLangRus() ? "бессрочный" : "unlimited");
				add.append(")").append("</button><br>");
			}
		}

		html = html.replaceFirst("%toreplace%", add.toString());

		Functions.show(html, player);
	}

	@Bypass("services.RateBonus:get")
	public void get(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.PREMIUM_ACCOUNT_ENABLED)
		{
			Functions.show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
			return;
		}

		if(!Config.PREMIUM_ACCOUNT_BASED_ON_GAMESERVER && AuthServerCommunication.getInstance().isShutdown())
		{
			list(player, npc, new String[0]);
			return;
		}

		if(param.length < 2)
		{
			list(player, npc, new String[0]);
			return;
		}

		int type = Integer.parseInt(param[0]);

		PremiumAccountTemplate premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(type);
		if(premiumAccount == null)
		{
			list(player, npc, new String[0]);
			return;
		}

		int delay = Integer.parseInt(param[1]);

		ItemData[] feeItems = premiumAccount.getFeeItems(delay);
		if(feeItems == null)
		{
			list(player, npc, new String[0]);
			return;
		}

		if(player.hasPremiumAccount() && player.getPremiumAccount() != premiumAccount)
		{
			String html = HtmCache.getInstance().getHtml("scripts/services/RateBonusAlready.htm", player);
			html = html.replaceFirst("endtime", TimeUtils.toSimpleFormat(player.getNetConnection().getPremiumAccountExpire() * 1000L));
			Functions.show(html, player);
			return;
		}

		boolean success = true;

		if(feeItems.length > 0)
		{
			for(ItemData feeItem : feeItems)
			{
				if(!ItemFunctions.haveItem(player, feeItem.getId(), feeItem.getCount()))
				{
					success = false;
					break;
				}
			}

			if(success)
			{
				for(ItemData feeItem : feeItems)
					ItemFunctions.deleteItem(player, feeItem.getId(), feeItem.getCount());
			}
			else
			{
				if(feeItems.length == 1 && feeItems[0].getId() == ItemTemplate.ITEM_ID_ADENA)
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				else
					player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
		}

		if(success)
		{
			if(player.givePremiumAccount(premiumAccount, delay))
			{
				Log.add(player.getName() + "|" + player.getObjectId() + "|rate bonus|" + type + "|" + delay + "|", "services");
				Functions.show(HtmCache.getInstance().getHtml("scripts/services/RateBonusGet.htm", player), player);
			}
			else
			{
				if(feeItems.length > 0)
				{
					for(ItemData feeItem : feeItems)
						ItemFunctions.addItem(player, feeItem.getId(), feeItem.getCount());
				}
				list(player, npc, new String[0]);
			}
		}
	}
}