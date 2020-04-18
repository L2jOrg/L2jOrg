package handlers.effecthandlers;



import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.serverpackets.sessionzones.TimedHuntingZoneList;

/**
 * @author Mobius
 */
public class AddHuntingTime extends AbstractEffect
{
	private final int _zoneId;
	private final long _time;
	
	private AddHuntingTime(StatsSet params)
	{
		_zoneId = params.getInt("zoneId", 0);
		_time = params.getLong("time", 3600000);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		final Player player = effected.getActingPlayer();
		if (player == null)
		{
			return;
		}
		
		final long currentTime = System.currentTimeMillis();
		long endTime = player.getVariables().getLong(PlayerVariables.HUNTING_ZONE_RESET_TIME + _zoneId, 0);
		if ((endTime > currentTime) && (((endTime - currentTime) + _time) >= Config.TIME_LIMITED_MAX_ADDED_TIME))
		{
			player.getInventory().addItem("AddHuntingTime effect refund", item.getId(), 1, player, false);
			player.sendMessage("You cannot exceed the time zone limit.");
			return;
		}
		
		if (player.isInTimedHuntingZone(_zoneId))
		{
			endTime = _time + player.getTimedHuntingZoneRemainingTime();
			player.getVariables().set(PlayerVariables.HUNTING_ZONE_RESET_TIME + _zoneId, currentTime + endTime);
			player.startTimedHuntingZone(_zoneId, endTime);
		}
		else
		{
			if ((endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY) < currentTime)
			{
				endTime = currentTime + Config.TIME_LIMITED_ZONE_INITIAL_TIME;
			}
			else if (endTime < currentTime)
			{
				endTime = currentTime;
			}
			player.getVariables().set(PlayerVariables.HUNTING_ZONE_RESET_TIME + _zoneId, endTime + _time);
		}
		
		player.sendPacket(new TimedHuntingZoneList(player));
	}

	public static final class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new AddHuntingTime(data);
		}

		@Override
		public String effectName() {
			return "AddHuntingTime";
		}
	}
}
