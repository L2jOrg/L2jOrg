package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.EnumMap;
import java.util.Map;

public final class ActionFailed implements IClientOutgoingPacket
{
	public static final ActionFailed STATIC_PACKET = new ActionFailed();
	private static final Map<SkillCastingType, ActionFailed> STATIC_PACKET_BY_CASTING_TYPE = new EnumMap<>(SkillCastingType.class);
	
	static
	{
		for (SkillCastingType castingType : SkillCastingType.values())
		{
			STATIC_PACKET_BY_CASTING_TYPE.put(castingType, new ActionFailed(castingType.getClientBarId()));
		}
	}
	
	private final int _castingType;
	
	private ActionFailed()
	{
		_castingType = 0;
	}
	
	private ActionFailed(int castingType)
	{
		_castingType = castingType;
	}
	
	public static ActionFailed get(SkillCastingType castingType)
	{
		return STATIC_PACKET_BY_CASTING_TYPE.getOrDefault(castingType, STATIC_PACKET);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.ACTION_FAIL.writeId(packet);
		
		packet.writeD(_castingType); // MagicSkillUse castingType
		return true;
	}
}
