package com.github.captainayan.accountlite.utility.statement;

import android.content.Context;

import androidx.preference.PreferenceManager;

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;

import java.util.ArrayList;

public class TrialBalanceHTMLStatement {
    private ArrayList<Ledger.LedgerWithBalance> ledgerWithBalanceList;
    private int total;
    private String currencyFormat, currencySymbol, currencySymbolPosition, dateFormat, dateSeparator;
    private double asOnDate;

    public TrialBalanceHTMLStatement() {}

    public TrialBalanceHTMLStatement setLedgerWithBalanceList(ArrayList<Ledger.LedgerWithBalance> ledgerWithBalanceList) {
        this.ledgerWithBalanceList = ledgerWithBalanceList;

        this.total = ledgerWithBalanceList.stream().map(l -> {
            if (l.getType() == Ledger.Type.EXPENDITURE || l.getType() == Ledger.Type.ASSET)
                return l.getBalance();
            return 0;
        }).reduce(0, Integer::sum);

        return this;
    }

    public TrialBalanceHTMLStatement setAsOnDate(double asOnDate) {
        this.asOnDate = asOnDate;
        return this;
    }

    private String getHTMLFromLedger(Ledger.LedgerWithBalance l) {
        String accountName = StringUtility.accountNameFormat(l.getName());
        boolean isDebitBalance = l.getType()==Ledger.Type.EXPENDITURE || l.getType()==Ledger.Type.ASSET;

        String balance = "";
        if (isDebitBalance)
            balance = StringUtility.amountFormat(l.getBalance(), currencyFormat, currencySymbol, currencySymbolPosition);
        else
            balance = StringUtility.amountFormat(l.getBalance()*-1, currencyFormat, currencySymbol, currencySymbolPosition);

        String html = String.format("<tr>" +
                "<td>%s</td>" +
                "<td>%s</td>" +
                "<td class='amt-cell'>%s</td>" +
                "<td class='amt-cell'>%s</td>" +
                "</tr>", l.getId(), accountName, isDebitBalance?balance:"", isDebitBalance?"":balance);
        return html;
    }

    // Build method to create an instance of TemplateClass
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

        String asOnDateFormatted = StringUtility.dateFormat(asOnDate, dateFormat, dateSeparator);
        String totalAmountFormatted = StringUtility
                .amountFormat(total, currencyFormat, currencySymbol, currencySymbolPosition);

        String startHtml = String.format("<!DOCTYPE html><html>" +
                        "<head>" +
                        "<meta charset='UTF-8' />" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0' />" +
                        "<title>Trial Balance</title>" +
                        "<style>" +
                        "*{margin:0; padding:0;}" +
                        "body{font-family:consolas,sans-serif;margin:20px;}" +
                        "table{width:100%%;border-collapse:collapse;margin-bottom:20px;}" +
                        "th,td{border:1px solid #ddd;padding:4px;text-align:left;vertical-align:top}" +
                        "th{text-align:center;background-color:#f2f2f2;}" +
                        ".amt-cell{text-align:right;}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div>" +
                        "<h2>Trial Balance</h2>" +
                        "<p>In the books of <b>%s</b><br/>As on date <b>%s</b><br /><br /></p>" +
                        "<table>" +
                        "<thead><tr><th>Id</th><th>Account</th><th>Debit</th><th>Credit</th></tr></thead>" +
                        "<tbody>",
                PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getString(ctx.getResources().getString(R.string.user_name_pref_key),""),
                asOnDateFormatted);

        String endHtml = String.format("</tbody>" +
                "<tfoot style='font-weight: bold'><td></td>" +
                "<td>Total</td><td class='amt-cell'>%s</td><td class='amt-cell'>%s</td>" +
                "</tfoot></table></div>" +
                "<p><br/>Generated using <a href='https://captainayan.github.io/accountlite/'>Accountlite</a></p>" +
                "</body></html>", totalAmountFormatted, totalAmountFormatted);

        StringBuilder html = new StringBuilder(startHtml);
        for (Ledger.LedgerWithBalance l :ledgerWithBalanceList) {
            html.append(getHTMLFromLedger(l));
        }
        html.append(endHtml);

        return html.toString();
    }
}
