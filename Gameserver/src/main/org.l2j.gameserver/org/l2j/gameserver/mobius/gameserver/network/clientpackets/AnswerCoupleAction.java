package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExRotation;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class AnswerCoupleAction extends IClientIncomingPacket
{
    private int _charObjId;
    private int _actionId;
    private int _answer;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _actionId = packet.getInt();
        _answer = packet.getInt();
        _charObjId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        final L2PcInstance target = L2World.getInstance().getPlayer(_charObjId);
        if ((activeChar == null) || (target == null))
        {
            return;
        }
        if ((target.getMultiSocialTarget() != activeChar.getObjectId()) || (target.getMultiSociaAction() != _actionId))
        {
            return;
        }
        if (_answer == 0) // cancel
        {
            target.sendPacket(SystemMessageId.THE_COUPLE_ACTION_WAS_DENIED);
        }
        else if (_answer == 1) // approve
        {
            final int distance = (int) activeChar.calculateDistance2D(target);
            if ((distance > 125) || (distance < 15) || (activeChar.getObjectId() == target.getObjectId()))
            {
                client.sendPacket(SystemMessageId.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
                target.sendPacket(SystemMessageId.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
                return;
            }
            int heading = Util.calculateHeadingFrom(activeChar, target);
            activeChar.broadcastPacket(new ExRotation(activeChar.getObjectId(), heading));
            activeChar.setHeading(heading);
            heading = Util.calculateHeadingFrom(target, activeChar);
            target.setHeading(heading);
            target.broadcastPacket(new ExRotation(target.getObjectId(), heading));
            activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), _actionId));
            target.broadcastPacket(new SocialAction(_charObjId, _actionId));
        }
        else if (_answer == -1) // refused
        {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_COUPLE_ACTIONS_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(activeChar);
            target.sendPacket(sm);
        }
        target.setMultiSocialAction(0, 0);
    }
}
