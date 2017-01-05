package data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import location.BoundingBox;
import location.LatLon;
import tiles.Tile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapsDB {
    private final String getNodes =
            "select * from node " +
            "where latitude >= ? and " +
            "latitude < ? and " +
            "longitude >= ? and " +
            "longitude < ?;";
    private final String getWays =
            "select w.id as wid, " +
                    "w.one_way as oneway, " +
                    "w.building as building, " +
                    "w.highway_type as highway, " +
                    "w.street_name as name, " +
                    "w.num_nodes as numnodes, " +
                    "n.id as nid, " +
                    "i.position as position " +
            "from way as w " +
            "join node as n " +
            "join intersection as i " +
            "on i.way_id = w.id and " +
            "i.node_id = n.id " +
            "where n.latitude >= ? and " +
            "n.latitude < ? and " +
            "n.longitude >= ? and " +
            "n.longitude < ?;";
    private PreparedStatement nodeStatement;
    private PreparedStatement wayStatement;

    public MapsDB(Connection connection) throws SQLException {
        nodeStatement = connection.prepareStatement(getNodes);
        wayStatement = connection.prepareStatement(getWays);
    }

    public List<DrawWay> fillTile(Tile tile) {
        List<DrawWay> drawWayList = Lists.newArrayList();
        try {
            /* make a map of nodes in the region */
            BoundingBox box = tile.getBox();
            nodeStatement.setDouble(1, box.getS());
            nodeStatement.setDouble(2, box.getN());
            nodeStatement.setDouble(3, box.getW());
            nodeStatement.setDouble(4, box.getE());

            ResultSet resultSet = nodeStatement.executeQuery();

            Map<Long, LatLon> nodeMap = Maps.newHashMap();

            long id;
            double lat, lon;
            while (resultSet.next()) {
                id = resultSet.getLong("id");
                lat = resultSet.getDouble("latitude");
                lon = resultSet.getDouble("longitude");
                nodeMap.put(id, new LatLon(lat, lon));
            }

            /* get all the ways that touch them */
            wayStatement.setDouble(1, box.getS());
            wayStatement.setDouble(2, box.getN());
            wayStatement.setDouble(3, box.getW());
            wayStatement.setDouble(4, box.getE());

            resultSet = wayStatement.executeQuery();

            Map<Long, Way> wayMap = Maps.newHashMap();
            Map<Long, Long[]> intersectionMap = Maps.newHashMap();

            Boolean oneWay;
            String building;
            String highway;
            String name;
            long nid;
            int numNodes;
            int position;
            while (resultSet.next()) {
                // w.id, w.one_way, w.building, w.highway_type, w.street_name, w.num_nodes, n.id, i.position
                id = resultSet.getLong("wid");
                nid = resultSet.getLong("nid");
                position = resultSet.getInt("position");
                if (wayMap.containsKey(id)) {
                    intersectionMap.get(id)[position] = nid;
                } else {
                    Way way = new Way();

                    oneWay = resultSet.getBoolean("oneway");
                    building = resultSet.getString("building");
                    highway = resultSet.getString("highway");
                    name = resultSet.getString("name");

                    way.setOneWay(oneWay);
                    way.setBuilding(building);
                    way.setHighway(highway);
                    way.setName(name);

                    wayMap.put(id, way);

                    numNodes = resultSet.getInt("numnodes");
                    Long[] nodes = new Long[numNodes];
                    Arrays.fill(nodes, -1L);
                    nodes[position] = nid;
                    intersectionMap.put(id, nodes);
                }
            }

            Way way;
            Long[] nodes;
            Iterator iterator = wayMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                id = (Long) pair.getKey();
                way = (Way) pair.getValue();
                nodes = intersectionMap.get(id);
                long prev;
                long curr;
                for (int i = 1; i < nodes.length; i ++) {
                    prev = nodes[i - 1];
                    curr = nodes[i];
                    if (prev != -1 && curr != -1) {
                        /* make a DrawWay */
                        DrawWay drawWay = new DrawWay(
                                way.isOneWay(),
                                way.getBuilding(),
                                way.getHighway(),
                                way.getName(),
                                nodeMap.get(prev),
                                nodeMap.get(curr));
                        drawWayList.add(drawWay);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drawWayList;
    }

//     get all nodes in a bounding box
//     make a map of id to
//     get all ways connecting to those nodes
//     get all nodes that lie on those ways
//     make

}
