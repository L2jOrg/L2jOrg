package org.l2j.gameserver.templates.spawn;

import java.util.List;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.data.xml.holder.NpcHolder;
import org.l2j.gameserver.templates.npc.MinionData;
import org.l2j.gameserver.templates.npc.NpcTemplate;


/**
 * @author VISTALL
 * @date 4:33/19.05.2011
 */
public class SpawnNpcInfo
{
	private final NpcTemplate _template;
	private final int _max;
	private final MultiValueSet<String> _parameters;
	private final List<MinionData> _minions;

	public SpawnNpcInfo(int npcId, int max, MultiValueSet<String> set, List<MinionData> minions)
	{
		_template = NpcHolder.getInstance().getTemplate(npcId);
		_max = max;
		_parameters = set;
		_minions = minions;
	}

	public NpcTemplate getTemplate()
	{
		return _template;
	}

	public int getMax()
	{
		return _max;
	}

	public MultiValueSet<String> getParameters()
	{
		return _parameters;
	}

	public List<MinionData> getMinionData()
	{
		return _minions;
	}
}