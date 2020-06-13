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
package ai.others.ClanHallManager;

import ai.AbstractNpcAI;
import org.l2j.commons.util.CommonUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import org.l2j.gameserver.data.xml.impl.ResidenceFunctionsData;
import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.enums.ClanHallGrade;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.residences.ResidenceFunctionTemplate;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.AgitDecoInfo;
import org.l2j.gameserver.world.World;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Clan Hall Manager AI.
 * @author St3eT
 */
public final class ClanHallManager extends AbstractNpcAI
{
	// NPCs
	// @formatter:off
	private static final int[] CLANHALL_MANAGERS =
	{
		35384, 35386, 35388, 35390, // Gludio
		35400, 35392, 35394, 35396, 35398, // Gludin
		35403, 35405, 35407, // Dion
		35439, 35441, 35443, 35445, 35447, 35449, // Aden
		35451, 35453, 35455, 35457, 35459, // Giran
		35461, 35463, 35465, 35467, // Goddard
		35566, 35568, 35570, 35572, 35574, 35576, 35578, // Rune
		35580, 35582, 35584, 35586, // Schuttgart
		36721, 36723, 36725, 36727, // Gludio Outskirts
		36729, 36731, 36733, 36735, // Dion Outskirts
		36737, 36739, // Floran Village
	};
	// @formatter:on
	// Misc
	private static final int[] ALLOWED_BUFFS =
	{
		4342, // Wind Walk
		4343, // Decrease Weight
		4344, // Shield
		4346, // Mental Shield
		4345, // Might
		4347, // Blessed Body
		4349, // Magic Barrier
		4350, // Resist Shock
		4348, // Blessed Soul
		4351, // Concentration
		4352, // Berserker Spirit
		4353, // Blessed Shield
		4358, // Guidance
		4354, // Vampiric Rage
		4355, // Acumen
		4356, // Empower
		4357, // Haste
		4359, // Focus
		4360, // Death Whisper
	};
	
	private ClanHallManager()
	{
		addStartNpc(CLANHALL_MANAGERS);
		addTalkId(CLANHALL_MANAGERS);
		addFirstTalkId(CLANHALL_MANAGERS);
		addSeeCreatureId(CLANHALL_MANAGERS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final StringTokenizer st = new StringTokenizer(event, " ");
		final String action = st.nextToken();
		final ClanHall clanHall = npc.getClanHall();
		String htmltext = null;
		
		if ((clanHall != null) && isOwningClan(player, npc))
		{
			switch (action)
			{
				case "index":
				{
					htmltext = isOwningClan(player, npc) ? "ClanHallManager-01.html" : "ClanHallManager-03.html";
					break;
				}
				case "manageDoors":
				{
					if (player.hasClanPrivilege(ClanPrivilege.CH_OPEN_DOOR))
					{
						if (st.hasMoreTokens())
						{
							final boolean open = st.nextToken().equals("1");
							clanHall.openCloseDoors(open);
							htmltext = "ClanHallManager-0" + (open ? "5" : "6") + ".html";
						}
						else
						{
							htmltext = "ClanHallManager-04.html";
						}
					}
					else
					{
						htmltext = "ClanHallManager-noAuthority.html";
					}
					break;
				}
				case "expel":
				{
					if (player.hasClanPrivilege(ClanPrivilege.CH_DISMISS))
					{
						if (st.hasMoreTokens())
						{
							clanHall.banishOthers();
							htmltext = "ClanHallManager-08.html";
						}
						else
						{
							htmltext = "ClanHallManager-07.html";
						}
					}
					else
					{
						htmltext = "ClanHallManager-noAuthority.html";
					}
					break;
				}
				case "useFunctions":
				{
					if (player.hasClanPrivilege(ClanPrivilege.CH_OTHER_RIGHTS))
					{
						if (!st.hasMoreTokens())
						{
							final ResidenceFunctionData hpFunc = clanHall.getFunction(ResidenceFunctionType.HP_REGEN);
							final ResidenceFunctionData mpFunc = clanHall.getFunction(ResidenceFunctionType.MP_REGEN);
							final ResidenceFunctionData xpFunc = clanHall.getFunction(ResidenceFunctionType.EXP_RESTORE);
							htmltext = getHtm(player, "ClanHallManager-09.html");
							htmltext = htmltext.replaceAll("%hpFunction%", hpFunc != null ? String.valueOf((int) hpFunc.getValue()) : "0");
							htmltext = htmltext.replaceAll("%mpFunction%", mpFunc != null ? String.valueOf((int) mpFunc.getValue()) : "0");
							htmltext = htmltext.replaceAll("%resFunction%", xpFunc != null ? String.valueOf((int) xpFunc.getValue()) : "0");
						}
						else
						{
							switch (st.nextToken())
							{
								case "teleport":
								{
									final int teleportLevel = clanHall.getFunctionLevel(ResidenceFunctionType.TELEPORT);
									if (teleportLevel > 0)
									{
										final TeleportHolder holder = TeleportersData.getInstance().getHolder(npc.getId(), "tel" + teleportLevel);
										if (holder != null)
										{
											if (!st.hasMoreTokens())
											{
												holder.showTeleportList(player, npc, "Quest ClanHallManager useFunctions teleport");
											}
											else if (st.countTokens() >= 2)
											{
												final String listName = st.nextToken();
												final int funcLvl = (listName.length() >= 4) ? CommonUtil.parseInt(listName.substring(3), -1) : -1;
												if (teleportLevel == funcLvl)
												{
													holder.doTeleport(player, npc, Util.parseNextInt(st, -1));
												}
											}
										}
									}
									else
									{
										htmltext = "ClanHallManager-noFunction.html";
									}
									break;
								}
								case "buffs":
								{
									final int buffLevel = clanHall.getFunctionLevel(ResidenceFunctionType.BUFF);
									if (buffLevel > 0)
									{
										if (!st.hasMoreTokens())
										{
											htmltext = getHtm(player, "ClanHallManager-funcBuffs_" + buffLevel + ".html");
											htmltext = htmltext.replaceAll("%manaLeft%", Integer.toString((int) npc.getCurrentMp()));
										}
										else
										{
											final String[] skillData = st.nextToken().split("_");
											final SkillHolder skill = new SkillHolder(Integer.parseInt(skillData[0]), Integer.parseInt(skillData[1]));
											if (Util.contains(ALLOWED_BUFFS, skill.getSkillId()))
											{
												if (npc.getCurrentMp() < (npc.getStats().getMpConsume(skill.getSkill()) + npc.getStats().getMpInitialConsume(skill.getSkill())))
												{
													htmltext = getHtm(player, "ClanHallManager-funcBuffsNoMp.html");
												}
												else if (npc.isSkillDisabled(skill.getSkill()))
												{
													htmltext = getHtm(player, "ClanHallManager-funcBuffsNoReuse.html");
												}
												else
												{
													castSkill(npc, player, skill);
													htmltext = getHtm(player, "ClanHallManager-funcBuffsDone.html");
												}
												htmltext = htmltext.replaceAll("%manaLeft%", Integer.toString((int) npc.getCurrentMp()));
											}
										}
									}
									else
									{
										htmltext = "ClanHallManager-noFunction.html";
									}
									break;
								}
								case "items":
								{
									final int itemLevel = clanHall.getFunctionLevel(ResidenceFunctionType.ITEM);
									switch (itemLevel)
									{
										case 1:
										case 2:
										case 3:
										{
											((Merchant) npc).showBuyWindow(player, Integer.parseInt(npc.getId() + "0" + (itemLevel - 1)));
											break;
										}
										default:
										{
											htmltext = "ClanHallManager-noFunction.html";
										}
									}
									break;
								}
							}
						}
					}
					else
					{
						htmltext = "ClanHallManager-noAuthority.html";
					}
					break;
				}
				case "warehouse":
				{
					htmltext = getHtm(player, "ClanHallManager-10.html");
					htmltext = htmltext.replaceAll("%lease%", String.valueOf(clanHall.getLease()));
					htmltext = htmltext.replaceAll("%payDate%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(clanHall.getNextPayment())));
					break;
				}
				case "manageFunctions":
				{
					if (player.hasClanPrivilege(ClanPrivilege.CH_SET_FUNCTIONS))
					{
						if (!st.hasMoreTokens())
						{
							htmltext = "ClanHallManager-11.html";
						}
						else
						{
							switch (st.nextToken())
							{
								case "recovery":
								{
									htmltext = getHtm(player, clanHall.getGrade() == ClanHallGrade.S ? "ClanHallManager-manageFuncRecoverySGrade.html" : "ClanHallManager-manageFuncRecoveryBGrade.html");
									htmltext = getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.HP_REGEN), htmltext, "HP");
									htmltext = getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.MP_REGEN), htmltext, "MP");
									htmltext = getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.EXP_RESTORE), htmltext, "XP");
									break;
								}
								case "other":
								{
									htmltext = getHtm(player, "ClanHallManager-manageFuncOther.html");
									htmltext = getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.TELEPORT), htmltext, "TP");
									htmltext = getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.BUFF), htmltext, "BUFF");
									htmltext = getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.ITEM), htmltext, "ITEM");
									break;
								}
								case "decor":
								{
									htmltext = getHtm(player, "ClanHallManager-manageFuncDecor.html");
									htmltext = getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.CURTAIN), htmltext, "CURTAIN");
									htmltext = getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.PLATFORM), htmltext, "PODIUM");
									break;
								}
								case "selectFunction":
								{
									if (st.countTokens() == 2)
									{
										final int funcId = Integer.parseInt(st.nextToken());
										final int funcLv = Integer.parseInt(st.nextToken());
										
										final ResidenceFunctionData oldFunc = clanHall.getFunction(funcId, funcLv);
										if (oldFunc != null)
										{
											final int funcVal = (int) oldFunc.getTemplate().getValue();
											htmltext = getHtm(player, "ClanHallManager-manageFuncAlreadySet.html");
											htmltext = htmltext.replaceAll("%funcEffect%", "<fstring p1=\"" + (funcVal > 0 ? funcVal : oldFunc.getLevel()) + "\">" + (funcVal > 0 ? NpcStringId.S1.getId() : NpcStringId.STAGE_S1.getId()) + "</fstring>");
										}
										else if ((funcId >= 1) && (funcId <= 8))
										{
											final ResidenceFunctionTemplate template = ResidenceFunctionsData.getInstance().getFunction(funcId, funcLv);
											if (template != null)
											{
												htmltext = getHtm(player, "ClanHallManager-funcConfirm" + funcId + ".html");
												htmltext = htmltext.replaceAll("%funcId%", String.valueOf(funcId));
												htmltext = htmltext.replaceAll("%funcLv%", String.valueOf(funcLv));
												htmltext = htmltext.replaceAll("%funcCost%", "<fstring p1=\"" + template.getCost().getCount() + "\" p2=\"" + template.getDurationAsDays() + "\">" + NpcStringId.FONT_COLOR_FFAABB_S1_FONT_ADENA_S2_DAY_S.getId() + "</fstring>");
											}
										}
									}
									break;
								}
								case "setFunction":
								{
									if (st.countTokens() == 2)
									{
										final int funcId = Integer.parseInt(st.nextToken());
										final int funcLv = Integer.parseInt(st.nextToken());
										final ResidenceFunctionTemplate template = ResidenceFunctionsData.getInstance().getFunction(funcId, funcLv);
										if ((template != null) && (getQuestItemsCount(player, template.getCost().getId()) >= template.getCost().getCount()))
										{
											if (clanHall.getFunction(funcId, funcLv) != null)
											{
												return null;
											}
											
											takeItems(player, template.getCost().getId(), template.getCost().getCount());
											clanHall.addFunction(funcId, funcLv);
											updateVisualEffects(clanHall, npc);
											htmltext = "ClanHallManager-manageFuncDone.html";
										}
										else
										{
											htmltext = "ClanHallManager-noAdena.html";
										}
									}
									break;
								}
								case "removeFunction":
								{
									if (st.countTokens() == 2)
									{
										final String act = st.nextToken();
										
										final ResidenceFunctionType funcType = ResidenceFunctionType.valueOf(st.nextToken());
										if (funcType != null)
										{
											if (act.equals("confirm"))
											{
												htmltext = getHtm(player, "ClanHallManager-removeFunctionConfirm.html");
												htmltext = htmltext.replaceAll("%FUNC_TYPE%", funcType.toString());
											}
											else if (act.equals("remove"))
											{
												final ResidenceFunctionData func = clanHall.getFunction(funcType);
												if (func != null)
												{
													clanHall.removeFunction(func);
													updateVisualEffects(clanHall, npc);
													htmltext = "ClanHallManager-removeFunctionDone.html";
												}
												else
												{
													htmltext = "ClanHallManager-removeFunctionFail.html";
												}
											}
											else
											{
												htmltext = "ClanHallManager-removeFunctionFail.html";
											}
										}
										else
										{
											htmltext = "ClanHallManager-removeFunctionFail.html";
										}
									}
									else
									{
										htmltext = "ClanHallManager-removeFunctionFail.html";
									}
									break;
								}
							}
						}
					}
					else
					{
						htmltext = "ClanHallManager-noAuthority.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		final ClanHall clanHall = npc.getClanHall();
		
		if (isOwningClan(player, npc))
		{
			if (clanHall.getCostFailDay() == 0)
			{
				htmltext = "ClanHallManager-01.html";
			}
			else
			{
				htmltext = getHtm(player, "ClanHallManager-02.html");
				htmltext = htmltext.replaceAll("%costFailDayLeft%", Integer.toString((8 - clanHall.getCostFailDay())));
			}
		}
		else
		{
			htmltext = "ClanHallManager-03.html";
		}
		return htmltext;
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (isPlayer(creature))
		{
			final ClanHall clanHall = npc.getClanHall();
			if (clanHall != null)
			{
				creature.getActingPlayer().sendPacket(new AgitDecoInfo(clanHall));
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	private void updateVisualEffects(ClanHall clanHall, Npc npc)
	{
		World.getInstance().forEachVisibleObject(npc, Player.class, player -> player.sendPacket(new AgitDecoInfo(clanHall)));
	}
	
	private String getFunctionInfo(ResidenceFunctionData func, String htmltext, String name)
	{
		if (func != null)
		{
			htmltext = htmltext.replaceAll("%" + name + "recovery%", String.valueOf((int) func.getTemplate().getValue()) + "%");
			htmltext = htmltext.replaceAll("%" + name + "price%", "<fstring p1=\"" + func.getTemplate().getCost().getCount() + "\" p2=\"" + func.getTemplate().getDurationAsDays() + "\">" + NpcStringId.FONT_COLOR_FFAABB_S1_FONT_ADENA_S2_DAY_S.getId() + "</fstring>");
			htmltext = htmltext.replace("%" + name + "expire%", "Withdraw the fee for the next time at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(func.getExpiration())));
			htmltext = htmltext.replaceAll("%" + name + "deactive%", "[<a action=\"bypass -h Quest ClanHallManager manageFunctions removeFunction confirm " + func.getType().toString() + "\">Deactivate</a>]");
		}
		else
		{
			htmltext = htmltext.replaceAll("%" + name + "recovery%", "<fstring p1=\"\" p2=\"\">" + NpcStringId.NONE_2.getId() + "</fstring>");
			htmltext = htmltext.replaceAll("%" + name + "price%", "");
			htmltext = htmltext.replaceAll("%" + name + "expire%", "");
			htmltext = htmltext.replaceAll("%" + name + "deactive%", "");
		}
		return htmltext;
	}
	
	private boolean isOwningClan(Player player, Npc npc)
	{
		return ((npc.getClanHall().getOwnerId() == player.getClanId()) && (player.getClanId() != 0));
	}
	
	public static AbstractNpcAI provider()
	{
		return new ClanHallManager();
	}
}