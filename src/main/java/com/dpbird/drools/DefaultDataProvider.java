package com.dpbird.drools;

import com.dpbird.drools.test.template.StatusRule;
import org.drools.template.DataProvider;

import java.util.Iterator;
import java.util.List;

public class DefaultDataProvider implements DataProvider {
    private Iterator<DefaultWorkFlowRule> iterator;

    public DefaultDataProvider(List<DefaultWorkFlowRule> rows) {
        this.iterator = rows.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public String[] next() {
        DefaultWorkFlowRule nextRule = iterator.next();
        String[] row = new String[]{
                nextRule.getWorkFlowStatusId(),
                nextRule.getActivityName(),
                nextRule.getActivityStatusId(),
                nextRule.getNextActivityName(),
                nextRule.getNextWorkFlowStatusId(),
                nextRule.getNextActivityAssignee(),
                nextRule.getWaitLock()
        };
        return row;
    }
}
