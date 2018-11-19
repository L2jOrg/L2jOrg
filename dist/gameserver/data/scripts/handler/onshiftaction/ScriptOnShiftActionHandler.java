package handler.onshiftaction;

import l2s.gameserver.handler.onshiftaction.OnShiftActionHandler;
import l2s.gameserver.handler.onshiftaction.OnShiftActionHolder;
import l2s.gameserver.listener.script.OnInitScriptListener;

/**
 * @author VISTALL
 * @date 2:42/19.08.2011
 */
public abstract class ScriptOnShiftActionHandler<T> implements OnInitScriptListener, OnShiftActionHandler<T>
{
	@Override
	public void onInit()
	{
		OnShiftActionHolder.getInstance().register(getClazz(), this);
	}

	public abstract Class<T> getClazz();
}
