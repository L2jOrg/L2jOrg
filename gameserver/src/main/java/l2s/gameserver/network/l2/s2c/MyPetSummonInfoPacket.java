package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.utils.Location;

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
	protected final void writeImpl()
	{
		writeC(_type);
		writeD(obj_id);
		writeD(npc_id + 1000000);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
		writeD(MAtkSpd);
		writeD(PAtkSpd);
		writeH(_runSpd);
		writeH(_walkSpd);
		writeH(_runSpd/*_swimRunSpd*/);
		writeH(_walkSpd/*_swimWalkSpd*/);
		writeH(_runSpd/*_flRunSpd*/);
		writeH(_walkSpd/*_flWalkSpd*/);
		writeH(_runSpd/*_flyRunSpd*/);
		writeH(_walkSpd/*_flyWalkSpd*/);
		writeF(_runSpdMul);
		writeF(_atkSpdMul);
		writeF(col_redius);
		writeF(col_height);
		writeD(_rhand); // right hand weapon
		writeD(0);
		writeD(_lhand); // left hand weapon
		writeC(_showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		writeD(-1);
		writeS(_name);
		writeD(-1);
		writeS(title);
		writeC(pvp_flag); //0=white, 1=purple, 2=purpleblink, if its greater then karma = purple
		writeD(karma); // hmm karma ??
		writeD(curFed); // how fed it is
		writeD(maxFed); //max fed it can be
		writeD(curHp); //current hp
		writeD(maxHp); // max hp
		writeD(curMp); //current mp
		writeD(maxMp); //max mp
		writeQ(_sp); //sp
		writeC(level);// lvl
		writeQ(exp);
		writeQ(exp_this_lvl); // 0%  absolute value
		writeQ(exp_next_lvl); // 100% absoulte value
		writeD(curLoad); //weight
		writeD(maxLoad); //max weight it can carry
		writeD(PAtk);//patk
		writeD(PDef);//pdef
		writeD(_pAccuracy); // P. Accuracy
		writeD(_pEvasion); // P. Evasion
		writeD(_pCrit); // P. Critical
		writeD(MAtk);//matk
		writeD(MDef);//mdef
		writeD(_mAccuracy); // M. Accuracy
		writeD(_mEvasion); // M. Evasion
		writeD(_mCrit); // M. Critical
		writeD(_runSpd);//speed
		writeD(PAtkSpd);//atkspeed
		writeD(MAtkSpd);//casting speed
		writeC(0x00);//unk
		writeC(_team.ordinal()); // team aura (1 = blue, 2 = red)
		writeC(ss);
		writeC(sps);
		writeD(type);
		writeD(_transformId); // transform id
		writeC(0x00); // sum points
		writeC(0x00); // max sum points

		writeH(_abnormalEffects.length);
		for(AbnormalEffect abnormal : _abnormalEffects)
			writeH(abnormal.getId());

		writeC(_flags);
	}
}