package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Set;

/**
 * @author Sdw
 */
public class NpcInfoAbnormalVisualEffect implements IClientOutgoingPacket
{
	private final L2Npc _npc;
	
	public NpcInfoAbnormalVisualEffect(L2Npc npc)
	{
		_npc = npc;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.NPC_INFO_ABNORMAL_VISUAL_EFFECT.writeId(packet);
		
		packet.writeD(_npc.getObjectId());
		packet.writeD(_npc.getTransformationDisplayId());
		
		final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
		packet.writeD(abnormalVisualEffects.size());
		for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects)
		{
			packet.writeH(abnormalVisualEffect.getClientId());
		}
		return true;
	}
}
