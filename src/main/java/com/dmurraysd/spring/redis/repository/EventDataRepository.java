package com.dmurraysd.spring.redis.repository;

import com.dmurraysd.spring.rest.EventStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDataRepository extends CrudRepository<EventDataEntity, String> {
    List<EventDataEntity> findAllByEventStatus(EventStatus eventStatus);
}
