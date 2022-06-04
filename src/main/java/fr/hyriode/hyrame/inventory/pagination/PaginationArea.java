package fr.hyriode.hyrame.inventory.pagination;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 23:27
 *
 * This class represents an area in a GUI.<br>
 * This area will be used as a board to place items and stuffs.
 */
public class PaginationArea {

    /** The start slot of the area */
    private int start;
    /** The end slot of an area */
    private int end;

    /**
     * Constructor of a {@linkplain PaginationArea pagination area} object
     *
     * @param start The start slot
     * @param end The end slot
     */
    public PaginationArea(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get the start slot of the area
     *
     * @return A slot in an inventory
     */
    public int getStart() {
        return this.start;
    }

    /**
     * Set the start slot of the area
     *
     * @param start A slot
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * Get the end slot of the area
     *
     * @return A slot in an inventory
     */
    public int getEnd() {
        return this.end;
    }

    /**
     * Set the end slot of the area
     *
     * @param end A slot
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * Get the size of the area
     *
     * @return A size
     */
    public int size() {
        return this.start < this.end ? this.end - this.start : this.start - this.end;
    }

}
