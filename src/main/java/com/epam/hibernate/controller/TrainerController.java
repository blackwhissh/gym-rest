package com.epam.hibernate.controller;

import com.epam.hibernate.config.LogEntryExit;
import com.epam.hibernate.dto.trainer.request.TrainerRegisterRequest;
import com.epam.hibernate.dto.trainer.request.TrainerTrainingsRequest;
import com.epam.hibernate.dto.trainer.request.UpdateTrainerRequest;
import com.epam.hibernate.dto.trainer.response.TrainerProfileResponse;
import com.epam.hibernate.dto.trainer.response.TrainerRegisterResponse;
import com.epam.hibernate.dto.trainer.response.TrainerTrainingsResponse;
import com.epam.hibernate.dto.trainer.response.UpdateTrainerResponse;
import com.epam.hibernate.dto.user.LoginDTO;
import com.epam.hibernate.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/trainer", consumes = {"application/JSON"}, produces = {"application/JSON", "application/XML"})
public class TrainerController {
    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping(value = "/register")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<TrainerRegisterResponse> registerTrainee(@RequestBody TrainerRegisterRequest request) {
        return trainerService.createProfile(request);
    }

    @GetMapping(value = "{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable String username,
                                                                    @RequestBody LoginDTO loginDTO) throws AuthenticationException {
        return trainerService.selectCurrentTrainerProfile(username, loginDTO);
    }

    @PutMapping(value = "/update/{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<UpdateTrainerResponse> updateTrainerProfile(@PathVariable String username,
                                                                      @RequestBody UpdateTrainerRequest request) throws AuthenticationException {
        return trainerService.updateTrainer(username, request);
    }

    @GetMapping(value = "/trainings/{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<List<TrainerTrainingsResponse>> getTrainingsList(@PathVariable String username,
                                                                           @RequestBody TrainerTrainingsRequest request) throws AuthenticationException {
        return trainerService.getTrainingList(username, request);
    }
}