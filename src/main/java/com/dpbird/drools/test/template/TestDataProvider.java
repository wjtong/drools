package com.dpbird.drools.test.template;


import org.drools.template.DataProvider;

import java.util.Iterator;
import java.util.List;

public class TestDataProvider implements DataProvider {
    private Iterator<StatusRule> iterator;

    public TestDataProvider(List<StatusRule> rows) {
        this.iterator = rows.iterator();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public String[] next() {
        StatusRule nextRule = iterator.next();
        String[] row = new String[]{ String.valueOf(nextRule.getConditionStatus()), String.valueOf(nextRule.getResultStatus())};
        return row;
    }
}
