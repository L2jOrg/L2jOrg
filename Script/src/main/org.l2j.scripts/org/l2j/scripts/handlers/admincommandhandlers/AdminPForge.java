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
package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.AdminForgePacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * This class handles commands for gm to forge packets
 * @author Maktakien, HorridoJoho
 */
public final class AdminPForge implements IAdminCommandHandler
{

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminPForge.class);

	private static final String[] ADMIN_COMMANDS =
	{
		"admin_forge",
		"admin_forge_values",
		"admin_forge_send"
	};
	
	private String[] getOpCodes(StringTokenizer st)
	{
		Collection<String> opCodes = null;
		while (st.hasMoreTokens())
		{
			final String token = st.nextToken();
			if (";".equals(token))
			{
				break;
			}
			
			if (opCodes == null)
			{
				opCodes = new LinkedList<>();
			}
			opCodes.add(token);
		}
		
		if (opCodes == null)
		{
			return null;
		}
		
		return opCodes.toArray(new String[0]);
	}
	
	private boolean isInvalidOpcodes(String[] opCodes) {
		if (opCodes == null || opCodes.length == 0 || opCodes.length > 3) {
			return true;
		}

		long[] maxValues = {255, 65535, 4294967295L};
		for (int i = 0; i < opCodes.length; ++i) {
			final String opCode = opCodes[i];
			long opCodeLong;
			try {
				opCodeLong = Long.decode(opCode);
			}
			catch (Exception e) {
				return i <= 0;
			}
			
			if (opCodeLong < 0 || opCodeLong > maxValues[i]) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hasInvalidFormat(String format) {
		var upper = 1 << 5;
		char[] validFormats = new char[] {
			'b', 'c', 'd', 'f', 'h', 'q', 's', 'x'
		};
		for (int i = 0; i < format.length(); ++i) {
			var digit = format.charAt(i);
			if(Arrays.binarySearch(validFormats, (char) (digit | upper)) < 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean validateMethod(String method) {
		return switch (method) {
			case "sc", "sb", "cs" -> true;
			default -> false;
		};
	}
	
	private void showValuesUsage(Player activeChar)
	{
		BuilderUtil.sendSysMessage(activeChar, "Usage: //forge_values opcode1[ opcode2[ opcode3]] ;[ format]");
		showMainPage(activeChar);
	}
	
	private void showSendUsage(Player activeChar) {
		BuilderUtil.sendSysMessage(activeChar, "Usage: //forge_send sc|sb|cs opcode1[;opcode2[;opcode3]][ format value1 ... valueN] ");
		showMainPage(activeChar);
	}
	
	private void showMainPage(Player activeChar)
	{
		AdminHtml.showAdminHtml(activeChar, "pforge/main.htm");
	}
	
	private void showValuesPage(Player activeChar, String[] opCodes, String format)
	{
		StringBuilder sendBypass;
		String valuesHtml = HtmCache.getInstance().getHtmForce(activeChar, "data/html/admin/pforge/values.htm");
		if (opCodes.length == 3)
		{
			valuesHtml = valuesHtml.replace("%opformat%", "chd");
			sendBypass = new StringBuilder(opCodes[0] + ";" + opCodes[1] + ";" + opCodes[2]);
		}
		else if (opCodes.length == 2)
		{
			valuesHtml = valuesHtml.replace("%opformat%", "ch");
			sendBypass = new StringBuilder(opCodes[0] + ";" + opCodes[1]);
		}
		else
		{
			valuesHtml = valuesHtml.replace("%opformat%", "c");
			sendBypass = new StringBuilder(opCodes[0]);
		}
		
		valuesHtml = valuesHtml.replace("%opcodes%", sendBypass.toString());
		
		StringBuilder editorsHtml = new StringBuilder();
		
		if (format == null)
		{
			valuesHtml = valuesHtml.replace("%format%", "");
			editorsHtml = new StringBuilder();
		}
		else
		{
			valuesHtml = valuesHtml.replace("%format%", format);
			sendBypass.append(" ").append(format);
			
			final String editorTemplate = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/pforge/inc/editor.htm");
			
			if (editorTemplate != null)
			{
				final StringBuilder singleCharSequence = new StringBuilder(1);
				singleCharSequence.append(' ');
				
				for (int chIdx = 0; chIdx < format.length(); ++chIdx)
				{
					final char ch = format.charAt(chIdx);
					singleCharSequence.setCharAt(0, ch);
					editorsHtml.append(editorTemplate.replace("%format%", singleCharSequence).replace("%editor_index%", String.valueOf(chIdx)));
					sendBypass.append(" $v").append(chIdx);
				}
			}
			else
			{
				editorsHtml = new StringBuilder();
			}
		}
		
		valuesHtml = valuesHtml.replace("%editors%", editorsHtml.toString());
		valuesHtml = valuesHtml.replace("%send_bypass%", sendBypass.toString());
		activeChar.sendPacket(new NpcHtmlMessage(0, 1, valuesHtml));
	}
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar) {
		if (command.equals("admin_forge")) {
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_forge_values ")) {
			return showValues(command, activeChar);
		}
		else if (command.startsWith("admin_forge_send ")) {
			return showSend(command, activeChar);
		}
		return true;
	}

	private boolean showSend(String command, Player activeChar) {
		try {
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // skip command token

			if (!st.hasMoreTokens()) {
				showSendUsage(activeChar);
				return false;
			}

			final String method = st.nextToken();
			if (!validateMethod(method)) {
				BuilderUtil.sendSysMessage(activeChar, "Invalid method!");
				showSendUsage(activeChar);
				return false;
			}

			final String[] opCodes = st.nextToken().split(";");
			if (isInvalidOpcodes(opCodes)) {
				BuilderUtil.sendSysMessage(activeChar, "Invalid op codes!");
				showSendUsage(activeChar);
				return false;
			}

			String format = null;
			if (st.hasMoreTokens()) {
				format = st.nextToken();
				if (hasInvalidFormat(format)) {
					BuilderUtil.sendSysMessage(activeChar, "Format invalid!");
					showSendUsage(activeChar);
					return false;
				}
			}

			if (!sendForgePacket(activeChar, st, method, opCodes, format)) {
				return false;
			}

			showValuesPage(activeChar, opCodes, format);
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			showSendUsage(activeChar);
			return false;
		}
		return true;
	}

	private boolean sendForgePacket(Player activeChar, StringTokenizer st, String method, String[] opCodes, String format) {
		AdminForgePacket afp = new AdminForgePacket();
		for (int i = 0; i < opCodes.length; ++i) {
			char type = packetType(i);
			afp.addPart((byte) type, opCodes[i]);
		}

		if (!addPacketContent(activeChar, st, format, afp)) {
			return false;
		}

		if (method.equals("sc")) {
			activeChar.sendPacket(afp);
		} else if (method.equals("sb")) {
			activeChar.broadcastPacket(afp);
		}
		return true;
	}

	private boolean addPacketContent(Player activeChar, StringTokenizer st, String format, AdminForgePacket afp) {
		if(format == null) {
			return true;
		}

		for (int i = 0; i < format.length(); ++i) {
			if (!st.hasMoreTokens()) {
				BuilderUtil.sendSysMessage(activeChar, "Not enough values!");
				showSendUsage(activeChar);
				return false;
			}

			String value = contentValue(activeChar, st);
			afp.addPart((byte) format.charAt(i), value);
		}
		return true;
	}

	private String contentValue(Player activeChar, StringTokenizer st) {
		WorldObject target = activeChar.getTarget();
		String value = st.nextToken();
		return switch (value) {
			case "$oid" -> String.valueOf(activeChar.getObjectId());
			case "$title" ->  activeChar.getTitle();
			case "$name" ->  activeChar.getName();
			case "$x" ->  String.valueOf(activeChar.getX());
			case "$y" ->  String.valueOf(activeChar.getY());
			case "$z" ->  String.valueOf(activeChar.getZ());
			case "$heading" ->  String.valueOf(activeChar.getHeading());
			case "$toid" ->  String.valueOf(activeChar.getTargetId());
			case "$ttitle" ->  targetTitle(target);
			case "$tname" ->  targetName(target);
			case "$tx" ->  targetX(target);
			case "$ty" ->  targetY(target);
			case "$tz" ->  targetZ(target);
			case "$theading" -> targetHeading(target);
			default -> value;
		};
	}

	private String targetHeading(WorldObject target) {
		String value;
		if (target != null) {
			value = String.valueOf(target.getHeading());
		} else {
			value = "0";
		}
		return value;
	}

	private String targetZ(WorldObject target) {
		String value;
		if (target != null) {
			value = String.valueOf(target.getZ());
		} else {
			value = "0";
		}
		return value;
	}

	private String targetY(WorldObject target) {
		String value;
		if (target != null) {
			value = String.valueOf(target.getY());
		} else {
			value = "0";
		}
		return value;
	}

	private String targetX(WorldObject target) {
		String value;
		if (target != null) {
			value = String.valueOf(target.getX());
		} else {
			value = "0";
		}
		return value;
	}

	private String targetName(WorldObject target) {
		String value;
		if (target != null) {
			value = target.getName();
		} else {
			value = "";
		}
		return value;
	}

	private String targetTitle(WorldObject target) {
		String value;
		if (isCreature(target)) {
			value = ((Creature) target).getTitle();
		} else {
			value = "";
		}
		return value;
	}

	private char packetType(int i) {
		char type;
		if (i == 0) {
			type = 'c';
		} else if (i == 1) {
			type = 'h';
		} else {
			type = 'd';
		}
		return type;
	}

	private boolean showValues(String command, Player activeChar) {
		try {
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // skip command token

			if (!st.hasMoreTokens()) {
				showValuesUsage(activeChar);
				return false;
			}

			final String[] opCodes = getOpCodes(st);
			if (isInvalidOpcodes(opCodes)) {
				BuilderUtil.sendSysMessage(activeChar, "Invalid op codes!");
				showValuesUsage(activeChar);
				return false;
			}

			String format = null;
			if (st.hasMoreTokens()) {
				format = st.nextToken();
				if (hasInvalidFormat(format)) {
					BuilderUtil.sendSysMessage(activeChar, "Format invalid!");
					showValuesUsage(activeChar);
					return false;
				}
			}

			showValuesPage(activeChar, opCodes, format);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			showValuesUsage(activeChar);
			return false;
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
