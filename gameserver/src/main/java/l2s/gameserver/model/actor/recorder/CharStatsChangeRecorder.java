package l2s.gameserver.model.actor.recorder;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.skills.AbnormalEffect;

/**
 * @author G1ta0
 */
public class CharStatsChangeRecorder<T extends Creature>
{
	public static final int BROADCAST_CHAR_INFO = 1 << 0; // Требуется обновить состояние персонажа у окружающих
	public static final int SEND_CHAR_INFO = 1 << 1; // Требуется обновить состояние только самому персонажу
	public static final int SEND_STATUS_INFO = 1 << 2; // Требуется обновить статус HP/MP/CP
	public static final int SEND_ABNORMAL_INFO = 1 << 3; // Требуется обновить абнормальные эффекты.
	public static final int SEND_TRANSFORMATION_INFO = 1 << 4; // Требуется обновить эффект трансформации.

	protected final T _activeChar;

	private AtomicBoolean _blocked = new AtomicBoolean();

	protected int _level;

	protected int _pAccuracy;
	protected int _mAccuracy;
	protected int _attackSpeed;
	protected int _castSpeed;
	protected int _pCriticalHit;
	protected int _mCriticalHit;
	protected int _pEvasion;
	protected int _mEvasion;
	protected int _magicAttack;
	protected int _magicDefence;
	protected int _maxHp;
	protected int _maxMp;
	protected int _physicAttack;
	protected int _physicDefence;
	protected int _moveSpeed;
	protected int _visualTransformId;

	protected Set<AbnormalEffect> _abnormalEffects = new CopyOnWriteArraySet<AbnormalEffect>();

	protected TeamType _team;

	protected int _changes;

	public CharStatsChangeRecorder(T actor)
	{
		this._activeChar = actor;
	}

	protected int set(int flag, int oldValue, int newValue)
	{
		if(oldValue != newValue)
			_changes |= flag;
		return newValue;
	}

	protected long set(int flag, long oldValue, long newValue)
	{
		if(oldValue != newValue)
			_changes |= flag;
		return newValue;
	}

	protected double set(int flag, double oldValue, double newValue)
	{
		if(oldValue != newValue)
			_changes |= flag;
		return newValue;
	}

	protected String set(int flag, String oldValue, String newValue)
	{
		if(!oldValue.equals(newValue))
			_changes |= flag;
		return newValue;
	}

	protected Set<AbnormalEffect> set(int flag, Set<AbnormalEffect> oldValue, Set<AbnormalEffect> newValue)
	{
		synchronized(oldValue)
		{
			if(oldValue.size() != newValue.size() || !newValue.equals(oldValue))
			{
				_changes |= flag;

				oldValue.clear();
				oldValue.addAll(newValue);
			}
		}
		return oldValue;
	}

	protected <E extends Enum<E>> E set(int flag, E oldValue, E newValue)
	{
		if(oldValue != newValue)
			_changes |= flag;
		return newValue;
	}

	protected void refreshStats()
	{
		_pAccuracy = set(SEND_CHAR_INFO, _pAccuracy, _activeChar.getPAccuracy());
		_mAccuracy = set(SEND_CHAR_INFO, _mAccuracy, _activeChar.getMAccuracy());
		_attackSpeed = set(BROADCAST_CHAR_INFO, _attackSpeed, _activeChar.getPAtkSpd());
		_castSpeed = set(BROADCAST_CHAR_INFO, _castSpeed, _activeChar.getMAtkSpd());
		_pCriticalHit = set(SEND_CHAR_INFO, _pCriticalHit, _activeChar.getPCriticalHit(null));
		_mCriticalHit = set(SEND_CHAR_INFO, _mCriticalHit, _activeChar.getMCriticalHit(null, null));
		_pEvasion = set(SEND_CHAR_INFO, _pEvasion, _activeChar.getPEvasionRate(null));
		_mEvasion = set(SEND_CHAR_INFO, _mEvasion, _activeChar.getMEvasionRate(null));
		_moveSpeed = set(BROADCAST_CHAR_INFO, _moveSpeed, _activeChar.getMoveSpeed());

		_physicAttack = set(SEND_CHAR_INFO, _physicAttack, _activeChar.getPAtk(null));
		_physicDefence = set(SEND_CHAR_INFO, _physicDefence, _activeChar.getPDef(null));
		_magicAttack = set(SEND_CHAR_INFO, _magicAttack, _activeChar.getMAtk(null, null));
		_magicDefence = set(SEND_CHAR_INFO, _magicDefence, _activeChar.getMDef(null, null));

		_maxHp = set(SEND_STATUS_INFO, _maxHp, _activeChar.getMaxHp());
		_maxMp = set(SEND_STATUS_INFO, _maxMp, _activeChar.getMaxMp());

		_level = set(SEND_CHAR_INFO, _level, _activeChar.getLevel());

		_abnormalEffects = set(SEND_ABNORMAL_INFO, _abnormalEffects, _activeChar.getAbnormalEffects());
		_visualTransformId = set(SEND_TRANSFORMATION_INFO, _visualTransformId, _activeChar.getVisualTransformId());

		_team = set(BROADCAST_CHAR_INFO, _team, _activeChar.getTeam());
	}

	public final void sendChanges()
	{
		if(_blocked.get())
			return;

		refreshStats();
		onSendChanges();
		_changes = 0;
	}

	protected void onSendChanges()
	{
		if((_changes & SEND_STATUS_INFO) == SEND_STATUS_INFO)
			_activeChar.broadcastStatusUpdate();
	}

	public void block()
	{
		_blocked.compareAndSet(false, true);
	}

	public void unblock()
	{
		_blocked.compareAndSet(true, false);
	}
}