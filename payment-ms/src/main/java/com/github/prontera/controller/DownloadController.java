package com.github.prontera.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/v1")
public class DownloadController {
    @Value("${server.port}")
    private String serverPort;
    @Value("${spring.application.name}")
    private String applicationName;

    @RequestMapping(value = "/downloads", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloads(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final File file = new File("/Users/chris/Downloads/typora_latest.zip");
        final byte[] fileToByteArray = FileUtils.readFileToByteArray(file);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Expires", "0");
        headers.add("Cache-Control", "no-store");
        headers.add("Content-Length", String.valueOf(file.length()));
        headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        return new ResponseEntity<>(fileToByteArray, headers, HttpStatus.OK);
    }

}
