package org.l2j.scripts.handler.dailymissions;

import org.l2j.gameserver.handler.dailymissions.DailyMissionHandlerHolder;
import org.l2j.gameserver.handler.dailymissions.impl.DefaultDailyMissionHandler;
import org.l2j.gameserver.listener.script.OnLoadScriptListener;

/**
 * @author Bonux
 */
public abstract class ScriptDailyMissionHandler extends DefaultDailyMissionHandler implements OnLoadScriptListener
{
	@Override
	public void onLoad()
	{
		DailyMissionHandlerHolder.getInstance().registerHandler(this);
	}
}
