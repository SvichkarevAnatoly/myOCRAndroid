package ru.myocr.model;

import java.util.ArrayList;
import java.util.List;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import nl.qbusict.cupboard.annotation.Ignore;
import ru.myocr.db.ReceiptContentProvider;
import ru.myocr.model.filter.Filter;

public class UserDiagram extends DbModel<UserDiagram> {

    @Ignore
    private ArrayList<Filter> filters;
    private String name;
    private DiagramType diagramType;

    public UserDiagram() {
    }

    public static List<UserDiagram> getAll() {
        return getProviderCompartment().query(UriHelper.with(ReceiptContentProvider.AUTHORITY)
                .getUri(UserDiagram.class), UserDiagram.class).list();
    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<Filter> filters) {
        this.filters = filters;
    }

    @Override
    protected Class<UserDiagram> getEntityClass() {
        return UserDiagram.class;
    }

    public enum DiagramType {
        LINEAR,
        PIE_CHART
    }
}
