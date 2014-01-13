package com.lighthouse.search.server.helper;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.sql.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryFilter;

import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;

import org.apache.lucene.search.WildcardQuery;

import com.lighthouse.search.client.domain.DateRange;
import com.lighthouse.search.client.domain.SearchResultList;
import com.lighthouse.search.exception.SearchException;
import com.mysql.jdbc.PreparedStatement;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.db.DBHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mahesh@ensarm.com This class will acts as the helper for the
 *         SearchServiceImpl Servlet that will provide help regarding all the
 *         search related cconcerns regarding to the inputs provided to them.
 * */
public class SearchHelper extends DBHelper {

	Logger logger = Logger.getLogger(SearchHelper.class.getName());

	public SearchHelper() {
	}

	/**
	 * This method decides which appropriate method should be called depending
	 * upon the parameters passed to it.
	 * 
	 * @param : 1) String keyword- that is to be searched. 2) DateRange object-
	 *        that should give the search result between those those date range.
	 * @return : SearchResultList object - the result for searched string.
	 * */
	public SearchResultList getSearchResultListHelper(String keyword,
			String tag, DateRange range, String lightHouseDir)
			throws SearchException {
		logger.log(Level.INFO, "IN getSearchResultListHelper() method: keyword :"+keyword+ " tag :" +tag+ " light house dir : "+lightHouseDir);
		SearchResultList searchResultList = new SearchResultList();

		if (keyword != null && range != null) {
			if (keyword.contains(":")) {
				searchResultList = getTimeBoundTagBasedSeacthList(keyword,
						range, lightHouseDir);
			} else {
				searchResultList = getTimeBoundTagSearchList(keyword, range,
						lightHouseDir);
			}

		}

		else if (range != null && keyword == null) {
			searchResultList = getTimeBoundSearchList(range, lightHouseDir);

		}

		else if (keyword != null && range == null) {
			if (keyword.contains(":")) {
				searchResultList = getTagBasedSearchList(keyword, lightHouseDir);

			} else if (keyword.contains("*")) {
				searchResultList = getWildCardSearchList(keyword, lightHouseDir);

			}

			else {
				searchResultList = getPlainSearchList(keyword, lightHouseDir);

			}
		}
		PageCriteria criteria = new PageCriteria();
		criteria.setPageSize(15);

		int numitems = searchResultList.size();
		searchResultList.setNumItems(numitems);
		int pages = 0;
		if (numitems % criteria.getPageSize() == 0)
			pages = numitems / criteria.getPageSize();
		else
			pages = (numitems / criteria.getPageSize() + 1);
		searchResultList.setTotalPages(pages);
		logger.log(Level.INFO, "IN getSearchResultListHelper() method: task completed for light house dir : "+lightHouseDir);
		return searchResultList;
	}

	private SearchResultList getTimeBoundTagBasedSeacthList(String keyword,
			DateRange range, String lightHouseDir) {
		logger.log(Level.INFO, "IN getTimeBoundTagBasedSeacthList() method: keyword :"+keyword+ " light house dir : "+lightHouseDir);
		SearchResultList tempResultList = new SearchResultList();
		ArrayList<Integer> tempNewsItemIdList = new ArrayList<Integer>();
	
		try {
			IndexSearcher searcher = new IndexSearcher(lightHouseDir);
			StringTokenizer st = new StringTokenizer(keyword, ":");
			String tag_name = st.nextToken();
			if (!st.hasMoreTokens()) {
				String text = "[" + range.getFromDate() + " TO "
						+ range.getToDate() + "]";
				logger.log(Level.INFO, "IN getTimeBoundTagBasedSeacthList() method: date range : "+text);
				Query query1 = new QueryParser("ItemDate",
						new StandardAnalyzer()).parse(text);
			
					Query filter2 = new QueryParser("Tags",
							new StandardAnalyzer()).parse(QueryParser
									.escape(tag_name));
					BooleanQuery filterQuery = new BooleanQuery();
					filterQuery.add(filter2, BooleanClause.Occur.SHOULD);
					QueryFilter filter = new QueryFilter(filterQuery);
					Hits hitsTags = searcher.search(query1, filter);
					for (int j = 0; j < hitsTags.length(); j++) {
						Document doc = hitsTags.doc(j);
						NewsItems newsitem = populateDetails(doc, null);
						if (!tempNewsItemIdList.contains(newsitem.getNewsId())) {
							tempNewsItemIdList.add(newsitem.getNewsId());
							tempResultList.add(newsitem);
						}
					}
			
			} else {
				String keyword1 = st.nextToken();
				String text = "[" + range.getFromDate() + " TO "
						+ range.getToDate() + "]";
				logger.log(Level.INFO, "IN getTimeBoundTagBasedSeacthList() method: date range : "+text);
				Query query1 = new QueryParser("ItemDate",
						new StandardAnalyzer()).parse(text);
				Query filter2 = new QueryParser("Tags", new StandardAnalyzer())
						.parse(QueryParser
								.escape(tag_name));
				BooleanQuery filterQuery = new BooleanQuery();
				Query filter1 = new QueryParser("Content",
						new StandardAnalyzer()).parse(QueryParser
								.escape(keyword1));
				filterQuery.add(filter1, BooleanClause.Occur.MUST);
				filterQuery.add(filter2, BooleanClause.Occur.MUST);
				QueryFilter filter = new QueryFilter(filterQuery);
				Hits hitsTags = searcher.search(query1, filter);

				for (int j = 0; j < hitsTags.length(); j++) {
					Document doc = hitsTags.doc(j);
					NewsItems newsitem = populateDetails(doc, null);
					if (!tempNewsItemIdList.contains(newsitem.getNewsId())) {
						tempNewsItemIdList.add(newsitem.getNewsId());
						tempResultList.add(newsitem);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "IN getTimeBoundTagBasedSeacthList() method: Error:"+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN getTimeBoundTagBasedSeacthList() method: task completed");
		return tempResultList;
	}

	/**
	 * This method returns the search list depending on the keyword passed to it
	 * and makes a wildcard search in the indexes.
	 * 
	 * @param : 1) String keyword- that is to be searched. 2) String
	 *        lightHouseName - the directory in which the search should be made.
	 * @return : SearchResultList object - the result for searched string.
	 * */
	private SearchResultList getWildCardSearchList(String keyword,
			String lightHouseName) {
		logger.log(Level.INFO, "IN getWildCardSearchList() method: keyword :"+keyword+ " light house dir : "+lightHouseName);
		SearchResultList tempResultList = new SearchResultList();
		ArrayList<Integer> tempNewsItemIdList = new ArrayList<Integer>();
		try {
			Searcher searcher = new IndexSearcher(
					IndexReader.open(lightHouseName));

			Query query = new WildcardQuery(new Term("Tags", keyword));
			Hits hits = searcher.search(query);
			for (int k = 0; k < hits.length(); k++) {
				Document doc = hits.doc(k);

				NewsItems newsitem = populateDetails(doc, null);
				if (!tempNewsItemIdList.contains(newsitem.getNewsId())) {
					tempNewsItemIdList.add(newsitem.getNewsId());
					tempResultList.add(newsitem);
				}
			}

			Query query1 = new WildcardQuery(new Term("Content", keyword));
			Hits hits2 = searcher.search(query1);
			for (int m = 0; m < hits2.length(); m++) {
				Document doc = hits2.doc(m);

				NewsItems newsitem = populateDetails(doc, null);
				if (!tempNewsItemIdList.contains(newsitem.getNewsId())) {
					tempNewsItemIdList.add(newsitem.getNewsId());
					tempResultList.add(newsitem);
				}
			}

		} catch (Exception e) {
			logger.log(Level.INFO, "IN getWildCardSearchList() method: Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN getWildCardSearchList() method:task completed");
		return tempResultList;
	}

	/**
	 * This method returns the search list depending on the keyword passed to it
	 * and makes a tag based search in the indexes.
	 * 
	 * @param : 1) String keyword- that is to be searched. 2) String
	 *        lightHouseName - the directory in which the search should be made.
	 * @return : SearchResultList object - the result for searched string.
	 * */
	private SearchResultList getTagBasedSearchList(String keyword,
			String lightHouseName) {
		logger.log(Level.INFO, "IN getTagBasedSearchList() method: keyword :"+keyword+ " light house dir : "+lightHouseName);
		SearchResultList tempResultList = new SearchResultList();
		ArrayList<Integer> tempNewsItemIdList = new ArrayList<Integer>();
		ArrayList<String> listOfKeyword = new ArrayList<String>();
		try {

			Searcher searcher = new IndexSearcher(
					IndexReader.open(lightHouseName));
			StringTokenizer st = new StringTokenizer(keyword, ":");
			String text1 = st.nextToken();
			if (st.hasMoreTokens()) {
				String text2 = st.nextToken();
				String validKeywordStr = validateKeyword(text2);
				String validKeywordArray[] = validKeywordStr.split(" ");
				for (int i = 0; i < validKeywordArray.length; i++) {
					if (!validKeywordArray[i].isEmpty()) {
						listOfKeyword.add(validKeywordArray[i].trim());
					}
				}

				Query query = new QueryParser("Tags", new StandardAnalyzer())
						.parse(QueryParser.escape(text1));
				for (String str : listOfKeyword) {
					Query filter1 = new QueryParser("Content",
							new StandardAnalyzer()).parse(QueryParser
							.escape(str));
					QueryFilter filter = new QueryFilter(filter1);
					Hits hits = searcher.search(query, filter);
					for (int k = 0; k < hits.length(); k++) {
						Document doc = hits.doc(k);

						NewsItems item = populateDetails(doc, null);
						if (item.getAbstractNews().length() != 0) {

							if (!tempNewsItemIdList.contains(item.getNewsId())) {
								tempNewsItemIdList.add(item.getNewsId());
								tempResultList.add(item);
							}
						}

					}
				}
			} else {
				Query query = new QueryParser("Tags", new StandardAnalyzer())
						.parse(QueryParser.escape(text1));
				Hits hits = searcher.search(query);
				for (int k = 0; k < hits.length(); k++) {
					Document doc = hits.doc(k);

					NewsItems item = populateDetails(doc, null);
					if (item.getAbstractNews().length() != 0) {

						if (!tempNewsItemIdList.contains(item.getNewsId())) {
							tempNewsItemIdList.add(item.getNewsId());
							tempResultList.add(item);
						}
					}

				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able the open the index directory");
			logger.log(Level.INFO, "IN getTagBasedSearchList() method: Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN getTagBasedSearchList() method:task completed");
		return tempResultList;
	}

	/**
	 * This method returns the search list depending on the keyword passed to it
	 * and makes a normal text search in the indexes.
	 * 
	 * @param : 1) String keyword- that is to be searched. 2) String
	 *        lightHouseName - the directory in which the search should be made.
	 * @return : SearchResultList object - the result for searched string.
	 * */
	private SearchResultList getPlainSearchList(String keyword,
			String lightHouseName) {
		logger.log(Level.INFO, "IN getPlainSearchList() method: keyword :"+keyword+ " light house dir : "+lightHouseName);
		SearchResultList tempResultList = new SearchResultList();
		ArrayList<Integer> tempNewsItemIdList = new ArrayList<Integer>();
		ArrayList<String> listOfKeyword = new ArrayList<String>();
		try {
			Searcher searcher = new IndexSearcher(
					IndexReader.open(lightHouseName));
			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
					new String[] { "Tags", "Content" }, new StandardAnalyzer());

			String validKeywordStr = validateKeyword(keyword);
			String validKeywordArray[] = validKeywordStr.split(" ");
			for (int i = 0; i < validKeywordArray.length; i++) {
				if (!validKeywordArray[i].isEmpty()) {
					listOfKeyword.add(validKeywordArray[i].trim());
				}
			}

			for (String str : listOfKeyword) {
				Hits hits = searcher.search(queryParser.parse(QueryParser
						.escape(str)));
				for (int k = 0; k < hits.length(); k++) {
					Document doc = hits.doc(k);
					NewsItems newsitem = populateDetails(doc, null);
					if (!tempNewsItemIdList.contains(newsitem.getNewsId())) {
						tempNewsItemIdList.add(newsitem.getNewsId());
						tempResultList.add(newsitem);
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able the open the index directory");
			logger.log(Level.INFO, "IN getPlainSearchList() method: Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN getPlainSearchList() method:task completed");
		return tempResultList;
	}

	/**
	 * This method returns the search list between the Date range passed to it
	 * and makes a search in the indexes.
	 * 
	 * @param : 1) DateRange object- the date range in which the search is to be
	 *        made. 2) String lightHouseName - the directory in which the search
	 *        should be made.
	 * @return : SearchResultList object - the result for searched string.
	 * */
	private SearchResultList getTimeBoundSearchList(DateRange range,
			String lightHouseName) {
		logger.log(Level.INFO, "IN getTimeBoundSearchList() method: light house dir : "+lightHouseName);
		SearchResultList tempResultList = new SearchResultList();
		ArrayList<Integer> tempNewsItemIdList = new ArrayList<Integer>();
		String text = "[" + range.getFromDate() + " TO " + range.getToDate()
				+ "]";
		logger.log(Level.INFO, "IN getTimeBoundSearchList() method: date range : "+text);
		try {
			Sort sort = new Sort("ItemDate", true);
			Searcher searcher = new IndexSearcher(
					IndexReader.open(lightHouseName));

			Query query2 = new QueryParser("ItemDate", new StandardAnalyzer())
					.parse(text);

			Hits hits2 = searcher.search(query2, sort);
			for (int x = 0; x < hits2.length(); x++) {
				Document doc = hits2.doc(x);
				NewsItems newsitem = populateDetails(doc, null);
				if (!tempNewsItemIdList.contains(newsitem.getNewsId())) {
					tempNewsItemIdList.add(newsitem.getNewsId());
					tempResultList.add(newsitem);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able the open the index directory");
			logger.log(Level.INFO, "IN getTimeBoundSearchList() method: Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN getTimeBoundSearchList() method:task completed");
		return tempResultList;
	}

	/**
	 * This method returns the search list between the Date range passed to it
	 * and and checks if the specified word is in the Search Result of the date
	 * range search.
	 * 
	 * @param : 1) DateRange object- the date range in which the search is to be
	 *        made. 2) String lightHouseName - the directory in which the search
	 *        should be made.
	 * @return : SearchResultList object - the result for searched string.
	 * */
	private SearchResultList getTimeBoundTagSearchList(String keyword,
			DateRange range, String lightHouseName) {
		logger.log(Level.INFO, "IN getTimeBoundSearchList() method: light house dir : "+lightHouseName);
		SearchResultList tempResultList = new SearchResultList();
		ArrayList<Integer> tempNewsItemIdList = new ArrayList<Integer>();
		ArrayList<String> listOfKeyword = new ArrayList<String>();
		String text = "[" + range.getFromDate() + " TO " + range.getToDate()
				+ "]";
		logger.log(Level.INFO, "IN getTimeBoundSearchList() method: date range : "+text);
		try {
			String validKeywordStr = validateKeyword(keyword);

			Searcher searcher = new IndexSearcher(
					IndexReader.open(lightHouseName));
			Sort sort = new Sort("ItemDate", true);
			Query query2 = new QueryParser("ItemDate", new StandardAnalyzer())
					.parse(text);

			Hits hits2;
			if ((keyword == null) || keyword.equals("null")) {
				hits2 = searcher.search(query2);
			} else

			{

				Query filter1 = new QueryParser("Tags", new StandardAnalyzer())
						.parse(QueryParser
								.escape(validKeywordStr));
				Query filter2 = new QueryParser("Content",
						new StandardAnalyzer()).parse(QueryParser
								.escape(validKeywordStr));
				BooleanQuery filterQuery = new BooleanQuery();
				filterQuery.add(filter1, BooleanClause.Occur.SHOULD);
				filterQuery.add(filter2, BooleanClause.Occur.SHOULD);
				QueryFilter filter = new QueryFilter(filterQuery);
				hits2 = searcher.search(query2, filter, sort);
			}

			for (int x = 0; x < hits2.length(); x++) {
				Document doc = hits2.doc(x);

				NewsItems item = populateDetails(doc, null);
				if (item.getAbstractNews().length() != 0) {

					if (!tempNewsItemIdList.contains(item.getNewsId())) {
						tempNewsItemIdList.add(item.getNewsId());
						tempResultList.add(item);
					}
				}
			}
	
		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able the open the index directory");
			logger.log(Level.INFO, "IN getTimeBoundSearchList() method: Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN getTimeBoundSearchList() method:task completed");
		return tempResultList;

	}

	/**
	 * This method returns the array list for the specific user of all the items
	 * which he previously searched for.
	 * 
	 * @param : int userid - the user for which we should get the arraylist of
	 *        search items.
	 * @return : ArrayList object - the list of the previously search items.
	 * */
	public ArrayList getAllSearchedByUser(int userId) {
		logger.log(Level.INFO, "IN getAllSearchedByUser() method:userid"+userId);
		ArrayList mostSearched = new ArrayList();
		Connection conn = getConnection();
		try {
			String sql = "SELECT id,timeOfAccess,searchCount,itemSearched FROM searchHistory s where userid="
					+ userId;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				mostSearched.add(rs.getString("id"));
				mostSearched.add(rs.getString("timeOfAccess"));
				mostSearched.add(rs.getString("searchCount"));
				mostSearched.add(rs.getString("itemSearched"));
			}
			rs.close();
			stmt.close();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			logger.log(Level.INFO, "IN getAllSearchedByUser() method: Error: "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN getAllSearchedByUser() method: task completed userid"+userId);
		return mostSearched;
	}

	/**
	 * This method that adds to search history table to provide suggestions.
	 * 
	 * @param : 1) Date obj - the date on which the search was made. 2) int
	 *        count - the count number on which the search was made 3) String
	 *        itemSearched - the string on which search was made. 4) int userid
	 *        - the search was made by which user. 5) int industryId - the
	 *        search was made in which LightHouse.
	 * @return : Boolean var- if insertion successful then true or else false.
	 * */
	public boolean addToSearchHistory(Date commDate, int count,
			String itemSearched, int userid, int industryId) {
		logger.log(Level.INFO, "IN addToSearchHistory() method:userid"+userid+"item searched : "+itemSearched+"count :"+count);
		boolean result = false;
		Connection conn = getConnection();
		try {
			String sql = "insert into searchHistory values(?,?,?,?,?,?)";

			PreparedStatement prestmt = (PreparedStatement) conn
					.prepareStatement(sql);
			prestmt.setString(1, null);
			prestmt.setDate(2, commDate);
			prestmt.setInt(3, count);
			prestmt.setString(4, itemSearched);
			prestmt.setInt(5, userid);
			prestmt.setInt(6, industryId);
			prestmt.executeUpdate();
			prestmt.close();
			result = true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			logger.log(Level.INFO, "IN addToSearchHistory() method: Error: "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN addToSearchHistory() method: task completed");
		return result;
	}

	/**
	 * This method deletes the search history for the specific user.
	 * 
	 * @param : String text -text from search history is to deleted.
	 * @return : Boolean var- if deletion successful then true or else false.
	 * */
	public boolean deleteByUserId(String textToDelete) {
		logger.log(Level.INFO, "IN deleteByUserId() method: text to delete"+textToDelete);
		boolean result = false;
		Connection conn = getConnection();
		try {
			String query = "select id from searchHistory where itemSearched='"
					+ textToDelete + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String sql = "delete from searchHistory where id="
						+ rs.getString("id");
				Statement stmt1 = conn.createStatement();
				stmt1.executeUpdate(sql);
			}
			rs.close();
			stmt.close();
			result = true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			logger.log(Level.INFO, "IN deleteByUserId() method: Error: "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN deleteByUserId() method: task completed ");
		return result;

	}

	/**
	 * This method updates the count of the news item search again.
	 * 
	 * @param : int userid - the user id whose search history is to deleted.
	 * @return : Boolean var- if deletion successful then true or else false.
	 * */
	public boolean updateCount(int userId, Date commDate, String itemSearched) {
		boolean result = false;
		Connection conn = getConnection();
		try {
			String sql = "update searchHistory set searchCount= searchCount+1,timeOfAccess = null where itemSearched = '"
					+ itemSearched + "'  and userId = " + userId;
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			result = true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * This method populates the arraylist by fetching from document object sent
	 * to it
	 * 
	 * @param - 1)Document object- the data will be fetched from this and will
	 *        be populted in the arraylist 2) String text - the text that is to
	 *        be searched again in the document
	 * @return - Arraylist object - populated arraylist
	 * */
	public NewsItems populateDetails(Document doc, String text2) {
		logger.log(Level.INFO, "IN populateDetails() method: text "+text2);
		Connection con = getConnection();
		NewsItems newsItem = new NewsItems();
		try {
			Statement stmt = con.createStatement();
			if (text2 == null) {

				String contentData = doc.get("Content").trim();

				ArrayList<String> newsInfoList = new ArrayList<String>();
				String newsItemId = null;
				String abstractCon = null;
				String url = null;
				String title = null;
				String content = null;
				String source = null;
				String isLocked = null;
				String st[] = contentData.split("\\$\\*\\$\\#");
				for (int i = 0; i < st.length; i++) {
					if (!st[i].isEmpty()) {
						newsInfoList.add(st[i]);
					}
				}
				for (int j = 0; j < newsInfoList.size(); j++) {
					newsItemId = newsInfoList.get(j).trim();
					abstractCon = newsInfoList.get(j + 1).trim();
					url = newsInfoList.get(j + 2).trim();
					title = newsInfoList.get(j + 3).trim();
					content = newsInfoList.get(j + 4).trim();
					source = newsInfoList.get(j + 5).trim();
					isLocked = newsInfoList.get(j + 6).trim();
					break;
				}

				ArrayList<TagItem> relatedTagsList = new ArrayList<TagItem>();

				Field[] list = doc.getFields("RelatedTags");
				for (Field fld : list) {
					String relatedTags = fld.stringValue();

					StringTokenizer stTags = new StringTokenizer(relatedTags,
							"\\$\\$\\$\\$\\$");
					String tagItemId = stTags.nextToken();
					String tagName = stTags.nextToken();

					TagItem item = new TagItem();
					item.setTagId(Integer.parseInt(tagItemId));
					item.setTagName(tagName);
					relatedTagsList.add(item);
				}

				newsItem.setNewsId(Integer.parseInt(newsItemId.trim()));
				newsItem.setAbstractNews(abstractCon);
				newsItem.setUrl(url);
				newsItem.setNewsTitle(title);
				newsItem.setNewsContent(content);
				newsItem.setNewsSource(source);
				newsItem.setNewsDate(doc.get("ItemDate"));
				newsItem.setAssociatedTagList(relatedTagsList);

				String commentCountQuery = "select count(*) from itemcomment i where i.newsItemId = "
						+ newsItemId;
				ResultSet rs = stmt.executeQuery(commentCountQuery);
				while (rs.next()) {
					newsItem.setCommentsCount(rs.getInt(1));
				}
				rs.close();
				String viewCountQuery = "SELECT sum(NewscatalystItemCount+NewsletterItemCount) as viewCount FROM useritemaccessstats where newsItemId = "
						+ newsItemId;
				ResultSet rs2 = stmt.executeQuery(viewCountQuery);
				while (rs2.next()) {
					newsItem.setViewsCount(rs2.getInt(1));
				}
				newsItem.setIsLocked(Integer.parseInt(isLocked));

			} else {

				ArrayList<String> listOfKeyword = new ArrayList<String>();
				String validKeywordStr = validateKeyword(text2);
				String validKeywordArray[] = validKeywordStr.split(" ");
				for (int i = 0; i < validKeywordArray.length; i++) {
					if (!validKeywordArray[i].isEmpty()) {
						listOfKeyword.add(validKeywordArray[i].trim());
					}
				}

				String contentData = doc.get("Content");

				for (String str : listOfKeyword) {
					if (contentData.contains(str)) {

						ArrayList<String> newsInfoList = new ArrayList<String>();
						String newsItemId = null;
						String abstractCon = null;
						String url = null;
						String title = null;
						String content = null;
						String source = null;
						String isLocked = null;
						String st[] = contentData.split("\\$\\*\\$\\#");
						for (int i = 0; i < st.length; i++) {
							if (!st[i].isEmpty()) {
								newsInfoList.add(st[i]);
							}
						}
						for (int j = 0; j < newsInfoList.size(); j++) {
							newsItemId = newsInfoList.get(j).trim();
							abstractCon = newsInfoList.get(j + 1).trim();
							url = newsInfoList.get(j + 2).trim();
							title = newsInfoList.get(j + 3).trim();
							content = newsInfoList.get(j + 4).trim();
							source = newsInfoList.get(j + 5).trim();
							isLocked = newsInfoList.get(j + 6).trim();
							break;
						}

						ArrayList<TagItem> relatedTagsList = new ArrayList<TagItem>();

						Field[] list = doc.getFields("RelatedTags");
						for (Field fld : list) {
							String relatedTags = fld.stringValue();

							StringTokenizer stTags = new StringTokenizer(
									relatedTags, "\\$\\$\\$\\$\\$");
							String tagItemId = stTags.nextToken();
							String tagName = stTags.nextToken();

							TagItem item = new TagItem();
							item.setTagId(Integer.parseInt(tagItemId));
							item.setTagName(tagName);
							relatedTagsList.add(item);
						}

						newsItem.setNewsId(Integer.parseInt(newsItemId.trim()));
						newsItem.setAbstractNews(abstractCon);
						newsItem.setUrl(url);
						newsItem.setNewsTitle(title);
						newsItem.setNewsContent(content);
						newsItem.setNewsSource(source);
						newsItem.setNewsDate(doc.get("ItemDate"));

						newsItem.setAssociatedTagList(relatedTagsList);

						String commentCountQuery = "select count(*) from itemcomment i where i.newsItemId = "
								+ newsItemId;
						ResultSet rs = stmt.executeQuery(commentCountQuery);
						while (rs.next()) {
							newsItem.setCommentsCount(rs.getInt(1));
						}
						rs.close();
						String viewCountQuery = "SELECT sum(NewscatalystItemCount+NewsletterItemCount) as viewCount FROM useritemaccessstats where newsItemId = "
								+ newsItemId;
						ResultSet rs2 = stmt.executeQuery(viewCountQuery);
						while (rs2.next()) {
							newsItem.setViewsCount(rs2.getInt(1));
						}
						newsItem.setIsLocked(Integer.parseInt(isLocked));
					}

				}
			}

		} catch (Exception e) {
			logger.log(Level.INFO, "IN populateDetails() method: Error"+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "IN populateDetails() method: task completed");
		return newsItem;
	}

	public SearchResultList getPage(SearchResultList originalResultList,
			PageCriteria criteria, int newsmode) {
		logger.log(Level.INFO, "IN getPage() method: ");
		SearchResultList tempResultList = new SearchResultList();

		int pageSize = criteria.getPageSize();
		int startIndex = criteria.getStartIndex();

		if (originalResultList.size() > (startIndex + pageSize)) {
			for (int i = startIndex; i < startIndex + pageSize; i++) {
				tempResultList.add(originalResultList.get(i));
			}
		} else {
			for (int i = startIndex; i < (originalResultList.size()); i++) {
				tempResultList.add(originalResultList.get(i));
			}
		}
		int numitems = originalResultList.size();
		tempResultList.setNumItems(numitems);
		int pages = 0;
		if (numitems % criteria.getPageSize() == 0)
			pages = numitems / criteria.getPageSize();
		else
			pages = (numitems / criteria.getPageSize() + 1);
		tempResultList.setTotalPages(pages);
		logger.log(Level.INFO, "IN getPage() method: task completed");
		return tempResultList;
	}

	public HashMap<String, Long> getTagCloudMapNew(String keyword,
			DateRange range, String lightHouseDir) {
		logger.log(Level.INFO, "IN getTagCloudMapNew() method: keyword : "+keyword+" lighthousedir: "+lightHouseDir);
		HashMap<String, Long> tagCloud = new HashMap<String, Long>();
		ArrayList<String> tempNewsItemIdList = null;
		java.util.Date now = new java.util.Date();
		long starttimestamp = now.getTime();
		String tagNameMain = null;
		logger.log(Level.INFO, "IN getTagCloudMapNew() method: start time " + starttimestamp);

		ArrayList<String> listOfKeyword = new ArrayList<String>();
		try {
			IndexSearcher searcher = new IndexSearcher(lightHouseDir);
			if (!(keyword == null)) {
				if (!keyword.contains(":")) {
					String validKeywordStr = validateKeyword(keyword);
					String validKeywordArray[] = validKeywordStr.split(" ");
					for (int i = 0; i < validKeywordArray.length; i++) {
						if (!validKeywordArray[i].isEmpty()) {
							listOfKeyword.add(validKeywordArray[i].trim());
						}
					}
				} else {
					StringTokenizer st = new StringTokenizer(keyword, ":");
					tagNameMain = st.nextToken();
					if (st.hasMoreTokens()) {
						String text2 = st.nextToken();
						String validKeywordStr = validateKeyword(text2);
						String validKeywordArray[] = validKeywordStr.split(" ");
						for (int i = 0; i < validKeywordArray.length; i++) {
							if (!validKeywordArray[i].isEmpty()) {
								listOfKeyword.add(validKeywordArray[i].trim());
							}
						}
					} else {
						String validKeywordStr = validateKeyword(tagNameMain);
						String validKeywordArray[] = validKeywordStr.split(" ");
						for (int i = 0; i < validKeywordArray.length; i++) {
							if (!validKeywordArray[i].isEmpty()) {
								listOfKeyword.add(validKeywordArray[i].trim());
							}
						}
					}
				}
			}

			if (keyword != null && range == null) {
				if (keyword.contains(":")) {
					StringTokenizer st = new StringTokenizer(keyword, ":");
					tagNameMain = st.nextToken();
					if (st.hasMoreTokens()) {
						for (String str : listOfKeyword) {
							Query query1 = new QueryParser("Content",
									new StandardAnalyzer()).parse(QueryParser
									.escape(str));

							Hits hitsTags = searcher.search(query1);
							tempNewsItemIdList = new ArrayList<String>();
							for (int j = 0; j < hitsTags.length(); j++) {
								Document doc = hitsTags.doc(j);
								String contentData = doc.get("Content").trim();
								StringTokenizer st1 = new StringTokenizer(
										contentData, "$*$#");
								String newsItemId = st1.nextToken().trim();
								if (!tempNewsItemIdList.contains(newsItemId)) {
									tempNewsItemIdList.add(newsItemId);
								}
							}
						}
					} else {

						ArrayList<String> tempTagIdList = new ArrayList<String>();
						StringTokenizer st1 = new StringTokenizer(
								lightHouseDir, "//");
						String str1 = st1.nextToken();
						String str2 = st1.nextToken();
						String str3 = st1.nextToken();

						String query = "SELECT ti.TagItemId FROM tagitem ti where ti.industryId in (Select ie.IndustryEnumId from industryenum ie where ie.Name='"
								+ str3 + "')";
						Connection conn = getConnection();
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(query);
						while (rs.next()) {
							String tagId = rs.getString("ti.TagItemId");

							tempTagIdList.add(tagId);

						}

						String tagitemIdList = null;
						String tagName;
						long count;
						for (int i = 0; i < tempTagIdList.size(); i++) {
							if (tagitemIdList == null) {
								tagitemIdList = tempTagIdList.get(i);
							} else {
								tagitemIdList = tagitemIdList + ","
										+ tempTagIdList.get(i);
							}
						}
						String query2 = "select a.name , a.tagitemid , COUNT(a.tagitemid) TotalCount  from tagitem a,newstagitem b, newsitem c where b.newsitemid= c.newsitemid and c.isReport= 0 and c.feedid is null and a.tagitemid = b.tagitemid and b.tagitemid in("
								+ tagitemIdList
								+ ") GROUP BY a.tagitemid HAVING COUNT(a.tagitemid) > 0 ORDER BY COUNT(a.tagitemid) DESC";
						ResultSet rs1 = stmt.executeQuery(query2);
						while (rs1.next()) {
							tagName = rs1.getString("a.name");
							count = Long.parseLong(rs1.getString("TotalCount"));
							tagCloud.put(tagName, count);
						}
						return tagCloud;
					}
				} else {
					for (String str : listOfKeyword) {
						Query query1 = new QueryParser("Content",
								new StandardAnalyzer()).parse(QueryParser
								.escape(str));

						Hits hitsTags = searcher.search(query1);
						tempNewsItemIdList = new ArrayList<String>();
						for (int j = 0; j < hitsTags.length(); j++) {
							Document doc = hitsTags.doc(j);
							String contentData = doc.get("Content").trim();
							StringTokenizer st = new StringTokenizer(
									contentData, "$*$#");
							String newsItemId = st.nextToken().trim();
							if (!tempNewsItemIdList.contains(newsItemId)) {
								tempNewsItemIdList.add(newsItemId);
							}
						}
					}

				}
			} else if (keyword == null && range != null) {

				String text = "[" + range.getFromDate() + " TO "
						+ range.getToDate() + "]";
				Query query1 = new QueryParser("ItemDate",
						new StandardAnalyzer()).parse(text);

				Hits hitsTags = searcher.search(query1);
				tempNewsItemIdList = new ArrayList<String>();
				for (int j = 0; j < hitsTags.length(); j++) {
					Document doc = hitsTags.doc(j);
					String contentData = doc.get("Content").trim();
					StringTokenizer st = new StringTokenizer(contentData,
							"$*$#");
					String newsItemId = st.nextToken().trim();
					if (!tempNewsItemIdList.contains(newsItemId)) {
						tempNewsItemIdList.add(newsItemId);
					}
				}
			} else if (keyword != null && range != null) {
				if (!keyword.contains(":")) {
					tempNewsItemIdList = new ArrayList<String>();
					String text = "[" + range.getFromDate() + " TO "
							+ range.getToDate() + "]";
					Query query1 = new QueryParser("ItemDate",
							new StandardAnalyzer()).parse(text);

					Query filter2 = new QueryParser("Content",
							new StandardAnalyzer()).parse(QueryParser
									.escape(keyword));
					BooleanQuery filterQuery = new BooleanQuery();

					filterQuery.add(filter2, BooleanClause.Occur.SHOULD);
					QueryFilter filter = new QueryFilter(filterQuery);
					Hits hitsTags = searcher.search(query1, filter);

					for (int j = 0; j < hitsTags.length(); j++) {
						Document doc = hitsTags.doc(j);
						String contentData = doc.get("Content").trim();
						StringTokenizer st = new StringTokenizer(contentData,
								"$*$#");
						String newsItemId = st.nextToken().trim();
						if (!tempNewsItemIdList.contains(newsItemId)) {
							tempNewsItemIdList.add(newsItemId);
						}
					}
					// }
				} else {
					StringTokenizer st = new StringTokenizer(keyword, ":");
					String tag_name = st.nextToken();
					if (!st.hasMoreTokens()) {
						String text = "[" + range.getFromDate() + " TO "
								+ range.getToDate() + "]";
						Query query1 = new QueryParser("ItemDate",
								new StandardAnalyzer()).parse(text);
					
						Hits hitsTags = searcher.search(query1);

						tempNewsItemIdList = new ArrayList<String>();
						for (int j = 0; j < hitsTags.length(); j++) {
							Document doc = hitsTags.doc(j);
							String contentData = doc.get("Content").trim();
							StringTokenizer st1 = new StringTokenizer(
									contentData, "$*$#");
							String newsItemId = st1.nextToken().trim();
							if (!tempNewsItemIdList.contains(newsItemId)) {
								tempNewsItemIdList.add(newsItemId);
							}
						}
					} else {
						String keyword1 = st.nextToken();
						String text = "[" + range.getFromDate() + " TO "
								+ range.getToDate() + "]";
						Query query1 = new QueryParser("ItemDate",
								new StandardAnalyzer()).parse(text);

						BooleanQuery filterQuery = new BooleanQuery();
						Query filter1 = new QueryParser("Content",
								new StandardAnalyzer()).parse(keyword1);
						filterQuery.add(filter1, BooleanClause.Occur.MUST);

						QueryFilter filter = new QueryFilter(filterQuery);
						Hits hitsTags = searcher.search(query1, filter);

						tempNewsItemIdList = new ArrayList<String>();
						for (int j = 0; j < hitsTags.length(); j++) {
							Document doc = hitsTags.doc(j);
							String contentData = doc.get("Content").trim();
							StringTokenizer st1 = new StringTokenizer(
									contentData, "$*$#");
							String newsItemId = st1.nextToken().trim();
							if (!tempNewsItemIdList.contains(newsItemId)) {
								tempNewsItemIdList.add(newsItemId);
							}
						}
					}
				}
			}
			String newsitemIdList = null;
			String tagName;
			long count;
			for (int i = 0; i < tempNewsItemIdList.size(); i++) {
				if (newsitemIdList == null) {
					newsitemIdList = tempNewsItemIdList.get(i);
				} else {
					newsitemIdList = newsitemIdList + ","
							+ tempNewsItemIdList.get(i);
				}
			}
			System.out.println("tag cloud count" + newsitemIdList);
			String query = "select a.name , a.tagitemid , COUNT(a.tagitemid) TotalCount from tagitem a,newstagitem b where a.tagitemid = b.tagitemid and b.newsitemid in("
					+ newsitemIdList
					+ ") GROUP BY a.tagitemid HAVING COUNT(a.tagitemid) > 0 ORDER BY COUNT(a.tagitemid) DESC";
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				tagName = rs.getString("a.name");
				count = Long.parseLong(rs.getString("TotalCount"));
				tagCloud.put(tagName, count);
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "IN getTagCloudMapNew() method: Error " + e);
			e.printStackTrace();
		}

		now = new java.util.Date();
		long endtimestamp = now.getTime();
	
		logger.log(Level.INFO, "IN getTagCloudMapNew() method: end time " + endtimestamp);
		logger.log(Level.INFO, "IN getTagCloudMapNew() method: time taken: in miiliseconds :  " + (endtimestamp - starttimestamp));
		logger.log(Level.INFO, "IN getTagCloudMapNew() method: time taken: in seconds :  " + (endtimestamp - starttimestamp)
				/ 1000);
		logger.log(Level.INFO, "IN getTagCloudMapNew() task completed");
		return tagCloud;
	}

	private String validateKeyword(String keyword) {
		String str = null;
		str = keyword.replaceAll("[^a-zA-Z0-9]", " ");
		return str;
	}

}
