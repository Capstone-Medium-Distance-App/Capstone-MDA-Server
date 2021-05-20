package com.mda.server.web.dto;

import com.mda.server.domain.schedule.Schedule;
import lombok.Getter;

@Getter
public class LocationFinishFragment {

    private Integer scheduleId;
    private String scheduleName;
    private String schedulePlaceName;
    private String schedulePlaceArea;
    private String scheduleWithUserName;

    public LocationFinishFragment(Schedule entity){
        this.scheduleId=entity.getScheduleId();
        this.scheduleName=entity.getScheduleName();
        this.schedulePlaceArea=entity.getSchedulePlaceArea();
        this.schedulePlaceName=entity.getSchedulePlaceName();
        this.scheduleWithUserName=entity.getScheduleWithUserName();
    }

}
