package com.arash.ariani.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.MediaType;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

public class LoggingUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public static String formatContent(String content, String contentType) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        try {
            if (contentType != null) {
                if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                    return formatJson(content);
                } else if (contentType.contains(MediaType.APPLICATION_XML_VALUE)) {
                    return formatXml(content);
                }
            }
        } catch (Exception e) {
            // If formatting fails, return original content
        }
        return content;
    }

    private static String formatJson(String json) {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            return json;
        }
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            return xml;
        }
    }
} 