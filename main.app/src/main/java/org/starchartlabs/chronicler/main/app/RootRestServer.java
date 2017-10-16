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
package org.starchartlabs.chronicler.main.app;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RootRestServer {

    private final String installationUrl;

    private final String sourceUrl;

    public RootRestServer(String installationUrl, String sourceUrl) {
        this.installationUrl = Objects.requireNonNull(installationUrl);
        this.sourceUrl = Objects.requireNonNull(sourceUrl);
    }

    // TODO romeara doc, test, headers, media-type. Move root view to app service?
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/", produces = { "application/json" })
    public ResponseEntity<RootView> getRoot() {
        RootView rootView = new RootView(installationUrl, sourceUrl);

        return new ResponseEntity<>(rootView, HttpStatus.OK);
    }

}
