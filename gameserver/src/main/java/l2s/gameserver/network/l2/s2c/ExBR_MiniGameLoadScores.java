package l2s.gameserver.network.l2.s2c;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import l2s.gameserver.instancemanager.games.MiniGameScoreManager;
import l2s.gameserver.model.Player;

import org.napile.pair.primitive.IntObjectPair;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

/**
 * @author VISTALL
 * @date  0:07:05/10.04.2010
 */
public class ExBR_MiniGameLoadScores extends L2GameServerPacket
{
	private int _place;
	private int _score;
	private int _lastScore;

	private IntObjectMap<List<Map.Entry<String, Integer>>> _entries = new TreeIntObjectMap<List<Map.Entry<String, Integer>>>();

	public ExBR_MiniGameLoadScores(Player player)
	{
		int lastBig = 0;
		int i = 1;

		for(IntObjectPair<Set<String>> entry : MiniGameScoreManager.getInstance().getScores().entrySet())
		{
			for(String name : entry.getValue())
			{
				List<Map.Entry<String, Integer>> set = _entries.get(i);
				if(set == null)
					_entries.put(i, (set = new ArrayList<Map.Entry<String, Integer>>()));

				if(name.equalsIgnoreCase(player.getName()))
					if(entry.getKey() > lastBig)
					{
						_place = i;
						_score = (lastBig = entry.getKey());
					}

				set.add(new AbstractMap.SimpleImmutableEntry<String, Integer>(name, entry.getKey()));

				i++;

				_lastScore = entry.getKey();

				if(i > 100)
					break;
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		writeD(_place); // place of last big score of player
		writeD(_score); // last big score of player
		writeD(0x00); //?
		writeD(_lastScore); //last score of list
		for(IntObjectPair<List<Map.Entry<String, Integer>>> entry : _entries.entrySet())
			for(Map.Entry<String, Integer> scoreEntry : entry.getValue())
			{
				writeD(entry.getKey());
				writeS(scoreEntry.getKey());
				writeD(scoreEntry.getValue());
			}
	}
}