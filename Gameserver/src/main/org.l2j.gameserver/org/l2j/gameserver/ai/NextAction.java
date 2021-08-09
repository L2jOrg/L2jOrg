/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.ai;

import java.util.Collections;
import java.util.List;

/**
 * Class for AI action after some event.<br>
 * Has 2 array list for "work" and "break".
 *
 * @author Yaroslav
 * @author JoeAlisson
 */
public class NextAction {
    private final List<CtrlEvent> events;
    private final List<CtrlIntention> intentions;
    private final NextActionCallback callback;

    public NextAction(CtrlEvent event, CtrlIntention intention, NextActionCallback callback) {
        events = List.of(event);
        intentions = List.of(intention);
        this.callback = callback;
    }

    public void doAction() {
        if (callback != null) {
            callback.doWork();
        }
    }
    public List<CtrlEvent> getEvents() {
        return events != null ? events : Collections.emptyList();
    }

    public List<CtrlIntention> getIntentions() {
        return intentions != null ? intentions : Collections.emptyList();
    }

    @FunctionalInterface
    public interface NextActionCallback {
        void doWork();
    }
}