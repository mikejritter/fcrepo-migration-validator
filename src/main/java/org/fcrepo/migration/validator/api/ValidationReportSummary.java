/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.migration.validator.api;

import java.util.Collection;
import java.util.HashSet;

/**
 * A data class defining all report wide summary information
 *
 * @author dbernstein
 * @author awoods
 * @since 2020-12-17
 */
public class ValidationReportSummary {

    private Collection<String> objectReportFilenames = new HashSet<>();

    /**
     * Setter for collecting ObjectReport filenames
     * @param objectReportFilename of generated HTML report
     */
    public void addObjectReport(final String objectReportFilename) {
        objectReportFilenames.add(objectReportFilename);
    }

    /**
     * Getter for collection of ObjectReport filenames
     * @return collection of ObjectReport filenames
     */
    public Collection<String> getObjectReportFilenames() {
        return objectReportFilenames;
    }
}