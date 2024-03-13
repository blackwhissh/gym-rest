package com.epam.hibernate.service;

import com.epam.hibernate.Utils;
import com.epam.hibernate.dto.trainee.request.TraineeRegisterRequest;
import com.epam.hibernate.dto.trainee.request.TraineeTrainingsRequest;
import com.epam.hibernate.dto.trainee.request.UpdateTraineeRequest;
import com.epam.hibernate.dto.trainee.response.TraineeProfileResponse;
import com.epam.hibernate.dto.trainee.response.TraineeRegisterResponse;
import com.epam.hibernate.dto.trainee.response.TraineeTrainingsResponse;
import com.epam.hibernate.dto.trainee.response.UpdateTraineeResponse;
import com.epam.hibernate.dto.user.LoginDTO;
import com.epam.hibernate.entity.RoleEnum;
import com.epam.hibernate.entity.Trainee;
import com.epam.hibernate.entity.Training;
import com.epam.hibernate.entity.User;
import com.epam.hibernate.repository.TraineeRepository;
import com.epam.hibernate.repository.TrainerRepository;
import com.epam.hibernate.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static com.epam.hibernate.Utils.checkAdmin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private TrainerRepository trainerRepository;
    @InjectMocks
    private TraineeService traineeService;

    private Trainee createMockTrainee() {
        Trainee mockTrainee = mock(Trainee.class);
        User mockUser = mock(User.class);

        when(mockUser.getFirstName()).thenReturn("John");
        when(mockUser.getLastName()).thenReturn("Doe");
        when(mockUser.getActive()).thenReturn(true);


        when(mockTrainee.getDob()).thenReturn(null);
        when(mockTrainee.getAddress()).thenReturn(null);
        when(mockTrainee.getUser()).thenReturn(mockUser);

        return mockTrainee;
    }
    private List<Training> createMockTrainingList() {
        return Collections.emptyList();
    }


    @Test
    void createTraineeProfileOk() {
        when(userRepository.usernameExists(any())).thenReturn(false);

        when(traineeRepository.save(any(Trainee.class))).thenReturn(new Trainee());

        TraineeRegisterRequest validRequest = new TraineeRegisterRequest("John", "Doe",
                Date.valueOf("2001-10-10"), "123 Main St");

        ResponseEntity<TraineeRegisterResponse> responseEntity = traineeService.createProfile(validRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        TraineeRegisterResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getUsername());
        assertNotNull(responseBody.getPassword());
    }

    @Test
    void createTraineeProfileSameNameOk() {
        when(userRepository.usernameExists(any())).thenReturn(true);

        when(traineeRepository.save(any(Trainee.class))).thenReturn(new Trainee());

        TraineeRegisterRequest validRequest = new TraineeRegisterRequest("John", "Doe",
                Date.valueOf("2001-10-10"), "123 Main St");

        ResponseEntity<TraineeRegisterResponse> responseEntity = traineeService.createProfile(validRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        TraineeRegisterResponse responseBody = responseEntity.getBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.getUsername());
        assertNotNull(responseBody.getPassword());

        assertNotNull(responseBody);
        assertNotNull(responseBody.getUsername());
        assertNotNull(responseBody.getPassword());

        assertEquals("John.Doe0", responseBody.getUsername());
    }

    @Test
    void selectTraineeProfileOk() throws AuthenticationException {
        when(userService.authenticate(any(LoginDTO.class))).thenReturn(null);

        Trainee mockTrainee = createMockTrainee();
        when(traineeRepository.selectByUsername(any(String.class))).thenReturn(mockTrainee);

        LoginDTO loginDTO = new LoginDTO("admin","admin");

        ResponseEntity<TraineeProfileResponse> response = traineeService.selectCurrentTraineeProfile(
                "John.Doe",loginDTO
        );

        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void updateTraineeProfileOk() throws AuthenticationException {
        when(userService.authenticate(any(LoginDTO.class))).thenReturn(null);

        Trainee mockTrainee = createMockTrainee();
        when(traineeRepository.updateTrainee(any(String.class), any(), any(), any(), any(), any()))
                .thenReturn(mockTrainee);

        ResponseEntity<UpdateTraineeResponse> responseEntity = traineeService.updateTrainee(
                "John.Doe",  new UpdateTraineeRequest(
                        new LoginDTO("admin", "admin"), "James", "Smith",
                        null,null,true
                ));

        // Assertions
        assertEquals(200, responseEntity.getStatusCode().value());
    }

    @Test
    void deleteTraineeOk() throws AuthenticationException, AccessDeniedException {
        when(userService.authenticate(any(LoginDTO.class))).thenReturn(null);


        when(userRepository.findByUsername(any())).thenReturn(new User(RoleEnum.ADMIN));

        traineeService.deleteTrainee("John.Doe", new LoginDTO("admin", "admin"));

        verify(userService, times(1)).authenticate(any(LoginDTO.class));
        verify(traineeRepository, times(1)).deleteTrainee("John.Doe");
    }
    @Test
    void getTrainingListOk() throws AuthenticationException {
        when(userService.authenticate(any(LoginDTO.class))).thenReturn(null);

        List<Training> mockTrainingList = createMockTrainingList();
        when(traineeRepository.getTrainingList(anyString(), any(), any(), any(), any()))
                .thenReturn(mockTrainingList);

        TraineeTrainingsRequest request = new TraineeTrainingsRequest(
                new LoginDTO("admin", "admin"),null,null,null,null
        );
        ResponseEntity<List<TraineeTrainingsResponse>> responseEntity = traineeService.getTrainingList("John.Doe", request);

        assertEquals(200, responseEntity.getStatusCode().value());
    }


//
//    @Test
//    void notAssignedTrainersList() throws AuthenticationException {
//        Trainee trainee = traineeService.createProfile("test", "test", true, null,null);
//
//        Trainer trainer1 = new Trainer(new TrainingType(TrainingTypeEnum.AGILITY),
//                new User("trainer1","trainer1",true,RoleEnum.TRAINER));
//        Trainer trainer2 = new Trainer(new TrainingType(TrainingTypeEnum.AGILITY),
//                new User("trainer2","trainer2",true,RoleEnum.TRAINER));
//
//        trainerRepository.save(trainer1);
//        trainerRepository.save(trainer2);
//
//        System.out.println(trainer1);
//
//
//        when(trainerRepository.getAllTrainers()).thenReturn(List.of(trainer1,trainer2));
//
//        List<Trainer> trainers = new ArrayList<>();
//        trainers.add(trainer1);
//        trainers.add(trainer2);
//
//        List<Trainer> expected = traineeService.notAssignedTrainersList(trainee.getUser(),trainee.getUser().getUsername());
//
//        assertEquals(trainers.size(),expected.size());
//    }
}