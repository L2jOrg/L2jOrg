package l2s.gameserver.utils;

import java.util.Map;

import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.model.*;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.components.*;
import l2s.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2s.gameserver.network.l2.s2c.ExUnReadMailCount;
import l2s.gameserver.network.l2.s2c.NSPacket;

/**
 * @Author: Diamond
 * @Date: 7/6/2007
 * @Time: 5:22:23
 */
public class Functions
{
	/**
	 * Вызывать только из скриптов
	 */
	public static void show(String text, Player player)
	{
		show(text, player, 0);
	}

	public static void show(CustomMessage message, Player player)
	{
		show(message.toString(player), player, 0);
	}

	public static void show(String text, Player player, int itemId, Object... arg)
	{
		if(text == null || player == null)
			return;

		HtmlMessage msg = new HtmlMessage(5);

		// приводим нашу html-ку в нужный вид
		if(text.endsWith(".html") || text.endsWith(".htm"))
			msg.setFile(text);
		else
			msg.setHtml(HtmlUtils.bbParse(text));

		if(arg != null && arg.length % 2 == 0)
			for(int i = 0; i < arg.length; i = +2)
				msg.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));

		msg.setItemId(itemId);
		player.sendPacket(msg);
	}

	/**
	 * Статический метод, для вызова из любых мест
	 */
	public static void show(String text, Player player, NpcInstance npc, Object... arg)
	{
		if(text == null || player == null)
			return;

		HtmlMessage msg = new HtmlMessage(npc);

		// приводим нашу html-ку в нужный вид
		if(text.endsWith(".html") || text.endsWith(".htm"))
			msg.setFile(text);
		else
			msg.setHtml(HtmlUtils.bbParse(text));

		if(arg != null && arg.length % 2 == 0)
			for(int i = 0; i < arg.length; i = +2)
				msg.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));

		player.sendPacket(msg);
	}

	// Белый чат
	public static void npcSayInRange(NpcInstance npc, String text, int range)
	{
		npcSayInRange(npc, range, NpcString.NONE, text);
	}

	// Белый чат
	public static void npcSayInRange(NpcInstance npc, int range, NpcString fStringId, String... params)
	{
		ChatUtils.say(npc, range, new NSPacket(npc, ChatType.NPC_ALL, fStringId, params));
	}

	// Белый чат
	public static void npcSay(NpcInstance npc, String text)
	{
		npcSayInRange(npc, text, 1500);
	}

	// Белый чат
	public static void npcSay(NpcInstance npc, NpcString npcString, String... params)
	{
		npcSayInRange(npc, 1500, npcString, params);
	}

	// Белый чат
	public static void npcSay(NpcInstance npc, Player player, NpcString npcString, String... params)
	{
		player.sendPacket(new NSPacket(npc, ChatType.NPC_ALL, npcString, params));
	}

	// Белый чат
	public static void npcSayInRangeCustomMessage(NpcInstance npc, int range, String address, Object... replacements)
	{
		CustomMessage cm = new CustomMessage(address);
		for(Object replacement : replacements)
			cm.addString(String.valueOf(replacement));

		ChatUtils.say(npc, range, cm);
	}

	// Белый чат
	public static void npcSayCustomMessage(NpcInstance npc, String address, Object... replacements)
	{
		npcSayInRangeCustomMessage(npc, 1500, address, replacements);
	}

	// private message
	public static void npcSayToPlayer(NpcInstance npc, Player player, String text)
	{
		npcSayToPlayer(npc, player, NpcString.NONE, text);
	}

	// private message
	public static void npcSayToPlayer(NpcInstance npc, Player player, NpcString npcString, String... params)
	{
		player.sendPacket(new NSPacket(npc, ChatType.TELL, npcString, params));
	}

	// Shout (желтый) чат
	public static void npcShout(NpcInstance npc, String text)
	{
		npcShout(npc, NpcString.NONE, text);
	}

	// Shout (желтый) чат
	public static void npcShout(NpcInstance npc, NpcString npcString, String... params)
	{
		ChatUtils.shout(npc, npcString, params);
	}

	// Shout (желтый) чат
	public static void npcShoutCustomMessage(NpcInstance npc, String address, Object... replacements)
	{
		CustomMessage cm = new CustomMessage(address);
		for(Object replacement : replacements)
			cm.addString(String.valueOf(replacement));

		ChatUtils.shout(npc, cm);
	}

	public static void npcSay(NpcInstance npc, NpcString address, ChatType type, int range, String... replacements)
	{
		ChatUtils.say(npc, range, new NSPacket(npc, type, address, replacements));
	}

	public static boolean ride(Player player, int npcId)
	{
		if(player.hasServitor())
		{
			player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
			return false;
		}

		player.setMount(0, npcId, player.getLevel(), -1);
		return true;
	}

	public static void unRide(Player player)
	{
		if(player.isMounted())
			player.setMount(null);
	}

	public static void unSummonPet(Player player, boolean onlyPets)
	{
		for(Servitor servitor : player.getServitors())
		{
			if(!onlyPets || servitor.isPet())
				servitor.unSummon(false);
		}
	}

	public static boolean IsActive(String name)
	{
		return ServerVariables.getString(name, "off").equalsIgnoreCase("on");
	}

	public static boolean SetActive(String name, boolean active)
	{
		if(active == IsActive(name))
			return false;
		if(active)
			ServerVariables.set(name, "on");
		else
			ServerVariables.unset(name);
		return true;
	}

	public static boolean SimpleCheckDrop(Creature mob, Creature killer)
	{
		return mob != null && mob.isMonster() && !mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 9;
	}
	
	public static void sendDebugMessage(Player player, String message)
	{
		if(!player.isGM())
			return;
		player.sendMessage(message);
	}

	public static void sendSystemMail(Player receiver, String title, String body, Map<Integer, Long> items)
	{
		if(receiver == null || !receiver.isOnline())
			return;
		if(title == null)
			return;
		if(items.keySet().size() > 8)
			return;

		Mail mail = new Mail();
		mail.setSenderId(1);
		mail.setSenderName("Admin");
		mail.setReceiverId(receiver.getObjectId());
		mail.setReceiverName(receiver.getName());
		mail.setTopic(title);
		mail.setBody(body);
		for(Map.Entry<Integer, Long> itm : items.entrySet())
		{
			ItemInstance item = ItemFunctions.createItem(itm.getKey());
			item.setLocation(ItemInstance.ItemLocation.MAIL);
			item.setCount(itm.getValue());
			item.save();
			mail.addAttachment(item);
		}
		mail.setType(Mail.SenderType.NEWS_INFORMER);
		mail.setUnread(true);
		mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
		mail.save();

		receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
		receiver.sendPacket(new ExUnReadMailCount(receiver));
		receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
	}
}