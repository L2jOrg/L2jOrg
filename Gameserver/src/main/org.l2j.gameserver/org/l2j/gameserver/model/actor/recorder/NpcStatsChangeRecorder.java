package org.l2j.gameserver.model.actor.recorder;

import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.NpcInfoAbnormalVisualEffect;

/**
 * @author G1ta0
 */
public class NpcStatsChangeRecorder extends CharStatsChangeRecorder<NpcInstance>
{
	public NpcStatsChangeRecorder(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onSendChanges()
	{
		super.onSendChanges();

		if((_changes & BROADCAST_CHAR_INFO) == BROADCAST_CHAR_INFO)
			_activeChar.broadcastCharInfo();

		if((_changes & SEND_ABNORMAL_INFO) == SEND_ABNORMAL_INFO || (_changes & SEND_TRANSFORMATION_INFO) == SEND_TRANSFORMATION_INFO)
			_activeChar.broadcastPacket(new NpcInfoAbnormalVisualEffect(_activeChar));
	}
}