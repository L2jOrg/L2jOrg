package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.petition.PetitionMainGroup;
import org.l2j.gameserver.model.petition.PetitionSubGroup;
import org.l2j.gameserver.network.l2.s2c.ExResponseShowContents;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class RequestExShowStepThree extends L2GameClientPacket
{
	private int _subId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_subId = buffer.get();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null || !Config.EX_NEW_PETITION_SYSTEM)
			return;

		PetitionMainGroup group = player.getPetitionGroup();
		if(group == null)
			return;

		PetitionSubGroup subGroup = group.getSubGroup(_subId);
		if(subGroup == null)
			return;

		player.sendPacket(new ExResponseShowContents(subGroup.getDescription(player.getLanguage())));
	}
}