import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class BuildSocialSymbols {
    private static final int SCALE = 12;
    private static final String[] NAMES = {"Instagram", "TikTok", "X", "Facebook"};

    public static void main(String[] args) throws Exception {
        StringBuilder java = new StringBuilder("package pl.glyphtoy.aiready;\n\nfinal class SocialSymbols {\n    private static final String[][] SYMBOLS = {\n");
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
        java.append("    };\n\n    static double coverage(AnimationStyle style, int x, int y) {\n        return Character.digit(SYMBOLS[style.ordinal() - AnimationStyle.INSTAGRAM.ordinal()][y].charAt(x), 16) / 15.0;\n    }\n}\n");
        svg.append("</svg>\n");
        canvas.dispose();
        Files.createDirectories(Path.of("dist"));
        Files.writeString(Path.of("app/src/main/java/pl/glyphtoy/aiready/SocialSymbols.java"), java);
        Files.writeString(Path.of("artwork/social-symbols.svg"), svg);
        ImageIO.write(preview, "png", Path.of("dist/Notify-Glyph-social-symbols.png").toFile());
        System.out.println("Generated four social symbols, SVG and PNG preview.");
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
            g.draw(new RoundRectangle2D.Double(3, 3, 18, 18, 7, 7));
            g.draw(new Ellipse2D.Double(7.5, 7.5, 9, 9));
            g.fill(new Ellipse2D.Double(17, 5, 2, 2));
        } else if (index == 1) {
            p.moveTo(12.8, 3); p.lineTo(16, 3);
            p.curveTo(16.2, 5.7, 18, 7.2, 20.8, 7.4); p.lineTo(20.8, 10.5);
            p.curveTo(18.8, 10.5, 17.2, 9.8, 16, 8.8); p.lineTo(16, 17.2);
            p.curveTo(16, 21.3, 12.2, 23.2, 8.7, 21.4);
            p.curveTo(5.3, 19.7, 5.9, 14.6, 9.1, 13.5);
            p.quadTo(10.7, 13, 11.8, 13.5); p.lineTo(11.8, 16.6);
            p.curveTo(9.3, 15.5, 8.4, 18, 10, 18.8);
            p.curveTo(11.4, 19.5, 12.8, 18.6, 12.8, 17.1);
            p.lineTo(12.8, 3); p.closePath(); g.fill(p);
        } else if (index == 2) {
            g.fill(polygon(3.8, 3, 8, 3, 21.2, 21, 17, 21));
            g.setColor(Color.BLACK);
            g.fill(polygon(6.1, 4.4, 7.2, 4.4, 18.9, 19.6, 17.8, 19.6));
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g.draw(new Line2D.Double(20.4, 3.2, 4.2, 20.8));
        } else {
            p.moveTo(10, 22); p.lineTo(14, 22); p.lineTo(14, 13);
            p.lineTo(18, 13); p.lineTo(18.7, 9.5); p.lineTo(14, 9.5); p.lineTo(14, 7.4);
            p.curveTo(14, 5.9, 15, 5.8, 16, 5.8); p.lineTo(19, 5.8); p.lineTo(19, 2.5);
            p.lineTo(16, 2.5); p.curveTo(11.3, 2.5, 10, 5.5, 10, 8.9);
            p.lineTo(10, 9.5); p.lineTo(7, 9.5); p.lineTo(7, 13); p.lineTo(10, 13);
            p.lineTo(10, 22); p.closePath(); g.fill(p);
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
