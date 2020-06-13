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
package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * This class manages requests (transactions) between two Player.
 *
 * @author kriau
 */
public class Request {
    private static final int REQUEST_TIMEOUT = 15; // in secs

    protected Player _player;
    protected Player _partner;
    protected boolean _isRequestor;
    protected boolean _isAnswerer;
    protected ClientPacket _requestPacket;

    public Request(Player player) {
        _player = player;
    }

    protected void clear() {
        _partner = null;
        _requestPacket = null;
        _isRequestor = false;
        _isAnswerer = false;
    }

    /**
     * @return the Player member of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
     */
    public Player getPartner() {
        return _partner;
    }

    /**
     * Set the Player member of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
     *
     * @param partner
     */
    private synchronized void setPartner(Player partner) {
        _partner = partner;
    }

    /**
     * Return the packet originally incomed from requester.
     *
     * @return
     */
    public ClientPacket getRequestPacket() {
        return _requestPacket;
    }

    /**
     * Set the packet incomed from requester.
     *
     * @param packet
     */
    private synchronized void setRequestPacket(ClientPacket packet) {
        _requestPacket = packet;
    }

    /**
     * Checks if request can be made and in success case puts both PC on request state.
     *
     * @param partner
     * @param packet
     * @return
     */
    public synchronized boolean setRequest(Player partner, ClientPacket packet) {
        if (partner == null) {
            _player.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return false;
        }
        if (partner.getRequest().isProcessingRequest()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
            sm.addString(partner.getName());
            _player.sendPacket(sm);
            return false;
        }
        if (isProcessingRequest()) {
            _player.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
            return false;
        }

        _partner = partner;
        _requestPacket = packet;
        setOnRequestTimer(true);
        _partner.getRequest().setPartner(_player);
        _partner.getRequest().setRequestPacket(packet);
        _partner.getRequest().setOnRequestTimer(false);
        return true;
    }

    private void setOnRequestTimer(boolean isRequestor) {
        _isRequestor = isRequestor;
        _isAnswerer = !isRequestor;
        ThreadPool.schedule(() -> clear(), REQUEST_TIMEOUT * 1000);
    }

    /**
     * Clears PC request state. Should be called after answer packet receive.
     */
    public void onRequestResponse() {
        if (_partner != null) {
            _partner.getRequest().clear();
        }
        clear();
    }

    /**
     * @return {@code true} if a transaction is in progress.
     */
    public boolean isProcessingRequest() {
        return _partner != null;
    }
}
