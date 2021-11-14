/*
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

import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.ClassInfo;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.settings.PvpSettings;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.SystemMessageId.YOUR_TITLE_HAS_BEEN_CHANGED;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * EditChar admin command implementation.
 */
public class AdminEditChar implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminEditChar.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_edit_character",
		"admin_current_player",
		"admin_setreputation", // sets reputation of target char to any amount. //setreputation <amout>
		"admin_nokarma", // sets reputation to 0 if its negative.
		"admin_setfame", // sets fame of target char to any amount. //setfame <fame>
		"admin_character_list", // same as character_info, kept for compatibility purposes
		"admin_character_info", // given a player name, displays an information window
		"admin_show_characters", // list of characters
		"admin_find_character", // find a player by his name or a part of it (case-insensitive)
		"admin_find_ip", // find all the player connections from a given IPv4 number
		"admin_find_account", // list all the characters from an account (useful for GMs w/o DB access)
		"admin_find_dualbox", // list all the IPs with more than 1 char logged in (dualbox)
		"admin_strict_find_dualbox",
		"admin_tracert",
		"admin_rec", // gives recommendation points
		"admin_settitle", // changes char title
		"admin_changename", // changes char name
		"admin_setsex", // changes characters' sex
		"admin_setcolor", // change charnames' color display
		"admin_settcolor", // change char title color
		"admin_setclass", // changes chars' classId
		"admin_setpk", // changes PK count
		"admin_setpvp", // changes PVP count
		"admin_set_pvp_flag",
		"admin_fullfood", // fulfills a pet's food bar
		"admin_remove_clan_penalty", // removes clan penalties
		"admin_summon_info", // displays an information window about target summon
		"admin_unsummon",
		"admin_summon_setlvl",
		"admin_show_pet_inv",
		"admin_partyinfo",
		"admin_set_hp",
		"admin_set_mp",
		"admin_set_cp",
		"admin_setparam",
		"admin_unsetparam"
	};
	public static final String SYNTAX_SETPARAM_STAT_VALUE = "Syntax: //setparam <stat> <value>";

	@Override
	public boolean useAdminCommand(String command, Player player) {
		return infoCmd(player, command) || editCmd(player, command);
	}

	private boolean editCmd(Player player, String command) {
		var result = false;
		if (command.startsWith("admin_settitle"))
		{
			return setTitle(command, player);
		}
		else if (command.startsWith("admin_changename"))
		{
			return changeName(command, player);
		}
		else if (command.startsWith("admin_setsex"))
		{
			return setSex(player);
		}
		else if (command.startsWith("admin_setcolor"))
		{
			return setColor(command, player);
		}
		else if (command.startsWith("admin_settcolor"))
		{
			return setTitleColor(command, player);
		}
		else if (command.startsWith("admin_remove_clan_penalty")) {
			return removeClanPenalty(command, player);
		}
		else if (command.startsWith("admin_set_pvp_flag"))
		{
			return setPvPFlag(player);
		}
		else if (command.startsWith("admin_fullfood"))
		{
			fullFood(player);
			result = true;
		}
		else if (command.startsWith("admin_unsummon"))
		{
			unSummon(player);
			result = true;
		} else if(cmdChangeStats(player, command)) {
			result = true;
		}
		return result;
	}

	private boolean cmdChangeStats(Player player, String command) {
		var result = false;
		if (command.startsWith("admin_setreputation")) {
			setReputation(command, player);
			result = true;
		}
		else if (command.startsWith("admin_nokarma")) {
			noKarma(player);
			result = true;
		}
		else if (command.startsWith("admin_setpk")) {
			setPK(command, player);
			result = true;
		}
		else if (command.startsWith("admin_setpvp")) {
			setPvP(command, player);
			result = true;
		}
		else if (command.startsWith("admin_setfame")) {
			setFame(command, player);
			result = true;
		}
		else if (command.startsWith("admin_rec")) {
			adminRec(command, player);
			result = true;
		}
		else if (command.startsWith("admin_setclass")) {
			return setClass(command, player);
		}
		else if (command.startsWith("admin_summon_setlvl"))
		{
			summonSetLevel(command, player);
			result = true;
		}
		else if (command.startsWith("admin_set_hp"))
		{
			return setHp(command, player);
		}
		else if (command.startsWith("admin_set_mp"))
		{
			return setMp(command, player);
		}
		else if (command.startsWith("admin_set_cp"))
		{
			return setCp(command, player);
		}
		else if (command.startsWith("admin_setparam"))
		{
			return setParam(command, player);
		}
		else if (command.startsWith("admin_unsetparam"))
		{
			return unsetParam(command, player);
		}
		return  result;
	}

	private boolean infoCmd(Player player, String command) {
		var result = false;
		if (command.equals("admin_current_player")) {
			currentPlayer(player, player);
			result = true;
		}
		else if (command.startsWith("admin_character_info")) {
			characterInfo(command, player);
			result = true;
		}
		else if (command.startsWith("admin_character_list")) {
			listCharacters(player, 0);
			result = true;
		}
		else if (command.startsWith("admin_show_characters")) {
			showCharacters(command, player);
			result = true;
		}
		else if (command.startsWith("admin_find_character")) {
			findCharacter(command, player);
			result = true;
		}
		else if (command.startsWith("admin_edit_character")) {
			editCharacter(command, player);
			result = true;
		} else if (command.startsWith("admin_find_ip")) {
			findIP(command, player);
			result = true;
		}
		else if (command.startsWith("admin_find_account")) {
			findAccount(command, player);
			result = true;
		}
		else if (command.startsWith("admin_find_dualbox")) {
			result = findDualBox(command, player);
		}
		else if (command.startsWith("admin_strict_find_dualbox")) {
			strictFindDualBox(command, player);
			result = true;
		}
		else if (command.startsWith("admin_tracert")) {
			tracert(command, player);
			result = true;
		}
		else if (command.startsWith("admin_summon_info")) {
			summonInfo(player);
			result = true;
		}
		else if (command.startsWith("admin_show_pet_inv")) {
			showPetInventory(command, player);
			result = true;
		}
		else if (command.startsWith("admin_partyinfo")) {
			partyInfo(command, player);
			result = true;
		}
		return result;
	}

	private boolean unsetParam(String command, Player player) {
		final WorldObject target = player.getTarget();
		if (!isCreature(target)) {
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken(); // admin_setparam
		if (!st.hasMoreTokens()) {
			BuilderUtil.sendSysMessage(player, "Syntax: //unsetparam <stat>");
			return false;
		}
		final String statName = st.nextToken();

		try {
			Stat stat = Stat.valueOf(statName);
			final Creature targetCreature = (Creature) target;
			targetCreature.getStats().removeFixedValue(stat);
			targetCreature.getStats().recalculateStats(true);
			BuilderUtil.sendSysMessage(player, "Fixed stat: " + stat + " has been removed.");
		} catch (Exception e) {
			BuilderUtil.sendSysMessage(player, "Couldn't find such stat!");
			return false;
		}
		return true;
	}

	private boolean setParam(String command, Player player) {
		final WorldObject target = player.getTarget();
		if (!isCreature(target))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken(); // admin_setparam
		if (!st.hasMoreTokens())
		{
			BuilderUtil.sendSysMessage(player, SYNTAX_SETPARAM_STAT_VALUE);
			return false;
		}
		final String statName = st.nextToken();
		if (!st.hasMoreTokens())
		{
			BuilderUtil.sendSysMessage(player, SYNTAX_SETPARAM_STAT_VALUE);
			return false;
		}

		try {
			Stat stat = Stat.valueOf(statName);

			final double value = Double.parseDouble(st.nextToken());
			final Creature targetCreature = (Creature) target;
			if (value >= 0)
			{
				targetCreature.getStats().addFixedValue(stat, value);
				targetCreature.getStats().recalculateStats(true);
				BuilderUtil.sendSysMessage(player, "Fixed stat: " + stat + " has been set to " + value);
			}
			else
			{
				BuilderUtil.sendSysMessage(player, "Non negative values are only allowed!");
			}
		}
		catch (Exception e) {
			BuilderUtil.sendSysMessage(player, "Couldn't find such stat!");
			BuilderUtil.sendSysMessage(player, SYNTAX_SETPARAM_STAT_VALUE);
			return false;
		}
		return true;
	}

	private boolean setPvPFlag(Player player) {
		try
		{
			final WorldObject target = player.getTarget();
			if (!isPlayable(target))
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}
			final Playable playable = ((Playable) target);
			playable.updatePvPFlag(Math.abs(playable.getPvpFlag() - 1));
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(player, "Usage: //set_pvp_flag");
		}
		return true;
	}

	private boolean setCp(String command, Player player) {
		final String[] data = command.split(" ");
		try
		{
			final WorldObject target = player.getTarget();
			if (!isCreature(target))
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}
			((Creature) target).setCurrentCp(Double.parseDouble(data[1]));
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(player, "Usage: //set_cp 1000");
		}
		return true;
	}

	private boolean setMp(String command, Player player) {
		final String[] data = command.split(" ");
		try
		{
			final WorldObject target = player.getTarget();
			if (!isCreature(target))
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}
			((Creature) target).setCurrentMp(Double.parseDouble(data[1]));
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(player, "Usage: //set_mp 1000");
		}
		return true;
	}

	private boolean setHp(String command, Player player) {
		final String[] data = command.split(" ");
		try
		{
			final WorldObject target = player.getTarget();
			if (!isCreature(target))
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}
			((Creature) target).setCurrentHp(Double.parseDouble(data[1]));
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(player, "Usage: //set_hp 1000");
		}
		return true;
	}

	private void partyInfo(String command, Player player) {
		WorldObject target;
		try
		{
			final String val = command.substring(16);
			target = World.getInstance().findPlayer(val);
			if (target == null)
			{
				target = player.getTarget();
			}
		}
		catch (Exception e)
		{
			target = player.getTarget();
		}

		if (isPlayer(target))
		{
			if (((Player) target).isInParty())
			{
				gatherPartyInfo((Player) target, player);
			}
			else
			{
				BuilderUtil.sendSysMessage(player, "Not in party.");
			}
		}
		else
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
		}
	}

	private void showPetInventory(String command, Player player) {
		WorldObject target;
		try
		{
			final String val = command.substring(19);
			final int objId = Integer.parseInt(val);
			target = World.getInstance().findPet(objId);
		}
		catch (Exception e)
		{
			target = player.getTarget();
		}

		if (isPet(target))
		{
			player.sendPacket(new GMViewItemList(1, (Pet) target));
		}
		else
		{
			BuilderUtil.sendSysMessage(player, "Usable only with Pets");
		}
	}

	private void summonSetLevel(String command, Player player) {
		final WorldObject target = player.getTarget();
		if (isPet(target)) {
			final Pet pet = (Pet) target;
			try {
				final String val = command.substring(20);
				final int level = Integer.parseInt(val);
				final long oldexp = pet.getStats().getExp();
				final long newexp = pet.getStats().getExpForLevel(level);
				if (oldexp > newexp) {
					pet.getStats().removeExp(oldexp - newexp);
				} else if (oldexp < newexp) {
					pet.getStats().addExp(newexp - oldexp);
				}
			} catch (Exception e) {
				LOGGER.warn(e.getMessage(), e);
			}
		} else {
			BuilderUtil.sendSysMessage(player, "Usable only with Pets");
		}
	}

	private void unSummon(Player player) {
		final WorldObject target = player.getTarget();
		if (isSummon(target))
		{
			((Summon) target).unSummon(((Summon) target).getOwner());
		}
		else
		{
			BuilderUtil.sendSysMessage(player, "Usable only with Pets/Summons");
		}
	}

	private void summonInfo(Player player) {
		final WorldObject target = player.getTarget();
		if (isSummon(target))
		{
			gatherSummonInfo((Summon) target, player);
		}
		else
		{
			BuilderUtil.sendSysMessage(player, "Invalid target.");
		}
	}

	private boolean tracert(String command, Player player) {
		final String[] data = command.split(" ");
		Player pl = null;
		if ((data.length > 1))
		{
			pl = World.getInstance().findPlayer(data[1]);
		}
		else
		{
			final WorldObject target = player.getTarget();
			if (isPlayer(target))
			{
				pl = (Player) target;
			}
		}

		if (pl == null)
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}

		final GameClient client = pl.getClient();
		if (client == null)
		{
			BuilderUtil.sendSysMessage(player, "Client is null.");
			return false;
		}

		StringBuilder ip;
		final int[][] trace = client.getTrace();
		for (int i = 0; i < trace.length; i++)
		{
			ip = new StringBuilder();
			for (int o = 0; o < trace[0].length; o++)
			{
				ip.append(trace[i][o]);
				if (o != (trace[0].length - 1))
				{
					ip.append(".");
				}
			}
			BuilderUtil.sendSysMessage(player, "Hop" + i + ": " + ip);
		}
		return true;
	}

	private boolean removeClanPenalty(String command, Player player) {
		try
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			if (st.countTokens() != 3)
			{
				BuilderUtil.sendSysMessage(player, "Usage: //remove_clan_penalty join|create charname");
				return false;
			}

			st.nextToken();

			final boolean changeCreateExpiryTime = st.nextToken().equalsIgnoreCase("create");

			final String playerName = st.nextToken();
			var target = World.getInstance().findPlayer(playerName);

			if (target == null) {
				getDAO(PlayerDAO.class).removeClanPenalty(playerName);
			}
			else if (changeCreateExpiryTime) // removing penalty
			{
				target.setClanCreateExpiryTime(0);
			}
			else
			{
				target.setClanJoinExpiryTime(0);
			}

			BuilderUtil.sendSysMessage(player, "Clan penalty successfully removed to character: " + playerName);
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		return true;
	}

	private void fullFood(Player player) {
		if (player.getTarget() instanceof Pet targetPet) {
			targetPet.setCurrentFed(targetPet.getMaxFed());
			targetPet.broadcastStatusUpdate();
		} else {
			player.sendPacket(SystemMessageId.INVALID_TARGET);
		}
	}

	private boolean setTitleColor(String command, Player player) {
		try
		{
			final String val = command.substring(16);
			if(!(player.getTarget() instanceof Player target)) {
				return false;
			}

			target.getAppearance().setTitleColor(Integer.decode("0x" + val));
			target.sendMessage("Your title color has been changed by a GM");
			target.broadcastUserInfo();
		}
		catch (Exception e)
		{ // Case of empty color or invalid hex string
			BuilderUtil.sendSysMessage(player, "You need to specify a valid new color.");
		}
		return true;
	}

	private boolean setColor(String command, Player player) {
		try
		{
			final String val = command.substring(15);
			if(!(player.getTarget() instanceof Player target)) {
				return false;
			}

			target.getAppearance().setNameColor(Integer.decode("0x" + val));
			target.sendMessage("Your name color has been changed by a GM");
			target.broadcastUserInfo();
		}
		catch (Exception e)
		{ // Case of empty color or invalid hex string
			BuilderUtil.sendSysMessage(player, "You need to specify a valid new color.");
		}
		return true;
	}

	private boolean setSex(Player player) {
		if (!(player.getTarget() instanceof  Player target)) {
			return false;
		}

		target.getAppearance().setFemale(!target.getAppearance().isFemale());
		target.sendMessage("Your gender has been changed by a GM");
		target.broadcastUserInfo();
		return true;
	}

	private boolean changeName(String command, Player player) {
		try
		{
			final String val = command.substring(17);
			if(!(player.getTarget() instanceof Player target)) {
				return false;
			}

			if (PlayerNameTable.getInstance().doesCharNameExist(val))
			{
				BuilderUtil.sendSysMessage(player, "Warning, player " + val + " already exists");
				return false;
			}
			target.setName(val);
			if (GeneralSettings.cachePlayersName())
			{
				PlayerNameTable.getInstance().addName(target);
			}
			target.storeMe();

			BuilderUtil.sendSysMessage(target, "Changed name to " + val);
			target.sendMessage("Your name has been changed by a GM.");
			target.broadcastUserInfo();

			if (target.isInParty())
			{
				// Delete party window for other party members
				target.getParty().broadcastToPartyMembers(target, PartySmallWindowDeleteAll.STATIC_PACKET);
				for (Player member : target.getParty().getMembers())
				{
					// And re-add
					if (member != target)
					{
						member.sendPacket(new PartySmallWindowAll(member, target.getParty()));
					}
				}
			}
			if (target.getClan() != null)
			{
				target.getClan().broadcastClanStatus();
			}
		}
		catch (StringIndexOutOfBoundsException e)
		{ // Case of empty character name
			BuilderUtil.sendSysMessage(player, "Usage: //setname new_name_for_target");
		}
		return true;
	}

	private boolean setTitle(String command, Player player) {
		try
		{
			final String val = command.substring(15);
			if (!(player.getTarget() instanceof Player target)) {
				return false;
			}

			target.setTitle(val);
			target.sendPacket(YOUR_TITLE_HAS_BEEN_CHANGED);
			target.broadcastTitleInfo();
		}
		catch (StringIndexOutOfBoundsException e) {
			BuilderUtil.sendSysMessage(player, "You need to specify the new title.");
		}
		return true;
	}

	private boolean setClass(String command, Player player) {
		try {
			final String val = command.substring(15).trim();
			final int classidval = Integer.parseInt(val);
			if (! (player.getTarget() instanceof Player target)) {
				return false;
			}

			if (ClassId.getClassId(classidval) != null && target.getClassId().getId() != classidval) {
				target.setClassId(classidval);
				target.setBaseClass(target.getActiveClass());

				final String newclass = ClassListData.getInstance().getClass(target.getClassId()).getClassName();

				target.store(false);
				target.broadcastUserInfo();
				target.sendSkillList();
				target.sendPacket(new ExSubjobInfo(target, SubclassInfoType.CLASS_CHANGED));
				target.sendPacket(new ExUserInfoInvenWeight());
				target.sendMessage("A GM changed your class to " + newclass + ".");
				player.sendMessage(target.getName() + " is a " + newclass + ".");
			} else {
				BuilderUtil.sendSysMessage(player, "Usage: //setclass <valid_new_classid>");
			}
		} catch (StringIndexOutOfBoundsException e) {
			AdminHtml.showAdminHtml(player, "setclass/human_fighter.htm");
		} catch (NumberFormatException e) {
			BuilderUtil.sendSysMessage(player, "Usage: //setclass <valid_new_classid>");
		}
		return true;
	}

	private void adminRec(String command, Player player) {
		try {
			final String val = command.substring(10);
			final int recVal = Integer.parseInt(val);

			if (player.getTarget() instanceof  Player target) {
				target.setRecommend(recVal);
				target.broadcastUserInfo(UserInfoType.SOCIAL);
				target.sendMessage("A GM changed your Recommend points to " + recVal);
				player.sendMessage(target.getName() + "'s Recommend changed to " + recVal);
			} else {
				player.sendPacket(SystemMessageId.INVALID_TARGET);
			}
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(player, "Usage: //rec number");
		}
	}

	private void setFame(String command, Player player) {
		try {
			final String val = command.substring(14);
			final int fame = Integer.parseInt(val);

			if (player.getTarget() instanceof Player target) {
				target.setFame(fame);
				target.sendPacket(new UserInfo(target, UserInfoType.VITA_FAME));
				target.sendMessage("A GM changed your Reputation points to " + fame);
				player.sendMessage(target.getName() + "'s Fame changed to " + fame);
			} else {
				player.sendPacket(SystemMessageId.INVALID_TARGET);
			}
		} catch (Exception e) {
			LOGGER.warn("Set Fame error", e);
			BuilderUtil.sendSysMessage(player, "Usage: //setfame <new_fame_value>");
		}
	}

	private void setPvP(String command, Player player) {
		try {
			final String val = command.substring(13);
			final int pvp = Integer.parseInt(val);

			if (player.getTarget() instanceof Player target) {
				target.setPvpKills(pvp);
				if(target.updatePvpTitleAndColor(false)) {
					target.broadcastUserInfo(UserInfoType.SOCIAL, UserInfoType.COLOR, UserInfoType.CLAN);
				} else {
					target.sendPacket(new UserInfo(target, UserInfoType.SOCIAL));
				}
				target.sendMessage("A GM changed your PVP count to " + pvp);
				player.sendMessage(target.getName() + "'s PVP count changed to " + pvp);
			} else {
				player.sendPacket(SystemMessageId.INVALID_TARGET);
			}
		} catch (Exception e) {
			LOGGER.warn("Error setting pvp:", e);
			BuilderUtil.sendSysMessage(player, "Usage: //setpvp <pvp_count>");
		}
	}

	private void setPK(String command, Player player) {
		try {
			final String val = command.substring(12);
			final int pk = Integer.parseInt(val);

			if (player.getTarget() instanceof Player target) {
				target.setPkKills(pk);
				target.sendPacket(new UserInfo(target, UserInfoType.SOCIAL));
				target.sendMessage("A GM changed your PK count to " + pk);
				player.sendMessage(target.getName() + "'s PK count changed to " + pk);
			} else {
				player.sendPacket(SystemMessageId.INVALID_TARGET);
			}
		}
		catch (Exception e) {
			LOGGER.warn("Error setting PK", e);
			BuilderUtil.sendSysMessage(player, "Usage: //setpk <pk_count>");
		}
	}

	private void noKarma(Player player) {
		setTargetReputation(player, 0);
	}

	private void setReputation(String command, Player player) {
		try {
			final String val = command.substring(20);
			final int reputation = Integer.parseInt(val);
			setTargetReputation(player, reputation);
		} catch (Exception e) {
			LOGGER.warn("Error setting reputation", e);
			BuilderUtil.sendSysMessage(player, "Usage: //setreputation <new_reputation_value>");
		}
	}

	private void setTargetReputation(Player player, int newReputation) {
		if(!(player.getTarget() instanceof  Player targetPlayer)) {
			return;
		}

		newReputation = Math.min(newReputation, PvpSettings.maxReputation());

		final var oldReputation = targetPlayer.getReputation();
		if(oldReputation == newReputation) {
			return;
		}

		targetPlayer.setReputation(newReputation);
		targetPlayer.sendPacket(getSystemMessage(SystemMessageId.YOUR_REPUTATION_HAS_BEEN_CHANGED_TO_S1).addInt(newReputation));
		BuilderUtil.sendSysMessage(player, "Successfully Changed karma for " + targetPlayer + " from (" + oldReputation + ") to (" + newReputation + ").");
	}

	private void editCharacter(String command, Player player) {
		final String[] data = command.split(" ");
		if (data.length > 1) {
			editCharacter(player, data[1]);
		} else if (isPlayer(player.getTarget())) {
			editCharacter(player, null);
		} else {
			player.sendPacket(SystemMessageId.INVALID_TARGET);
		}
	}

	private void editCharacter(Player activeChar, String targetName) {
		WorldObject target;
		if (targetName != null) {
			target = World.getInstance().findPlayer(targetName);
		} else {
			target = activeChar.getTarget();
		}

		if (target instanceof Player player) {
			gatherCharacterInfo(activeChar, player, "charedit.htm");
		}
	}


	private void findAccount(String command, Player player) {
		try
		{
			final String val = command.substring(19);
			findCharactersPerAccount(player, val);
		}
		catch (Exception e)
		{ // Case of empty or malformed player name
			BuilderUtil.sendSysMessage(player, "Usage: //find_account <player_name>");
			listCharacters(player, 0);
		}
	}

	private void findIP(String command, Player player) {
		try
		{
			final String val = command.substring(14);
			findCharactersPerIp(player, val);
		}
		catch (Exception e)
		{ // Case of empty or malformed IP number
			BuilderUtil.sendSysMessage(player, "Usage: //find_ip <www.xxx.yyy.zzz>");
			listCharacters(player, 0);
		}
	}

	private void findCharacter(String command, Player player) {
		try
		{
			final String val = command.substring(21);
			findCharacter(player, val);
		}
		catch (StringIndexOutOfBoundsException e)
		{ // Case of empty character name
			BuilderUtil.sendSysMessage(player, "Usage: //find_character <character_name>");
			listCharacters(player, 0);
		}
	}

	private void findCharacter(Player activeChar, String CharacterToFind)
	{
		int CharactersFound = 0;
		String name;
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		adminReply.setFile(activeChar, "data/html/admin/charfind.htm");

		final StringBuilder replyMSG = new StringBuilder(1000);

		final List<Player> players = new ArrayList<>(World.getInstance().getPlayers());
		players.sort(Comparator.comparingLong(Player::getUptime));
		for (Player player : players)
		{ // Add player info into new Table row
			name = player.getName();
			if (name.toLowerCase().contains(CharacterToFind.toLowerCase()))
			{
				CharactersFound += 1;
				replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_info ");
				replyMSG.append(name);
				replyMSG.append("\">");
				replyMSG.append(name);
				replyMSG.append("</a></td><td width=110>");
				replyMSG.append(ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
				replyMSG.append("</td><td width=40>");
				replyMSG.append(player.getLevel());
				replyMSG.append("</td></tr>");
			}
			if (CharactersFound > 20)
			{
				break;
			}
		}
		adminReply.replace("%results%", replyMSG.toString());

		final String replyMSG2;

		if (CharactersFound == 0)
		{
			replyMSG2 = "s. Please try again.";
		}
		else if (CharactersFound > 20)
		{
			adminReply.replace("%number%", " more than 20");
			replyMSG2 = "s.<br>Please refine your search to see all of the results.";
		}
		else if (CharactersFound == 1)
		{
			replyMSG2 = ".";
		}
		else
		{
			replyMSG2 = "s.";
		}

		adminReply.replace("%number%", String.valueOf(CharactersFound));
		adminReply.replace("%end%", replyMSG2);
		activeChar.sendPacket(adminReply);
	}

	private void showCharacters(String command, Player player) {
		try
		{
			final String val = command.substring(22);
			final int page = Integer.parseInt(val);
			listCharacters(player, page);
		}
		catch (StringIndexOutOfBoundsException e)
		{
			// Case of empty page number
			BuilderUtil.sendSysMessage(player, "Usage: //show_characters <page_number>");
		}
	}

	private void characterInfo(String command, Player player) {
		final String[] data = command.split(" ");
		if ((data.length > 1))
		{
			currentPlayer(player, World.getInstance().findPlayer(data[1]));
		}
		else if (isPlayer(player.getTarget()))
		{
			currentPlayer(player, player.getTarget().getActingPlayer());
		}
		else
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
		}
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void listCharacters(Player activeChar, int page)
	{
		final List<Player> players = new ArrayList<>(World.getInstance().getPlayers());
		players.sort(Comparator.comparingLong(Player::getUptime));
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(activeChar, "data/html/admin/charlist.htm");
		
		final PageResult result = PageBuilder.newBuilder(players, 20, "bypass -h admin_show_characters").currentPage(page).bodyHandler((pages, player, sb) ->
		{
			sb.append("<tr>");
			sb.append("<td width=80><a action=\"bypass -h admin_character_info ").append(player.getName()).append("\">").append(player.getName()).append("</a></td>");
			sb.append("<td width=110>").append(ClassListData.getInstance().getClass(player.getClassId()).getClientCode()).append("</td><td width=40>").append(player.getLevel()).append("</td>");
			sb.append("</tr>");
		}).build();
		
		if (result.getPages() > 0)
		{
			html.replace("%pages%", "<table width=280 cellspacing=0><tr>" + result.getPagerTemplate() + "</tr></table>");
		}
		else
		{
			html.replace("%pages%", "");
		}
		
		html.replace("%players%", result.getBodyTemplate().toString());
		activeChar.sendPacket(html);
	}
	
	private void currentPlayer(Player activeChar, Player player) {
		if (isNull(player)) {
			final WorldObject target = activeChar.getTarget();
			if (isPlayer(target)) {
				player = (Player) target;
			}
			else {
				return;
			}
		}
		else {
			activeChar.setTarget(player);
		}
		gatherCharacterInfo(activeChar, player, "charinfo.htm");
	}
	
	/**
	 * Retrieve and replace player's info in filename htm file, sends it to activeChar as NpcHtmlMessage.
	 */
	private void gatherCharacterInfo(Player activeChar, Player player, String filename)
	{
		String ip = "N/A";
		
		if (isNull(player)) {
			BuilderUtil.sendSysMessage(activeChar, "Player is null.");
			return;
		}
		
		final GameClient client = player.getClient();
		if (isNull(client)) {
			BuilderUtil.sendSysMessage(activeChar, "Client is null.");
		}
		else {
			ip = client.getHostAddress();
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		adminReply.setFile(activeChar, "data/html/admin/" + filename);
		adminReply.replace("%name%", player.getName());
		adminReply.replace("%level%", String.valueOf(player.getLevel()));
		adminReply.replace("%clan%", String.valueOf(player.getClan() != null ? "<a action=\"bypass -h admin_clan_info " + player.getObjectId() + "\">" + player.getClan().getName() + "</a>" : null));
		adminReply.replace("%xp%", String.valueOf(player.getExp()));
		adminReply.replace("%sp%", String.valueOf(player.getSp()));
		adminReply.replace("%class%", ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
		adminReply.replace("%ordinal%", String.valueOf(player.getClassId().getId()));
		adminReply.replace("%classid%", String.valueOf(player.getClassId()));
		adminReply.replace("%baseclass%", Util.emptyIfNullOrElse(ClassListData.getInstance().getClass(player.getBaseClass()), ClassInfo::getClientCode));
		adminReply.replace("%x%", String.valueOf(player.getX()));
		adminReply.replace("%y%", String.valueOf(player.getY()));
		adminReply.replace("%z%", String.valueOf(player.getZ()));
		adminReply.replace("%heading%", String.valueOf(player.getHeading()));
		adminReply.replace("%currenthp%", String.valueOf((int) player.getCurrentHp()));
		adminReply.replace("%maxhp%", String.valueOf(player.getMaxHp()));
		adminReply.replace("%reputation%", String.valueOf(player.getReputation()));
		adminReply.replace("%currentmp%", String.valueOf((int) player.getCurrentMp()));
		adminReply.replace("%maxmp%", String.valueOf(player.getMaxMp()));
		adminReply.replace("%pvpflag%", String.valueOf(player.getPvpFlag()));
		adminReply.replace("%currentcp%", String.valueOf((int) player.getCurrentCp()));
		adminReply.replace("%maxcp%", String.valueOf(player.getMaxCp()));
		adminReply.replace("%pvpkills%", String.valueOf(player.getPvpKills()));
		adminReply.replace("%pkkills%", String.valueOf(player.getPkKills()));
		adminReply.replace("%currentload%", String.valueOf(player.getCurrentLoad()));
		adminReply.replace("%maxload%", String.valueOf(player.getMaxLoad()));
		adminReply.replace("%percent%", String.format("%.2f", (((float) player.getCurrentLoad() / player.getMaxLoad()) * 100)));
		adminReply.replace("%patk%", String.valueOf(player.getPAtk()));
		adminReply.replace("%matk%", String.valueOf(player.getMAtk()));
		adminReply.replace("%pdef%", String.valueOf(player.getPDef()));
		adminReply.replace("%mdef%", String.valueOf(player.getMDef()));
		adminReply.replace("%accuracy%", String.valueOf(player.getAccuracy()));
		adminReply.replace("%evasion%", String.valueOf(player.getEvasionRate()));
		adminReply.replace("%critical%", String.valueOf(player.getCriticalHit()));
		adminReply.replace("%runspeed%", String.valueOf(player.getRunSpeed()));
		adminReply.replace("%patkspd%", String.valueOf(player.getPAtkSpd()));
		adminReply.replace("%matkspd%", String.valueOf(player.getMAtkSpd()));
		adminReply.replace("%access%", player.getAccessLevel().getLevel() + " (" + player.getAccessLevel().getName() + ")");
		adminReply.replace("%account%", player.getAccountName());
		adminReply.replace("%ip%", ip);
		adminReply.replace("%hwid%", (player.getClient() != null) && (player.getClient().getHardwareInfo() != null) ? player.getClient().getHardwareInfo().getMacAddress() : "Unknown");
		adminReply.replace("%ai%", player.getAI().getIntention().name());
		adminReply.replace("%inst%", player.isInInstance() ? "<tr><td>InstanceId:</td><td><a action=\"bypass -h admin_instance_spawns " + player.getInstanceId() + "\">" + player.getInstanceId() + "</a></td></tr>" : "");
		activeChar.sendPacket(adminReply);
	}

	private void findCharactersPerIp(Player activeChar, String IpAdress) throws IllegalArgumentException
	{
		boolean findDisconnected = false;
		
		if (IpAdress.equals("disconnected"))
		{
			findDisconnected = true;
		}
		else if (!IpAdress.matches("^(?:(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))$"))
		{
			throw new IllegalArgumentException("Malformed IPv4 number");
		}
		
		int CharactersFound = 0;
		GameClient client;
		final StringBuilder replyMSG = new StringBuilder(1000);
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		adminReply.setFile(activeChar, "data/html/admin/ipfind.htm");
		
		final List<Player> players = new ArrayList<>(World.getInstance().getPlayers());
		players.sort(Comparator.comparingLong(Player::getUptime));
		for (Player player : players)
		{
			client = player.getClient();
			if (client == null)
			{
				continue;
			}

			if (findDisconnected)
			{
				continue;
			}

			String ip = client.getHostAddress();
			if (!ip.equals(IpAdress))
			{
				continue;
			}
			
			final String name = player.getName();
			CharactersFound += 1;
			replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_info ");
			replyMSG.append(name);
			replyMSG.append("\">");
			replyMSG.append(name);
			replyMSG.append("</a></td><td width=110>");
			replyMSG.append(ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
			replyMSG.append("</td><td width=40>");
			replyMSG.append(player.getLevel());
			replyMSG.append("</td></tr>");
			
			if (CharactersFound > 20)
			{
				break;
			}
		}
		adminReply.replace("%results%", replyMSG.toString());
		
		final String replyMSG2;
		
		if (CharactersFound == 0)
		{
			replyMSG2 = "s. Maybe they got d/c? :)";
		}
		else if (CharactersFound > 20)
		{
			adminReply.replace("%number%", " more than " + CharactersFound);
			replyMSG2 = "s.<br>In order to avoid you a client crash I won't <br1>display results beyond the 20th character.";
		}
		else if (CharactersFound == 1)
		{
			replyMSG2 = ".";
		}
		else
		{
			replyMSG2 = "s.";
		}
		adminReply.replace("%ip%", IpAdress);
		adminReply.replace("%number%", String.valueOf(CharactersFound));
		adminReply.replace("%end%", replyMSG2);
		activeChar.sendPacket(adminReply);
	}

	private void findCharactersPerAccount(Player activeChar, String characterName) throws IllegalArgumentException
	{
		final Player player = World.getInstance().findPlayer(characterName);
		if (player == null)
		{
			throw new IllegalArgumentException("Player doesn't exist");
		}
		
		final var chars = player.getAccountChars();
		final StringJoiner replyMSG = new StringJoiner("<br1>");
		chars.values().forEach(replyMSG::add);

		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		adminReply.setFile(activeChar, "data/html/admin/accountinfo.htm");
		adminReply.replace("%account%", player.getAccountName());
		adminReply.replace("%player%", characterName);
		adminReply.replace("%characters%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private boolean findDualBox(String command, Player player) {
		int multiBox = 2;
		try {
			final String val = command.substring(19);
			multiBox = Integer.parseInt(val);
			if (multiBox < 1) {
				BuilderUtil.sendSysMessage(player, "Usage: //find_dualbox [number > 0]");
				return false;
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		findDualBox(player, multiBox);
		return true;
	}

	private void findDualBox(Player activeChar, int multibox) {
		Map<String, Integer> ipCount = new HashMap<>();
		var results = new StringBuilder();
		for (var player : World.getInstance().getPlayers()) {
			var ip = player.getIPAddress();
			var count = ipCount.merge(player.getIPAddress(), 1, Integer::sum);
			if(count >= multibox) {
				results.append("<a action=\"bypass -h admin_find_ip ").append(ip).append("\">").append(ip).append(" (").append(count).append(")</a><br1>");
			}
		}

		var adminReply = new NpcHtmlMessage(0, 1);
		adminReply.setFile(activeChar, "data/html/admin/dualbox.htm");
		adminReply.replace("%multibox%", String.valueOf(multibox));
		adminReply.replace("%results%", results.toString());
		adminReply.replace("%strict%", "");
		activeChar.sendPacket(adminReply);
	}

	private boolean strictFindDualBox(String command, Player player) {
		int multibox = 2;
		try {
			final String val = command.substring(26);
			multibox = Integer.parseInt(val);
			if (multibox < 1) {
				BuilderUtil.sendSysMessage(player, "Usage: //strict_find_dualbox [number > 0]");
				return false;
			}
		}
		catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		findDualBoxStrict(player, multibox);
		return true;
	}
	
	private void findDualBoxStrict(Player activeChar, int multibox) {
		Map<IpPack, Integer> ipCount = new HashMap<>();
		var results = new StringBuilder();
		for (var player : World.getInstance().getPlayers()) {
			var client = player.getClient();
			var ip = new IpPack(client.getHostAddress(), client.getTrace());
			var count = ipCount.merge(ip, 1, Integer::sum);
			if(count >= multibox) {
				results.append("<a action=\"bypass -h admin_find_ip ").append(ip.ip).append("\">").append(ip.ip).append(" (").append(count).append(")</a><br1>");
			}
		}

		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		adminReply.setFile(activeChar, "data/html/admin/dualbox.htm");
		adminReply.replace("%multibox%", String.valueOf(multibox));
		adminReply.replace("%results%", results.toString());
		adminReply.replace("%strict%", "strict_");
		activeChar.sendPacket(adminReply);
	}
	
	private static final class IpPack {
		String ip;
		int[][] tracert;
		
		public IpPack(String ip, int[][] tracert) {
			this.ip = ip;
			this.tracert = tracert;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			IpPack ipPack = (IpPack) o;
			return Objects.equals(ip, ipPack.ip) && Arrays.deepEquals(tracert, ipPack.tracert);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(ip);
			result = 31 * result + Arrays.deepHashCode(tracert);
			return result;
		}
	}
	
	private void gatherSummonInfo(Summon target, Player activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(activeChar, "data/html/admin/petinfo.htm");
		final String name = target.getName();
		html.replace("%name%", name == null ? "N/A" : name);
		html.replace("%level%", Integer.toString(target.getLevel()));
		html.replace("%exp%", Long.toString(target.getStats().getExp()));
		final String owner = target.getActingPlayer().getName();
		html.replace("%owner%", " <a action=\"bypass -h admin_character_info " + owner + "\">" + owner + "</a>");
		html.replace("%class%", target.getClass().getSimpleName());
		html.replace("%ai%", target.hasAI() ? target.getAI().getIntention().name() : "NULL");
		html.replace("%hp%", (int) target.getStatus().getCurrentHp() + "/" + target.getStats().getMaxHp());
		html.replace("%mp%", (int) target.getStatus().getCurrentMp() + "/" + target.getStats().getMaxMp());
		html.replace("%karma%", Integer.toString(target.getReputation()));
		html.replace("%race%", target.getTemplate().getRace().toString());
		if (isPet(target))
		{
			final int objId = target.getActingPlayer().getObjectId();
			html.replace("%inv%", " <a action=\"bypass admin_show_pet_inv " + objId + "\">view</a>");
		}
		else
		{
			html.replace("%inv%", "none");
		}
		if (isPet(target))
		{
			html.replace("%food%", ((Pet) target).getCurrentFed() + "/" + ((Pet) target).getPetLevelData().getPetMaxFeed());
			html.replace("%load%", target.getInventory().getTotalWeight() + "/" + target.getMaxLoad());
		}
		else
		{
			html.replace("%food%", "N/A");
			html.replace("%load%", "N/A");
		}
		activeChar.sendPacket(html);
	}
	
	private void gatherPartyInfo(Player target, Player activeChar)
	{
		boolean color = true;
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(activeChar, "data/html/admin/partyinfo.htm");
		final StringBuilder text = new StringBuilder(400);
		for (Player member : target.getParty().getMembers())
		{
			if (color)
			{
				text.append("<tr><td><table width=270 border=0 bgcolor=131210 cellpadding=2><tr><td width=30 align=right>");
			}
			else
			{
				text.append("<tr><td><table width=270 border=0 cellpadding=2><tr><td width=30 align=right>");
			}
			text.append(member.getLevel()).append("</td><td width=130><a action=\"bypass -h admin_character_info ").append(member.getName()).append("\">").append(member.getName()).append("</a>");
			text.append("</td><td width=110 align=right>").append(member.getClassId()).append("</td></tr></table></td></tr>");
			color = !color;
		}
		html.replace("%player%", target.getName());
		html.replace("%party%", text.toString());
		activeChar.sendPacket(html);
	}
}
