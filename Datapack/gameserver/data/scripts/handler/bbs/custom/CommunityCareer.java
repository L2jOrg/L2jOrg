package handler.bbs.custom;

import handler.bbs.ScriptsCommunityHandler;
import org.l2j.gameserver.dao.CustomHeroDAO;
import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.data.htm.HtmTemplates;
import org.l2j.gameserver.handler.bbs.BbsHandlerHolder;
import org.l2j.gameserver.handler.bbs.IBbsHandler;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ShowBoardPacket;
import org.l2j.gameserver.network.l2.s2c.SocialActionPacket;
import org.l2j.gameserver.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Bonux
**/
public class CommunityCareer extends ScriptsCommunityHandler
{
	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_cbbscareer"
		};
	}

	@Override
	protected void doBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";

		if("cbbscareer".equals(cmd))
		{
			String cmd2 = st.nextToken();
			if("profession".equals(cmd2))
			{
				if(BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_1 == 0 && BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_2 == 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/professions.htm", player);
				html = tpls.get(0);

				StringBuilder content = new StringBuilder();

				final int feeItemId = getFeeItemIdForChangeClass(player);
				final long feeItemCount = getFeeItemCountForChangeClass(player);
				final int nextClassMinLevel = getNextClassMinLevel(player);
				if(!st.hasMoreTokens())
				{
					if(nextClassMinLevel == -1)
						content.append(tpls.get(1));
					else if(feeItemId == 0)
						content.append(tpls.get(8));
					else
					{
						if(nextClassMinLevel > player.getLevel())
							content.append(tpls.get(5).replace("<?level?>", String.valueOf(nextClassMinLevel)));
						else
						{
							List<ClassId> availClasses = getAvailClasses(player.getClassId());
							if(availClasses.isEmpty())
								content.append(tpls.get(6));
							else
							{
								ClassId classId = availClasses.get(0);

								content.append(tpls.get(2));

								if(feeItemId > 0 && feeItemCount > 0)
								{
									content.append("<br1>");
									content.append(tpls.get(3).replace("<?fee_item_count?>", String.valueOf(feeItemCount)).replace("<?fee_item_name?>", HtmlUtils.htmlItemName(feeItemId)));
								}

								for(ClassId cls : availClasses)
								{
									content.append("<br>");

									String classHtm = tpls.get(4);
									classHtm = classHtm.replace("<?class_name?>", cls.getName(player));
									classHtm = classHtm.replace("<?class_id?>", String.valueOf(cls.getId()));

									content.append(classHtm);
								}
							}
						}
					}
				}
				else
				{
					if(!BBSConfig.GLOBAL_USE_FUNCTIONS_CONFIGS && !checkUseCondition(player))
					{
						onWrongCondition(player);
						return;
					}

					if(nextClassMinLevel == -1 || feeItemId == 0 || nextClassMinLevel > player.getLevel())
					{
						IBbsHandler handler = BbsHandlerHolder.getInstance().getCommunityHandler("_cbbscareer_profession");
						if(handler != null)
							onBypassCommand(player, "_cbbscareer_profession");
						return;
					}

					List<ClassId> availClasses = getAvailClasses(player.getClassId());
					if(availClasses.isEmpty())
					{
						IBbsHandler handler = BbsHandlerHolder.getInstance().getCommunityHandler("_cbbscareer_profession");
						if(handler != null)
							onBypassCommand(player, "_cbbscareer_profession");
						return;
					}

					boolean avail = false;
					ClassId classId = ClassId.VALUES[Integer.parseInt(st.nextToken())];
					for(ClassId cls : availClasses)
					{
						if(cls == classId)
						{
							avail = true;
							break;
						}
					}

					if(!avail)
					{
						IBbsHandler handler = BbsHandlerHolder.getInstance().getCommunityHandler("_cbbscareer_profession");
						if(handler != null)
							onBypassCommand(player, "_cbbscareer_profession");
						return;
					}

					if(feeItemId > 0 && feeItemCount > 0 && !ItemFunctions.deleteItem(player, feeItemId, feeItemCount, true))
					{
						String errorMsg = tpls.get(7).replace("<?fee_item_count?>", String.valueOf(feeItemCount)).replace("<?fee_item_name?>", HtmlUtils.htmlItemName(feeItemId));
						html = html.replace("<?content?>", errorMsg);
						ShowBoardPacket.separateAndSend(html, player);
						return;
					}

					player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);
					player.setClassId(classId.getId(), false);
					player.broadcastUserInfo(true);

					IBbsHandler handler = BbsHandlerHolder.getInstance().getCommunityHandler("_cbbscareer_profession");
					if(handler != null)
						onBypassCommand(player, "_cbbscareer_profession");
					return;
				}

				html = html.replace("<?content?>", content.toString());
			}
			else if("hero".equals(cmd2))
			{
				if(BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_1_DAY <= 0 && BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_FOREVER <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/hero.htm", player);
				html = tpls.get(0);

				StringBuilder content = new StringBuilder();

				if(Hero.getInstance().isHero(player.getObjectId()))
					content.append(tpls.get(1));
				else if(Hero.getInstance().isInactiveHero(player.getObjectId()))
					content.append(tpls.get(2));
				else
				{
					int expiryTime = CustomHeroDAO.getInstance().getExpiryTime(player.getObjectId());
					if(expiryTime != -1 && expiryTime < (System.currentTimeMillis() / 1000))
						expiryTime = (int) (System.currentTimeMillis() / 1000);

					if(!st.hasMoreTokens())
					{
						if(expiryTime == -1)
							content.append(tpls.get(3));
						else
						{
							if(expiryTime > (System.currentTimeMillis() / 1000))
							{
								String activeHero = tpls.get(4);
								activeHero = activeHero.replace("<?expire_time?>", String.valueOf(TimeUtils.toSimpleFormat(expiryTime * 1000L)));
								content.append(activeHero);
							}

							if(BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_1_DAY > 0)
							{
								for(int period : BBSConfig.HERO_SERVICE_PERIOD_VARIATIONS)
								{
									String tempBlock = tpls.get(5);
									tempBlock = tempBlock.replace("<?period?>", String.valueOf(period));

									long price = BBSConfig.HERO_SERVICE_COST_ITEM_COUNT_PER_1_DAY * period;
									if(price > 0)
									{
										String tempFeeBlock = tpls.get(10);
										tempFeeBlock = tempFeeBlock.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_1_DAY));
										tempFeeBlock = tempFeeBlock.replace("<?fee_item_count?>", Util.formatAdena(price));
										tempBlock = tempBlock.replace("<?fee_block?>", tempFeeBlock);
									}
									else
										tempBlock = tempBlock.replace("<?fee_block?>", tpls.get(11));

									content.append(tempBlock);
								}
							}

							if(BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_FOREVER > 0)
							{
								String tempBlock = tpls.get(6);

								long price = BBSConfig.HERO_SERVICE_COST_ITEM_COUNT_PER_FOREVER;
								if(price > 0)
								{
									String tempFeeBlock = tpls.get(10);
									tempFeeBlock = tempFeeBlock.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_FOREVER));
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
						String cmd3 = st.nextToken();
						if("buy".equals(cmd3))
						{
							if(!st.hasMoreTokens())
								return;

							if(expiryTime == -1)
								return;

							String cmd4 = st.nextToken();
							if("unlim".equals(cmd4))
							{
								long price = BBSConfig.HERO_SERVICE_COST_ITEM_COUNT_PER_FOREVER;
								if(price <= 0 || ItemFunctions.deleteItem(player, BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_1_DAY, price, true))
								{
									CustomHeroDAO.getInstance().addCustomHero(player.getObjectId(), -1);
									if(!player.isHero())
									{
										player.setHero(true);
										player.updatePledgeRank();
										player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.GIVE_HERO));
										player.checkHeroSkills();
									}
									content.append(tpls.get(9));
								}
								else
								{
									String feeInfo = tpls.get(7);
									feeInfo = feeInfo.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_1_DAY));
									feeInfo = feeInfo.replace("<?fee_item_count?>", Util.formatAdena(price));
									content.append(feeInfo);
								}
							}
							else
							{
								int days = Integer.parseInt(cmd4);
								long price = BBSConfig.HERO_SERVICE_COST_ITEM_COUNT_PER_1_DAY * days;
								if(price <= 0 || ItemFunctions.deleteItem(player, BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_1_DAY, price, true))
								{
									CustomHeroDAO.getInstance().addCustomHero(player.getObjectId(), expiryTime + (days * 24 * 60 * 60));
									if(!player.isHero())
									{
										player.setHero(true);
										player.updatePledgeRank();
										player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.GIVE_HERO));
										player.checkHeroSkills();
									}
									content.append(tpls.get(8));
								}
								else
								{
									String feeInfo = tpls.get(7);
									feeInfo = feeInfo.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.HERO_SERVICE_COST_ITEM_ID_PER_1_DAY));
									feeInfo = feeInfo.replace("<?fee_item_count?>", Util.formatAdena(price));
									content.append(feeInfo);
								}
							}
						}
					}
				}
				html = html.replace("<?content?>", content.toString());
			}
			else if("multiclass".equals(cmd2))
			{
				MulticlassUtils.showMulticlassList(player);
				player.sendPacket(ShowBoardPacket.CLOSE);
			}
		}
		ShowBoardPacket.separateAndSend(html, player);
	}

	@Override
	protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		//
	}

	private static int getNextClassMinLevel(Player player)
	{
		final ClassId classId = player.getClassId();
		if(classId.isLast())
			return -1;

		return classId.getClassMinLevel(true);
	}

	private static int getFeeItemIdForChangeClass(Player player)
	{
		switch(player.getClassId().getClassLevel())
		{
			case NONE:
				return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_1;
			case FIRST:
				return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_2;
			case SECOND:
				return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_3;
		}
		return 0;
	}

	private static long getFeeItemCountForChangeClass(Player player)
	{
		switch(player.getClassId().getClassLevel())
		{
			case NONE:
				return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_1;
			case FIRST:
				return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_2;
			case SECOND:
				return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_3;
		}
		return 0L;
	}

	private static List<ClassId> getAvailClasses(ClassId playerClass)
	{
		List<ClassId> result = new ArrayList<ClassId>();
		for(ClassId _class : ClassId.values())
		{
			if(!_class.isDummy() && _class.getClassLevel().ordinal() == playerClass.getClassLevel().ordinal() + 1 && _class.childOf(playerClass))
				result.add(_class);
		}		
		return result;
	}
}