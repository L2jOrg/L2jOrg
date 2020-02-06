package org.l2j.gameserver.model.skills;

import org.l2j.gameserver.model.actor.Creature;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 */
public final class SkillChannelized {
    private final Map<Integer, Map<Integer, Creature>> _channelizers = new ConcurrentHashMap<>();

    public void addChannelizer(int skillId, Creature channelizer) {
        _channelizers.computeIfAbsent(skillId, k -> new ConcurrentHashMap<>()).put(channelizer.getObjectId(), channelizer);
    }

    public void removeChannelizer(int skillId, Creature channelizer) {
        getChannelizers(skillId).remove(channelizer.getObjectId());
    }

    public int getChannerlizersSize(int skillId) {
        return getChannelizers(skillId).size();
    }

    public Map<Integer, Creature> getChannelizers(int skillId) {
        return _channelizers.getOrDefault(skillId, Collections.emptyMap());
    }

    public void abortChannelization() {
        for (Map<Integer, Creature> map : _channelizers.values()) {
            for (Creature channelizer : map.values()) {
                channelizer.abortCast();
            }
        }
        _channelizers.clear();
    }

    public boolean isChannelized() {
        for (Map<Integer, Creature> map : _channelizers.values()) {
            if (!map.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
