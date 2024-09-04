package ru.herooo.projects.unturnedkitsxmlgenerator.directories.custom;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ru.herooo.projects.unturnedkitsxmlgenerator.directories.DirectoryWithExample;
import ru.herooo.projects.unturnedkitsxmlgenerator.directories.FileType;
import ru.herooo.projects.unturnedkitsxmlgenerator.utils.XMLUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class DirectoryItems extends DirectoryWithExample {
    public DirectoryItems() {
        super("items", FileType.XML);
    }

    @Override
    protected File createExample() {
        File example = super.createExample();

        Document document = null;
        try {
            document = XMLUtils.createNewDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Node rootNode = document.createElement("UnturnedItems");
        document.appendChild(rootNode);

        // Название мода предметов
        Comment comment = document.createComment("В теге Mod указывается название модификации, в которую входят перечисленные предметы");
        rootNode.appendChild(comment);

        comment = document.createComment("Данный тег необязателен, но он ускорит генерацию XML-файла наборов (китов)");
        rootNode.appendChild(comment);

        Node modNode = document.createElement("Mod");
        rootNode.appendChild(modNode);

        modNode.setTextContent("ModName");

        // Предметы
        Node itemsNode = createItemsNode(document);
        rootNode.appendChild(itemsNode);

        try {
            example = XMLUtils.saveDocument(document, example);
        } catch (IOException | TransformerException e) {
            throw new RuntimeException(e);
        }

        return example;
    }

    private Node createItemsNode(Document document) {
        Node itemsNode = document.createElement("Items");

        Element itemElement = document.createElement("Item");
        itemElement.setAttribute("id", "101010101");
        itemElement.setAttribute("name", "ModItem");
        itemsNode.appendChild(itemElement);

        itemElement = document.createElement("Item");
        itemElement.setAttribute("id", "2");
        itemElement.setAttribute("name", "Work Jeans");
        itemsNode.appendChild(itemElement);

        itemElement = document.createElement("Item");
        itemElement.setAttribute("id", "67");
        itemElement.setAttribute("name", "Metal Scrap");
        itemsNode.appendChild(itemElement);

        itemElement = document.createElement("Item");
        itemElement.setAttribute("id", "79");
        itemElement.setAttribute("name", "Canned Tuna");
        itemsNode.appendChild(itemElement);

        return itemsNode;
    }
}
