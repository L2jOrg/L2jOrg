package org.l2j.gameserver.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * Класс хранилища и валидации ссылок в html.
 *
 * @author G1ta0
 */
public class BypassStorage
{
	public static enum BypassType
	{
		DEFAULT,
		BBS,
		ITEM;

		public static final BypassType[] VALUES = values();
	}

	private static final Pattern htmlBypass = Pattern.compile("\\s+action=\"bypass\\s+(?:-h +)?([^\"]+?)\"", Pattern.CASE_INSENSITIVE);
	private static final Pattern htmlLink = Pattern.compile("\\s+action=\"link\\s+([^\"]+?\\.html?(#[0-9]+)?)\"", Pattern.CASE_INSENSITIVE);
	private static final Pattern bbsWrite = Pattern.compile("\\s+action=\"write\\s+(\\S+)\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\"", Pattern.CASE_INSENSITIVE);

	private static final Pattern directHtmlBypass = Pattern.compile("^(_mrsl|_diary|_match|manor_menu_select|learn_skill|_olympiad|pledgegame|_heroes|menu_select?|talk_select|teleport_request|deposit|withdraw|deposit_pledge|withdraw_pledge|class_change?|quest_accept?|multiclass?).*", Pattern.DOTALL);
	private static final Pattern directBbsBypass = Pattern.compile("^(_bbshome|_bbsgetfav|_bbsaddfav|_bbslink|_bbsloc|_bbsclan|_bbsmemo|_maillist|_friendlist).*", Pattern.DOTALL);

	public static class ValidBypass
	{
		public String bypass;
		public boolean args;
		public BypassType type;

		public ValidBypass(String bypass, boolean args, BypassType type)
		{
			this.bypass = bypass;
			this.args = args;
			this.type = type;
		}
	}

	private List<ValidBypass> bypasses = new CopyOnWriteArrayList<ValidBypass>();

	public void parseHtml(CharSequence html, BypassType type)
	{
		clear(type);

		if(isNullOrEmpty(html))
			return;

		Matcher m = htmlBypass.matcher(html);
		while(m.find())
		{
			String bypass = m.group(1);
			//При передаче аргументов, мы можем проверить только часть команды до первого аргумента
			int i = bypass.indexOf(" $");
			if(i > 0)
				bypass = bypass.substring(0, i);
			addBypass(new ValidBypass(bypass, i >= 0, type));
		}

		if(type == BypassType.BBS)
		{
			m = bbsWrite.matcher(html);
			while(m.find())
			{
				String bypass = m.group(1);
				addBypass(new ValidBypass(bypass, true, type));
			}
		}

		m = htmlLink.matcher(html);
		while(m.find())
		{
			String bypass = m.group(1);
			addBypass(new ValidBypass(bypass, false, type));
		}
	}

	public ValidBypass validate(String bypass)
	{
		ValidBypass ret = null;

		if(directHtmlBypass.matcher(bypass).matches())
			ret = new ValidBypass(bypass, false, BypassType.DEFAULT);
		else if(directBbsBypass.matcher(bypass).matches())
			ret = new ValidBypass(bypass, false, BypassType.BBS);
		else
		{
			boolean args = bypass.indexOf(" ") > 0;
			for(ValidBypass bp : bypasses)
			{
				if(bp.bypass.equals(bypass) || (args == bp.args && bypass.startsWith(bp.bypass + " ")))
				{
					ret = bp;
					break;
				}
			}
		}

		//if(ret != null)
		//{
		//	clear(ret.bbs);
		//}

		return ret;
	}

	private void addBypass(ValidBypass bypass)
	{
		bypasses.add(bypass);
	}

	private void clear(BypassType type)
	{
		for(ValidBypass bp : bypasses)
			if(bp.type == type)
				bypasses.remove(bp);
	}
}