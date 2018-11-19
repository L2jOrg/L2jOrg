package handler.dailymissions;

import l2s.gameserver.handler.dailymissions.DailyMissionHandlerHolder;
import l2s.gameserver.handler.dailymissions.impl.DefaultDailyMissionHandler;
import l2s.gameserver.listener.script.OnLoadScriptListener;

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
