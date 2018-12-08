package club.szuai.signin.utils;

/**
 * 经纬度工具类(lat:纬度;lng:经度)
 */
public class LatAndLongitudeUtil {

    /**
     * 地球半径:赤道半径(单位m)
     */
    private static final double EARTH_RADIUS = 6371000;


    public static double hav(double theta) {
        double s = Math.sin(theta / 2);
        return s * s;
    }

    /**
     * 计算两点距离(单位m)
     */
    public static double getDistance(double lat0, double lng0, double lat1, double lng1) {
        // from math import sin, asin, cos, radians, fabs, sqrt

        // def hav(theta):
        // s = sin(theta / 2)
        // return s * s

        // def get_distance_hav(lat0, lng0, lat1, lng1):
        // "用haversine公式计算球面两点间的距离。"
        // # 经纬度转换成弧度
        // lat0 = radians(lat0)
        // lat1 = radians(lat1)
        // lng0 = radians(lng0)
        // lng1 = radians(lng1)

        // dlng = fabs(lng0 - lng1)
        // dlat = fabs(lat0 - lat1)
        // h = hav(dlat) + cos(lat0) * cos(lat1) * hav(dlng)
        // distance = 2 * EARTH_RADIUS * asin(sqrt(h))

        // return distance
        try {
            lat0 = Math.toRadians(lat0);
            lat1 = Math.toRadians(lat1);
            lng0 = Math.toRadians(lng0);
            lng1 = Math.toRadians(lng1);

            double dlng = Math.abs(lng0 - lng1);
            double dlat = Math.abs(lat0 - lat1);
            double h = hav(dlat) + Math.cos(lat0) * Math.cos(lat1) * hav(dlng);
            double distance = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(h));

            return distance;
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 获取以distance为边长的正方形区域
     */
    public static Location[] getRectangle4Point(double lat, double lng, double distance) {
        // float dlng = 2 * asin(sin(distance / (2 * EARTH_RADIUS)) / cos(lat));
        // float dlng = degrees(dlng) // 弧度转换成角度
        double dlng = 2 * Math.asin(Math.sin(distance / (2 * EARTH_RADIUS)) / Math.cos(lat));
        dlng = Math.toDegrees(dlng);

        // dlat = distance / EARTH_RADIUS
        // dlng = degrees(dlat) # 弧度转换成角度
        double dlat = distance / EARTH_RADIUS;
        dlat = Math.toDegrees(dlat); // # 弧度转换成角度

        // left-top : (lat + dlat, lng - dlng)
        // right-top : (lat + dlat, lng + dlng)
        // left-bottom : (lat - dlat, lng - dlng)
        // right-bottom: (lat - dlat, lng + dlng)
        Location leftTop = new Location(lat + dlat, lng - dlng);
        Location rightTop = new Location(lat + dlat, lng + dlng);
        Location leftBottom = new Location(lat - dlat, lng - dlng);
        Location rightBottom = new Location(lat - dlat, lng + dlng);


        Location[] locations = new Location[4];
        locations[0] = leftTop;
        locations[1] = rightTop;
        locations[2] = leftBottom;
        locations[3] = rightBottom;
        return locations;
    }

    public static class Location {
        public double latitude;
        public double longitude;

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

    }

    public static void main(String[] args) {
        double lat = 24.4369086;
        double lng = 118.1059525;
        double distance = 10000D;
        Location[] locations = LatAndLongitudeUtil.getRectangle4Point(lat, lng, distance);

        for (Location location : locations) {
            System.out.println(location.latitude + "," + location.longitude);
        }
        double lat1 = 23.4369086;
        double lng1 = 118.1059525;
//        double lat1 = 24.4369086;
//        double lng1 = 119.1059525;
        double d = LatAndLongitudeUtil.getDistance(lat, lng, lat1, lng1);
        System.out.println(d);
    }
}