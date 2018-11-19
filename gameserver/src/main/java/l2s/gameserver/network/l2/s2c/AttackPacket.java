package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;

public class AttackPacket extends L2GameServerPacket
{
	/*
	 * TODO: Aweking
	 * 0x00 >> обычный удар 
	 * 0x02 >> увернулся 
	 * 0x04 >> крит. удар 
	 * 0x06 >> заблокирован удар 
	 * 0x08 >> удар с соской 

	 * 0x0a >> обычный удар с соской 
	 * 0x0b >> промах 
	 * 0x0c >> критический удар с соской 
	 * 0x0d >> большая надпись, удара нет 
	 * 0x0e >> тоже, что и 0x0a, но есть большая надпись 
	 */
	public static final int HITFLAG_MISS = 0x01;
	public static final int HITFLAG_SHLD = 0x02;
	public static final int HITFLAG_CRIT = 0x04;
	public static final int HITFLAG_USESS = 0x08;

	private class Hit
	{
		int _targetId, _damage, _flags;

		Hit(GameObject target, int damage, boolean miss, boolean crit, boolean shld)
		{
			_targetId = target.getObjectId();
			_damage = damage;

			if(miss)
			{
				_flags = HITFLAG_MISS;
				return;
			}

			if(_soulshot)
				_flags = HITFLAG_USESS;

			if(crit)
				_flags |= HITFLAG_CRIT;

			if(shld)
				_flags |= HITFLAG_SHLD;
		}
	}

	public final int _attackerId;
	public final boolean _soulshot;
	private final int _grade;
	private final int _x, _y, _z, _tx, _ty, _tz;
	private Hit[] hits;
	private final int _addShotEffect;

	public AttackPacket(Creature attacker, Creature target, boolean ss, int grade)
	{
		_attackerId = attacker.getObjectId();
		_soulshot = ss;
		_grade = grade;
		_addShotEffect = attacker.getAdditionalVisualSSEffect();
		_x = attacker.getX();
		_y = attacker.getY();
		_z = attacker.getZ();
		_tx = target.getX();
		_ty = target.getY();
		_tz = target.getZ();
		hits = new Hit[0];
	}

	/**
	 * Add this hit (target, damage, miss, critical, shield) to the Server-Client packet Attack.<BR><BR>
	 */
	public void addHit(GameObject target, int damage, boolean miss, boolean crit, boolean shld)
	{
		// Get the last position in the hits table
		int pos = hits.length;

		// Create a new Hit object
		Hit[] tmp = new Hit[pos + 1];

		// Add the new Hit object to hits table
		System.arraycopy(hits, 0, tmp, 0, hits.length);
		tmp[pos] = new Hit(target, damage, miss, crit, shld);
		hits = tmp;
	}

	/**
	 * Return True if the Server-Client packet Attack conatins at least 1 hit.<BR><BR>
	 */
	public boolean hasHits()
	{
		return hits.length > 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_attackerId);
		writeD(hits[0]._targetId);
		writeD(_soulshot ? _addShotEffect : 0x00);
		writeD(hits[0]._damage);
		writeD(hits[0]._flags);
		writeD(_soulshot ? _grade : 0x00);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeH(hits.length - 1);
		for(int i = 1; i < hits.length; i++)
		{
			writeD(hits[i]._targetId);
			writeD(hits[i]._damage);
			writeD(hits[i]._flags);
			writeD(_soulshot ? _grade : 0x00);
		}
		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
	}
}