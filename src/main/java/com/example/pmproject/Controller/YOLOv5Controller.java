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

import java.util.List;

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
        JSONObject jsonObject = flask.requestToFlask(imgFile);
        List jsonResult = (List) jsonObject.get("result");
        // result 리스트에 "hmo"가 포함되어 있는지 확인
        boolean containsHmo = false;
        String result;

        for (Object item : jsonResult) {
            if (item.equals("hmo")) {
                containsHmo = true;
                break;
            }
        }

        if (containsHmo) {
            result = "hmo";
        } else {
            result = "hmx";
        }

        model.addAttribute("jsonObject", result);

        return "pm/rentResult";
    }
}
