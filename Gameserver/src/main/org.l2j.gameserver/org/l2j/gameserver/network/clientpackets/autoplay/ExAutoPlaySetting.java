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
package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.engine.autoplay.AutoPlaySettings;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class ExAutoPlaySetting extends ClientPacket {

    private short size;
    private boolean active;
    private boolean pickUp;
    private short nextTargetMode;
    private boolean isNearTarget;
    private int usableHpPotionPercent;
    private boolean respectfulHunt;
    private int usableHpPetPotionPercent;

    @Override
    protected void readImpl() throws Exception {
        size = readShort();
        active = readBoolean();
        pickUp = readBoolean();
        nextTargetMode = readShort();
        isNearTarget = readBoolean();
        usableHpPotionPercent = readInt();
        usableHpPetPotionPercent = readInt();
        respectfulHunt = readBoolean();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        var settings = player.getAutoPlaySettings();
        if(isNull(settings)) {
            settings = new AutoPlaySettings(size, active, pickUp, nextTargetMode, isNearTarget, usableHpPotionPercent, usableHpPetPotionPercent, respectfulHunt);
            player.setAutoPlaySettings(settings);
        } else {
            settings.setSize(size);
            settings.setActive(active);
            settings.setAutoPickUpOn(pickUp);
            settings.setNextTargetMode(nextTargetMode);
            settings.setNearTarget(isNearTarget);
            settings.setUsableHpPotionPercent(usableHpPotionPercent);
            settings.setUsableHpPetPotionPercent(usableHpPetPotionPercent);
            settings.setRespectfulHunt(respectfulHunt);
        }
        if(active) {
            AutoPlayEngine.getInstance().startAutoPlay(client.getPlayer());
        } else {
            AutoPlayEngine.getInstance().stopAutoPlay(client.getPlayer());
        }
    }
}
