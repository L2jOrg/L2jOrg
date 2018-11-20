package handler.onshiftaction;

import org.l2j.gameserver.handler.onshiftaction.OnShiftActionHandler;
import org.l2j.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.l2j.gameserver.listener.script.OnInitScriptListener;

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
