package com.tantum.app.tantum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TantumApplication.class)
@AutoConfigureMockMvc
public class TantumControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private static String token = "Q$2tQm1Ts*u7qSWK";

	private static String bad_request = "{\"success\":false,\"result\":\"token parameter is missing\"}";

	private static String constraints = "{\"periods\": [0, 1, 2],\"creditMax\": 2,\"creditMin\": 0,\"equivalent\": true,\"subjectsWanted\": [],\"subjectsNotWanted\": []}";

	@Test
	public void testTest() throws Exception {
		this.mockMvc.perform(get("/v1/test")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string("This is a Test, REST API is Working"))
				.andExpect(jsonPath("$").exists());
	}

	@Test
	public void loginTest() throws Exception {
		this.mockMvc.perform(get("/v1/login").param("token", token)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string("{\"success\":true,\"result\":\"Q$2tQm1Ts*u7qSWK\"}"))
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result").value(token));
	}

	@Test
	public void loginTestFail() throws Exception {
		this.mockMvc.perform(get("/v1/login")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(bad_request))
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result").value("token parameter is missing"));
	}

	@Test
	public void scheduleTest() throws Exception {
		this.mockMvc.perform(get("/v1/schedule/2018-1").param("token", token)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result.subjects").exists())
				.andExpect(jsonPath("$.result.subjects").isNotEmpty());
	}

	@Test
	public void scheduleTestFail() throws Exception {
		this.mockMvc.perform(get("/v1/schedule/2018-1")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(bad_request))
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result").value("token parameter is missing"));

	}

	@Test
	public void subjectsTest() throws Exception {
		this.mockMvc.perform(get("/v1/subjects").param("token", token)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result.subjects").exists())
				.andExpect(jsonPath("$.result.subjects").isNotEmpty());
	}

	@Test
	public void subjectsTestFail() throws Exception {
		this.mockMvc.perform(get("/v1/subjects")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(bad_request))
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result").value("token parameter is missing"));
	}

	@Test
	public void staticticsTest() throws Exception {
		this.mockMvc.perform(get("/v1/statictics").param("token", token)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.semestersRemaining").isNumber())
				.andExpect(jsonPath("$.semestersRemaining").value(2))
				.andExpect(jsonPath("$.semesters").exists())
				.andExpect(jsonPath("$.semesters").isNotEmpty())
				.andExpect(jsonPath("$.semestersIA").exists())
				.andExpect(jsonPath("$.semestersIA").isNotEmpty())
				.andExpect(jsonPath("$.courseIA").exists())
				.andExpect(jsonPath("$.courseIA").isNotEmpty());
	}

	@Test
	public void staticticsTestFail() throws Exception {
		this.mockMvc.perform(get("/v1/statictics")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(bad_request))
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result").value("token parameter is missing"));
	}

	@Test
	public void calculateSemesterTest() throws Exception {
		this.mockMvc.perform(post("/v1/calculate-semester")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(constraints)
				.param("token", token))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result.nextSemesters").exists())
				.andExpect(jsonPath("$.result.nextSemesters").isNotEmpty())
				.andExpect(jsonPath("$.result.subjectsWantedError").exists())
				.andExpect(jsonPath("$.result.subjectsWantedError").isEmpty())
				.andExpect(jsonPath("$.result.subjectsNotWantedError").exists())
				.andExpect(jsonPath("$.result.subjectsNotWantedError").isEmpty())
				.andExpect(jsonPath("$.result.subjectsNotSelected").exists())
				.andExpect(jsonPath("$.result.subjectsNotSelected").isEmpty());
	}

	@Test
	public void calculateSemesterTestFail() throws Exception {
		this.mockMvc.perform(post("/v1/calculate-semester")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(bad_request))
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.success").isBoolean())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.result").exists())
				.andExpect(jsonPath("$.result").value("token parameter is missing"));
	}

}
