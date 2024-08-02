package com.example.testcasehtmlunit;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.requireNonNullElse;
import static org.springframework.web.util.HtmlUtils.htmlEscape;

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
                <p>You submitted form <b id="submittedForm">%s</b>, the values of 'x' are: <b id="valuesOfX">%s</b>.</p>
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
            </body>
            </html>
            """;

    @GetMapping
    @ResponseBody
    public String doGet(
            @SessionAttribute(name = "submittedForm", required = false) String submittedForm,
            @SessionAttribute(name = "valuesOfX", required = false) String valuesOfX,
            HttpSession session) {
        session.removeAttribute("submittedForm");
        session.removeAttribute("valuesOfX");
        return HTML_TEMPLATE.formatted(
                htmlEscape(requireNonNullElse(submittedForm, "none")),
                htmlEscape(requireNonNullElse(valuesOfX, "none"))
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

}
