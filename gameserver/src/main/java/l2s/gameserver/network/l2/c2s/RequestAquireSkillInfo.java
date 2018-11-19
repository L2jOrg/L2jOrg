package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.AcquireSkillInfoPacket;
import l2s.gameserver.network.l2.s2c.ExAcquireSkillInfo;

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
		_id = readD();
		_level = readD();
		_type = AcquireType.getById(readD());
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