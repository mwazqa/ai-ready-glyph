import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;

public final class BuildAppIcon {
    private static final double SOURCE_X = 43, SOURCE_Y = 54, DOT_RADIUS = 4.25;
    private static final double ANGLE = 53, STROKE = 4.2;
    private static final double[] RADII = {11, 20, 29};

    private static String circle(double x, double y, double radius) {
        return String.format(Locale.ROOT, "M%.2f,%.2f a%.2f,%.2f 0,1 0,%.2f,0 a%.2f,%.2f 0,1 0,%.2f,0",
                x - radius, y, radius, radius, radius * 2, radius, radius, -radius * 2);
    }

    private static String arc(double radius) {
        double angle = Math.toRadians(ANGLE);
        double x = SOURCE_X + radius * Math.cos(angle), offset = radius * Math.sin(angle);
        return String.format(Locale.ROOT, "M%.2f,%.2f A%.2f,%.2f 0,0 1,%.2f,%.2f",
                x, SOURCE_Y - offset, radius, radius, x, SOURCE_Y + offset);
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
        for (double radius : RADII) {
            String path = arc(radius);
            svg.append("<path d=\"").append(path).append("\" fill=\"none\" stroke=\"#F5F5F5\" stroke-width=\"4.2\" stroke-linecap=\"round\"/>");
            body.append("    <path android:fillColor=\"#00000000\" android:strokeColor=\"#F5F5F5\" android:strokeWidth=\"4.2\" android:strokeLineCap=\"round\" android:pathData=\"").append(path).append("\" />\n");
            g.draw(new Arc2D.Double(SOURCE_X - radius, SOURCE_Y - radius, radius * 2, radius * 2, -ANGLE, ANGLE * 2, Arc2D.OPEN));
        }
        String dot = "    <path android:fillColor=\"#D71920\" android:pathData=\"" + circle(SOURCE_X, SOURCE_Y, DOT_RADIUS) + "\" />\n";
        svg.append("<circle cx=\"43\" cy=\"54\" r=\"4.25\" fill=\"#D71920\"/></svg>\n");
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
