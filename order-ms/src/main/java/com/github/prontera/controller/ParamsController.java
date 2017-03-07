package com.github.prontera.controller;

import com.github.prontera.util.Echoes;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/form", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ParamsController {
    @Autowired
    private Echoes echoes;

    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.GET)
    public Map<String, ?> get(@PathVariable String version, HttpServletRequest request) throws IOException {
        return echoes.mark(request, null);
    }

    @Data
    static class User {
        String version;
    }

    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.POST)
    public Map<String, ?> post(@PathVariable String version, HttpServletRequest request, User user) throws IOException {
        return echoes.mark(request, null);
    }

    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.PUT)
    public Map<String, ?> put(@PathVariable String version, HttpServletRequest request) throws IOException {
        return echoes.mark(request, null);
    }

    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.PATCH)
    public Map<String, ?> patch(@PathVariable String version, HttpServletRequest request) throws IOException {
        return echoes.mark(request, null);
    }

    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.DELETE)
    public Map<String, ?> delete(@PathVariable String version, HttpServletRequest request) throws IOException {
        return echoes.mark(request, null);
    }

    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.OPTIONS)
    public Map<String, ?> options(@PathVariable String version, HttpServletRequest request) throws IOException {
        return echoes.mark(request, null);
    }

    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.TRACE)
    public Map<String, ?> trace(@PathVariable String version, HttpServletRequest request) throws IOException {
        return echoes.mark(request, null);
    }

}
