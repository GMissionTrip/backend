package com.gangchu.gangchutrip.mission.pose.service;


import static com.gangchu.gangchutrip.mission.pose.service.PoseMath.mid;
import static com.gangchu.gangchutrip.mission.pose.service.PoseMath.rotate;
import static com.gangchu.gangchutrip.mission.pose.service.PoseMath.scale;
import static com.gangchu.gangchutrip.mission.pose.service.PoseMath.toVec;
import static com.gangchu.gangchutrip.mission.pose.service.PoseMath.translate;
import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static java.lang.Math.abs;
import static java.util.Optional.ofNullable;

import com.gangchu.gangchutrip.mission.pose.domain.Joint;
import com.gangchu.gangchutrip.mission.pose.domain.KeyPoint;
import com.gangchu.gangchutrip.mission.pose.domain.PoseTemplate;
import com.gangchu.gangchutrip.mission.pose.domain.Vec2;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PoseEvaluationService {

    private final PoseTemplateRepository templateRepository; // 구현은 인메모리/DB 선택

    public EvaluateResult evaluate(String poseId, Map<Joint, KeyPoint> userKps, boolean allowMirror) {
        PoseTemplate tpl = templateRepository.getById(poseId);
        if (tpl == null) throw new IllegalArgumentException("Unknown missionPoseId: " + poseId);

        // 1) Normalize user keypoints (center, scale, rotation)
        NormResult normA = normalize(userKps);
        // 2) Optional mirror case
        NormResult normB = allowMirror ? normalize(MirrorUtils.mirrorLR(userKps)) : null;

        ScoreResult sA = scoreOnce(tpl, normA);
        ScoreResult sB = normB == null ? null : scoreOnce(tpl, normB);

        ScoreResult best = (sB == null || sA.totalScore >= sB.totalScore) ? sA : sB;

        boolean criticalOk = checkCriticals(best.perJointScore, tpl.getCriticalJoints(),
            defaultIfNull(tpl.getCriticalMinPerJointScore(), 0.7));
        boolean pass = best.totalScore >= defaultIfNull(tpl.getPassScoreThreshold(), 80.0)
            && best.coverage >= defaultIfNull(tpl.getCoverageThreshold(), 0.85)
            && criticalOk;

        return new EvaluateResult(pass, best.totalScore, best.perJointScore, best.coverage, best.notes);
    }

    private static double defaultIfNull(Double v, double d) { return v == null ? d : v; }

    private static boolean checkCriticals(Map<Joint, Double> perJoint, Set<Joint> criticals, double minScore) {
        if (criticals == null || criticals.isEmpty()) return true;
        long ok = criticals.stream().filter(j -> perJoint.getOrDefault(j, 0.0) >= minScore).count();
        return ok >= Math.ceil(criticals.size() * 0.8); // 80% 이상 통과
    }

    private static class NormResult { Map<Joint, KeyPoint> kps; double coverage; }

    private NormResult normalize(Map<Joint, KeyPoint> src) {
        // coverage by confidence ≥ 0.3 and non-null
        double present = src.values().stream().filter(k -> k != null && k.getConfidence() >= 0.3).count();
        double coverage = present / Joint.values().length;

        // center at mid-hip
        Optional<Vec2> midHipOpt = mid(src, Joint.LEFT_HIP, Joint.RIGHT_HIP);
        Optional<Vec2> midShOpt = mid(src, Joint.LEFT_SHOULDER, Joint.RIGHT_SHOULDER);
        if (midHipOpt.isEmpty() || midShOpt.isEmpty()) {
            NormResult nr = new NormResult(); nr.kps = src; nr.coverage = coverage; return nr;
        }
        Vec2 midHip = midHipOpt.get();
        Map<Joint, KeyPoint> out = translate(src, midHip);

        // scale by torso length (mid-hip to mid-shoulder)
        Vec2 midSh = midShOpt.get().sub(midHip);
        double scale = midSh.norm();
        if (scale > 1e-6) out = scale(out, scale);

        // rotate so shoulder line is horizontal (right - left)
        KeyPoint lsh = out.get(Joint.LEFT_SHOULDER); KeyPoint rsh = out.get(Joint.RIGHT_SHOULDER);
        if (lsh != null && rsh != null) {
            Vec2 shVec = toVec(rsh).sub(toVec(lsh));
            double theta = Math.atan2(shVec.y, shVec.x); // radians
            out = rotate(out, -theta);
        }

        NormResult nr = new NormResult();
        nr.kps = out; nr.coverage = coverage;
        return nr;
    }

    private static class ScoreResult {
        double totalScore; // 0~100
        Map<Joint, Double> perJointScore; // 0~1
        double coverage; // 0~1
        List<String> notes;
    }

    private ScoreResult scoreOnce(PoseTemplate tpl, NormResult norm) {
        Map<Joint, KeyPoint> kps = norm.kps;

        // A) Joint angle scores
        List<Joint> jointsOfInterest = List.of(
            Joint.LEFT_ELBOW, Joint.RIGHT_ELBOW,
            Joint.LEFT_KNEE, Joint.RIGHT_KNEE,
            Joint.LEFT_SHOULDER, Joint.RIGHT_SHOULDER,
            Joint.LEFT_HIP, Joint.RIGHT_HIP
        );

        Map<Joint, Double> perJoint = new EnumMap<>(Joint.class);
        List<String> notes = new ArrayList<>();

        for (Joint j : jointsOfInterest) {
            double ref = ofNullable(tpl.getJointAnglesDeg()).map(m -> m.get(j)).orElse(NaN);
            double usr = computeAngleAtJoint(kps, j);
            double score;
            if (isNaN(ref) || isNaN(usr)) {
                score = 0.0;
            } else {
                double delta = abs(ref - usr);
                double tol = resolveToleranceDeg(tpl, j, 20.0);
                score = Math.max(0.0, 1.0 - (delta / tol));
                if (delta > tol) notes.add(j+" 각도 부족/과다 (Δ=" + String.format("%.1f°", delta) + ")");
            }
            perJoint.put(j, score);
        }

        // B) Bone vector direction scores (upper/lower arms & legs)
        double sVec = meanNonNaN(List.of(
            vectorDirScore(kps, Joint.LEFT_SHOULDER, Joint.LEFT_ELBOW, tpl),
            vectorDirScore(kps, Joint.LEFT_ELBOW, Joint.LEFT_WRIST, tpl),
            vectorDirScore(kps, Joint.RIGHT_SHOULDER, Joint.RIGHT_ELBOW, tpl),
            vectorDirScore(kps, Joint.RIGHT_ELBOW, Joint.RIGHT_WRIST, tpl),
            vectorDirScore(kps, Joint.LEFT_HIP, Joint.LEFT_KNEE, tpl),
            vectorDirScore(kps, Joint.LEFT_KNEE, Joint.LEFT_ANKLE, tpl),
            vectorDirScore(kps, Joint.RIGHT_HIP, Joint.RIGHT_KNEE, tpl),
            vectorDirScore(kps, Joint.RIGHT_KNEE, Joint.RIGHT_ANKLE, tpl)
        ));

        // C) Length ratio scores
        double sLen = meanNonNaN(List.of(
            lengthRatioScore(kps, Joint.LEFT_SHOULDER, Joint.LEFT_ELBOW, tpl),
            lengthRatioScore(kps, Joint.LEFT_ELBOW, Joint.LEFT_WRIST, tpl),
            lengthRatioScore(kps, Joint.RIGHT_SHOULDER, Joint.RIGHT_ELBOW, tpl),
            lengthRatioScore(kps, Joint.RIGHT_ELBOW, Joint.RIGHT_WRIST, tpl),
            lengthRatioScore(kps, Joint.LEFT_HIP, Joint.LEFT_KNEE, tpl),
            lengthRatioScore(kps, Joint.LEFT_KNEE, Joint.LEFT_ANKLE, tpl),
            lengthRatioScore(kps, Joint.RIGHT_HIP, Joint.RIGHT_KNEE, tpl),
            lengthRatioScore(kps, Joint.RIGHT_KNEE, Joint.RIGHT_ANKLE, tpl)
        ));

        double sAng = meanNonNaN(new ArrayList<>(perJoint.values()));

        double total = 100.0 * (0.6 * sAng + 0.3 * sVec + 0.1 * sLen);

        ScoreResult r = new ScoreResult();
        r.totalScore = total;
        r.perJointScore = perJoint;
        r.coverage = norm.coverage;
        r.notes = notes;
        return r;
    }

    private double computeAngleAtJoint(Map<Joint, KeyPoint> kps, Joint j) {
        switch (j) {
            case LEFT_ELBOW: return PoseMath.angleAt(kps, Joint.LEFT_ELBOW, Joint.LEFT_SHOULDER, Joint.LEFT_WRIST);
            case RIGHT_ELBOW: return PoseMath.angleAt(kps, Joint.RIGHT_ELBOW, Joint.RIGHT_SHOULDER, Joint.RIGHT_WRIST);
            case LEFT_KNEE: return PoseMath.angleAt(kps, Joint.LEFT_KNEE, Joint.LEFT_HIP, Joint.LEFT_ANKLE);
            case RIGHT_KNEE: return PoseMath.angleAt(kps, Joint.RIGHT_KNEE, Joint.RIGHT_HIP, Joint.RIGHT_ANKLE);
            case LEFT_SHOULDER: return PoseMath.angleAt(kps, Joint.LEFT_SHOULDER, Joint.LEFT_ELBOW, Joint.LEFT_HIP);
            case RIGHT_SHOULDER: return PoseMath.angleAt(kps, Joint.RIGHT_SHOULDER, Joint.RIGHT_ELBOW, Joint.RIGHT_HIP);
            case LEFT_HIP: return PoseMath.angleAt(kps, Joint.LEFT_HIP, Joint.LEFT_SHOULDER, Joint.LEFT_KNEE);
            case RIGHT_HIP: return PoseMath.angleAt(kps, Joint.RIGHT_HIP, Joint.RIGHT_SHOULDER, Joint.RIGHT_KNEE);
            default: return Double.NaN;
        }
    }

    private double resolveToleranceDeg(PoseTemplate tpl, Joint j, double defaultDeg) {
        Map<String, Double> tol = tpl.getTolerancesDeg();
        if (tol == null || tol.isEmpty()) return defaultDeg;
        String key;
        switch (j) {
            case LEFT_ELBOW: case RIGHT_ELBOW: key = "ELBOW"; break;
            case LEFT_KNEE: case RIGHT_KNEE: key = "KNEE"; break;
            case LEFT_SHOULDER: case RIGHT_SHOULDER: key = "SHOULDER"; break;
            case LEFT_HIP: case RIGHT_HIP: key = "HIP"; break;
            default: key = "default";
        }
        return tol.getOrDefault(key, tol.getOrDefault("default", defaultDeg));
    }

    private double vectorDirScore(Map<Joint, KeyPoint> kps, Joint a, Joint b, PoseTemplate tpl) {
        KeyPoint ka = kps.get(a), kb = kps.get(b);
        if (ka == null || kb == null) return Double.NaN;
        // Compare against reference vector if provided; otherwise approximate using symmetric limb
        Map<Joint, KeyPoint> ref = tpl.getReferenceKeypoints();
        if (ref != null && ref.containsKey(a) && ref.containsKey(b)) {
            Vec2 vRef = toVec(ref.get(b)).sub(toVec(ref.get(a))).unit();
            Vec2 vUsr = toVec(kb).sub(toVec(ka)).unit();
            double ang = vRef.angleTo(vUsr); // radians
            double tolDeg = tpl.getVectorDirToleranceDeg() != null ? tpl.getVectorDirToleranceDeg() : 20.0;
            double score = Math.max(0.0, 1.0 - Math.toDegrees(ang) / tolDeg);
            return score;
        }
        return 1.0; // no reference – neutral
    }

    private double lengthRatioScore(Map<Joint, KeyPoint> kps, Joint a, Joint b, PoseTemplate tpl) {
        KeyPoint ka = kps.get(a), kb = kps.get(b);
        if (ka == null || kb == null) return Double.NaN;
        Map<Joint, KeyPoint> ref = tpl.getReferenceKeypoints();
        if (ref != null && ref.containsKey(a) && ref.containsKey(b)) {
            double usrLen = toVec(kb).sub(toVec(ka)).norm();
            double refLen = toVec(ref.get(b)).sub(toVec(ref.get(a))).norm();
            if (refLen < 1e-6) return Double.NaN;
            double rUser = usrLen / refLen;
            double tol = tpl.getLengthRatioTolerance() != null ? tpl.getLengthRatioTolerance() : 0.15;
            double score = Math.max(0.0, 1.0 - abs(rUser - 1.0) / tol);
            return score;
        }
        return 1.0;
    }

    private static double meanNonNaN(Collection<Double> vals) {
        double sum = 0; int n = 0;
        for (Double v : vals) {
            if (v != null && !isNaN(v)) { sum += v; n++; }
        }
        return n == 0 ? 0.0 : sum / n;
    }

    // --- DTO for internal result ---
    public static class EvaluateResult {
        public final boolean success;
        public final double score;
        public final Map<Joint, Double> perJoint;
        public final double coverage;
        public final List<String> notes;
        public EvaluateResult(boolean success, double score, Map<Joint, Double> perJoint, double coverage, List<String> notes) {
            this.success = success; this.score = score; this.perJoint = perJoint; this.coverage = coverage; this.notes = notes;
        }
    }
}
