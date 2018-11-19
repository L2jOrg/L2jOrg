package l2s.gameserver.model;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.utils.Location;

public abstract class ObservableArena
{
	private final List<ObservePoint> _observers = new CopyOnWriteArrayList<ObservePoint>();

	public abstract Reflection getReflection();

	public abstract Location getObserverEnterPoint(Player player);

	public abstract boolean showObservableArenasList(Player player);

	public void onAppearObserver(ObservePoint observer)
	{

	}

	public void onAddObserver(ObservePoint observer)
	{

	}

	public void onRemoveObserver(ObservePoint observer)
	{

	}

	public void onEnterObserverArena(Player player)
	{

	}

	public void onChangeObserverArena(Player player)
	{

	}

	public void onExitObserverArena(Player player)
	{

	}

	public final List<ObservePoint> getObservers()
	{
		return _observers;
	}

	public final void addObserver(ObservePoint observer)
	{
		if(_observers.add(observer))
			onAddObserver(observer);
	}

	public final void removeObserver(ObservePoint observer)
	{
		if(_observers.remove(observer))
			onRemoveObserver(observer);
	}

	public final void clearObservers()
	{
		for(ObservePoint observer : _observers)
		{
			Player player = observer.getPlayer();
			if(player.isInObserverMode())
				player.leaveObserverMode();
		}

		_observers.clear();
	}
}