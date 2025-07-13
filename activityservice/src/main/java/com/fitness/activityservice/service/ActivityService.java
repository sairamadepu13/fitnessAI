package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ActivityService {

    private final UserValidationService userValidationService;
    private final ActivityRepository activityRepository;

//    public ActivityService(UserValidationService userValidationService, ActivityRepository activityRepository){
//        this.userValidationService = userValidationService;
//        this.activityRepository = activityRepository;
//    }


    public ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if(!isValidUser){
            throw new RuntimeException("Invalid USer " + request.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMatrics(request.getAdditionalMetrics())
                .build();
        Activity savedActivity = activityRepository.save(activity);

        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setDate(activity.getStartTime());
        response.setAdditionalMatrics(activity.getAdditionalMatrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAT(activity.getUpdatedAT());
        return response;
    }


    public List<ActivityResponse> getAllActivity(String userId) {
        List<Activity> responses = activityRepository.findByUserId(userId);
        System.out.println(responses);
        List<ActivityResponse> responses1 = responses.stream().map(this::mapToResponse).collect(Collectors.toList());
        return responses1;
    }

    public ActivityResponse getActivity(String id) {

        return activityRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NoSuchElementException("Activity not found with id: " + id));
    }
}
