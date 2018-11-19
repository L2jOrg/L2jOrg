package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.HennaHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.HennaUnequipInfoPacket;
import l2s.gameserver.templates.HennaTemplate;

public class RequestHennaUnequipInfo extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		HennaTemplate henna = HennaHolder.getInstance().getHenna(_symbolId);
		if(henna != null)
			player.sendPacket(new HennaUnequipInfoPacket(henna, player));
	}
}