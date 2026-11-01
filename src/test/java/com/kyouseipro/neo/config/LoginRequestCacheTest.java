package com.kyouseipro.neo.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class LoginRequestCacheTest {
    @Test
    void savesPageNavigation() {
        var request = request("/", "text/html");
        var cache = new SecurityConfig().loginRequestCache();
        var response = new MockHttpServletResponse();
        cache.saveRequest(request, response);
        assertNotNull(cache.getRequest(request, response));
    }

    @Test
    void excludesServiceWorkerApiAndBackgroundRequests() {
        for (String path : new String[]{"/sw.js", "/api/my-orders", "/js/app.js", "/css/app.css"}) {
            var request = request(path, "text/html");
            var response = new MockHttpServletResponse();
            var cache = new SecurityConfig().loginRequestCache();
            cache.saveRequest(request, response);
            assertNull(cache.getRequest(request, response), path);
        }
        var request = request("/", "*/*");
        var cache = new SecurityConfig().loginRequestCache();
        var response = new MockHttpServletResponse();
        cache.saveRequest(request, response);
        assertNull(cache.getRequest(request, response));
    }

    private MockHttpServletRequest request(String path, String accept) {
        var request = new MockHttpServletRequest("GET", path);
        request.setServletPath(path);
        request.addHeader("Accept", accept);
        return request;
    }
}
