package l2s.gameserver.model.actor.flags;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.flags.flag.DefaultFlag;
import l2s.gameserver.model.actor.flags.flag.UndyingFlag;

public class CreatureFlags
{
	private final Creature _owner;
	private final DefaultFlag _afraid = new DefaultFlag();
	private final DefaultFlag _muted = new DefaultFlag();
	private final DefaultFlag _pmuted = new DefaultFlag();
	private final DefaultFlag _amuted = new DefaultFlag();
	private final DefaultFlag _paralyzed = new DefaultFlag();
	private final DefaultFlag _moveBlocked = new DefaultFlag();
	private final DefaultFlag _sleeping = new DefaultFlag();
	private final DefaultFlag _stunned = new DefaultFlag();
	private final DefaultFlag _immobilized = new DefaultFlag();
	private final DefaultFlag _confused = new DefaultFlag();
	private final DefaultFlag _frozen = new DefaultFlag();
	private final DefaultFlag _knockDowned = new DefaultFlag();
	private final DefaultFlag _knockBacked = new DefaultFlag();
	private final DefaultFlag _flyUp = new DefaultFlag();
	private final DefaultFlag _healBlocked = new DefaultFlag();
	private final DefaultFlag _damageBlocked = new DefaultFlag();
	private final DefaultFlag _buffImmunity = new DefaultFlag();
	private final DefaultFlag _debuffImmunity = new DefaultFlag();
	private final DefaultFlag _effectImmunity = new DefaultFlag();
	private final DefaultFlag _deathImmunity = new DefaultFlag();
	private final DefaultFlag _distortedSpace = new DefaultFlag();
	private final DefaultFlag _invisible = new DefaultFlag();
	private final DefaultFlag _invulnerable = new DefaultFlag();
	private final DefaultFlag _weaponEquipBlocked = new DefaultFlag();
	private final UndyingFlag _undying = new UndyingFlag();

	public CreatureFlags(Creature owner)
	{
		_owner = owner;
	}

	public DefaultFlag getAfraid()
	{
		return _afraid;
	}

	public DefaultFlag getMuted()
	{
		return _muted;
	}

	public DefaultFlag getPMuted()
	{
		return _pmuted;
	}

	public DefaultFlag getAMuted()
	{
		return _amuted;
	}

	public DefaultFlag getParalyzed()
	{
		return _paralyzed;
	}

	public DefaultFlag getMoveBlocked()
	{
		return _moveBlocked;
	}

	public DefaultFlag getSleeping()
	{
		return _sleeping;
	}

	public DefaultFlag getStunned()
	{
		return _stunned;
	}

	public DefaultFlag getImmobilized()
	{
		return _immobilized;
	}

	public DefaultFlag getConfused()
	{
		return _confused;
	}

	public DefaultFlag getFrozen()
	{
		return _frozen;
	}

	public DefaultFlag getKnockDowned()
	{
		return _knockDowned;
	}

	public DefaultFlag getKnockBacked()
	{
		return _knockBacked;
	}

	public DefaultFlag getFlyUp()
	{
		return _flyUp;
	}

	public DefaultFlag getHealBlocked()
	{
		return _healBlocked;
	}

	public DefaultFlag getDamageBlocked()
	{
		return _damageBlocked;
	}

	public DefaultFlag getBuffImmunity()
	{
		return _buffImmunity;
	}

	public DefaultFlag getDebuffImmunity()
	{
		return _debuffImmunity;
	}

	public DefaultFlag getEffectImmunity()
	{
		return _effectImmunity;
	}

	public DefaultFlag getDeathImmunity()
	{
		return _deathImmunity;
	}

	public DefaultFlag getDistortedSpace()
	{
		return _distortedSpace;
	}

	public DefaultFlag getInvisible()
	{
		return _invisible;
	}

	public DefaultFlag getInvulnerable()
	{
		return _invulnerable;
	}

	public DefaultFlag getWeaponEquipBlocked()
	{
		return _weaponEquipBlocked;
	}

	public UndyingFlag getUndying()
	{
		return _undying;
	}
}