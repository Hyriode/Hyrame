package fr.hyriode.tools.tab;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class Tab {

    /** Lines on the top */
    protected Map<Integer, String> headerLines;

    /** Lines on the bottom */
    protected Map<Integer, String> footerLines;

    /**
     * Constructor of {@link Tab}
     */
    public Tab() {
        this.headerLines = new HashMap<>();
        this.footerLines = new HashMap<>();
    }

    /**
     * Send the tab to a given player
     *
     * @param player - Player
     */
    public void send(Player player) {
        final String header = this.getFormattedLines(new ArrayList<>(this.getSortedMap(this.headerLines).values()));
        final String footer = this.getFormattedLines(new ArrayList<>(this.getSortedMap(this.footerLines).values()));

        TabTitle.setTabTitle(player, header, footer);
    }

    /**
     * Sort a map by its key and value
     *
     * @param map - Map to sort
     * @return - Sorted map
     */
    private Map<Integer, String> getSortedMap(Map<Integer, String> map) {
        final LinkedHashMap<Integer, String> sortedMap = new LinkedHashMap<>();

        map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        return sortedMap;
    }

    /**
     * Format lines with space, etc
     *
     * @param lines - Lines to format
     * @return - Formatted line
     */
    private String getFormattedLines(List<String> lines) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < lines.size(); i++) {
            stringBuilder.append(lines.get(i));

            if (i != lines.size() - 1) {
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Set a header line
     *
     * @param line - Line's number
     * @param value - Line's value
     */
    public void setHeaderLine(int line, String value) {
        this.headerLines.put(line, value);
    }

    /**
     * Set a footer line
     *
     * @param line - Line's number
     * @param value - Line's value
     */
    public void setFooterLine(int line, String value) {
        this.footerLines.put(line, value);
    }

    /**
     * Set a blank line in header
     *
     * @param line - Line's number
     */
    public void setBlankHeaderLine(int line) {
        this.headerLines.put(line, "");
    }

    /**
     * Set a blank line in footer
     *
     * @param line - Line's number
     */
    public void setBlankFooterLine(int line) {
        this.footerLines.put(line, "");
    }

    /**
     * Get header lines
     *
     * @return - A collection of string
     */
    public Collection<String> getHeaderLines() {
        return this.headerLines.values();
    }

    /**
     * Set header lines
     *
     * @param headerLines - New lines
     */
    public void setHeaderLines(List<String> headerLines) {
        this.headerLines.clear();

        for (int i = 0; i <= headerLines.size(); i++) {
            this.headerLines.put(i, headerLines.get(i));
        }
    }

    /**
     * Get footer lines
     *
     * @return - A collection of string
     */
    public Collection<String> getFooterLines() {
        return this.footerLines.values();
    }

    /**
     * Set footer lines
     *
     * @param footerLines - New lines
     */
    public void setFooterLines(List<String> footerLines) {
        this.footerLines.clear();

        for (int i = 0; i <= footerLines.size(); i++) {
            this.footerLines.put(i, footerLines.get(i));
        }
    }

}
