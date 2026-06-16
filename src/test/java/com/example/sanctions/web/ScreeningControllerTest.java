package com.example.sanctions.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void flagsSuspiciousName() throws Exception {
        mockMvc.perform(post("/api/screening")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bin Laden, Osama\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.match").value(true))
                .andExpect(jsonPath("$.matches[0].fullName").value("Osama Bin Laden"));
    }

    @Test
    void clearsUnrelatedName() throws Exception {
        mockMvc.perform(post("/api/screening")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Angela Merkel\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.match").value(false));
    }

    @Test
    void flagsNameAddedThroughSanctionsApi() throws Exception {
        var created = mockMvc.perform(post("/api/sanctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Carlos The Jackal\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(created.getResponse().getContentAsString());
        long id = node.get("id").asLong();

        mockMvc.perform(post("/api/screening")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Carlos Jackal\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.match").value(true))
                .andExpect(jsonPath("$.matches[0].id").value(id))
                .andExpect(jsonPath("$.matches[0].fullName").value("Carlos The Jackal"));
    }

    @Test
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/screening")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
