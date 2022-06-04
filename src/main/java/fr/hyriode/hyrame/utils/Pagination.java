package fr.hyriode.hyrame.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 22:54
 */
public class Pagination<T> extends ArrayList<T> {

    private int pageSize;

    public Pagination(int pageSize, List<T> objects) {
        this.pageSize = pageSize;
        this.addAll(objects);
    }

    @SafeVarargs
    public Pagination(int pageSize, T... objects) {
        this(pageSize, Arrays.asList(objects));
    }

    public Pagination(int pageSize) {
        this(pageSize, new ArrayList<>());
    }

    public int pageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int totalPages() {
        return (int) Math.ceil((double) this.size() / this.pageSize);
    }

    public boolean existsPage(int page) {
        return page >= 0 && page < this.totalPages();
    }

    public List<T> getPageContent(int page) {
        final List<T> content = new ArrayList<>();
        final int min = page * this.pageSize;
        int max = min + this.pageSize;

        if (max > this.size()) {
            max = this.size();
        }

        for (int i = min; i < max; i++) {
            content.add(this.get(i));
        }
        return content;
    }

}
