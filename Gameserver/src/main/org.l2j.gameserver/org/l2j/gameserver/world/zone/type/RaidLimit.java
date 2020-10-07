package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.actor.tasks.player.PvPFlagTask;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class RaidLimit extends Zone {
	private static final Logger LOGGER = LoggerFactory.getLogger(RaidLimit.class);
	private int _bossId;

    public RaidLimit(int id) {
        super(id);
    }


    @Override
	public void setParameter(String name, String value)
	{
		LOGGER.info("bossId" + name + value);
		if (name.equals("bossId"))
				LOGGER.info("bossId" + name);
	    		_bossId = Integer.parseInt(value);
    }

    @Override
	protected void onEnter(Creature character)
	{
		if (!isEnabled()) {
			LOGGER.info("zone pas enable");
			return;
		}
		if (isPlayer(character)) {
			final Player player = character.getActingPlayer();
			player.setInsideZone(ZoneType.RAID_LIMIT, true);
			player.startPvPFlag();
			LOGGER.info("on rentre zone ");
		}
	}

	@Override
	protected void onExit(Creature character)
	{
		if (isPlayer(character)) {
			final Player player = character.getActingPlayer();
			character.setInsideZone(ZoneType.RAID_LIMIT, false);
			LOGGER.info("on sort zone ");
			player.stopPvpRegTask();
			if (character instanceof RaidBoss)
			{
				final RaidBoss raidboss = ((RaidBoss) character);
				if (raidboss.getId() == _bossId)
				{
					raidboss.teleToLocation(raidboss.getSpawn().getLocation(), 0);
					raidboss.setTarget(null);
					raidboss.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				}
			}
		}

	}

	@Override
	public void setEnabled(boolean state) {
		super.setEnabled(state);
		if (state) {
			forEachPlayer(player -> {
				revalidateInZone(player);

				if(nonNull(player.getPet())) {
					revalidateInZone(player.getPet());
				}

				player.getServitors().values().forEach(this::revalidateInZone);
			});
		} else {
			forEachCreature(this::removeCreature);
		}
	}
}
