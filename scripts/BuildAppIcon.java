import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;

public final class BuildAppIcon {
    private static final double SOURCE_X = 73, SOURCE_Y = 37, DOT_RADIUS = 5.25;
    private static final double STROKE = 3.8;
    private static final String[] PATHS = {
            "M63,37H37Q30,37 30,44V64Q30,71 37,71H69Q76,71 76,64V47",
            "M40,49H59", "M40,59H53"
    };

    private static String circle(double x, double y, double radius) {
        return String.format(Locale.ROOT, "M%.2f,%.2f a%.2f,%.2f 0,1 0,%.2f,0 a%.2f,%.2f 0,1 0,%.2f,0",
                x - radius, y, radius, radius, radius * 2, radius, radius, -radius * 2);
    }

    private static Path2D card() {
        Path2D path = new Path2D.Double();
        path.moveTo(63, 37); path.lineTo(37, 37); path.quadTo(30, 37, 30, 44);
        path.lineTo(30, 64); path.quadTo(30, 71, 37, 71); path.lineTo(69, 71);
        path.quadTo(76, 71, 76, 64); path.lineTo(76, 47);
        return path;
    }

    public static void main(String[] arguments) throws Exception {
        Path root = Path.of(arguments.length > 0 ? arguments[0] : ".");
        Path drawables = root.resolve("app/src/main/res/drawable");
        Files.createDirectories(drawables);
        Files.createDirectories(root.resolve("artwork"));
        Files.createDirectories(root.resolve("dist"));
        StringBuilder svg = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 108 108\"><rect width=\"108\" height=\"108\" rx=\"26\" fill=\"#080808\"/>");
        String header = "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\" android:width=\"108dp\" android:height=\"108dp\" android:viewportWidth=\"108\" android:viewportHeight=\"108\">\n";
        StringBuilder body = new StringBuilder();
        BufferedImage preview = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = preview.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(512.0 / 108, 512.0 / 108);
        g.setColor(Color.decode("#080808"));
        g.fillRoundRect(0, 0, 108, 108, 52, 52);
        g.setColor(Color.decode("#F5F5F5"));
        g.setStroke(new BasicStroke((float) STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (String path : PATHS) {
            svg.append("<path d=\"").append(path).append("\" fill=\"none\" stroke=\"#F5F5F5\" stroke-width=\"3.8\" stroke-linecap=\"round\"/>");
            body.append("    <path android:fillColor=\"#00000000\" android:strokeColor=\"#F5F5F5\" android:strokeWidth=\"3.8\" android:strokeLineCap=\"round\" android:pathData=\"").append(path).append("\" />\n");
        }
        g.draw(card());
        g.draw(new Line2D.Double(40, 49, 59, 49));
        g.draw(new Line2D.Double(40, 59, 53, 59));
        String dot = "    <path android:fillColor=\"#D71920\" android:pathData=\"" + circle(SOURCE_X, SOURCE_Y, DOT_RADIUS) + "\" />\n";
        svg.append("<circle cx=\"73\" cy=\"37\" r=\"5.25\" fill=\"#D71920\"/></svg>\n");
        g.setColor(Color.decode("#D71920"));
        g.fill(new Ellipse2D.Double(SOURCE_X - DOT_RADIUS, SOURCE_Y - DOT_RADIUS, DOT_RADIUS * 2, DOT_RADIUS * 2));
        g.dispose();
        Files.writeString(drawables.resolve("ic_ready_foreground.xml"), header + body + dot + "</vector>\n");
        Files.writeString(drawables.resolve("ic_ready_monochrome.xml"), header
                + body.toString().replace("#F5F5F5", "#FFFFFF") + dot.replace("#D71920", "#FFFFFF") + "</vector>\n");
        Files.writeString(root.resolve("artwork/app-icon.svg"), svg);
        ImageIO.write(preview, "png", root.resolve("dist/Notify-Glyph-icon.png").toFile());
    }
}
