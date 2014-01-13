package com.lighthouse.search.server.helper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;

import com.lighthouse.admin.client.domain.LHNewsItemsAdminInformation;

import com.newscenter.client.tags.TagItem;
import com.newscenter.server.db.DBHelper;

public class SingleRecordIndexOperations extends Thread implements Runnable {

	LHNewsItemsAdminInformation newsItem;
	String dirPath;
	DBHelper dbHelper;
	String operation;

	HashMap hmNewsItemId;

	Logger logger = Logger.getLogger(SingleRecordIndexOperations.class
			.getName());

	public void run() {
		logger.log(Level.INFO, "IN run() of the thread for operation : "+ operation);
		if (operation.equals("addRecord")) {
			addRecordToIndex(newsItem, dirPath);
		} else if (operation.equals("deleteRecord")) {
			deleteRecordFromIndex(hmNewsItemId, dirPath,null);
		} else if (operation.equals("updateRecord")) {
			updateRecordFromIndex(newsItem, dirPath);
		}
		logger.log(Level.INFO, "the thread task completed for operation : "+ operation);
	}

	public ArrayList getnewsitemId(LHNewsItemsAdminInformation newsinfo) {
	
		ArrayList newsIdList = new ArrayList();
		try {
			dbHelper = new DBHelper();
			Connection con = dbHelper.getConnection();

			String sql = "SELECT newsitemId FROM newsitem where Content = \""
					+ newsinfo.getContent() + "\"  and Title = \""
					+ newsinfo.getNewsTitle() + "\" and Abstract = \""
					+ newsinfo.getAbstractNews() + "\" and url=\""
					+ newsinfo.getUrl() + "\" and ItemDate = \""
					+ newsinfo.getDate() + "\" and source = \""
					+ newsinfo.getSource() + "\"";

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				newsIdList.add(rs.getString("newsitemId"));
			}
			rs.close();
			stmt.close();
		}

		catch (Exception e) {
			logger.log(Level.SEVERE,
					"IN getnewsitemId() method : connection is null");
			e.printStackTrace();
		}
		return newsIdList;
	}

	public void addRecordToIndex(LHNewsItemsAdminInformation newsItem,
			String dirPath) {
		logger.log(Level.SEVERE, "IN addRecordToIndex() method : ");
		logger.log(Level.SEVERE, "IN getnewsitemId() method ");
		java.util.Date now = new java.util.Date();
		long starttimestamp = now.getTime();
		logger.log(Level.SEVERE, "the start time for indexing : "+starttimestamp);
		StandardAnalyzer analyzer = new StandardAnalyzer();
		String indexTag= null;
		String indexContent = null;
		String indexPublishedDate = null;
		String indexRelatedTags= null;
		IndexWriter writer = null;

		logger.log(Level.SEVERE,
				"IN addRecordToIndex() method : Adding records in dir: "
						+ dirPath);
		try {
			File indexDir = new File(dirPath);

			if (indexDir.exists()) {
				logger.log(Level.SEVERE,
						"IN addRecordToIndex() method : dir already exist: "
								+ dirPath);
				FSDirectory fsdDir = FSDirectory.getDirectory(indexDir, false);

				fsdDir.setDisableLocks(true);
				// IndexWriter writer = new IndexWriter(indexDir, new
				// SimpleAnalyzer(), false);
				logger.log(Level.SEVERE,
						"IN addRecordToIndex() method : trying to obtain writer for : "
								+ fsdDir.getFile());
				writer = new IndexWriter(fsdDir, analyzer, false);
				logger.log(Level.SEVERE,
						"IN addRecordToIndex() method : writer oject obtained succesfully");
				try {

					ArrayList tempOnlyTaglist = newsItem.getArrayTagList();

					for (int j = 0; j < tempOnlyTaglist.size(); j++) {
						TagItem tempTagItem = (TagItem) tempOnlyTaglist.get(j);

						if (newsItem.getNewsItemId() == 0) {
							ArrayList newsItemIdList = getnewsitemId(newsItem);
							logger.log(
									Level.SEVERE,
									"IN addRecordToIndex() method : creating the docs object when the newsitem=0 means for adding new record");
							for (int k = 0; k < newsItemIdList.size(); k++) {
								Document doc = new Document();
								indexTag =  tempTagItem.getTagName();
								indexContent = newsItemIdList
										.get(k)
										+ " $*$# "
										+ newsItem.getAbstractNews()
										+ " $*$# "
										+ newsItem.getUrl()
										+ " $*$# "
										+ newsItem.getNewsTitle()
										+ " $*$# "
										+ newsItem.getContent()
										+ " $*$# "
										+ newsItem.getSource()
										+ " $*$# "
										+ newsItem.getIsLocked();
								
								doc.add(new Field("Tags",indexTag, Field.Store.YES,
										Field.Index.TOKENIZED));
								doc.add(new Field("Content",indexContent ,
										Field.Store.YES, Field.Index.TOKENIZED));

								java.sql.Date convertedDate = new Date(newsItem
										.getDate().getTime());
								indexPublishedDate = convertedDate
										.toString();

								doc.add(new Field("ItemDate", indexPublishedDate, Field.Store.YES,
										Field.Index.UN_TOKENIZED));

								ArrayList tempTaglist = newsItem
										.getArrayTagList();

								for (int i = 0; i < tempTaglist.size(); i++) {
									TagItem tagItem = (TagItem) tempTaglist
											.get(i);
									indexRelatedTags = tagItem
											.getTagId()
											+ "$$$$$"
											+ tagItem.getTagName();
									doc.add(new Field("RelatedTags", indexRelatedTags,
											Field.Store.YES,
											Field.Index.TOKENIZED));
								}
								
								writer.addDocument(doc);
								logger.log(Level.SEVERE,
										"IN addRecordToIndex() method : writer is optimising now");
								writer.optimize();
								logger.log(Level.SEVERE,
										"IN addRecordToIndex() method : writer oject obtained succesfully");
							}
						}

						else {
							logger.log(
									Level.SEVERE,
									"IN addRecordToIndex() method : creating docs when updating a reord i.e. newsitemid !=0");
							Document doc = new Document();
							indexTag = tempTagItem.getTagName();
							indexContent =  newsItem
									.getNewsItemId()
									+ " $*$# "
									+ newsItem.getAbstractNews()
									+ " $*$# "
									+ newsItem.getUrl()
									+ " $*$# "
									+ newsItem.getNewsTitle()
									+ " $*$# "
									+ newsItem.getContent()
									+ " $*$# "
									+ newsItem.getSource()
									+ " $*$# "
									+ newsItem.getIsLocked();
							doc.add(new Field("Tags", indexTag,
									Field.Store.YES, Field.Index.TOKENIZED));
							doc.add(new Field("Content", indexContent, Field.Store.YES,
									Field.Index.TOKENIZED));

							java.sql.Date convertedDate = new Date(newsItem
									.getDate().getTime());
							
							indexPublishedDate = convertedDate
									.toString();
							
							doc.add(new Field("ItemDate", indexPublishedDate , Field.Store.YES,
									Field.Index.UN_TOKENIZED));

							ArrayList tempTaglist = newsItem.getArrayTagList();

							for (int i = 0; i < tempTaglist.size(); i++) {
								TagItem tagItem = (TagItem) tempTaglist.get(i);
								indexRelatedTags = tagItem
										.getTagId()
										+ "$$$$$"
										+ tagItem.getTagName();
								doc.add(new Field("RelatedTags", indexRelatedTags ,
										Field.Store.YES, Field.Index.TOKENIZED));
							}

							writer.addDocument(doc);
							logger.log(Level.SEVERE,
									"IN addRecordToIndex() method : writer is optimising now");
							writer.optimize();
							logger.log(Level.SEVERE,
									"IN addRecordToIndex() method : writer oject obtained succesfully");
						}
					}
					// writer.close();

				} catch (Exception e) {
				
					logger.log(Level.SEVERE,
							"IN addRecordToIndex() method : Exception while indexing when dir is present");
					e.printStackTrace();
					SetupIndexHelper helper = new SetupIndexHelper();
					helper.saveForReindexing(indexTag, indexContent, indexPublishedDate, indexRelatedTags,dirPath);
				}
			} else {
				logger.log(Level.SEVERE,
						"IN addRecordToIndex() method : dir doesn't exist: "
								+ dirPath);
				FSDirectory fsdDir = FSDirectory.getDirectory(indexDir, true);

				fsdDir.setDisableLocks(true);
				writer = new IndexWriter(fsdDir, analyzer, true);
				try {

					ArrayList tempOnlyTaglist = newsItem.getArrayTagList();

					for (int j = 0; j < tempOnlyTaglist.size(); j++) {
						TagItem tempTagItem = (TagItem) tempOnlyTaglist.get(j);

						if (newsItem.getNewsItemId() == 0) {
							ArrayList newsItemIdList = getnewsitemId(newsItem);
							logger.log(
									Level.SEVERE,
									"IN addRecordToIndex() method : creating the docs object when the newsitem=0 means for adding new record");

							for (int k = 0; k < newsItemIdList.size(); k++) {
								Document doc = new Document();
								indexTag = tempTagItem
										.getTagName();
								indexContent = newsItemIdList
										.get(k)
										+ " $*$# "
										+ newsItem.getAbstractNews()
										+ " $*$# "
										+ newsItem.getUrl()
										+ " $*$# "
										+ newsItem.getNewsTitle()
										+ " $*$# "
										+ newsItem.getContent()
										+ " $*$# "
										+ newsItem.getSource()
										+ " $*$# "
										+ newsItem.getIsLocked();
								doc.add(new Field("Tags", indexTag, Field.Store.YES,
										Field.Index.TOKENIZED));
								doc.add(new Field("Content", indexContent,
										Field.Store.YES, Field.Index.TOKENIZED));

								java.sql.Date convertedDate = new Date(newsItem
										.getDate().getTime());
								indexPublishedDate = convertedDate
										.toString();
								
								
								doc.add(new Field("ItemDate", indexPublishedDate, Field.Store.YES,
										Field.Index.UN_TOKENIZED));

								ArrayList tempTaglist = newsItem
										.getArrayTagList();

								for (int i = 0; i < tempTaglist.size(); i++) {
									TagItem tagItem = (TagItem) tempTaglist
											.get(i);
									indexRelatedTags = tagItem
											.getTagId()
											+ "$$$$$"
											+ tagItem.getTagName();
									doc.add(new Field("RelatedTags", indexRelatedTags,
											Field.Store.YES,
											Field.Index.TOKENIZED));
								}

								writer.addDocument(doc);
								logger.log(Level.SEVERE,
										"IN addRecordToIndex() method : writer is optimising now");
								writer.optimize();
								logger.log(Level.SEVERE,
										"IN addRecordToIndex() method : writer oject obtained succesfully");
							}
						}

						else {
							logger.log(
									Level.SEVERE,
									"IN addRecordToIndex() method : creating docs when updating a reord i.e. newsitemid !=0");

							Document doc = new Document();
							indexTag =  tempTagItem.getTagName();
							indexContent = newsItem
									.getNewsItemId()
									+ " $*$# "
									+ newsItem.getAbstractNews()
									+ " $*$# "
									+ newsItem.getUrl()
									+ " $*$# "
									+ newsItem.getNewsTitle()
									+ " $*$# "
									+ newsItem.getContent()
									+ " $*$# "
									+ newsItem.getSource()
									+ " $*$# "
									+ newsItem.getIsLocked();
							doc.add(new Field("Tags", indexTag,
									Field.Store.YES, Field.Index.TOKENIZED));
							doc.add(new Field("Content", indexContent, Field.Store.YES,
									Field.Index.TOKENIZED));

							java.sql.Date convertedDate = new Date(newsItem
									.getDate().getTime());
							indexPublishedDate =  convertedDate
									.toString();
							
							doc.add(new Field("ItemDate", indexPublishedDate, Field.Store.YES,
									Field.Index.UN_TOKENIZED));

							ArrayList tempTaglist = newsItem.getArrayTagList();

							for (int i = 0; i < tempTaglist.size(); i++) {
								TagItem tagItem = (TagItem) tempTaglist.get(i);
								indexRelatedTags = tagItem
										.getTagId()
										+ "$$$$$"
										+ tagItem.getTagName();
								doc.add(new Field("RelatedTags", indexRelatedTags,
										Field.Store.YES, Field.Index.TOKENIZED));
							}

							writer.addDocument(doc);
							logger.log(Level.SEVERE,
									"IN addRecordToIndex() method : writer is optimising now");
							writer.optimize();
							logger.log(Level.SEVERE,
									"IN addRecordToIndex() method : writer oject obtained succesfully");
						}
					}
					// writer.close();

				}

				catch (Exception e) {

					// writer.close();
					logger.log(Level.SEVERE,
							"IN addRecordToIndex() method : error while indexing");
					e.printStackTrace();
					SetupIndexHelper helper = new SetupIndexHelper();
					helper.saveForReindexing(indexTag, indexContent, indexPublishedDate, indexRelatedTags,dirPath);
				}

			}
		} catch (Exception e) {

			try {
				writer.close();
			} catch (IOException e1) {

				e1.printStackTrace();
			}

			logger.log(Level.SEVERE,
					"IN addRecordToIndex() method : writer not created");
			e.printStackTrace();
		} finally {

			try {
				logger.log(Level.SEVERE,
						"IN addRecordToIndex() method : writer " + writer);
				if (writer != null)

				{
					writer.close();
					logger.log(Level.SEVERE,
							"IN addRecordToIndex() method : writer closed");
				}

			} catch (IOException ioe) {
				logger.log(Level.SEVERE,
						"IN addRecordToIndex() method : writer not closed");
				ioe.printStackTrace();

			}

		}

		now = new java.util.Date();
		long endtimestamp = now.getTime();
		logger.log(Level.SEVERE, "the end time for indexing : "+endtimestamp);
		logger.log(Level.SEVERE, "time taken: in miilisecondsg : "+(endtimestamp - starttimestamp));
		logger.log(Level.SEVERE, "time taken: in seconds : "+ (endtimestamp - starttimestamp)/1000);
		
		
	}

	public void deleteRecordFromIndex(HashMap hmNewsItemId, String dirPath,
			Long newsItemId) {
		// SimpleAnalyzer analyzer = new SimpleAnalyzer();
		logger.log(Level.SEVERE, "IN deleteRecordFromIndex() method ");
		java.util.Date now = new java.util.Date();
		long starttimestamp = now.getTime();
		logger.log(Level.SEVERE, "the start time for indexing : "+starttimestamp);
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexReader indexReader = null;

		try {
			File indexDir = new File(dirPath);
			logger.log(Level.SEVERE,
					"IN deleteRecordFromIndex() method : deleting from the dir "
							+ dirPath);
			FSDirectory fsdDir = FSDirectory.getDirectory(indexDir, false);

			fsdDir.setDisableLocks(true);

			indexReader = IndexReader.open(fsdDir);

			Searcher searcher = new IndexSearcher(indexReader);

			if (newsItemId == null) {
				for (Object obj : hmNewsItemId.keySet()) {
					newsItemId = (Long) obj;

					Query query = new QueryParser("Content", analyzer)
							.parse(Long.toString(newsItemId));

					Hits hits = searcher.search(query);
					for (int k = 0; k < hits.length(); k++) {
						int docNum = hits.id(k);
						indexReader.deleteDocument(docNum);
						logger.log(Level.SEVERE,
								"IN deleteRecordFromIndex() method : the newsitem deleted :"
										+ newsItemId);
					}
				}
			} else if (hmNewsItemId == null) {
				Query query = new QueryParser("Content", analyzer).parse(Long
						.toString(newsItemId));

				Hits hits = searcher.search(query);
				for (int k = 0; k < hits.length(); k++) {
					int docNum = hits.id(k);
					indexReader.deleteDocument(docNum);
					logger.log(Level.SEVERE,
							"IN deleteRecordFromIndex() method : the newsitem deleted :"
									+ newsItemId);
				}
			}
			// indexReader.close();

		} catch (Exception e) {
			/*
			 * try { indexReader.close(); } catch (IOException e1) {
			 * e1.printStackTrace(); }
			 */
			logger.log(Level.SEVERE,
					"IN deleteRecordFromIndex() method : the newsitem can't be deleted :"
							+ newsItemId);
			e.printStackTrace();
		} finally {

			try {
				logger.log(Level.SEVERE,
						"IN deleteRecordFromIndex() method : the index reader is :"
								+ indexReader);
				if (indexReader != null)

				{

					indexReader.close();
					logger.log(Level.SEVERE,
							"IN deleteRecordFromIndex() method : the index reader is closed");
				}

			} catch (IOException ioe) {
				logger.log(Level.SEVERE,
						"IN deleteRecordFromIndex() method : error in closing the reader");
				ioe.printStackTrace();

			}

		}
		now = new java.util.Date();
		long endtimestamp = now.getTime();
		logger.log(Level.SEVERE, "the end time for indexing : "+endtimestamp);
		logger.log(Level.SEVERE, "time taken: in miilisecondsg : "+(endtimestamp - starttimestamp));
		logger.log(Level.SEVERE, "time taken: in seconds : "+ (endtimestamp - starttimestamp)/1000);
		
	}

	public void updateRecordFromIndex(LHNewsItemsAdminInformation newsItem,
			String dirPath) {
		logger.log(Level.SEVERE, "IN updateRecordFromIndex() method");
		java.util.Date now = new java.util.Date();
		long starttimestamp = now.getTime();
		logger.log(Level.SEVERE, "the start time for indexing : "+starttimestamp);
		deleteRecordFromIndex(null, dirPath, (long) newsItem.getNewsItemId());
		logger.log(
				Level.SEVERE,
				"IN updateRecordFromIndex() method : record deleted successfully during updation of a record");
		addRecordToIndex(newsItem, dirPath);
		logger.log(
				Level.SEVERE,
				"IN updateRecordFromIndex() method : record added successfully during updation of a record");
		logger.log(
				Level.SEVERE,
				"IN updateRecordFromIndex() method : record updated successfully during updation of a record");
		now = new java.util.Date();
		long endtimestamp = now.getTime();
		logger.log(Level.SEVERE, "the end time for indexing : "+endtimestamp);
		logger.log(Level.SEVERE, "time taken: in miilisecondsg : "+(endtimestamp - starttimestamp));
		logger.log(Level.SEVERE, "time taken: in seconds : "+ (endtimestamp - starttimestamp)/1000);
	}
	

	public void setNewsItem(LHNewsItemsAdminInformation newsItem) {
		this.newsItem = newsItem;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setHmNewsItemId(HashMap hmNewsItemId) {
		this.hmNewsItemId = hmNewsItemId;
	}

}
