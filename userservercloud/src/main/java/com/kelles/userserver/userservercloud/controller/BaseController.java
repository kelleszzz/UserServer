package com.kelles.userserver.userservercloud.controller;

import com.kelles.userserver.userservercloud.userserversdk.setting.Setting;
import com.kelles.userserver.userservercloud.component.BaseComponent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseController extends BaseComponent {
    @ModelAttribute
    void addUrlsToModel(Model model) {
        model.addAttribute("indexUrl", Setting.URL_INDEX);
        model.addAttribute("insertUrl", Setting.URL_INSERT);
        model.addAttribute("removeUrl", Setting.URL_REMOVE);
    }
}
