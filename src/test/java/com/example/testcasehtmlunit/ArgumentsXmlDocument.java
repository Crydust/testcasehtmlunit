package com.example.testcasehtmlunit;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class ArgumentsXmlDocument implements AutoCloseable {

    private final ReentrantLock lock = new ReentrantLock();
    private final String name;
    private BufferedWriter bufferedWriter;
    private XMLStreamWriter xmlStreamWriter;

    public ArgumentsXmlDocument() {
        name = "output-" + LocalDateTime.now().toString().replaceAll("[^-.0-9A-Za-z]+", "-") + ".xml";
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
