package l2s.gameserver.handler.dailymissions;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.handler.dailymissions.impl.DefaultDailyMissionHandler;
import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.actor.listener.CharListenerList;

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
			warn(getClass().getSimpleName() + ": Cannot find handler [" + handler + "]!");
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