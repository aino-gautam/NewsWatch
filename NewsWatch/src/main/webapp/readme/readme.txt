
1.Group table:
   foreign key :  groupParentId
   changes the foreign key settings as below:
   Key Name : group_groupParent_fk_constraint
   On delete : set null
   On update : set null
   refer table: group


2. In the web.xml while delpoying the war on local machineor on 116 comment- out the line as below:
	<servlet>
		<servlet-name>indexsetup</servlet-name>
		<servlet-class>com.lighthouse.search.server.SearchIndexSetupServlet
		</servlet-class>
		<init-param>
			<param-name>IndexDirPath</param-name>
			<param-value>/lighthouse/searchindexes/</param-value>
		</init-param>
		<init-param>
			<param-name>IndexSetupUsername</param-name>
			<param-value>mahesh</param-value>
		</init-param>
		<init-param>
			<param-name>IndexSetupPassword</param-name>
			<param-value>erom</param-value>
		</init-param>
	<!-- 	<load-on-startup>4</load-on-startup> -->  (like this)
	</servlet>
	<servlet-mapping>
		<servlet-name>indexsetup</servlet-name>
		<url-pattern>/SearchIndexSetup</url-pattern>
	</servlet-mapping>
	
	
3.In NewsItem table :
	feedId(BIGINT)
	feedContent(LONGTEXT)
	isLocked(TINYINT(1))
	
4. Update newsletterdelivery table :
	delivery(DATETIME) & Default Value:NULL


5.In tagitem table:
    user(FK):Remove on delete/update RESTRICT make it as on delete/update CASCADE

6.In useritemaccessstats
	newsItemId(FK):Remove on delete/update RESTRICT make it as on delete/update CASCADE
	
7. In useritemsharehistory
      destinationUser Field: removed the NOT NULL constraint.  
    
8. In the Sp_getandregularnews
	make changes and replace the procedure	
	
	
------------------------------

LhInterim release DB changes:

1. updates in groups table

2. create newsletter_statistics table

3. updates in industryenum table

4. create newsletter_design 

5. updates in newsitems table

6. updates in user permission

	
	