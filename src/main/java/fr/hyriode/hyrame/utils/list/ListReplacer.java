package fr.hyriode.hyrame.utils.list;

import java.util.List;

/**
 * Created by AstFaster
 * on 30/07/2022 at 13:44
 */
public class ListReplacer {

    private final List<String> input;

    public ListReplacer(List<String> input) {
        this.input = input;
    }

    public static ListReplacer replace(List<String> input, String character, String replacer) {
        return new ListReplacer(input).replace(character, replacer);
    }

    public ListReplacer replace(String character, String replacer) {
        this.input.replaceAll(s -> s.replace(character, replacer));
        return this;
    }

    public List<String> list() {
        return this.input;
    }

}
