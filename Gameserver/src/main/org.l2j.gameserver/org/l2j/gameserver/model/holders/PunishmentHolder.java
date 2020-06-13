/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 */
public final class PunishmentHolder {
    private final Map<String, Map<PunishmentType, PunishmentTask>> _holder = new ConcurrentHashMap<>();

    /**
     * Stores the punishment task in the Map.
     *
     * @param task
     */
    public void addPunishment(PunishmentTask task) {
        if (!task.isExpired()) {
            final String key = String.valueOf(task.getKey());
            _holder.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(task.getType(), task);
        }
    }

    /**
     * Removes previously stopped task from the Map.
     *
     * @param task
     */
    public void stopPunishment(PunishmentTask task) {
        final String key = String.valueOf(task.getKey());
        if (_holder.containsKey(key)) {
            task.stopPunishment();
            final Map<PunishmentType, PunishmentTask> punishments = _holder.get(key);
            punishments.remove(task.getType());
            if (punishments.isEmpty()) {
                _holder.remove(key);
            }
        }
    }

    public void stopPunishment(PunishmentType type) {
        _holder.values().stream().flatMap(p -> p.values().stream()).filter(p -> p.getType() == type).forEach(t ->
        {
            t.stopPunishment();
            final String key = String.valueOf(t.getKey());
            final Map<PunishmentType, PunishmentTask> punishments = _holder.get(key);
            punishments.remove(t.getType());
            if (punishments.isEmpty()) {
                _holder.remove(key);
            }
        });
    }

    /**
     * @param key
     * @param type
     * @return {@code true} if Map contains the current key and type, {@code false} otherwise.
     */
    public boolean hasPunishment(String key, PunishmentType type) {
        return getPunishment(key, type) != null;
    }

    /**
     * @param key
     * @param type
     * @return {@link PunishmentTask} by specified key and type if exists, null otherwise.
     */
    public PunishmentTask getPunishment(String key, PunishmentType type) {
        if (_holder.containsKey(key)) {
            return _holder.get(key).get(type);
        }
        return null;
    }
}
