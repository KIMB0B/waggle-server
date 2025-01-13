package com.waggle.domain.user.service;

import com.waggle.domain.reference.entity.*;
import com.waggle.domain.reference.service.ReferenceService;
import com.waggle.domain.user.dto.UpdateUserDto;
import com.waggle.domain.user.entity.*;
import com.waggle.domain.user.repository.UserRepository;
import com.waggle.global.exception.JwtTokenException;
import com.waggle.global.response.ApiStatus;
import com.waggle.global.secure.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ReferenceService referenceService;

    @Override
    public User getCurrentUser() {
        String authorizationHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest()
                .getHeader("Authorization");

        String token = jwtUtil.getTokenFromHeader(authorizationHeader);

        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new JwtTokenException(ApiStatus._INVALID_ACCESS_TOKEN);
        }

        return userRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new JwtTokenException(ApiStatus._INVALID_ACCESS_TOKEN));
    }

    @Override
    public User updateUser(UpdateUserDto updateUserDto) {
        User user = getCurrentUser();
        user.clearInfo();

        user.setName(updateUserDto.getName());

        Set<UserJob> userJobs = new HashSet<>();
        updateUserDto.getJobs().forEach(userJobDto -> {
            Job job = referenceService.getJobById(userJobDto.getJobId());
            UserJob userJob = UserJob.builder()
                    .job(job)
                    .user(user)
                    .yearCnt(userJobDto.getYearCnt())
                    .build();
            userJobs.add(userJob);
        });
        user.setUserJobs(userJobs);

        Set<UserIndustry> userIndustries = new HashSet<>();
        updateUserDto.getIndustries().forEach(industryId -> {
            Industry industry = referenceService.getIndustryById(industryId);
            UserIndustry userIndustry = UserIndustry.builder()
                    .industry(industry)
                    .user(user)
                    .build();
            userIndustries.add(userIndustry);
        });
        user.setUserIndustries(userIndustries);

        Set<UserSkill> userSkills = new HashSet<>();
        updateUserDto.getSkills().forEach(skillId -> {
            Skill skill = referenceService.getSkillById(skillId);
            UserSkill userSkill = UserSkill.builder()
                    .skill(skill)
                    .user(user)
                    .build();
            userSkills.add(userSkill);
        });
        user.setUserSkills(userSkills);

        Set<UserWeekDays> userWeekDays = new HashSet<>();
        updateUserDto.getPreferWeekDays().forEach(userWeekDayId -> {
            WeekDays weekDays = referenceService.getWeekDaysById(userWeekDayId);
            UserWeekDays userWeekDay = UserWeekDays.builder()
                    .user(user)
                    .weekDays(weekDays)
                    .build();
            userWeekDays.add(userWeekDay);
        });
        user.setUserWeekDays(userWeekDays);

        TimeOfWorking preferTow = referenceService.getTimeOfWorkingById(updateUserDto.getPreferTowId());
        user.setPreferTow(preferTow);

        WaysOfWorking preferWow = referenceService.getWaysOfWorkingById(updateUserDto.getPreferWowId());
        user.setPreferWow(preferWow);

        Sido preferSido = referenceService.getSidoesById(updateUserDto.getPreferSidoId());
        user.setPreferSido(preferSido);

        user.setDetail(updateUserDto.getDetail());

        Set<UserPortfolioUrl> userPortfolioUrls = new HashSet<>();
        updateUserDto.getPortfolioUrls().forEach(portfolioUrlDto -> {
            PortfolioUrl portfolioUrl = referenceService.getPortfolioUrlById(portfolioUrlDto.getPortfolioUrlId());
            UserPortfolioUrl userPortfolioUrl = UserPortfolioUrl.builder()
                    .portfolioUrl(portfolioUrl)
                    .user(user)
                    .url(portfolioUrlDto.getUrl())
                    .build();
            userPortfolioUrls.add(userPortfolioUrl);
        });
        user.setUserPortfolioUrls(userPortfolioUrls);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser() {
        User user = getCurrentUser();
        userRepository.delete(user);
    }
}
