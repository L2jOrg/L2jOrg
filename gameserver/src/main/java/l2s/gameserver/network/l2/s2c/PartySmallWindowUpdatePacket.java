package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.updatetype.PartySmallWindowUpdateType;

public class PartySmallWindowUpdatePacket extends L2GameServerPacket
{
	private int obj_id, class_id, level;
	private int curCp, maxCp, curHp, maxHp, curMp, maxMp, vitality;
	private String obj_name;
	private int _flags = 0;

	public PartySmallWindowUpdatePacket(Player member, boolean addAllFlags)
	{
		obj_id = member.getObjectId();
		obj_name = member.getName();
		curCp = (int) member.getCurrentCp();
		maxCp = member.getMaxCp();
		curHp = (int) member.getCurrentHp();
		maxHp = member.getMaxHp();
		curMp = (int) member.getCurrentMp();
		maxMp = member.getMaxMp();
		level = member.getLevel();
		class_id = member.getClassId().getId();

		if(addAllFlags)
		{
			for(PartySmallWindowUpdateType type : PartySmallWindowUpdateType.values())
				addUpdateType(type);
		}
	}

	public PartySmallWindowUpdatePacket(Player member)
	{
		this(member, true);
	}
	
	public void addUpdateType(PartySmallWindowUpdateType type)
	{
		_flags |= type.getMask();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(obj_id);
		writeH(_flags);
		if(containsMask(_flags, PartySmallWindowUpdateType.CURRENT_CP))
			writeD(curCp); // c4

		if(containsMask(_flags, PartySmallWindowUpdateType.MAX_CP))
			writeD(maxCp); // c4

		if(containsMask(_flags, PartySmallWindowUpdateType.CURRENT_HP))
			writeD(curHp);

		if(containsMask(_flags, PartySmallWindowUpdateType.MAX_HP))
			writeD(maxHp);

		if(containsMask(_flags, PartySmallWindowUpdateType.CURRENT_MP))
			writeD(curMp);

		if(containsMask(_flags, PartySmallWindowUpdateType.MAX_MP))
			writeD(maxMp);

		if(containsMask(_flags, PartySmallWindowUpdateType.LEVEL))
			writeC(level);

		if(containsMask(_flags, PartySmallWindowUpdateType.CLASS_ID))
			writeH(class_id);

		if(containsMask(_flags, PartySmallWindowUpdateType.VITALITY_POINTS))
			writeD(0x00);
	}
}