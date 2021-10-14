package com.github.captainayan.accountlite.utility;

import java.io.IOException;
import java.io.Writer;

public class CSVWriter {

    private Writer writer;
    private int column;
    private StringBuilder escapeBuffer;
    private char[] EOL = new char[] { '\r', '\n' };
    private char separator = ',';

    public CSVWriter(Writer writer) {
        this.writer = writer;
        this.escapeBuffer = new StringBuilder();
    }

    public CSVWriter writeValues(String... values) throws IOException {
        if (column > 0)
            writer.write(separator);

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.write(separator);
            }
            String value = values[i];
            if (value != null) {
                String rewritten = escape(value);
                writer.write(rewritten);
            }
        }

        column += values.length;
        return this;
    }

    public CSVWriter writeValues(Object... values) throws IOException {
        if (column > 0)
            writer.write(separator);

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.write(separator);
            }
            Object value = values[i];
            if (value != null) {
                String rewritten = escape(value.toString());
                writer.write(rewritten);
            }
        }

        column += values.length;
        return this;
    }

    public CSVWriter writeValue(Character value) throws IOException {
        write(value != null ? value.toString() : null);
        return this;
    }

    public CSVWriter writeValue(Number value) throws IOException {
        write(value != null ? value.toString() : null);
        return this;
    }

    public CSVWriter writeValue(String value) throws IOException {
        write(value);
        return this;
    }

    public CSVWriter writeNULL() throws IOException {
        write((String) null);
        return this;
    }

    private void write(String value) throws IOException {
        if (column > 0)
            writer.write(separator);
        column++;
        if (value != null) {
            String rewritten = escape(value);
            writer.write(rewritten);
        }
    }

    public CSVWriter endLine() throws IOException {
        writer.write(EOL);
        column = 0;
        return this;
    }

    private String escape(String value) {
        boolean quote = false;
        escapeBuffer.setLength(0);
        escapeBuffer.append(value);

        for (int i = 0; i < escapeBuffer.length(); i++) {
            char c = escapeBuffer.charAt(i);
            switch (c) {
                case '"':
                    quote = true;
                    escapeBuffer.insert(i, '"');
                    i++;
                    break;

                case '\r':// \r\n
                    if (i < escapeBuffer.length() - 1
                            && escapeBuffer.charAt(i + 1) == '\n') {
                        escapeBuffer.deleteCharAt(i);
                        quote = true;
                    }
                    break;

                case '\n':
                    quote = true;
                    break;

                default:
                    if (c == separator) {
                        quote = true;
                    }
                    break;
            }
        }

        if (quote) {
            escapeBuffer.insert(0, '"');
            escapeBuffer.append('"');
        }
        return escapeBuffer.toString();
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();
    }

}