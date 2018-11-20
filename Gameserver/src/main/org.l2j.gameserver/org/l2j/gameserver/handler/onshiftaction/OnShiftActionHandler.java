package org.l2j.gameserver.handler.onshiftaction;

import org.l2j.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 2:38/19.08.2011
 */
public interface OnShiftActionHandler<T>
{
	boolean call(T t, Player player);
}