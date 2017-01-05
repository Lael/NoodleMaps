package data;

import autocorrect.Trie;

import javax.management.modelmbean.XMLParseException;
import javax.xml.stream.XMLStreamException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * Tosses XML data into the database.
 */
public class FileEater {

    private Connection connection;
    private Statement statement;
    private Trie trie;

    public FileEater(Connection connection, Trie trie) {
        this.connection = connection;
        this.trie = trie;
        try {
            this.statement = this.connection.createStatement();
        } catch (SQLException e) {
            // TODO: not this
            System.exit(1);
        }
    }

    private void insertObject(String sql) throws SQLException {
        statement.executeUpdate(sql);
    }

    /**
     * Isn't very careful about XML format!
     *
     * @param stream the file as a stream
     * @throws XMLStreamException
     * @throws IOException
     */
    public void consumeXml(InputStream stream) throws XMLStreamException, IOException, XMLParseException, SQLException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(stream);

        while (reader.hasNext()) {
            int eventType = reader.getEventType();
            if (eventType == START_ELEMENT) {
                String tagName = reader.getLocalName();
                if (tagName.equals("node")) {
                    getNode(reader);
                } else if (tagName.equals("way")) {
                    getWay(reader);
                }
            }
            reader.next();
        }

        reader.close();
        stream.close();
    }

    private void getNode(XMLStreamReader reader) throws SQLException {
        Node node = new Node();
        int numAtts = reader.getAttributeCount();
        for (int i = 0; i < numAtts; i++) {
            String attName = reader.getAttributeLocalName(i);
            String attValue = reader.getAttributeValue(i);
            if (attName.equals("id")) {
                node.setId(Long.parseLong(attValue));
            } else if (attName.equals("lat")) {
                node.setLat(Double.parseDouble(attValue));
            } else if (attName.equals("lon")) {
                node.setLon(Double.parseDouble(attValue));
            }
        }
        insertObject(node.getInsertMessage());
    }

    private void getWay(XMLStreamReader reader) throws XMLStreamException, XMLParseException, SQLException {
        Way way = new Way();
        int numAtts = reader.getAttributeCount();
        for (int i = 0; i < numAtts; i++) {
            String attName = reader.getAttributeLocalName(i);
            String attValue = reader.getAttributeValue(i);
            if (attName.equals("id")) {
                way.setId(Long.parseLong(attValue));
                break;
            }
        }
        way = getIntersections(way, reader);

        insertObject(way.getInsertMessage());
    }

    private Way getIntersections(Way way, XMLStreamReader reader) throws XMLStreamException, XMLParseException, SQLException {
        int index = 0;
        long firstId = -1;
        while (reader.hasNext()) {
            int eventType = reader.getEventType();
            if (eventType == START_ELEMENT) {
                String name = reader.getLocalName();
                if (name.equals("nd")) {
                    long nodeId = Long.parseLong(reader.getAttributeValue(0));
                    if (nodeId == firstId) {
                        way.setClosed(true);
                    } else {
                        Intersection intersection = new Intersection(way.getId(),
                                nodeId,
                                index);
                        insertObject(intersection.getInsertMessage());
                        index++;
                    }
                    if (firstId == -1) {
                        firstId = nodeId;
                    }
                } else if (name.equals("tag")) {
                    String k = reader.getAttributeValue(0);
                    String v = reader.getAttributeValue(1);
                    if (k.equals("landuse")) {
                        way.setLanduse(v);
                    } else if (k.equals("building")) {
                        way.setBuilding(v);
                    } else if (k.equals("oneway") && v.equals("yes")) {
                        way.setOneWay(true);
                    } else if (k.equals("name")) {
                        way.setName(v);
                        trie.add(v);
                    } else if (k.equals("highway")) {
                        way.setHighway(v);
                    }
                }
            } else if (eventType == END_ELEMENT) {
                if (reader.getLocalName().equals("way")) {
                    way.setNumNodes(index);
                    return way;
                }
            }
            reader.next();
        }
        throw new XMLParseException("File ended too soon!");
    }
}
