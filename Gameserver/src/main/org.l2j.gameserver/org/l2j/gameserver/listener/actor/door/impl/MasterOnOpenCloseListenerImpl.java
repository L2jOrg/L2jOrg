package org.l2j.gameserver.listener.actor.door.impl;

import org.l2j.gameserver.listener.actor.door.OnOpenCloseListener;
import org.l2j.gameserver.model.instances.DoorInstance;

/**
* @author VISTALL
* @date 21:41/04.07.2011
*/
public class MasterOnOpenCloseListenerImpl implements OnOpenCloseListener
{
	private DoorInstance _door;

	public MasterOnOpenCloseListenerImpl(DoorInstance door)
	{
		_door = door;
	}

	@Override
	public void onOpen(DoorInstance doorInstance)
	{
		_door.openMe();
	}

	@Override
	public void onClose(DoorInstance doorInstance)
	{
		_door.closeMe();
	}
}