package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.actor.tasks.player.PvPFlagTask;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class RaidLimit extends Zone {
	private static final Logger LOGGER = LoggerFactory.getLogger(RaidLimit.class);
	private int _bossId;
	private RaidLimit(int id) {
        super(id);
    }


    @Override
	public void setParameter(String name, String value)
	{
		if (name.equals("bossId"))
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
			player.setInsideZone(ZoneType.PVP, true);
			player.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
			LOGGER.info("on rentre zone ");
		}
	}

	@Override
	protected void onExit(Creature character)
	{
		if (isPlayer(character)) {
			final Player player = character.getActingPlayer();
			character.setInsideZone(ZoneType.RAID_LIMIT, false);
			character.setInsideZone(ZoneType.PVP, false);
			character.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
			LOGGER.info("on sort zone ");
			if (player.getPvpFlag() == 0) {
				player.startPvPFlag();
			}
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
