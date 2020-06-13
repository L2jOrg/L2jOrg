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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordAck;

/**
 * (ch)cS{S} c: change pass? S: current password S: new password
 *
 * @author mrTJO
 */
public class RequestEx2ndPasswordReq extends ClientPacket {
    private int changePass;
    private String password;
    private String newPassword;

    @Override
    public void readImpl() {
        changePass = readByte();
        password = readString();
        if (changePass == 2) {
            newPassword = readString();
        }
    }

    @Override
    public void runImpl() {
        if (!SecondaryAuthManager.getInstance().isEnabled()) {
            return;
        }

        boolean success = false;

        if ((changePass == 0) && !client.hasSecondPassword()) {
            success = client.saveSecondPassword(password);
        } else if ((changePass == 2) && client.hasSecondPassword()) {
            success = client.changeSecondPassword(password, newPassword);
        }

        if (success) {
            client.sendPacket(new Ex2ndPasswordAck(changePass, Ex2ndPasswordAck.SUCCESS));
        }
    }
}
