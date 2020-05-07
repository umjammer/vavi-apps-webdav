/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vavi.apps.webdav;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vavi.net.webdav.WebdavService;


/**
 * Main.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/02/27 umjammer initial version <br>
 */
@SpringBootApplication
@Controller
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    /** @see SpringBootApplication */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @Autowired
    WebdavService service;

    @RequestMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping("/admin/list")
    String list(Model model) {
        Map<?, ?>[] attrs = service.getStrageStatus();
        model.addAttribute("availables", attrs[0]);
        model.addAttribute("errors", attrs[1]);
        model.addAttribute("nameMap", attrs[2]);
        return "list";
    }

    @GetMapping("/login")
    String login(RedirectAttributes redirectAttributes,
                 @RequestParam(name = "id", required = true) String id) {
LOG.debug("id: " + id);
        String url = service.login(id);
        return "redirect:" + url;
    }

    @GetMapping("/redirect/microsoft")
    String microsoft(Model model,
                     @RequestParam(name = "code", required = false) String code,
                     @RequestParam(name = "state", required = false) String state,
                     @RequestParam(name = "error", required = false) String error,
                     @RequestParam(name = "error_description", required = false) String errorDescription) {
        if (code != null) {
            service.auth(code, state);
            return list(model);
        } else {
            model.addAttribute("error", error);
            model.addAttribute("errorDescription", errorDescription);
            return "onedrive";
        }
    }

    @GetMapping("/redirect/box")
    String box(Model model,
                     @RequestParam(name = "code", required = false) String code,
                     @RequestParam(name = "state", required = false) String state,
                     @RequestParam(name = "error", required = false) String error,
                     @RequestParam(name = "error_description", required = false) String errorDescription) {
        if (code != null) {
            service.auth(code, state);
            return list(model);
        } else {
            model.addAttribute("error", error);
            model.addAttribute("errorDescription", errorDescription);
            return "onedrive";
        }
    }

    @GetMapping("/redirect/dropbox")
    String dropbox(Model model,
                     @RequestParam(name = "code", required = false) String code,
                     @RequestParam(name = "state", required = false) String state,
                     @RequestParam(name = "error", required = false) String error,
                     @RequestParam(name = "error_description", required = false) String errorDescription) {
        if (code != null) {
            service.auth(code, state);
            return list(model);
        } else {
            model.addAttribute("error", error);
            model.addAttribute("errorDescription", errorDescription);
            return "onedrive";
        }
    }

    @GetMapping("/redirect/google")
    String google(Model model,
                    @RequestParam(name = "code", required = false) String code,
                    @RequestParam(name = "state", required = false) String state,
                    @RequestParam(name = "error", required = false) String error,
                    @RequestParam(name = "error_description", required = false) String errorDescription) {
        if (code != null) {
            service.auth(code, state);
            return list(model);
        } else {
            model.addAttribute("error", error);
            model.addAttribute("errorDescription", errorDescription);
            return "onedrive";
        }
    }
}
