package com.example.Mobile_Wallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.CMS_Token;

public interface CMS_TokenRepository extends JpaRepository<CMS_Token,Integer> {
	
	@Query(value="select * from cms.cms_token where tokennumber=?1",nativeQuery=true)
	public CMS_Token getByToken(String tokennumber);
	
	@Query(value="select tokennumber from cms.cms_token where accountid=?1",nativeQuery=true)
	public CMS_Token getToken(Integer id);
	
	@Query(value="select walletid from cms.cms_token where accountid=?1",nativeQuery=true)
	public  String getbywid(Integer id);
	
	
	@Query(value="select * from cms.cms_token where tokennumber=?1 and tokenexpiry>now()",nativeQuery=true)
	public CMS_Token checkExpiry(String tokennumber);
	
	@Modifying
	@Transactional
	@Query(value="insert into CMS.cms_token (tokennumber,accountid,walletid,tokenexpiry,status,createddate) values (?1,?2,?3,date_add(now(),interval ?4 second),'ACTIVE',now())",nativeQuery = true)
	public void insert(String token,Integer accountid,String walletid,String expiry);

	@Modifying
	@Transactional
	@Query(value="update CMS.cms_token set status=?2 where accountid=?1",nativeQuery = true)
	void updatestatus(Integer id,String status);
	
	@Modifying
	@Transactional
	@Query(value="update CMS.cms_token set token=?2 where accountid=?1",nativeQuery = true)
	void updattoken(Integer id,String token);
	
	@Modifying
	@Transactional
	@Query(value="delete from cms.cms_token where tokennumber=?1",nativeQuery = true)
	public void deleteByToken(String token);
	
	
	@Query(value="select count(accountid=?1) from cms.cms_token where walletid=?2",nativeQuery=true)
	public int count(Integer accountid ,String walletid);
}
