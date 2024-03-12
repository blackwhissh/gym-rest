package com.epam.hibernate.controller;

import com.epam.hibernate.config.LogEntryExit;
import com.epam.hibernate.dto.AddTrainingRequest;
import com.epam.hibernate.dto.user.LoginDTO;
import com.epam.hibernate.entity.TrainingType;
import com.epam.hibernate.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.io.NotActiveException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/training", consumes = {"application/JSON"}, produces = {"application/JSON", "application/XML"})
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }
    @PostMapping(value = "/add")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<?> addTraining(@RequestBody AddTrainingRequest request) throws AccessDeniedException, AuthenticationException, NotActiveException {
        return trainingService.addTraining(request);
    }
    @GetMapping(value = "/types")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<List<TrainingType>> getTrainingTypes(@RequestBody LoginDTO loginDTO) throws AuthenticationException {
        return trainingService.getTrainingTypes(loginDTO);
    }

}
