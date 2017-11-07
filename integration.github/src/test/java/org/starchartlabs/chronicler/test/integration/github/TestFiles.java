/*
 * Copyright 2017 StarChart Labs Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starchartlabs.chronicler.test.integration.github;

import java.nio.file.Path;
import java.nio.file.Paths;

//TODO romeara doc
public class TestFiles {

    private static final Path RESOURCES_PATH = Paths.get("org", "starchartlabs", "chronicler", "integration", "github");

    public static final Path PULL_REQUEST_EVENT = RESOURCES_PATH.resolve("pullRequestEvent.json");

    public static final Path PULL_REQUEST_META_DATA = RESOURCES_PATH.resolve("pullRequestMetaData.json");

    public static final Path PING_EVENT = RESOURCES_PATH.resolve("pingEvent.json");

    public static final Path INSTALLATION_EVENT = RESOURCES_PATH.resolve("installationEvent.json");

    public static final Path INSTALLATION_META_DATA = RESOURCES_PATH.resolve("installationMetaData.json");

    public static final Path OWNER_META_DATA = RESOURCES_PATH.resolve("ownerMetaData.json");

    public static final Path REPOSITORY_EVENT = RESOURCES_PATH.resolve("repositoryEvent.json");

    public static final Path REPOSITORY_META_DATA = RESOURCES_PATH.resolve("repositoryMetaData.json");

}
