package com.example.testcasehtmlunit;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.Objects.requireNonNullElse;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.util.HtmlUtils.htmlEscape;

@Controller
@RequestMapping("/")
public class SimpleController {

    // language=HTML
    private static final String HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Minimal testcase query parameters ignored in post request by HtmlUnitRequestBuilder</title>
            </head>
            <body>
                <h1>Minimal testcase query parameters ignored in post request by HtmlUnitRequestBuilder</h1>
                <p>You submitted form <b id="submittedForm">%s</b>,<br>
                 the values of 'x' are: <b id="valuesOfX">%s</b>,<br>
                 the uploaded file name is: <b id="fileName">%s</b>,<br>
                 the uploaded file contents is: <b id="fileContents">%s</b>.</p>
                 the json contents is: <b id="json">%s</b>.</p>
                <form action="?form=1" method="post" id="form1">
                    <p><button type="submit">Submit form <b>1</b> (form has query parameter in action)</button></p>
                </form>
                <form action="?" method="post" id="form2">
                    <p><button type="submit" formaction="?form=2">Submit form <b>2</b> (button has query parameter in formaction)</button></p>
                </form>
                <form action="?" method="post" id="form3">
                    <p><button type="submit" name="form" value="3">Submit form <b>3</b> (button has name and value)</button></p>
                </form>
                <form action="?" method="post" id="form4">
                    <p><button type="submit" formaction="?form=4" name="dummy" value="foo">Submit form <b>4</b> (button has query parameter in formaction and has irrelevant name and value)</button></p>
                </form>
                <form action="?form=5" method="post" id="form5" enctype="multipart/form-data">
                    <p><button type="submit" name="dummy" value="foo">Submit form <b>5</b> (form has query parameter in action, enctype is multipart/form-data)</button></p>
                </form>
                <form action="?form=6&amp;x=query" method="post" id="form6">
                    <p>
                        <input type="hidden" name="x" value="hidden">
                        <button type="submit" name="x" value="button">Submit form <b>6</b> (form has query parameter in action, multiple values for 'x')</button>
                    </p>
                </form>
                <form action="?" method="post" id="form7">
                    <p>
                        <input type="checkbox" name="x" value="checkbox1" checked="checked"> x = checkbox1,
                        <input type="checkbox" name="x" value="checkbox2" checked="checked"> x = checkbox2,
                        <input type="checkbox" name="x" value="checkbox3" checked="checked"> x = checkbox3<br>
                        <button type="submit" name="form" value="7">Submit form <b>7</b> (simple form, multiple values for 'x' checkbox)</button>
                    </p>
                </form>
                <form action="?form=8&amp;x=query" method="post" id="form8" enctype="multipart/form-data">
                    <p>
                        <input type="hidden" name="x" value="hidden">
                        <button type="submit" name="x" value="button">Submit form <b>8</b> (form has query parameter in action, multiple values for 'x', enctype is multipart/form-data)</button>
                    </p>
                </form>
                <form action="?" method="post" id="form9" enctype="multipart/form-data">
                    <p>
                        <input type="checkbox" name="x" value="checkbox1" checked="checked"> x = checkbox1,
                        <input type="checkbox" name="x" value="checkbox2" checked="checked"> x = checkbox2,
                        <input type="checkbox" name="x" value="checkbox3" checked="checked"> x = checkbox3<br>
                        <button type="submit" name="form" value="9">Submit form <b>9</b> (simple form, multiple values for 'x' checkbox, enctype is multipart/form-data)</button>
                    </p>
                </form>
                <form action="?" method="post" id="form10" enctype="multipart/form-data">
                    <p>
                        <input type="hidden" name="form" value="10">
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="submit">Submit form <b>10</b> (form with file input, enctype is multipart/form-data)</button>
                    </p>
                </form>
                <div id="form11">
                    <p><button type="button" onclick="submitForm('11', 'PUT');">Submit form <b>11</b> (put method with javascript)</button></p>
                </div>
                <div id="form12">
                    <p><button type="button" onclick="submitForm('12', 'DELETE');">Submit form <b>12</b> (delete method with javascript)</button></p>
                </div>
                <div id="form13">
                    <p><button type="button" onclick="submitForm('13', 'PATCH');">Submit form <b>13</b> (patch method with javascript, warning: PATCH is weird)</button></p>
                </div>
                <div id="form14">
                    <p><button type="button" onclick="submitForm('14', 'OPTIONS');">Submit form <b>14</b> (options method with javascript)</button></p>
                </div>
                <div id="form15">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFile('15', 'PUT');">Submit form <b>15</b> (put method with javascript)</button>
                    </p>
                </div>
                <div id="form16">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFile('16', 'DELETE');">Submit form <b>16</b> (delete method with javascript)</button>
                    </p>
                </div>
                <div id="form17">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFile('17', 'PATCH');">Submit form <b>17</b> (patch method with javascript, warning: PATCH is weird)</button>
                    </p>
                </div>
                <div id="form18">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFile('18', 'OPTIONS');">Submit form <b>18</b> (options method with javascript)</button>
                    </p>
                </div>
                <div id="form19">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFileAndReceiveXml('19', 'PUT');">Submit form <b>19</b> (put method with javascript returns XML)</button>
                    </p>
                </div>
                <div id="form20">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFileAndReceiveXml('20', 'DELETE');">Submit form <b>20</b> (delete method with javascript returns XML)</button>
                    </p>
                </div>
                <div id="form21">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFileAndReceiveXml('21', 'PATCH');">Submit form <b>21</b> (patch method with javascript returns XML, warning: PATCH is weird)</button>
                    </p>
                </div>
                <div id="form22">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFileAndReceiveXml('22', 'OPTIONS');">Submit form <b>22</b> (options method with javascript returns XML)</button>
                    </p>
                </div>
                <div id="form23">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFile('23', 'POST');">Submit form <b>23</b> (post method with javascript)</button>
                    </p>
                </div>
                <div id="form24">
                    <p>
                        file: <input type="file" name="file">  (we assume an ascii encoded text-file will be uploaded)<br>
                        <button type="button" onclick="submitFormWithFileAndJson('24', 'POST');">Submit form <b>24</b> (post method with javascript combining file and some json)</button>
                    </p>
                </div>
                <div id="form25">
                    <p>
                        <button type="button" onclick="submitJson('25', 'POST');">Submit form <b>25</b> (post method with javascript and json body)</button>
                    </p>
                </div>
                <script>
                    function submitForm(form, method) {
                        let ids = ['submittedForm', 'valuesOfX', 'fileName', 'fileContents'];
                        document.getElementById('json').textContent = 'none';
                        ids.forEach(id => document.getElementById(id).style.display = 'none');
                        ids.forEach(id => document.getElementById(id).textContent = 'Loading ...');
                        let body = new URLSearchParams();
                        body.append('x', 'body');
                        // for some reason spring boot dislikes the "patch" method
                        if (method === 'PATCH') {
                            method = 'POST';
                            body.append('_method', 'PATCH');
                        }
                        let xhr = new XMLHttpRequest();
                        xhr.open(method, '?form=' + encodeURIComponent(form) + '&x=query', false);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                        xhr.setRequestHeader('Accept', 'application/json');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === 4 && xhr.status === 200) {
                                const data = JSON.parse(xhr.responseText);
                                ids.forEach(id => document.getElementById(id).textContent = data[id]);
                                ids.forEach(id => document.getElementById(id).style.display = '');
                            }
                        };
                        xhr.send(body);
                    }
                    function submitFormWithFile(form, method) {
                        let ids = ['submittedForm', 'valuesOfX', 'fileName', 'fileContents'];
                        document.getElementById('json').textContent = 'none';
                        ids.forEach(id => document.getElementById(id).style.display = 'none');
                        ids.forEach(id => document.getElementById(id).textContent = 'Loading ...');
                        let body = new FormData();
                        body.append('x', 'body');
                        let fileInput = document.querySelector('#form' + form + ' input[name="file"]');
                        if (fileInput.files.length !== 0) {
                            body.append('file', fileInput.files[0]);
                        }
                        // for some reason spring boot dislikes the "patch" method
                        if (method === 'PATCH') {
                            method = 'POST';
                            body.append('_method', 'PATCH');
                        }
                        let xhr = new XMLHttpRequest();
                        xhr.open(method, '?form=' + encodeURIComponent(form) + '&x=query', false);
                        // Warning: do NOT set the Content-Type header yourself!
                        // xhr.setRequestHeader('Content-Type', 'multipart/form-data');
                        xhr.setRequestHeader('Accept', 'application/json');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === 4 && xhr.status === 200) {
                                const data = JSON.parse(xhr.responseText);
                                ids.forEach(id => document.getElementById(id).textContent = data[id]);
                                ids.forEach(id => document.getElementById(id).style.display = '');
                            }
                        };
                        xhr.send(body);
                    }
                    function submitFormWithFileAndReceiveXml(form, method) {
                        let ids = ['submittedForm', 'valuesOfX', 'fileName', 'fileContents'];
                        document.getElementById('json').textContent = 'none';
                        ids.forEach(id => document.getElementById(id).style.display = 'none');
                        ids.forEach(id => document.getElementById(id).textContent = 'Loading ...');
                        let body = new FormData();
                        body.append('x', 'body');
                        let fileInput = document.querySelector('#form' + form + ' input[name="file"]');
                        if (fileInput.files.length !== 0) {
                            body.append('file', fileInput.files[0]);
                        }
                        // for some reason spring boot dislikes the "patch" method
                        if (method === 'PATCH') {
                            method = 'POST';
                            body.append('_method', 'PATCH');
                        }
                        let xhr = new XMLHttpRequest();
                        xhr.open(method, '?form=' + encodeURIComponent(form) + '&x=query', false);
                        // Warning: do NOT set the Content-Type header yourself!
                        // xhr.setRequestHeader('Content-Type', 'multipart/form-data');
                        xhr.setRequestHeader('Accept', 'application/xml');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === 4 && xhr.status === 200) {
                                const data = xhr.responseXML;
                                ids.forEach(id => document.getElementById(id).textContent = data.evaluate("//" + id + "/text()", data, null, XPathResult.STRING_TYPE, null).stringValue);
                                ids.forEach(id => document.getElementById(id).style.display = '');
                            }
                        };
                        xhr.send(body);
                    }
                    function submitFormWithFileAndJson(form, method) {
                        let ids = ['submittedForm', 'valuesOfX', 'fileName', 'fileContents', 'json'];
                        ids.forEach(id => document.getElementById(id).style.display = 'none');
                        ids.forEach(id => document.getElementById(id).textContent = 'Loading ...');
                        let body = new FormData();
                        body.append('x', 'body');
                        let fileInput = document.querySelector('#form' + form + ' input[name="file"]');
                        if (fileInput.files.length !== 0) {
                            body.append('file', fileInput.files[0]);
                        }
                        // for some reason spring boot dislikes the "patch" method
                        if (method === 'PATCH') {
                            method = 'POST';
                            body.append('_method', 'PATCH');
                        }
                        body.append('json', new Blob([JSON.stringify({"a":"b","x":"foo"})], {type: 'application/json'}));
                        let xhr = new XMLHttpRequest();
                        xhr.open(method, '?form=' + encodeURIComponent(form) + '&x=query', false);
                        // Warning: do NOT set the Content-Type header yourself!
                        // xhr.setRequestHeader('Content-Type', 'multipart/form-data');
                        xhr.setRequestHeader('Accept', 'application/json');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === 4 && xhr.status === 200) {
                                const data = JSON.parse(xhr.responseText);
                                ids.forEach(id => document.getElementById(id).textContent = data[id]);
                                ids.forEach(id => document.getElementById(id).style.display = '');
                            }
                        };
                        xhr.send(body);
                    }
                    function submitJson(form, method) {
                        let ids = ['submittedForm', 'valuesOfX', 'fileName', 'fileContents', 'json'];
                        ids.forEach(id => document.getElementById(id).style.display = 'none');
                        ids.forEach(id => document.getElementById(id).textContent = 'Loading ...');
                        let body = JSON.stringify({"a":"b","x":"foo"});
                        let xhr = new XMLHttpRequest();
                        xhr.open(method, '?form=' + encodeURIComponent(form) + '&x=query', false);
                        xhr.setRequestHeader('Content-Type', 'application/json');
                        xhr.setRequestHeader('Accept', 'application/json');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === 4 && xhr.status === 200) {
                                const data = JSON.parse(xhr.responseText);
                                ids.forEach(id => document.getElementById(id).textContent = data[id]);
                                ids.forEach(id => document.getElementById(id).style.display = '');
                            }
                        };
                        xhr.send(body);
                    }
                </script>
            </body>
            </html>
            """;

    @GetMapping
    @ResponseBody
    public String doGet(
            @SessionAttribute(name = "submittedForm", required = false) String submittedForm,
            @SessionAttribute(name = "valuesOfX", required = false) String valuesOfX,
            @SessionAttribute(name = "fileName", required = false) String fileName,
            @SessionAttribute(name = "fileContents", required = false) String fileContents,
            @SessionAttribute(name = "json", required = false) String json,
            HttpSession session) {
        session.removeAttribute("submittedForm");
        session.removeAttribute("valuesOfX");
        session.removeAttribute("fileName");
        session.removeAttribute("fileContents");
        session.removeAttribute("json");
        return HTML_TEMPLATE.formatted(
                htmlEscape(requireNonNullElse(submittedForm, "none")),
                htmlEscape(requireNonNullElse(valuesOfX, "none")),
                htmlEscape(requireNonNullElse(fileName, "none")),
                htmlEscape(requireNonNullElse(fileContents, "none")),
                htmlEscape(requireNonNullElse(json, "none"))
        );
    }

    @PostMapping(params = "form=1")
    public String handleForm1(String form, HttpSession session) {
        session.setAttribute("submittedForm", form);
        return "redirect:/";
    }

    @PostMapping(params = "form=2")
    public String handleForm2(String form, HttpSession session) {
        session.setAttribute("submittedForm", form);
        return "redirect:/";
    }

    @PostMapping(params = "form=3")
    public String handleForm3(String form, HttpSession session) {
        session.setAttribute("submittedForm", form);
        return "redirect:/";
    }

    @PostMapping(params = "form=4")
    public String handleForm4(String form, HttpSession session) {
        session.setAttribute("submittedForm", form);
        return "redirect:/";
    }

    @PostMapping(params = "form=5")
    public String handleForm5(String form, HttpSession session) {
        session.setAttribute("submittedForm", form);
        return "redirect:/";
    }

    @PostMapping(params = "form=6")
    public String handleForm6(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            HttpSession session) {
        session.setAttribute("submittedForm", form);
        if (x != null) {
            session.setAttribute("valuesOfX", String.join(", ", x));
        }
        return "redirect:/";
    }

    @PostMapping(params = "form=7")
    public String handleForm7(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            HttpSession session) {
        session.setAttribute("submittedForm", form);
        if (x != null) {
            session.setAttribute("valuesOfX", String.join(", ", x));
        }
        return "redirect:/";
    }

    @PostMapping(params = "form=8")
    public String handleForm8(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            HttpSession session) {
        session.setAttribute("submittedForm", form);
        if (x != null) {
            session.setAttribute("valuesOfX", String.join(", ", x));
        }
        return "redirect:/";
    }

    @PostMapping(params = "form=9")
    public String handleForm9(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            HttpSession session) {
        session.setAttribute("submittedForm", form);
        if (x != null) {
            session.setAttribute("valuesOfX", String.join(", ", x));
        }
        return "redirect:/";
    }

    @PostMapping(params = "form=10")
    public String handleForm10(
            String form,
            @RequestParam(name= "file", required = false) MultipartFile file,
            HttpSession session) throws IOException {
        session.setAttribute("submittedForm", form);
        if (file != null && !file.isEmpty()) {
            session.setAttribute("fileName", requireNonNullElse(file.getOriginalFilename(), "null"));
            session.setAttribute("fileContents", new String(file.getBytes(), StandardCharsets.US_ASCII));
        }
        return "redirect:/";
    }

    @PutMapping(params = "form=11", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm11(
            String form,
            @RequestParam(name = "x", required = false) String[] x) {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", "none",
                "fileContents", "none"
        );
    }

    @DeleteMapping(params = "form=12", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm12(
            String form,
            @RequestParam(name = "x", required = false) String[] x) {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", "none",
                "fileContents", "none"
        );
    }

    @PatchMapping(params = "form=13", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm13(
            String form,
            @RequestParam(name = "x", required = false) String[] x) {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", "none",
                "fileContents", "none"
        );
    }

    @RequestMapping(method = OPTIONS, params = "form=14", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm14(
            String form,
            @RequestParam(name = "x", required = false) String[] x) {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", "none",
                "fileContents", "none"
        );
    }

    @PutMapping(params = "form=15", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm15(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @DeleteMapping(params = "form=16", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm16(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @PatchMapping(params = "form=17", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm17(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @RequestMapping(method = OPTIONS, params = "form=18", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm18(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @PutMapping(params = "form=19", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_XML_VALUE)
    @ResponseBody
    public Map<String, String> handleForm19(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @DeleteMapping(params = "form=20", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_XML_VALUE)
    @ResponseBody
    public Map<String, String> handleForm20(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @PatchMapping(params = "form=21", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_XML_VALUE)
    @ResponseBody
    public Map<String, String> handleForm21(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @RequestMapping(method = OPTIONS, params = "form=22", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_XML_VALUE)
    @ResponseBody
    public Map<String, String> handleForm22(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @PostMapping(params = "form=23", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm23(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none"
        );
    }

    @PostMapping(params = "form=24", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm24(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestPart(name = "json", required = false) Map<String, String> json,
            @RequestPart(name = "file", required = false) MultipartFile file) throws IOException {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", file != null && !file.isEmpty() ? requireNonNullElse(file.getOriginalFilename(), "null") : "none",
                "fileContents", file != null && !file.isEmpty() ? new String(file.getBytes(), StandardCharsets.US_ASCII) : "none",
                "json", json != null ? json.toString() : "none"
        );
    }

    @PostMapping(params = "form=25", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleForm25(
            String form,
            @RequestParam(name = "x", required = false) String[] x,
            @RequestBody(required = false) Map<String, String> json) {
        return Map.of(
                "submittedForm", form,
                "valuesOfX", String.join(", ", x),
                "fileName", "none",
                "fileContents", "none",
                "json", json != null ? json.toString() : "none"
        );
    }

}
