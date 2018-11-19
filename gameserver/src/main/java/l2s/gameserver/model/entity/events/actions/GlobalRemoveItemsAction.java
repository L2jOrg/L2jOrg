package l2s.gameserver.model.entity.events.actions;

import l2s.gameserver.dao.ItemsDAO;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.objects.ItemObject;

public class GlobalRemoveItemsAction implements EventAction
{
	private final String _name;

	public GlobalRemoveItemsAction(String name)
	{
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		for(Object o : event.getObjects(_name))
		{
			if(o instanceof ItemObject)
				ItemsDAO.getInstance().glovalRemoveItem(((ItemObject) o).getItemId(), "Remove items by event: " + event.getName());
		}
	}
}