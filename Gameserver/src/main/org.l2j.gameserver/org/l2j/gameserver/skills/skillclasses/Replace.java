package org.l2j.gameserver.skills.skillclasses;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.network.l2.s2c.FlyToLocationPacket;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.PositionUtils;

public class Replace extends Skill
{
	private final boolean _faceToFace;

	public Replace(StatsSet set)
	{
		super(set);
		_faceToFace = set.getBool("face-to-face", false);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!activeChar.isPlayer())
			return;

		final Player player = activeChar.getPlayer();
		final Location loc = player.getLoc();
		final int heading = PositionUtils.calculateHeadingFrom(player, target);

		if(_faceToFace)
			player.setHeading(PositionUtils.calculateHeadingFrom(target, player));

		player.broadcastPacket(new FlyToLocationPacket(player, target.getLoc(), FlyToLocationPacket.FlyType.DUMMY, 0, 0, 0));
		player.setLoc(target.getLoc());

		if(_faceToFace)
			target.setHeading(heading);

		target.broadcastPacket(new FlyToLocationPacket(target, loc, FlyToLocationPacket.FlyType.DUMMY, 0, 0, 0));
		target.setLoc(loc);

		for(Creature creature : player.getAroundCharacters(1000, 100))
		{
			if(creature.getTarget() == player)
				creature.setTarget(target);
			if(creature.getTarget() == target)
				creature.setTarget(creature);
		}
	}
}