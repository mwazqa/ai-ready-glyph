import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;

public final class BuildAppIcon {
    private static boolean monogram(int x, int y) {
        return y >= 1 && y <= 7 && (x == 1 || x == 6 || x == Math.round(1 + (y - 1) * 5f / 6));
    }

    private static String circle(double x, double y, double radius) {
        return String.format(Locale.ROOT, "M%.2f,%.2f a%.2f,%.2f 0,1 0,%.2f,0 a%.2f,%.2f 0,1 0,%.2f,0",
                x - radius, y, radius, radius, radius * 2, radius, radius, -radius * 2);
    }

    private static String paths(String data, String color, boolean dim) {
        StringBuilder result = new StringBuilder(), group = new StringBuilder();
        for (String circle : data.split("(?=M)")) {
            if (group.length() + circle.length() > 600) {
                result.append(path(group.toString(), color, dim));
                group.setLength(0);
            }
            group.append(circle);
        }
        if (!group.isEmpty()) result.append(path(group.toString(), color, dim));
        return result.toString();
    }

    private static String path(String data, String color, boolean dim) {
        return "    <path android:fillColor=\"" + color + "\" " + (dim ? "android:fillAlpha=\"0.18\" " : "")
                + "android:pathData=\"" + data + "\" />\n";
    }

    public static void main(String[] arguments) throws Exception {
        Path root = Path.of(arguments.length > 0 ? arguments[0] : ".");
        Path drawables = root.resolve("app/src/main/res/drawable");
        Files.createDirectories(drawables);
        StringBuilder dots = new StringBuilder(), mark = new StringBuilder();
        StringBuilder svg = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 108 108\"><rect width=\"108\" height=\"108\" rx=\"26\" fill=\"#080808\"/>");
        BufferedImage preview = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = preview.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(512.0 / 108, 512.0 / 108);
        g.setColor(new Color(8, 8, 8));
        g.fillRoundRect(0, 0, 108, 108, 52, 52);
        for (int y = 0; y < 9; y++) for (int x = 0; x < 9; x++) {
            if (Math.hypot(x - 4, y - 4) > 4.25) continue;
            double cx = 30 + x * 6, cy = 30 + y * 6;
            boolean lit = monogram(x, y);
            if (Math.hypot(cx - 75, cy - 33) < 7) continue;
            (lit ? mark : dots).append(circle(cx, cy, lit ? 2.5 : 1.65));
            String color = lit ? "#F5F5F5" : "#3A3A3A";
            svg.append(String.format(Locale.ROOT, "<circle cx=\"%.2f\" cy=\"%.2f\" r=\"%.2f\" fill=\"%s\"/>", cx, cy, lit ? 2.5 : 1.65, color));
            g.setColor(Color.decode(color));
            double r = lit ? 2.5 : 1.65;
            g.fill(new java.awt.geom.Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        }
        String signal = circle(75, 33, 4.25);
        svg.append("<circle cx=\"75\" cy=\"33\" r=\"4.25\" fill=\"#D71920\"/></svg>\n");
        g.setColor(new Color(215, 25, 32));
        g.fill(new java.awt.geom.Ellipse2D.Double(70.75, 28.75, 8.5, 8.5));
        g.dispose();
        String header = "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\" android:width=\"108dp\" android:height=\"108dp\" android:viewportWidth=\"108\" android:viewportHeight=\"108\">\n";
        String body = paths(dots.toString(), "#3A3A3A", false)
                + paths(mark.toString(), "#F5F5F5", false) + paths(signal, "#D71920", false);
        Files.writeString(drawables.resolve("ic_ready_foreground.xml"), header + body + "</vector>\n");
        Files.writeString(drawables.resolve("ic_ready_monochrome.xml"), header
                + paths(dots.toString(), "#FFFFFF", true)
                + paths(mark.toString() + signal, "#FFFFFF", false) + "</vector>\n");
        Files.writeString(root.resolve("artwork/app-icon.svg"), svg);
        Files.createDirectories(root.resolve("dist"));
        ImageIO.write(preview, "png", root.resolve("dist/Notify-Glyph-icon.png").toFile());
    }
}
