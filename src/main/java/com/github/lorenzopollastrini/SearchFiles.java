package com.github.lorenzopollastrini;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SearchFiles {
    public static void main(String[] args) throws Exception {
        String usage = "Utilizzo: java com.github.lorenzopollastrini.SearchFiles" +
                " [-index INDEX_PATH] -query QUERY\n\n" +
                "Questo comando interroga l'indice Lucene contenuto in INDEX_PATH con la query specificata.";

        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }

        String indexPathString = "target/index";
        StringBuilder queryString = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-index":
                    indexPathString = args[++i];
                    break;
                case "-query":
                    queryString = new StringBuilder(args[++i]);
                    while (i < args.length - 1) {
                        queryString.append(" ").append(args[++i]);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Parametro " + args[i] + " sconosciuto");
            }
        }

        Path indexPath = Paths.get(indexPathString);

        Analyzer analyzer = new EnglishAnalyzer(CharArraySet.EMPTY_SET);

        QueryParser parser = new QueryParser("contents", analyzer);

        Query query = parser.parse(String.valueOf(queryString));

        try (Directory indexDirectory = FSDirectory.open(indexPath)) {
            try (IndexReader reader = DirectoryReader.open(indexDirectory)) {
                IndexSearcher searcher = new IndexSearcher(reader);

                SearchFiles searchFiles = new SearchFiles();
                searchFiles.runQuery(searcher, query);
            }
        }
    }

    private void runQuery(IndexSearcher searcher, Query query) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        StoredFields storedFields = searcher.storedFields();
        for (ScoreDoc hit : hits.scoreDocs) {
            Document doc = storedFields.document(hit.doc);
            System.out.println("Documento " + hit.doc + ": " +
                    doc.get("title") + " (" + hit.score + ")");
        }
    }
}
