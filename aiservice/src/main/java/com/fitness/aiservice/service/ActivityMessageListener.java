package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActivityMessageListener {

    private final RecommendationRepository recommendationRepository;
    private final ActivityAiService activityAiService;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){
        log.info("Received activity for processing: {}", activity.getId());
//        log.info("generated Recomendation : {}",activityAiService.generateRecomendation(activity) );
         Recommendation recommendation = activityAiService.generateRecomendation(activity);
        recommendationRepository.save(recommendation);

    }
}

