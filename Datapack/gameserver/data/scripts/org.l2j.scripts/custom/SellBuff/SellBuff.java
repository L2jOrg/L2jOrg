/*
 * Copyright © 2019 L2J Mobius
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
package custom.SellBuff;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.handler.VoicedCommandHandler;
import org.l2j.gameserver.instancemanager.SellBuffsManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.holders.SellBuffHolder;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.isInteger;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * Sell Buffs voice command
 * @author St3eT
 */
public class SellBuff implements IVoicedCommandHandler, IBypassHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"sellbuff",
		"sellbuffs",
	};
	
	private static final String[] BYPASS_COMMANDS =
	{
		"sellbuffadd",
		"sellbuffaddskill",
		"sellbuffedit",
		"sellbuffchangeprice",
		"sellbuffremove",
		"sellbuffbuymenu",
		"sellbuffbuyskill",
		"sellbuffstart",
		"sellbuffstop",
	};
	
	public SellBuff()
	{
		if (Config.SELLBUFF_ENABLED)
		{
			BypassHandler.getInstance().registerHandler(this);
			VoicedCommandHandler.getInstance().registerHandler(this);
		}
	}
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		String cmd = "";
		String params = "";
		final StringTokenizer st = new StringTokenizer(command, " ");
		
		if (st.hasMoreTokens())
		{
			cmd = st.nextToken();
		}
		
		while (st.hasMoreTokens())
		{
			params += st.nextToken() + (st.hasMoreTokens() ? " " : "");
		}
		
		if (cmd.isEmpty())
		{
			return false;
		}
		return useBypass(cmd, player, params);
	}
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		switch (command)
		{
			case "sellbuff":
			case "sellbuffs":
			{
				SellBuffsManager.getInstance().sendSellMenu(activeChar);
				break;
			}
		}
		return true;
	}
	
	public boolean useBypass(String command, Player activeChar, String params)
	{
		if (!Config.SELLBUFF_ENABLED)
		{
			return false;
		}
		
		switch (command)
		{
			case "sellbuffstart":
			{
				if (activeChar.isSellingBuffs() || (params == null) || params.isEmpty())
				{
					return false;
				}
				else if (activeChar.getSellingBuffs().isEmpty())
				{
					activeChar.sendMessage("Your list of buffs is empty, please add some buffs first!");
					return false;
				}
				else
				{
					String title = "BUFF SELL: ";
					final StringTokenizer st = new StringTokenizer(params, " ");
					while (st.hasMoreTokens())
					{
						title += st.nextToken() + " ";
					}
					
					if (title.length() > 40)
					{
						activeChar.sendMessage("Your title cannot exceed 29 characters in length. Please try again.");
						return false;
					}
					
					SellBuffsManager.getInstance().startSellBuffs(activeChar, title);
				}
				break;
			}
			case "sellbuffstop":
			{
				if (activeChar.isSellingBuffs())
				{
					SellBuffsManager.getInstance().stopSellBuffs(activeChar);
				}
				break;
			}
			case "sellbuffadd":
			{
				if (!activeChar.isSellingBuffs())
				{
					int index = 0;
					if ((params != null) && !params.isEmpty() && isInteger(params))
					{
						index = Integer.parseInt(params);
					}
					
					SellBuffsManager.getInstance().sendBuffChoiceMenu(activeChar, index);
				}
				break;
			}
			case "sellbuffedit":
			{
				if (!activeChar.isSellingBuffs())
				{
					SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
				}
				break;
			}
			case "sellbuffchangeprice":
			{
				if (!activeChar.isSellingBuffs() && (params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int skillId = -1;
					int price = -1;
					
					if (st.hasMoreTokens())
					{
						skillId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						try
						{
							price = Integer.parseInt(st.nextToken());
						}
						catch (NumberFormatException e)
						{
							activeChar.sendMessage("Too big price! Maximal price is " + Config.SELLBUFF_MAX_PRICE);
							SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
						}
					}
					
					if ((skillId == -1) || (price == -1))
					{
						return false;
					}
					
					final Skill skillToChange = activeChar.getKnownSkill(skillId);
					if (skillToChange == null)
					{
						return false;
					}
					
					final SellBuffHolder holder = activeChar.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skillToChange.getId())).findFirst().orElse(null);
					if ((holder != null))
					{
						activeChar.sendMessage("Price of " + activeChar.getKnownSkill(holder.getSkillId()).getName() + " has been changed to " + price + "!");
						holder.setPrice(price);
						SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
					}
				}
				break;
			}
			case "sellbuffremove":
			{
				if (!activeChar.isSellingBuffs() && (params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int skillId = -1;
					
					if (st.hasMoreTokens())
					{
						skillId = Integer.parseInt(st.nextToken());
					}
					
					if ((skillId == -1))
					{
						return false;
					}
					
					final Skill skillToRemove = activeChar.getKnownSkill(skillId);
					if (skillToRemove == null)
					{
						return false;
					}
					
					final SellBuffHolder holder = activeChar.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skillToRemove.getId())).findFirst().orElse(null);
					if ((holder != null) && activeChar.getSellingBuffs().remove(holder))
					{
						activeChar.sendMessage("Skill " + activeChar.getKnownSkill(holder.getSkillId()).getName() + " has been removed!");
						SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
					}
				}
				break;
			}
			case "sellbuffaddskill":
			{
				if (!activeChar.isSellingBuffs() && (params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int skillId = -1;
					long price = -1;
					
					if (st.hasMoreTokens())
					{
						skillId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						try
						{
							price = Integer.parseInt(st.nextToken());
						}
						catch (NumberFormatException e)
						{
							activeChar.sendMessage("Too big price! Maximal price is " + Config.SELLBUFF_MIN_PRICE);
							SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
						}
					}
					
					if ((skillId == -1) || (price == -1))
					{
						return false;
					}
					
					final Skill skillToAdd = activeChar.getKnownSkill(skillId);
					if (skillToAdd == null)
					{
						return false;
					}
					else if (price < Config.SELLBUFF_MIN_PRICE)
					{
						activeChar.sendMessage("Too small price! Minimal price is " + Config.SELLBUFF_MIN_PRICE);
						return false;
					}
					else if (price > Config.SELLBUFF_MAX_PRICE)
					{
						activeChar.sendMessage("Too big price! Maximal price is " + Config.SELLBUFF_MAX_PRICE);
						return false;
					}
					else if (activeChar.getSellingBuffs().size() >= Config.SELLBUFF_MAX_BUFFS)
					{
						activeChar.sendMessage("You already reached max count of buffs! Max buffs is: " + Config.SELLBUFF_MAX_BUFFS);
						return false;
					}
					else if (!SellBuffsManager.getInstance().isInSellList(activeChar, skillToAdd))
					{
						activeChar.getSellingBuffs().add(new SellBuffHolder(skillToAdd.getId(), price));
						activeChar.sendMessage(skillToAdd.getName() + " has been added!");
						SellBuffsManager.getInstance().sendBuffChoiceMenu(activeChar, 0);
					}
				}
				break;
			}
			case "sellbuffbuymenu":
			{
				if ((params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int objId = -1;
					int index = 0;
					if (st.hasMoreTokens())
					{
						objId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						index = Integer.parseInt(st.nextToken());
					}
					
					final Player seller = World.getInstance().findPlayer(objId);
					if (seller != null)
					{
						if (!seller.isSellingBuffs() || !isInsideRadius3D(activeChar, seller, Npc.INTERACTION_DISTANCE))
						{
							return false;
						}
						
						SellBuffsManager.getInstance().sendBuffMenu(activeChar, seller, index);
					}
				}
				break;
			}
			case "sellbuffbuyskill":
			{
				if ((params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					int objId = -1;
					int skillId = -1;
					int index = 0;
					
					if (st.hasMoreTokens())
					{
						objId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						skillId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						index = Integer.parseInt(st.nextToken());
					}
					
					if ((skillId == -1) || (objId == -1))
					{
						return false;
					}
					
					final Player seller = World.getInstance().findPlayer(objId);
					if (seller == null)
					{
						return false;
					}
					
					final Skill skillToBuy = seller.getKnownSkill(skillId);
					if (!seller.isSellingBuffs() || !GameUtils.checkIfInRange(Npc.INTERACTION_DISTANCE, activeChar, seller, true) || (skillToBuy == null))
					{
						return false;
					}
					
					if (seller.getCurrentMp() < (skillToBuy.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER))
					{
						activeChar.sendMessage(seller.getName() + " has no enough mana for " + skillToBuy.getName() + "!");
						SellBuffsManager.getInstance().sendBuffMenu(activeChar, seller, index);
						return false;
					}
					
					final SellBuffHolder holder = seller.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skillToBuy.getId())).findFirst().orElse(null);
					if (holder != null)
					{
						if (AbstractScript.getQuestItemsCount(activeChar, Config.SELLBUFF_PAYMENT_ID) >= holder.getPrice())
						{
							AbstractScript.takeItems(activeChar, Config.SELLBUFF_PAYMENT_ID, holder.getPrice());
							AbstractScript.giveItems(seller, Config.SELLBUFF_PAYMENT_ID, holder.getPrice());
							seller.reduceCurrentMp(skillToBuy.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER);
							skillToBuy.activateSkill(seller, activeChar);
						}
						else
						{
							final ItemTemplate item = ItemEngine.getInstance().getTemplate(Config.SELLBUFF_PAYMENT_ID);
							if (item != null)
							{
								activeChar.sendMessage("Not enough " + item.getName() + "!");
							}
							else
							{
								activeChar.sendMessage("Not enough items!");
							}
						}
					}
					SellBuffsManager.getInstance().sendBuffMenu(activeChar, seller, index);
				}
				break;
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	@Override
	public String[] getBypassList()
	{
		return BYPASS_COMMANDS;
	}
}