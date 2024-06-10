package com.github.captainayan.accountlite.utility.statement;


import android.content.Context;

import androidx.preference.PreferenceManager;

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.utility.StringUtility;

import java.util.ArrayList;

public class JournalEntriesHTMLStatement {
    private long toDate, fromDate;
    private ArrayList<Journal> journalList;

    private String currencyFormat, currencySymbol, currencySymbolPosition, dateFormat, dateSeparator;

    public JournalEntriesHTMLStatement() {}

    public JournalEntriesHTMLStatement setDateRange(long toDate, long fromDate) {
        this.toDate = toDate;
        this.fromDate = fromDate;
        return this;
    }

    public JournalEntriesHTMLStatement setJournalList(ArrayList<Journal> journalList) {
        this.journalList = journalList;
        return this;
    }

    private String getHTMLFromJournal(Journal j) {
        String date = StringUtility.dateFormat(j.getTimestamp(), dateFormat, dateSeparator);
        String debit = StringUtility.accountNameFormat(j.getDebitLedger().getName());
        String credit = StringUtility.accountNameFormat(j.getCreditLedger().getName());
        String amount = StringUtility.amountFormat(j.getAmount(), currencyFormat, currencySymbol, currencySymbolPosition);
        String narration = StringUtility.narrationFormat(j.getNarration());

        String html = String.format("<tr>" +
                "<td>%s</td>" +
                "<td><p>%s ....Dr.<br/>To. %s<br/>%s</p></td>" +
                "<td>%s</td>" +
                "<td class='amt-cell'>%s</td>" +
                "<td class='amt-cell'><p><br/>%s</p></td>" +
                "</tr>", date, debit, credit, narration, j.getId(), amount, amount);
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

        String toDateFormatted = StringUtility.dateFormat(toDate, dateFormat, dateSeparator);
        String fromDateFormatted = StringUtility.dateFormat(fromDate, dateFormat, dateSeparator);


        String startHtml = String.format("<!DOCTYPE html><html>" +
                "<head>" +
                "<meta charset='UTF-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0' />" +
                "<title>Journal Entry Book</title>" +
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
                "<h2>Journal Entries</h2>" +
                "<p>In the books of <b>%s</b><br/>From <b>%s</b> to <b>%s</b></br></br></p>" +
                "<table>" +
                "<thead><tr><th>Date</th><th>Particulars</th><th>Folio</th><th>Debit</th><th>Credit</th></tr></thead>" +
                "<tbody>",
                PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getString(ctx.getResources().getString(R.string.user_name_pref_key),""),
                fromDateFormatted,
                toDateFormatted);

        String endHtml = "</tbody></table></div>" +
                "<p><br/>Generated using <a href='https://captainayan.github.io/accountlite/'>Accountlite</a></p>" +
                "</body></html>";

        StringBuilder html = new StringBuilder(startHtml);
        for (Journal j :journalList) {
            html.append(getHTMLFromJournal(j));
        }
        html.append(endHtml);

        return html.toString();
    }

}