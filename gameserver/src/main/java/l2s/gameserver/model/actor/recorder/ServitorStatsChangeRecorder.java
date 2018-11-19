package l2s.gameserver.model.actor.recorder;

import l2s.gameserver.model.Servitor;

/**
 * @author G1ta0
 */
public class ServitorStatsChangeRecorder extends CharStatsChangeRecorder<Servitor>
{
	public ServitorStatsChangeRecorder(Servitor actor)
	{
		super(actor);
	}

	@Override
	protected void onSendChanges()
	{
		super.onSendChanges();

		if((_changes & SEND_CHAR_INFO) == SEND_CHAR_INFO)
			_activeChar.sendPetInfo();
		else if((_changes & BROADCAST_CHAR_INFO) == BROADCAST_CHAR_INFO || (_changes & SEND_ABNORMAL_INFO) == SEND_ABNORMAL_INFO || (_changes & SEND_TRANSFORMATION_INFO) == SEND_TRANSFORMATION_INFO)
			_activeChar.broadcastCharInfo();
	}
}
