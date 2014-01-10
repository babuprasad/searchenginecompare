package edu.buffalo.trec.queryexpansion;

//import XMLParser;

import java.io.File;
import java.util.Collection;

public class Runner {

	public static void main(String[] args) {
		if(args == null || args.length == 0)
			System.out.println("Invalid Arguments");
		else
		{
			String filename = args[0];
			File queryFile = new File(filename);
			String expandedQueryFile = queryFile.getParent() + File.pathSeparator + "exp_" +queryFile.getName();
	//		Collection<Query> queries = XMLParser.parseQueryFile(queryFile);
			//Collection<Query> expandedQueries =  Tokenizer.Tokenize(queries);
			//Parser.writeQueryFile(queries);
		}	
	}
	
}
