package com.ldap.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.stereotype.Repository;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.controls.SubentriesRequestControl;

@Repository
public class UserLDAPDao {
	private LDAPConnection ldapConnection;
	
	private Logger logger=Logger.getLogger(getClass());

	{
		try {
			//"cn=root,dc=cn,dc=com", "root"是ldap中的用户节person节点路径dn，和其userPassword密码属性，匹配上，才可以创建与ldap的连接对象（可以通过判断是否创建连接成功，来判断用户dn属性，与userPassword密码属性是否匹配）
			//当然，我在ldap中添加的普通对象有查询的权限，但没有更改（增删改）节点信息的权限
			//这里我就用的 etc/openldap/slapd.conf中定义的管理员账号，与密码来创建连接的
			ldapConnection = new LDAPConnection("192.168.99.100", 389, "cn=root,dc=cn,dc=com", "root");
		} catch (LDAPException e) {
			e.printStackTrace();
		}
	}

	public List<SearchResultEntry>  queryByFilter(String baseDN, String filter) {
		try {

			SearchRequest searchRequest = new SearchRequest(baseDN, SearchScope.SUB, filter);
			searchRequest.addControl(new SubentriesRequestControl());
			SearchResult searchResult = ldapConnection.search(searchRequest);
			return searchResult.getSearchEntries();
		} catch (LDAPException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<SearchResultEntry>();
	}

	public void createEntry(String baseDN,String uid) {

		try {
			String dn="uid="+uid+","+baseDN;
			List<SearchResultEntry> entries=queryByFilter(baseDN,"uid="+uid);
			if (entries.size()==0) {
				List<Attribute> attributes=new ArrayList<Attribute>();
				attributes.add(new Attribute("objectclass", "person","organizationalPerson","inetOrgPerson"));
				attributes.add(new Attribute("uid", uid));
				attributes.add(new Attribute("cn", uid));
				attributes.add(new Attribute("sn", uid.split("_")[0]));
				ldapConnection.add(dn, attributes);
				
			}else{
				logger.info("account "+uid + " alreay exists !!!");
			}	
			
		} catch (LDAPException e) {
			e.printStackTrace();
		}

	}

	public void deleteEntry (String dn) {

		try {
			ldapConnection.delete(dn);
			
		} catch (LDAPException e) {
			e.printStackTrace();
		}

	}
	
	public void updateEntry (String dn,Map<String,String> attributes) {

		try {
			
			SearchResultEntry entry=ldapConnection.getEntry(dn);
			if (entry==null) {
				logger.info(dn+" does not exists !!");
				return ;
			}
			List<Modification> modifications=new ArrayList<Modification>();
			for (Map.Entry<String, String> entry2:attributes.entrySet()) {
				modifications.add(new Modification(ModificationType.REPLACE,entry2.getKey() ,entry2.getValue()));
			}
			
			ldapConnection.modify(dn, modifications);
			
			
		} catch (LDAPException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	
	
	
	
	

}
