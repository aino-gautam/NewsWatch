package com.lighthouse.search.server.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


import java.sql.Connection;


import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.apache.lucene.index.IndexWriter;


import org.apache.lucene.store.FSDirectory;


import com.newscenter.server.db.DBHelper;

/**
 * @author mahesh@ensarm.com This class will act as the helper setup index
 *         servlet. And will perform all the operations needed to perform
 *         indexing procedure.
 * */
public class SetupIndexHelper extends DBHelper {

	Logger logger = Logger.getLogger(SetupIndexHelper.class.getName());
	/**
	 * This method creates the directory for all Lighthouse and gives the writer
	 * object and then performs the indexing on that directory
	 * 
	 * @param : Strind indexPath - the path for creating the directory
	 * */
	public void indexSetup(String indexPath) {
		logger.log(Level.INFO,"IN indexSetup()method : In the index setup method...");
		Connection con = getConnection();
		ArrayList<String> dirNames = new ArrayList<String>();
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriter writer = null;
		String dirPath=null;
		String industryName=null;
		try {

			String sql = "select Name from industryenum";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			int i = 0;
			while (rs.next()) {
				dirNames.add(rs.getString("Name"));
				i++;
			}
			rs.close();
			stmt.close();
			
			for (int j = 0; j < dirNames.size(); j++) {
				dirPath = indexPath + dirNames.get(j);
				industryName = dirNames.get(j);
				logger.log(Level.INFO,"IN indexSetup() method : Indexing for : "+industryName);
				logger.log(Level.INFO,"IN indexSetup() method : The setup index path : "+ dirPath);
				// if (industryName.matches("Solar PV")) {
				File indexDir = new File(dirPath);
				try {
					if (indexDir.exists()) {
						logger.log(Level.INFO,
								"IN indexSetup() method : Index directory exist");
						FSDirectory fsdDir = FSDirectory.getDirectory(indexDir,
								false);
						fsdDir.setDisableLocks(true);
						writer = new IndexWriter(fsdDir, analyzer, false);
						/*		System.out.println("Indexing to directory '"
										+ indexDir.getName() + "'...");
								indexDocs(writer, industryName);
								writer.optimize();*/

						writer.close();

					} else {
						FSDirectory fsdDir = FSDirectory.getDirectory(indexDir,
								true);
						fsdDir.setDisableLocks(true);
						writer = new IndexWriter(fsdDir, analyzer, true);
						indexDocs(writer, industryName,dirPath);
						writer.optimize();

						writer.close();

					}
				} catch (Exception e) {
					logger.log(Level.INFO,"IN indexSetup()method : Error : "+e);
					e.printStackTrace();
					if(e instanceof FileNotFoundException)
						performReindexing(dirPath,industryName);
				}

			}
			// }
		} catch (Exception e) {
			logger.log(Level.INFO,"IN indexSetup()method : Error : "+e);

			e.printStackTrace();
		}

		finally {

			try {

				if (writer != null)

				{

					writer.close();

				}

			} catch (IOException ioe) {
				logger.log(Level.INFO,"IN indexSetup()method : Error : "+ioe);
				ioe.printStackTrace();

			}

		}
		ReIndexer reIndexerThread = new ReIndexer();
		reIndexerThread.setDaemon(true);
		reIndexerThread.start();
		logger.log(Level.INFO,"IN indexSetup()method : In the index setup method...  indexing completed");
	}

	private void performReindexing(String dirPath,String industryName) {
		IndexWriter writer = null;
		try{
	
		FSDirectory fsdDir = FSDirectory.getDirectory(dirPath, true);
		fsdDir.setDisableLocks(true);
		writer = new IndexWriter(fsdDir, new StandardAnalyzer(), true);
		indexDocs(writer, industryName,dirPath);
		writer.optimize();

		writer.close();
		}
		catch (Exception e) {
			logger.log(Level.INFO,"IN indexSetup()method : Error : "+e);
			e.printStackTrace();
		}
		finally {
				try {
					if (writer != null)
					{
						writer.close();
					}
				} 
				catch (IOException ioe) {
				logger.log(Level.INFO,"IN indexSetup()method : Error : "+ioe);
				ioe.printStackTrace();
				}

		}
	}

	/**
	 * This method will write the document object in to the the directory for
	 * which the writer is been provided
	 * @param dirPath 
	 * 
	 * @param : 1) IndexWriter object - the reference of the directory in which
	 *        the indexing document is to be wrote 2) String industryName - the
	 *        lighthouse name.
	 * */
	private void indexDocs(IndexWriter writer, String industryName, String dirPath) {
		logger.log(Level.INFO,"IN indexDocs() method : Creating docs and adding it to the indexes method...");
		Connection con = getConnection();
		ArrayList<String> tagItem = new ArrayList<String>();
		String indexingPath = dirPath;
		String indexTag= null;
		String indexContent = null;
		String indexPublishedDate = null;
		String indexRelatedTags= null;
		
		try {
			logger.log(Level.INFO,"IN indexDocs() method : fetching all the tags..");
			String sql = "select Name from tagitem where IndustryId=(select IndustryEnumId from industryenum where Name = '"
					+ industryName + "')";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				tagItem.add(rs.getString("Name"));
			}
			rs.close();
			
			for (int i = 0; i < tagItem.size(); i++) {
				logger.log(Level.INFO,"IN indexDocs() method : Fetching the tagitemid..");
				String query = "Select tagitemid from tagitem where name= '"
						+ tagItem.get(i)
						+ "' and IndustryId=(select IndustryEnumId from industryenum where Name = '"
						+ industryName + "')";
				ResultSet tagItemIds = stmt.executeQuery(query);
				ArrayList<String> tagItemIdList = new ArrayList<String>();
				while (tagItemIds.next()) {
					tagItemIdList.add(tagItemIds.getString("tagitemid"));
				}
				tagItemIds.close();
				
				for (int k = 0; k < tagItemIdList.size(); k++) {
					logger.log(Level.INFO,"IN indexDocs() method : fetching newsitems");
					String query1 = "select newsitemid from newstagitem where tagitemid= "
							+ tagItemIdList.get(k)
							+ " and IndustryEnumId=(select IndustryEnumId from industryenum where Name = '"
							+ industryName + "')";
					ResultSet newsItemIds = stmt.executeQuery(query1);
					ArrayList<String> newsItem = new ArrayList<String>();
					while (newsItemIds.next()) {
						newsItem.add(newsItemIds.getString("newsitemid"));

					}
					tagItemIds.close();
					
					for (int j = 0; j < newsItem.size(); j++) {
						logger.log(Level.INFO,"IN indexDocs() method : actaul indexing to the docs...");
						String query2 = "select newsitemid,Abstract,url,ItemDate,Title,Content,Source,isLocked from newsitem where NewsItemId ="
								+ newsItem.get(j) + " and isReport=0 ";
						ResultSet newsData = stmt.executeQuery(query2);
						while (newsData.next()) {
							Document doc = new Document();
							
							indexTag = tagItem.get(i);
							indexContent = newsData
									.getString("newsitemid")
									+ " $*$# "
									+ newsData.getString("Abstract")
									+ " $*$# "
									+ newsData.getString("url")
									+ " $*$# "
									+ newsData.getString("Title")
									+ " $*$# "
									+ newsData.getString("Content")
									+ " $*$# "
									+ newsData.getString("Source")
									+ " $*$# "
									+ newsData.getString("isLocked");
							indexPublishedDate =  newsData
									.getString("ItemDate");
							
							doc.add(new Field("Tags", indexTag ,
									Field.Store.YES, Field.Index.TOKENIZED));

							doc.add(new Field("Content",indexContent ,
									Field.Store.YES, Field.Index.TOKENIZED));
							doc.add(new Field("ItemDate", indexPublishedDate, Field.Store.YES,
									Field.Index.UN_TOKENIZED));

							String relTagQuery = "SELECT TagItemId from newstagitem where NewsItemId="
									+ newsData.getString("newsitemid")
									+ " and IndustryEnumId=(select IndustryEnumId from industryenum where Name = '"
									+ industryName + "')";
							stmt = con.createStatement();
							ResultSet rsRelatedTag = stmt
									.executeQuery(relTagQuery);
							while (rsRelatedTag.next()) {
								String relTagNameQuery = "select name from tagitem where TagItemId ="
										+ rsRelatedTag.getString("TagItemId");
								stmt = con.createStatement();
								ResultSet rsRelatedTagName = stmt
										.executeQuery(relTagNameQuery);
								while (rsRelatedTagName.next()) {
									indexRelatedTags = rsRelatedTag.getString("TagItemId")
											+ "$$$$$"
											+ rsRelatedTagName
													.getString("name");
									doc.add(new Field("RelatedTags", indexRelatedTags,
											Field.Store.YES,
											Field.Index.TOKENIZED));
								}
								rsRelatedTagName.close();
							}
							rsRelatedTag.close();

							writer.addDocument(doc);
						}
						newsData.close();

					}
				}
			}
			
			writer.optimize();
			logger.log(Level.INFO,"IN indexDocs() method : writer is optimised properly");
			// writer.close();
		} catch (Exception e) {
			
			saveForReindexing(indexTag,indexContent,indexPublishedDate,indexRelatedTags,indexingPath);
			logger.log(Level.SEVERE,"IN indexDocs() method : exception while indexing for industry : " +industryName);
			e.printStackTrace();
		} finally {

			try {
				logger.log(Level.INFO,"IN indexDocs() method : the writer is : " +writer);
				if (writer != null)

				{

					writer.close();
					logger.log(Level.INFO,"IN indexDocs() method : writer closed successfully...");
				}

			} catch (IOException ioe) {
				logger.log(Level.SEVERE,"IN indexDocs() method : writer not closed properly..");
				ioe.printStackTrace();

			}

		}

	}
	

  	public void saveForReindexing(String indexTag, String indexContent,
			String indexPublishedDate, String indexRelatedTags , String indexingPath) {
  		logger.log(Level.SEVERE,"IN saveForReindexing() method...");
  		try{
  			logger.log(Level.SEVERE,"Reindex tag : "+ indexTag);
  			logger.log(Level.SEVERE,"Reindex Content : "+indexContent);
  			logger.log(Level.SEVERE,"Reindex ItemDate : "+indexPublishedDate);
  			logger.log(Level.SEVERE,"Reindex Related Tags : "+ indexRelatedTags);
  			if(indexTag !=null && indexContent !=null && indexPublishedDate !=null && indexRelatedTags !=null){
  			Connection conn = getConnection();
  			
  				String query =  "insert into reindexingrecords values(null,\""+indexTag+"\",\""+indexContent+"\",\""+indexPublishedDate+"\",\""+indexRelatedTags+"\",\""+indexingPath+"\")";
  				logger.log(Level.SEVERE,"IN saveForReindexing() method... query : "+query);
  				Statement stmt = conn.createStatement() ;
  					stmt.execute(query);
  					stmt.close();
  					logger.log(Level.SEVERE,"IN saveForReindexing() method :  the reindexing record is saved in db...");
  			}
  		}
  		catch (Exception e) {
  			logger.log(Level.SEVERE,"IN saveForReindexing() method :  Error : "+e.getMessage());
  			e.printStackTrace();
  		}
	}

	public void indexErrornousRecords(){
		logger.log(Level.SEVERE,"IN indexErrornousRecords() method : performing reindexing...........");
		String indexingPath = null;
		String indexTag= null;
		String indexContent = null;
		String indexPublishedDate = null;
		String indexRelatedTags= null;
		String recordId= null;

		try{
		Connection conn = getConnection();
		String query = "SELECT id, tags, content, itemDate, relatedTags, indexingPath FROM reindexingrecords";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			logger.log(Level.SEVERE,"IN indexErrornousRecords() method : got records for reindexing.....");
			indexingPath = rs.getString("indexingPath");
			indexTag = rs.getString("tags");
			indexContent = rs.getString("content");
			indexPublishedDate = rs.getString("itemDate");
			indexRelatedTags = rs.getString("relatedTags");
			recordId = rs.getString("id");
			logger.log(Level.SEVERE,"IN indexErrornousRecords() method :performing reindexing.....");
			reindexErrornousRecord(indexingPath,recordId,indexTag,indexContent,indexPublishedDate,indexRelatedTags);
			logger.log(Level.SEVERE,"IN indexErrornousRecords() method : now will delete records from database........");
			deleteRecordFromIndexTable(recordId);
			logger.log(Level.SEVERE,"IN indexErrornousRecords() method : reindexing task completed successfully...");
		}
	}catch (Exception e) {
		logger.log(Level.SEVERE,"IN indexErrornousRecords() method : Error : "+e.getMessage());
		e.printStackTrace();
		saveForReindexing(indexTag, indexContent, indexPublishedDate, indexRelatedTags, indexingPath);
	}
		logger.log(Level.SEVERE,"IN indexErrornousRecords() method : task compeleted.....");
	}

	private void deleteRecordFromIndexTable(String recordId) {
		try {
			logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : deleting from reindexing table id : "+recordId);
			Connection conn = (Connection) getConnection();
			Statement stmt = conn.createStatement();
			String query = "delete from reindexingrecords where id=" + recordId;
			stmt.execute(query);
			stmt.close();
		} catch (Exception ex) {
			logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : Error : "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : task completed.....");
	}

	private void reindexErrornousRecord(String indexingPath, String recordId,
			String indexTag, String indexContent, String indexPublishedDate,
			String indexRelatedTags) {
		logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : now indexing single record....");
		IndexWriter writer = null;
		try{
	
		FSDirectory fsdDir = FSDirectory.getDirectory(indexingPath, false);
		fsdDir.setDisableLocks(true);
		logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : creating writer object.....");
		writer = new IndexWriter(fsdDir, new StandardAnalyzer(), false);
		logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : writer object created successfully and now creating document.");
		Document doc = new Document();
		doc.add(new Field("Tags", indexTag ,
				Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("Content",indexContent ,
				Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("ItemDate", indexPublishedDate, Field.Store.YES,
				Field.Index.UN_TOKENIZED));
		doc.add(new Field("RelatedTags", indexRelatedTags,
				Field.Store.YES,
				Field.Index.TOKENIZED));
		writer.addDocument(doc);
		writer.optimize();		
		logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : writer optimised.......");
		}
		catch (Exception e) {
			logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : Error ::"+e.getMessage());
			e.printStackTrace();
			saveForReindexing(indexTag, indexContent, indexPublishedDate, indexRelatedTags, indexingPath);
		}
		finally {
				try {
					logger.log(Level.INFO,"IN reindexErrornousRecord() method : the writer is : " +writer);
					if (writer != null)
					{
						writer.close();
						logger.log(Level.INFO,"IN reindexErrornousRecord() method : writer closed successfully...");
					}
				} catch (IOException ioe) {
					logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : writer not closed properly..");
					ioe.printStackTrace();
				}
		}
		logger.log(Level.SEVERE,"IN reindexErrornousRecord() method : task completed.......");
	}
	
}
