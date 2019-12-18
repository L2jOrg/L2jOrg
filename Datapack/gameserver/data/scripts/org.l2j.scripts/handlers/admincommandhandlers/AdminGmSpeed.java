package handlers.admincommandhandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.EnumSet;
import java.util.Set;
import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A retail-like implementation of //gmspeed builder command.
 * @author lord_rex
 */
public final class AdminGmSpeed implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_gmspeed",
	};
	
	private static final Set<Stat> SPEED_STATS = EnumSet.of(Stat.RUN_SPEED, Stat.WALK_SPEED, Stat.SWIM_RUN_SPEED, Stat.SWIM_WALK_SPEED, Stat.FLY_RUN_SPEED, Stat.FLY_WALK_SPEED);
	
	@Override
	public boolean useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		
		if (cmd.equals("admin_gmspeed"))
		{
			if (!st.hasMoreTokens())
			{
				BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
				return false;
			}
			final String token = st.nextToken();
			
			// Rollback feature for old custom way, in order to make everyone happy.
			if (Config.USE_SUPER_HASTE_AS_GM_SPEED)
			{
				AdminCommandHandler.getInstance().useAdminCommand(player, AdminSuperHaste.ADMIN_COMMANDS[0] + " " + token, false);
				return true;
			}
			
			if (!Util.isFloat(token))
			{
				BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
				return false;
			}
			final double runSpeedBoost = Double.parseDouble(token);
			if ((runSpeedBoost < 0) || (runSpeedBoost > 10))
			{
				// Custom limit according to SDW's request - real retail limit is unknown.
				BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
				return false;
			}
			
			final Creature targetCharacter;
			final WorldObject target = player.getTarget();
			if (isCreature(target))
			{
				targetCharacter = (Creature) target;
			}
			else
			{
				// If there is no target, let's use the command executer.
				targetCharacter = player;
			}
			
			SPEED_STATS.forEach(speedStat -> targetCharacter.getStats().removeFixedValue(speedStat));
			if (runSpeedBoost > 0)
			{
				SPEED_STATS.forEach(speedStat -> targetCharacter.getStats().addFixedValue(speedStat, targetCharacter.getTemplate().getBaseValue(speedStat, 120) * runSpeedBoost));
			}
			
			targetCharacter.getStats().recalculateStats(false);
			if (isPlayer(targetCharacter))
			{
				((Player) targetCharacter).broadcastUserInfo();
			}
			else
			{
				targetCharacter.broadcastInfo();
			}
			
			BuilderUtil.sendSysMessage(player, "[" + targetCharacter.getName() + "] speed is [" + (runSpeedBoost * 100) + "0]% fast.");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
