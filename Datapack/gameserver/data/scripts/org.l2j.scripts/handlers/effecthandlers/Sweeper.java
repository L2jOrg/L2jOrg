package handlers.effecthandlers;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import java.util.Collection;

import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Sweeper effect implementation.
 * @author Zoey76
 */
public final class Sweeper extends AbstractEffect {

	public Sweeper(StatsSet params) {
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!isPlayer(effector) || !isAttackable(effected)) {
			return;
		}
		
		final Player player = effector.getActingPlayer();
		final Attackable monster = (Attackable) effected;
		if (!monster.checkSpoilOwner(player, false)) {
			return;
		}
		
		if (!player.getInventory().checkInventorySlotsAndWeight(monster.getSpoilLootItems(), false, false)) {
			return;
		}
		
		final Collection<ItemHolder> items = monster.takeSweep();
		if (items != null) {
			for (ItemHolder sweepedItem : items) {
				final Party party = player.getParty();
				if (party != null) {
					party.distributeItem(player, sweepedItem, true, monster);
				} else {
					player.addItem("Sweeper", sweepedItem, effected, true);
				}
			}
		}
	}
}
