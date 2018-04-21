package com.tantum.app.tantum.services;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tantum.app.tantum.models.SeticAuthDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {

	private RestTemplate restTemplate;

	public static final String URL = "https://sistemas.homologacao.ufsc.br/oauth2.0/profile?access_token=";

	@PostConstruct
	private void init() {
		this.restTemplate = new RestTemplate();
	}

	public Boolean doAuthenticate(String token) {
		try {
			SeticAuthDTO dto = this.restTemplate.getForObject(URL + token, SeticAuthDTO.class);
			log.info("Authtentication successful, id: " + dto.getId());
			return Objects.isNull(dto.getError());
		} catch (Exception e) {
			log.error("Error in authentication with token: " + token + " " + e);
		}
		return false;
	}

}
