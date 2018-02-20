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
package uk.ac.ebi.ampt2d.accession.common.persistence;

import uk.ac.ebi.ampt2d.accession.common.accessioning.AccessioningRepository;
import uk.ac.ebi.ampt2d.accession.common.generators.ModelHashAccession;
import uk.ac.ebi.ampt2d.accession.common.accessioning.SaveResponse;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BasicDatabaseService<MODEL, ENTITY extends MODEL, HASH, ACCESSION extends Serializable>
        implements DatabaseService<MODEL, HASH, ACCESSION> {

    private AccessioningRepository<ENTITY, HASH, ACCESSION> repository;

    private final Function<ModelHashAccession<MODEL, HASH, ACCESSION>, ENTITY> toEntityFunction;

    private final Function<ENTITY, ACCESSION> getAccessionFunction;

    private final Function<ENTITY, HASH> getHashedMessageFunction;


    public BasicDatabaseService(AccessioningRepository<ENTITY, HASH, ACCESSION> repository,
                                Function<ModelHashAccession<MODEL, HASH, ACCESSION>, ENTITY> toEntityFunction,
                                Function<ENTITY, ACCESSION> getAccessionFunction,
                                Function<ENTITY, HASH> getHashedMessageFunction) {
        this.repository = repository;
        this.toEntityFunction = toEntityFunction;
        this.getAccessionFunction = getAccessionFunction;
        this.getHashedMessageFunction = getHashedMessageFunction;
    }

    @Override
    public Map<ACCESSION, ? extends MODEL> findAllAccessionByMessageHash(Collection<HASH> messageHashes) {
        return repository.findByHashedMessageIn(messageHashes).stream()
                .collect(Collectors.toMap(getAccessionFunction, e -> e));
    }

    @Override
    public Map<HASH, ACCESSION> getExistingAccessions(Collection<HASH> messageHashes) {
        return repository.findByHashedMessageIn(messageHashes).stream()
                .collect(Collectors.toMap(getHashedMessageFunction, getAccessionFunction));
    }

    @Override
    public SaveResponse save(List<ModelHashAccession<MODEL, HASH, ACCESSION>> objects) {
        // TODO overly optimisting database service that will work always
        HashMap<ACCESSION, MODEL> savedAccessions = new HashMap<>();
        HashMap<ACCESSION, MODEL> unsavedAccessions = new HashMap<>();
        HashMap<ACCESSION, MODEL> accessionOfUnsavedMessages = new HashMap<>();

        Set<ENTITY> entitySet = objects.stream()
                .map(toEntityFunction).collect(Collectors.toSet());
        Iterable<ENTITY> savedEntities = repository.save(entitySet);
        savedEntities.forEach(entity -> savedAccessions.put(getAccessionFunction.apply(entity), entity));

        return new SaveResponse(savedAccessions, unsavedAccessions, accessionOfUnsavedMessages);
    }

    @Override
    public Map<ACCESSION, ? extends MODEL> findAllAccessionByAccessions(List<ACCESSION> accessions) {
        Map<ACCESSION, MODEL> result = new HashMap<>();
        repository.findAll(accessions).iterator().forEachRemaining(entity -> result.put(getAccessionFunction
                .apply(entity), entity));
        return result;
    }

}