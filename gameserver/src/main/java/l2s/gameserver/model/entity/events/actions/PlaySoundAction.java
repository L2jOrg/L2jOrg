package l2s.gameserver.model.entity.events.actions;

import java.util.List;

import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;

/**
 * @author VISTALL
 * @date 16:25/06.01.2011
 */
public class PlaySoundAction implements EventAction
{
	private int _range;
	private String _sound;
	private PlaySoundPacket.Type _type;

	public PlaySoundAction(int range, String s, PlaySoundPacket.Type type)
	{
		_range = range;
		_sound = s;
		_type = type;
	}

	@Override
	public void call(Event event)
	{
		GameObject object = event.getCenterObject();
		PlaySoundPacket packet = null;
		if(object != null)
			packet = new PlaySoundPacket(_type, _sound, 1, object.getObjectId(), object.getLoc());
		else
			packet = new PlaySoundPacket(_type, _sound, 0, 0, 0, 0, 0);

		List<Player> players = event.broadcastPlayers(_range);
		for(Player player : players)
		{
			if(player != null)
				player.sendPacket(packet);
		}
	}
}