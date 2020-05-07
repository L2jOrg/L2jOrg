package handlers.voicedcommandhandlers;

import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.returns.TerminateReturn;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author xban1x
 */
public final class ExperienceGain implements IVoicedCommandHandler
{
	private final String[] COMMANDS = new String[]
	{
		"expoff",
		"expon",
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String params)
	{
		if (command.equals("expoff"))
		{
			if (!player.getVariables().getBoolean("EXPOFF", false))
			{
				player.addListener(new FunctionEventListener(player, EventType.ON_PLAYABLE_EXP_CHANGED, (OnPlayableExpChanged event) -> onExperienceReceived(event.getPlayable(), event.getNewExp() - event.getOldExp()), this));
				player.getVariables().set("EXPOFF", true);
				player.sendMessage("Experience gain is disabled.");
			}
		}
		else if (command.equals("expon"))
		{
			if (player.getVariables().getBoolean("EXPOFF", false))
			{
				player.removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
				player.getVariables().set("EXPOFF", false);
				player.sendMessage("Experience gain is enabled.");
			}
		}
		return true;
	}
	
	private TerminateReturn onExperienceReceived(Playable playable, long exp)
	{
		if (isPlayer(playable) && playable.getActingPlayer().isDead())
		{
			return new TerminateReturn(false, false, false);
		}
		return new TerminateReturn(true, true, true);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}

	public static IVoicedCommandHandler provider() {
		return new ExperienceGain();
	}
}
