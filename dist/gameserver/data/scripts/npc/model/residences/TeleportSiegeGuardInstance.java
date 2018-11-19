package npc.model.residences;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 17:49/13.07.2011
 */
public class TeleportSiegeGuardInstance extends SiegeGuardInstance
{
	private static final long serialVersionUID = 1L;

	public TeleportSiegeGuardInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		//
	}
}
