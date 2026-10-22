import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;

public final class BuildAppIcon {
    private static final String[] SPARK = {
            "....##...",
            "....##...",
            ".##...##.",
            "..#...#..",
            "##..r..##",
            "..#...#..",
            ".##...##.",
            "...##....",
            "...##...."
    };

    private static String circle(double x, double y, double radius) {
        return String.format(Locale.ROOT, "M%.2f,%.2f a%.2f,%.2f 0,1 0,%.2f,0 a%.2f,%.2f 0,1 0,%.2f,0",
                x - radius, y, radius, radius, radius * 2, radius, radius, -radius * 2);
    }

    private static String paths(String data, String color) {
        StringBuilder result = new StringBuilder(), group = new StringBuilder();
        for (String circle : data.split("(?=M)")) {
            if (group.length() + circle.length() > 600) {
                result.append(path(group.toString(), color)); group.setLength(0);
            }
            group.append(circle);
        }
        if (!group.isEmpty()) result.append(path(group.toString(), color));
        return result.toString();
    }

    private static String path(String data, String color) {
        return "    <path android:fillColor=\"" + color + "\" android:pathData=\"" + data + "\" />\n";
    }

    public static void main(String[] arguments) throws Exception {
        Path root = Path.of(arguments.length > 0 ? arguments[0] : ".");
        Path drawables = root.resolve("app/src/main/res/drawable");
        Files.createDirectories(drawables);
        Files.createDirectories(root.resolve("artwork"));
        Files.createDirectories(root.resolve("dist"));
        StringBuilder svg = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 108 108\"><rect width=\"108\" height=\"108\" rx=\"15\" fill=\"#080808\"/>");
        String header = "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\" android:width=\"108dp\" android:height=\"108dp\" android:viewportWidth=\"108\" android:viewportHeight=\"108\">\n";
        StringBuilder body = new StringBuilder();
        BufferedImage preview = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = preview.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(512.0 / 108, 512.0 / 108);
        g.setColor(Color.decode("#080808"));
        g.fill(new RoundRectangle2D.Double(0, 0, 108, 108, 30, 30));
        StringBuilder bright = new StringBuilder(), accent = new StringBuilder();
        for (int y = 0; y < SPARK.length; y++) for (int x = 0; x < SPARK[y].length(); x++) {
            char pixel = SPARK[y].charAt(x);
            if (pixel == '.') continue;
            double cx = 54 + (x - 4) * 5.6, cy = 54 + (y - 4) * 5.6;
            double radius = pixel == 'r' ? 3.9 : 2.1;
            String color = pixel == 'r' ? "#D71920" : "#F5F5F5";
            (pixel == 'r' ? accent : bright).append(circle(cx, cy, radius));
            svg.append(String.format(Locale.ROOT, "<circle cx=\"%.2f\" cy=\"%.2f\" r=\"%.2f\" fill=\"%s\"/>", cx, cy, radius, color));
            g.setColor(Color.decode(color));
            g.fill(new Ellipse2D.Double(cx - radius, cy - radius, radius * 2, radius * 2));
        }
        body.append(paths(bright.toString(), "#F5F5F5"));
        String dot = paths(accent.toString(), "#D71920");
        svg.append("</svg>\n");
        g.dispose();
        Files.writeString(drawables.resolve("ic_ready_foreground.xml"), header + body + dot + "</vector>\n");
        Files.writeString(drawables.resolve("ic_ready_monochrome.xml"), header
                + body.toString().replace("#F5F5F5", "#FFFFFF") + dot.replace("#D71920", "#FFFFFF") + "</vector>\n");
        Files.writeString(root.resolve("artwork/app-icon.svg"), svg);
        ImageIO.write(preview, "png", root.resolve("dist/Notify-Glyph-icon.png").toFile());
    }
}
