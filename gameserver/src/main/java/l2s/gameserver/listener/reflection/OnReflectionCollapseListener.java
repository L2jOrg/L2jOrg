package l2s.gameserver.listener.reflection;

import l2s.commons.listener.Listener;
import l2s.gameserver.model.entity.Reflection;

public interface OnReflectionCollapseListener extends Listener<Reflection>
{
	public void onReflectionCollapse(Reflection reflection);
}