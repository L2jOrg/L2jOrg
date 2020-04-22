package handlers.admincommandhandlers;

import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.StringTokenizer;

import static java.lang.Math.min;
import static org.l2j.commons.util.Util.parseNextInt;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * @author JoeAlisson
 */
public class AdminLevel implements IAdminCommandHandler {

	private static final String[] ADMIN_COMMANDS = {
		"admin_add_level",
		"admin_set_level"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player gm) {
		final var target = gm.getTarget();
		if (!isPlayable(target)) {
			gm.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET); // incorrect target!
			return false;
		}

		final var st = new StringTokenizer(command, " ");
		final var actualCommand = st.nextToken();
		var level = parseNextInt(st, 0);

		if(level <= 0) {
			BuilderUtil.sendSysMessage(gm, "You must specify level greater than 0.");
			return false;
		}

		final var playableTarget = (Playable) target;
		final var maxAddLevel = LevelData.getInstance().getMaxLevel() - playableTarget.getLevel() -1;

		if (actualCommand.equalsIgnoreCase("admin_set_level")) {
			level = level - playableTarget.getLevel();
		}

		return playableTarget.getStats().addLevel((byte) min(maxAddLevel, level));
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
