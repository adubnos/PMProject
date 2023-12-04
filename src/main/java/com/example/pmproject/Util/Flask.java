package com.example.pmproject.Util;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.util.Base64;
import java.util.logging.Handler;

@Component
@RequiredArgsConstructor
public class Flask {

    @Value("${flask.Server.Url}")
    private String url;

    @Value("${tempFolder}")
    private String tempFolder;

    private JSONObject jsonObject = new JSONObject();

    private String getBase64String(MultipartFile multipartFile) throws Exception {
        byte[] bytes = multipartFile.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public void requestToFlask(MultipartFile file) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        String imageFileString = getBase64String(file);
        body.add("extension", extension);
        body.add("image", imageFileString);

        HttpEntity<?> requestMessage = new HttpEntity<>(body, httpHeaders);

        String response = restTemplate.postForObject(url, requestMessage, String.class);

        JSONParser parser = new JSONParser();
        jsonObject = (JSONObject) parser.parse(response);

        System.out.println(jsonObject.size());

        byte[] decodedImageDate = Base64.getDecoder().decode((String)(jsonObject.get("image")));
        String outputFilePath = tempFolder+"result.jpg";

        try(FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(decodedImageDate);
        }


    }
}
