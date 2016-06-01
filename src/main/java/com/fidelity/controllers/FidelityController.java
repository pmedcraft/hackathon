package com.fidelity.controllers;

import com.fidelity.sdlweb.ContentServiceClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by pmedcraft on 31/05/2016.
 */
@Controller
public class FidelityController {

    @RequestMapping(value="/fidelity-hackathon/", method=GET)
    public String showFidelityTestPage(Model model) {
        ContentServiceClient contentServiceClient = new ContentServiceClient();
        model.addAttribute("componentList", contentServiceClient.requestPageContent("662", "15"));
        return "fidelity-main";
    }

    @RequestMapping(value="/fidelity-hackathon/{publicationId}/{pageId}", method=GET)
    public String showFidelityTestPage(@PathVariable("publicationId") String publicationId, @PathVariable("pageId") String pageId, Model model) {
        ContentServiceClient contentServiceClient = new ContentServiceClient();
        model.addAttribute("componentList", contentServiceClient.requestPageContent(pageId, publicationId));
        return "fidelity-main";
    }
}
