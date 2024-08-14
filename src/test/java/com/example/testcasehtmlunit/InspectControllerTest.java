package com.example.testcasehtmlunit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

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

@Disabled("This test is disabled here, because it is meant to be run with a real browser to generate a 'golden master'.")
public class InspectControllerTest {

    private static WebDriver driver;

    @BeforeAll
    static void setup() {
//        driver = WebDriverManager.firefoxdriver().create();
        driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080/form");
    }

    @AfterAll
    static void teardown() {
        driver.quit();
    }
//
//    @BeforeEach
//    void setup() {
//        driver = WebDriverManager.firefoxdriver().create();
////        driver = new HtmlUnitDriver();
//    }
//
//    @AfterEach
//    void teardown() {
//        driver.quit();
//    }

    @TempDir
    static Path tempDir;
    static Path tempFile;

    private static final ArgumentsXmlDocument output = new ArgumentsXmlDocument();

    @BeforeAll
    static void createTempFile() throws IOException {
        tempFile = tempDir.resolve("example.txt");
        Files.writeString(tempFile, "Hello world!", StandardCharsets.US_ASCII);
    }

    @BeforeAll
    static void openOutputFile() throws Exception {
        output.writeStart();
    }

    @AfterAll
    static void afterAll() throws Exception {
        output.close();
    }

    @ParameterizedTest(name = "{0}: method={1}, query={2}, encoding={3}, body={4}, accept={5}")
    @MethodSource("factory")
    void shouldSubmitForm(int nr, String method, String query, String encoding, String body, String accept, String expected) throws Exception {
        //driver.get("http://localhost:8080/form");
        new Select(driver.findElement(By.id("method"))).selectByVisibleText(method);
        new Select(driver.findElement(By.id("query"))).selectByVisibleText(query);
        new Select(driver.findElement(By.id("encoding"))).selectByVisibleText(encoding);
        driver.findElement(By.id("file")).sendKeys(tempFile.toAbsolutePath().toString());
        new Select(driver.findElement(By.id("body"))).selectByVisibleText(body);
        new Select(driver.findElement(By.id("accept"))).selectByVisibleText(accept);

        driver.findElement(By.id("button")).click();
        waitUntilAjaxFinished();

        String actual = driver.findElement(By.id("output")).getText();

        output.writeArguments(nr, method, query, encoding, body, accept, expected, actual);
        assertThat(actual, is(expected));
    }

    public static Stream<Arguments> factory() {
        int nr = 0;
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
                            if ((method.equals("GET") || method.equals("HEAD"))
                                && (!encoding.equals("application/x-www-form-urlencoded") || !body.equals("empty"))) {
                                continue;
                            }
                            String expected = "???Unknown???";
                            arguments.add(Arguments.of(nr++, method, query, encoding, body, accept, expected));
                        }
                    }
                }
            }
        }
        return arguments.stream()
                //.filter(not(it -> "TRACE".equals(it.get()[1]) && "text/plain".equals(it.get()[3])))
                //.filter(it -> "TRACE".equals(it.get()[1]))
                ;
    }

    private void waitUntilAjaxFinished() {
        new WebDriverWait(driver, Duration.of(2, SECONDS))
                .until(visibilityOfElementLocated(By.id("output")));
    }

}
