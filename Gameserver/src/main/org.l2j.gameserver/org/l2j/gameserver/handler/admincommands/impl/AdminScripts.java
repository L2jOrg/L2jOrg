package org.l2j.gameserver.handler.admincommands.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.handler.admincommands.IAdminCommandHandler;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.scripts.Scripts;
import org.l2j.gameserver.settings.ServerSettings;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class AdminScripts implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_run_script,
		admin_runs,
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanReload)
			return false;

		switch(command)
		{
			case admin_run_script:
			case admin_runs:
				if(wordList.length < 2)
					return false;

				String param = wordList[1];
				if (!run(param))
					activeChar.sendMessage("Can't run script.");
				else
					activeChar.sendMessage("Running script...");
				break;
		}
		return true;
	}

	private boolean run(String target) {
		var filePath = getSettings(ServerSettings.class).dataPackRootPath().resolve(String.format("data/scripts/%s.java",target.replace(".", "/")));
		if(Files.notExists(filePath)) {
			return false;
		}

		List<Class<?>> classes = Scripts.getInstance().loadScriptsFromFile(filePath);
		for(Class<?> clazz : classes)
		{

			if(!Runnable.class.isAssignableFrom(clazz))
				return false;

			Runnable r;
			try
			{
				r = (Runnable) clazz.getDeclaredConstructor().newInstance();
			}
			catch(Exception e)
			{
				return false;
			}

			ThreadPoolManager.getInstance().execute(r);
			return true;
		}

		return false;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}