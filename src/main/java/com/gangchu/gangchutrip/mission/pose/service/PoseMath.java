package com.gangchu.gangchutrip.mission.pose.service;

import com.gangchu.gangchutrip.mission.pose.domain.*;
import java.util.*;
import static java.lang.Math.*;

public class PoseMath {
    public static Vec2 toVec(KeyPoint k) { return new Vec2(k.getX(), k.getY()); }

    public static Optional<Vec2> mid(Map<Joint, KeyPoint> kps, Joint a, Joint b) {
        KeyPoint ka = kps.get(a), kb = kps.get(b);
        if (ka == null || kb == null) return Optional.empty();
        return Optional.of(new Vec2((ka.getX() + kb.getX())/2.0, (ka.getY() + kb.getY())/2.0));
    }

    public static double angleAt(Map<Joint, KeyPoint> kps, Joint center, Joint a, Joint b) {
        // angle between vectors (center->a) and (center->b)
        KeyPoint kc = kps.get(center), ka = kps.get(a), kb = kps.get(b);
        if (kc == null || ka == null || kb == null) return Double.NaN;
        Vec2 vc = toVec(kc);
        Vec2 v1 = toVec(ka).sub(vc);
        Vec2 v2 = toVec(kb).sub(vc);
        double ang = v1.angleTo(v2); // radians
        return toDegrees(ang);
    }

    public static Map<Joint, KeyPoint> translate(Map<Joint, KeyPoint> src, Vec2 delta) {
        Map<Joint, KeyPoint> out = new EnumMap<>(Joint.class);
        for (Map.Entry<Joint, KeyPoint> e : src.entrySet()) {
            KeyPoint k = e.getValue();
            out.put(e.getKey(), new KeyPoint(k.getX()-delta.x, k.getY()-delta.y, k.getConfidence()));
        }
        return out;
    }

    public static Map<Joint, KeyPoint> scale(Map<Joint, KeyPoint> src, double s) {
        Map<Joint, KeyPoint> out = new EnumMap<>(Joint.class);
        for (Map.Entry<Joint, KeyPoint> e : src.entrySet()) {
            KeyPoint k = e.getValue();
            out.put(e.getKey(), new KeyPoint(k.getX()/s, k.getY()/s, k.getConfidence()));
        }
        return out;
    }

    public static Map<Joint, KeyPoint> rotate(Map<Joint, KeyPoint> src, double rad) {
        Map<Joint, KeyPoint> out = new EnumMap<>(Joint.class);
        double c = cos(rad), s = sin(rad);
        for (Map.Entry<Joint, KeyPoint> e : src.entrySet()) {
            KeyPoint k = e.getValue();
            double x = k.getX(), y = k.getY();
            double xr = c*x - s*y, yr = s*x + c*y;
            out.put(e.getKey(), new KeyPoint(xr, yr, k.getConfidence()));
        }
        return out;
    }

    public static double dist(Vec2 a, Vec2 b) { return a.sub(b).norm(); }
}
