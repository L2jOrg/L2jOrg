package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.HennaHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.HennaItemInfoPacket;
import l2s.gameserver.templates.HennaTemplate;

public class RequestHennaItemInfo extends L2GameClientPacket
{
	// format  cd
	private int _symbolId;

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

		HennaTemplate template = HennaHolder.getInstance().getHenna(_symbolId);
		if(template != null)
			player.sendPacket(new HennaItemInfoPacket(template, player));
	}
}