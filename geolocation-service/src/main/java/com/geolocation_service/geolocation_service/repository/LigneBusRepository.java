package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.LigneBus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LigneBusRepository extends MongoRepository<LigneBus, String> {
    Optional<LigneBus> findByNumeroLigne(String numeroLigne);
    List<LigneBus> findByActif(boolean actif);
}