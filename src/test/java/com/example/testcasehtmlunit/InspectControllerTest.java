package com.example.testcasehtmlunit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@SpringBootTest
@AutoConfigureMockMvc
public class InspectControllerTest {

    @Autowired
    WebDriver driver;

    @TempDir
    static Path tempDir;
    static Path tempFile;

    @BeforeAll
    static void beforeAll() throws IOException {
        tempFile = tempDir.resolve("example.txt");
        Files.writeString(tempFile, "Hello world!", StandardCharsets.US_ASCII);
    }

    @ParameterizedTest
    @MethodSource("factory")
    void isBlank_ShouldReturnTrueForNullOrBlankStrings(String method, String query, String encoding, String body, String accept, String expected) {
        driver.get("http://localhost:8080/form");
        new Select(driver.findElement(By.id("method"))).selectByVisibleText(method);
        new Select(driver.findElement(By.id("query"))).selectByVisibleText(query);
        new Select(driver.findElement(By.id("encoding"))).selectByVisibleText(encoding);
        driver.findElement(By.id("file")).sendKeys(tempFile.toAbsolutePath().toString());
        new Select(driver.findElement(By.id("body"))).selectByVisibleText(body);
        new Select(driver.findElement(By.id("accept"))).selectByVisibleText(accept);

        driver.findElement(By.id("button")).click();
        waitUntilAjaxFinished();

        String actual = driver.findElement(By.id("output")).getText();
        assertThat(actual, is(expected));
    }

    public static Stream<Arguments> factory() {
        final String[] methods = {"GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"};
        final String[] queries = {"", "?a=b", "?a=b&c=d", "?a=", "?a", "?", "?a=b&a=d"};
        final String[] encodings = {"application/x-www-form-urlencoded", "multipart/form-data", "text/plain"};
        final String[] bodies = {"empty", "oneParameter", "emptyValue", "sameAsInQuery", "sameKeyAsInQuery", "sameKeyDifferentValues"};
        final String[] accepts = {"text/html", "application/json", "application/xml", "text/plain"};
        final List<Arguments> arguments = new ArrayList<>();
        for (String method : methods) {
            for (String query : queries) {
                for (String encoding : encodings) {
                    for (String body : bodies) {
                        for (String accept : accepts) {
                            if ((method.equals("GET") || method.equals("HEAD")) && (!encoding.equals("application/x-www-form-urlencoded") || !body.equals("empty"))) {
                                continue;
                            }
                            String expected = "???Unknown???";
                            arguments.add(Arguments.of(method, query, encoding, body, accept, expected));
                        }
                    }
                }
            }
        }
        return arguments.stream();
    }

    private void waitUntilAjaxFinished() {
        new WebDriverWait(driver, Duration.of(2, SECONDS))
                .until(visibilityOfElementLocated(By.id("output")));
    }

}
