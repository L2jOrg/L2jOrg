package l2s.gameserver.listener.actor.door;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.instances.DoorInstance;

/**
 * @author VISTALL
 * @date 21:03/04.07.2011
 */
public interface OnOpenCloseListener extends CharListener
{
	void onOpen(DoorInstance doorInstance);

	void onClose(DoorInstance doorInstance);
}