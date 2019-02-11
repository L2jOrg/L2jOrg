package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.PetInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;

import java.nio.ByteBuffer;

public class RequestChangePetName extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_name = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		PetInstance pet = activeChar.getPet();
		if(pet == null)
			return;

		if(pet.isDefaultName())
		{
			if(_name.length() < 1 || _name.length() > 8)
			{
				activeChar.sendPacket(SystemMsg.YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS_IN_LENGTH);
				return;
			}
			pet.setName(_name);
			pet.broadcastCharInfo();
			pet.updateControlItem();
		}
	}
}