package org.l2j.scripts.handler.items;

import org.l2j.gameserver.handler.items.ItemHandler;
import org.l2j.gameserver.handler.items.impl.DefaultItemHandler;
import org.l2j.gameserver.listener.script.OnLoadScriptListener;

/**
 * @author Bonux
 */
public abstract class ScriptItemHandler extends DefaultItemHandler implements OnLoadScriptListener
{
	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}
}
