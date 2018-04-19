/*
 *
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.ampt2d.commons.accession.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ebi.ampt2d.test.configuration.BasicRestControllerTestConfiguration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {BasicRestControllerTestConfiguration.class})
public class BasicRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testNoContentIfAccessioningDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/test/notExistingId").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void testAccessionOk() throws Exception {
        mockMvc.perform(post("/v1/test")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content("[{ \"value\" : \"simpleTest\" }]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].data.value", containsInAnyOrder("simpleTest")));
    }

    @Test
    public void testMultipleAccessionOk() throws Exception {
        mockMvc.perform(post("/v1/test")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content("[{ \"value\" : \"simpleTest2\" }, { \"value\" : \"simpleTest3\" }]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].data.value", containsInAnyOrder("simpleTest2", "simpleTest3")));
    }

    @Test
    public void testThrowExceptions() throws Exception {
        mockMvc.perform(post("/v1/test")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content("[{ \"value\" : \"MissingUnsavedAccessionsException\" }]"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exception")
                        .value("uk.ac.ebi.ampt2d.commons.accession.core.exceptions.MissingUnsavedAccessionsException"));
        mockMvc.perform(post("/v1/test")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content("[{ \"value\" : \"AccessionIsNotPendingException\" }]"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exception")
                        .value("uk.ac.ebi.ampt2d.commons.accession.core.exceptions.AccessionIsNotPendingException"));
        mockMvc.perform(post("/v1/test")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content("[{ \"value\" : \"AccessionCouldNotBeGeneratedException\" }]"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exception")
                        .value("uk.ac.ebi.ampt2d.commons.accession.core.exceptions" +
                                ".AccessionCouldNotBeGeneratedException"));
    }

}
