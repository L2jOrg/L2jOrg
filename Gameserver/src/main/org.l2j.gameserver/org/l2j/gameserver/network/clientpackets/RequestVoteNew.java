package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;

public final class RequestVoteNew extends ClientPacket {
    private int _targetId;

    @Override
    public void readImpl() {
        _targetId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final WorldObject object = activeChar.getTarget();
        if (!(object instanceof Player)) {
            if (object == null) {
                client.sendPacket(SystemMessageId.SELECT_TARGET);
            } else {
                client.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
            }
            return;
        }

        final Player target = (Player) object;

        if (target.getObjectId() != _targetId) {
            return;
        }

        if (target == activeChar) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECOMMEND_YOURSELF);
            return;
        }

        if (activeChar.getRecomLeft() <= 0) {
            client.sendPacket(SystemMessageId.YOU_ARE_OUT_OF_RECOMMENDATIONS_TRY_AGAIN_LATER);
            return;
        }

        if (target.getRecomHave() >= 255) {
            client.sendPacket(SystemMessageId.YOUR_SELECTED_TARGET_CAN_NO_LONGER_RECEIVE_A_RECOMMENDATION);
            return;
        }

        activeChar.giveRecom(target);

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT);
        sm.addPcName(target);
        sm.addInt(activeChar.getRecomLeft());
        client.sendPacket(sm);

        sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_BEEN_RECOMMENDED_BY_C1);
        sm.addPcName(activeChar);
        target.sendPacket(sm);

        client.sendPacket(new UserInfo(activeChar));
        target.broadcastUserInfo();

        client.sendPacket(new ExVoteSystemInfo(activeChar));
        target.sendPacket(new ExVoteSystemInfo(target));
    }
}
