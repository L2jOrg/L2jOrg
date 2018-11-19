package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.PetitionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.petition.PetitionMainGroup;
import l2s.gameserver.model.petition.PetitionSubGroup;

public final class RequestPetition extends L2GameClientPacket
{
	private String _content;
	private int _type;

	@Override
	protected void readImpl()
	{
		_content = readS();
		_type = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
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
