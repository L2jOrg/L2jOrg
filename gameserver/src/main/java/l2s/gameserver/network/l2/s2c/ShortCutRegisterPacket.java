package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.ShortCut;

public class ShortCutRegisterPacket extends ShortCutPacket
{
	private ShortcutInfo _shortcutInfo;

	public ShortCutRegisterPacket(Player player, ShortCut sc)
	{
		_shortcutInfo = convert(player, sc);
	}

	@Override
	protected final void writeImpl()
	{
		_shortcutInfo.write(this);
	}
}