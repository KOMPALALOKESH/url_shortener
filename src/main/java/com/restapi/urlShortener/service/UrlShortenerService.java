package com.restapi.urlShortener.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restapi.urlShortener.model.UrlMapping;
import com.restapi.urlShortener.repository.UrlMappingRepository;

@Service
public class UrlShortenerService {
	
	@Autowired
	private UrlMappingRepository repository;
	
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int SHORT_CODE_LENGTH = 6;
	
	@Transactional
	public UrlMapping createShortUrl(String originalUrl) {
		String shortCode = generateUniqueShortCode();
		UrlMapping	mapping = new UrlMapping();
		mapping.setUrl(originalUrl);
		mapping.setShortCode(shortCode);
		return repository.save(mapping);
	}
	
	@Transactional(readOnly=true)
	public Optional<UrlMapping> getOriginalUrl(String shortCode){
		Optional<UrlMapping> urlMapping = repository.findById(shortCode);
		if (urlMapping.isPresent()) {
			UrlMapping mapping = urlMapping.get();
			mapping.setAccessCount(mapping.getAccessCount()+1);
			return Optional.of(repository.save(mapping));
		}
		return Optional.empty();
	}
	
	@Transactional(readOnly=true)
	public Optional<UrlMapping> getUrlStats(String shortCode){
		return repository.findById(shortCode);
	}

	private String generateUniqueShortCode() {
		Random random = new Random();
		String shortCode;
		int attempts = 0;
		do {
			StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
			for (int i=0; i<SHORT_CODE_LENGTH; i++) {
				sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
			}
			shortCode = sb.toString();
			attempts++;
			if (attempts>10) {
				throw new RuntimeException("Failed to generate a unique short code after 10 attempts");
			}
		} while (repository.existsById(shortCode));
		return shortCode;
	}
	
	@Transactional
	public Optional<UrlMapping> updateShortUrl(String ShortCode, String newUrl){
		Optional<UrlMapping> existingMapping = repository.findById(ShortCode);
		if (existingMapping.isPresent()) {
			UrlMapping mapping = existingMapping.get();
			mapping.setUrl(newUrl);
			mapping.setUpdatedAt(LocalDateTime.now());
			return Optional.of(repository.save(mapping));
		}
		return Optional.empty();
	}
	
	@Transactional
	public boolean deleteUrl(String shortCode) {
		if (repository.existsById(shortCode)) {
			repository.deleteById(shortCode);
			return true;
		}
		return false;
	}

}
