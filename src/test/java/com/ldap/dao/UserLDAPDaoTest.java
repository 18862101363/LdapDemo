package com.ldap.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchResultEntry;



/**
 * Unit test for simple App.
 */
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class UserLDAPDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private UserLDAPDao userLDAPDao;
	
	private String BASE_DN_USER="ou=Marketing,dc=cn,dc=com";
	
	@Test
	public void testQueryByFilter() {
		
		List<SearchResultEntry> entries = userLDAPDao.queryByFilter(BASE_DN_USER, "uid=liming");
		
		for (SearchResultEntry entry : entries) {

			System.out.println("dn: " + entry.getDN());
			// Attribute attribute = entry.getAttribute("userPassword");
			// System.out.println("userPassword: "+ attribute.getValue());
			Collection<Attribute> attributes = entry.getAttributes();

			for (Attribute attribute : attributes) {
				System.out.println(attribute.getName() + ": " + attribute.getValue());
			}

		}
		
	}
	
	
	@Test
	public void testCreateEntry() {
		userLDAPDao.createEntry("ou=Marketing,dc=cn,dc=com","li_ming");
	}
	
	
	@Test
	public void testDeleteEntry() {
		userLDAPDao.deleteEntry("uid=xiaohong,ou=Marketing,dc=cn,dc=com");
	}
	
	@Test
	public void testUpdateEntry() {
		
		Map<String, String> attributes=new HashMap<String, String>();
		attributes.put("userPassword", "000000");
		
		userLDAPDao.updateEntry("uid=zhangsan,ou=Marketing,dc=cn,dc=com",attributes);
	}
	
	
}
