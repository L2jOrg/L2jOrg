/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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