package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.data.xml.holder.NpcHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.templates.npc.NpcTemplate;

public class NpcInfoPoly extends L2GameServerPacket
{
	//   ddddddddddddddddddffffdddcccccSSddd dddddccffddddccd
	private Creature _obj;
	private int _x, _y, _z, _heading;
	private int _npcId;
	private boolean _isSummoned, _isRunning, _isInCombat, _isAlikeDead;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private int _rhand, _lhand;
	private String _name, _title;
	private int _abnormalEffect, _abnormalEffect2;
	private double colRadius, colHeight;
	private TeamType _team;

	public NpcInfoPoly(Player cha)
	{
		_obj = cha;
		_npcId = cha.getPolyId();
		NpcTemplate template = NpcHolder.getInstance().getTemplate(_npcId);
		_rhand = 0;
		_lhand = 0;
		_isSummoned = false;
		colRadius = template.getCollisionRadius();
		colHeight = template.getCollisionHeight();
		_x = _obj.getX();
		_y = _obj.getY();
		_z = _obj.getZ();
		_rhand = template.rhand;
		_lhand = template.lhand;
		_heading = cha.getHeading();
		_mAtkSpd = cha.getMAtkSpd();
		_pAtkSpd = cha.getPAtkSpd();
		_runSpd = cha.getRunSpeed();
		_walkSpd = cha.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
		_isRunning = cha.isRunning();
		_isInCombat = cha.isInCombat();
		_isAlikeDead = cha.isAlikeDead();
		_name = cha.getName();
		_title = cha.getTitle();
		_abnormalEffect = 0/*cha.getAbnormalEffect()*/;
		_abnormalEffect2 = 0/*cha.getAbnormalEffect2()*/;
		_team = cha.getTeam();
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_obj.getObjectId());
		writeInt(_npcId + 1000000); // npctype id
		writeInt(0x00);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_heading);
		writeInt(0x00);
		writeInt(_mAtkSpd);
		writeInt(_pAtkSpd);
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_swimRunSpd/*0x32*/); // swimspeed
		writeInt(_swimWalkSpd/*0x32*/); // swimspeed
		writeInt(_flRunSpd);
		writeInt(_flWalkSpd);
		writeInt(_flyRunSpd);
		writeInt(_flyWalkSpd);
		writeF(1/*_cha.getProperMultiplier()*/);
		writeF(1/*_cha.getAttackSpeedMultiplier()*/);
		writeF(colRadius);
		writeF(colHeight);
		writeInt(_rhand); // right hand weapon
		writeInt(0);
		writeInt(_lhand); // left hand weapon
		writeByte(1); // name above char 1=true ... ??
		writeByte(_isRunning ? 1 : 0);
		writeByte(_isInCombat ? 1 : 0);
		writeByte(_isAlikeDead ? 1 : 0);
		writeByte(_isSummoned ? 2 : 0); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		writeString(_name);
		writeString(_title);
		writeInt(0);
		writeInt(0);
		writeInt(0000); // hmm karma ??

		writeInt(_abnormalEffect);

		writeInt(0000); // C2
		writeInt(0000); // C2
		writeInt(0000); // C2
		writeInt(0000); // C2
		writeByte(0000); // C2
		writeByte(_team.ordinal());
		writeF(colRadius); // тут что-то связанное с colRadius
		writeF(colHeight); // тут что-то связанное с colHeight
		writeInt(0x00); // C4
		writeInt(0x00); // как-то связано с высотой
		writeInt(0x00);
		writeInt(0x00); // maybe show great wolf type ?

		writeByte(0x00); //?GraciaFinal
		writeByte(0x00); //?GraciaFinal
		writeInt(_abnormalEffect2);
	}
}