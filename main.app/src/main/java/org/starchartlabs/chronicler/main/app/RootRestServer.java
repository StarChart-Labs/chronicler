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

    public RootRestServer(String installationUrl) {
        this.installationUrl = Objects.requireNonNull(installationUrl);
    }

    // TODO romeara doc, test, headers, media-type. Move root view to app service?
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/", produces = { "application/json" })
    public ResponseEntity<RootView> getRoot() {
        RootView rootView = new RootView(installationUrl);

        return new ResponseEntity<>(rootView, HttpStatus.OK);
    }

}
