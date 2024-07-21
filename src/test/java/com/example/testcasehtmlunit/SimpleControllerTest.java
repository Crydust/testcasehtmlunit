package com.example.testcasehtmlunit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * When using HtmlUnit ...
 * org.springframework.test.web.servlet.htmlunit.HtmlUnitRequestBuilder.buildRequest
 * org.springframework.test.web.servlet.htmlunit.HtmlUnitRequestBuilder.params
 * reads this.webRequest.getParameters(), but ignores query parameters?
 * org.htmlunit.WebRequest.getParameters ignores query string parameters for post?
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SimpleControllerTest {

    @Autowired
    WebDriver driver;

    @Test
    void shouldDoGet() {
        driver.get("http://localhost:8080/");

        String submittedForm = driver.findElement(By.cssSelector("#submittedForm")).getText();
        assertThat(submittedForm, is("none"));
    }

    @Test
    void shouldHandleForm1() {
        // fails with HtmlUnitRequestBuilder
        driver.get("http://localhost:8080/");

        driver.findElement(By.cssSelector("#form1 button")).click();

        String submittedForm = driver.findElement(By.cssSelector("#submittedForm")).getText();
        assertThat(submittedForm, is("1"));
    }

    @Test
    void shouldHandleForm2() {
        // fails with HtmlUnitRequestBuilder
        driver.get("http://localhost:8080/");

        driver.findElement(By.cssSelector("#form2 button")).click();

        String submittedForm = driver.findElement(By.cssSelector("#submittedForm")).getText();
        assertThat(submittedForm, is("2"));
    }

    @Test
    void shouldHandleForm3() {
        driver.get("http://localhost:8080/");

        driver.findElement(By.cssSelector("#form3 button")).click();

        String submittedForm = driver.findElement(By.cssSelector("#submittedForm")).getText();
        assertThat(submittedForm, is("3"));
    }

    @Test
    void shouldHandleForm4() {
        // fails with HtmlUnitRequestBuilder
        driver.get("http://localhost:8080/");

        driver.findElement(By.cssSelector("#form4 button")).click();

        String submittedForm = driver.findElement(By.cssSelector("#submittedForm")).getText();
        assertThat(submittedForm, is("4"));
    }

}
