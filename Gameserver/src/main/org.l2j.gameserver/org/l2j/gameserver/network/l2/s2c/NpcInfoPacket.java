package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.instances.PetInstance;
import org.l2j.gameserver.model.instances.SummonInstance;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import org.l2j.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import org.l2j.gameserver.skills.AbnormalEffect;
import org.l2j.gameserver.utils.Location;

import org.apache.commons.lang3.StringUtils;

/**
 * @author UnAfraid
 * reworked by Bonux
 */
public class NpcInfoPacket extends AbstractMaskPacket<NpcInfoType>
{
	public static class SummonInfoPacket extends NpcInfoPacket
	{
		public SummonInfoPacket(SummonInstance summon, Creature attacker)
		{
			super(summon, attacker);
		}
	}

	public static class PetInfoPacket extends NpcInfoPacket
	{
		public PetInfoPacket(PetInstance summon, Creature attacker)
		{
			super(summon, attacker);
		}
	}

	// Flags
	private static final int IS_IN_COMBAT = 1 << 0;
	private static final int IS_ALIKE_DEAD = 1 << 1;
	private static final int IS_TARGETABLE = 1 << 2;
	private static final int IS_SHOW_NAME = 1 << 3;

	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x0C,
		(byte) 0x0C,
		(byte) 0x00,
		(byte) 0x00
	};

	private final Creature _creature;
	private final int _npcId;
	private final boolean _isAttackable;
	private final int _rHand, _lHand;
	private final String _name, _title;
	private final int _state;
	private final NpcString _nameNpcString, _titleNpcString;

	private int _initSize = 0;
	private int _blockSize = 0;

	private int _statusMask = 0;

	private int _showSpawnAnimation;
	private int _npcObjId;
	private Location _loc;
	private int _pAtkSpd, _mAtkSpd;
	private double _atkSpdMul, _runSpdMul;
	private int _pvpFlag;
	private boolean _alive, _running, _flying, _inWater;
	private TeamType _team;
	private int _currentHP, _currentMP;
	private int _maxHP, _maxMP;
	private int _enchantEffect;
	private int _transformId;
	private AbnormalEffect[] _abnormalEffects;
	private int _clanId, _clanCrestId, _largeClanCrestId;
	private int _allyId, _allyCrestId;

	public NpcInfoPacket(NpcInstance npc, Creature attacker)
	{
		_creature = npc;

		if(!npc.getName().equals(npc.getTemplate().name))
			_name = npc.getName();
		else
			_name = StringUtils.EMPTY;

		if(!npc.getTitle().equals(npc.getTemplate().title))
			_title = npc.getTitle();
		else
			_title = StringUtils.EMPTY;

		_npcId = npc.getDisplayId() != 0 ? npc.getDisplayId() : npc.getNpcId();
		_isAttackable = npc.isAutoAttackable(attacker);
		_rHand = npc.getRightHandItem();
		_lHand = npc.getLeftHandItem();

		_showSpawnAnimation = npc.getSpawnAnimation();
		_state = npc.getNpcState();

		NpcString nameNpcString = npc.getNameNpcString();
		_nameNpcString = nameNpcString != null ? nameNpcString : NpcString.NONE;

		NpcString titleNpcString = npc.getTitleNpcString();
		_titleNpcString = titleNpcString != null ? titleNpcString : NpcString.NONE;

		if(npc.isTargetable(attacker))
			_statusMask |= IS_TARGETABLE;

		if(npc.isShowName())
			_statusMask |= IS_SHOW_NAME;

		common();
	}

	public NpcInfoPacket(Servitor servitor, Creature attacker)
	{
		_creature = servitor;

		if(!servitor.getName().equals(servitor.getTemplate().name))
			_name = servitor.getName();
		else
			_name = StringUtils.EMPTY;

		String title = servitor.getTitle();
		if(title.equals(Servitor.TITLE_BY_OWNER_NAME))
			_title = servitor.getPlayer().getVisibleName(attacker.getPlayer());
		else
			_title = title;

		_npcId = servitor.getDisplayId() != 0 ? servitor.getDisplayId() : servitor.getNpcId();
		_isAttackable = servitor.isAutoAttackable(attacker);
		_rHand = servitor.getTemplate().rhand;
		_lHand = servitor.getTemplate().lhand;

		_showSpawnAnimation = servitor.getSpawnAnimation();
		_state = servitor.getNpcState();
		_nameNpcString = NpcString.NONE;
		_titleNpcString = NpcString.NONE;

		if(servitor.isTargetable(attacker))
			_statusMask |= IS_TARGETABLE;

		if(servitor.isShowName())
			_statusMask |= IS_SHOW_NAME;

		common();
	}

	private void common()
	{
		_npcObjId = _creature.getObjectId();
		_loc = _creature.getLoc();
		_pAtkSpd = _creature.getPAtkSpd();
		_mAtkSpd = _creature.getMAtkSpd();
		_atkSpdMul = _creature.getAttackSpeedMultiplier();
		_runSpdMul = _creature.getMovementSpeedMultiplier();
		_pvpFlag = _creature.getPvpFlag();
		_alive = !_creature.isAlikeDead();
		_running = _creature.isRunning();
		_flying = _creature.isFlying();
		_inWater = _creature.isInWater();
		_team = _creature.getTeam();
		_currentHP = (int) _creature.getCurrentHp();
		_currentMP = (int) _creature.getCurrentMp();
		_maxHP = _creature.getMaxHp();
		_maxMP = _creature.getMaxMp();
		_enchantEffect = _creature.getEnchantEffect();
		_transformId = _creature.getVisualTransformId();
		_abnormalEffects = _creature.getAbnormalEffectsArray();

		Clan clan = _creature.getClan();
		Alliance alliance = clan == null ? null : clan.getAlliance();

		_clanId = clan == null ? 0 : clan.getClanId();
		_clanCrestId = clan == null ? 0 : clan.getCrestId();
		_largeClanCrestId = clan == null ? 0 : clan.getCrestLargeId();
		_allyId = alliance == null ? 0 : alliance.getAllyId();
		_allyCrestId = alliance == null ? 0 : alliance.getAllyCrestId();
	}

	public NpcInfoPacket init()
	{
		addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.UNKNOWN1, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING);

		if(_name != StringUtils.EMPTY)
			addComponentType(NpcInfoType.NAME);

		if(_title != StringUtils.EMPTY)
			addComponentType(NpcInfoType.TITLE);

		if(_loc.h > 0)
			addComponentType(NpcInfoType.HEADING);

		if(_pAtkSpd > 0 || _mAtkSpd > 0)
			addComponentType(NpcInfoType.ATK_CAST_SPEED);

		if(_running && _creature.getRunSpeed() > 0 || !_running && _creature.getWalkSpeed() > 0)
			addComponentType(NpcInfoType.SPEED_MULTIPLIER);

		if(_rHand > 0 || _lHand > 0)
			addComponentType(NpcInfoType.EQUIPPED);

		if(_team != TeamType.NONE)
			addComponentType(NpcInfoType.TEAM);

		if(_state > 0)
			addComponentType(NpcInfoType.DISPLAY_EFFECT);

		if(_inWater || _flying)
			addComponentType(NpcInfoType.SWIM_OR_FLY);

		if(_flying)
			addComponentType(NpcInfoType.FLYING);

		if(_maxHP > 0)
			addComponentType(NpcInfoType.MAX_HP);

		if(_maxMP > 0)
			addComponentType(NpcInfoType.MAX_MP);

		if(_currentHP <= _maxHP)
			addComponentType(NpcInfoType.CURRENT_HP);

		if(_currentMP <= _maxMP)
			addComponentType(NpcInfoType.CURRENT_MP);

		if(_abnormalEffects.length > 0)
			addComponentType(NpcInfoType.ABNORMALS);

		if(_enchantEffect > 0)
			addComponentType(NpcInfoType.ENCHANT);

		if(_transformId > 0)
			addComponentType(NpcInfoType.TRANSFORMATION);

		if(_clanId > 0)
			addComponentType(NpcInfoType.CLAN);

		addComponentType(NpcInfoType.UNKNOWN8);

		if(_creature.isInCombat())
			_statusMask |= IS_IN_COMBAT;

		if(_creature.isAlikeDead())
			_statusMask |= IS_ALIKE_DEAD;

		if(_statusMask != 0)
			addComponentType(NpcInfoType.VISUAL_STATE);

		if(_nameNpcString != NpcString.NONE)
			addComponentType(NpcInfoType.NAME_NPCSTRINGID);

		if(_titleNpcString != NpcString.NONE)
			addComponentType(NpcInfoType.TITLE_NPCSTRINGID);

		return this;
	}

	public NpcInfoPacket update(IUpdateTypeComponent... components)
	{
		_showSpawnAnimation = 1;
		addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.UNKNOWN1, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING);

		for(IUpdateTypeComponent component : components)
		{
			if(component instanceof NpcInfoType)
				addComponentType((NpcInfoType) component);
		}
		return this;
	}

	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}

	@Override
	protected void onNewMaskAdded(NpcInfoType component)
	{
		switch(component)
		{
			case ATTACKABLE:
			case UNKNOWN1:
			{
				_initSize += component.getBlockLength();
				break;
			}
			case TITLE:
			{
				_initSize += component.getBlockLength() + (_title.length() * 2);
				break;
			}
			case NAME:
			{
				_blockSize += component.getBlockLength() + (_name.length() * 2);
				break;
			}
			default:
			{
				_blockSize += component.getBlockLength();
				break;
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_npcObjId);
		writeByte(_showSpawnAnimation); // // 0=teleported 1=default 2=summoned
		writeShort(37); // mask_bits_37
		writeB(_masks);

		// Block 1
		writeByte(_initSize);

		if(containsMask(NpcInfoType.ATTACKABLE))
			writeByte(_isAttackable);

		if(containsMask(NpcInfoType.UNKNOWN1))
			writeInt(0x00); // unknown

		if(containsMask(NpcInfoType.TITLE))
			writeString(_title);

		// Block 2
		writeShort(_blockSize);

		if(containsMask(NpcInfoType.ID))
			writeInt(_npcId + 1000000);

		if(containsMask(NpcInfoType.POSITION))
		{
			writeInt(_loc.x);
			writeInt(_loc.y);
			writeInt(_loc.z);
		}

		if(containsMask(NpcInfoType.HEADING))
			writeInt(_loc.h);

		if(containsMask(NpcInfoType.UNKNOWN2))
			writeInt(0x00); // Unknown

		if(containsMask(NpcInfoType.ATK_CAST_SPEED))
		{
			writeInt(_pAtkSpd);
			writeInt(_mAtkSpd);
		}

		if(containsMask(NpcInfoType.SPEED_MULTIPLIER))
		{
			writeCutF(_runSpdMul);
			writeCutF(_atkSpdMul);
		}

		if(containsMask(NpcInfoType.EQUIPPED))
		{
			writeInt(_rHand);
			writeInt(0x00); // Armor id?
			writeInt(_lHand);
		}

		if(containsMask(NpcInfoType.ALIVE))
			writeByte(_alive);

		if(containsMask(NpcInfoType.RUNNING))
			writeByte(_running);

		if(containsMask(NpcInfoType.SWIM_OR_FLY))
			writeByte(_inWater ? 1 : _flying ? 2 : 0);

		if(containsMask(NpcInfoType.TEAM))
			writeByte(_team.ordinal());

		if(containsMask(NpcInfoType.ENCHANT))
			writeInt(_enchantEffect);

		if(containsMask(NpcInfoType.FLYING))
			writeInt(_flying);

		if(containsMask(NpcInfoType.CLONE))
			writeInt(0x00); // Player ObjectId with Decoy

		if(containsMask(NpcInfoType.UNKNOWN8))
		{
			// No visual effect
			writeInt(0x00); // Unknown
		}

		if(containsMask(NpcInfoType.DISPLAY_EFFECT))
			writeInt(_state);

		if(containsMask(NpcInfoType.TRANSFORMATION))
			writeInt(_transformId);

		if(containsMask(NpcInfoType.CURRENT_HP))
			writeInt(_currentHP);

		if(containsMask(NpcInfoType.CURRENT_MP))
			writeInt(_currentMP);

		if(containsMask(NpcInfoType.MAX_HP))
			writeInt(_maxHP);

		if(containsMask(NpcInfoType.MAX_MP))
			writeInt(_maxMP);

		if(containsMask(NpcInfoType.SUMMONED))
			writeByte(0x00); // 2 - do some animation on spawn

		if(containsMask(NpcInfoType.UNKNOWN12))
		{
			writeInt(0x00);
			writeInt(0x00);
		}

		if(containsMask(NpcInfoType.NAME))
			writeString(_name);

		if(containsMask(NpcInfoType.NAME_NPCSTRINGID))
			writeInt(_nameNpcString.getId()); // NPCStringId for name

		if(containsMask(NpcInfoType.TITLE_NPCSTRINGID))
			writeInt(_titleNpcString.getId()); // NPCStringId for title

		if(containsMask(NpcInfoType.PVP_FLAG))
			writeByte(_pvpFlag); // PVP flag

		if(containsMask(NpcInfoType.NAME_COLOR))
			writeInt(0x00); // Name color

		if(containsMask(NpcInfoType.CLAN))
		{
			writeInt(_clanId);
			writeInt(_clanCrestId);
			writeInt(_largeClanCrestId);
			writeInt(_allyId);
			writeInt(_allyCrestId);
		}

		if(containsMask(NpcInfoType.VISUAL_STATE))
			writeByte(_statusMask);

		if(containsMask(NpcInfoType.ABNORMALS))
		{
			writeShort(_abnormalEffects.length);
			for(AbnormalEffect abnormal : _abnormalEffects)
				writeShort(abnormal.getId());
		}
	}
}