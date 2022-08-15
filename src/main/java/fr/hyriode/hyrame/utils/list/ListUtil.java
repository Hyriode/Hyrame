package fr.hyriode.hyrame.utils.list;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 11/01/2022 at 19:33
 */
public class ListUtil {

    /**
     * Get the maximum value that exists in an {@link Integer} list
     *
     * @param list The list with all values
     * @return The maximum value
     */
    public static int getMaxValue(List<Integer> list) {
        int max = 0;
        for (int value : list) {
            if (max == 0) {
                max = value;
            } else {
                if (value > max) {
                    max = value;
                }
            }
        }
        return max;
    }

}
