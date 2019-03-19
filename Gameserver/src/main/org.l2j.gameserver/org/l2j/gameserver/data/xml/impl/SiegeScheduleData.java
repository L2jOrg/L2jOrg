package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.SiegeScheduleDate;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.util.IGameXmlReader;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * @author UnAfraid
 */
public class SiegeScheduleData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiegeScheduleData.class);

    private final List<SiegeScheduleDate> _scheduleData = new ArrayList<>();

    protected SiegeScheduleData() {
        load();
    }

    public static SiegeScheduleData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public synchronized void load() {
        _scheduleData.clear();
        parseFile(new File("config/SiegeSchedule.xml"));
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _scheduleData.size() + " siege schedulers.");
        if (_scheduleData.isEmpty()) {
            _scheduleData.add(new SiegeScheduleDate(new StatsSet()));
            LOGGER.info(getClass().getSimpleName() + ": Emergency Loaded: " + _scheduleData.size() + " default siege schedulers.");
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node cd = n.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                    switch (cd.getNodeName()) {
                        case "schedule": {
                            final StatsSet set = new StatsSet();
                            final NamedNodeMap attrs = cd.getAttributes();
                            for (int i = 0; i < attrs.getLength(); i++) {
                                final Node node = attrs.item(i);
                                final String key = node.getNodeName();
                                String val = node.getNodeValue();
                                if ("day".equals(key)) {
                                    if (!Util.isDigit(val)) {
                                        val = Integer.toString(getValueForField(val));
                                    }
                                }
                                set.set(key, val);
                            }
                            _scheduleData.add(new SiegeScheduleDate(set));
                            break;
                        }
                    }
                }
            }
        }
    }

    private int getValueForField(String field) {
        try {
            return Calendar.class.getField(field).getInt(Calendar.class.getName());
        } catch (Exception e) {
            LOGGER.warn("", e);
            return -1;
        }
    }

    public List<SiegeScheduleDate> getScheduleDates() {
        return _scheduleData;
    }

    private static class SingletonHolder {
        protected static final SiegeScheduleData _instance = new SiegeScheduleData();
    }

}
