package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;

public class ArtefactInstanceAction implements IActionHandler
{
	/**
	 * Manage actions when a player click on the ArtefactInstance.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Set the NpcInstance as target of the PlayerInstance player (if necessary)</li>
	 * <li>Send a Server->Client packet MyTargetSelected to the PlayerInstance player (display the select window)</li>
	 * <li>Send a Server->Client packet ValidateLocation to correct the NpcInstance position and heading on the client</li><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Client packet : Action, AttackRequest</li><BR>
	 * <BR>
	 */
	@Override
	public boolean action(Player player, L2Object target, boolean interact)
	{
		if (!((L2Npc) target).canTarget(player))
		{
			return false;
		}
		if (player.getTarget() != target)
		{
			player.setTarget(target);
		}
		// Calculate the distance between the PlayerInstance and the NpcInstance
		else if (interact && !((L2Npc) target).canInteract(player))
		{
			// Notify the PlayerInstance AI with AI_INTENTION_INTERACT
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2ArtefactInstance;
	}
}