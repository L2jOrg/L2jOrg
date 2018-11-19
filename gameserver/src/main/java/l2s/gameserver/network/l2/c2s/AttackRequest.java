package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;

public class AttackRequest extends L2GameClientPacket
{
	// cddddc
	private int _objectId;
	private int _originX;
	private int _originY;
	private int _originZ;
	private int _attackId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		_attackId = readC(); // 0 for simple click   1 for shift-click
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setActive();

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getPlayerAccess().CanAttack)
		{
			activeChar.sendActionFailed();
			return;
		}

		GameObject target = activeChar.getVisibleObject(_objectId);
		if(target == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if(!(target instanceof Creature))
		{
			activeChar.sendActionFailed();
			return;		
		}
		
		if(activeChar.getAggressionTarget() != null && activeChar.getAggressionTarget() != target && !activeChar.getAggressionTarget().isDead())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(target.isPlayer() && (activeChar.isInBoat() || target.isInBoat()))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getTarget() != target)
		{
			target.onAction(activeChar, _attackId == 1);
			return;
		}

		if(target.getObjectId() != activeChar.getObjectId() && !activeChar.isInStoreMode() && !activeChar.isProcessingRequest())
			activeChar.getAI().Attack(target, true, _attackId == 1);
	}
}