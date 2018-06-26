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
package uk.ac.ebi.ampt2d.commons.accession;

import com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ampt2d.commons.accession.core.exceptions.AccessionDoesNotExistException;
import uk.ac.ebi.ampt2d.commons.accession.core.exceptions.AccessionMergedException;
import uk.ac.ebi.ampt2d.test.configuration.MongoDbCucumberTestConfiguration;
import uk.ac.ebi.ampt2d.test.models.TestModel;
import uk.ac.ebi.ampt2d.test.rule.FixSpringMongoDbRule;
import uk.ac.ebi.ampt2d.test.testers.AccessioningServiceTester;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongoDbCucumberTestConfiguration.class})
@DirtiesContext
public class AccessioningServiceStepDefinitions {

    @Rule
    public MongoDbRule mongoDbRule = new FixSpringMongoDbRule(MongoDbConfigurationBuilder.mongoDb()
            .databaseName("accession-test").build());

    //Required for nosql unit
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AccessioningServiceTester tester;

    @Before
    public void doSomethingBefore() {
        mongoDbRule.getDatabaseOperation().deleteAll();
    }

    @Given("^already accessioned ([\\w-,]+)$")
    @When("^user submits ([\\w-,]+) to accessioning service$")
    public void submitObjectsToAccessioningService(String objects) {
        tester.getOrCreate(toModels(objects));
    }

    @Then("^user received accessions: ([\\w-,]+)$")
    public void iShouldReceiveAccessions(String objects) {
        final String[] accessions = objects.split(",");
        tester.getSingleVersionResults().assertAccessions(accessions);
    }

    @When("^user sends patch (\\w+) for accession ([\\w-]+)$")
    public void userSendsPatchInputForAccessionAccession(String patchData, String accession) {
        tester.patch(accession, TestModel.of(patchData));
    }

    @Then("^user should receive version list for ([\\w-]+) with (\\d+) version$")
    public void userShouldReceivePatchForAccessionsWithVersion(String accession, int totalVersions) {
        tester.getLastMultipleVersionResult().assertAccession(accession).assertTotalVersions(totalVersions);
    }

    @And("^patch must have versions increased$")
    public void patchMustHaveVersionsIncreased() {
        tester.getLastMultipleVersionResult().assertVersionAreIncreased();
    }

    @Then("^user receives accession does not exist$")
    public void userReceivesAccessionDoesNotExistError() {
        tester.getLastMethodResponse().assertThrow(AccessionDoesNotExistException.class);
    }

    @When("^user updates ([\\w-]+) patch (\\d+) with ([\\w-]+)$")
    public void userUpdatesAccessionWithInput(String accession, int patch, String newData) {
        tester.update(accession, patch, newData);
    }


    @And("^hash of version (\\d+) should be ([\\w-]+)$")
    public void hashOfVersionShouldBeHash(int version, String hash) {
        tester.getLastMultipleVersionResult().assertHash(version, hash);
    }

    @When("^user merges ([\\w-]+) with ([\\w-]+) reason: ([\\w ]+)$")
    public void userMergesAWithB(String accessionA, String accessionB, String reason) {
        tester.merge(accessionA, accessionB, reason);
    }

    @Then("^user receives an unknown error$")
    public void weShouldHaveAUnknownError() {
        tester.getLastMethodResponse().assertThrow(UnknownError.class);
    }

    @Then("^user operation finished correctly$")
    public void lastProcessFinishedOk() {
        tester.getLastMethodResponse().assertNoException();
    }

    @Then("^user receives an accession already merged error$")
    public void weShouldHaveAAccessionAlreadyMerged() {
        tester.getLastMethodResponse().assertThrow(AccessionMergedException.class);
    }

    @When("^user retrieves objects: ([\\w-,]+)$")
    public void userRetrievesObjectA(String values) {
        tester.get(toModels(values));
    }

    private List<TestModel> toModels(String values) {
        return Arrays.stream(values.split(",")).map(TestModel::of).collect(Collectors.toList());
    }

    @Then("^user receives (\\d+) elements$")
    public void userReceivesANumberOfElements(int numberOfElements) {
        tester.getSingleVersionResults().assertSize(numberOfElements);
    }

    @And("^user received a response with values: ([\\w-,]+)$")
    public void userReceivedAResponseWithObject(String values) {
        tester.getSingleVersionResults().assertAccessionValues(values.split(","));
    }

    @When("^user retrieves accessions: ([\\w-,]+)$")
    public void userRetrievesAccessionsIdServiceA(String accessionIds) {
        tester.getAccessions(accessionIds.split(","));
    }
}
