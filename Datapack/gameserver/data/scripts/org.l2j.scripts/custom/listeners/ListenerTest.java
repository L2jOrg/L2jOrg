/*
 * Copyright © 2019 L2J Mobius
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
package custom.listeners;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.scripting.annotations.Disabled;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.annotations.*;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.item.OnItemCreate;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.holders.ItemHolder;

import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * An example usage of Listeners.
 * @author UnAfraid
 */
@Disabled
public class ListenerTest extends AbstractNpcAI
{
	private static final int[] ELPIES =
	{
		20432
	};
	
	private ListenerTest()
	{
		
		// Method preset listener registration
		// An set function which is a Consumer it has one parameter and doesn't returns anything!
		setAttackableAttackId(this::onAttackableAttack, ELPIES);
		
		// Manual listener registration
		Listeners.Global().addListener(new ConsumerEventListener(Listeners.Global(), EventType.ON_PLAYER_DLG_ANSWER, (OnPlayerDlgAnswer event) ->
		{
			LOGGER.info(getClass().getSimpleName() + ": " + event.getActiveChar() + " OnPlayerDlgAnswer: Answer: " + event.getAnswer() + " MessageId: " + event.getMessageId());
		}, this));
	}
	
	/**
	 * This method will be invoked as soon as an Attackable (Rabbits 20432) is being attacked from Player (a player)
	 * @param event
	 */
	private void onAttackableAttack(OnAttackableAttack event)
	{
		LOGGER.info(getClass().getSimpleName() + ": " + event.getClass().getSimpleName() + " invoked attacker: " + event.getAttacker() + " target: " + event.getTarget() + " damage: " + event.getDamage() + " skill: " + event.getSkill());
	}
	
	/**
	 * This method will be invoked as soon as Attackable (Rabbits 20432) are being killed by Player (a player)<br>
	 * This listener is registered into individual npcs container.
	 * @param event
	 */
	// Annotation listener registration
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(20432)
	private void onCreatureKill(OnCreatureDeath event)
	{
		LOGGER.info(getClass().getSimpleName() + ": " + event.getClass().getSimpleName() + " invoked attacker: " + event.getAttacker() + " target: " + event.getTarget());
	}
	
	/**
	 * This method will be invoked as soon as Siege of castle ids 1-9 starts<br>
	 * This listener is registered into individual castle container.
	 * @param event
	 */
	@RegisterEvent(EventType.ON_CASTLE_SIEGE_START)
	@RegisterType(ListenerRegisterType.CASTLE)
	@Range(from = 1, to = 9)
	private void onSiegeStart(OnCastleSiegeStart event)
	{
		LOGGER.info(getClass().getSimpleName() + ": The siege of " + event.getSiege().getCastle().getName() + " (" + event.getSiege().getCastle().getId() + ") has started!");
	}
	
	/**
	 * This method will be invoked as soon as Ancient Adena (5575) item is created on player's inventory (As new item!).<br>
	 * This listener is registered into individual items container.
	 * @param event
	 */
	@RegisterEvent(EventType.ON_ITEM_CREATE)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(5575)
	private void onItemCreate(OnItemCreate event)
	{
		LOGGER.info(getClass().getSimpleName() + ": Item [" + event.getItem() + "] has been created actor: " + event.getActiveChar() + " process: " + event.getProcess() + " reference: " + event.getReference());
	}
	
	/**
	 * Prioritized event notification <br>
	 * This method will be invoked as soon as creature from level range between 1 and 10 dies.<br>
	 * This listener is registered into individual npcs container.
	 * @param event
	 */
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	@RegisterType(ListenerRegisterType.NPC)
	@NpcLevelRange(from = 1, to = 10)
	@Priority(100)
	private void OnCreatureKill(OnCreatureDeath event)
	{
		// 70% chance to drop
		if (Rnd.get(100) >= 70)
		{
			return;
		}
		
		// Make sure a player killed this monster.
		if ((event.getAttacker() != null) && isPlayable(event.getAttacker()) && isAttackable(event.getTarget()))
		{
			final Attackable monster = (Attackable) event.getTarget();
			monster.dropItem(event.getAttacker().getActingPlayer(), new ItemHolder(57, Rnd.get(100, 1000)));
		}
	}
	
	/**
	 * This method will be invoked as soon a a player logs into the game.<br>
	 * This listener is registered into global players container.
	 * @param event
	 */
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		LOGGER.info(getClass().getSimpleName() + ": Player: " + event.getPlayer() + " has logged in!");
	}
	
	/**
	 * Prioritized event notification - Ensuring that this listener will be the first to receive notification.<br>
	 * Also this method interrupts notification to other listeners and taking over return if somehow it wasn't the first one to set.<br>
	 * This method will be invoked as soon a a creature dies.<br>
	 * This listener is registered into global players container.
	 * @param event
	 * @return termination return preventing the base code execution if needed.
	 */
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	@Priority(Integer.MAX_VALUE)
	private TerminateReturn onPlayerDeath(OnCreatureDeath event)
	{
		if (event.getTarget().isGM())
		{
			LOGGER.info(getClass().getSimpleName() + ": Player: " + event.getTarget() + " was prevented from dying!");
			return new TerminateReturn(true, true, true);
		}
		return null;
	}
	
	public static AbstractNpcAI provider() {
		return new ListenerTest();
	}
}