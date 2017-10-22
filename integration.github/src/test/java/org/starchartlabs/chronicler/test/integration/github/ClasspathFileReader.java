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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//TODO romeara Move to production code for re-use, generalize
public final class ClasspathFileReader extends BufferedReader {

    private ClasspathFileReader(InputStreamReader inputStream) {
        super(Objects.requireNonNull(inputStream));
    }

    public static BufferedReader forResource(Path relativeToSourceDir) {
        Objects.requireNonNull(relativeToSourceDir);
        InputStream stream = ClasspathFileReader.class.getClassLoader()
                .getResourceAsStream(relativeToSourceDir.toString());

        return new ClasspathFileReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    public static String readToString(Path relativeToSourceDir) throws IOException {
        Objects.requireNonNull(relativeToSourceDir);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = forResource(relativeToSourceDir)) {
            String line = reader.readLine();

            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }

        return lines.stream().collect(Collectors.joining("\n"));
    }
}
