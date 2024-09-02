package ru.herooo.projects.unturnedkitsxmlgenerator;

import org.xml.sax.SAXException;
import ru.herooo.projects.unturnedkitsxmlgenerator.directories.custom.DirectoryKits;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.Properties;

public class UnturnedKitsXMLGenerator {
    public static void main(String[] args) {
        DirectoryKits directoryKits = new DirectoryKits();
        File[] kits = directoryKits.getFiles();

        File xml = null;
        try {
            System.out.println("---------------");
            System.out.println("Идёт создание файла наборов (китов)");
            System.out.println("---------------");

            UnturnedKitsXMLCreator creator = new UnturnedKitsXMLCreator();
            xml = creator.create(kits);
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            throw new RuntimeException(String.format("Произошла ошибка создания наборов (китов) (%s)", e.getMessage()));
        }

        System.out.println("Файл наборов (китов) успешно создан!");
        System.out.printf("Путь к файлу: %s\n", xml.getAbsolutePath());
        System.out.println("---------------");

        xml = tryToCopyNewKitsConfiguration(xml);
        if (xml != null) {
            System.out.println("Файл наборов (китов) успешно скопирован!");
            System.out.printf("Путь к файлу: %s\n", xml.getAbsolutePath());
            System.out.println("---------------");
        } else {
            System.out.printf("Если вы хотите, чтобы сгенерированный файл сразу копировался в нужную директорию, " +
                    "укажите путь к ней в файле \"%s\"\n", Constants.UNTURNED_KITS_XML_GENERATOR_PROPERTIES);
            System.out.println("---------------");
        }
    }

    private static File tryToCopyNewKitsConfiguration(File originalXML) {
        final String PATH_TO_KITS_CONFIGURATION_XML = "PATH_TO_KITS_CONFIGURATION_XML";

        Properties config = new Properties();
        File kitsConfig = new File(Constants.UNTURNED_KITS_XML_GENERATOR_PROPERTIES);
        if (kitsConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(kitsConfig)) {
                config.load(fis);
            } catch (IOException e) {
                throw new RuntimeException(String.format("Произошла ошибка чтения файла \"%s\" (%s)\n",
                        kitsConfig.getAbsolutePath(),
                        e.getMessage()));
            }
        } else {
            try {
                kitsConfig.createNewFile();

                config.put(PATH_TO_KITS_CONFIGURATION_XML, "");
                try (FileOutputStream fos = new FileOutputStream(kitsConfig)) {
                    config.store(fos, null);
                }
                return null;
            } catch (IOException e) {
                throw new RuntimeException(String.format("Произошла ошибка создания файла \"%s\" (%s)\n",
                        kitsConfig.getAbsolutePath(),
                        e.getMessage()));
            }
        }

        String pathToDirectory = config.getProperty(PATH_TO_KITS_CONFIGURATION_XML);
        if (pathToDirectory == null) return null;

        File directory = new File(pathToDirectory);
        if (!directory.exists() || !directory.isDirectory())
            throw new RuntimeException("Указанный путь для копирования конфигурации наборов (китов) не существует или не является директорией. " +
                    "Если вы уверены, что путь указан верно, проверьте, нет ли обратных слешей в указанном пути (\\). " +
                    "Если в пути такие слеши присутствуют, замените их на обратные (/) или продублируйте их (\\\\).");

        File copiedXML = new File(String.format("%s/%s", directory.getAbsolutePath(), Constants.KITS_CONFIGURATION_XML));
        try (InputStream is = new BufferedInputStream(
                new FileInputStream(originalXML));
             OutputStream os = new BufferedOutputStream(
                     new FileOutputStream(copiedXML))) {

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = is.read(buffer)) > 0) {
                os.write(buffer, 0, lengthRead);
                os.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Произошла ошибка копирования файла (%s)", e.getMessage()));
        }

        return copiedXML;
    }
}
