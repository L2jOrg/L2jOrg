package handlers.targethandlers.affectscope;

import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.targets.AffectScope;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * Party and Clan affect scope implementation.
 * @author Nik
 */
public class PartyPledge implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		
		if (isPlayable(target)) {
			final Playable playable = (Playable) target;
			final Player player = playable.getActingPlayer();
			final Party party = player.getParty();
			
			// Create the target filter.
			final AtomicInteger affected = new AtomicInteger(0);
			final Predicate<Playable> filter = plbl ->
			{
				if ((affectLimit > 0) && (affected.get() >= affectLimit))
				{
					return false;
				}
				
				final Player p = plbl.getActingPlayer();
				if ((p == null) || p.isDead())
				{
					return false;
				}
				
				if (p != player)
				{
					if (((p.getParty() == null)) || ((p.getClanId() != player.getClanId()) && (party != player.getParty())))
					{
						return false;
					}
				}
				
				if ((affectObject != null) && !affectObject.checkAffectedObject(activeChar, p))
				{
					return false;
				}
				
				affected.incrementAndGet();
				return true;
			};
			
			// Add object of origin since its skipped in the forEachVisibleObjectInRange method.
			if (filter.test(playable))
			{
				action.accept(playable);
			}
			
			// Check and add targets.
			World.getInstance().forEachVisibleObjectInRange(playable, Playable.class, affectRange, c ->
			{
				if (filter.test(c))
				{
					action.accept(c);
				}
			});
		}
	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.PARTY_PLEDGE;
	}
}
