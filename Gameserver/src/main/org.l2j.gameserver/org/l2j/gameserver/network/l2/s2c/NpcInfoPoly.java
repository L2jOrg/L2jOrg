package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.data.xml.holder.NpcHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.npc.NpcTemplate;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_obj.getObjectId());
		buffer.putInt(_npcId + 1000000); // npctype id
		buffer.putInt(0x00);
		buffer.putInt(_x);
		buffer.putInt(_y);
		buffer.putInt(_z);
		buffer.putInt(_heading);
		buffer.putInt(0x00);
		buffer.putInt(_mAtkSpd);
		buffer.putInt(_pAtkSpd);
		buffer.putInt(_runSpd);
		buffer.putInt(_walkSpd);
		buffer.putInt(_swimRunSpd/*0x32*/); // swimspeed
		buffer.putInt(_swimWalkSpd/*0x32*/); // swimspeed
		buffer.putInt(_flRunSpd);
		buffer.putInt(_flWalkSpd);
		buffer.putInt(_flyRunSpd);
		buffer.putInt(_flyWalkSpd);
		buffer.putDouble(1/*_cha.getProperMultiplier()*/);
		buffer.putDouble(1/*_cha.getAttackSpeedMultiplier()*/);
		buffer.putDouble(colRadius);
		buffer.putDouble(colHeight);
		buffer.putInt(_rhand); // right hand weapon
		buffer.putInt(0);
		buffer.putInt(_lhand); // left hand weapon
		buffer.put((byte)1); // name above char 1=true ... ??
		buffer.put((byte)(_isRunning ? 1 : 0));
		buffer.put((byte)(_isInCombat ? 1 : 0));
		buffer.put((byte)(_isAlikeDead ? 1 : 0));
		buffer.put((byte)(_isSummoned ? 2 : 0)); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		writeString(_name, buffer);
		writeString(_title, buffer);
		buffer.putInt(0);
		buffer.putInt(0);
		buffer.putInt(0000); // hmm karma ??

		buffer.putInt(_abnormalEffect);

		buffer.putInt(0000); // C2
		buffer.putInt(0000); // C2
		buffer.putInt(0000); // C2
		buffer.putInt(0000); // C2
		buffer.put((byte)0000); // C2
		buffer.put((byte)_team.ordinal());
		buffer.putDouble(colRadius); // тут что-то связанное с colRadius
		buffer.putDouble(colHeight); // тут что-то связанное с colHeight
		buffer.putInt(0x00); // C4
		buffer.putInt(0x00); // как-то связано с высотой
		buffer.putInt(0x00);
		buffer.putInt(0x00); // maybe show great wolf type ?

		buffer.put((byte)0x00); //?GraciaFinal
		buffer.put((byte)0x00); //?GraciaFinal
		buffer.putInt(_abnormalEffect2);
	}
}