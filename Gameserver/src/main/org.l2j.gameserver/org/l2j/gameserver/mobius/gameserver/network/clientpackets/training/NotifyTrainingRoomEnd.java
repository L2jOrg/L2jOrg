package org.l2j.gameserver.mobius.gameserver.network.clientpackets.training;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.TrainingHolder;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.training.ExTrainingZone_Leaving;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class NotifyTrainingRoomEnd extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {
        // Nothing to read
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        final TrainingHolder holder = activeChar.getTraingCampInfo();
        if (holder == null)
        {
            return;
        }

        if (holder.isTraining())
        {
            holder.setEndTime(System.currentTimeMillis());
            activeChar.setTraingCampInfo(holder);
            activeChar.enableAllSkills();
            activeChar.setIsInvul(false);
            activeChar.setInvisible(false);
            activeChar.setIsImmobilized(false);
            activeChar.teleToLocation(activeChar.getLastLocation());
            activeChar.sendPacket(ExTrainingZone_Leaving.STATIC_PACKET);
            holder.setEndTime(System.currentTimeMillis());
            activeChar.setTraingCampInfo(holder);
        }
    }
}
