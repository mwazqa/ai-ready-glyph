import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class BuildMessagingSymbols {
    private static final int SCALE = 12;
    private static final String[] NAMES = {"Discord", "Messenger", "WhatsApp", "SMS"};

    public static void main(String[] args) throws Exception {
        StringBuilder java = new StringBuilder("package pl.glyphtoy.aiready;\n\nfinal class MessagingSymbols {\n    private static final String[][] SYMBOLS = {\n");
        StringBuilder svg = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"880\" height=\"260\" viewBox=\"0 0 880 260\"><rect width=\"880\" height=\"260\" fill=\"#080808\"/>");
        BufferedImage preview = new BufferedImage(880, 260, BufferedImage.TYPE_INT_RGB);
        Graphics2D canvas = preview.createGraphics();
        canvas.setColor(new Color(8, 8, 8));
        canvas.fillRect(0, 0, 880, 260);
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int symbol = 0; symbol < NAMES.length; symbol++) {
            BufferedImage image = symbol(symbol);
            java.append("            {\n");
            for (int y = 0; y < 25; y++) {
                java.append("                    \"");
                for (int x = 0; x < 25; x++) {
                    int sum = 0;
                    for (int sy = 0; sy < SCALE; sy++) for (int sx = 0; sx < SCALE; sx++)
                        sum += (image.getRGB(x * SCALE + sx, y * SCALE + sy) >> 16) & 255;
                    int value = Math.round(sum / (SCALE * SCALE * 17f));
                    if (Math.hypot(x - 12, y - 12) > 12.5) value = 0;
                    java.append(Character.forDigit(value, 16));
                    if (Math.hypot(x - 12, y - 12) <= 12.5) {
                        int light = value == 0 ? 30 : value * 15;
                        int cx = symbol * 220 + 26 + x * 7, cy = 24 + y * 7;
                        canvas.setColor(new Color(light, light, light));
                        canvas.fill(new Ellipse2D.Double(cx - 2.1, cy - 2.1, 4.2, 4.2));
                        svg.append("<circle cx=\"").append(cx).append("\" cy=\"").append(cy)
                                .append("\" r=\"2.1\" fill=\"rgb(").append(light).append(',').append(light).append(',').append(light).append(")\"/>");
                    }
                }
                java.append(y == 24 ? "\"\n" : "\",\n");
            }
            java.append(symbol == 3 ? "            }\n" : "            },\n");
            canvas.setColor(new Color(245, 245, 245));
            canvas.setFont(new Font("SansSerif", Font.PLAIN, 18));
            canvas.drawString(NAMES[symbol], symbol * 220 + 26, 230);
            svg.append("<text x=\"").append(symbol * 220 + 26).append("\" y=\"230\" fill=\"#f5f5f5\" font-family=\"sans-serif\" font-size=\"18\">").append(NAMES[symbol]).append("</text>");
        }
        java.append("    };\n\n    static double coverage(AnimationStyle style, int x, int y) {\n        return Character.digit(SYMBOLS[style.ordinal() - AnimationStyle.DISCORD.ordinal()][y].charAt(x), 16) / 15.0;\n    }\n}\n");
        svg.append("</svg>\n");
        canvas.dispose();
        Files.createDirectories(Path.of("dist"));
        Files.writeString(Path.of("app/src/main/java/pl/glyphtoy/aiready/MessagingSymbols.java"), java);
        Files.writeString(Path.of("artwork/messaging-symbols.svg"), svg);
        ImageIO.write(preview, "png", Path.of("dist/Ready-Glyph-messaging-symbols.png").toFile());
        System.out.println("Generated four messaging symbols, SVG and PNG preview.");
    }

    private static BufferedImage symbol(int index) {
        BufferedImage image = new BufferedImage(25 * SCALE, 25 * SCALE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.scale(SCALE, SCALE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D p = new Path2D.Double();
        if (index == 0) {
            p.moveTo(6.6, 6.3); p.lineTo(9.6, 5.4); p.lineTo(10.2, 6.6);
            p.quadTo(12, 6.1, 14.2, 6.6); p.lineTo(14.8, 5.4); p.lineTo(18, 6.3);
            p.curveTo(20, 9.4, 22, 15.5, 20, 18); p.lineTo(16.5, 19.5); p.lineTo(15.4, 17.7);
            p.quadTo(12, 18.5, 8.6, 17.7); p.lineTo(7.5, 19.5); p.lineTo(4, 18);
            p.curveTo(2, 15, 4.5, 9, 6.6, 6.3); p.closePath(); g.fill(p);
            g.setColor(Color.BLACK);
            g.fill(new Ellipse2D.Double(7.1, 10.4, 3.1, 4));
            g.fill(new Ellipse2D.Double(14.6, 10.4, 3.1, 4));
        } else if (index == 1) {
            g.fill(new Ellipse2D.Double(2.7, 2.5, 19.6, 18.9));
            g.fill(polygon(5, 17, 5, 22.5, 11, 19));
            g.setColor(Color.BLACK);
            g.fill(polygon(5.8, 14.6, 11, 8.1, 14, 11.1, 19.3, 7.9, 14, 14.9, 11, 11.9));
        } else if (index == 2) {
            g.draw(new Ellipse2D.Double(3.2, 2.7, 18.6, 18.6));
            g.setColor(Color.BLACK); g.fill(polygon(4.1, 16, 9.3, 20, 3.3, 22.7));
            g.setColor(Color.WHITE);
            p.moveTo(4.9, 16.8); p.lineTo(3.5, 22); p.lineTo(8.9, 20.4); g.draw(p);
            p.reset(); p.moveTo(8, 6.6);
            p.curveTo(7, 6.7, 6.8, 8.2, 7.1, 9.6); p.curveTo(8.3, 13.8, 11.2, 16.1, 15, 17);
            p.curveTo(16.6, 17.3, 18, 16, 17.8, 14.8); p.lineTo(14.6, 13.3); p.lineTo(13.5, 14.4);
            p.curveTo(11.6, 13.8, 10.1, 12.2, 9.5, 10.4); p.lineTo(10.6, 9.3); p.lineTo(9.2, 6.8);
            p.closePath(); g.fill(p);
        } else {
            p.moveTo(7, 4.5); p.lineTo(18, 4.5); p.quadTo(22, 4.5, 22, 8.5);
            p.lineTo(22, 14.5); p.quadTo(22, 18.5, 18, 18.5); p.lineTo(10, 18.5);
            p.lineTo(5, 22); p.lineTo(5, 18); p.quadTo(2.5, 17, 2.5, 14);
            p.lineTo(2.5, 9); p.quadTo(2.5, 4.5, 7, 4.5); p.closePath(); g.draw(p);
            for (int x : new int[]{8, 12, 16}) g.fill(new Ellipse2D.Double(x - 1, 10.5, 2, 2));
        }
        g.dispose();
        return image;
    }

    private static Path2D polygon(double... points) {
        Path2D p = new Path2D.Double();
        p.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) p.lineTo(points[i], points[i + 1]);
        p.closePath();
        return p;
    }
}
