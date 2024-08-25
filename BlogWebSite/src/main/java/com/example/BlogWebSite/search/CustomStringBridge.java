package com.example.BlogWebSite.search;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

public class CustomStringBridge implements FieldBridge {

    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
        if (value != null) {
            luceneOptions.addFieldToDocument(name, value.toString(), document);
        }
    }
}
