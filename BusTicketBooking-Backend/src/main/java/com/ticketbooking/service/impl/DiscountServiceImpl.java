package com.ticketbooking.service.impl;

import com.ticketbooking.dto.PageResponse;
import com.ticketbooking.exception.ExistingResourceException;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.model.Discount;
import com.ticketbooking.repo.DiscountRepo;
import com.ticketbooking.repo.UtilRepo;
import com.ticketbooking.service.DiscountService;
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
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepo discountRepo;

    private final ObjectValidator<Discount> objectValidator;

    private final UtilRepo utilRepo;

    @Override
    public Discount findById(Long id) {
        return discountRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Discount<%d>".formatted(id)));
    }

    @Override
    public Discount findByCode(String code) {
        return discountRepo.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Discount<%s>".formatted(code)));
    }

    @Override
    @Cacheable(cacheNames = {"discounts"})
    public List<Discount> findAll() {
        return discountRepo.findAll();
    }

    @Override
    public List<Discount> findAllAvailable() {
        return discountRepo.findAllAvailable();
    }

    @Override
    @Cacheable(cacheNames = {"discounts_paging"}, key = "{#page, #limit}")
    public PageResponse<Discount> findAll(Integer page, Integer limit) {
        Page<Discount> pageSlice = discountRepo.findAll(PageRequest.of(page, limit));
        PageResponse<Discount> pageResponse = new PageResponse<>();
        pageResponse.setDataList(pageSlice.getContent());
        pageResponse.setPageCount(pageSlice.getTotalPages());
        pageResponse.setTotalElements(pageSlice.getTotalElements());
        return pageResponse;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"discounts", "discounts_paging"}, allEntries = true)
    public Discount save(Discount discount) {
        /*
         * Double check
         * 1. On client-side when user types in form and click submit (validate with YUP)
         * 2. On server-side when data are sent to server (maybe unnecessary but for sure)
         */
        objectValidator.validate(discount);

        if (!checkDuplicateDiscountInfo("ADD", discount.getId(), "code", discount.getCode())) {
            throw new ExistingResourceException("Discount Code<%s> is already exist".formatted(discount.getCode()));
        }

        // check start date < end date
        if (discount.getStartDateTime().isAfter(discount.getEndDateTime())) {
            throw new ResourceNotFoundException("Invalid: START DATE is after END DATE!");
        }

        return discountRepo.save(discount);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"discounts", "discounts_paging"}, allEntries = true)
    public Discount update(Discount discount) {
        /*
         * Double check
         * 1. On client-side when user types in form and click submit (validate with YUP)
         * 2. On server-side when data are sent to server (maybe unnecessary but for sure)
         */
        objectValidator.validate(discount);

        if (!checkDuplicateDiscountInfo("EDIT", discount.getId(), "code", discount.getCode())) {
            throw new ExistingResourceException("Discount Code<%s> is already exist".formatted(discount.getCode()));
        }

        // check start date < end date
        if (discount.getStartDateTime().isAfter(discount.getEndDateTime())) {
            throw new ResourceNotFoundException("Invalid: START DATE can't be after END DATE!");
        }

        return discountRepo.save(discount);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"discounts", "discounts_paging"}, allEntries = true)
    public String delete(Long id) {

        Discount foundDiscount = findById(id);

        if (!foundDiscount.getTrips().isEmpty()) {
            throw new ExistingResourceException("This Discount<%d> has been used, can't be deleted".formatted(id));
        }

        discountRepo.deleteById(id);

        return "Delete Discount<%d> successfully!".formatted(id);
    }

    @Override
    public Boolean checkDuplicateDiscountInfo(String mode, Long discountId, String field, String value) {
        List<Discount> foundDiscounts = utilRepo.checkDuplicateByStringField(Discount.class, mode, "id",
                discountId, field, value);
        return foundDiscounts.isEmpty();
    }
}
