import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.StemmerUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/** Simple command-line based search demo. */
public class BatchSearch {

	private BatchSearch() {}

	/** Simple command-line based search demo. */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		String usage =
				"Usage:\tjava BatchSearch [-index dir] [-simfn similarity] [-field f] [-queries file]";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.out.println("Supported similarity functions:\ndefault: DefaultSimilary (tfidf)\n");
			System.exit(0);
		}

		String index = "index";
		String field = "contents";
		String queries = null;
		String simstring = "default";
		
		AnalyzerMode analyzerMode = AnalyzerMode.STANDARD;

		for(int i = 0;i < args.length;i++) {
			if ("-index".equals(args[i])) {
				index = args[i+1];
				i++;
			} else if ("-field".equals(args[i])) {
				field = args[i+1];
				i++;
			} else if ("-queries".equals(args[i])) {
				queries = args[i+1];
				i++;
			} else if ("-simfn".equals(args[i])) {
				simstring = args[i+1];
				i++;
			}
		}

		Similarity simfn = null;
		if ("default".equals(simstring)) {
			simfn = new DefaultSimilarity();
		} else if ("bm25".equals(simstring)) {
			simfn = new BM25Similarity();
		} else if ("dfr".equals(simstring)) {
			simfn = new DFRSimilarity(new BasicModelP(), new AfterEffectL(), new NormalizationH2());
		} else if ("lm".equals(simstring)) {
			simfn = new LMDirichletSimilarity();
		}
		if (simfn == null) {
			System.out.println(usage);
			System.out.println("Supported similarity functions:\ndefault: DefaultSimilary (tfidf)");
			System.out.println("bm25: BM25Similarity (standard parameters)");
			System.out.println("dfr: Divergence from Randomness model (PL2 variant)");
			System.out.println("lm: Language model, Dirichlet smoothing");
			System.exit(0);
		}
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(simfn);
		
		// Standard Analyzer - Default
		Analyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_41);
		SimpleAnalyzer simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_41);
		WhitespaceAnalyzer whitespaceAnalyzer = new WhitespaceAnalyzer(Version.LUCENE_41);
		StopAnalyzer stopAnalyzer = new StopAnalyzer(Version.LUCENE_41);
		
		// Topics to Query conversion
		//File queryTopicFile = new File("test-data/topics.301-450");
		//Collection<QueryTopic> queryTopics = XMLParser.parseQueryTopic(queryTopicFile);
		//XMLParser.writeQueryFile(queryTopics);
		
		BufferedReader in = null;
		if (queries != null) {
			//in = new BufferedReader(new InputStreamReader(new FileInputStream("test-data/title-queries_exp.301-450"), "UTF-8"));
			in = new BufferedReader(new InputStreamReader(new FileInputStream(queries), "UTF-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(new FileInputStream("queries"), "UTF-8"));
		}		
		
		Analyzer analyzer = null;
		if(analyzerMode == AnalyzerMode.STANDARD)
			analyzer = standardAnalyzer;
		else if(analyzerMode == AnalyzerMode.SIMPLE)
			analyzer = simpleAnalyzer;
		else if(analyzerMode == AnalyzerMode.STOPWORDS)
			analyzer = stopAnalyzer;
		else if(analyzerMode == AnalyzerMode.WHITESPACE)
			analyzer = whitespaceAnalyzer;

		QueryParser parser = new QueryParser(Version.LUCENE_41, field, analyzer);

		while (true) {
			String line = in.readLine();

			if (line == null || line.length() == -1) {
				break;
			}

			line = line.trim();
			if (line.length() == 0) {
				break;
			}
			
			String[] pair = line.split(" ", 2);
			//Query query = parser.parse("");
			PhraseQuery query = new PhraseQuery();
			query.add(new Term("contents",pair[1]));
			//Query query = parser.parse(pair[1]);
			
			
			
			//query.setBoost(0.2f);
			
			
			//System.out.println("Query  : " + query.toString());
			
			// weight based on query

			doBatchSearch(in, searcher, pair[0], query, simstring);
		}
		reader.close();
	}

	/**
	 * This function performs a top-1000 search for the query as a basic TREC run.
	 */
	public static void doBatchSearch(BufferedReader in, IndexSearcher searcher, String qid, Query query, String runtag)	 
			throws IOException {

		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 1000);
		ScoreDoc[] hits = results.scoreDocs;
		HashMap<String, String> seen = new HashMap<String, String>(1000);
		int numTotalHits = results.totalHits;
		
		int start = 0;
		int end = Math.min(numTotalHits, 1000);

		for (int i = start; i < end; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String docno = doc.get("docno");
			// There are duplicate document numbers in the FR collection, so only output a given
			// docno once.
			if (seen.containsKey(docno)) {
				continue;
			}
			seen.put(docno, docno);
			System.out.println(qid+" Q0 "+docno+" "+i+" "+hits[i].score+" "+runtag);
		}
	}
}

enum AnalyzerMode
{
	STANDARD,
	SIMPLE,
	STOPWORDS,
	WHITESPACE
}