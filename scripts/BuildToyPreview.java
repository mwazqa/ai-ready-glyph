import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;

public final class BuildToyPreview {
    private static final int SIZE = 256;
    private static final double PITCH = 9.12;
    private static final double PIXEL = 6.6;
    private static final double SOURCE_X = SIZE / 2.0 - 5 * PITCH;
    private static final double SOURCE_Y = SIZE / 2.0;
    private static final double DOT_RADIUS = 15.2;

    private static boolean lit(int x, int y) {
        double limit = Math.toRadians(53);
        double angle = Math.max(-limit, Math.min(limit, Math.atan2(y - 12, x - 7)));
        for (double radius : new double[]{11, 20, 29}) {
            double scaled = radius * 11.4 / 29;
            if (Math.hypot(x - 7 - scaled * Math.cos(angle), y - 12 - scaled * Math.sin(angle)) <= .83) return true;
        }
        return false;
    }

    private static String circle(double radius) {
        return String.format(Locale.ROOT, "M%.2f,%.2fa%.2f,%.2f 0,1 0,0,%.2fa%.2f,%.2f 0,1 0,0,-%.2fZ",
                SOURCE_X, SOURCE_Y - radius, radius, radius, radius * 2, radius, radius, radius * 2);
    }

    private static String rectangle(double x, double y) {
        return String.format(Locale.ROOT, "M%.2f,%.2fh%.2fv%.2fh-%.2fZ", x, y, PIXEL, PIXEL, PIXEL);
    }

    private static String paths(String data, String color) {
        StringBuilder result = new StringBuilder(), group = new StringBuilder();
        for (String rectangle : data.split("(?=M)")) {
            if (group.length() + rectangle.length() > 600) {
                result.append(path(group.toString(), color));
                group.setLength(0);
            }
            group.append(rectangle);
        }
        if (!group.isEmpty()) result.append(path(group.toString(), color));
        return result.toString();
    }

    private static String path(String data, String color) {
        return "    <path android:fillColor=\"" + color + "\" android:pathData=\"" + data + "\" />\n";
    }

    public static void main(String[] arguments) throws Exception {
        Path root = Path.of(arguments.length > 0 ? arguments[0] : ".");
        StringBuilder dim = new StringBuilder(), bright = new StringBuilder();
        StringBuilder svg = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 256 256\"><circle cx=\"128\" cy=\"128\" r=\"128\" fill=\"#000000\"/>");
        BufferedImage preview = new BufferedImage(SIZE * 2, SIZE * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = preview.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(2, 2);
        g.setColor(Color.BLACK);
        g.fill(new Ellipse2D.Double(0, 0, SIZE, SIZE));
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            if (Math.hypot(x - 12, y - 12) > 12.5) continue;
            double left = SIZE / 2.0 + (x - 12) * PITCH - PIXEL / 2;
            double top = SIZE / 2.0 + (y - 12) * PITCH - PIXEL / 2;
            boolean white = lit(x, y);
            String color = white ? "#FFFFFF" : "#1C1C1C";
            (white ? bright : dim).append(rectangle(left, top));
            svg.append(String.format(Locale.ROOT, "<rect x=\"%.2f\" y=\"%.2f\" width=\"%.2f\" height=\"%.2f\" fill=\"%s\"/>", left, top, PIXEL, PIXEL, color));
            g.setColor(Color.decode(color));
            g.fill(new Rectangle2D.Double(left, top, PIXEL, PIXEL));
        }
        g.setColor(Color.BLACK);
        g.fill(new Ellipse2D.Double(SOURCE_X - DOT_RADIUS - 3, SOURCE_Y - DOT_RADIUS - 3,
                (DOT_RADIUS + 3) * 2, (DOT_RADIUS + 3) * 2));
        g.setColor(Color.decode("#D71920"));
        g.fill(new Ellipse2D.Double(SOURCE_X - DOT_RADIUS, SOURCE_Y - DOT_RADIUS, DOT_RADIUS * 2, DOT_RADIUS * 2));
        g.dispose();
        svg.append(String.format(Locale.ROOT, "<circle cx=\"%.2f\" cy=\"%.2f\" r=\"%.2f\" fill=\"#000000\"/><circle cx=\"%.2f\" cy=\"%.2f\" r=\"%.2f\" fill=\"#D71920\"/>",
                SOURCE_X, SOURCE_Y, DOT_RADIUS + 3, SOURCE_X, SOURCE_Y, DOT_RADIUS));
        svg.append("</svg>\n");
        String vector = "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\" android:width=\"128dp\" android:height=\"128dp\" android:viewportWidth=\"256\" android:viewportHeight=\"256\">\n"
                + path("M128,0a128,128 0,1 0,0,256a128,128 0,1 0,0,-256Z", "#000000")
                + paths(dim.toString(), "#1C1C1C") + paths(bright.toString(), "#FFFFFF")
                + path(circle(DOT_RADIUS + 3), "#000000")
                + path(circle(DOT_RADIUS), "#D71920") + "</vector>\n";
        Path drawables = root.resolve("app/src/main/res/drawable");
        Files.createDirectories(drawables);
        Files.createDirectories(root.resolve("artwork"));
        Files.createDirectories(root.resolve("dist"));
        Files.writeString(drawables.resolve("ic_glyph_toy_preview.xml"), vector);
        Files.writeString(root.resolve("artwork/glyph-toy-preview.svg"), svg);
        ImageIO.write(preview, "png", root.resolve("dist/Notify-Glyph-toy-preview.png").toFile());
    }
}
