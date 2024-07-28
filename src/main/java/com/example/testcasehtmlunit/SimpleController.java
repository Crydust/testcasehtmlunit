package com.example.testcasehtmlunit;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.requireNonNullElse;

@Controller
@RequestMapping("/")
public class SimpleController {

    private static final String HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Minimal testcase query parameters ignored in post request by HtmlUnitRequestBuilder</title>
            </head>
            <body>
                <h1>Minimal testcase query parameters ignored in post request by HtmlUnitRequestBuilder</h1>
                <p>You submitted form <b id="submittedForm">%s</b>.</p>
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
            </body>
            </html>
            """;

    @GetMapping
    @ResponseBody
    public String doGet(@SessionAttribute(name = "submittedForm", required = false) String submittedForm, HttpSession session) {
        return HTML_TEMPLATE.formatted(requireNonNullElse(submittedForm, "none"));
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

}
