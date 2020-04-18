package events.ChefMonkeyEvent;

import events.ScriptEvent;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.LongTimeEvent;

/**
 * Chef Monkey Event
 * URL https://eu.4gameforum.com/threads/603119/
 * @author Mobius
 */
public final class ChefMonkeyEvent extends LongTimeEvent implements ScriptEvent
{
	// NPC
	private static final int CHEF_MONKEY = 34292;
	
	private ChefMonkeyEvent()
	{
		addStartNpc(CHEF_MONKEY);
		addFirstTalkId(CHEF_MONKEY);
		addTalkId(CHEF_MONKEY);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "34292-01.htm";
	}

	public static ScriptEvent provider() {
		return new ChefMonkeyEvent();
	}
}
