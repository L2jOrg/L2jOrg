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
package handlers.admincommandhandlers;

import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

import java.util.List;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands: - gm = turns gm mode on/off
 * @version $Revision: 1.1.2.1 $ $Date: 2005/03/15 21:32:48 $
 */
public class AdminFightCalculator implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_fight_calculator",
		"admin_fight_calculator_show",
		"admin_fcs",
	};
	
	// TODO: remove from gm list etc etc
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		try
		{
			if (command.startsWith("admin_fight_calculator_show"))
			{
				handleShow(command.substring("admin_fight_calculator_show".length()), activeChar);
			}
			else if (command.startsWith("admin_fcs"))
			{
				handleShow(command.substring("admin_fcs".length()), activeChar);
			}
			else if (command.startsWith("admin_fight_calculator"))
			{
				handleStart(command.substring("admin_fight_calculator".length()), activeChar);
			}
		}
		catch (StringIndexOutOfBoundsException e)
		{
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleStart(String params, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(params);
		int lvl1 = 0;
		int lvl2 = 0;
		int mid1 = 0;
		int mid2 = 0;
		while (st.hasMoreTokens())
		{
			final String s = st.nextToken();
			if (s.equals("lvl1"))
			{
				lvl1 = Integer.parseInt(st.nextToken());
				continue;
			}
			if (s.equals("lvl2"))
			{
				lvl2 = Integer.parseInt(st.nextToken());
				continue;
			}
			if (s.equals("mid1"))
			{
				mid1 = Integer.parseInt(st.nextToken());
				continue;
			}
			if (s.equals("mid2"))
			{
				mid2 = Integer.parseInt(st.nextToken());
				continue;
			}
		}
		
		NpcTemplate npc1 = null;
		if (mid1 != 0)
		{
			npc1 = NpcData.getInstance().getTemplate(mid1);
		}
		NpcTemplate npc2 = null;
		if (mid2 != 0)
		{
			npc2 = NpcData.getInstance().getTemplate(mid2);
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		
		final String replyMSG;
		
		if ((npc1 != null) && (npc2 != null))
		{
			replyMSG = "<html><title>Selected mobs to fight</title><body><table><tr><td>First</td><td>Second</td></tr><tr><td>level " + lvl1 + "</td><td>level " + lvl2 + "</td></tr><tr><td>id " + npc1.getId() + "</td><td>id " + npc2.getId() + "</td></tr><tr><td>" + npc1.getName() + "</td><td>" + npc2.getName() + "</td></tr></table><center><br><br><br><button value=\"OK\" action=\"bypass -h admin_fight_calculator_show " + npc1.getId() + " " + npc2.getId() + "\"  width=100 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>";
		}
		else if ((lvl1 != 0) && (npc1 == null))
		{
			final List<NpcTemplate> npcs = NpcData.getInstance().getAllOfLevel(lvl1);
			final StringBuilder sb = new StringBuilder(50 + (npcs.size() * 200));
			sb.append("<html><title>Select first mob to fight</title><body><table>");
			
			for (NpcTemplate n : npcs)
			{
				sb.append("<tr><td><a action=\"bypass -h admin_fight_calculator lvl1 " + lvl1 + " lvl2 " + lvl2 + " mid1 " + n.getId() + " mid2 " + mid2 + "\">" + n.getName() + "</a></td></tr>");
			}
			
			sb.append("</table></body></html>");
			replyMSG = sb.toString();
		}
		else if ((lvl2 != 0) && (npc2 == null))
		{
			final List<NpcTemplate> npcs = NpcData.getInstance().getAllOfLevel(lvl2);
			final StringBuilder sb = new StringBuilder(50 + (npcs.size() * 200));
			sb.append("<html><title>Select second mob to fight</title><body><table>");
			
			for (NpcTemplate n : npcs)
			{
				sb.append("<tr><td><a action=\"bypass -h admin_fight_calculator lvl1 " + lvl1 + " lvl2 " + lvl2 + " mid1 " + mid1 + " mid2 " + n.getId() + "\">" + n.getName() + "</a></td></tr>");
			}
			
			sb.append("</table></body></html>");
			replyMSG = sb.toString();
		}
		else
		{
			replyMSG = "<html><title>Select mobs to fight</title><body><table><tr><td>First</td><td>Second</td></tr><tr><td><edit var=\"lvl1\" width=80></td><td><edit var=\"lvl2\" width=80></td></tr></table><center><br><br><br><button value=\"OK\" action=\"bypass -h admin_fight_calculator lvl1 $lvl1 lvl2 $lvl2\"  width=100 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>";
		}
		
		adminReply.setHtml(replyMSG);
		activeChar.sendPacket(adminReply);
	}
	
	private void handleShow(String params, Player activeChar)
	{
		params = params.trim();
		
		Creature npc1 = null;
		Creature npc2 = null;
		if (params.isEmpty())
		{
			npc1 = activeChar;
			npc2 = (Creature) activeChar.getTarget();
			if (npc2 == null)
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				return;
			}
		}
		else
		{
			int mid1 = 0;
			int mid2 = 0;
			final StringTokenizer st = new StringTokenizer(params);
			mid1 = Integer.parseInt(st.nextToken());
			mid2 = Integer.parseInt(st.nextToken());
			
			npc1 = new Monster(NpcData.getInstance().getTemplate(mid1));
			npc2 = new Monster(NpcData.getInstance().getTemplate(mid2));
		}
		
		int miss1 = 0;
		int miss2 = 0;
		int shld1 = 0;
		int shld2 = 0;
		int crit1 = 0;
		int crit2 = 0;
		double patk1 = 0;
		double patk2 = 0;
		double pdef1 = 0;
		double pdef2 = 0;
		double dmg1 = 0;
		double dmg2 = 0;
		
		// ATTACK speed in milliseconds
		int sAtk1 = Formulas.calculateTimeBetweenAttacks(npc1.getPAtkSpd());
		int sAtk2 = Formulas.calculateTimeBetweenAttacks(npc2.getPAtkSpd());
		// number of ATTACK per 100 seconds
		sAtk1 = 100000 / sAtk1;
		sAtk2 = 100000 / sAtk2;
		
		for (int i = 0; i < 10000; i++)
		{
			final boolean _miss1 = Formulas.calcHitMiss(npc1, npc2);
			if (_miss1)
			{
				miss1++;
			}
			final byte _shld1 = Formulas.calcShldUse(npc1, npc2, false);
			if (_shld1 > 0)
			{
				shld1++;
			}
			final boolean _crit1 = Formulas.calcCrit(npc1.getCriticalHit(), npc1, npc2, null);
			if (_crit1)
			{
				crit1++;
			}
			
			double _patk1 = npc1.getPAtk();
			_patk1 += npc1.getRandomDamageMultiplier();
			patk1 += _patk1;
			
			final double _pdef1 = npc1.getPDef();
			pdef1 += _pdef1;
			
			if (!_miss1)
			{
				final double _dmg1 = Formulas.calcAutoAttackDamage(npc1, npc2, _shld1, _crit1, false);
				dmg1 += _dmg1;
				npc1.abortAttack();
			}
		}
		
		for (int i = 0; i < 10000; i++)
		{
			final boolean _miss2 = Formulas.calcHitMiss(npc2, npc1);
			if (_miss2)
			{
				miss2++;
			}
			final byte _shld2 = Formulas.calcShldUse(npc2, npc1, false);
			if (_shld2 > 0)
			{
				shld2++;
			}
			final boolean _crit2 = Formulas.calcCrit(npc2.getCriticalHit(), npc2, npc1, null);
			if (_crit2)
			{
				crit2++;
			}
			
			double _patk2 = npc2.getPAtk();
			_patk2 *= npc2.getRandomDamageMultiplier();
			patk2 += _patk2;
			
			final double _pdef2 = npc2.getPDef();
			pdef2 += _pdef2;
			
			if (!_miss2)
			{
				final double _dmg2 = Formulas.calcAutoAttackDamage(npc2, npc1, _shld2, _crit2, false);
				dmg2 += _dmg2;
				npc2.abortAttack();
			}
		}
		
		miss1 /= 100;
		miss2 /= 100;
		shld1 /= 100;
		shld2 /= 100;
		crit1 /= 100;
		crit2 /= 100;
		patk1 /= 10000;
		patk2 /= 10000;
		pdef1 /= 10000;
		pdef2 /= 10000;
		dmg1 /= 10000;
		dmg2 /= 10000;
		
		// total damage per 100 seconds
		final int tdmg1 = (int) (sAtk1 * dmg1);
		final int tdmg2 = (int) (sAtk2 * dmg2);
		// HP restored per 100 seconds
		final double maxHp1 = npc1.getMaxHp();
		final int hp1 = (int) ((npc1.getStats().getValue(Stat.REGENERATE_HP_RATE) * 100000) / Formulas.getRegeneratePeriod(npc1));
		
		final double maxHp2 = npc2.getMaxHp();
		final int hp2 = (int) ((npc2.getStats().getValue(Stat.REGENERATE_HP_RATE) * 100000) / Formulas.getRegeneratePeriod(npc2));
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		
		final StringBuilder replyMSG = new StringBuilder(1000);
		replyMSG.append("<html><title>Selected mobs to fight</title><body><table>");
		
		if (params.isEmpty())
		{
			replyMSG.append("<tr><td width=140>Parameter</td><td width=70>me</td><td width=70>target</td></tr>");
		}
		else
		{
			replyMSG.append("<tr><td width=140>Parameter</td><td width=70>" + ((NpcTemplate) npc1.getTemplate()).getName() + "</td><td width=70>" + ((NpcTemplate) npc2.getTemplate()).getName() + "</td></tr>");
		}
		
		replyMSG.append("<tr><td>miss</td><td>" + miss1 + "%</td><td>" + miss2 + "%</td></tr><tr><td>shld</td><td>" + shld2 + "%</td><td>" + shld1 + "%</td></tr><tr><td>crit</td><td>" + crit1 + "%</td><td>" + crit2 + "%</td></tr><tr><td>pAtk / pDef</td><td>" + (int) patk1 + " / " + (int) pdef1 + "</td><td>" + (int) patk2 + " / " + (int) pdef2 + "</td></tr><tr><td>made hits</td><td>" + sAtk1 + "</td><td>" + sAtk2 + "</td></tr><tr><td>dmg per hit</td><td>" + (int) dmg1 + "</td><td>" + (int) dmg2 + "</td></tr><tr><td>got dmg</td><td>" + tdmg2 + "</td><td>" + tdmg1 + "</td></tr><tr><td>got regen</td><td>" + hp1 + "</td><td>" + hp2 + "</td></tr><tr><td>had HP</td><td>" + (int) maxHp1 + "</td><td>" + (int) maxHp2 + "</td></tr><tr><td>die</td>");
		
		if ((tdmg2 - hp1) > 1)
		{
			replyMSG.append("<td>" + ((int) ((100 * maxHp1) / (tdmg2 - hp1))) + " sec</td>");
		}
		else
		{
			replyMSG.append("<td>never</td>");
		}
		
		if ((tdmg1 - hp2) > 1)
		{
			replyMSG.append("<td>" + ((int) ((100 * maxHp2) / (tdmg1 - hp2))) + " sec</td>");
		}
		else
		{
			replyMSG.append("<td>never</td>");
		}
		
		replyMSG.append("</tr></table><center><br>");
		
		if (params.isEmpty())
		{
			replyMSG.append("<button value=\"Retry\" action=\"bypass -h admin_fight_calculator_show\"  width=100 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		else
		{
			replyMSG.append("<button value=\"Retry\" action=\"bypass -h admin_fight_calculator_show " + ((NpcTemplate) npc1.getTemplate()).getId() + " " + ((NpcTemplate) npc2.getTemplate()).getId() + "\"  width=100 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		
		replyMSG.append("</center></body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
		
		if (params.length() != 0)
		{
			npc1.deleteMe();
			npc2.deleteMe();
		}
	}
}
