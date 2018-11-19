package handler.bbs.custom;

import java.util.StringTokenizer;

import l2s.gameserver.Config;
import l2s.gameserver.dao.HardwareLimitsDAO;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.TimeUtils;
import l2s.gameserver.utils.Util;

import handler.bbs.ScriptsCommunityHandler;

/**
 * @author Bonux
**/
public class CommunityAddWindows extends ScriptsCommunityHandler
{
	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_cbbsaddwindows"
		};
	}

	@Override
	protected void doBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";

		if("cbbsaddwindows".equals(cmd))
		{
			if(BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY <= 0 && BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_FOREVER <= 0)
			{
				player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
				player.sendPacket(ShowBoardPacket.CLOSE);
				return;
			}

			HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/additional_windows.htm", player);
			html = tpls.get(0);

			String hardware;
			if(BBSConfig.ADD_WINDOW_SERVICE_TYPE == 1)
			{
				hardware = player.getHWID();
				if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}
			}
			else if(BBSConfig.ADD_WINDOW_SERVICE_TYPE == 2)
			{
				hardware = player.getAccountName();
				if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}
			}
			else
			{
				hardware = player.getIP();
				if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_IP <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}
			}

			final int[] limits = HardwareLimitsDAO.getInstance().select(hardware);

			StringBuilder content = new StringBuilder();

			if(!st.hasMoreTokens())
			{
				boolean canAddWindow = false;
				boolean haveUnlimited = false;

				if(limits[0] > 0)
				{
					if(limits[1] > (System.currentTimeMillis() / 1000))
					{
						String activeWindows = tpls.get(1);
						activeWindows = activeWindows.replace("<?active_windows?>", String.valueOf(limits[0]));
						activeWindows = activeWindows.replace("<?expire_time?>", String.valueOf(TimeUtils.toSimpleFormat(limits[1] * 1000L)));
						content.append(activeWindows);
						canAddWindow = true;
					}
					else if(limits[1] == -1)
					{
						String activeWindows = tpls.get(2);
						activeWindows = activeWindows.replace("<?active_windows?>", String.valueOf(limits[0]));
						content.append(activeWindows);
						canAddWindow = true;
						haveUnlimited = true;
					}
				}

				if(canAddWindow)
				{
					int itemId = BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_FOREVER;
					long price = BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_FOREVER;
					if(!haveUnlimited)
					{
						double priceMod = (limits[1] - (System.currentTimeMillis() / 1000)) / 60. / 60. / 24.;
						itemId = BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY;
						price = (long) (BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_1_DAY * priceMod);
					}

					if(itemId > 0)
					{
						String tempBlock = tpls.get(3);
						if(price > 0)
						{
							String tempFeeBlock = tpls.get(10);
							tempFeeBlock = tempFeeBlock.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(itemId));
							tempFeeBlock = tempFeeBlock.replace("<?fee_item_count?>", Util.formatAdena(price));
							tempBlock = tempBlock.replace("<?fee_block?>", tempFeeBlock);
						}
						else
							tempBlock = tempBlock.replace("<?fee_block?>", tpls.get(11));
						content.append(tempBlock);
					}
				}

				if(!haveUnlimited)
				{
					int availableWindows = canAddWindow ? limits[0] : 1;
					if(BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY > 0)
					{
						for(int period : BBSConfig.ADD_WINDOW_SERVICE_PERIOD_VARIATIONS)
						{
							String tempBlock = tpls.get(4);
							tempBlock = tempBlock.replace("<?avail_windows?>", String.valueOf(availableWindows));
							tempBlock = tempBlock.replace("<?period?>", String.valueOf(period));

							long price = BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_1_DAY * availableWindows * period;
							if(price > 0)
							{
								String tempFeeBlock = tpls.get(10);
								tempFeeBlock = tempFeeBlock.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY));
								tempFeeBlock = tempFeeBlock.replace("<?fee_item_count?>", Util.formatAdena(price));
								tempBlock = tempBlock.replace("<?fee_block?>", tempFeeBlock);
							}
							else
								tempBlock = tempBlock.replace("<?fee_block?>", tpls.get(11));

							content.append(tempBlock);
						}
					}

					if(BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_FOREVER > 0)
					{
						String tempBlock = tpls.get(5);
						tempBlock = tempBlock.replace("<?avail_windows?>", String.valueOf(availableWindows));

						long price = BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_FOREVER * availableWindows;
						if(price > 0)
						{
							String tempFeeBlock = tpls.get(10);
							tempFeeBlock = tempFeeBlock.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_FOREVER));
							tempFeeBlock = tempFeeBlock.replace("<?fee_item_count?>", Util.formatAdena(price));
							tempBlock = tempBlock.replace("<?fee_block?>", tempFeeBlock);
						}
						else
							tempBlock = tempBlock.replace("<?fee_block?>", tpls.get(11));

						content.append(tempBlock);
					}
				}
			}
			else
			{
				String cmd2 = st.nextToken();
				if("add".equals(cmd2))
				{
					int activeWindows = 0;
					int currentExpire = (int) (System.currentTimeMillis() / 1000);
					if(limits[0] > 0 && (limits[1] == -1 || limits[1] > (System.currentTimeMillis() / 1000)))
					{
						activeWindows = limits[0];
						currentExpire = limits[1];
					}
					else
						return;

					long price = BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_FOREVER;
					if(limits[1] > 0)
					{
						double priceMod = (limits[1] - (System.currentTimeMillis() / 1000)) / 60. / 60. / 24.;
						price = (long) (BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_1_DAY * priceMod);
					}

					if(price <= 0 || ItemFunctions.deleteItem(player, BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY, price, true))
					{
						HardwareLimitsDAO.getInstance().insert(hardware, activeWindows + 1, currentExpire);
						content.append(tpls.get(7));
					}
					else
					{
						String feeInfo = tpls.get(6);
						feeInfo = feeInfo.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY));
						feeInfo = feeInfo.replace("<?fee_item_count?>", Util.formatAdena(price));
						content.append(feeInfo);
					}
				}
				else if("buy".equals(cmd2))
				{
					if(!st.hasMoreTokens())
						return;

					int activeWindows = 1;
					int currentExpire = (int) (System.currentTimeMillis() / 1000);
					if(limits[0] > 0)
					{
						if(limits[1] > (System.currentTimeMillis() / 1000))
						{
							activeWindows = limits[0];
							currentExpire = limits[1];
						}
						else if(limits[1] == -1)
							return;
					}

					String cmd3 = st.nextToken();
					if("unlim".equals(cmd3))
					{
						long price = BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_FOREVER * activeWindows;
						if(price <= 0 || ItemFunctions.deleteItem(player, BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY, price, true))
						{
							HardwareLimitsDAO.getInstance().insert(hardware, activeWindows, -1);
							content.append(tpls.get(9));
						}
						else
						{
							String feeInfo = tpls.get(6);
							feeInfo = feeInfo.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY));
							feeInfo = feeInfo.replace("<?fee_item_count?>", Util.formatAdena(price));
							content.append(feeInfo);
						}
					}
					else
					{
						int days = Integer.parseInt(cmd3);
						long price = BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_1_DAY * activeWindows * days;
						if(price <= 0 || ItemFunctions.deleteItem(player, BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY, price, true))
						{
							HardwareLimitsDAO.getInstance().insert(hardware, activeWindows, currentExpire + (days * 24 * 60 * 60));
							content.append(tpls.get(8));
						}
						else
						{
							String feeInfo = tpls.get(6);
							feeInfo = feeInfo.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY));
							feeInfo = feeInfo.replace("<?fee_item_count?>", Util.formatAdena(price));
							content.append(feeInfo);
						}
					}
				}
			}
			html = html.replace("<?content?>", content.toString());
		}
		ShowBoardPacket.separateAndSend(html, player);
	}

	@Override
	protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		//
	}

	private static String doubleToString(double value)
	{
		int intValue = (int) value;
		if(intValue == value)
			return String.valueOf(intValue);
		return String.valueOf(value);
	}
}