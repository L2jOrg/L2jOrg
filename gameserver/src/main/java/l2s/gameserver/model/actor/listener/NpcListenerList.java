package l2s.gameserver.model.actor.listener;

import l2s.commons.listener.Listener;
import l2s.gameserver.listener.actor.npc.OnDecayListener;
import l2s.gameserver.listener.actor.npc.OnSpawnListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author G1ta0
 */
public class NpcListenerList extends CharListenerList
{
	public NpcListenerList(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public NpcInstance getActor()
	{
		return (NpcInstance) actor;
	}

	public void onSpawn()
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnSpawnListener.class.isInstance(listener))
					((OnSpawnListener) listener).onSpawn(getActor());

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnSpawnListener.class.isInstance(listener))
					((OnSpawnListener) listener).onSpawn(getActor());
	}

	public void onDecay()
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnDecayListener.class.isInstance(listener))
					((OnDecayListener) listener).onDecay(getActor());

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnDecayListener.class.isInstance(listener))
					((OnDecayListener) listener).onDecay(getActor());
	}
}