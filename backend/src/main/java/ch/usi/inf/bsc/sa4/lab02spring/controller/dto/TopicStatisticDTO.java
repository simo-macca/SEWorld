package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing statistical information about a specific topic.
 *
 * <p>This record encapsulates various metrics related to a topic
 *
 * @param topicDid the unique identifier of the topic
 * @param topicTitle the name of the topic
 * @param avgUsersGrade the average grade of all users for this topic
 * @param userGrade the grade of the user for this topic
 * @param completionStage the completion percentage of the topic
 */
public record TopicStatisticDTO(
    UUID topicDid,
    String topicTitle,
    Double avgUsersGrade,
    Double userGrade,
    Double completionStage) {}
