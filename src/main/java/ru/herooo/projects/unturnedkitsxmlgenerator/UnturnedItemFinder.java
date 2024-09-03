package ru.herooo.projects.unturnedkitsxmlgenerator;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.herooo.projects.unturnedkitsxmlgenerator.pojo.UnturnedItem;
import ru.herooo.projects.unturnedkitsxmlgenerator.utils.XMLUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class UnturnedItemFinder {
    private Document xml;
    private String mod;

    public UnturnedItemFinder(String filePath) throws IOException, ParserConfigurationException, SAXException {
        xml = XMLUtils.parseDocument(filePath);

        // Ищем название мода предметов
        if (xml != null) {
            Node root = xml.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeName().equals("Mod")) {
                    mod = node.getTextContent();
                    break;
                }
            }
        }
    }

    public String getMod() {
        return mod;
    }

    public UnturnedItem find(String name) {
        UnturnedItem unturnedItem = null;
        Node root = xml.getDocumentElement();

        NodeList rootNodes = root.getChildNodes();
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node rootNode = rootNodes.item(i);
            if (rootNode.getNodeName().equals("Items")) {
                unturnedItem = readItems(rootNode.getChildNodes(), name);
                break;
            }
        }

        return unturnedItem;
    }

    private UnturnedItem readItems(NodeList items, String name) {
        UnturnedItem unturnedItem = null;
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            if (item.getNodeName().equals("Item")) {
                NamedNodeMap attributes = item.getAttributes();
                if (attributes != null) {
                    Node nameAtt = attributes.getNamedItem("name");
                    if (nameAtt != null && nameAtt.getTextContent().equalsIgnoreCase(name)) {
                        unturnedItem = new UnturnedItem();

                        // Id
                        Node idAtt = attributes.getNamedItem("id");
                        if (idAtt != null) {
                            unturnedItem.setId(Integer.parseInt(idAtt.getTextContent()));
                        }

                        // Name
                        unturnedItem.setName(nameAtt.getTextContent());

                        break;
                    }
                }
            }
        }

        return unturnedItem;
    }
}
