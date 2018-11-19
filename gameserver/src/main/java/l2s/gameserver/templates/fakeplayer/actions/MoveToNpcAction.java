package l2s.gameserver.templates.fakeplayer.actions;

import java.util.List;

import l2s.gameserver.ai.FakeAI;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

import org.dom4j.Element;

public class MoveToNpcAction extends MoveAction
{
	private final int _npcId;

	public MoveToNpcAction(int npcId, double chance)
	{
		super(chance);
		_npcId = npcId;
	}

	@Override
	public boolean performAction(FakeAI ai)
	{
		Player player = ai.getActor();
		NpcInstance npc = null;

		List<NpcInstance> npcs = GameObjectsStorage.getAllByNpcId(_npcId, true, true);
		for(NpcInstance n : npcs)
		{
			if(npc == null || n.getDistance(player) < npc.getDistance(player))
				npc = n;
		}

		if(npc != null)
		{
			int range = (int) (Math.max(30, player.getMinDistance(npc)) + 20);
			if(player.isInRangeZ(npc, range + 40))
			{
				player.doInteract(npc);
			}
			else
			{
				Location loc = npc.getLoc();
				if(player.getDistance(loc) > 2000 || !player.moveToLocation(loc, range, true))
					player.teleToLocation(loc, 0, range);
			}
			return true;
		}
		return false;
	}

	public static MoveToNpcAction parse(Element element)
	{
		int npcId = Integer.parseInt(element.attributeValue("id"));
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new MoveToNpcAction(npcId, chance);
	}
}