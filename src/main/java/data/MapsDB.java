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
            "select w.oneWay, w.building, w.highway, w.name, n.id, i.position " +
            "from way as w " +
            "join node as n " +
            "on i.node_id = n.id " +
            "join intersection as i " +
            "on i.way_id = w.id " +
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
                System.out.println("Id: " + id + ", lat: " + lat + ", lon: " + lon + ".");
                nodeMap.put(id, new LatLon(lat, lon));
            }

            /* get all the ways that touch them */
            nodeStatement.setDouble(1, box.getS());
            nodeStatement.setDouble(2, box.getN());
            nodeStatement.setDouble(3, box.getW());
            nodeStatement.setDouble(4, box.getE());

            resultSet = wayStatement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getLong("w.id");
                System.out.println("id = " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }

//     get all nodes in a bounding box
//     make a map of id to
//     get all ways connecting to those nodes
//     get all nodes that lie on those ways
//     make

}
