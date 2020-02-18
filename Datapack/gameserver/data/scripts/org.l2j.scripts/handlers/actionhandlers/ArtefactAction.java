package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

public class ArtefactAction implements IActionHandler
{
    /**
     * Manage actions when a player click on the Artefact.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Set the Folk as target of the Player player (if necessary)</li>
     * <li>Send a Server->Client packet MyTargetSelected to the Player player (display the select window)</li>
     * <li>Send a Server->Client packet ValidateLocation to correct the Folk position and heading on the client</li><BR>
     * <BR>
     * <B><U> Example of use </U> :</B><BR>
     * <BR>
     * <li>Client packet : Action, AttackRequest</li><BR>
     * <BR>
     */
    @Override
    public boolean action(Player activeChar, WorldObject target, boolean interact)
    {
        if (!((Npc) target).canTarget(activeChar))
        {
            return false;
        }
        if (activeChar.getTarget() != target)
        {
            activeChar.setTarget(target);
        }
        // Calculate the distance between the Player and the Folk
        else if (interact && !((Npc) target).canInteract(activeChar))
        {
            // Notify the Player AI with AI_INTENTION_INTERACT
            activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
        }
        return true;
    }

    @Override
    public InstanceType getInstanceType()
    {
        return InstanceType.L2ArtefactInstance;
    }
}