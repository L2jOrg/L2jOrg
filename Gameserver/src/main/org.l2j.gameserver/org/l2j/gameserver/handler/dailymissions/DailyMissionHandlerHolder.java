package org.l2j.gameserver.handler.dailymissions;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.handler.dailymissions.impl.DefaultDailyMissionHandler;
import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.actor.listener.CharListenerList;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bonux
 **/
public class DailyMissionHandlerHolder extends AbstractHolder
{
	private static final IDailyMissionHandler DEFAULT_DAILY_MISSION_HANDLER = new DefaultDailyMissionHandler();

	private static final DailyMissionHandlerHolder _instance = new DailyMissionHandlerHolder();

	private final Map<String, IDailyMissionHandler> _handlers = new HashMap<String, IDailyMissionHandler>();

	public static DailyMissionHandlerHolder getInstance()
	{
		return _instance;
	}

	private DailyMissionHandlerHolder()
	{
		registerHandler(DEFAULT_DAILY_MISSION_HANDLER);
	}

	public void registerHandler(IDailyMissionHandler handler)
	{
		CharListener listener = handler.getListener();
		if(listener != null)
			CharListenerList.addGlobal(listener);

		_handlers.put(handler.getClass().getSimpleName().replace("DailyMissionHandler", ""), handler);
	}

	public IDailyMissionHandler getHandler(String handler)
	{
		if(handler.contains("DailyMissionHandler"))
			handler = handler.replace("DailyMissionHandler", "");

		if(_handlers.isEmpty() || !_handlers.containsKey(handler))
		{
			logger.warn(getClass().getSimpleName() + ": Cannot find handler [" + handler + "]!");
			return DEFAULT_DAILY_MISSION_HANDLER;
		}

		return _handlers.get(handler);
	}

	@Override
	public int size()
	{
		return _handlers.size();
	}

	@Override
	public void clear()
	{
		_handlers.clear();
	}
}