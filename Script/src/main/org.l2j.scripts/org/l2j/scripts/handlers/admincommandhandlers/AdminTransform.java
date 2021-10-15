/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.engine.transform.TransformEngine;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;

import static org.l2j.commons.util.Util.isDigit;

/**
 * @author Mobius
 */
public class AdminTransform implements IAdminCommandHandler {

	private static final String[] ADMIN_COMMANDS = {
		"admin_transform",
		"admin_untransform",
		"admin_transform_menu",
	};
	
	@Override
	public boolean useAdminCommand(String command, Player gm) {
		if (command.equals("admin_transform_menu")) {
			AdminHtml.showAdminHtml(gm, "transform.htm");
			return true;
		} else if (command.startsWith("admin_untransform")) {
			return untransform(gm);
		}
		else if (command.startsWith("admin_transform")) {
			return transform(gm, command);
		}
		
		return false;
	}

	private boolean transform(Player gm, String command) {
		final var obj = gm.getTarget();
		if (!(obj instanceof Player player)) {
			gm.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}

		if (!isInTransformableState(gm, player)) {
			return false;
		}

		if (player.isTransformed()) {
			if (!command.contains(" ")) {
				player.stopTransformation(true);
				return true;
			}
			return false;
		}

		final String[] parts = command.split(" ");
		if ((parts.length != 2) || !isDigit(parts[1])) {
			BuilderUtil.sendSysMessage(gm, "Usage: //transform <id>");
			return false;
		}

		final int id = Integer.parseInt(parts[1]);
		if (!TransformEngine.getInstance().transform(player, id, true)) {
			player.sendMessage("Unknown transformation ID: " + id);
			return false;
		}
		return true;
	}

	private boolean isInTransformableState(Player gm, Player player) {
		if (player.isSitting()) {
			gm.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
			return false;
		}

		if (player.isInWater()) {
			gm.sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
			return false;
		}

		if (player.isFlyingMounted() || player.isMounted()) {
			gm.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_RIDING_A_PET);
			return false;
		}
		return true;
	}

	private boolean untransform(Player gm) {
		final var target = gm.getTarget() == null ? gm : gm.getTarget();
		if (!(target instanceof Creature creature) || !creature.isTransformed()) {
			gm.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		creature.stopTransformation(true);
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
