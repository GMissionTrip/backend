package com.gangchu.gangchutrip.archive.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArchivesResponseDTO {

    private Long archiveId;

    private String title;

    private Date startDate;

    private Date endDate;
}
