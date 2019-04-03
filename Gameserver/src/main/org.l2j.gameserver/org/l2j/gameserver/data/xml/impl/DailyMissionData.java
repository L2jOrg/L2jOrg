package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.DailyMissionDataHolder;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sdw
 */
public class DailyMissionData extends IGameXmlReader{
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyMissionData.class);
    private final Map<Integer, List<DailyMissionDataHolder>> _dailyMissionRewards = new LinkedHashMap<>();
    private boolean _isAvailable;

    private DailyMissionData() {
        load();
    }

    @Override
    public void load() {
        _dailyMissionRewards.clear();
        parseDatapackFile("data/DailyMission.xml");
        _isAvailable = !_dailyMissionRewards.isEmpty();
        LOGGER.info("Loaded {} one day rewards.",  _dailyMissionRewards.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "reward", rewardNode ->
        {
            final StatsSet set = new StatsSet(parseAttributes(rewardNode));

            final List<ItemHolder> items = new ArrayList<>(1);
            forEach(rewardNode, "items", itemsNode -> forEach(itemsNode, "item", itemNode ->
            {
                final int itemId = parseInteger(itemNode.getAttributes(), "id");
                final int itemCount = parseInteger(itemNode.getAttributes(), "count");
                items.add(new ItemHolder(itemId, itemCount));
            }));

            set.set("items", items);

            final List<ClassId> classRestriction = new ArrayList<>(1);
            forEach(rewardNode, "classId", classRestrictionNode ->
            {
                classRestriction.add(ClassId.getClassId(Integer.parseInt(classRestrictionNode.getTextContent())));
            });
            set.set("classRestriction", classRestriction);

            // Initial values in case handler doesn't exists
            set.set("handler", "");
            set.set("params", StatsSet.EMPTY_STATSET);

            // Parse handler and parameters
            forEach(rewardNode, "handler", handlerNode ->
            {
                set.set("handler", parseString(handlerNode.getAttributes(), "name"));

                final StatsSet params = new StatsSet();
                set.set("params", params);
                forEach(handlerNode, "param", paramNode -> params.set(parseString(paramNode.getAttributes(), "name"), paramNode.getTextContent()));
            });

            final DailyMissionDataHolder holder = new DailyMissionDataHolder(set);
            _dailyMissionRewards.computeIfAbsent(holder.getId(), k -> new ArrayList<>()).add(holder);
        }));
    }

    public Collection<DailyMissionDataHolder> getDailyMissionData() {
        //@formatter:off
        return _dailyMissionRewards.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        //@formatter:on
    }

    public Collection<DailyMissionDataHolder> getDailyMissionData(L2PcInstance player) {
        //@formatter:off
        return _dailyMissionRewards.values()
                .stream()
                .flatMap(List::stream)
                .filter(o -> o.isDisplayable(player))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public Collection<DailyMissionDataHolder> getDailyMissionData(int id) {
        return _dailyMissionRewards.get(id);
    }

    public boolean isAvailable() {
        return _isAvailable;
    }

    public static DailyMissionData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DailyMissionData INSTANCE = new DailyMissionData();
    }
}
