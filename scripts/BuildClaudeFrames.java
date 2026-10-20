import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.File;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;

public class BuildClaudeFrames {
    static Node find(Node node, String name) {
        if (node.getNodeName().equals(name)) return node;
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            Node found = find(child, name); if (found != null) return found;
        }
        return null;
    }
    static String attr(Node node, String name) { return node.getAttributes().getNamedItem(name).getNodeValue(); }
    public static void main(String[] args) throws Exception {
        java.util.List<String> frames = new ArrayList<>(); java.util.List<Integer> delays = new ArrayList<>();
        try (ImageInputStream input = ImageIO.createImageInputStream(new File(args[0]))) {
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next(); reader.setInput(input);
            BufferedImage canvas = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < reader.getNumImages(true); i++) {
                Node metadata = reader.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0");
                Node descriptor = find(metadata, "ImageDescriptor"), control = find(metadata, "GraphicControlExtension");
                int left = Integer.parseInt(attr(descriptor, "imageLeftPosition"));
                int top = Integer.parseInt(attr(descriptor, "imageTopPosition"));
                delays.add(Integer.parseInt(attr(control, "delayTime")) * 10);
                BufferedImage before = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D copy = before.createGraphics(); copy.drawImage(canvas, 0, 0, null); copy.dispose();
                BufferedImage frame = reader.read(i);
                Graphics2D cg = canvas.createGraphics(); cg.drawImage(frame, left, top, null); cg.dispose();
                BufferedImage white = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < 128; y++) for (int x = 0; x < 128; x++)
                    white.setRGB(x, y, (canvas.getRGB(x, y) & 0xff000000) | 0xffffff);
                BufferedImage sampled = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D sg = sampled.createGraphics();
                sg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                sg.drawImage(white, 8, 8, 184, 184, null); sg.dispose();
                StringBuilder encoded = new StringBuilder();
                for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
                    double sum = 0;
                    for (int dy = 0; dy < 8; dy++) for (int dx = 0; dx < 8; dx++)
                        sum += sampled.getRaster().getSample(x * 8 + dx, y * 8 + dy, 0);
                    encoded.append(Integer.toHexString((int) Math.round(sum / 64 / 255 * 15)));
                }
                frames.add(encoded.toString());
                String disposal = attr(control, "disposalMethod");
                if (disposal.equals("restoreToBackgroundColor")) {
                    Graphics2D clear = canvas.createGraphics(); clear.setComposite(AlphaComposite.Clear);
                    clear.fillRect(left, top, frame.getWidth(), frame.getHeight()); clear.dispose();
                } else if (disposal.equals("restoreToPrevious")) canvas = before;
            }
            reader.dispose();
        }
        StringBuilder source = new StringBuilder("package pl.glyphtoy.aiready;\n\nfinal class ClaudeFrames {\n    static final int[] DELAYS = {");
        for (int i = 0; i < delays.size(); i++) source.append(i == 0 ? "" : ", ").append(delays.get(i));
        source.append("};\n    private static final String[] DATA = {\n");
        for (String frame : frames) source.append("        \"").append(frame).append("\",\n");
        source.append("    };\n    private static final int[][] FRAMES = decode();\n    static int[] at(long time) {\n        int elapsed = (int) (time % 1000);\n        for (int i = 0; i < DELAYS.length; i++) {\n            if (elapsed < DELAYS[i]) return FRAMES[i];\n            elapsed -= DELAYS[i];\n        }\n        return FRAMES[0];\n    }\n    private static int[][] decode() {\n        int[][] frames = new int[DATA.length][625];\n        for (int i = 0; i < DATA.length; i++) for (int p = 0; p < 625; p++)\n            frames[i][p] = Character.digit(DATA[i].charAt(p), 16) * 15;\n        return frames;\n    }\n}\n");
        Files.writeString(Path.of(args[1]), source.toString());
        System.out.println("Frames: " + frames.size() + "; loop duration: " + delays.stream().mapToInt(Integer::intValue).sum() + "ms");
    }
}
