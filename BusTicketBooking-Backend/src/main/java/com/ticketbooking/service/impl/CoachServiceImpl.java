package com.ticketbooking.service.impl;

import com.ticketbooking.dto.PageResponse;
import com.ticketbooking.exception.ExistingResourceException;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.model.Coach;
import com.ticketbooking.repo.CoachRepo;
import com.ticketbooking.repo.UtilRepo;
import com.ticketbooking.service.CoachService;
import com.ticketbooking.validator.ObjectValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachServiceImpl implements CoachService {

    private final ObjectValidator<Coach> coachValidator;

    private final UtilRepo utilRepo;

    private final CoachRepo coachRepo;

    @Override
    public Coach findById(Long id) {
        return coachRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Coach<%d>".formatted(id)));
    }

    @Override
    @Cacheable(cacheNames = {"coaches"})
    public List<Coach> findAll() {
        return coachRepo.findAll();
    }

    @Override
    @Cacheable(cacheNames = {"coaches_paging"}, key = "{#page, #limit}")
    public PageResponse<Coach> findAll(Integer page, Integer limit) {
        Page<Coach> pageSlice = coachRepo.findAll(PageRequest.of(page, limit));
        PageResponse<Coach> pageResponse = new PageResponse<>();
        pageResponse.setDataList(pageSlice.getContent());
        pageResponse.setPageCount(pageSlice.getTotalPages());
        pageResponse.setTotalElements(pageSlice.getTotalElements());
        return pageResponse;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"coaches", "coaches_paging"}, allEntries = true)
    public Coach save(Coach coach) {
        /*
         * Double check
         * 1. On client-side when user types in form and click submit (validate with YUP)
         * 2. On server-side when data are sent to server (maybe unnecessary but for sure)
         */
        coachValidator.validate(coach);

        if (!checkDuplicateCoachInfo("ADD", coach.getId(), "name", coach.getName())) {
            throw new ExistingResourceException("Name<%s> is already exist".formatted(coach.getName()));
        }

        if (!checkDuplicateCoachInfo("ADD", coach.getId(), "licensePlate", coach.getLicensePlate())) {
            throw new ExistingResourceException("License plate<%s> is already exist".formatted(coach.getLicensePlate()));
        }

        return coachRepo.save(coach);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"coaches", "coaches_paging"}, allEntries = true)
    public Coach update(Coach coach) {
        /*
         * Double check
         * 1. On client-side when user types in form and click submit (validate with YUP)
         * 2. On server-side when data are sent to server (maybe unnecessary but for sure)
         */
        coachValidator.validate(coach);

        if (!checkDuplicateCoachInfo("EDIT", coach.getId(), "name", coach.getName())) {
            throw new ExistingResourceException("Name<%s> is already exist".formatted(coach.getName()));
        }

        if (!checkDuplicateCoachInfo("EDIT", coach.getId(), "licensePlate", coach.getLicensePlate())) {
            throw new ExistingResourceException("License plate<%d> is already exist".formatted(coach.getId()));
        }

        return coachRepo.save(coach);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"coaches", "coaches_paging"}, allEntries = true)
    public String delete(Long id) {
        Coach foundCoach = findById(id);

        if (!foundCoach.getTrips().isEmpty()) {
            throw new ExistingResourceException("Coach<%d> has run some trips, can't be deleted".formatted(id));
        }

        coachRepo.deleteById(id);

        return "Delete Coach<%d> successfully".formatted(id);
    }

    @Override
    public Boolean checkDuplicateCoachInfo(String mode, Long coachId, String field, String value) {
        List<Coach> foundCoaches = utilRepo.checkDuplicateByStringField(Coach.class, mode, "id",
                coachId, field, value);
        return foundCoaches.isEmpty();
    }
}
