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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.stream.*;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Comparator.comparingInt;
import static java.util.function.Predicate.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

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
//        int nr = 0;
//        final String[] methods = {"GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"};
//        final String[] queries = {"", "?a=b", "?a=b&c=d", "?a=", "?a", "?", "?a=b&a=d"};
//        final String[] encodings = {"application/x-www-form-urlencoded", "multipart/form-data", "text/plain"};
//        final String[] bodies = {"empty", "oneParameter", "emptyValue", "sameAsInQuery", "sameKeyAsInQuery", "sameKeyDifferentValues"};
//        final String[] accepts = {"text/html", "application/json", "application/xml", "text/plain"};
//        final List<Arguments> arguments = new ArrayList<>();
//        for (String method : methods) {
//            for (String query : queries) {
//                for (String encoding : encodings) {
//                    for (String body : bodies) {
//                        for (String accept : accepts) {
//                            if ((method.equals("GET") || method.equals("HEAD")) && (!encoding.equals("application/x-www-form-urlencoded") || !body.equals("empty"))) {
//                                continue;
//                            }
//                            String expected = "???Unknown???";
//                            arguments.add(Arguments.of(nr++, method, query, encoding, body, accept, expected));
//                            if (nr > 20) {
//                                return arguments.stream();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return arguments.stream();

        final List<Arguments> arguments = new ArrayList<>();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try (InputStream in = InspectController2Test.class.getResourceAsStream("/OutputFromFirefox2.xml")) {
            StringBuilder sb = new StringBuilder();
            int nr = -1;
            String method = "";
            String query = "";
            String encoding = "";
            String body = "";
            String accept = "";
            String actual = "";
            XMLEventReader reader = null;
            try {
                reader = xmlInputFactory.createXMLEventReader(in);
                while (reader.hasNext()) {
                    XMLEvent nextEvent = reader.nextEvent();
                    if (nextEvent.isStartElement()) {
                        var startElement = nextEvent.asStartElement();
                        switch (startElement.getName().getLocalPart()) {
                            case "nr":
                            case "method":
                            case "query":
                            case "encoding":
                            case "body":
                            case "accept":
                            case "actual":
                                sb.setLength(0);
                                break;
                        }
                    } else if (nextEvent.isCharacters()) {
                        sb.append(nextEvent.asCharacters().getData());
                    } else if (nextEvent.isEndElement()) {
                        EndElement endElement = nextEvent.asEndElement();
                        switch (endElement.getName().getLocalPart()) {
                            case "nr":
                                nr = Integer.parseInt(sb.toString());
                                break;
                            case "method":
                                method = sb.toString();
                                break;
                            case "query":
                                query = sb.toString();
                                break;
                            case "encoding":
                                encoding = sb.toString();
                                break;
                            case "body":
                                body = sb.toString();
                                break;
                            case "accept":
                                accept = sb.toString();
                                break;
                            case "actual":
                                actual = sb.toString();
                                break;
                            case "arguments":
                                arguments.add(Arguments.of(nr, method, query, encoding, body, accept, actual));
                                break;
                        }
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        return arguments.stream()
                .filter(not(it -> "TRACE".equals(it.get()[1]) && "text/plain".equals(it.get()[3])))
//                .filter(it -> Integer.valueOf(108).equals(it.get()[0]))
//                .filter(it -> Integer.valueOf(877).equals(it.get()[0]))
//                .sorted(comparingInt(it -> (int) it.get()[0]))
//                .limit(100)
                ;
    }

    private void waitUntilAjaxFinished() {
        new WebDriverWait(driver, Duration.of(2, SECONDS), Duration.of(100, MILLIS))
                .until(visibilityOfElementLocated(By.id("output")));
    }

}
