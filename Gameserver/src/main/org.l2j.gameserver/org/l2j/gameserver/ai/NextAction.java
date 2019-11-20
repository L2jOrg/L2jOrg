package org.l2j.gameserver.ai;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for AI action after some event.<br>
 * Has 2 array list for "work" and "break".
 *
 * @author Yaroslav
 */
public class NextAction {
    private List<CtrlEvent> _events;
    private List<CtrlIntention> _intentions;
    private NextActionCallback _callback;

    /**
     * Main constructor.
     *
     * @param events
     * @param intentions
     * @param callback
     */
    public NextAction(List<CtrlEvent> events, List<CtrlIntention> intentions, NextActionCallback callback) {
        _events = events;
        _intentions = intentions;
        setCallback(callback);
    }

    /**
     * Single constructor.
     *
     * @param event
     * @param intention
     * @param callback
     */
    public NextAction(CtrlEvent event, CtrlIntention intention, NextActionCallback callback) {
        if (_events == null) {
            _events = new ArrayList<>();
        }

        if (_intentions == null) {
            _intentions = new ArrayList<>();
        }

        if (event != null) {
            _events.add(event);
        }

        if (intention != null) {
            _intentions.add(intention);
        }
        setCallback(callback);
    }

    /**
     * Do action.
     */
    public void doAction() {
        if (_callback != null) {
            _callback.doWork();
        }
    }

    /**
     * @return the _event
     */
    public List<CtrlEvent> getEvents() {
        // If null return empty list.
        if (_events == null) {
            _events = new ArrayList<>();
        }
        return _events;
    }

    /**
     * @param event the event to set.
     */
    public void setEvents(ArrayList<CtrlEvent> event) {
        _events = event;
    }

    /**
     * @param event
     */
    public void addEvent(CtrlEvent event) {
        if (_events == null) {
            _events = new ArrayList<>();
        }

        if (event != null) {
            _events.add(event);
        }
    }

    /**
     * @param event
     */
    public void removeEvent(CtrlEvent event) {
        if (_events == null) {
            return;
        }
        _events.remove(event);
    }

    /**
     * @return the _callback
     */
    public NextActionCallback getCallback() {
        return _callback;
    }

    /**
     * @param callback the callback to set.
     */
    public void setCallback(NextActionCallback callback) {
        _callback = callback;
    }

    /**
     * @return the _intentions
     */
    public List<CtrlIntention> getIntentions() {
        // If null return empty list.
        if (_intentions == null) {
            _intentions = new ArrayList<>();
        }
        return _intentions;
    }

    /**
     * @param intentions the intention to set.
     */
    public void setIntentions(ArrayList<CtrlIntention> intentions) {
        _intentions = intentions;
    }

    /**
     * @param intention
     */
    public void addIntention(CtrlIntention intention) {
        if (_intentions == null) {
            _intentions = new ArrayList<>();
        }

        if (intention != null) {
            _intentions.add(intention);
        }
    }

    /**
     * @param intention
     */
    public void removeIntention(CtrlIntention intention) {
        if (_intentions == null) {
            return;
        }
        _intentions.remove(intention);
    }

    public interface NextActionCallback {
        void doWork();
    }
}