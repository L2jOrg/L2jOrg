package org.l2j.gameserver.data.xml;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.RandomCraftExtractDataHolder;
import org.l2j.gameserver.model.holders.RandomCraftRewardDataHolder;
import org.l2j.gameserver.model.holders.RandomCraftRewardItemHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.*;


public class RandomCraftData extends GameXmlReader {
    private static final Map<Integer, RandomCraftExtractDataHolder> EXTRACT_DATA = new HashMap<>();
    private static final Map<Integer, RandomCraftRewardDataHolder> REWARD_DATA = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomCraftData.class);


    protected RandomCraftData()
    {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return  ServerSettings.dataPackDirectory().resolve("data/xsd/RandomCraftExtractData.xsd");
    }

    @Override
    public void load() {
        EXTRACT_DATA.clear();
        parseDatapackFile("data/RandomCraftExtractData.xml");
        final int extractCount = EXTRACT_DATA.size();
        if (extractCount > 0)
        {
            LOGGER.info(getClass().getSimpleName() + ": Loaded " + extractCount + " extraction data.");
        }

        REWARD_DATA.clear();
        parseDatapackFile("data/RandomCraftRewardData.xml");
        final int rewardCount = REWARD_DATA.size();
        if (rewardCount > 4)
        {
            LOGGER.info(getClass().getSimpleName() + ": Loaded " + rewardCount + " rewards.");
        }
        else if (rewardCount > 0)
        {
            LOGGER.info(getClass().getSimpleName() + ": Random craft rewards should be more than " + rewardCount + ".");
            REWARD_DATA.clear();
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "extract", extractNode ->
        {
            forEach(extractNode, "item", itemNode ->
            {
                final StatsSet stats = new StatsSet(parseAttributes(itemNode));
                final int itemId = stats.getInt("id");
                final long points = stats.getLong("points");
                final long fee = stats.getLong("fee");
                EXTRACT_DATA.put(itemId, new RandomCraftExtractDataHolder(points, fee));
            });
        }));

        forEach(doc, "list", listNode -> forEach(listNode, "rewards", rewardNode ->
        {
            forEach(rewardNode, "item", itemNode ->
            {
                final StatsSet stats = new StatsSet(parseAttributes(itemNode));
                final int itemId = stats.getInt("id");
                final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
                if (item == null)
                {
                    LOGGER.info(getClass().getSimpleName() + " unexisting item reward: " + itemId);
                }
                else
                {
                    REWARD_DATA.put(itemId, new RandomCraftRewardDataHolder(stats.getInt("id"), stats.getLong("count", 1), Math.min(100, Math.max(0.00000000000001, stats.getDouble("chance", 100))), stats.getBoolean("announce", false)));
                }
            });
        }));
    }

    public boolean isEmpty()
    {
        return REWARD_DATA.isEmpty();
    }

    public RandomCraftRewardItemHolder getNewReward()
    {
        final List<RandomCraftRewardDataHolder> rewards = new ArrayList<>(REWARD_DATA.values());
        Collections.shuffle(rewards);

        RandomCraftRewardItemHolder result = null;
        while (result == null)
        {
            SEARCH: for (RandomCraftRewardDataHolder reward : rewards)
            {
                if (Rnd.get(100) < reward.getChance())
                {
                    result = new RandomCraftRewardItemHolder(reward.getItemId(), reward.getCount(), false, 20);
                    break SEARCH;
                }
            }
        }
        return result;
    }

    public boolean isAnnounce(int id)
    {
        final RandomCraftRewardDataHolder holder = REWARD_DATA.get(id);
        if (holder == null)
        {
            return false;
        }
        return holder.isAnnounce();
    }

    public long getPoints(int id)
    {
        final RandomCraftExtractDataHolder holder = EXTRACT_DATA.get(id);
        if (holder == null)
        {
            return 0;
        }
        return holder.getPoints();
    }

    public long getFee(int id)
    {
        final RandomCraftExtractDataHolder holder = EXTRACT_DATA.get(id);
        if (holder == null)
        {
            return 0;
        }
        return holder.getFee();
    }

    public static RandomCraftData getInstance()
    {
        return RandomCraftData.Singleton.INSTANCE;
    }

    private static class Singleton
    {
        protected static final RandomCraftData INSTANCE = new RandomCraftData();
    }

}
