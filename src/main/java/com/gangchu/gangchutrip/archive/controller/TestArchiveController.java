package com.gangchu.gangchutrip.archive.controller;

import com.gangchu.gangchutrip.archive.repository.TravelRepository;
import com.gangchu.gangchutrip.global.entity.Travel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test/archives")
@RequiredArgsConstructor
public class TestArchiveController {

    private final TravelRepository travelRepository;

    @GetMapping
    public ResponseEntity<?> getTestArchives() {
        try {
            List<Travel> travels = travelRepository.findAll();
            
            List<Object> archives = travels.stream().map(travel -> {
                return new Object() {
                    public final Long id = travel.getId();
                    public final String title = travel.getTitle();
                    public final String date = travel.getStartDate() + " - " + travel.getEndDate();
                    public final String location = travel.getMainTheme() + " (" + travel.getSubTheme() + ")";
                    public final String background = "https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?w=400&h=300&fit=crop";
                    public final boolean isImage = true;
                    public final String description = travel.getMainTheme() + " 테마의 " + travel.getSubTheme() + " 여행입니다.";
                    public final String createdAt = travel.getCreatedDate() != null ? travel.getCreatedDate().toString() : "";
                    public final String updatedAt = travel.getModifiedDate() != null ? travel.getModifiedDate().toString() : "";
                };
            }).collect(Collectors.toList());

            return ResponseEntity.ok().body(new Object() {
                public final String message = "success";
                public final Object data = archives;
            });
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public final String message = "error";
                public final String error = e.getMessage();
            });
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getTestArchiveDetail(@org.springframework.web.bind.annotation.RequestParam Long archiveId) {
        try {
            Travel travel = travelRepository.findById(archiveId).orElse(null);
            
            if (travel == null) {
                return ResponseEntity.notFound().build();
            }

            Object archiveDetail = new Object() {
                public final Long id = travel.getId();
                public final String title = travel.getTitle();
                public final String date = travel.getStartDate() + " - " + travel.getEndDate();
                public final String location = travel.getMainTheme() + " (" + travel.getSubTheme() + ")";
                public final String background = "https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?w=400&h=300&fit=crop";
                public final boolean isImage = true;
                public final String description = travel.getMainTheme() + " 테마의 " + travel.getSubTheme() + " 여행입니다.";
                public final String createdAt = travel.getCreatedDate() != null ? travel.getCreatedDate().toString() : "";
                public final String updatedAt = travel.getModifiedDate() != null ? travel.getModifiedDate().toString() : "";
                public final Object[] missions = new Object[] {
                    new Object() {
                        public final String id = "1";
                        public final String time = "10:00";
                        public final String title = "첫 번째 미션";
                        public final String place = travel.getSubTheme() + " 방문";
                        public final String img = "https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?w=200&h=150&fit=crop";
                        public final String description = travel.getMainTheme() + " 테마의 첫 번째 미션입니다.";
                        public final int order = 1;
                    },
                    new Object() {
                        public final String id = "2";
                        public final String time = "14:00";
                        public final String title = "두 번째 미션";
                        public final String place = travel.getSubTheme() + " 체험";
                        public final String img = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=200&h=150&fit=crop";
                        public final String description = travel.getMainTheme() + " 테마의 두 번째 미션입니다.";
                        public final int order = 2;
                    }
                };
                public final Object[] photos = new Object[0];
            };

            return ResponseEntity.ok().body(new Object() {
                public final String message = "success";
                public final Object data = archiveDetail;
            });
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public final String message = "error";
                public final String error = e.getMessage();
            });
        }
    }
}
