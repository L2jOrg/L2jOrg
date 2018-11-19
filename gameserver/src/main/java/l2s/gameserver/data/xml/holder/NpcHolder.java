package l2s.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.templates.npc.NpcTemplate;

public final class NpcHolder extends AbstractHolder
{
	private static final NpcHolder _instance = new NpcHolder();

	private TIntObjectHashMap<NpcTemplate> _npcs = new TIntObjectHashMap<>(20000);
	private TIntObjectHashMap<List<NpcTemplate>> _npcsByLevel;
	private NpcTemplate[] _allTemplates;
	private Map<String, NpcTemplate> _npcsNames;

	public static NpcHolder getInstance()
	{
		return _instance;
	}

	NpcHolder()
	{

	}

	public void addTemplate(NpcTemplate template)
	{
		_npcs.put(template.getId(), template);
	}

	public NpcTemplate getTemplate(int id)
	{
		NpcTemplate npc = ArrayUtils.valid(_allTemplates, id);
		if(npc == null)
		{
			warn("Not defined npc id : " + id + ", or out of range!", new Exception());
			return null;
		}
		return _allTemplates[id];
	}

	public NpcTemplate getTemplateByName(String name)
	{
		return _npcsNames.get(name.toLowerCase());
	}

	public List<NpcTemplate> getAllOfLevel(int lvl)
	{
		return _npcsByLevel.get(lvl);
	}

	public NpcTemplate[] getAll()
	{
		return _npcs.values(new NpcTemplate[_npcs.size()]);
	}

	private void buildFastLookupTable()
	{
		_npcsByLevel = new TIntObjectHashMap<>();
		_npcsNames = new HashMap<>();

		int highestId = 0;
		for(int id : _npcs.keys())
		{
			if(id > highestId)
			{
				highestId = id;
			}
		}

		_allTemplates = new NpcTemplate[highestId + 1];
		for(TIntObjectIterator<NpcTemplate> iterator = _npcs.iterator(); iterator.hasNext(); )
		{
			iterator.advance();
			int npcId = iterator.key();
			NpcTemplate npc = iterator.value();

			_allTemplates[npcId] = npc;

			List<NpcTemplate> byLevel;
			if((byLevel = _npcsByLevel.get(npc.level)) == null)
			{
				_npcsByLevel.put(npcId, byLevel = new ArrayList<NpcTemplate>());
			}
			byLevel.add(npc);

			_npcsNames.put(npc.name.toLowerCase(), npc);
		}
	}

	@Override
	protected void process()
	{
		buildFastLookupTable();
	}

	@Override
	public int size()
	{
		return _npcs.size();
	}

	@Override
	public void clear()
	{
		_npcsNames.clear();
		_npcs.clear();
	}
}