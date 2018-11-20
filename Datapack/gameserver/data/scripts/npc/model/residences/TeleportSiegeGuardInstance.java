package npc.model.residences;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.npc.NpcTemplate;

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
