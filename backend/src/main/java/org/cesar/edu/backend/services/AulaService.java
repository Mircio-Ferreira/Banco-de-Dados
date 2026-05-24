package org.cesar.edu.backend.services;

import org.cesar.edu.backend.repositories.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;

    @Autowired
    public AulaService(AulaRepository aulaRepository) {
        this.aulaRepository = aulaRepository;
    }

}
