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
package org.l2j.scripts.ai.others.ClassMaster;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.ClassInfo;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.*;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.scripts.ai.AbstractNpcAI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;


/**
 * 	Class Master AI.
 * @author Nik
 */
public final class ClassMaster extends AbstractNpcAI
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassMaster.class);
	// NPCs
	private static final IntSet CLASS_MASTERS = IntSet.of(31756, 31757); // Mr. Cat, Queen of Hearts
	public static final String TEST_SERVER_HELPER_011_HTML = "test_server_helper011.html";

	// Misc
	private boolean isEnabled;
	private boolean spawnClassMasters;
	private boolean showPopupWindow;

	private final List<ClassChangeData> classChangeData = new LinkedList<>();
	
	public ClassMaster() {
		new DataLoader().load();
		addStartNpc(CLASS_MASTERS);
		addTalkId(CLASS_MASTERS);
		addFirstTalkId(CLASS_MASTERS);
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		if (spawnClassMasters)
		{
			template.spawnAllIncludingNotDefault(null);
		}
	}
	
	@Override
	public void onSpawnDeactivate(SpawnTemplate template)
	{
		template.despawnAll();
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "test_server_helper001.html";
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		if (!isEnabled) {
			return null;
		}
		
		final StringTokenizer st = new StringTokenizer(event);
		event = st.nextToken();
		return switch (event) {
			case "buyitems" -> npc != null && npc.getId() == 31756 ? "test_server_helper001a.html" : "test_server_helper001b.html";
			case "firstclass" -> getFirstOccupationChangeHtml(player);
			case "secondclass" -> getSecondOccupationChangeHtml(player);
			case "thirdclass" -> getThirdOccupationChangeHtml(player);
			case "setclass" -> setClassHtml(player, npc, st);
			case "clanlevel" -> player.isClanLeader() ? "test_server_helper022.html" : "pl014.html";
			case "learnskills" -> onLearnSkill(player);
			case "clanlevelup" -> clanLevelUpHtml(player);
			case "test_server_helper001.html" -> npc != null && CLASS_MASTERS.contains(npc.getId()) ? event : null;
			default -> null;
		};
	}

	private String onLearnSkill(Player player) {
		player.giveAvailableSkills(true, true);
		return null;
	}

	private String clanLevelUpHtml(Player player) {
		String htmlText = null;
		if ((player.getClan() == null) || !player.isClanLeader()) {
			return null;
		}

		if (player.getClan().getLevel() >= 10) {
			htmlText = "test_server_helper022a.html";
		} else {
			player.getClan().setLevel(player.getClan().getLevel() + 1);
			player.getClan().broadcastClanStatus();
		}
		return htmlText;
	}

	private String setClassHtml(Player player, Npc npc, StringTokenizer st) {
		if (!st.hasMoreTokens()) {
			return null;
		}

		final int classId = Integer.parseInt(st.nextToken());
		if(!canChangeClass(player, classId)) {
			return null;
		}

		int classDataIndex = -1;
		if (st.hasMoreTokens()) {
			classDataIndex = Integer.parseInt(st.nextToken());
		}

		if (classDataIndex == -1 && checkIfClassChangeHasOptions(player)) {
			String htmlText = getHtml(player, "cc_options.html");
			htmlText = htmlText.replace("%name%", Util.emptyIfNullOrElse(ClassListData.getInstance().getClass(classId), ClassInfo::getClassName));
			htmlText = htmlText.replace("%options%", getClassChangeOptions(player, classId));
			return htmlText;
		}

		if (!chargeFeeAndGiveRewards(player, npc, classDataIndex)) {
			return null;
		}

		changeClass(player, classId);
		return "test_server_helper021.html";
	}

	private boolean chargeFeeAndGiveRewards(Player player, Npc npc, int classDataIndex) {
		final ClassChangeData data = getClassChangeData(classDataIndex);
		if (data != null) {
			for (var ri : data.requiredItems()) {
				if (player.getInventory().getInventoryItemCount(ri.getId(), -1) < ri.getCount()) {
					player.sendMessage("You do not have enough items.");
					return false;
				}
			}
			for (var ri : data.requiredItems()) {
				player.destroyItemByItemId(getClass().getSimpleName(), ri.getId(), ri.getCount(), npc, true);
			}

			for (ItemHolder ri : data.rewardedItems()) {
				giveItems(player, ri);
			}
		}
		return true;
	}

	private void changeClass(Player player, int classId) {
		player.setClassId(classId);
		player.setBaseClass(player.getActiveClass());
		if (CharacterSettings.autoLearnSkillEnabled()) {
			player.giveAvailableSkills(CharacterSettings.autoLearnSkillFSEnabled(), true);
		}
		player.store(false);
		player.broadcastUserInfo();
		player.sendSkillList();
		player.sendPacket(PlaySound.sound("ItemSound.quest_fanfare_2"));
	}

	private boolean canChangeClass(Player player, int classId) {
		boolean canChange = false;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) && player.getLevel() >= 40) {
			canChange = CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId);
		} else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20)) {
			canChange = CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId);
		} else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76)) {
			canChange = CategoryManager.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId);
		}
		return canChange;
	}

	private String getThirdOccupationChangeHtml(Player player) {
		String htmlText = null;
		if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() > 75)) {
			if (changeToNextClass(player)) {
				player.sendPacket(PlaySound.sound("ItemSound.quest_fanfare_2"));
				player.broadcastUserInfo();
				htmlText = "test_server_helper021.html";
			}
		} else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
			htmlText = TEST_SERVER_HELPER_011_HTML;
		}
		else {
			htmlText = "test_server_helper024.html";
		}
		return htmlText;
	}

	private String getFirstOccupationChangeHtml(Player player) {
		String htmlText = null;
		if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) {
			if (player.getLevel() < 20) {
				htmlText = "test_server_helper027.html";
			} else {
				htmlText = firstOccupationHtmlOfClassId(player);
			}
		} else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
			htmlText = "test_server_helper028.html";
		} else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmlText = "test_server_helper010.html";
		} else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
			htmlText = TEST_SERVER_HELPER_011_HTML;
		}
		return htmlText;
	}

	private String firstOccupationHtmlOfClassId(Player player) {
		String htmlText;
		htmlText = switch (player.getClassId()) {
			case FIGHTER -> "test_server_helper026a.html";
			case MAGE -> "test_server_helper026b.html";
			case ELVEN_FIGHTER -> "test_server_helper026c.html";
			case ELVEN_MAGE -> "test_server_helper026d.html";
			case DARK_FIGHTER -> "test_server_helper026e.html";
			case DARK_MAGE -> "test_server_helper026f.html";
			case ORC_FIGHTER -> "test_server_helper026g.html";
			case ORC_MAGE -> "test_server_helper026h.html";
			case DWARVEN_FIGHTER -> "test_server_helper026i.html";
			case JIN_KAMAEL_SOLDIER -> "test_server_helper026j.html";
			default -> null;
		};
		return htmlText;
	}

	private String getSecondOccupationChangeHtml(Player player) {
		String htmlText;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
			if (player.getLevel() < 40) {
				htmlText = "test_server_helper023.html";
			} else {
				htmlText = secondOccupationHtmlOfClassId(player);
			}
		} else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmlText = "test_server_helper010.html";
		} else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
			htmlText = TEST_SERVER_HELPER_011_HTML;
		} else {
			htmlText = "test_server_helper029.html";
		}
		return htmlText;
	}

	private String secondOccupationHtmlOfClassId(Player player) {
		return switch (player.getClassId()) {
			case FIGHTER -> "test_server_helper012.html";
			case WARRIOR -> "test_server_helper012a.html";
			case KNIGHT -> "test_server_helper012b.html";
			case ROGUE -> "test_server_helper012c.html";
			case MAGE -> "test_server_helper013.html";
			case WIZARD -> "test_server_helper013a.html";
			case CLERIC -> "test_server_helper013b.html";
			case ELVEN_FIGHTER -> "test_server_helper014.html";
			case ELVEN_KNIGHT -> "test_server_helper014a.html";
			case ELVEN_SCOUT -> "test_server_helper014b.html";
			case ELVEN_MAGE -> "test_server_helper015.html";
			case ELVEN_WIZARD -> "test_server_helper015a.html";
			case ORACLE -> "test_server_helper015b.html";
			case DARK_FIGHTER -> "test_server_helper016.html";
			case PALUS_KNIGHT -> "test_server_helper016a.html";
			case ASSASSIN -> "test_server_helper016b.html";
			case DARK_MAGE -> "test_server_helper017.html";
			case DARK_WIZARD -> "test_server_helper017a.html";
			case SHILLIEN_ORACLE -> "test_server_helper017b.html";
			case ORC_FIGHTER -> "test_server_helper018.html";
			case ORC_RAIDER -> "test_server_helper018a.html";
			case ORC_MONK -> "test_server_helper018b.html";
			case ORC_MAGE, ORC_SHAMAN -> "test_server_helper019.html";
			case DWARVEN_FIGHTER -> "test_server_helper020.html";
			case ARTISAN -> "test_server_helper020b.html";
			case SCAVENGER -> "test_server_helper020a.html";
			case JIN_KAMAEL_SOLDIER, TROOPER -> "test_server_helper020c.html";
			case SOUL_FINDER -> "test_server_helper020d.html";
			case WARDER -> "test_server_helper030c.html";
			default -> null;
		};
	}

	private boolean changeToNextClass(Player player)
	{
		ClassId newClass = Arrays.stream(ClassId.values()).filter(cid -> player.getClassId() == cid.getParent()).findAny().orElse(null);

		if (newClass == null)
		{
			LOGGER.warn(": No new classId found for player " + player);
			return false;
		}
		else if (newClass == player.getClassId())
		{
			LOGGER.warn(": New classId found for player " + player + " is exactly the same as the one he currently is!");
			return false;
		}
		else if (checkIfClassChangeHasOptions(player))
		{
			String html = getHtml(player, "cc_options.html");
			html = html.replace("%name%", Util.emptyIfNullOrElse(ClassListData.getInstance().getClass(newClass.getId()), ClassInfo::getClassName));
			html = html.replace("%options%", getClassChangeOptions(player, newClass.getId()));
			showResult(player, html);
			return false;
		}
		else
		{
			ClassChangeData data = null;
			for (var d : classChangeData) {
				if(player.isInCategory(d.category())) {
					data = d;
					break;
				}
			}
			if (data != null)
			{

				for (var ri : data.requiredItems()) {
					if (player.getInventory().getInventoryItemCount(ri.getId(), -1) < ri.getCount()) {
						player.sendMessage("You do not have enough items.");
						return false; // No class change if payment failed.
					}
				}
				for (var ri : data.requiredItems()) {
					player.destroyItemByItemId(getClass().getSimpleName(), ri.getId(), ri.getCount(), player, true);
				}

				for (var ri : data.rewardedItems()) {
					giveItems(player, ri);
				}
			}
			
			player.setClassId(newClass.getId());
			player.setBaseClass(player.getActiveClass());

			if (CharacterSettings.autoLearnSkillEnabled())
			{
				player.giveAvailableSkills(CharacterSettings.autoLearnSkillFSEnabled(), true);
			}
			player.store(false); // Save player cause if server crashes before this char is saved, he will lose class and the money payed for class change.
			player.broadcastUserInfo();
			player.sendSkillList();
			return true;
		}
	}
	
	private void showPopupWindow(Player player)
	{
		if (!showPopupWindow)
		{
			return;
		}

		//@formatter:off
		if ((player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20)) ||
			((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) ||
			(player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76)))
		//@formatter:on
		{
			player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET); // mark id was 1001 - used 2 for quest text
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		final Player player = event.getPlayer();
		
		if (!showPopupWindow || (event.getMarkId() != 2)) // mark id was 1001 - used 2 for quest text
		{
			return;
		}
		
		String html = null;
		if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) // In retail you can skip first occupation
		{
			html = getHtml(player, getSecondOccupationChangeHtml(player));
		}
		else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20))
		{
			html = getHtml(player, getFirstOccupationChangeHtml(player));
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76))
		{
			html = getHtml(player, "qm_thirdclass.html");
		}
		
		if (html != null)
		{
			showResult(event.getPlayer(), html);
			// player.sendPacket(new TutorialShowHtml(html));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerBypass(OnPlayerBypass event)
	{
		if (event.getCommand().startsWith("Quest ClassMaster "))
		{
			final String html = onAdvEvent(event.getCommand().substring(18), null, event.getPlayer());
			event.getPlayer().sendPacket(TutorialCloseHtml.STATIC_PACKET);
			showResult(event.getPlayer(), html);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerProfessionChange(OnPlayerProfessionChange event)
	{
		showPopupWindow(event.getActiveChar());
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event) {
		showPopupWindow(event.getActiveChar());
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		showPopupWindow(event.getPlayer());
	}
	
	private String getClassChangeOptions(Player player, int selectedClassId) {
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < classChangeData.size(); i++) {
			final ClassChangeData option = getClassChangeData(i);
			if (option == null || !player.isInCategory(option.category())) {
				continue;
			}
			
			sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
			sb.append("<tr><td><table bgcolor=3f3f3f width=100%>");
			sb.append("<tr><td align=center><a action=\"bypass -h Quest ClassMaster setclass ").append(selectedClassId).append(" ").append(i).append("\">").append(option.name()).append(":</a></td></tr>");
			sb.append("<tr><td><table width=276>");
			sb.append("<tr><td>Requirements:</td></tr>");

			if (!option.hasRequiredItems()) {
				sb.append("<tr><td><font color=LEVEL>Free</font></td></tr>");
			}
			else
			{
				option.requiredItems().forEach(ih -> sb.append("<tr><td><font color=\"LEVEL\">").append(ih.getCount()).append("</font></td><td>").append(ItemEngine.getInstance().getTemplate(ih.getId()).getName()).append("</td><td width=30></td></tr>"));
			}
			sb.append("<tr><td>Rewards:</td></tr>");
			option.rewardedItems().forEach(ih -> sb.append("<tr><td><font color=\"LEVEL\">").append(ih.getCount()).append("</font></td><td>").append(ItemEngine.getInstance().getTemplate(ih.getId()).getName()).append("</td><td width=30></td></tr>"));

			sb.append("</table></td></tr>");
			sb.append("</table></td></tr>");
			sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
		}
		
		return sb.toString();
	}

	private record ClassChangeData(String name, CategoryType category, List<ItemHolder> requiredItems, List<ItemHolder> rewardedItems) {

		public boolean hasRequiredItems() {
			return !requiredItems.isEmpty();
		}

		public boolean hasRewards() {
			return !rewardedItems.isEmpty();
		}
	}
	
	private boolean checkIfClassChangeHasOptions(Player player) {
		int amount = 0;
		for (ClassChangeData data : classChangeData) {
			if(player.isInCategory(data.category())) {
				if(data.hasRewards()) {
					amount++;
				}

				if(data.hasRequiredItems() || amount > 1) {
					return true;
				}
			}
		}
		return false;
	}
	
	private ClassChangeData getClassChangeData(int index) {
		if ((index >= 0) && (index < classChangeData.size())) {
			return classChangeData.get(index);
		}
		return null;
	}

	private class DataLoader extends GameXmlReader {

		@Override
		protected Path getSchemaFilePath() {
			return Path.of("config/xsd/class-master.xsd");
		}

		@Override
		public void load() {
			classChangeData.clear();
			parseFile("config/class-master.xml");

			LOGGER.info("Loaded {} class change options.", classChangeData.size());
			releaseResources();
		}

		@Override
		public void parseDocument(Document doc, File f) {
			var listNode = doc.getFirstChild();
			var classMasterNode = listNode.getFirstChild();
			parseClassMaster(classMasterNode);
		}

		private void parseClassMaster(Node node) {
			var attrs = node.getAttributes();
			isEnabled = parseBoolean(attrs, "enable");
			if(!isEnabled) {
				return;
			}

			spawnClassMasters = parseBoolean(attrs, "spawn");
			showPopupWindow = parseBoolean(attrs, "show-popup");

			for (var optionNode = node.getFirstChild(); optionNode != null; optionNode = optionNode.getNextSibling()) {
				parseClassChangeOption(optionNode);
			}
		}

		private void parseClassChangeOption(Node optionNode) {
			var attrs = optionNode.getAttributes();
			List<ItemHolder> requiredItems = null;
			List<ItemHolder> rewardedItems = null;

			var optionName = parseString(attrs, "name", "");
			var category = parseEnum(attrs, CategoryType.class, "apply-to");

			for (var node = optionNode.getFirstChild(); node != null; node = node.getNextSibling()) {
				if ("rewards".equals(node.getNodeName())) {
					rewardedItems = parseItems(node);
				} else if ("conditions".equals(node.getNodeName())) {
					requiredItems = parseItems(node);
				}
			}

			ClassMaster.this.classChangeData.add(new ClassChangeData(optionName, category, requiredItems, rewardedItems));
		}

		private List<ItemHolder> parseItems(Node node) {
			List<ItemHolder> items = new ArrayList<>();
			for (var itemNode = node.getFirstChild(); itemNode != null; itemNode = itemNode.getNextSibling()) {
				items.add(parseItemHolder(itemNode));
			}
			return items;
		}
	}

	public static AbstractNpcAI provider()
	{
		return new ClassMaster();
	}
}