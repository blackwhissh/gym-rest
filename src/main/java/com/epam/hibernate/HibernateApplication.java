package com.epam.hibernate;


import com.epam.hibernate.dto.AddTrainingRequest;
import com.epam.hibernate.dto.trainee.request.TraineeRegisterRequest;
import com.epam.hibernate.dto.trainer.request.TrainerRegisterRequest;
import com.epam.hibernate.dto.user.LoginDTO;
import com.epam.hibernate.entity.*;
import com.epam.hibernate.repository.TraineeRepository;
import com.epam.hibernate.repository.TrainerRepository;
import com.epam.hibernate.repository.TrainingTypeRepository;
import com.epam.hibernate.repository.UserRepository;
import com.epam.hibernate.service.TraineeService;
import com.epam.hibernate.service.TrainerService;
import com.epam.hibernate.service.TrainingService;
import com.epam.hibernate.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.naming.AuthenticationException;
import java.io.NotActiveException;
import java.nio.file.AccessDeniedException;
import java.sql.Date;

@SpringBootApplication
public class HibernateApplication {

    public static void main(String[] args) throws InterruptedException, AuthenticationException, NotActiveException, AccessDeniedException {
        ApplicationContext context = SpringApplication.run(HibernateApplication.class, args);

        TrainingTypeRepository trainingTypeRepository = context.getBean(TrainingTypeRepository.class);
        trainingTypeRepository.addTrainingTypes();
        TraineeRepository traineeRepository = context.getBean(TraineeRepository.class);
        TrainerRepository trainerRepository = context.getBean(TrainerRepository.class);
        UserRepository userRepository = context.getBean(UserRepository.class);

        TrainerService trainerService = context.getBean(TrainerService.class);
        TraineeService traineeService = context.getBean(TraineeService.class);
        TrainingService trainingService = context.getBean(TrainingService.class);
        traineeService.createProfile(new TraineeRegisterRequest(
                "t",
                "t",
                null,
                null
        ));
        Trainee trainee = traineeRepository.selectByUsername("t.t");
        trainee.getUser().setPassword("t");


        trainerService.createProfile(new TrainerRegisterRequest(
                "first", "trainer", TrainingTypeEnum.AGILITY
        ));
        trainerService.createProfile(new TrainerRegisterRequest(
                "second", "trainer", TrainingTypeEnum.AGILITY
        ));

        Trainer trainer = trainerRepository.selectByUsername("first.trainer");

        User admin = new User("admin", "admin", true, RoleEnum.ADMIN);
        admin.setUsername("admin");
        admin.setPassword("admin");
        UserService userService = context.getBean(UserService.class);
        userService.saveAdmin(admin);

        trainingService.addTraining(new AddTrainingRequest(new LoginDTO("admin", "admin"),
                "t.t","first.trainer","my first training",
                Date.valueOf("2020-10-10"), 50, TrainingTypeEnum.AGILITY));

        Thread.currentThread().join();
    }

}
