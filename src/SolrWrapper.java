import java.awt.List;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.ws.RespectBinding;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.lucene.index.*;

public class SolrWrapper {
	
	private SolrServer solrServer;
	private final String solrServerURL = "http://localhost:8983/solr/db/";
	private final String mainIndexField = "tweet_text";
    
	public SolrWrapper () 
	{
		solrServer = new HttpSolrServer(solrServerURL);
	}
	public SolrWrapper (String serverURL) 
	{
		solrServer = new HttpSolrServer(serverURL);
	}
		
	public ArrayList<String> getTweetsByKeyword(String kw)
	{

		SolrQuery q = new SolrQuery();
		q.setQuery(mainIndexField+":\""+kw+"\"");
		q.addField(mainIndexField);
		q.setRows(40000000);		
	    SolrDocumentList results = null ;
		
		try {
			QueryResponse rsp = solrServer.query(q);
			results = rsp.getResults();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
 
		ArrayList<String> tweets = new ArrayList<String>();
		for (SolrDocument doc : results)
		{
			tweets.add(doc.getFieldValue(mainIndexField).toString());
		}
		
		
		
		return tweets  ;
	}
	
	
	/**
	 * If you have a term and want its document frequency
	 * i.e. the number of documents that contain this term
	 * @return
	 */
	public float getDocumentFrequency(String term)
	{
		
		String Query ="{!func}docfreq("+mainIndexField+",'"+term+"')";
		
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.set("fl", "score");
		solrParams.set("defType", "func");
		solrParams.set("rows", 1);
		solrParams.set("q", Query);
		
	    SolrDocumentList results = null ;	
		
		try {
			QueryResponse rsp = solrServer.query(solrParams);
			results = rsp.getResults();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		SolrDocument doc  = results.get(0);
		String idf = doc.getFieldValue("score").toString();
		float x = Float.parseFloat(idf);
		
		return x;
		
	}
	

	/**
	 * If you have a term and want its total term frequency
	 * i.e. the number repetitions all over the documents
	 * @return
	 */
	public float getTermFrequency(String term)
	{
		
		String Query ="{!func}totaltermfreq("+mainIndexField+",'"+term+"')";
		
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.set("fl", "score");
		solrParams.set("defType", "func");
		solrParams.set("rows", 1);
		solrParams.set("q", Query);
		
	    SolrDocumentList results = null ;	
		
		try {
			QueryResponse rsp = solrServer.query(solrParams);
			results = rsp.getResults();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		SolrDocument doc  = results.get(0);
		String idf = doc.getFieldValue("score").toString();
		float x = Float.parseFloat(idf);
		
		return x;
		
	}
	
	/**
	 * If you have a term and want its idf   inverse document frequency
	 * i.e. the number repetitions all over the documents inversed
	 * @return
	 */
	public float getidf(String term)
	{
		
		String Query ="{!func}idf("+mainIndexField+",'"+term+"')";
		
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.set("fl", "score");
		solrParams.set("defType", "func");
		solrParams.set("rows", 1);
		solrParams.set("q", Query);
		
	    SolrDocumentList results = null ;	
		
		try {
			QueryResponse rsp = solrServer.query(solrParams);
			results = rsp.getResults();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		SolrDocument doc  = results.get(0);
		String idf = doc.getFieldValue("score").toString();
		float x = Float.parseFloat(idf);
		
		return x;
		
	}
	
	
	
}
