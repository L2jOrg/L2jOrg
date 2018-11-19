package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestVoteNew extends L2GameClientPacket
{
	private int _targetObjectId;

	@Override
	protected void readImpl()
	{
		_targetObjectId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(!activeChar.getPlayerAccess().CanEvaluate)
			return;

		GameObject target = activeChar.getTarget();
		if(target == null || !target.isPlayer() || target.getObjectId() != _targetObjectId)
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return;
		}

		if(target.getObjectId() == activeChar.getObjectId())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECOMMEND_YOURSELF);
			return;
		}

		Player targetPlayer = (Player) target;

		if(activeChar.getRecomLeft() <= 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_MAKE_FURTHER_RECOMMENDATIONS_AT_THIS_TIME);
			return;
		}

		if(targetPlayer.getRecomHave() >= 255)
		{
			activeChar.sendPacket(SystemMsg.YOUR_SELECTED_TARGET_CAN_NO_LONGER_RECEIVE_A_RECOMMENDATION);
			return;
		}

		activeChar.giveRecom(targetPlayer);
		SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT);
		sm.addName(target);
		sm.addInteger(activeChar.getRecomLeft());
		activeChar.sendPacket(sm);

		sm = new SystemMessagePacket(SystemMsg.YOU_HAVE_BEEN_RECOMMENDED_BY_C1);
		sm.addName(activeChar);
		targetPlayer.sendPacket(sm);
	}
}