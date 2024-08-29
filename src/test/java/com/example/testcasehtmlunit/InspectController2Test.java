package com.example.testcasehtmlunit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class InspectController2Test {

    @Autowired
    WebDriver driver;

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

        output.writeArguments(nr, method, query, encoding, body, accept, expected, actual);
        assertThat(actual, is(expected));
    }

    public static Stream<Arguments> factory() throws Exception {
        final List<Arguments> arguments = ArgumentsXmlDocument.readArguments("/OutputFromFirefox2.xml");
        return arguments.stream()
                .filter(not(it -> "TRACE".equals(it.get()[1]) && "text/plain".equals(it.get()[3])))
//                .filter(it -> Integer.valueOf(108).equals(it.get()[0]))
//                .filter(it -> Integer.valueOf(877).equals(it.get()[0]))
//                .sorted(comparingInt(it -> (int) it.get()[0]))
//                .limit(100)
                ;
    }

    private void waitUntilAjaxFinished() {
        // NOOP ... all xhr calls are synchronous
//        new WebDriverWait(driver, Duration.of(2, SECONDS))
//                .until(visibilityOfElementLocated(By.id("output")));
    }

}
