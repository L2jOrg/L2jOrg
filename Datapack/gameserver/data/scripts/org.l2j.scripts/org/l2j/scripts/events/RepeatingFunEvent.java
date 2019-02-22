package org.l2j.scripts.events;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.entity.events.actions.StartStopAction;
import io.github.joealisson.primitive.pair.IntObjectPair;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.TreeIntObjectMap;

import java.util.ArrayList;
import java.util.List;

public class RepeatingFunEvent extends FunEvent
{
	private final SchedulingPattern _repeatPattern;
	private final int _random;
	private IntObjectMap<List<EventAction>> _storedOnTimeActions = null;

	public RepeatingFunEvent(MultiValueSet<String> set)
	{
		super(set);

		final String repeat = set.getString("repeat_time_pattern", null);
		_repeatPattern = repeat != null ? new SchedulingPattern(repeat) : null;
		_random = set.getInteger("random", 0);
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		final long stopTime = _stopPattern.getTimeInMillis();
		final long currentTime = System.currentTimeMillis();
		if (currentTime >= stopTime) // event already finished ?
			return;

		long startTime = _startPattern.getTimeInMillis();
		if (startTime < currentTime && _repeatPattern != null) // event already started
			startTime = _repeatPattern.next(currentTime + 60000L);

		if (_random > 0)
			startTime += Rnd.get(-_random, _random) * 1000L;
		if (startTime <= currentTime)
			startTime = currentTime + 60000L;

		final long dff = stopTime - startTime;
		if (dff <= 0) // next start time after event finish
			return;

		if (onInit)
		{
			if (!_onTimeActions.isEmpty())
			{
				_storedOnTimeActions = new TreeIntObjectMap<List<EventAction>>();
				for (IntObjectPair<List<EventAction>> actionList : _onTimeActions.entrySet())
				{
					if (actionList.getValue() != null && !actionList.getValue().isEmpty())
					{
						List<EventAction> newList = new ArrayList<EventAction>(actionList.getValue().size());
						for (EventAction action : actionList.getValue())
							newList.add(action);
						_storedOnTimeActions.put(actionList.getKey(), newList);
					}
				}					
			}
		}
		else
		{
			clearActions();
			_onTimeActions.clear();

			if (_storedOnTimeActions != null)
				for (IntObjectPair<List<EventAction>> actionList : _storedOnTimeActions.entrySet())
				{
					List<EventAction> newList = new ArrayList<EventAction>(actionList.getValue().size());
					for (EventAction action : actionList.getValue())
						newList.add(action);
					_onTimeActions.put(actionList.getKey(), newList);
				}				
		}

		addOnTimeAction(0, new StartStopAction(StartStopAction.EVENT, true));
		addOnTimeAction((int)(dff / 1000L), new StartStopAction(StartStopAction.EVENT, false));

		_startTime = startTime;
		registerActions();
	}
}