package org.l2j.gameserver.model.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.VehiclePathPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public final class ShuttleData {
    private final int _id;
    private final Location _loc;
    private final List<Integer> _doors = new ArrayList<>(2);
    private final List<ShuttleStop> _stops = new ArrayList<>(2);
    private final List<VehiclePathPoint[]> _routes = new ArrayList<>(2);

    public ShuttleData(StatsSet set) {
        _id = set.getInt("id");
        _loc = new Location(set);
    }

    public int getId() {
        return _id;
    }

    public Location getLocation() {
        return _loc;
    }

    public void addDoor(int id) {
        _doors.add(id);
    }

    public List<Integer> getDoors() {
        return _doors;
    }

    public void addStop(ShuttleStop stop) {
        _stops.add(stop);
    }

    public List<ShuttleStop> getStops() {
        return _stops;
    }

    public void addRoute(VehiclePathPoint[] route) {
        _routes.add(route);
    }

    public List<VehiclePathPoint[]> getRoutes() {
        return _routes;
    }
}
