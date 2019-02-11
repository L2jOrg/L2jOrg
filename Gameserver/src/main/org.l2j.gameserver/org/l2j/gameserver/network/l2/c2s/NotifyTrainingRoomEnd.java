package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.dao.CharacterTrainingCampDAO;
import org.l2j.gameserver.instancemanager.TrainingCampManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.TrainingCamp;

import java.nio.ByteBuffer;

public class NotifyTrainingRoomEnd extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		TrainingCamp trainingCamp = TrainingCampManager.getInstance().getTrainingCamp(activeChar);
		if(trainingCamp == null || (!trainingCamp.isTraining() && !trainingCamp.isValid(activeChar)))
		{
			activeChar.sendActionFailed();
			return;
		}

		trainingCamp.setEndTime(System.currentTimeMillis());
		CharacterTrainingCampDAO.getInstance().replace(activeChar.getAccountName(), trainingCamp);
		TrainingCampManager.getInstance().onExitTrainingCamp(activeChar);
	}
}