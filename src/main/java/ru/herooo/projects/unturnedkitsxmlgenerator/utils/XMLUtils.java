package ru.herooo.projects.unturnedkitsxmlgenerator.utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class XMLUtils {
    public static Document createNewDocument() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.newDocument();
    }

    public static Document parseDocument(String filePath) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(filePath);
    }

    public static Document parseDocument(InputStream inputStream) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(inputStream);
    }

    public static File saveDocument(Document xmlDoc, String xmlFilePath) throws TransformerException, IOException {
        DOMSource source = new DOMSource(xmlDoc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        File xml = tryToCreateXMLFile(xmlFilePath);
        StreamResult streamResult = new StreamResult(xml);
        transformer.transform(source, streamResult);

        return xml;
    }

    public static File saveDocument(Document xmlDoc, File xmlFile) throws IOException, TransformerException {
        return saveDocument(xmlDoc, xmlFile.getPath());
    }

    private static File tryToCreateXMLFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (directory != null) {
                directory.mkdirs();
            }

            file.createNewFile();
        }

        return file;
    }
}
