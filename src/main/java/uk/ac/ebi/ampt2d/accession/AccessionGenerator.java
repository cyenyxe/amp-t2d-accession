/*
 * Copyright 2017 EMBL - European Bioinformatics Institute
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
 */
package uk.ac.ebi.ampt2d.accession;

import java.util.Map;
import java.util.Set;

/**
 * An accession generator, that generate unique accessions for given objects
 *
 * @param <T> Object class
 */

public interface AccessionGenerator<T> {

    /**
     * Generate unique accessions for a set of objects. The returned accessions should be unique: two different
     * objects cannot get the same accession
     *
     * @param objects Set of objects to accession
     * @return A map of objects to unique accessions
     */
    Map<T, String> get(Set<T> objects);
}