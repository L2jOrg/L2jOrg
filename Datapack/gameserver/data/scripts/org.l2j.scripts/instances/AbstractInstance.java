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
package instances;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.InstanceReenterType;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.List;

/**
 * Abstract class for Instances.
 * @author FallenAngel
 */
public abstract class AbstractInstance extends AbstractNpcAI
{
	private final int[] _templateIds;
	
	public AbstractInstance(int... templateId)
	{
		if (templateId.length == 0)
		{
			throw new IllegalStateException("No template ids were provided!");
		}
		_templateIds = templateId;
	}
	
	public int[] getTemplateId()
	{
		return _templateIds;
	}
	
	public boolean isInInstance(Instance instance)
	{
		return (instance != null) && Util.contains(_templateIds, instance.getTemplateId());
	}
	
	/**
	 * Get instance world associated with {@code player}.<br>
	 * @param player player who wants get instance world
	 * @return instance world if found, otherwise null
	 */
	public Instance getPlayerInstance(Player player) {
		return InstanceManager.getInstance().getPlayerInstance(player, false);
	}
	
	/**
	 * Show an on screen message to each player inside instance.
	 * @param instance instance where message should be broadcasted
	 * @param npcStringId the NPC string to display
	 * @param position the position of the message on the screen
	 * @param time the duration of the message in milliseconds
	 * @param params values of parameters to replace in the NPC String (like S1, C1 etc.)
	 */
	public void showOnScreenMsg(Instance instance, NpcStringId npcStringId, int position, int time, String... params)
	{
		instance.broadcastPacket(new ExShowScreenMessage(npcStringId, position, time, params));
	}
	
	/**
	 * Show an on screen message to each player inside instance.
	 * @param instance instance where message should be broadcasted
	 * @param npcStringId the NPC string to display
	 * @param position the position of the message on the screen
	 * @param time the duration of the message in milliseconds
	 * @param showEffect show visual effect near text
	 * @param params values of parameters to replace in the NPC String (like S1, C1 etc.)
	 */
	public void showOnScreenMsg(Instance instance, NpcStringId npcStringId, int position, int time, boolean showEffect, String... params)
	{
		instance.broadcastPacket(new ExShowScreenMessage(npcStringId, position, time, showEffect, params));
	}
	
	/**
	 * Put player into instance world.<br>
	 * If instance world doesn't found for player then try to create new one.
	 * @param player player who wants to enter into instance
	 * @param npc NPC which allows to enter into instance
	 * @param templateId template ID of instance where player wants to enter
	 */
	protected final void enterInstance(Player player, Npc npc, int templateId)
	{
		Instance instance = getPlayerInstance(player);
		if (instance != null) // Player has already any instance active
		{
			if (instance.getTemplateId() != templateId)
			{
				player.sendPacket(SystemMessageId.THE_MAXIMUM_NUMBER_OF_INSTANT_ZONES_HAS_BEEN_EXCEEDED_YOU_CANNOT_ENTER);
				return;
			}
			onEnter(player, instance, false);
		}
		else {
			final InstanceManager manager = InstanceManager.getInstance();
			final InstanceTemplate template = manager.getInstanceTemplate(templateId);
			if (template == null)
			{
				LOGGER.warn("Player {} wants to create instance with unknown template id {} !", player, templateId);
				return;
			}

			final List<Player> enterGroup = template.getEnterGroup(player);
			if (enterGroup == null) {
				LOGGER.warn("Instance {} has invalid group size limits!", template);
				return;
			}

			if (!player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS) && (!template.validateConditions(enterGroup, npc, this::showHtmlFile) || !validateConditions(enterGroup, npc, template))) {
				return;
			}
			
			// Check if maximum world count limit is exceeded
			if ((template.getMaxWorlds() != -1) && (manager.getWorldCount(templateId) >= template.getMaxWorlds()))
			{
				player.sendPacket(SystemMessageId.THE_NUMBER_OF_INSTANT_ZONES_THAT_CAN_BE_CREATED_HAS_BEEN_EXCEEDED_PLEASE_TRY_AGAIN_LATER);
				return;
			}

			for (Player member : enterGroup) {
				if (getPlayerInstance(member) != null) {
					enterGroup.forEach(p -> p.sendPacket(SystemMessageId.THE_MAXIMUM_NUMBER_OF_INSTANT_ZONES_HAS_BEEN_EXCEEDED_YOU_CANNOT_ENTER));
					return;
				}

				if (InstanceManager.getInstance().getInstanceTime(member, templateId) > 0) {
					enterGroup.forEach(p -> p.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET).addString(member.getName())));
					return;
				}
			}

			instance = manager.createInstance(template, player);
			
			// Move each player from enter group to instance
			for (Player member : enterGroup)
			{
				instance.addAllowed(member);
				onEnter(member, instance, true);
			}
			
			// Apply condition success effects
			template.applyConditionEffects(enterGroup);
			
			// Set re-enter for instances with re-enter on start
			if (instance.getReenterType() == InstanceReenterType.ON_ENTER)
			{
				instance.setReenterTime();
			}
		}
	}
	
	/**
	 * This function is called when player enter into instance trough NPC.
	 * @param player player who enter
	 * @param instance instance world where player enter
	 * @param firstEnter when {@code true} player enter first time, otherwise player entered multiple times
	 */
	protected void onEnter(Player player, Instance instance, boolean firstEnter)
	{
		teleportPlayerIn(player, instance);
	}
	
	/**
	 * This method is used to teleport player into instance by start NPC.<br>
	 * When you override whole method, XML teleport data won't be applied.
	 * @param player player which should be teleported
	 * @param instance instance where player should be teleported
	 */
	protected void teleportPlayerIn(Player player, Instance instance)
	{
		final Location loc = instance.getEnterLocation();
		if (loc != null)
		{
			player.teleToLocation(loc, instance);
		}
		else
		{
			LOGGER.warn("Missing start location for instance instance.getName() (" + instance.getId() + ")");
		}
	}
	
	/**
	 * This method is used to teleport player from instance world by NPC.
	 * @param player player which should be ejected
	 * @param instance instance from player should be removed
	 */
	protected void teleportPlayerOut(Player player, Instance instance)
	{
		instance.ejectPlayer(player);
	}
	
	/**
	 * Sets instance to finish state. <br>
	 * See {@link Instance#finishInstance()} for more details.
	 * @param player player used for determine current instance world
	 */
	protected void finishInstance(Player player)
	{
		final Instance inst = player.getInstanceWorld();
		if (inst != null)
		{
			inst.finishInstance();
		}
	}
	
	/**
	 * Sets instance to finish state.<br>
	 * See {@link Instance#finishInstance(int)} for more details.
	 * @param player player used for determine current instance world
	 * @param delay finish delay in minutes
	 */
	protected void finishInstance(Player player, int delay)
	{
		final Instance inst = player.getInstanceWorld();
		if (inst != null)
		{
			inst.finishInstance(delay);
		}
	}
	
	/**
	 * This method is supposed to be used for validation of additional conditions that are too much specific to instance world (to avoid useless core conditions).<br>
	 * These conditions are validated after conditions defined in XML template.
	 * @param group group of players which wants to enter (first player inside list is player who make enter request)
	 * @param npc NPC used for enter
	 * @param template template of instance world which should be created
	 * @return {@code true} when conditions are valid, otherwise {@code false}
	 */
	protected boolean validateConditions(List<Player> group, Npc npc, InstanceTemplate template)
	{
		return true;
	}
}