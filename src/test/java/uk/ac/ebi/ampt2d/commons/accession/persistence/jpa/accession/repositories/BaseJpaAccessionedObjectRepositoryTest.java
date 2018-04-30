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
package uk.ac.ebi.ampt2d.commons.accession.persistence.jpa.accession.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ampt2d.commons.accession.core.AccessionWrapper;
import uk.ac.ebi.ampt2d.test.TestModel;
import uk.ac.ebi.ampt2d.test.configuration.TestJpaDatabaseServiceTestConfiguration;
import uk.ac.ebi.ampt2d.test.persistence.TestEntity;
import uk.ac.ebi.ampt2d.test.persistence.TestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {TestJpaDatabaseServiceTestConfiguration.class})
public class BaseJpaAccessionedObjectRepositoryTest {

    public static final TestEntity ENTITY = new TestEntity(new AccessionWrapper("a1", "h1",
            TestModel.of("something1")));

    @Autowired
    private TestRepository repository;

    @Test
    public void testSaveInitializesActiveFlag() {
        TestEntity savedEntity = repository.save(ENTITY);
        assertTrue(savedEntity.isActive());
    }

    @Test
    public void testSaveInitializesCreatedDate() {
        LocalDateTime beforeSave = LocalDateTime.now();
        TestEntity savedEntity = repository.save(ENTITY);
        assertTrue(beforeSave.isBefore(savedEntity.getCreatedDate()));
    }

    @Test
    public void testSave() {
        TestEntity savedEntity = repository.save(ENTITY);
        assertEquals("a1", savedEntity.getAccession());
        assertEquals("h1", savedEntity.getHashedMessage());
        assertEquals("something1", savedEntity.getSomething());
    }

    @Test
    public void testEnableEntitiesByHash() {
        TestEntity savedEntity = repository.save(new TestEntity("a1", "h1", 1, false, "something1"));
        assertFalse(savedEntity.isActive());
        HashSet<String> hashes = new HashSet<>();
        hashes.add("h1");
        repository.enableByHashedMessageIn(hashes);
        TestEntity dbEntity = repository.findOne("h1");
        assertTrue(dbEntity.isActive());
    }

    @Test
    public void testInsertTwoVersionsSameAccession() {
        TestEntity testEntity1 = new TestEntity("a1", "h1", 1, true, "something1");
        TestEntity testEntity2 = new TestEntity("a1", "h2", 2, true, "something2");
        repository.insert(Arrays.asList(testEntity1, testEntity2));
        assertEquals(2, repository.count());
    }

    @Test
    public void testFindByAccession() {
        TestEntity testEntity1 = new TestEntity("a1", "h1", 1, true, "something1");
        TestEntity testEntity2 = new TestEntity("a1", "h2", 2, true, "something2");
        repository.insert(Arrays.asList(testEntity1, testEntity2));
        assertEquals(2, repository.count());
        assertEquals(2, repository.findByAccession("a1").size());
        assertEquals(2, repository.findByAccessionIn(Arrays.asList("a1")).size());
    }

    @Test
    public void testFindByAccessionAndVersion() {
        TestEntity testEntity1 = new TestEntity("a1", "h1", 1, true, "something1");
        TestEntity testEntity2 = new TestEntity("a1", "h2", 2, true, "something2");
        TestEntity testEntity3 = new TestEntity("a1", "h3", 2, true, "something2");
        repository.insert(Arrays.asList(testEntity1, testEntity2, testEntity3));
        assertEquals(3, repository.count());
        assertEquals(1, repository.findByAccessionAndVersion("a1", 1).size());
        assertEquals(2, repository.findByAccessionAndVersion("a1", 2).size());
    }

}