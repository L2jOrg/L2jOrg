package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.SkillAcquireHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.AcquireSkillInfoPacket;
import org.l2j.gameserver.network.l2.s2c.ExAcquireSkillInfo;

/**
 * Reworked: VISTALL
 */
public class RequestAquireSkillInfo extends L2GameClientPacket
{
	private int _id;
	private int _level;
	private AcquireType _type;

	@Override
	protected void readImpl()
	{
		_id = readInt();
		_level = readInt();
		_type = AcquireType.getById(readInt());
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null || player.isTransformed() || SkillHolder.getInstance().getSkill(_id, _level) == null || _type == null)
			return;

		if(_type != AcquireType.NORMAL && _type != AcquireType.MULTICLASS && _type != AcquireType.CUSTOM)
		{
			NpcInstance trainer = player.getLastNpc();
			if((trainer == null || !player.checkInteractionDistance(trainer)) && !player.isGM())
				return;
		}

		ClassId selectedMultiClassId = player.getSelectedMultiClassId();
		if(_type == AcquireType.MULTICLASS)
		{
			if(selectedMultiClassId == null)
				return;
		}
		else
			selectedMultiClassId = null;

		SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, selectedMultiClassId, _id, _level, _type);
		if(skillLearn == null)
			return;

		if(_type == AcquireType.NORMAL)
			sendPacket(new ExAcquireSkillInfo(player, _type, skillLearn));
		else
			sendPacket(new AcquireSkillInfoPacket(_type, skillLearn));
	}
}