package com.gangchu.gangchutrip.mission.pose.extract;

import com.gangchu.gangchutrip.mission.pose.domain.Joint;
import com.gangchu.gangchutrip.mission.pose.domain.KeyPoint;
import java.io.IOException;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface PoseKeyPointExtractor {
    Map<Joint, KeyPoint> extract(MultipartFile image) throws IOException;
}
