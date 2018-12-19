package org.l2j.gameserver.network.l2.s2c;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.gameserver.instancemanager.games.MiniGameScoreManager;
import org.l2j.gameserver.model.Player;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @date  0:07:05/10.04.2010
 */
public class ExBR_MiniGameLoadScores extends L2GameServerPacket
{
	private int _place;
	private int _score;
	private int _lastScore;

	private TIntObjectMap<List<Map.Entry<String, Integer>>> _entries = new TIntObjectHashMap<>();

	public ExBR_MiniGameLoadScores(Player player)
	{
		int lastBig = 0;
		int i = 1;

		var scores = MiniGameScoreManager.getInstance().getScores().iterator();

		while(scores.hasNext() && i <= 100) {
			scores.advance();

			for(String name : scores.value()) {
				List<Map.Entry<String, Integer>> set = _entries.get(i);
				if(set == null)
					_entries.put(i, (set = new ArrayList<>()));

				if(name.equalsIgnoreCase(player.getName()))
					if(scores.key() > lastBig)
					{
						_place = i;
						_score = (lastBig = scores.key());
					}

				set.add(new AbstractMap.SimpleImmutableEntry<>(name, scores.key()));

				i++;

				_lastScore = scores.key();

				if(i > 100)
					break;
			}
		}
	}

	@Override
	protected void writeImpl() {
		writeInt(_place); // place of last big score of player
		writeInt(_score); // last big score of player
		writeInt(0x00); //?
		writeInt(_lastScore); //last score of list

		_entries.forEachEntry((key, value) -> {
			for(Map.Entry<String, Integer> scoreEntry : value) {
				writeInt(key);
				writeString(scoreEntry.getKey());
				writeInt(scoreEntry.getValue());
			}

			return true;
		});
	}
}