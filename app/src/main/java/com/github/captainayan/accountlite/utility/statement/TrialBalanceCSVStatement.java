package com.github.captainayan.accountlite.utility.statement;

import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;

import java.util.ArrayList;

public class TrialBalanceCSVStatement {

    private ArrayList<Ledger.LedgerWithBalance> ledgetWithBalanceList;

    public TrialBalanceCSVStatement() {
    }

    public TrialBalanceCSVStatement setLedgerWithBalanceList(ArrayList<Ledger.LedgerWithBalance> ledgerWithBalanceList) {
        this.ledgetWithBalanceList = ledgerWithBalanceList;
        return this;
    }


    public String build() {
        StringBuilder csv = new StringBuilder()
                .append("ID,Account,Debit,Credit\n");

        for (Ledger.LedgerWithBalance l: ledgetWithBalanceList) {
            csv.append(l.getId()).append(",")
                    .append(StringUtility.accountNameFormat(l.getName())).append(",");

            if (l.getType() == Ledger.Type.EXPENDITURE || l.getType() == Ledger.Type.ASSET) {
                csv.append(l.getBalance()).append(",")
                        .append("").append("\n");
            }
            else {
                csv.append("").append(",")
                        .append(l.getBalance()*-1).append("\n");
            }
        }
        return csv.toString();
    }
}
