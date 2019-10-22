package com.tron.gymservice.services;

import com.tron.gymservice.dto.UserDeleteDto;
import com.tron.gymservice.dto.UserPhysicalInfoDto;
import com.tron.gymservice.dto.UserRegistrationDto;
import com.tron.gymservice.entity.QandA;
import com.tron.gymservice.entity.UserMasterInfo;
import com.tron.gymservice.entity.UserMedicInfo;
import com.tron.gymservice.entity.UserPhysicalInfo;
import com.tron.gymservice.exceptions.MasterInfoNotFoundException;
import com.tron.gymservice.repository.UserMasterInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserMasterInfoServiceImpl implements UserMasterInfoService {

    @Autowired
    private UserMasterInfoRepository userMasterInfoRepository;

    @Override
    public List<UserMasterInfo> findAll() {
        return userMasterInfoRepository.findAll();
    }

    @Override
    @Transactional
    public UserMasterInfo updatePhysicalInfo(UserPhysicalInfoDto userPhysicalInfoDto) {
        Optional<UserMasterInfo> userMasterInfo=userMasterInfoRepository
                .findById(userPhysicalInfoDto.getUserId());
        userMasterInfo.ifPresent(u -> {
            String activityString = ListToCommaSeparatedString(userPhysicalInfoDto.getActivities());
            String exerciseList= ListToCommaSeparatedString(userPhysicalInfoDto.getExerciseList());
            UserPhysicalInfo  userPhysic = u.getUserPhysicalInfo();
            userPhysic.setHeight(userPhysicalInfoDto.getHeight());
            userPhysic.setWeight(userPhysicalInfoDto.getWeight());
            userPhysic.setBloodGroup(userPhysicalInfoDto.getBloodGroup());
            userPhysic.setActivities(activityString);
            userPhysic.setExerciseList(exerciseList);
            UserMedicInfo userMedicInfo= userPhysic.getUserMedicInfo();
            userMedicInfo.setIsCardiacPatient(userPhysicalInfoDto.getUserMedicInfo().getIsCardiacPatient());
            userMedicInfo.setIsDiabeticsPatient(userPhysicalInfoDto.getUserMedicInfo().getIsDiabeticsPatient());
            userMedicInfo.setNote(userPhysicalInfoDto.getUserMedicInfo().getNote());
            userPhysic.setUserMedicInfo(userMedicInfo);
        });
        userMasterInfo.orElseThrow(MasterInfoNotFoundException::new);
        return userMasterInfoRepository.save(userMasterInfo.get());
    }

    private String ListToCommaSeparatedString(List<String> listOfString) {
        return Optional.ofNullable(listOfString)
                        .map(a->a.stream().collect(Collectors.joining(","))).orElse("");
    }

    @Override
    @Transactional
    public UserMasterInfo save(UserRegistrationDto userRegistrationDto) {
        UserMasterInfo userMasterInfo=new UserMasterInfo();
        UserPhysicalInfo userPhysicalInfo=new UserPhysicalInfo();
        UserMedicInfo medicInfo=new UserMedicInfo();
        userPhysicalInfo.setUserMedicInfo(medicInfo);
        userMasterInfo.setUserName(userRegistrationDto.getUserName());
        userMasterInfo.setPassword(userRegistrationDto.getPassword());
        userMasterInfo.setRoleId(1);
        userMasterInfo.setPhoneNo(userRegistrationDto.getPhoneNo());
        userMasterInfo.setEmailId(userRegistrationDto.getEmailId());
        userMasterInfo.setUserPhysicalInfo(userPhysicalInfo);
        return userMasterInfoRepository.save(userMasterInfo);
    }

    @Override
    public boolean delete(UserDeleteDto userDeleteDto) {
        Optional<UserMasterInfo> userMasterInfo=userMasterInfoRepository
                .findById(userDeleteDto.getId());
        userMasterInfo.orElseThrow(MasterInfoNotFoundException::new);
        return true;
    }

}
