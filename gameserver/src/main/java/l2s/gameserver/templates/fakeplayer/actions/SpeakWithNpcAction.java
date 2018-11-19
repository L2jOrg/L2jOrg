package l2s.gameserver.templates.fakeplayer.actions;

import java.util.List;

import l2s.gameserver.ai.FakeAI;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;

import org.dom4j.Element;

public class SpeakWithNpcAction extends AbstractAction
{
	private final int _npcId;
	private final String _bypass;

	public SpeakWithNpcAction(int npcId, String bypass, double chance)
	{
		super(chance);
		_npcId = npcId;
		_bypass = bypass;
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
		if(npc == null || !npc.isPeaceNpc() || !player.checkInteractionDistance(npc))
			return false;

		npc.onAction(player, false);
		if(_bypass != null)
			npc.onBypassFeedback(player, _bypass);
		return true;
	}

	public static SpeakWithNpcAction parse(Element element)
	{
		int npcId = Integer.parseInt(element.attributeValue("id"));
		String bypass = element.attributeValue("bypass");
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new SpeakWithNpcAction(npcId, bypass, chance);
	}
}