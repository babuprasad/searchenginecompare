

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class XMLParser {

	public static Collection<QueryTopic> parseQueryTopic(File queryTopicFile) {
		Collection<QueryTopic> queryTopics = null;
		try {
			// Parse XML File
			queryTopics =  new ArrayList<QueryTopic>();
			//System.out.println("Start XML Parsing. !");
			InputStream in = new FileInputStream(queryTopicFile);
			XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(in);
			
			QueryTopic queryTopic = null;
			String elementName = "";
			String elementText = "";
			while(xmlStreamReader.hasNext())
			{
				int eventCode = xmlStreamReader.next();				
				switch (eventCode) 
				{	
					case XMLStreamReader.START_ELEMENT:
						 elementName = xmlStreamReader.getName().getLocalPart();
						 elementText = "";
						if(elementName.compareTo("top") == 0)
							queryTopic = new QueryTopic();
						break;
						
					case XMLStreamReader.CHARACTERS:
						elementText += xmlStreamReader.getText();
						elementText = elementText
								.replaceAll("\n", " ")
								.replaceAll("/"," ")
								.replaceAll("\\?","\\\\?")
								.replaceAll("--", "-");
								
						switch (elementName) 
						{
							case "num":								
								queryTopic.setmQueryNo(elementText.replaceAll("Number:", "").trim());
								break;
							case "title":
								queryTopic.setmTitle(elementText.trim());										
								break;
							case "desc":
								queryTopic.setmDescription(elementText.replaceAll("Description:", "").trim());										
								break;
							case "narr":
								queryTopic.setmNarrative(elementText.replaceAll("Narrative:", "").trim());										
								break;
							default:
								break;
						}
						break;
						
					case XMLStreamReader.END_ELEMENT:
						if(xmlStreamReader.getLocalName().compareTo("top") == 0)
							queryTopics.add(queryTopic);
						break;
	
					default:
						break;
				}
			}
			
			
			//System.out.println("End XML Parsing. Query Collection Length :"+queryTopics.size());
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (XMLStreamException e) {
			
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			
			e.printStackTrace();
		}	
		
		return queryTopics;
	}
	
	
	public static void writeQueryFile(Collection<QueryTopic> queryTopics)
	{
		try {
			if(queryTopics != null)
			{				
				File file = new File("test-data/title-queries_exp.301-450");
				QPType qpType = QPType.POS;
				if(file.exists());
					file.delete();
				file.createNewFile();
				Writer out = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(out);
				String titleWt = "0.7",	descWt = "0.5",	narrWt = "0.3";			
				String query = "";
				MaxentTagger tagger = new MaxentTagger("C:\\Users\\Patronus\\Desktop\\Master's\\Projects\\IR\\IRProject 2\\stanford-postagger-2013-06-20\\models\\wsj-0-18-bidirectional-nodistsim.tagger");
				for (QueryTopic queryTopic : queryTopics)	{
					switch (qpType) {
						case TRIALERROR:
							query =  queryTopic.getmQueryNo() + " " 
							 		+ "(" + queryTopic.getmTitle() + ")^"+ titleWt 
							 		+ "(" + queryTopic.getmDescription() + ")^" + descWt  
							 		+ "(" + queryTopic.getmNarrative() + ")^"+ narrWt;
							break;							
						case POS:
							query =  getPOSWeightedQuery(tagger, queryTopic);
							break;							
						case ML:
							query = getMLWeightedQuery(queryTopic);
							
							break;		
						default:
							break;
					}
					 bw.write(query);
					 bw.write("\n");					 
				}
				bw.flush();
				bw.close();				
				out.close();				
			}
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}


	private static String getMLWeightedQuery(QueryTopic queryTopic) {

		//
		
		return null;
	}


	private static String getPOSWeightedQuery(MaxentTagger tagger, QueryTopic queryTopic) {

		String queryTopicString = queryTopic.getAllQueryTerms()
								.replaceAll("\\.", "")
								.replaceAll(",", "")
								.replaceAll("\\(", "")
								.replaceAll("\\)", "")
								.replaceAll("\\$", "")
								.replaceAll("\\*", "")
								.replaceAll("%", "")
								.replaceAll("\\^", "")
								.replaceAll("&", "")
								.replaceAll("!", "")
								.replaceAll("~", "")
								.replaceAll("@", "")
								.replaceAll("#", "")
								.replaceAll("\\?", "")
								.replaceAll("\\\\", "")
								.replaceAll("/", "")
								.replaceAll(":", "")
								.replaceAll(":", "")
								.replaceAll("<", "")
								.replaceAll(">", "")
								.replaceAll("\\|", "")
								.replaceAll("\\{", "")
								.replaceAll("\\}", "")
								.replaceAll("\\[", "")
								.replaceAll("\\]", "")
								.replaceAll("\"", "")
								.replaceAll("\'", "")
								.replaceAll("-", "")
								.replaceAll("=", "")
								.replaceAll("\\+", "")
								.replaceAll("`", "")
								;
		
	    String  taggedStr = tagger.tagString(queryTopicString);
	    
	    String[] queryTerms = taggedStr.split(" ");
	    
	    String weightedQuery = queryTopic.getmQueryNo() + " ";
	    
	    for (String term : queryTerms) 
	    {
	    	if(!term.trim().isEmpty())
	    	{
				String[] termArr = term.split("_");
				weightedQuery += "(" + termArr[0]; 
				switch (termArr[1]) 
				{
					case "NN":
					case "NNP":
					case "NNPS":
					case "NNS":
						weightedQuery += ")^" + "0.9";
						break;
						
					case "VB":
					case "VBG":
					case "VBD":
					case "VBN":
					case "VBP":
					case "VBZ":
						weightedQuery += ")^" + "0.5";
						break;
					
					case "JJ":
					case "JJR":
					case "JJS":
						weightedQuery += ")^" + "0.7";
						break;					
						
					case "RB":
					case "RBR":
					case "RBS":
						weightedQuery += ")^" + "0.3";
						break;
						
						
					default:
						weightedQuery += ")^" + "0.1";
						break;
						
				}
	    	}
		}
		
		return weightedQuery;
	}


}

class QueryTopic 
{

	private String mQueryNo = "";
	
	private String mTitle = "";
	
	private String mDescription = "";
	
	private String mNarrative = "";

	public String getmQueryNo() {
		return mQueryNo;
	}

	public void setmQueryNo(String mQueryNo) {
		this.mQueryNo = mQueryNo;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getmNarrative() {
		return mNarrative;
	}

	public void setmNarrative(String mNarrative) {
		this.mNarrative = mNarrative;
	}
	
	public String getAllQueryTerms()
	{
		return mTitle + " " + mDescription + " " + mNarrative;
	}
	

}

enum QPType
{
	TRIALERROR,
	POS,
	ML
}