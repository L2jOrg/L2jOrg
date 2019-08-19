package org.l2j.gameserver.model.beautyshop;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sdw
 */
public class BeautyData {
    private final Map<Integer, BeautyItem> _hairList = new HashMap<>();
    private final Map<Integer, BeautyItem> _faceList = new HashMap<>();

    public final void addHair(BeautyItem hair) {
        _hairList.put(hair.getId(), hair);
    }

    public final void addFace(BeautyItem face) {
        _faceList.put(face.getId(), face);
    }

    public final Map<Integer, BeautyItem> getHairList() {
        return _hairList;
    }

    public final Map<Integer, BeautyItem> getFaceList() {
        return _faceList;
    }
}
