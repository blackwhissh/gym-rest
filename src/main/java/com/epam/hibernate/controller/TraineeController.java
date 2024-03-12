package com.epam.hibernate.controller;

import com.epam.hibernate.config.LogEntryExit;
import com.epam.hibernate.dto.trainee.request.TraineeRegisterRequest;
import com.epam.hibernate.dto.trainee.request.TraineeTrainingsRequest;
import com.epam.hibernate.dto.trainee.request.UpdateTraineeRequest;
import com.epam.hibernate.dto.trainee.request.UpdateTrainersListRequest;
import com.epam.hibernate.dto.trainee.response.*;
import com.epam.hibernate.dto.trainer.TrainerListInfo;
import com.epam.hibernate.dto.user.LoginDTO;
import com.epam.hibernate.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/trainee", consumes = {"application/JSON"}, produces = {"application/JSON", "application/XML"})
public class TraineeController {
    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping(value = "/register")
    @LogEntryExit(showArgs = true, showResult = true)
    @Operation(summary = "Register Trainee Profile", description = "This method registers Trainee profile and returns " +
            "username and password")
    public ResponseEntity<TraineeRegisterResponse> registerTrainee(@RequestBody TraineeRegisterRequest request) {
        return traineeService.createProfile(request);
    }

    @GetMapping("{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable String username,
                                                                    @RequestBody LoginDTO loginDTO) throws AuthenticationException {
        return traineeService.selectCurrentTraineeProfile(username, loginDTO);
    }

    @PutMapping("/update/{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<UpdateTraineeResponse> updateTraineeProfile(@PathVariable String username,
                                                                      @RequestBody UpdateTraineeRequest request) throws AuthenticationException {
        LoginDTO loginDTO = request.getLoginDTO();
        return traineeService.updateTrainee(username, loginDTO, request);
    }

    @DeleteMapping("/delete/{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<?> deleteTraineeProfile(@PathVariable String username,
                                                  @RequestBody LoginDTO loginDTO) throws AccessDeniedException, AuthenticationException {
        traineeService.deleteTrainee(username, loginDTO);
        return ResponseEntity.ok().body("Trainee profile deleted successfully");
    }

    @GetMapping(value = "/not-assigned-trainers/{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<List<NotAssignedTrainer>> getNotAssignedTrainers(@PathVariable String username,
                                                                           @RequestBody LoginDTO loginDTO) throws AuthenticationException {
        return traineeService.notAssignedTrainersList(username, loginDTO);
    }

    @PutMapping(value = "/update-trainers/{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<List<TrainerListInfo>> updateTrainersList(@PathVariable String username,
                                                                    @RequestBody UpdateTrainersListRequest request) throws AuthenticationException {
        return traineeService.updateTrainersList(username, request);
    }

    @GetMapping(value = "/trainings/{username}")
    @LogEntryExit(showArgs = true, showResult = true)
    public ResponseEntity<List<TraineeTrainingsResponse>> getTrainingsList(@PathVariable String username,
                                                                           @RequestBody TraineeTrainingsRequest request) throws AuthenticationException {
        return traineeService.getTrainingList(username, request);
    }

}
