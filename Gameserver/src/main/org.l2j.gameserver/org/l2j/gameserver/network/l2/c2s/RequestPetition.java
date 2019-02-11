package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.petition.PetitionMainGroup;
import org.l2j.gameserver.model.petition.PetitionSubGroup;

import java.nio.ByteBuffer;

public final class RequestPetition extends L2GameClientPacket
{
	private String _content;
	private int _type;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_content = readString(buffer);
		_type = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		if(Config.EX_NEW_PETITION_SYSTEM)
		{
			PetitionMainGroup group = player.getPetitionGroup();
			if(group == null)
				return;

			PetitionSubGroup subGroup = group.getSubGroup(_type);
			if(subGroup == null)
				return;

			subGroup.getHandler().handle(player, _type, _content);
		}
		else
		{
			PetitionManager.getInstance().handle(player, _type, _content);
		}
	}
}
