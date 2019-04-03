package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.holders.ItemPointHolder;
import org.l2j.gameserver.model.holders.LuckyGameDataHolder;
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sdw
 */
public class LuckyGameData extends IGameXmlReader{
    private final Map<Integer, LuckyGameDataHolder> _luckyGame = new HashMap<>();
    private final AtomicInteger _serverPlay = new AtomicInteger();

    private LuckyGameData() {
        load();
    }

    @Override
    public void load() {
        _luckyGame.clear();
        parseDatapackFile("data/LuckyGameData.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _luckyGame.size() + " lucky game data.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "luckygame", rewardNode ->
        {
            final LuckyGameDataHolder holder = new LuckyGameDataHolder(new StatsSet(parseAttributes(rewardNode)));

            forEach(rewardNode, "common_reward", commonRewardNode -> forEach(commonRewardNode, "item", itemNode ->
            {
                final StatsSet stats = new StatsSet(parseAttributes(itemNode));
                holder.addCommonReward(new ItemChanceHolder(stats.getInt("id"), stats.getDouble("chance"), stats.getLong("count")));
            }));

            forEach(rewardNode, "unique_reward", uniqueRewardNode -> forEach(uniqueRewardNode, "item", itemNode ->
            {
                holder.addUniqueReward(new ItemPointHolder(new StatsSet(parseAttributes(itemNode))));
            }));

            forEach(rewardNode, "modify_reward", uniqueRewardNode ->
            {
                holder.setMinModifyRewardGame(parseInteger(uniqueRewardNode.getAttributes(), "min_game"));
                holder.setMaxModifyRewardGame(parseInteger(uniqueRewardNode.getAttributes(), "max_game"));
                forEach(uniqueRewardNode, "item", itemNode ->
                {
                    final StatsSet stats = new StatsSet(parseAttributes(itemNode));
                    holder.addModifyReward(new ItemChanceHolder(stats.getInt("id"), stats.getDouble("chance"), stats.getLong("count")));
                });
            });

            _luckyGame.put(parseInteger(rewardNode.getAttributes(), "index"), holder);
        }));
    }

    public LuckyGameDataHolder getLuckyGameDataByIndex(int index) {
        return _luckyGame.get(index);
    }

    public int increaseGame() {
        return _serverPlay.incrementAndGet();
    }

    public static LuckyGameData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {

        protected static final LuckyGameData INSTANCE = new LuckyGameData();
    }
}
