package org.l2j.gameserver.listener.reflection;

import org.l2j.commons.listener.Listener;
import org.l2j.gameserver.model.entity.Reflection;

public interface OnReflectionCollapseListener extends Listener<Reflection>
{
	public void onReflectionCollapse(Reflection reflection);
}