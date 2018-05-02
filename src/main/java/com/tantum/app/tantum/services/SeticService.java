package com.tantum.app.tantum.services;

import java.util.Objects;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tantum.app.tantum.models.SeticAuthDTO;

@Slf4j
@Service
public class SeticService {

	private RestTemplate restTemplate;

	private static final String URL = "https://sistemas.ufsc.br/oauth2.0/profile?access_token=";
	private static final String VINCULO_PESSOA_WS = "https://ws.homologacao.ufsc.br/rest/CadastroPessoaUsuarioService/vinculosPessoa";
	private static final String MATRICULAS_CAGR_WS = "https://ws.homologacao.ufsc.br/rest/CAGRUsuarioService/matriculas";
	private static final String INFORMACAO_ALUNO_CAGR_WS = "https://ws.homologacao.ufsc.br/rest/CAGRUsuarioService/informacaoAluno";

	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER = "Bearer ";

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

	public String getSubjects(String token) {
		return this.restTemplate.postForObject(INFORMACAO_ALUNO_CAGR_WS, this.getHeader(token), String.class);
	}

	public String getMatriculas(String token) {
		return this.restTemplate.postForObject(MATRICULAS_CAGR_WS, this.getHeader(token), String.class);
	}

	public String getInfo(String token) {
		return this.restTemplate.postForObject(VINCULO_PESSOA_WS, this.getHeader(token), String.class);
	}

	private HttpEntity<String> getHeader(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTH_HEADER, BEARER + token);

		return new HttpEntity<>("parameters", headers);
	}

}
