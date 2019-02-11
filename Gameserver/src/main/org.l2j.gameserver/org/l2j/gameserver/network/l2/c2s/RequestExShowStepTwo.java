package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.PetitionGroupHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.petition.PetitionMainGroup;
import org.l2j.gameserver.network.l2.s2c.ExResponseShowStepTwo;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class RequestExShowStepTwo extends L2GameClientPacket
{
	private int _petitionGroupId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_petitionGroupId = buffer.get();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null || !Config.EX_NEW_PETITION_SYSTEM)
			return;

		PetitionMainGroup group = PetitionGroupHolder.getInstance().getPetitionGroup(_petitionGroupId);
		if(group == null)
			return;

		player.setPetitionGroup(group);
		player.sendPacket(new ExResponseShowStepTwo(player, group));
	}
}