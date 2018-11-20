package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.components.SystemMsg;

/**
* @author VISTALL
* @date 23/03/2011
*/
public class ConfirmDlgPacket extends SysMsgContainer<ConfirmDlgPacket>
{
	private int _time;
	private int _requestId;

	public ConfirmDlgPacket(SystemMsg msg, int time)
	{
		super(msg);
		_time = time;
	}

	@Override
	protected final void writeImpl()
	{
		writeElements();
		writeInt(_time);
		writeInt(_requestId);
	}

	public void setRequestId(int requestId)
	{
		_requestId = requestId;
	}
}