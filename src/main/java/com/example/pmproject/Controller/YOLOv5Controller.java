package com.example.pmproject.Controller;

import com.example.pmproject.Util.Flask;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class YOLOv5Controller {

    @Autowired
    private Flask flask;


    @GetMapping("/user/pm/rent")
    public String rentDetect() {
        return "pm/rent";
    }

    @PostMapping("/user/pm/rent")
    public String result(@RequestParam("imgFile")MultipartFile imgFile, Model model) throws Exception {
        flask.requestToFlask(imgFile);

        return "pm/rentResult";
    }
}
