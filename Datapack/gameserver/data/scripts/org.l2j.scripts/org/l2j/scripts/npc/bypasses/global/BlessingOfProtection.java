package org.l2j.scripts.npc.bypasses.global;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.listener.hooks.ListenerHook;
import org.l2j.gameserver.listener.hooks.ListenerHookType;
import org.l2j.gameserver.listener.script.OnInitScriptListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
**/
public class BlessingOfProtection extends ListenerHook implements OnInitScriptListener
{
	@Override
	public void onInit()
	{
		addHookGlobal(ListenerHookType.NPC_ASK);
	}

	@Override
	public void onNpcAsk(NpcInstance npc, int ask, long reply, Player player)
	{
		if(ask == -20151209)
		{
			if(reply == 1)
			{
				if(player.getLevel() < 40)
					npc.forceUseSkill(SkillHolder.getInstance().getSkill(5182, 1), player);
				else
					npc.showChatWindow(player, "teleporter/no_bless.htm", true);
			}
		}
	}
}