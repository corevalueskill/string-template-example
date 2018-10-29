package com.corevalueskill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.stringtemplate.v4.ST;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@SpringBootApplication
public class StringTemplateExampleApplication implements CommandLineRunner {

    @Autowired
    private ResourceLoader resourceLoader;

    private static final char DELIMITER = '$';

    public static void main(String[] args) {
        SpringApplication.run(StringTemplateExampleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final Resource resource = resourceLoader.getResource("classpath:templates/email/email-Test.html");
        final InputStream inputStream = resource.getInputStream();
        final ST emailTestTemplate = new ST(new String(readBytes(inputStream)), DELIMITER, DELIMITER);
        System.out.println(processTemplate(emailTestTemplate));
    }

    private String processTemplate(ST emailTestTemplate) {
        String emailContent = "Hello, World!";

        addAttributeToEmailTemplate(emailTestTemplate, "Title", "Mail Subject is Here");
        addAttributeToEmailTemplate(emailTestTemplate, "firstName", "Alan");
        addAttributeToEmailTemplate(emailTestTemplate, "lastName", "Walker");
        addAttributeToEmailTemplate(emailTestTemplate, "content", emailContent);
        addAttributeToEmailTemplate(emailTestTemplate, "senderFullName", "corevalueskill");

        return emailTestTemplate.render();
    }

    private void addAttributeToEmailTemplate(ST emailTemplate, String attributeName, String attributeValue) {
        try {
            emailTemplate.remove(attributeName);
        } catch (Exception ignored) {
        }

        emailTemplate.add(attributeName, attributeValue);
    }

    private static byte[] readBytes(InputStream stream) throws IOException {
        if (stream == null) return new byte[]{};
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        boolean error = false;
        try {
            int numRead = 0;
            while ((numRead = stream.read(buffer)) > -1) {
                output.write(buffer, 0, numRead);
            }
        } catch (IOException | RuntimeException e) {
            error = true;
            throw e;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                if (!error) throw e;
            }
        }
        output.flush();

        return output.toByteArray();
    }

}
