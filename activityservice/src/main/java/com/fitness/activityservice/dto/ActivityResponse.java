package com.fitness.activityservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitness.activityservice.model.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityResponse {

    @JsonIgnore
    private String id;
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime date;
    private Map<String, Object> additionalMatrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAT;

}
