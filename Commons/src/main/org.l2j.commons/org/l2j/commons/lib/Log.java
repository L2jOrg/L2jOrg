/**
 coded by Balancer
 balancer@balancer.ru
 http://balancer.ru

 version 0.1, 2005-06-06
 */
package org.l2j.commons.lib;

import org.l2j.commons.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
	private static final Logger _log = LoggerFactory.getLogger(Log.class);
	
	public static final void add(String text, String cat)
	{
		/*
		 * Logger _log = logs.get(cat); if(_log == null) { _log = LoggerFactory.getLogger(cat); logs.put(cat, _log); }
		 */
		
		String date = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date());
		
		new File("log/game").mkdirs();
		
		try
		{
			File file = new File("log/game/" + (cat != null ? cat : "_all") + ".txt");
			// file.getAbsolutePath().mkdirs();
			FileWriter save = new FileWriter(file, true);
			String out = "[" + date + "] '---': " + text + "\n"; // "+char_name()+"
			save.write(out);
			save.flush();
			save.close();
			save = null;
			file = null;
		}
		catch (IOException e)
		{
			_log.warn("saving chat log failed: " + e);
			e.printStackTrace();
		}
		
		if (cat != null)
		{
			add(text, null);
		}
	}
	
	@Deprecated
	public static final void Assert(boolean exp)
	{
		Assert(exp, "");
	}
	
	public static final void Assert(boolean exp, String cmt)
	{
		if (exp || !Config.ASSERT)
		{
			return;
		}
		
		System.out.println("Assertion error [" + cmt + "]");
		Thread.dumpStack();
	}
}
