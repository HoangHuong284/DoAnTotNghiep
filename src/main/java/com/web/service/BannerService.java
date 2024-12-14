package com.web.service;

import com.web.entity.Banner;
import com.web.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    
    public Banner save(Banner banner) {
        return bannerRepository.save(banner);
    }

    
    public Banner update(Banner banner) {
        return bannerRepository.save(banner);
    }

    
    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }

    
    public Banner findById(Long id) {
        return bannerRepository.findById(id).get();
    }

    
    public List<Banner> findByPageName(String pageName) {
        return bannerRepository.findByPageName("%"+pageName+"%");
    }

    
    public List<Banner> findAll() {
        return bannerRepository.findAll();
    }
}
