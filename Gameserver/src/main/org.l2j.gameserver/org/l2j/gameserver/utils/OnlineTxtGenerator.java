package org.l2j.gameserver.utils;

import java.io.File;
import java.io.FileWriter;

import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.tables.FakePlayersTable;

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
			fw.write(String.valueOf(GameObjectsStorage.getPlayers().size() + FakePlayersTable.getActiveFakePlayersCount()));
			fw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}