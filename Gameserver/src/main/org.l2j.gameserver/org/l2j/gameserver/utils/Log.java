package org.l2j.gameserver.utils;

import org.l2j.commons.text.PrintfFormat;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log
{
	public static final PrintfFormat LOG_BOSS_KILLED = new PrintfFormat("%s: %s[%d] killed by %s at Loc(%d %d %d) in %s");
	public static final PrintfFormat LOG_BOSS_RESPAWN = new PrintfFormat("%s: %s[%d] scheduled for respawn in %s at %s");

	private static final Logger _log = LoggerFactory.getLogger(Log.class);

	private static final Logger _logChat = LoggerFactory.getLogger("chat");
	private static final Logger _logGm = LoggerFactory.getLogger("gmactions");
	private static final Logger _logItems = LoggerFactory.getLogger("item");
	private static final Logger _logGame = LoggerFactory.getLogger("game");
	private static final Logger _logDebug = LoggerFactory.getLogger("debug");
	private static final Logger _logMultisell = LoggerFactory.getLogger("multisell");

	public static final String Create = "Create";
	public static final String Delete = "Delete";
	public static final String Drop = "Drop";
	public static final String PvPDrop = "PvPDrop";
	public static final String Crystalize = "Crystalize";
	public static final String EnchantFail = "EnchantFail";
	public static final String Pickup = "Pickup";
	public static final String PartyPickup = "PartyPickup";
	public static final String PrivateStoreBuy = "PrivateStoreBuy";
	public static final String PrivateStoreSell = "PrivateStoreSell";
	public static final String TradeBuy = "TradeBuy";
	public static final String TradeSell = "TradeSell";
	public static final String PostRecieve = "PostRecieve";
	public static final String SafePostRecieve = "SafePostRecieve";
	public static final String PostPaymentRecieve = "PostPaymentRecieve";
	public static final String PostSend = "PostSend";
	public static final String PostCancel = "PostCancel";
	public static final String PostExpire = "PostExpire";
	public static final String RefundSell = "RefundSell";
	public static final String RefundReturn = "RefundReturn";
	public static final String WarehouseDeposit = "WarehouseDeposit";
	public static final String WarehouseWithdraw = "WarehouseWithdraw";
	public static final String FreightWithdraw = "FreightWithdraw";
	public static final String FreightDeposit = "FreightDeposit";
	public static final String ClanWarehouseDeposit = "ClanWarehouseDeposit";
	public static final String ClanWarehouseWithdraw = "ClanWarehouseWithdraw";
	public static final String CommissionBuy = "CommissionBuy";
	public static final String CommissionSell = "CommissionSell";
	public static final String CommissionRegistered = "CommissionRegistered";
	public static final String CommissionUnregister = "CommissionUnregister";
	public static final String CommissionExpiredReturn = "CommissionExpiredReturn";
	public static final String ClanChangeLeaderRequestAdd = "ClanChangeLeaderRequestAdd";
	public static final String ClanChangeLeaderRequestDone = "ClanChangeLeaderRequestDone";
	public static final String ClanChangeLeaderRequestCancel = "ClanChangeLeaderRequestCancel";
	public static final String ItemMallBuy = "ItemMallBuy";
	public static final String DelayedItemReceive = "DelayedItemReceive";

	public static void add(PrintfFormat fmt, Object[] o, String cat)
	{
		add(fmt.sprintf(o), cat);
	}

	public static void add(String fmt, Object[] o, String cat)
	{
		add(new PrintfFormat(fmt).sprintf(o), cat);
	}

	public static void add(String text, String cat, Player player)
	{
		if(!Config.TURN_LOG_SYSTEM)
			return;
			
		StringBuilder output = new StringBuilder();

		output.append(cat);
		if(player != null)
		{
			output.append(' ');
			output.append(player);
		}
		output.append(' ');
		output.append(text);

		_logGame.info(output.toString());
	}

	public static void add(String text, String cat)
	{
		add(text, cat, null);
	}

	public static void debug(String text)
	{
		_logDebug.debug(text);
	}

	public static void debug(String text, Throwable t)
	{
		_logDebug.debug(text, t);
	}

	public static void LogChat(String type, String player, String target, String text)
	{
		if(!Config.LOG_CHAT)
			return;

		StringBuilder output = new StringBuilder();
		output.append(type);
		output.append(' ');
		output.append('[');
		output.append(player);
		if(target != null)
		{
			output.append(" -> ");
			output.append(target);
		}
		output.append(']');
		output.append(' ');
		output.append(text);

		_logChat.info(output.toString());
	}

	public static void LogCommand(Player player, GameObject target, String command, boolean success)
	{
		//if(!Config.LOG_GM)
		//	return;

		StringBuilder output = new StringBuilder();

		if(success)
			output.append("SUCCESS");
		else
			output.append("FAIL   ");

		output.append(' ');
		output.append(player);
		if(target != null)
		{
			output.append(" -> ");
			output.append(target);
		}
		output.append(' ');
		output.append(command);

		_logGm.info(output.toString());
	}
	
	public static void LogMultisell(String log)
	{
		_logMultisell.info(log);
	}

	public static void LogItem(String className, int objId, String process, ItemInstance item)
	{
		LogItem(className, objId, process, item, item.getCount());
	}

	public static void LogItem(String className, int objId, String process, ItemInstance item, long count)
	{
		if(!Config.TURN_LOG_SYSTEM)
			return;

		StringBuilder output = new StringBuilder();
		output.append(process);
		output.append(' ');
		output.append(item);
		output.append(' ');
		output.append(className);
		output.append('[');
		output.append(objId);
		output.append(']');
		output.append(' ');
		output.append(count);

		_logItems.info(output.toString());
	}

	public static void LogItem(Creature activeChar, String process, ItemInstance item)
	{
		LogItem(activeChar, process, item, item.getCount());
	}

	public static void LogItem(Creature activeChar, String process, ItemInstance item, long count)
	{
		LogItem(activeChar, process, item, count, "");
	}

	public static void LogItem(Creature activeChar, String process, ItemInstance item, String desc)
	{
		LogItem(activeChar, process, item, item.getCount(), desc);
	}

	public static void LogItem(Creature activeChar, String process, ItemInstance item, long count, String desc)
	{
		if(!Config.TURN_LOG_SYSTEM)
			return;

		StringBuilder output = new StringBuilder();
		output.append(process);
		output.append(' ');
		output.append(item);
		output.append(' ');
		output.append(activeChar);
		output.append(' ');
		output.append(count);
		output.append(' ');
		output.append(desc);

		_logItems.info(output.toString());
	}

	public static void LogItem(Creature activeChar, String process, int item, long count)
	{
		if(!Config.TURN_LOG_SYSTEM)
			return;

		StringBuilder output = new StringBuilder();
		output.append(process);
		output.append(' ');
		output.append(item);
		output.append(' ');
		output.append(activeChar);
		output.append(' ');
		output.append(count);

		_logItems.info(output.toString());
	}

	public static void LogPetition(Player fromChar, Integer Petition_type, String Petition_text)
	{
		//TODO: implement
	}

	public static void LogAudit(Player player, String type, String msg)
	{
		//TODO: implement
	}
}