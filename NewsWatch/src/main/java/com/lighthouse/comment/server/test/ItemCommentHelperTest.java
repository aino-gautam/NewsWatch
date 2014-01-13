package com.lighthouse.comment.server.test;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lighthouse.comment.client.exception.CommentException;

import com.lighthouse.comment.server.db.ItemCommentHelper;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public class ItemCommentHelperTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	public void testPostComment() {
		try {
			ItemCommentHelper commentHelper = new ItemCommentHelper();

			commentHelper.postComment(
					"good job IB", 4, 2);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
     * 
     */
	@Test
	public void testUpdateComment() {
		try {

			ItemCommentHelper commentHelper = new ItemCommentHelper();
			commentHelper.updateComment(1,
					"good job IB");

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
     * 
     */
	@Test
	public void testDeleteComment() {
		try {
			ItemCommentHelper commentHelper = new ItemCommentHelper();
			commentHelper.deleteComment(3);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
     * 
     */
	@Test
	public void CommentListForNewsItem() {

		try {
			ItemCommentHelper commentHelper = new ItemCommentHelper();
			ArrayList commentList = commentHelper.getCommentListForItem(4);
			assertNotNull(commentList);
		} catch (CommentException e) {

			e.printStackTrace();
		}
	}

}
