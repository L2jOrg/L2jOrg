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
package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.settings.GeneralSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task that handles illegal player actions.
 */
public final class IllegalPlayerActionTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger("audit");

    private final String message;
    private final IllegalActionPunishmentType punishment;
    private final Player actor;

    public IllegalPlayerActionTask(Player actor, String message, IllegalActionPunishmentType punishment) {
        this.message = message;
        this.punishment = punishment;
        this.actor = actor;

        switch (punishment) {
            case KICK -> this.actor.sendMessage("You will be kicked for illegal action, GM informed.");
            case KICKBAN -> {
                if (!this.actor.isGM()) {
                    this.actor.setAccessLevel(-1, false, true);
                    this.actor.setAccountAccessLevel(-1);
                }
                this.actor.sendMessage("You are banned for illegal action, GM informed.");
            }
            case JAIL -> {
                this.actor.sendMessage("Illegal action performed!");
                this.actor.sendMessage("You will be teleported to GM Consultation Service area and jailed.");
            }
        }
    }

    @Override
    public void run() {
        LOGGER.info("AUDIT, {}, {}, {}", message, actor, punishment);

        if (!actor.isGM()) {
            switch (punishment) {
                case KICK -> Disconnection.of(actor).logout(false);
                case KICKBAN -> PunishmentManager.getInstance().startPunishment(new PunishmentTask(actor.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN, System.currentTimeMillis() + GeneralSettings.punishTime(), message, getClass().getSimpleName()));
                case JAIL -> PunishmentManager.getInstance().startPunishment(new PunishmentTask(actor.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + GeneralSettings.punishTime(), message, getClass().getSimpleName()));
                default -> LOGGER.info("No punishment to {}, message {}", actor, message);
            }
        }
    }
}
