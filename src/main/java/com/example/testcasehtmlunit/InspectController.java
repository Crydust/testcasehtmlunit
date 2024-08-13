package com.example.testcasehtmlunit;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static java.util.Objects.requireNonNullElse;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class InspectController {

    // language=HTML
    private static final String HTML = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Minimal testcase query parameters ignored in post request by HtmlUnitRequestBuilder</title>
            </head>
            <body>
                <p>
                    Method: <select id='method'>
                        <option value="GET">GET</option>
                        <option value="HEAD">HEAD</option>
                        <option value="POST">POST</option>
                        <option value="PUT">PUT</option>
                        <option value="PATCH">PATCH</option>
                        <option value="DELETE">DELETE</option>
                        <option value="OPTIONS">OPTIONS</option>
                        <option value="TRACE">TRACE</option>
                    </select>
                </p>
                <p>
                    Query: <select id='query'>
                        <option value=""></option>
                        <option value="?a=b">?a=b</option>
                        <option value="?a=b&amp;c=d">?a=b&amp;c=d</option>
                        <option value="?a=">?a=</option>
                        <option value="?a">?a</option>
                        <option value="?">?</option>
                        <option value="?a=b&amp;a=d">?a=b&amp;a=d</option>
                    </select>
                </p>
                <p>
                    Encoding: <select id='encoding'>
                        <option value="application/x-www-form-urlencoded">application/x-www-form-urlencoded</option>
                        <option value="multipart/form-data">multipart/form-data</option>
                        <option value="text/plain">text/plain</option>
                    </select>
                </p>
                <p>
                    File: <input id='file' type='file'>
                </p>
                <p>
                    Body: <select id='body'>
                        <option value="">empty</option>
                        <option value="p1=v1" title="p1=v1">oneParameter</option>
                        <option value="a=" title=="a=">emptyValue</option>
                        <option value="a=b" title="a=b">sameAsInQuery</option>
                        <option value="a=other" title="a=other">sameKeyAsInQuery</option>
                        <option value="same=value1&amp;same=value2" title="same=value1&amp;same=value2">sameKeyDifferentValues</option>
                    </select>
                </p>
                <p>
                    Accept: <select id='accept'>
                        <option value="text/html">text/html</option>
                        <option value="application/json">application/json</option>
                        <option value="application/xml">application/xml</option>
                        <option value="text/plain">text/plain</option>
                    </select>
                </p>
                <p>
                    <button type='button' id='button' onclick='handleButtonClick()'>Click me!</button>
                </p>
                <pre id='output' style='border: 1px solid #ccc; padding: 1em; display: none;'></pre>
                <script>
                    function handleButtonClick() {
                        document.getElementById('output').style.display = 'none';
                        document.getElementById('output').textContent = 'Loading ...';
                        let method = document.getElementById('method').value;
                        let query = document.getElementById('query').value;
                        let encoding = document.getElementById('encoding').value;
                        let fileInput = document.getElementById('file');
                        let file = fileInput.files.length === 0 ? null : fileInput.files[0];
                        let body = document.getElementById('body').value;
                        let accept = document.getElementById('accept').value;
                        let data = null;
                        if (method === 'GET' || method === 'HEAD') {
                            data = null;
                        } else if (encoding === 'application/x-www-form-urlencoded') {
                            data = new URLSearchParams();
                        } else if (encoding === 'multipart/form-data') {
                            data = new FormData();
                        } else {
                            data = null;
                        }
                        // for some reason spring boot dislikes the "patch" method
                        if (data !== null && method === 'PATCH') {
                            method = 'POST';
                            data.append('_method', 'PATCH');
                        }
                        // something goes wrong with trace requests
                        if (data !== null && method === 'TRACE') {
                            method = 'POST';
                            data.append('_method', 'TRACE');
                        }
                        let url = '/bounce' + query;
                        if (data !== null){
                            body.split('&').forEach(pair => {
                                if (pair === '') {
                                    return;
                                }
                                let [key, value] = pair.split('=');
                                data.append(key, value === undefined ? '' : value);
                            });
                        }
                        if (data !== null && encoding === 'multipart/form-data' && file !== null) {
                            data.append('file', file);
                        }
                        let xhr = new XMLHttpRequest();
                        // async is slow
                        //xhr.open(method, url, true);
                        // using sync speeds up testing
                        xhr.open(method, url, false);
                        xhr.setRequestHeader('Cache-Control', 'no-cache');
                        if (encoding === 'multipart/form-data') {
                            // Warning: do NOT set the Content-Type header yourself!
                        } else if (method !== 'GET' && method !== 'HEAD') {
                            xhr.setRequestHeader('Content-Type', encoding);
                        }
                        xhr.setRequestHeader('Accept', accept);
                        xhr.onload = () => {
                            if (xhr.status >= 200 && xhr.status < 300) {
                                if (method === 'HEAD') {
                                    let xhr2 = new XMLHttpRequest();
                                    xhr2.open('GET', '/previousParameters', true);
                                    xhr2.onload = () => {
                                        if (xhr2.status >= 200 && xhr2.status < 300) {
                                            document.getElementById('output').textContent = xhr2.responseText;
                                            document.getElementById('output').style.display = '';
                                        }
                                    };
                                    xhr2.send();
                                } else {
                                    document.getElementById('output').textContent = xhr.responseText;
                                    document.getElementById('output').style.display = '';
                                }
                            } else {
                                document.getElementById('output').textContent = 'Error ' + xhr.status;
                                document.getElementById('output').style.display = '';
                            }
                        };
                        xhr.onerror = () => {
                            document.getElementById('output').textContent = 'Error ?';
                            document.getElementById('output').style.display = '';
                        };
                        // not strictly necessary, but htmlunit ignores the 'Content-Type' header when data is URLSearchParams or FormData
                        if (data !== null && encoding === "text/plain") {
                            xhr.send(null);
                        } else {
                            xhr.send(data);
                        }
                    }
                </script>
            </body>
            </html>
            """;

    @GetMapping(path = "/form", produces = TEXT_HTML_VALUE)
    @ResponseBody
    public String doGet() {
        return HTML;
    }

    @GetMapping(path = "/previousParameters", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public String previousParameters(@SessionAttribute(name = "previousParameters", required = false) String previousParameters) {
        return requireNonNullElse(previousParameters, "null");
    }

    @RequestMapping(
            path = "/bounce",
            method = {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE},
            //consumes = {APPLICATION_FORM_URLENCODED_VALUE, MULTIPART_FORM_DATA_VALUE},
            produces = {TEXT_HTML_VALUE, APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, TEXT_PLAIN_VALUE})
    @ResponseBody
    public String bounce(HttpMethod method, WebRequest request, @RequestParam(name = "file", required = false) MultipartFile file, HttpSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("Parameters: \n");

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            sb.append("  '").append(key).append("': [");
            if (values == null) {
                sb.append("null");
            } else {
                for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append("'").append(values[i]).append("'");
                }
            }
            sb.append("]\n");
        }

        if (file != null) {
            sb.append("  '").append(file.getName()).append("': '").append(file.getOriginalFilename()).append("'\n");
        }

        session.setAttribute("previousParameters", sb.toString());
//        System.out.println("==================================");
//        System.out.println(sb);
//        System.out.println("==================================");
        if (method == HttpMethod.HEAD) {
            return null;
        }
        return sb.toString();
    }
}
