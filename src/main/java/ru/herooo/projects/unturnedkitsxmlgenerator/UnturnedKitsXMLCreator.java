package ru.herooo.projects.unturnedkitsxmlgenerator;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import ru.herooo.projects.unturnedkitsxmlgenerator.directories.custom.DirectoryItems;
import ru.herooo.projects.unturnedkitsxmlgenerator.directories.custom.DirectoryResults;
import ru.herooo.projects.unturnedkitsxmlgenerator.pojo.UnturnedItem;
import ru.herooo.projects.unturnedkitsxmlgenerator.pojo.UnturnedKitItem;
import ru.herooo.projects.unturnedkitsxmlgenerator.utils.FileUtils;
import ru.herooo.projects.unturnedkitsxmlgenerator.utils.StringUtils;
import ru.herooo.projects.unturnedkitsxmlgenerator.utils.XMLUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnturnedKitsXMLCreator {
    private List<UnturnedItemFinder> finders = new ArrayList<>();

    public UnturnedKitsXMLCreator() throws IOException, ParserConfigurationException, SAXException {
        // Добавляем поисковики предметов
        DirectoryItems directoryItems = new DirectoryItems();
        for (File file: directoryItems.getFiles()) {
            if (FileUtils.getExtension(file).equals(".xml")) {
                finders.add(new UnturnedItemFinder(file.getPath()));
            }
        }
    }

    public File create(File[] kits) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document kitsDocument = builder.newDocument();

        // Генерируем заголовок файла
        Element root = kitsDocument.createElement("KitsConfiguration");
        root.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        kitsDocument.appendChild(root);

        // Заполняем наборы (киты)
        Node kitsNode = kitsDocument.createElement("Kits");
        root.appendChild(kitsNode);

        for (File kit: kits) {
            if (FileUtils.getExtension(kit).equals(".xml")) {
                Node kitNode = generateKitNode(kit, kitsDocument);
                kitsNode.appendChild(kitNode);
            }
        }

        // Генерируем XML-файл на основе данных
        DirectoryResults directoryResults = new DirectoryResults();
        File file = directoryResults.createFile(String.format("%s/%s",
                        StringUtils.createLocalDateTimeStr("dd.MM.yyy (hh-mm-ss)"), Constants.KITS_CONFIGURATION_XML));
        return XMLUtils.saveDocument(kitsDocument, file);
    }

    private Node generateKitNode(File kit, Document kitsDocument) throws ParserConfigurationException, IOException, SAXException {
        Node resultNode = kitsDocument.createElement("Kit");

        // Читаем XML-файл набора
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document kitDocument = builder.parse(kit);
        Node root = kitDocument.getDocumentElement();

        // Основная информация о наборе
        NodeList rootNodes = root.getChildNodes();
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node rootNode = rootNodes.item(i);
            if (rootNode.getNodeName().equals("Name")) {
                // Имя набора
                Node nameNode = kitsDocument.createElement("Name");
                nameNode.setTextContent(rootNode.getTextContent());
                resultNode.appendChild(nameNode);

                // Другие теги
                Node xpNode = kitsDocument.createElement("XP");
                xpNode.setTextContent("0");
                resultNode.appendChild(xpNode);

                Element moneyElement = kitsDocument.createElement("Money");
                moneyElement.setAttribute("xsi:nil", "true");
                resultNode.appendChild(moneyElement);

                Element vehicleElement = kitsDocument.createElement("Vehicle");
                vehicleElement.setAttribute("xsi:nil", "true");
                resultNode.appendChild(vehicleElement);

                break;
            }
        }

        // Предметы набора
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node rootNode = rootNodes.item(i);
            if (rootNode.getNodeName().equals("Items")) {
                Node itemsNode = kitsDocument.createElement("Items");
                resultNode.appendChild(itemsNode);

                NodeList itemsNodes = rootNode.getChildNodes();
                for (int j = 0; j < itemsNodes.getLength(); j++) {
                    Node itemNode = itemsNodes.item(j);
                    if (itemNode.getNodeName().equals("Item")) {
                        NamedNodeMap attributes = itemNode.getAttributes();
                        if (attributes != null) {
                            UnturnedKitItem unturnedKitItem = new UnturnedKitItem();
                            Node nameAtt = attributes.getNamedItem("name");
                            if (nameAtt != null) {
                                unturnedKitItem.setName(nameAtt.getNodeValue());
                            }

                            Node amountAtt = attributes.getNamedItem("amount");
                            if (amountAtt != null) {
                                unturnedKitItem.setAmount(Integer.parseInt(amountAtt.getNodeValue()));
                            }

                            Node modAtt = attributes.getNamedItem("mod");
                            if (modAtt != null) {
                                unturnedKitItem.setMod(modAtt.getNodeValue());
                            }

                            UnturnedItem unturnedItem = findUnturnedItem(unturnedKitItem);
                            if (unturnedItem == null) {
                                throw new RuntimeException(String.format("Предмет с именем \"%s\" не найден",
                                        unturnedKitItem.getName()));
                            }

                            Element itemElement = kitsDocument.createElement("Item");
                            itemElement.setAttribute("id", String.valueOf(unturnedItem.getId()));
                            itemElement.setAttribute("amount", String.valueOf(unturnedKitItem.getAmount()));

                            itemsNode.appendChild(itemElement);
                        }
                    }
                }

                break;
            }
        }

        // Задержка выдачи набора
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node rootNode = rootNodes.item(i);
            if (rootNode.getNodeName().equals("Cooldown")) {
                // Задержка повторной выдачи набора
                Node cooldownNode = kitsDocument.createElement("Cooldown");
                cooldownNode.setTextContent(rootNode.getTextContent());
                resultNode.appendChild(cooldownNode);
            }
        }

        return resultNode;
    }

    private UnturnedItem findUnturnedItem(UnturnedKitItem unturnedKitItem) {
        UnturnedItem unturnedItem = null;
        if (unturnedKitItem != null) {
            String mod = unturnedKitItem.getMod();
            if (mod != null) {
                UnturnedItemFinder finder = finders
                        .stream()
                        .filter(f -> f.getMod() != null && f.getMod().equals(mod))
                        .findFirst()
                        .orElse(null);
                if (finder != null) {
                    unturnedItem = finder.find(unturnedKitItem.getName());
                } else {
                    throw new RuntimeException(String.format("Поисковик мода \"%s\" не найден", mod));
                }
            } else {
                for (UnturnedItemFinder finder: finders) {
                    unturnedItem = finder.find(unturnedKitItem.getName());
                    if (unturnedItem != null) break;
                }
            }
        }

        return unturnedItem;
    }
}
