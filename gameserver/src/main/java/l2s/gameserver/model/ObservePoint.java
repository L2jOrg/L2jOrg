package l2s.gameserver.model;

import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.utils.Log;

/**
 * Псевдо-объект, представляющий собой точку просмотра игрока.
 *
 * @author G1ta0
 */
public class ObservePoint extends Creature
{
	private final Player _player;

	public ObservePoint(Player player)
	{
		super(player.getObjectId(), player.getTemplate());
		_player = player;
		setFlying(true);
	}

	@Override
	public Player getPlayer()
	{
		return _player;
	}

	@Override
	public int getLevel()
	{
		return 1;
	}

	@Override
	public int getMoveSpeed()
	{
		return 400; // estimated
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		if(getReflection().isMain() && !isMovementDisabled() && !getPlayer().getPlayerAccess().CanUseMovingObservationPoint)
			Log.add("Movable observation point from player " + getPlayer().toString() + " somehow appeared in main world !", "warning");
	}

	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}

	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}

	@Override
	public WeaponTemplate getActiveWeaponTemplate()
	{
		return null;
	}

	@Override
	public WeaponTemplate getSecondaryWeaponTemplate()
	{
		return null;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public List<L2GameServerPacket> deletePacketList(Player forPlayer)
	{
		return Collections.emptyList();
	}

	@Override
	public boolean isObservePoint()
	{
		return true;
	}

	@Override
	public boolean isInvulnerable()
	{
		return true;
	}

	@Override
	public boolean isHealBlocked()
	{
		return true;
	}

	@Override
	public boolean isEffectImmune(Creature effector)
	{
		return true;
	}

	@Override
	public void sendPacket(IBroadcastPacket p)
	{
		_player.sendPacket(p);
	}

	@Override
	public void sendPacket(IBroadcastPacket... packets)
	{
		_player.sendPacket(packets);
	}

	@Override
	public void sendPacket(List<? extends IBroadcastPacket> packets)
	{
		_player.sendPacket(packets);
	}

	@Override
	public void broadcastMove()
	{
		_player.sendPacket(movePacket());
	}

	@Override
	public void broadcastStopMove()
	{
		_player.sendPacket(stopMovePacket());
	}

	@Override
	public void validateLocation(int broadcast)
	{

	}

	@Override
	public boolean isCreature()
	{
		return false;
	}
}