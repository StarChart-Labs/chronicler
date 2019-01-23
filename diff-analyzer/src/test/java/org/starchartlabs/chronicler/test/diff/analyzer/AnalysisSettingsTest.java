/*
 * Copyright (c) Jan 22, 2019 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
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
