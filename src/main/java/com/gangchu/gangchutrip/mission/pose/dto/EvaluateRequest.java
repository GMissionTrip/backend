package com.gangchu.gangchutrip.mission.pose.dto;

import com.gangchu.gangchutrip.mission.pose.domain.*;
import lombok.*;
import java.util.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class EvaluateRequest {

    private String missionPoseId;
    // 클라이언트(모바일/웹)에서 추출한 키포인트 전달 권장
    private Map<Joint, KeyPoint> userKeypoints;
    // (옵션) 이미지 업로드 URL – 별도 추출기 연동 시 사용
    private String userImageUrl;
    // 좌우 미러 허용 여부 (예: 셀카/후면 카메라 뒤집힘 보정)
    @Builder.Default private boolean allowMirror = true;
}
