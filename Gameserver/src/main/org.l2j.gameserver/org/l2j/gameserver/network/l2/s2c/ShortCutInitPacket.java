package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.ShortCut;
import org.l2j.gameserver.network.l2.GameClient;

public class ShortCutInitPacket extends ShortCutPacket
{
	private List<ShortcutInfo> _shortCuts = Collections.emptyList();

	public ShortCutInitPacket(Player pl)
	{
		Collection<ShortCut> shortCuts = pl.getAllShortCuts();
		_shortCuts = new ArrayList<ShortcutInfo>(shortCuts.size());
		for(ShortCut shortCut : shortCuts)
			_shortCuts.add(convert(pl, shortCut));
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_shortCuts.size());

		for(final ShortcutInfo sc : _shortCuts)
			sc.write(buffer,this);
	}
}