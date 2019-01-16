package org.l2j.gameserver.utils;

import org.l2j.gameserver.model.GameObjectsStorage;

import java.io.File;
import java.io.FileWriter;

public class OnlineTxtGenerator implements Runnable
{
	public void run()
	{
		try
		{
			File out = new File("data/webserver/online.txt");
			out.delete();
			out.createNewFile();
			FileWriter fw = new FileWriter(out);
			fw.write(String.valueOf(GameObjectsStorage.getPlayers().size()));
			fw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}