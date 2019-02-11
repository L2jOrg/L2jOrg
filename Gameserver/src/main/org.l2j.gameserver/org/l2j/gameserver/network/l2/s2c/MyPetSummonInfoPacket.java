package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.skills.AbnormalEffect;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class MyPetSummonInfoPacket extends L2GameServerPacket
{
	private static final int IS_UNK_FLAG_1 = 1 << 0;
	private static final int IS_UNK_FLAG_2 = 1 << 1;
	private static final int IS_RUNNING = 1 << 2;
	private static final int IS_IN_COMBAT = 1 << 3;
	private static final int IS_ALIKE_DEAD = 1 << 4;
	private static final int IS_RIDEABLE = 1 << 5;

	private int _runSpd, _walkSpd, MAtkSpd, PAtkSpd, pvp_flag, karma;
	private int _type, obj_id, npc_id, incombat, dead, _sp, level;
	private int curFed, maxFed, curHp, maxHp, curMp, maxMp, curLoad, maxLoad;
	private int PAtk, PDef, MAtk, MDef, sps, ss, type, _showSpawnAnimation;
	private int _pAccuracy, _pEvasion, _pCrit, _mAccuracy, _mEvasion, _mCrit;
	private Location _loc;
	private double col_redius, col_height;
	private long exp, exp_this_lvl, exp_next_lvl;
	private String _name, title;
	private TeamType _team;
	private double _atkSpdMul, _runSpdMul;
	private int _transformId;
	private AbnormalEffect[] _abnormalEffects;
	private int _rhand, _lhand;
	private int _flags;

	public MyPetSummonInfoPacket(Servitor summon)
	{
		_type = summon.getServitorType();
		obj_id = summon.getObjectId();
		npc_id = summon.getNpcId();
		_loc = summon.getLoc();
		MAtkSpd = summon.getMAtkSpd();
		PAtkSpd = summon.getPAtkSpd();
		_runSpd = summon.getRunSpeed();
		_walkSpd = summon.getWalkSpeed();
		col_redius = summon.getCollisionRadius();
		col_height = summon.getCollisionHeight();
		incombat = summon.isInCombat() ? 1 : 0;
		dead = summon.isAlikeDead() ? 1 : 0;
		_name = summon.getName().equalsIgnoreCase(summon.getTemplate().name) ? "" : summon.getName();

		title = summon.getTitle();
		if(title.equals(Servitor.TITLE_BY_OWNER_NAME))
			title = summon.getPlayer().getVisibleName(summon.getPlayer());

		pvp_flag = summon.getPvpFlag();
		karma = summon.getKarma();
		curFed = summon.getCurrentFed();
		maxFed = summon.getMaxFed();
		curHp = (int) summon.getCurrentHp();
		maxHp = summon.getMaxHp();
		curMp = (int) summon.getCurrentMp();
		maxMp = summon.getMaxMp();
		_sp = summon.getSp();
		level = summon.getLevel();
		exp = summon.getExp();
		exp_this_lvl = summon.getExpForThisLevel();
		exp_next_lvl = summon.getExpForNextLevel();
		curLoad = summon.getCurrentLoad();
		maxLoad = summon.getMaxLoad();
		PAtk = summon.getPAtk(null);
		PDef = summon.getPDef(null);
		MAtk = summon.getMAtk(null, null);
		MDef = summon.getMDef(null, null);
		_pAccuracy = summon.getPAccuracy();
		_pEvasion = summon.getPEvasionRate(null);
		_pCrit = summon.getPCriticalHit(null);
		_mAccuracy = summon.getMAccuracy();
		_mEvasion = summon.getMEvasionRate(null);
		_mCrit = summon.getMCriticalHit(null, null);
		_abnormalEffects = summon.getAbnormalEffectsArray();
		_team = summon.getTeam();
		ss = summon.getSoulshotConsumeCount();
		sps = summon.getSpiritshotConsumeCount();
		_showSpawnAnimation = summon.getSpawnAnimation();
		type = summon.getFormId();
		_atkSpdMul = summon.getAttackSpeedMultiplier();;
		_runSpdMul = summon.getMovementSpeedMultiplier();
		_transformId = summon.getVisualTransformId();

		boolean rideable = summon.isMountable();

		Player owner = summon.getPlayer();
		if(owner != null)
		{
			// В режиме трансформации значек mount/dismount не отображается
			if(owner.isTransformed())
				rideable = false;
		}

		_rhand = summon.getTemplate().rhand;
		_lhand = summon.getTemplate().lhand;

		_flags |= IS_UNK_FLAG_2;

		if(summon.isRunning())
			_flags |= IS_RUNNING;

		if(summon.isInCombat())
			_flags |= IS_IN_COMBAT;

		if(summon.isAlikeDead())
			_flags |= IS_ALIKE_DEAD;

		if(rideable)
			_flags |= IS_RIDEABLE;
	}

	public MyPetSummonInfoPacket update()
	{
		_showSpawnAnimation = 1;
		return this;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)_type);
		buffer.putInt(obj_id);
		buffer.putInt(npc_id + 1000000);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(_loc.h);
		buffer.putInt(MAtkSpd);
		buffer.putInt(PAtkSpd);
		buffer.putShort((short) _runSpd);
		buffer.putShort((short) _walkSpd);
		buffer.putShort((short) _runSpd/*_swimRunSpd*/);
		buffer.putShort((short) _walkSpd/*_swimWalkSpd*/);
		buffer.putShort((short) _runSpd/*_flRunSpd*/);
		buffer.putShort((short) _walkSpd/*_flWalkSpd*/);
		buffer.putShort((short) _runSpd/*_flyRunSpd*/);
		buffer.putShort((short) _walkSpd/*_flyWalkSpd*/);
		buffer.putDouble(_runSpdMul);
		buffer.putDouble(_atkSpdMul);
		buffer.putDouble(col_redius);
		buffer.putDouble(col_height);
		buffer.putInt(_rhand); // right hand weapon
		buffer.putInt(0);
		buffer.putInt(_lhand); // left hand weapon
		buffer.put((byte)_showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		buffer.putInt(-1);
		writeString(_name, buffer);
		buffer.putInt(-1);
		writeString(title, buffer);
		buffer.put((byte)pvp_flag); //0=white, 1=purple, 2=purpleblink, if its greater then karma = purple
		buffer.putInt(karma); // hmm karma ??
		buffer.putInt(curFed); // how fed it is
		buffer.putInt(maxFed); //max fed it can be
		buffer.putInt(curHp); //current hp
		buffer.putInt(maxHp); // max hp
		buffer.putInt(curMp); //current mp
		buffer.putInt(maxMp); //max mp
		buffer.putLong(_sp); //sp
		buffer.put((byte)level);// lvl
		buffer.putLong(exp);
		buffer.putLong(exp_this_lvl); // 0%  absolute value
		buffer.putLong(exp_next_lvl); // 100% absoulte value
		buffer.putInt(curLoad); //weight
		buffer.putInt(maxLoad); //max weight it can carry
		buffer.putInt(PAtk);//patk
		buffer.putInt(PDef);//pdef
		buffer.putInt(_pAccuracy); // P. Accuracy
		buffer.putInt(_pEvasion); // P. Evasion
		buffer.putInt(_pCrit); // P. Critical
		buffer.putInt(MAtk);//matk
		buffer.putInt(MDef);//mdef
		buffer.putInt(_mAccuracy); // M. Accuracy
		buffer.putInt(_mEvasion); // M. Evasion
		buffer.putInt(_mCrit); // M. Critical
		buffer.putInt(_runSpd);//speed
		buffer.putInt(PAtkSpd);//atkspeed
		buffer.putInt(MAtkSpd);//casting speed
		buffer.put((byte)0x00);//unk
		buffer.put((byte)_team.ordinal()); // team aura (1 = blue, 2 = red)
		buffer.put((byte)ss);
		buffer.put((byte)sps);
		buffer.putInt(type);
		buffer.putInt(_transformId); // transform id
		buffer.put((byte)0x00); // sum points
		buffer.put((byte)0x00); // max sum points

		buffer.putShort((short) _abnormalEffects.length);
		for(AbnormalEffect abnormal : _abnormalEffects)
			buffer.putShort((short) abnormal.getId());

		buffer.put((byte)_flags);
	}
}