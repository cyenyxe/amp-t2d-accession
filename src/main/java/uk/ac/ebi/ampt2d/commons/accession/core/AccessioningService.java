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

import uk.ac.ebi.ampt2d.commons.accession.generators.exceptions.AccessionCouldNotBeGeneratedException;

import java.util.List;
import java.util.Map;

public interface AccessioningService<MODEL, ACCESSION> {

    Map<ACCESSION, MODEL> getOrCreateAccessions(List<? extends MODEL> messages) throws AccessionCouldNotBeGeneratedException;

    Map<ACCESSION, MODEL> getAccessions(List<? extends MODEL> accessionedObjects);

    Map<ACCESSION, ? extends MODEL> getByAccessions(List<ACCESSION> accessions);
}
