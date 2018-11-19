package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExResponseShowStepOne;

/**
 * @author VISTALL
 */
public class RequestExShowNewUserPetition extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null || !Config.EX_NEW_PETITION_SYSTEM)
			return;

		player.sendPacket(new ExResponseShowStepOne(player));
	}
}