package fr.hyriode.hyrame.utils.player;

import fr.hyriode.api.HyriAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Created by AstFaster
 * on 24/07/2022 at 10:24
 */
public class PlayerHeadAPI {

    private static final String REDIS_KEY = "player-heads:";

    public static void savePlayerHead(Player player) {
        HyriAPI.get().getRedisProcessor().processAsync(jedis -> {
            try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                final SkinUtil.SkinImage skin = SkinUtil.getPlayerSkinPart(player, SkinUtil.SkinPart.HEAD_FRONT, 8);

                if (skin == null) {
                    return;
                }

                final BufferedImage image = skin.getImage();

                ImageIO.write(image, "png", outputStream);

                jedis.set(REDIS_KEY + player.getUniqueId().toString(), Base64.getEncoder().encodeToString(outputStream.toByteArray()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static BufferedImage getPlayerHead(Player player) {
        return HyriAPI.get().getRedisProcessor().get(jedis -> {
            final String encodedHead = jedis.get(REDIS_KEY + player.getUniqueId().toString());

            if (encodedHead == null) {
                return null;
            }

            final byte[] decodedHead = Base64.getDecoder().decode(encodedHead);

            try (final InputStream inputStream = new ByteArrayInputStream(decodedHead)) {
                return ImageIO.read(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static ImageMessage createImageMessage(Player player, int size, char imageChar) {
        return new ImageMessage(getPlayerHead(player), size, imageChar);
    }

    public static class ImageMessage {

        private static final Color[] COLORS = new Color[] {
                new Color(0, 0, 0),
                new Color(0, 0, 170),
                new Color(0, 170, 0),
                new Color(0, 170, 170),
                new Color(170, 0, 0),
                new Color(170, 0, 170),
                new Color(255, 170, 0),
                new Color(170, 170, 170),
                new Color(85, 85, 85),
                new Color(85, 85, 255),
                new Color(85, 255, 85),
                new Color(85, 255, 255),
                new Color(255, 85, 85),
                new Color(255, 85, 255),
                new Color(255, 255, 85),
                new Color(255, 255, 255) };

        private final String[] lines;

        public ImageMessage(BufferedImage image, int size, char imageChar) {
            this.lines = this.toImageMessage(this.toChatColorArray(image, size), imageChar);
        }

        public ImageMessage(ChatColor[][] chatColors, char imageChar) {
            this.lines = this.toImageMessage(chatColors, imageChar);
        }

        public ImageMessage(String... imgLines) {
            this.lines = imgLines;
        }

        public ImageMessage appendText(String... text) {
            for (int i = 0; i < this.lines.length; i++) {
                if (text.length > i) {
                    this.lines[i] = this.lines[i] + " " + text[i];
                }
            }
            return this;
        }

        private ChatColor[][] toChatColorArray(BufferedImage image, int size) {
            final double ratio = (double) image.getHeight() / image.getWidth();
            final BufferedImage resized = this.resizeImage(image, (int) (size / ratio), (int) (size / ratio));
            final ChatColor[][] chatImg = new ChatColor[resized.getWidth()][resized.getHeight()];

            for (int x = 0; x < resized.getWidth(); x++) {
                for (int y = 0; y < resized.getHeight(); y++) {
                    final int rgb = resized.getRGB(x, y);
                    final ChatColor closest = this.getClosestChatColor(new Color(rgb, true));

                    chatImg[x][y] = closest;
                }
            }
            return chatImg;
        }

        private String[] toImageMessage(ChatColor[][] colors, char imageChar) {
            final String[] lines = new String[colors[0].length];

            for (int y = 0; y < colors[0].length; y++) {
                final StringBuilder line = new StringBuilder();

                for (ChatColor[] chatColors : colors) {
                    final ChatColor color = chatColors[y];

                    line.append(color != null ? chatColors[y].toString() + imageChar : "");
                }

                lines[y] = line.toString() + ChatColor.RESET;
            }
            return lines;
        }

        private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
            final AffineTransform transform = new AffineTransform();

            transform.scale((double) width / originalImage.getWidth(), (double) height / originalImage.getHeight());

            return new AffineTransformOp(transform, 1).filter(originalImage, null);
        }

        private double getDistance(Color c1, Color c2) {
            final double rmean = (c1.getRed() + c2.getRed()) / 2.0D;
            final double r = (c1.getRed() - c2.getRed());
            final double g = (c1.getGreen() - c2.getGreen());
            final int b = c1.getBlue() - c2.getBlue();
            final double weightR = 2.0D + rmean / 256.0D;
            final double weightG = 4.0D;
            final double weightB = 2.0D + (255.0D - rmean) / 256.0D;

            return weightR * r * r + weightG * g * g + weightB * b * b;
        }

        private boolean areIdentical(Color c1, Color c2) {
            return (Math.abs(c1.getRed() - c2.getRed()) <= 5 &&
                    Math.abs(c1.getGreen() - c2.getGreen()) <= 5 &&
                    Math.abs(c1.getBlue() - c2.getBlue()) <= 5);
        }

        private ChatColor getClosestChatColor(Color color) {
            if (color.getAlpha() < 128) {
                return null;
            }

            for (int i = 0; i < COLORS.length; i++) {
                if (this.areIdentical(COLORS[i], color)) {
                    return ChatColor.values()[i];
                }
            }

            ChatColor result = ChatColor.values()[0];
            double best = -1.0D;
            for (int i = 0; i < COLORS.length; i++) {
                double distance = this.getDistance(color, COLORS[i]);
                if (distance < best || best == -1.0D) {
                    best = distance;
                    result = ChatColor.values()[i];
                }
            }
            return result;
        }

        public String[] getLines() {
            return this.lines;
        }

        public void send(Player player) {
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < this.lines.length; i++) {
                builder.append(this.lines[i]);

                if (i != this.lines.length -1) {
                    builder.append("\n");
                }
            }

            player.sendMessage(builder.toString());
        }

    }

}
