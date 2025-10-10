package com.gangchu.gangchutrip.mission.pose.service;

import java.util.*;
import com.gangchu.gangchutrip.mission.pose.domain.*;

public class MirrorUtils {

    private static final Map<Joint, Joint> MIRROR = new EnumMap<>(Joint.class);
    static {
        MIRROR.put(Joint.LEFT_EYE, Joint.RIGHT_EYE); MIRROR.put(Joint.RIGHT_EYE, Joint.LEFT_EYE);
        MIRROR.put(Joint.LEFT_EAR, Joint.RIGHT_EAR); MIRROR.put(Joint.RIGHT_EAR, Joint.LEFT_EAR);
        MIRROR.put(Joint.LEFT_SHOULDER, Joint.RIGHT_SHOULDER); MIRROR.put(Joint.RIGHT_SHOULDER, Joint.LEFT_SHOULDER);
        MIRROR.put(Joint.LEFT_ELBOW, Joint.RIGHT_ELBOW); MIRROR.put(Joint.RIGHT_ELBOW, Joint.LEFT_ELBOW);
        MIRROR.put(Joint.LEFT_WRIST, Joint.RIGHT_WRIST); MIRROR.put(Joint.RIGHT_WRIST, Joint.LEFT_WRIST);
        MIRROR.put(Joint.LEFT_HIP, Joint.RIGHT_HIP); MIRROR.put(Joint.RIGHT_HIP, Joint.LEFT_HIP);
        MIRROR.put(Joint.LEFT_KNEE, Joint.RIGHT_KNEE); MIRROR.put(Joint.RIGHT_KNEE, Joint.LEFT_KNEE);
        MIRROR.put(Joint.LEFT_ANKLE, Joint.RIGHT_ANKLE); MIRROR.put(Joint.RIGHT_ANKLE, Joint.LEFT_ANKLE);
        // NOSE maps to itself
        MIRROR.put(Joint.NOSE, Joint.NOSE);
    }

    public static Map<Joint, KeyPoint> mirrorLR(Map<Joint, KeyPoint> kps) {
        Map<Joint, KeyPoint> out = new EnumMap<>(Joint.class);
        for (Joint j : Joint.values()) {
            Joint tgt = MIRROR.getOrDefault(j, j);
            KeyPoint src = kps.get(j);
            if (src != null) out.put(tgt, new KeyPoint(-src.getX(), src.getY(), src.getConfidence()));
        }
        return out;
    }
}
