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

import org.fcrepo.storage.ocfl.OcflObjectSessionFactory;

/**
 * An interface for building validation tasks
 *
 * @author dbernstein
 */
public interface ValidationTaskBuilder<T extends ValidationTask> {
     /**
      * Build a new task instance
      *
      * @return
      */
     T build();

     /**
      * @param writer
      * @return
      */
     ValidationTaskBuilder<T> writer(final ValidationResultWriter writer);

     /**
      * Sets the OcflObjectSessionFactory objectSessionFactory
      *
      * @param objectSessionFactory
      * @return
      */
     ValidationTaskBuilder<T> objectSessionFactory(final OcflObjectSessionFactory objectSessionFactory);
}
