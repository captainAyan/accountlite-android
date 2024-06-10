package com.github.captainayan.accountlite.utility.statement;

import android.content.Context;

import androidx.preference.PreferenceManager;

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LedgerAccountHTMLStatement {
    private long toDate, fromDate;
    private Ledger ledger;
    private ArrayList<Journal> journalList;

    private String currencyFormat, currencySymbol, currencySymbolPosition, dateFormat, dateSeparator;
    private int openingBalance, closingBalance;

    public LedgerAccountHTMLStatement() {}


    public LedgerAccountHTMLStatement setDateRange(long toDate, long fromDate) {
        this.toDate = toDate;
        this.fromDate = fromDate;
        return this;
    }

    public LedgerAccountHTMLStatement setJournalList(ArrayList<Journal> journalList) {
        this.journalList = journalList;
        return this;
    }

    public LedgerAccountHTMLStatement setLedger(Ledger ledger) {
        this.ledger = ledger;
        return this;
    }

    public LedgerAccountHTMLStatement setOpeningBalance(int openingBalance) {
        this.openingBalance = openingBalance;
        return this;
    }

    public LedgerAccountHTMLStatement setClosingBalance(int closingBalance) {
        this.closingBalance = closingBalance;
        return this;
    }

    private String getHTMLFromLedgerPosing(Journal j) {
        String date = StringUtility.dateFormat(j.getTimestamp(), dateFormat, dateSeparator);
        String amount = StringUtility.amountFormat(j.getAmount(), currencyFormat, currencySymbol, currencySymbolPosition);
        String narration = StringUtility.narrationFormat(j.getNarration());

        // true, if the ledger (the ledger we are viewing) is debited in the entry
        boolean ledgerAccountIsDebitedInPosting = j.getDebitLedger().getId() == ledger.getId();

        String ledgerName = (ledgerAccountIsDebitedInPosting ? "To. " : "By. ") +
                StringUtility.accountNameFormat(
                        ledgerAccountIsDebitedInPosting ?
                                        j.getCreditLedger().getName() :
                                        j.getDebitLedger().getName());

        String html = String.format("<tr>" +
                "<td>%s</td>" +
                "<td><p>%s<br/>%s</p></td>" +
                "<td>%s</td>" +
                "<td class='amt-cell'>%s</td>" +
                "<td class='amt-cell'>%s</td>" +
                "</tr>",
                date, ledgerName, narration, j.getId(),
                (ledgerAccountIsDebitedInPosting?amount:""), (ledgerAccountIsDebitedInPosting?"":amount));

        return html;
    }

    public String build(Context ctx) {
        currencyFormat = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.currency_format_pref_key),
                ctx.getResources().getString(R.string.currency_format_default_value));

        currencySymbol = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.currency_symbol_pref_key),
                ctx.getResources().getString(R.string.currency_symbol_default_value));

        currencySymbolPosition = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.currency_symbol_position_pref_key),
                ctx.getResources().getString(R.string.currency_symbol_position_default_value));

        dateFormat = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.date_format_pref_key),
                ctx.getResources().getString(R.string.date_format_default_value));

        dateSeparator = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.date_separator_pref_key),
                ctx.getResources().getString(R.string.date_separator_default_value));

        if (ledger.getType() == Ledger.Type.LIABILITY
                || ledger.getType() == Ledger.Type.EQUITY
                || ledger.getType() == Ledger.Type.REVENUE) {
            this.openingBalance *= -1;
            this.closingBalance *= -1;
        }

        String toDateFormatted = StringUtility.dateFormat(toDate, dateFormat, dateSeparator);
        String fromDateFormatted = StringUtility.dateFormat(fromDate, dateFormat, dateSeparator);

        String startHtml = String.format("<!DOCTYPE html><html><head>" +
                "<meta charset='UTF-8' /><meta name='viewport' content='width=device-width, initial-scale=1.0' />" +
                "<title>Ledger</title><style>" +
                "*{margin:0; padding:0;}" +
                "body{font-family:consolas,sans-serif;margin:20px;}" +
                "table{width:100%%;border-collapse:collapse;margin-bottom:20px;}" +
                "th,td{border:1px solid #ddd;padding:4px;text-align:left;vertical-align:top}" +
                "th{text-align:center;background-color:#f2f2f2;}" +
                ".amt-cell{text-align:right;}" +
                "</style></head>" +
                "<body><div>" +
                "<h2>%s</h2>" +
                "<h3>%s</h3>" +
                "<p>In the books of <b>%s</b><br/>From <b>%s</b> to <b>%s</b></p>" +
                "<p><br />" +
                "Opening Balance <b>%s</b> <br />" +
                "Closing Balance <b>%s</b> <br /><br />" +
                "</p>" +
                "<table><thead>" +
                "<tr><th>Date</th><th>Particulars</th><th>J.F.</th><th>Debit</th><th>Credit</th></tr></thead>" +
                "<tbody>",
                StringUtility.accountNameFormat(ledger.getName()),
                ctx.getResources().getStringArray(R.array.ledger_types)[ledger.getType()],
                PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getString(ctx.getResources().getString(R.string.user_name_pref_key),""),
                fromDateFormatted,
                toDateFormatted,
                StringUtility.amountFormat(openingBalance, currencyFormat, currencySymbol, currencySymbolPosition),
                StringUtility.amountFormat(closingBalance, currencyFormat, currencySymbol, currencySymbolPosition)
        );

        String endHtml = "</tbody></table></div>" +
                "<p><br/>Generated using <a href='https://captainayan.github.io/accountlite/'>Accountlite</a></p>" +
                "</body></html>";

        StringBuilder html = new StringBuilder(startHtml);
        for (Journal j :journalList) html.append(getHTMLFromLedgerPosing(j));
        html.append(endHtml);

        return html.toString();
    }
}
