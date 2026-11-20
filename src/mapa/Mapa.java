package mapa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.event.MouseInputListener;
import org.json.JSONObject;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

public class Mapa extends JXMapViewer {

    private final Image imagen;
    public String road = "";
    public String depar = "";
    Set<Waypoint> waypoints;

    public Mapa() {
        imagen = new ImageIcon(getClass().getResource("/images/icon-posicion-24px.png")).getImage();
        waypoints = new HashSet<>();
    }

    public void init() {
        double latitud = 0;
        double longitud = 0;
        try {
            String mapUrl = System.getenv("TRANSPORMAP_LEGACY_MAP_URL");
            if (mapUrl == null || mapUrl.isBlank()) {
                throw new IllegalStateException("El servicio de mapas heredado no está configurado.");
            }
            URL url = new URL(mapUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            StringBuilder infoRespuesta;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                infoRespuesta = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    infoRespuesta.append(line);
                }
            }
            JSONObject jsonRespuesta = new JSONObject(infoRespuesta.toString());

            latitud = jsonRespuesta.getDouble("latitude");
            longitud = jsonRespuesta.getDouble("longitude");
        } catch (Exception e) {
            System.err.println("No se pudo consultar el servicio de mapas heredado.");
        }

        setTileFactory(new DefaultTileFactory(new OSMTileFactoryInfo()));
        setAddressLocation(new GeoPosition(latitud, longitud));
        setZoom(10);

        MouseInputListener moInput = new PanMouseInputListener(this);
        addMouseMotionListener(moInput);
        addMouseListener(moInput);
        addMouseWheelListener(new ZoomMouseWheelListenerCenter(this));
    }

    public String getLocationInfo(GeoPosition geoPosition) {
        try {
            String apiUrl = "https://nominatim.openstreetmap.org/reverse?format=json&lat="
                    + geoPosition.getLatitude() + "&lon=" + geoPosition.getLongitude() + "&zoom=18&addressdetails=1";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                String address = jsonResponse.getString("display_name");
                System.out.println(jsonResponse.toString());

                road = new String(jsonResponse.getJSONObject("address").optString("road", "").getBytes("ISO-8859-1"), "UTF-8");
                depar = new String(jsonResponse.getJSONObject("address").optString("state", "").getBytes("ISO-8859-1"), "UTF-8");
                return new String(address.getBytes("ISO-8859-1"), "UTF-8");
            } else {
                System.out.println("Error en la solicitud HTTP: " + responseCode);
                return "Error en la solicitud HTTP: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al obtener la informaci�n de ubicaci�n.";
        }
    }
    
    public void init(float latitud, float longitud, int zoom) {
        setTileFactory(new DefaultTileFactory(new OSMTileFactoryInfo()));
        setAddressLocation(new GeoPosition(latitud, longitud));
        setZoom(zoom);

        MouseInputListener moInput = new PanMouseInputListener(this);
        addMouseMotionListener(moInput);
        addMouseListener(moInput);
        addMouseWheelListener(new ZoomMouseWheelListenerCenter(this));
    }

    public void addWaypoints(double latitud, double longitud) {
        waypoints.add(new DefaultWaypoint(latitud, longitud));
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        this.setOverlayPainter(waypointPainter);
    }

    public void showWaypoints() {
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        setOverlayPainter(waypointPainter);
    }

    public void noShowWaypoint() {
        setOverlayPainter(null);
    }

    public void cleanWaypoint() {
        waypoints.clear();
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        setOverlayPainter(waypointPainter);
    }

    public void delateLastWaypoint() {
        if (!waypoints.isEmpty()) {
            Waypoint ultimoMarcador = null;
            for (Waypoint waypoint : waypoints) {
                ultimoMarcador = waypoint;
            }
            waypoints.remove(ultimoMarcador);
            WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(waypoints);
            setOverlayPainter(waypointPainter);
        }
    }

}
