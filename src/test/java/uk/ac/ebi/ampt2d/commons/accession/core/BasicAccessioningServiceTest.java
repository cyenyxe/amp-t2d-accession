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
package uk.ac.ebi.ampt2d.commons.accession.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ampt2d.commons.accession.generators.SingleAccessionGenerator;
import uk.ac.ebi.ampt2d.commons.accession.generators.exceptions.AccessionCouldNotBeGeneratedException;
import uk.ac.ebi.ampt2d.commons.accession.hashing.SHA1HashingFunction;
import uk.ac.ebi.ampt2d.commons.accession.persistence.BasicSpringDataRepositoryDatabaseService;
import uk.ac.ebi.ampt2d.test.TestModel;
import uk.ac.ebi.ampt2d.test.configuration.TestDatabaseServiceTestConfiguration;
import uk.ac.ebi.ampt2d.test.persistence.TestEntity;
import uk.ac.ebi.ampt2d.test.persistence.TestRepository;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {TestDatabaseServiceTestConfiguration.class})
public class BasicAccessioningServiceTest {

    @Autowired
    private TestRepository repository;

    @Autowired
    private BasicSpringDataRepositoryDatabaseService<TestModel, TestEntity, String, String> databaseService;

    @Test
    public void testAccessionElements() throws AccessionCouldNotBeGeneratedException {
        BasicAccessioningService<TestModel, String, String> accessioningService = getAccessioningService();

        Map<String, TestModel> accessions = accessioningService.getOrCreateAccessions(Arrays.asList(
                TestModel.of("service-test-1"),
                TestModel.of("service-test-2"),
                TestModel.of("service-test-3")
        ));
        assertEquals(3, accessions.size());
    }

    private BasicAccessioningService<TestModel, String, String> getAccessioningService() {
        return new BasicAccessioningService<>(
                SingleAccessionGenerator.ofHashAccessionGenerator(
                        TestModel::getSomething,
                        s -> "id-service-" + s
                ),
                databaseService,
                TestModel::getSomething,
                new SHA1HashingFunction()
        );
    }

    @Test
    public void testGetOrCreateFiltersRepeated() throws AccessionCouldNotBeGeneratedException {
        BasicAccessioningService<TestModel, String, String> accessioningService = getAccessioningService();

        Map<String, TestModel> accessions = accessioningService.getOrCreateAccessions(Arrays.asList(
                TestModel.of("service-test-1"),
                TestModel.of("service-test-2"),
                TestModel.of("service-test-2"),
                TestModel.of("service-test-3")
        ));
        assertEquals(3, accessions.size());
    }

    @Test
    public void testGetAccessions() throws AccessionCouldNotBeGeneratedException {
        BasicAccessioningService<TestModel, String, String> accessioningService = getAccessioningService();

        Map<String, TestModel> accessions = accessioningService.getAccessions(Arrays.asList(
                TestModel.of("service-test-1"),
                TestModel.of("service-test-2"),
                TestModel.of("service-test-3")
        ));
        assertEquals(0, accessions.size());
    }

    @Test
    public void testGetWithExistingEntries() throws AccessionCouldNotBeGeneratedException {
        repository.save(new TestEntity(
                "id-service-test-3",
                "85C4F271CBD3E11A9F8595854F755ADDFE2C0732",
                "service-test-3"));

        BasicAccessioningService<TestModel, String, String> accessioningService = getAccessioningService();

        Map<String, TestModel> accessions = accessioningService.getAccessions(Arrays.asList(
                TestModel.of("service-test-1"),
                TestModel.of("service-test-2"),
                TestModel.of("service-test-3")
        ));
        assertEquals(1, accessions.size());
    }

    @Test
    public void testGetOrCreateWithExistingEntries() throws AccessionCouldNotBeGeneratedException {
        repository.save(new TestEntity(
                "id-service-test-3",
                "85C4F271CBD3E11A9F8595854F755ADDFE2C0732",
                "service-test-3"));

        BasicAccessioningService<TestModel, String, String> accessioningService = getAccessioningService();

        Map<String, TestModel> accessions = accessioningService.getOrCreateAccessions(Arrays.asList(
                TestModel.of("service-test-1"),
                TestModel.of("service-test-2"),
                TestModel.of("service-test-3")
        ));
        assertEquals(3, accessions.size());
    }

}
