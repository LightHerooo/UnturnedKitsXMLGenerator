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

public class DirectoryKits extends DirectoryWithExample {
    public DirectoryKits() {
        super("kits", FileType.XML);
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

        Node rootNode = document.createElement("Kit");
        document.appendChild(rootNode);

        // Имя набора (кита)
        Node nameNode = document.createElement("Name");
        rootNode.appendChild(nameNode);

        nameNode.setTextContent("KitName");

        // Предметы
        Node itemsNode = createItemsNode(document);
        rootNode.appendChild(itemsNode);

        // Задержка повторной выдачи
        Node cooldownNode = document.createElement("Cooldown");
        rootNode.appendChild(cooldownNode);

        Comment comment = document.createComment("Задержка повторной выдачи (в секундах)");
        cooldownNode.appendChild(comment);

        cooldownNode.setTextContent("600");

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
        itemElement.setAttribute("name", "Work Jeans");
        itemElement.setAttribute("amount", "1");
        itemsNode.appendChild(itemElement);

        itemElement = document.createElement("Item");
        itemElement.setAttribute("name", "Metal Scrap");
        itemElement.setAttribute("amount", "10");
        itemsNode.appendChild(itemElement);

        itemElement = document.createElement("Item");
        itemElement.setAttribute("name", "Canned Tuna");
        itemElement.setAttribute("amount", "3");
        itemsNode.appendChild(itemElement);

        itemsNode.appendChild(document.createTextNode("\n"));

        Comment comment = document.createComment("Если предмет относится к определённой модификации, следует указать её название в атрибуте mod");
        itemsNode.appendChild(comment);

        comment = document.createComment("Важно, чтобы предметы модификации были перечислены в отдельном файле в директории /items, а значение тега <Mod> совпадало со значением атрибута");
        itemsNode.appendChild(comment);

        comment = document.createComment("Данный атрибут необязателен, но он ускорит генерацию XML-файла наборов (китов)");
        itemsNode.appendChild(comment);

        itemElement = document.createElement("Item");
        itemElement.setAttribute("name", "ModItem");
        itemElement.setAttribute("amount", "1");
        itemElement.setAttribute("mod", "ModName");
        itemsNode.appendChild(itemElement);

        return itemsNode;
    }
}
