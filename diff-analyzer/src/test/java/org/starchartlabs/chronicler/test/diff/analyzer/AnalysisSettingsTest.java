/*
 * Copyright 2019 StarChart-Labs Contributors (https://github.com/StarChart-Labs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starchartlabs.chronicler.test.diff.analyzer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.starchartlabs.chronicler.diff.analyzer.AnalysisSettings;
import org.starchartlabs.chronicler.diff.analyzer.exception.InvalidConfigurationException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

public class AnalysisSettingsTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void fromYamlNull() throws Exception {
        AnalysisSettings.fromYaml(null);
    }

    @Test
    public void fromYamlBlank() throws Exception {
        Optional<AnalysisSettings> result = AnalysisSettings.fromYaml("");

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isPresent());
    }

    @Test(expectedExceptions = InvalidConfigurationException.class)
    public void fromYamlIncorrectStructure() throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("property", "failure");

        Yaml yaml = new Yaml();
        String yamlContents = yaml.dump(data);

        AnalysisSettings.fromYaml(yamlContents);
    }

    @Test
    public void fromYaml() throws Exception {
        Map<String, Object> productionFiles = new HashMap<String, Object>();
        Map<String, Object> releaseNoteFiles = new HashMap<String, Object>();

        productionFiles.put("include", Arrays.asList("**/prodInclude1/**"));
        productionFiles.put("exclude", Arrays.asList("**/prodExclude1"));

        releaseNoteFiles.put("include", Arrays.asList("**/relInclude1/**"));
        releaseNoteFiles.put("exclude", Arrays.asList("**/relExclude1"));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("productionFiles", productionFiles);
        data.put("releaseNoteFiles", releaseNoteFiles);

        Yaml yaml = new Yaml();
        String yamlContents = yaml.dump(data);

        Optional<AnalysisSettings> result = AnalysisSettings.fromYaml(yamlContents);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isPresent());

        AnalysisSettings settings = result.get();

        Assert.assertTrue(settings.isProductionFile("/prodInclude1/something"));
        Assert.assertFalse(settings.isProductionFile("/prodInclude1/prodExclude1"));

        Assert.assertTrue(settings.isReleaseNoteFile("/relInclude1/something"));
        Assert.assertFalse(settings.isReleaseNoteFile("/relInclude1/relExclude1"));
    }

    @Test
    public void fromYamlProductionFilesOnly() throws Exception {
        Map<String, Object> productionFiles = new HashMap<String, Object>();

        productionFiles.put("include", Arrays.asList("**/prodInclude1/**"));
        productionFiles.put("exclude", Arrays.asList("**/prodExclude1"));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("productionFiles", productionFiles);

        Yaml yaml = new Yaml();
        String yamlContents = yaml.dump(data);

        Optional<AnalysisSettings> result = AnalysisSettings.fromYaml(yamlContents);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isPresent());

        AnalysisSettings settings = result.get();

        Assert.assertTrue(settings.isProductionFile("/prodInclude1/something"));
        Assert.assertFalse(settings.isProductionFile("/prodInclude1/prodExclude1"));

        // Defaults should be in place
        Assert.assertTrue(settings.isReleaseNoteFile("/CHANGE_LOG.md"));
    }

    @Test
    public void fromYamlReleaseNoteFilesOnly() throws Exception {
        Map<String, Object> releaseNoteFiles = new HashMap<String, Object>();

        releaseNoteFiles.put("include", Arrays.asList("**/relInclude1/**"));
        releaseNoteFiles.put("exclude", Arrays.asList("**/relExclude1"));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("releaseNoteFiles", releaseNoteFiles);

        Yaml yaml = new Yaml();
        String yamlContents = yaml.dump(data);

        Optional<AnalysisSettings> result = AnalysisSettings.fromYaml(yamlContents);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isPresent());

        AnalysisSettings settings = result.get();

        // Defaults should be in place
        Assert.assertTrue(settings.isProductionFile("/src/something"));
        Assert.assertFalse(settings.isProductionFile("/src/test/testfile.java"));

        Assert.assertTrue(settings.isReleaseNoteFile("/relInclude1/something"));
        Assert.assertFalse(settings.isReleaseNoteFile("/relInclude1/relExclude1"));
    }

}
