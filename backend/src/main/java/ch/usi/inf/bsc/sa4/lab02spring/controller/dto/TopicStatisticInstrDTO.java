package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing statistical information about a specific topic from an
 * instructor's perspective.
 *
 * <p>This record encapsulates
 *
 * @param topicDid the unique identifier of the topic
 * @param topicTitle the title of the topic
 * @param avgUsersGrade the average grade of all users for this topic
 */
public record TopicStatisticInstrDTO(UUID topicDid, String topicTitle, Double avgUsersGrade) {}
