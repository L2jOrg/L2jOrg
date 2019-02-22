package org.l2j.scripts.npc.model.residences;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.base.ResidenceFunctionType;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.model.entity.residence.ResidenceFunction;
import org.l2j.gameserver.model.instances.MerchantInstance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.HtmlMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.templates.residence.ResidenceFunctionTemplate;
import org.l2j.gameserver.utils.ReflectionUtils;
import org.l2j.gameserver.utils.SkillUtils;
import org.l2j.gameserver.utils.TimeUtils;
import org.l2j.gameserver.utils.WarehouseFunctions;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * some rework by VISTALL
 */
public abstract class ResidenceManager extends MerchantInstance
{
	private static final int CH_BLESSING_SKIL_ID_1 = 4367;
	private static final int CH_BLESSING_SKIL_ID_2 = 4368;
	private static final int CH_BLESSING_SKIL_ID_3 = 4369;
	private static final int CH_BLESSING_SKIL_ID_4 = 4370;
	private static final int CH_BLESSING_SKIL_ID_5 = 4371;
	private static final int CH_BLESSING_SKIL_ID_6 = 4372;
	private static final int CH_BLESSING_SKIL_ID_7 = 4373;
	private static final int CH_BLESSING_SKIL_ID_8 = 4374;
	private static final int CH_BLESSING_SKIL_ID_9 = 4375;

	protected static final int COND_FAIL	= 0;
	protected static final int COND_SIEGE	= 1;
	protected static final int COND_OWNER	= 2;

	protected String _siegeDialog;
	protected String _mainDialog;
	protected String _failDialog;

	protected int[] _doors;

	public ResidenceManager(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		setDialogs();

		_doors = template.getAIParams().getIntegerArray("doors", Util.INT_ARRAY_EMPTY);
	}

	protected void setDialogs()
	{
		_siegeDialog = getTemplate().getAIParams().getString("siege_dialog", "npcdefault.htm");
		_mainDialog = getTemplate().getAIParams().getString("main_dialog", "npcdefault.htm");
		_failDialog = getTemplate().getAIParams().getString("fail_dialog", "npcdefault.htm");
	}

	protected abstract Residence getResidence();

	protected abstract L2GameServerPacket decoPacket();

	protected abstract int getPrivUseFunctions();

	protected abstract int getPrivSetFunctions();

	protected abstract int getPrivDismiss();

	protected abstract int getPrivDoors();

	public void broadcastDecoInfo()
	{
		L2GameServerPacket decoPacket = decoPacket();
		if(decoPacket == null)
			return;
		for(Player player : World.getAroundObservers(this))
			player.sendPacket(decoPacket);
	}

	protected int getCond(Player player)
	{
		Residence residence = getResidence();
		Clan residenceOwner = residence.getOwner();
		if(residenceOwner != null && player.getClan() == residenceOwner)
		{
			if(residence.getSiegeEvent().isInProgress())
				return COND_SIEGE;
			else
				return COND_OWNER;
		}
		else
			return COND_FAIL;
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		String filename = null;
		int cond = getCond(player);
		switch(cond)
		{
			case COND_OWNER:
				filename = _mainDialog;
				break;
			case COND_SIEGE:
				filename = _siegeDialog;
				break;
			case COND_FAIL:
				filename = _failDialog;
				break;
		}
		player.sendPacket(new HtmlMessage(this, filename).setPlayVoice(firstTalk));
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		String val = "";
		if(st.countTokens() >= 1)
			val = st.nextToken();

		int cond = getCond(player);
		switch(cond)
		{
			case COND_SIEGE:
				showChatWindow(player, _siegeDialog, false);
				return;
			case COND_FAIL:
				showChatWindow(player, _failDialog, false);
				return;
		}

		if(actualCommand.equalsIgnoreCase("banish"))
		{
			HtmlMessage html = new HtmlMessage(this);
			html.setFile("residence/Banish.htm");
			sendHtmlMessage(player, html);
		}
		else if(actualCommand.equalsIgnoreCase("banish_foreigner"))
		{
			if(!isHaveRigths(player, getPrivDismiss()))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			getResidence().banishForeigner(player.getClan().getClanId());
			return;
		}
		else if(actualCommand.equalsIgnoreCase("Buy"))
		{
			if(val.equals(""))
				return;
			showShopWindow(player, Integer.valueOf(val), true);
		}
		else if(actualCommand.equalsIgnoreCase("manage_vault"))
		{
			if(val.equalsIgnoreCase("deposit"))
				WarehouseFunctions.showDepositWindowClan(player);
			else if(val.equalsIgnoreCase("withdraw"))
				WarehouseFunctions.showWithdrawWindowClan(player);
			else
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/vault.htm");
				sendHtmlMessage(player, html);
			}
			return;
		}
		else if(actualCommand.equalsIgnoreCase("door"))
		{
			showChatWindow(player, "residence/door.htm", false);
		}
		else if(actualCommand.equalsIgnoreCase("openDoors"))
		{
			if(isHaveRigths(player, getPrivDoors()))
			{
				for(int i : _doors)
					ReflectionUtils.getDoor(i).openMe();

				showChatWindow(player, "residence/door.htm", false);
			}
			else
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
		}
		else if(actualCommand.equalsIgnoreCase("closeDoors"))
		{
			if(isHaveRigths(player, getPrivDoors()))
			{
				for(int i : _doors)
					ReflectionUtils.getDoor(i).closeMe();

				showChatWindow(player, "residence/door.htm", false);
			}
			else
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
		}
		else if(actualCommand.equalsIgnoreCase("functions"))
		{
			if(!isHaveRigths(player, getPrivUseFunctions()))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(val.equalsIgnoreCase("tele"))
			{
				showTeleportList(player);
			}
			else if(val.equalsIgnoreCase("item_creation"))
			{
				showShopWindow(player);
			}
			else if(val.equalsIgnoreCase("support"))
			{
				ResidenceFunction function = getResidence().getActiveFunction(ResidenceFunctionType.SUPPORT);
				if(function == null)
				{
					showChatWindow(player, "residence/" + getDialogsPrefix() + "funcdisabled.htm", false);
					return;
				}

				int level = getResidence().getVisibleFunctionLevel(function.getLevel());
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/" + getDialogsPrefix() + "Buff_" + level + ".htm");
				html.replace("<?MPLeft?>", String.valueOf(Math.round(getCurrentMp())));
				sendHtmlMessage(player, html);
			}
			else if(val.equalsIgnoreCase("back"))
				showChatWindow(player, 0, false);
			else
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/" + getDialogsPrefix() + "decofunction.htm");

				ResidenceFunction function = getResidence().getActiveFunction(ResidenceFunctionType.RESTORE_EXP);
				if(function != null)
					html.replace("<?XPDepth?>", String.valueOf((int) (function.getTemplate().getExpRestore() * 100)));
				else
					html.replace("<?XPDepth?>", "0");

				function = getResidence().getActiveFunction(ResidenceFunctionType.RESTORE_HP);
				if(function != null)
					html.replace("<?HPDepth?>", String.valueOf((int) (function.getTemplate().getHpRegen() * 100 - 100)));
				else
					html.replace("<?HPDepth?>", "0");

				function = getResidence().getActiveFunction(ResidenceFunctionType.RESTORE_MP);
				if(function != null)
					html.replace("<?MPDepth?>", String.valueOf((int) (function.getTemplate().getMpRegen() * 100 - 100)));
				else
					html.replace("<?MPDepth?>", "0");

				sendHtmlMessage(player, html);
			}
		}
		else if(actualCommand.equalsIgnoreCase("manage"))
		{
			if(!isHaveRigths(player, getPrivSetFunctions()))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}

			if(val.equalsIgnoreCase("recovery"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("hp"))
						success = getResidence().updateFunctions(ResidenceFunctionType.RESTORE_HP, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("mp"))
						success = getResidence().updateFunctions(ResidenceFunctionType.RESTORE_MP, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("exp"))
						success = getResidence().updateFunctions(ResidenceFunctionType.RESTORE_EXP, Integer.valueOf(st.nextToken()));
					if(!success)
						player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
					{
						getResidence().getZone().refreshListeners();
						broadcastDecoInfo();
					}
				}
				showManageRecovery(player);
			}
			else if(val.equalsIgnoreCase("other"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("item"))
						success = getResidence().updateFunctions(ResidenceFunctionType.ITEM_CREATE, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("tele"))
						success = getResidence().updateFunctions(ResidenceFunctionType.TELEPORT, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("support"))
					{
						success = getResidence().updateFunctions(ResidenceFunctionType.SUPPORT, Integer.valueOf(st.nextToken()));
						if(success)
							checkAdditionalSupportSkill();
					}
					if(!success)
						player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
						broadcastDecoInfo();
				}
				showManageOther(player);
			}
			else if(val.equalsIgnoreCase("deco"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("platform"))
						success = getResidence().updateFunctions(ResidenceFunctionType.PLATFORM, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("curtain"))
						success = getResidence().updateFunctions(ResidenceFunctionType.CURTAIN, Integer.valueOf(st.nextToken()));
					if(!success)
						player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
						broadcastDecoInfo();
				}
				showManageDeco(player);
			}
			else if(val.equalsIgnoreCase("back"))
				showChatWindow(player, 0, false);
			else
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/manage.htm");
				sendHtmlMessage(player, html);
			}
			return;
		}
		else if(actualCommand.equalsIgnoreCase("support"))
		{
			if(!isHaveRigths(player, getPrivUseFunctions()))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}

			setTarget(player);

			if(val.isEmpty())
				return;

			if(!getResidence().isFunctionActive(ResidenceFunctionType.SUPPORT))
				return;

			int skill_id = Integer.parseInt(val);
			int skill_lvl = st.countTokens() >= 1 ? Integer.parseInt(st.nextToken()) : 0;
			useSkill(skill_id, skill_lvl, player);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		int cond = getCond(player);
		switch(cond)
		{
			case COND_SIEGE:
				showChatWindow(player, _siegeDialog, false);
				return;
			case COND_FAIL:
				showChatWindow(player, _failDialog, false);
				return;
		}

		if(ask == 0)
			showChatWindow(player, 0, false);
		else if(ask == -201)
		{
			if(reply == 0)
				showChatWindow(player, 0, false);
			else if(reply == 3 || reply == 103)
				onBypassFeedback(player, "functions");
			else if(reply == 5 || reply == 105)
				onBypassFeedback(player, "manage");
			else if(reply == 7 || reply == 107)
				onBypassFeedback(player, "functions support");
			else if(reply == 12)
				onBypassFeedback(player, "functions item_creation");
		}
		else if(ask == -208 || ask == -22208)
		{
			int id, level;
			if(reply >= Integer.MIN_VALUE && reply <= Integer.MAX_VALUE)
			{
				id = SkillUtils.getSkillIdFromPTSHash((int) reply);
				level = SkillUtils.getSkillLevelFromPTSHash((int) reply);
			}
			else
			{
				id = SkillUtils.getSkillIdFromPTSLongHash(reply);
				level = SkillUtils.getSkillLevelFromPTSLongHash(reply);
			}
			System.out.println("support " + id + " " + level);
			onBypassFeedback(player, "support " + id + " " + level);
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public void showTeleportList(Player player, int listId)
	{
		ResidenceFunction function = getResidence().getActiveFunction(ResidenceFunctionType.TELEPORT);
		if(function == null)
		{
			showChatWindow(player, "residence/" + getDialogsPrefix() + "funcdisabled.htm", false);
			return;
		}

		int level = getResidence().getVisibleFunctionLevel(function.getLevel());
		super.showTeleportList(player, level);
	}

	@Override
	protected void showShopWindow(Player player, int listId, boolean tax)
	{
		if(isCheckBuyFunction())
		{
			ResidenceFunction function = getResidence().getActiveFunction(ResidenceFunctionType.ITEM_CREATE);
			if(function == null)
			{
				showChatWindow(player, "residence/" + getDialogsPrefix() + "funcdisabled.htm", false);
				return;
			}

			int level = getResidence().getVisibleFunctionLevel(function.getLevel());
			super.showShopWindow(player, level, true); // Взымается ли налог?
		}
		else
			super.showShopWindow(player, listId, tax); // Взымается ли налог?
	}

	private void useSkill(int id, int level, Player player)
	{
		Skill skill = SkillHolder.getInstance().getSkill(id, level);
		if(skill == null)
		{
			player.sendMessage("Invalid skill " + id);
			return;
		}

		if(skill.getMpConsume() < getCurrentMp())
		{
			if(!isSkillDisabled(skill))
			{
				altUseSkill(skill, player);
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/" + getDialogsPrefix() + "afterbuff.htm");
				html.replace("<?MPLeft?>", String.valueOf(Math.round(getCurrentMp())));
				sendHtmlMessage(player, html);
			}
			else
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/" + getDialogsPrefix() + "needcooltime.htm");
				html.replace("<?MPLeft?>", String.valueOf(Math.round(getCurrentMp())));
				sendHtmlMessage(player, html);
			}
		}
		else
		{
			HtmlMessage html = new HtmlMessage(this);
			html.setFile("residence/" + getDialogsPrefix() + "notenoughmp.htm");
			html.replace("<?MPLeft?>", String.valueOf(Math.round(getCurrentMp())));
			sendHtmlMessage(player, html);
		}
	}

	private void sendHtmlMessage(Player player, HtmlMessage html)
	{
		player.sendPacket(html);
	}

	private void replace(Player player, HtmlMessage html, ResidenceFunctionType type, String replace1, String replace2)
	{
		ResidenceFunction function = getResidence().getActiveFunction(type);
		if(function != null)
		{
			if(type == ResidenceFunctionType.RESTORE_HP)
				html.replace("%" + replace1 + "%", String.valueOf((int) (function.getTemplate().getHpRegen() * 100 - 100)));
			else if(type == ResidenceFunctionType.RESTORE_MP)
				html.replace("%" + replace1 + "%", String.valueOf((int) (function.getTemplate().getMpRegen() * 100 - 100)));
			else if(type == ResidenceFunctionType.RESTORE_EXP)
				html.replace("%" + replace1 + "%", String.valueOf((int) (function.getTemplate().getExpRestore() * 100)));
			else
				html.replace("%" + replace1 + "%", String.valueOf(getResidence().getVisibleFunctionLevel(function.getLevel())));

			html.replace("%" + replace1 + "Price%", String.valueOf(function.getTemplate().getCost() / function.getTemplate().getPeriod()));
			html.replace("%" + replace1 + "Date%", TimeUtils.toSimpleFormat(function.getEndTimeInMillis()));
		}
		else
		{
			html.replace("%" + replace1 + "%", "0");
			html.replace("%" + replace1 + "Price%", "0");
			html.replace("%" + replace1 + "Date%", "0");
		}

		List<ResidenceFunctionTemplate> availableFunctions = getResidence().getAvailableFunctions(type);
		if(!availableFunctions.isEmpty())
		{
			Collections.sort(availableFunctions);

			boolean percent = type == ResidenceFunctionType.RESTORE_HP || type == ResidenceFunctionType.RESTORE_MP || type == ResidenceFunctionType.RESTORE_EXP;

			StringBuilder out = new StringBuilder();
			if(getResidence().isFunctionActive(type))
				out.append(percent ? "[" : "").append("<a action=\"bypass -h npc_%objectId%_manage ").append(replace2).append(" ").append(replace1).append(" 0\">").append(new CustomMessage("STOP").toString(player)).append("</a>").append(percent ? "]" : "").append("&nbsp;");

			for(ResidenceFunctionTemplate template : availableFunctions)
			{
				String name;
				if(template.getType() == ResidenceFunctionType.RESTORE_HP)
					name = String.valueOf((int) (template.getHpRegen() * 100 - 100)) + "%";
				else if(template.getType() == ResidenceFunctionType.RESTORE_MP)
					name = String.valueOf((int) (template.getMpRegen() * 100 - 100)) + "%";
				else if(template.getType() == ResidenceFunctionType.RESTORE_EXP)
					name = String.valueOf((int) (template.getExpRestore() * 100)) + "%";
				else
					name = new CustomMessage("STAGE").toString(player) + " " + String.valueOf(getResidence().getVisibleFunctionLevel(template.getLevel()));
	
				out.append(percent ? "[" : "").append("<a action=\"bypass -h npc_%objectId%_manage ").append(replace2).append(" ").append(replace1).append(" ").append(template.getLevel()).append("\">").append(name).append("</a>").append(percent ? "]" : "").append("&nbsp;");
			}
			html.replace("%" + replace1 + "Manage%", out.toString());
		}
		else
			html.replace("%" + replace1 + "Manage%", new CustomMessage("NOT_AVAILABLE").toString(player));
	}

	private void showManageRecovery(Player player)
	{
		HtmlMessage html = new HtmlMessage(this);
		html.setFile("residence/edit_recovery.htm");

		replace(player, html, ResidenceFunctionType.RESTORE_EXP, "exp", "recovery");
		replace(player, html, ResidenceFunctionType.RESTORE_HP, "hp", "recovery");
		replace(player, html, ResidenceFunctionType.RESTORE_MP, "mp", "recovery");

		sendHtmlMessage(player, html);
	}

	private void showManageOther(Player player)
	{
		HtmlMessage html = new HtmlMessage(this);
		html.setFile("residence/edit_other.htm");

		replace(player, html, ResidenceFunctionType.TELEPORT, "tele", "other");
		replace(player, html, ResidenceFunctionType.SUPPORT, "support", "other");
		replace(player, html, ResidenceFunctionType.ITEM_CREATE, "item", "other");

		sendHtmlMessage(player, html);
	}

	private void showManageDeco(Player player)
	{
		HtmlMessage html = new HtmlMessage(this);
		html.setFile("residence/edit_deco.htm");

		replace(player, html, ResidenceFunctionType.CURTAIN, "curtain", "deco");
		replace(player, html, ResidenceFunctionType.PLATFORM, "platform", "deco");

		sendHtmlMessage(player, html);
	}

	protected boolean isHaveRigths(Player player, int rigthsToCheck)
	{
		return player.getClan() != null && (player.getClanPrivileges() & rigthsToCheck) == rigthsToCheck;
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		List<L2GameServerPacket> list = super.addPacketList(forPlayer, dropper);
		L2GameServerPacket p = decoPacket();
		if(p != null)
			list.add(p);
		return list;
	}

	@Override
	public double getCurrentMp()
	{
		if(Config.ALT_CH_UNLIM_MP)
			return getMaxMp();
		return super.getCurrentMp();
	}

	@Override
	protected void onSpawn()
	{
		checkAdditionalSupportSkill();
		super.onSpawn();
	}

	private void checkAdditionalSupportSkill()
	{
		Residence residence = getResidence();
		if(residence == null)
			return;

		removeSkillById(CH_BLESSING_SKIL_ID_1);
		removeSkillById(CH_BLESSING_SKIL_ID_2);
		removeSkillById(CH_BLESSING_SKIL_ID_3);
		removeSkillById(CH_BLESSING_SKIL_ID_4);
		removeSkillById(CH_BLESSING_SKIL_ID_5);
		removeSkillById(CH_BLESSING_SKIL_ID_6);
		removeSkillById(CH_BLESSING_SKIL_ID_7);
		removeSkillById(CH_BLESSING_SKIL_ID_8);
		removeSkillById(CH_BLESSING_SKIL_ID_9);

		ResidenceFunction function = residence.getActiveFunction(ResidenceFunctionType.SUPPORT);
		if(function != null)
		{
			int level = residence.getVisibleFunctionLevel(function.getLevel());
			switch(level)
			{
				case 1:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_1, 1));
					break;
				case 2:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_2, 1));
					break;
				case 3:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_3, 1));
					break;
				case 4:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_4, 1));
					break;
				case 5:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_5, 1));
					break;
				case 6:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_6, 1));
					break;
				case 7:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_7, 1));
					break;
				case 8:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_8, 1));
					break;
				case 9:
					addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_9, 1));
					break;
			}
		}
		refreshHpMpCp();
	}

	protected boolean isCheckBuyFunction()
	{
		return true;
	}

	protected abstract String getDialogsPrefix();
}