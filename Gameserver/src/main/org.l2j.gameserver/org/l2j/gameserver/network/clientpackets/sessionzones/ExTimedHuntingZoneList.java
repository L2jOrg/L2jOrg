package org.l2j.gameserver.network.clientpackets.sessionzones;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.sessionzones.TimedHuntingZoneList;


/**
 * @author Mobius
 */
public class ExTimedHuntingZoneList extends ClientPacket
{
	@Override
	public void readImpl()
	{
	}
	
	@Override
	public void runImpl()
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		client.sendPacket(new TimedHuntingZoneList(player));
	}
}
