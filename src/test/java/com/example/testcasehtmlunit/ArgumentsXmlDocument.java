package com.example.testcasehtmlunit;

import org.junit.jupiter.params.provider.Arguments;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ArgumentsXmlDocument implements AutoCloseable {

    private final ReentrantLock lock = new ReentrantLock();
    private final String name;
    private BufferedWriter bufferedWriter;
    private XMLStreamWriter xmlStreamWriter;

    public ArgumentsXmlDocument() {
        name = "output-" + LocalDateTime.now().toString().replaceAll("[^-.0-9A-Za-z]+", "-") + ".xml";
    }

    public static List<Arguments> readArguments(String resource) throws IOException, XMLStreamException {
        final List<Arguments> arguments = new ArrayList<>();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try (InputStream in = InspectController2Test.class.getResourceAsStream(resource)) {
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
                        sb.setLength(0);
                    } else if (nextEvent.isCharacters()) {
                        sb.append(nextEvent.asCharacters().getData());
                    } else if (nextEvent.isEndElement()) {
                        switch (nextEvent.asEndElement().getName().getLocalPart()) {
                            case "nr" -> nr = Integer.parseInt(sb.toString());
                            case "method" -> method = sb.toString();
                            case "query" -> query = sb.toString();
                            case "encoding" -> encoding = sb.toString();
                            case "body" -> body = sb.toString();
                            case "accept" -> accept = sb.toString();
                            case "actual" -> actual = sb.toString();
                            case "arguments" -> arguments.add(Arguments.of(nr, method, query, encoding, body, accept, actual));
                        }
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        return arguments;
    }

    public void writeStart() throws Exception {
        lock.lock();
        try {
            bufferedWriter = Files.newBufferedWriter(Path.of(name), StandardCharsets.UTF_8);
            xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(bufferedWriter);
            xmlStreamWriter.writeStartDocument();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("root");
        } finally {
            lock.unlock();
        }
    }

    public void writeArguments(int nr, String method, String query, String encoding, String body, String accept, String expected, String actual) throws Exception {
        lock.lock();
        try {
            xmlStreamWriter.writeCharacters("\n  ");
            xmlStreamWriter.writeStartElement("arguments");
            xmlStreamWriter.writeCharacters("\n    ");
            xmlStreamWriter.writeStartElement("nr");
            xmlStreamWriter.writeCharacters(String.valueOf(nr));
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n    ");
            xmlStreamWriter.writeStartElement("method");
            xmlStreamWriter.writeCharacters(method);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n    ");
            xmlStreamWriter.writeStartElement("query");
            xmlStreamWriter.writeCharacters(query);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n    ");
            xmlStreamWriter.writeStartElement("encoding");
            xmlStreamWriter.writeCharacters(encoding);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n    ");
            xmlStreamWriter.writeStartElement("body");
            xmlStreamWriter.writeCharacters(body);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n    ");
            xmlStreamWriter.writeStartElement("accept");
            xmlStreamWriter.writeCharacters(accept);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n    ");
            xmlStreamWriter.writeComment("expected: " + expected);
//            xmlStreamWriter.writeStartElement("expected");
//            xmlStreamWriter.writeCharacters(expected);
//            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n    ");
            xmlStreamWriter.writeStartElement("actual");
            xmlStreamWriter.writeCharacters(actual);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n  ");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.flush();
        } finally {
            lock.unlock();
        }
    }

    public void writeEnd() throws Exception {
        lock.lock();
        try {
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.flush();
            xmlStreamWriter.close();
            bufferedWriter.flush();
            bufferedWriter.close();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        writeEnd();
    }
}
