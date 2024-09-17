package com.restapi.urlShortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restapi.urlShortener.model.UrlMapping;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {

}
