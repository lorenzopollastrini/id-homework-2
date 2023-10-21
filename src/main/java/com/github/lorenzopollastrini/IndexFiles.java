package com.github.lorenzopollastrini;

import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class IndexFiles {
    public static void main(String[] args) throws Exception {
        String usage = "Utilizzo: java com.github.lorenzopollastrini.IndexFiles" +
                " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n" +
                "Questo comando indicizza i documenti in DOCS_PATH, creando un indice Lucene" +
                "in INDEX_PATH che può essere interrogato con SearchFiles. " +
                "Specificando -update è possibile aggiornare l'indice senza cancellarlo e ricostruirlo.";

        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }

        String indexPathString = "target/index";
        String docsPathString = null;
        boolean create = true;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-index":
                    indexPathString = args[++i];
                    break;
                case "-docs":
                    docsPathString = args[++i];
                    break;
                case "-update":
                    create = false;
                    break;
                default:
                    throw new IllegalArgumentException("Parametro " + args[i] + " sconosciuto");
            }
        }

        if (docsPathString == null) {
            System.err.println(usage);
            System.exit(1);
        }

        final Path indexPath = Paths.get(indexPathString);
        final Path docsPath = Paths.get(docsPathString);

        Analyzer analyzer = new EnglishAnalyzer(CharArraySet.EMPTY_SET);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        if (create) {
            config.setOpenMode(OpenMode.CREATE);
        } else {
            config.setOpenMode(OpenMode.CREATE_OR_APPEND);
        }

        Date start;
        Date end;

        try (Directory indexDirectory = FSDirectory.open(indexPath)) {
            try (IndexWriter writer = new IndexWriter(indexDirectory, config)) {
                IndexFiles indexFiles = new IndexFiles();
                start = new Date();
                indexFiles.indexDocs(writer, docsPath);
                end = new Date();

                writer.commit();
            }

            try (IndexReader reader = DirectoryReader.open(indexDirectory)) {
                System.out.println(
                        "Indicizzati " +
                                reader.numDocs() +
                                " documenti in " +
                                (end.getTime() - start.getTime()) +
                                " ms"
                );
            }
        }
    }

    private void indexDocs(final IndexWriter writer, Path path) throws IOException {
        writer.deleteAll();

        if (Files.isDirectory(path)) {
            Files.walkFileTree(
                    path,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            try {
                                indexDoc(writer, file);
                            } catch (IOException e) {
                                e.printStackTrace(System.err);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        } else {
            indexDoc(writer, path);
        }
    }

    private void indexDoc(IndexWriter writer, Path file) throws IOException {
        Document doc = new Document();

        doc.add(
                new TextField(
                        "title",
                        FilenameUtils.removeExtension(file.getFileName().toString()),
                        Field.Store.YES
                )
        );

        doc.add(
                new TextField(
                        "contents",
                        Files.readString(file, StandardCharsets.UTF_8),
                        Field.Store.NO
                )
        );

        writer.addDocument(doc);
    }
}
