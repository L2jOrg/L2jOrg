package handler.items;

import l2s.gameserver.handler.items.ItemHandler;
import l2s.gameserver.handler.items.impl.DefaultItemHandler;
import l2s.gameserver.listener.script.OnLoadScriptListener;

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
