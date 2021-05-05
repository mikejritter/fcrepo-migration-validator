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
package org.fcrepo.migration.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fcrepo.migration.validator.api.ValidationResult.ValidationType.BINARY_HEAD_COUNT;
import static org.fcrepo.migration.validator.api.ValidationResult.ValidationType.BINARY_VERSION_COUNT;
import static org.fcrepo.migration.validator.api.ValidationResult.ValidationType.SOURCE_OBJECT_RESOURCE_DELETED;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.fcrepo.migration.validator.api.ValidationResult;
import org.junit.Test;

/**
 * @author mikejritter
 */
public class DeletedValidationIT extends AbstractValidationIT {

    final File DELETED_BASE_DIR = new File(FIXTURES_BASE_DIR, "deleted-validation-it");

    @Test
    public void testValidateDeletedDatastream() {
        final var f3DatastreamsDir = new File(DELETED_BASE_DIR, "valid/f3/datastreams");
        final var f3ObjectsDir = new File(DELETED_BASE_DIR, "valid/f3/objects");
        final var f6OcflRootDir = new File(DELETED_BASE_DIR, "valid/f6/data/ocfl-root");
        final var config = getConfig(f3DatastreamsDir, f3ObjectsDir, f6OcflRootDir);

        final var reportHandler = doValidation(config);

        // verify expected results
        assertEquals("Should be no errors!", 0, reportHandler.getErrors().size());

        // verify datastream metadata
        // only 1 datastream was deleted, so we expect 1 SOURCE_OBJECT_RESOURCE_DELETED
        assertThat(reportHandler.getPassed())
            .map(ValidationResult::getValidationType)
            .containsOnlyOnce(SOURCE_OBJECT_RESOURCE_DELETED);
    }

    @Test
    public void testValidateDeletedDatastreamError() {
        final var f3DatastreamsDir = new File(DELETED_BASE_DIR, "valid/f3/datastreams");
        final var f3ObjectsDir = new File(DELETED_BASE_DIR, "valid/f3/objects");
        final var f6OcflRootDir = new File(DELETED_BASE_DIR, "ocfl-missing-version/f6/data/ocfl-root");
        final var config = getConfig(f3DatastreamsDir, f3ObjectsDir, f6OcflRootDir);

        final var reportHandler = doValidation(config);

        // verify expected results
        // SOURCE_OBJECT_RESOURCE_DELETED -> no deleted version exists
        // BINARY_VERSION_COUNT -> we expected an extra version from OCFL because of the deleted resource
        // BINARY_HEAD_COUNT -> the datastream was never deleted, so it still exists in the HEAD for OCFL
        assertThat(reportHandler.getErrors())
            .isNotEmpty()
            .map(ValidationResult::getValidationType)
            .containsOnly(SOURCE_OBJECT_RESOURCE_DELETED, BINARY_VERSION_COUNT, BINARY_HEAD_COUNT);
    }

    @Test
    public void testValidateDeletedDatastreamFailure() {
        final var f3DatastreamsDir = new File(DELETED_BASE_DIR, "valid/f3/datastreams");
        final var f3ObjectsDir = new File(DELETED_BASE_DIR, "valid/f3/objects");
        final var f6OcflRootDir = new File(DELETED_BASE_DIR, "ocfl-not-deleted/f6/data/ocfl-root");
        final var config = getConfig(f3DatastreamsDir, f3ObjectsDir, f6OcflRootDir);

        final var reportHandler = doValidation(config);

        // verify expected results
        // unlike the previous test, we have an equal number of versions for our test file (props), however the ocfl
        // headers show that the file is not deleted
        // SOURCE_OBJECT_RESOURCE_DELETED -> no deleted version exists
        // BINARY_HEAD_COUNT -> the datastream was never deleted, so it still exists in the HEAD for OCFL
        assertThat(reportHandler.getErrors())
            .isNotEmpty()
            .map(ValidationResult::getValidationType)
            .containsOnly(SOURCE_OBJECT_RESOURCE_DELETED, BINARY_HEAD_COUNT);
    }

}
