package com.example.sanctions.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SanctionedNameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUpdateAndDeleteRoundTrip() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/sanctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Carlos The Jackal\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Carlos The Jackal"))
                .andReturn();

        JsonNode node = objectMapper.readTree(created.getResponse().getContentAsString());
        long id = node.get("id").asLong();

        mockMvc.perform(get("/api/sanctions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Carlos The Jackal"));

        mockMvc.perform(put("/api/sanctions/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Ilich Ramirez Sanchez\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Ilich Ramirez Sanchez"));

        mockMvc.perform(delete("/api/sanctions/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/sanctions/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns404ForMissingId() throws Exception {
        mockMvc.perform(get("/api/sanctions/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void rejectsBlankFullName() throws Exception {
        mockMvc.perform(post("/api/sanctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }
}
