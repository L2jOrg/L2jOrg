/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.ai.others.CastleChamberlain;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.scripts.ai.AbstractNpcAI;

import java.util.Calendar;
import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.parseNextInt;

/**
 * Castle Chamberlain AI.
 * @author malyelfik
 */
public final class CastleChamberlain extends AbstractNpcAI
{
	// NPCs
	//@formatter:off
	private static final int[] NPC =
	{
		// Chamberlain of Light / Chamberlain of Darkness
		35100, 36653, // Gludio
		35142, 36654, // Dion
		35184, 36655, // Giran
		35226, 36656, // Oren
		35274, 36657, // Aden
		// 35316, 36658, // Innadril
		// 35363, 36659, // Goddard
		// 35509, 36660, // Rune
		// 35555, 36661, // Schuttgart
	};
	//@formatter:on
	// Item
	private static final int CROWN = 6841;
	private static final int LORD_CLOAK_OF_LIGHT = 34925;
	private static final int LORD_CLOAK_OF_DARK = 34926;

	
	// Buffs
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(4342, 2), // Wind Walk Lv.2
		new SkillHolder(4343, 3), // Decrease Weight Lv.3
		new SkillHolder(4344, 3), // Shield Lv.3
		new SkillHolder(4346, 4), // Mental Shield Lv.4
		new SkillHolder(4345, 3), // Might Lv.3
		new SkillHolder(4347, 2), // Bless the Body Lv.2
		new SkillHolder(4349, 1), // Magic Barrier Lv.1
		new SkillHolder(4350, 1), // Resist Shock Lv.1
		new SkillHolder(4348, 2), // Bless the Soul Lv.2
		new SkillHolder(4351, 2), // Concentration Lv.2
		new SkillHolder(4352, 1), // Berserker Spirit Lv.1
		new SkillHolder(4353, 2), // Bless Shield Lv.2
		new SkillHolder(4358, 1), // Guidance Lv.1
		new SkillHolder(4354, 1), // Vampiric Rage Lv.1
		new SkillHolder(4347, 6), // Bless the Body Lv.6
		new SkillHolder(4349, 2), // Magic Barrier Lv.2
		new SkillHolder(4350, 4), // Resist Shock Lv.4
		new SkillHolder(4348, 6), // Bless the Soul Lv.6
		new SkillHolder(4351, 6), // Concentration Lv.6
		new SkillHolder(4352, 2), // Berserker Spirit Lv.2
		new SkillHolder(4353, 6), // Bless Shield Lv.6
		new SkillHolder(4358, 3), // Guidance Lv.3
		new SkillHolder(4354, 4), // Vampiric Rage Lv.4
		new SkillHolder(4355, 1), // Acumen Lv.1
		new SkillHolder(4356, 1), // Empower Lv.1
		new SkillHolder(4357, 1), // Haste Lv.1
		new SkillHolder(4359, 1), // Focus Lv.1
		new SkillHolder(4360, 1), // Death Whisper Lv.1
	};
	public static final String CHAMBERLAIN_01_HTML = "chamberlain-01.html";
	public static final String CASTLEFUNCDISABLED_HTML = "castlefuncdisabled.html";
	public static final String CHAMBERLAIN_08_HTML = "chamberlain-08.html";
	public static final String CHAMBERLAIN_09_HTML = "chamberlain-09.html";

	private CastleChamberlain()
	{
		addStartNpc(NPC);
		addTalkId(NPC);
		addFirstTalkId(NPC);
	}
	
	private NpcHtmlMessage getHtmlPacket(Player player, Npc npc, String htmlFile)
	{
		final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
		packet.setHtml(getHtml(player, htmlFile));
		return packet;
	}
	
	private final String funcConfirmHtml(Player player, Npc npc, Castle castle, int func, int level)
	{
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
		{
			final NpcHtmlMessage html;
			final String fstring = (func == Castle.FUNC_TELEPORT) ? "9" : "10";
			if (level == 0)
			{
				html = getHtmlPacket(player, npc, "castleresetdeco.html");
				html.replace("%AgitDecoSubmit%", Integer.toString(func));
			}
			else if ((castle.getCastleFunction(func) != null) && (castle.getCastleFunction(func).getLevel() == level))
			{
				html = getHtmlPacket(player, npc, "castledecoalreadyset.html");
				html.replace("%AgitDecoEffect%", "<fstring p1=\"" + level + "\">" + fstring + "</fstring>");
			}
			else
			{
				html = getHtmlPacket(player, npc, "castledeco-0" + func + ".html");
				html.replace("%AgitDecoCost%", "<fstring p1=\"" + getFunctionFee(func, level) + "\" p2=\"" + (getFunctionRatio(func) / 86400000) + "\">6</fstring>");
				html.replace("%AgitDecoEffect%", "<fstring p1=\"" + level + "\">" + fstring + "</fstring>");
				html.replace("%AgitDecoSubmit%", func + " " + level);
			}
			player.sendPacket(html);
			return null;
		}
		return "chamberlain-21.html";
	}
	
	private final void funcReplace(Castle castle, NpcHtmlMessage html, int func, String str)
	{
		var function = castle.getCastleFunction(func);
		if (function == null)
		{
			html.replace("%" + str + "Depth%", "<fstring>4</fstring>");
			html.replace("%" + str + "Cost%", "");
			html.replace("%" + str + "Expire%", "<fstring>4</fstring>");
			html.replace("%" + str + "Reset%", "");
		}
		else
		{
			final String fstring = ((func == Castle.FUNC_SUPPORT) || (func == Castle.FUNC_TELEPORT)) ? "9" : "10";
			final Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(function.getEndTime());
			html.replace("%" + str + "Depth%", "<fstring p1=\"" + function.getLevel() + "\">" + fstring + "</fstring>");
			html.replace("%" + str + "Cost%", "<fstring p1=\"" + function.getLease() + "\" p2=\"" + (function.getRate() / 86400000) + "\">6</fstring>");
			html.replace("%" + str + "Expire%", "<fstring p1=\"" + calendar.get(Calendar.DATE) + "\" p2=\"" + (calendar.get(Calendar.MONTH) + 1) + "\" p3=\"" + calendar.get(Calendar.YEAR) + "\">5</fstring>");
			html.replace("%" + str + "Reset%", "[<a action=\"bypass -h Quest CastleChamberlain " + str + " 0\">Deactivate</a>]");
		}
	}
	
	private final int getFunctionFee(int func, int level)
	{
		int fee = 0;
		switch (func)
		{
			case Castle.FUNC_RESTORE_EXP:
			{
				fee = (level == 45) ? Config.CS_EXPREG1_FEE : Config.CS_EXPREG2_FEE;
				break;
			}
			case Castle.FUNC_RESTORE_HP:
			{
				fee = (level == 300) ? Config.CS_HPREG1_FEE : Config.CS_HPREG2_FEE;
				break;
			}
			case Castle.FUNC_RESTORE_MP:
			{
				fee = (level == 40) ? Config.CS_MPREG1_FEE : Config.CS_MPREG2_FEE;
				break;
			}
			case Castle.FUNC_SUPPORT:
			{
				fee = (level == 5) ? Config.CS_SUPPORT1_FEE : Config.CS_SUPPORT2_FEE;
				break;
			}
			case Castle.FUNC_TELEPORT:
			{
				fee = (level == 1) ? Config.CS_TELE1_FEE : Config.CS_TELE2_FEE;
				break;
			}
		}
		return fee;
	}
	
	private final long getFunctionRatio(int func)
	{
		long ratio = 0;
		switch (func)
		{
			case Castle.FUNC_RESTORE_EXP:
			{
				ratio = Config.CS_EXPREG_FEE_RATIO;
				break;
			}
			case Castle.FUNC_RESTORE_HP:
			{
				ratio = Config.CS_HPREG_FEE_RATIO;
				break;
			}
			case Castle.FUNC_RESTORE_MP:
			{
				ratio = Config.CS_MPREG_FEE_RATIO;
				break;
			}
			case Castle.FUNC_SUPPORT:
			{
				ratio = Config.CS_SUPPORT_FEE_RATIO;
				break;
			}
			case Castle.FUNC_TELEPORT:
			{
				ratio = Config.CS_TELE_FEE_RATIO;
				break;
			}
		}
		return ratio;
	}
	
	private final int getDoorUpgradePrice(int type, int level)
	{
		int price = 0;
		switch (type)
		{
			case 1: // Outer Door
			{
				switch (level)
				{
					case 2:
					{
						price = Config.OUTER_DOOR_UPGRADE_PRICE2;
						break;
					}
					case 3:
					{
						price = Config.OUTER_DOOR_UPGRADE_PRICE3;
						break;
					}
					case 5:
					{
						price = Config.OUTER_DOOR_UPGRADE_PRICE5;
						break;
					}
				}
				break;
			}
			case 2: // Inner Door
			{
				switch (level)
				{
					case 2:
					{
						price = Config.INNER_DOOR_UPGRADE_PRICE2;
						break;
					}
					case 3:
					{
						price = Config.INNER_DOOR_UPGRADE_PRICE3;
						break;
					}
					case 5:
					{
						price = Config.INNER_DOOR_UPGRADE_PRICE5;
						break;
					}
				}
				break;
			}
			case 3: // Wall
			{
				switch (level)
				{
					case 2:
					{
						price = Config.WALL_UPGRADE_PRICE2;
						break;
					}
					case 3:
					{
						price = Config.WALL_UPGRADE_PRICE3;
						break;
					}
					case 5:
					{
						price = Config.WALL_UPGRADE_PRICE5;
						break;
					}
				}
				break;
			}
		}
		return price;
	}
	
	private final int getTrapUpgradePrice(int level)
	{
		int price = 0;
		switch (level)
		{
			case 1:
			{
				price = Config.TRAP_UPGRADE_PRICE1;
				break;
			}
			case 2:
			{
				price = Config.TRAP_UPGRADE_PRICE2;
				break;
			}
			case 3:
			{
				price = Config.TRAP_UPGRADE_PRICE3;
				break;
			}
			case 4:
			{
				price = Config.TRAP_UPGRADE_PRICE4;
				break;
			}
		}
		return price;
	}
	
	private boolean isOwner(Player player, Npc npc)
	{
		return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || ((player.getClan() != null) && (player.getClanId() == npc.getCastle().getOwnerId()));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		final Castle castle = npc.getCastle();
		final StringTokenizer st = new StringTokenizer(event, " ");
		final boolean isMyLord = player.isClanLeader() && (player.getClan().getCastleId() == (npc.getCastle() != null ? npc.getCastle().getId() : -1));

		return switch (st.nextToken()) {
			case CHAMBERLAIN_01_HTML -> event;
			case "siege_functions" -> siegeFunctions(npc, player, castle);
			case "manage_doors" -> manageDoors(npc, player, st);
			case "upgrade_doors" -> upgradeDoors(event, npc, player, st);
			case "upgrade_doors_confirm" -> upgradeDoorsConfirm(npc, player, castle, st);
			case "manage_trap" -> manageTrap(npc, player, castle, st);
			case "upgrade_trap" -> upgradeTrap(npc, player, st);
			case "upgrade_trap_confirm" -> upgradeTrapConfirm(npc, player, castle, st);
			case "receive_report" -> receiveReport(npc, player, castle, isMyLord);
			case "manage_vault" -> manageVault(npc, player, castle);
			case "manage_vault_deposit" -> manageVaultDeposit(npc, player, castle);
			case "manage_vault_withdraw" -> manageVaultWithdraw(npc, player, castle);
			case "deposit" -> deposit(npc, player, castle, st);
			case "withdraw" -> withdraw(npc, player, castle, st);
			case "manage_functions" -> manageFunctions(npc, player, castle);
			case "banish_foreigner_show" -> banishForeignerShow(npc, player, castle);
			case "banish_foreigner" -> banishForeigner(npc, player, castle);
			case "doors" -> doors(npc, player, castle);
			case "operate_door" -> operateDoor(npc, player, castle, st);
			case "additional_functions" -> (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) ? "castletdecomanage.html" : "chamberlain-21.html";
			case "recovery" -> recovery(npc, player, castle);
			case "other" -> other(npc, player, castle);
			case "HP" -> hp(npc, player, castle, st);
			case "MP" -> mp(npc, player, castle, st);
			case "XP" -> xp(npc, player, castle, st);
			case "TP" -> tp(npc, player, castle, st);
			case "BF" -> bf(npc, player, castle, st);
			case "set_func" -> setFunc(npc, player, castle, st);
			case "functions" -> functions(npc, player, castle);
			case "teleport" -> teleport(npc, player, castle);
			case "goto" -> teleportTo(npc, player, castle, st); // goto listId locId
			case "buffer" -> buffer(npc, player, castle);
			case "cast_buff" -> castBuff(npc, player, castle, st);
			case "list_siege_clans" -> listSiegeClans(npc, player, castle);
			case "products" -> products(npc, player);
			case "buy" -> buy(npc, player, st);
			case "give_cloak" -> giveCloak(npc, player, castle, isMyLord);
			case "give_crown" -> giveCrown(npc, player, castle, isMyLord);
			default -> null;
		};
	}

	private String teleportTo(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmlText;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS) && (st.countTokens() >= 2)) {
			htmlText = doCastleTeleport(npc, player, castle, st);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String doCastleTeleport(Npc npc, Player player, Castle castle, StringTokenizer st) {
		var func = castle.getCastleFunction(Castle.FUNC_TELEPORT);
		if (func == null) {
			return CASTLEFUNCDISABLED_HTML;
		}

		final String listId = st.nextToken();
		final int funcLvl = (listId.length() >= 4) ? CommonUtil.parseInt(listId.substring(3), -1) : -1;
		if (func.getLevel() == funcLvl) {
			final TeleportHolder holder = TeleportersData.getInstance().getHolder(npc.getId(), listId);
			if (holder != null) {
				holder.doTeleport(player, npc, parseNextInt(st, -1));
			}
		}
		return null;
	}

	private String giveCrown(Npc npc, Player player, Castle castle, boolean isMyLord) {
		String htmlText = null;
		if (castle.getSiege().isInProgress()) {
			htmlText = CHAMBERLAIN_08_HTML;
		} else if (isMyLord) {
			if (hasQuestItems(player, CROWN)) {
				htmlText = "chamberlain-24.html";
			} else {
				final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-25.html");
				html.replace("%owner_name%", player.getName());
				html.replace("%feud_name%", String.valueOf(1001000 + castle.getId()));
				player.sendPacket(html);
				giveItems(player, CROWN, 1);
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String giveCloak(Npc npc, Player player, Castle castle, boolean isMyLord) {
		String htmlText = null;
		if (castle.getSiege().isInProgress()) {
			htmlText = CHAMBERLAIN_08_HTML;
		} else if (isMyLord) {
			final int cloakId = npc.getCastle().getSide() == CastleSide.DARK ? LORD_CLOAK_OF_DARK : LORD_CLOAK_OF_LIGHT;

			if (hasQuestItems(player, cloakId)) {
				htmlText = "chamberlain-03.html";
			} else {
				giveItems(player, cloakId, 1);
			}
		} else {
			htmlText = "chamberlain-29.html";
		}
		return htmlText;
	}

	private String buy(Npc npc, Player player, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
			((Merchant) npc).showBuyWindow(player, Integer.parseInt(st.nextToken()));
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String products(Npc npc, Player player) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-22.html");
			html.replace("%npcId%", Integer.toString(npc.getId()));
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String listSiegeClans(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE)) {
			castle.getSiege().listRegisterClan(player);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String castBuff(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
			if (castle.getCastleFunction(Castle.FUNC_SUPPORT) == null) {
				htmlText = CASTLEFUNCDISABLED_HTML;
			} else {
				final int index = Integer.parseInt(st.nextToken());
				if (BUFFS.length > index) {
					final NpcHtmlMessage html;
					final SkillHolder holder = BUFFS[index];
					if (holder.getSkill().getMpConsume() < npc.getCurrentMp()) {
						npc.setTarget(player);
						npc.doCast(holder.getSkill());
						html = getHtmlPacket(player, npc, "castleafterbuff.html");
					} else {
						html = getHtmlPacket(player, npc, "castlenotenoughmp.html");
					}

					html.replace("%MPLeft%", Integer.toString((int) npc.getCurrentMp()));
					player.sendPacket(html);
				}
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String buffer(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
			if (castle.getCastleFunction(Castle.FUNC_SUPPORT) == null) {
				htmlText = CASTLEFUNCDISABLED_HTML;
			} else {
				final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlebuff-0" + castle.getCastleFunction(Castle.FUNC_SUPPORT).getLevel() + ".html");
				html.replace("%MPLeft%", Integer.toString((int) npc.getCurrentMp()));
				player.sendPacket(html);
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String teleport(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (!isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
			htmlText = "chamberlain-21.html";
		} else if (castle.getCastleFunction(Castle.FUNC_TELEPORT) == null) {
			htmlText = CASTLEFUNCDISABLED_HTML;
		} else {
			final String listName = "tel" + castle.getCastleFunction(Castle.FUNC_TELEPORT).getLevel();
			final TeleportHolder holder = TeleportersData.getInstance().getHolder(npc.getId(), listName);
			if (holder != null) {
				holder.showTeleportList(player, npc, "Quest CastleChamberlain goto");
			}
		}
		return htmlText;
	}

	private String functions(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
			var HP = castle.getCastleFunction(Castle.FUNC_RESTORE_HP);
			var MP = castle.getCastleFunction(Castle.FUNC_RESTORE_MP);
			var XP = castle.getCastleFunction(Castle.FUNC_RESTORE_EXP);
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "castledecofunction.html");
			html.replace("%HPDepth%", (HP == null) ? "0" : Integer.toString(HP.getLevel()));
			html.replace("%MPDepth%", (MP == null) ? "0" : Integer.toString(MP.getLevel()));
			html.replace("%XPDepth%", (XP == null) ? "0" : Integer.toString(XP.getLevel()));
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String setFunc(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			final int func = Integer.parseInt(st.nextToken());
			final int level = Integer.parseInt(st.nextToken());
			if (level == 0) {
				castle.updateFunctions(player, func, level, 0, 0, false);
			} else if (!castle.updateFunctions(player, func, level, getFunctionFee(func, level), getFunctionRatio(func), castle.getCastleFunction(func) == null)) {
				htmlText = CHAMBERLAIN_09_HTML;
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String bf(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmltext;
		final int level = Integer.parseInt(st.nextToken());
		htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_SUPPORT, level);
		return htmltext;
	}

	private String tp(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmltext;
		final int level = Integer.parseInt(st.nextToken());
		htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_TELEPORT, level);
		return htmltext;
	}

	private String xp(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmltext;
		final int level = Integer.parseInt(st.nextToken());
		htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_RESTORE_EXP, level);
		return htmltext;
	}

	private String mp(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmltext;
		final int level = Integer.parseInt(st.nextToken());
		htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_RESTORE_MP, level);
		return htmltext;
	}

	private String hp(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmltext;
		final int level = Integer.parseInt(st.nextToken());
		htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_RESTORE_HP, level);
		return htmltext;
	}

	private String other(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "castledeco-AE01.html");
			funcReplace(castle, html, Castle.FUNC_TELEPORT, "TP");
			funcReplace(castle, html, Castle.FUNC_SUPPORT, "BF");
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String recovery(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "castledeco-AR01.html");
			funcReplace(castle, html, Castle.FUNC_RESTORE_HP, "HP");
			funcReplace(castle, html, Castle.FUNC_RESTORE_MP, "MP");
			funcReplace(castle, html, Castle.FUNC_RESTORE_EXP, "XP");
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String operateDoor(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmltext;
		if (!isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_OPEN_DOOR)) {
			htmltext = "chamberlain-21.html";
		} else if (castle.getSiege().isInProgress()) {
			htmltext = CHAMBERLAIN_08_HTML;
		} else {
			final boolean open = (Integer.parseInt(st.nextToken()) == 1);
			while (st.hasMoreTokens()) {
				castle.openCloseDoor(player, Integer.parseInt(st.nextToken()), open);
			}
			htmltext = (open ? "chamberlain-05.html" : "chamberlain-06.html");
		}
		return htmltext;
	}

	private String doors(Npc npc, Player player, Castle castle) {
		String htmltext;
		if (!isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_OPEN_DOOR)) {
			htmltext = "chamberlain-21.html";
		} else if (castle.getSiege().isInProgress()) {
			htmltext = CHAMBERLAIN_08_HTML;
		} else {
			htmltext = npc.getCastle().getName() + "-d.html";
		}
		return htmltext;
	}

	private String banishForeigner(Npc npc, Player player, Castle castle) {
		String htmltext;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_DISMISS)) {
			if (castle.getSiege().isInProgress()) {
				htmltext = CHAMBERLAIN_08_HTML;
			} else {
				castle.banishForeigners();
				htmltext = "chamberlain-11.html";
			}
		} else {
			htmltext = "chamberlain-21.html";
		}
		return htmltext;
	}

	private String banishForeignerShow(Npc npc, Player player, Castle castle) {
		String htmltext;
		if (!isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_DISMISS)) {
			htmltext = "chamberlain-21.html";
		} else if (castle.getSiege().isInProgress()) {
			htmltext = CHAMBERLAIN_08_HTML;
		} else {
			htmltext = "chamberlain-10.html";
		}
		return htmltext;
	}

	private String manageFunctions(Npc npc, Player player, Castle castle) {
		String htmltext;
		if (!isOwner(player, npc)) {
			htmltext = "chamberlain-21.html";
		} else if (castle.getSiege().isInProgress()) {
			htmltext = CHAMBERLAIN_08_HTML;
		} else {
			htmltext = "chamberlain-23.html";
		}
		return htmltext;
	}

	private String withdraw(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
			final long amount = (st.hasMoreTokens()) ? Long.parseLong(st.nextToken()) : 0;
			if (amount <= castle.getTreasury()) {
				castle.addToTreasuryNoTax((-1) * amount);
				giveAdena(player, amount, false);
				htmlText = CHAMBERLAIN_01_HTML;
			} else {
				final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlenotenoughbalance.html");
				html.replace("%tax_income%", GameUtils.formatAdena(castle.getTreasury()));
				html.replace("%withdraw_amount%", GameUtils.formatAdena(amount));
				player.sendPacket(html);
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String deposit(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmltext;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
			final long amount = (st.hasMoreTokens()) ? Long.parseLong(st.nextToken()) : 0;
			if ((amount > 0) && (amount < CharacterSettings.maxAdena())) {
				if (player.getAdena() >= amount) {
					takeItems(player, CommonItem.ADENA, amount);
					castle.addToTreasuryNoTax(amount);
				} else {
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
				}
			}
			htmltext = CHAMBERLAIN_01_HTML;
		} else {
			htmltext = "chamberlain-21.html";
		}
		return htmltext;
	}

	private String manageVaultWithdraw(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlemanagevault_withdraw.html");
			html.replace("%tax_income%", GameUtils.formatAdena(castle.getTreasury()));
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String manageVaultDeposit(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlemanagevault_deposit.html");
			html.replace("%tax_income%", GameUtils.formatAdena(castle.getTreasury()));
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String manageVault(Npc npc, Player player, Castle castle) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlemanagevault.html");
			html.replace("%tax_income%", GameUtils.formatAdena(castle.getTreasury()));
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String receiveReport(Npc npc, Player player, Castle castle, boolean isMyLord) {
		String htmlText = null;
		if (isMyLord) {
			if (castle.getSiege().isInProgress()) {
				htmlText = "chamberlain-07.html";
			} else {
				final Clan clan = ClanEngine.getInstance().getClan(castle.getOwnerId());
				final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-02.html");
				html.replace("%clanleadername%", clan.getLeaderName());
				html.replace("%clanname%", clan.getName());
				html.replace("%castlename%", String.valueOf(1001000 + castle.getId()));
				player.sendPacket(html);
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String upgradeTrapConfirm(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			if (castle.getSiege().isInProgress()) {
				htmlText = CHAMBERLAIN_08_HTML;
			} else {
				final int trapIndex = Integer.parseInt(st.nextToken());
				final int level = Integer.parseInt(st.nextToken());
				final int price = getTrapUpgradePrice(level);
				final int currentLevel = castle.getTrapUpgradeLevel(trapIndex);

				if (currentLevel >= level) {
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-19.html");
					html.replace("%dmglevel%", Integer.toString(currentLevel));
					player.sendPacket(html);
				} else if (player.getAdena() >= price) {
					takeItems(player, CommonItem.ADENA, price);
					castle.setTrapUpgrade(trapIndex, level, true);
					htmlText = "chamberlain-20.html";
				} else {
					htmlText = CHAMBERLAIN_09_HTML;
				}
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String upgradeTrap(Npc npc, Player player, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			final String trapIndex = st.nextToken();
			final int level = Integer.parseInt(st.nextToken());
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-18.html");
			html.replace("%trapIndex%", trapIndex);
			html.replace("%level%", Integer.toString(level));
			html.replace("%dmgzone_price%", Integer.toString(getTrapUpgradePrice(level)));
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String manageTrap(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			if (st.hasMoreTokens()) {
				final NpcHtmlMessage html;
				if (castle.getName().equalsIgnoreCase("aden")) {
					html = getHtmlPacket(player, npc, "chamberlain-17a.html");
				} else {
					html = getHtmlPacket(player, npc, "chamberlain-17.html");
				}
				html.replace("%trapIndex%", st.nextToken());
				player.sendPacket(html);
			} else {
				htmlText = npc.getCastle().getName() + "-tu.html";
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String upgradeDoorsConfirm(Npc npc, Player player, Castle castle, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			if (castle.getSiege().isInProgress()) {
				htmlText = CHAMBERLAIN_08_HTML;
			} else {
				htmlText = upgradeDoor(npc, player, castle, st);
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String upgradeDoor(Npc npc, Player player, Castle castle, StringTokenizer st) {
		final int type = Integer.parseInt(st.nextToken());
		final int level = Integer.parseInt(st.nextToken());
		final int price = getDoorUpgradePrice(type, level);
		final int[] doors = new int[2];
		for (int i = 0; i <= st.countTokens(); i++) {
			doors[i] = Integer.parseInt(st.nextToken());
		}

		final Door door = castle.getDoor(doors[0]);
		if(door == null) {
			return null;
		}

		String htmlText = null;
		final int currentLevel = door.getStats().getUpgradeHpRatio();
		if (currentLevel >= level) {
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-15.html");
			html.replace("%doorlevel%", Integer.toString(currentLevel));
			player.sendPacket(html);
		} else if (player.getAdena() >= price) {
			takeItems(player, CommonItem.ADENA, price);
			for (int doorId : doors) {
				castle.setDoorUpgrade(doorId, level, true);
			}
			htmlText = "chamberlain-16.html";
		} else {
			htmlText = CHAMBERLAIN_09_HTML;
		}
		return htmlText;
	}

	private String upgradeDoors(String event, Npc npc, Player player, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			final int type = Integer.parseInt(st.nextToken());
			final int level = Integer.parseInt(st.nextToken());
			final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-14.html");
			html.replace("%gate_price%", Integer.toString(getDoorUpgradePrice(type, level)));
			html.replace("%event%", event.substring("upgrade_doors".length() + 1));
			player.sendPacket(html);
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String manageDoors(Npc npc, Player player, StringTokenizer st) {
		String htmlText = null;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			if (st.hasMoreTokens()) {
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-13.html");
				html.replace("%type%", st.nextToken());
				while (st.hasMoreTokens()) {
					sb.append(" ").append(st.nextToken());
				}
				html.replace("%doors%", sb.toString());
				player.sendPacket(html);
			} else {
				htmlText = npc.getCastle().getName() + "-du.html";
			}
		} else {
			htmlText = "chamberlain-21.html";
		}
		return htmlText;
	}

	private String siegeFunctions(Npc npc, Player player, Castle castle) {
		String htmltext;
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
			if (castle.getSiege().isInProgress()) {
				htmltext = CHAMBERLAIN_08_HTML;
			}
			// else if (!isDomainFortressInContractStatus(castle.getResidenceId()))
			// {
			// htmltext = "chamberlain-27.html";
			// }
			else {
				htmltext = "chamberlain-12.html";
			}
		} else {
			htmltext = "chamberlain-21.html";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return (isOwner(player, npc)) ? CHAMBERLAIN_01_HTML : "chamberlain-04.html";
	}
	
	public static AbstractNpcAI provider()
	{
		return new CastleChamberlain();
	}
}
