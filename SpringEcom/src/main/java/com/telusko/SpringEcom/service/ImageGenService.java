package com.telusko.SpringEcom.service;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
public class ImageGenService {

    @Autowired
    private ImageModel imageModel;

    public byte[] genImage(String prompt) {

        OpenAiImageOptions options = OpenAiImageOptions.builder()
                .N(1)
                .width(1024)
                .height(1024)
                .quality("standard")
                .responseFormat("url")
                .model("dall-e-3")
                .build();

        ImageResponse response = imageModel.call(new ImagePrompt(prompt, options));
        String url = response.getResult().getOutput().getUrl();


        try {
           return new URL(url).openStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
