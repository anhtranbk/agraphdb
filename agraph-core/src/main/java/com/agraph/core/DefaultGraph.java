package com.agraph.core;

import com.agraph.AGraphTransaction;
import com.agraph.core.transaction.TransactionBuilder;

public class DefaultGraph extends TinkerBaseGraph {

    @Override
    protected AGraphTransaction getCurrentTx() {
        return null;
    }

    @Override
    public AGraphTransaction newTransaction() {
        return null;
    }

    @Override
    public TransactionBuilder transactionBuilder() {
        return null;
    }
}
