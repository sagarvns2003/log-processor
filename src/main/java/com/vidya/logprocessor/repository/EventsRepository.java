package com.vidya.logprocessor.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vidya.logprocessor.repository.entity.Events;

@Repository
public interface EventsRepository extends CrudRepository<Events, String> {
}
