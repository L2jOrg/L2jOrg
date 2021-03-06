/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.holders.DoorRequest;
import org.l2j.gameserver.model.holders.SummonRequest;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.nonNull;

/**
 * @author Dezmond_snz
 * @author JoeAlisson
 */
public final class DlgAnswer extends ClientPacket {
    public static final int ANY_STRING = 1983;
    private int messageId;
    private int answer;
    private int requesterId;

    @Override
    public void readImpl() {
        messageId = readInt();
        answer = readInt();
        requesterId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayerDlgAnswer(player, messageId, answer, requesterId), player, TerminateReturn.class);
        if ((term != null) && term.terminate()) {
            return;
        }

        if (messageId == ANY_STRING) {
            if (player.removeAction(PlayerAction.ADMIN_COMMAND)) {
                final String cmd = player.getAdminConfirmCmd();
                player.setAdminConfirmCmd(null);
                if (answer == 0) {
                    return;
                }

                // The 'useConfirm' must be disabled here, as we don't want to repeat that process.
                AdminCommandHandler.getInstance().useAdminCommand(player, cmd, false);
            }
        } else if ((messageId == SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT.getId()) || (messageId == SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW.getId())) {
            player.reviveAnswer(answer);
        } else if (messageId == SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId()) {
            final SummonRequest request = player.getRequest(SummonRequest.class);
            if (answer == 1 && nonNull(request) && request.getTarget().getObjectId() == requesterId) {
                player.teleToLocation(request.getTarget().getLocation(), true);
                player.removeRequest(SummonRequest.class);
            }
        } else if (messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId()) {

            final DoorRequest request = player.getRequest(DoorRequest.class);
            if (nonNull(request) && request.getDoor() == player.getTarget() && answer == 1) {
                request.getDoor().openMe();
                player.removeRequest(DoorRequest.class);
            }
        } else if (messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId()) {
            final DoorRequest request = player.getRequest(DoorRequest.class);
            if (nonNull(request) && request.getDoor() == player.getTarget() && answer == 1) {
                request.getDoor().closeMe();
                player.removeRequest(DoorRequest.class);
            }
        }
    }
}
