package com.example.Mobile_Wallet.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.CMS_Account;

public interface CMS_AccountRepository extends JpaRepository<CMS_Account,Integer>{
	
	@Query(value="select * from cms.account where userid=?1",nativeQuery=true)
	public List<CMS_Account> getByUser(int userid);
	
	@Query(value="select accountid from cms.account where userid=?1",nativeQuery=true)
	public List<String> getaccountids(String userid);
	
	@Query(value="select * from cms.account where accountid=?1",nativeQuery=true)
	public CMS_Account getaccountbyid(String Accountid);
	
	@Query(value="select userid from cms.account where accountno=?1",nativeQuery=true)
	public int getuidbyaccountno(String accountno);
	
	@Query(value="select accountid from cms.account where accountno=?1",nativeQuery=true)
	public String getaidbyaccountno(String accountno);
	
	@Query(value="select accountid from cms.account where cardnumber=?1",nativeQuery=true)
	public String get_aid_from_uid(String cardnumber);
	
	@Query(value="select count(*) from cms.account where accountno=?1",nativeQuery=true)
	public int countByAccountno(String accountno);
	
	@Query(value="select * from cms.account where cardnumber=?1 and cvv=?2 and expiry=?3",nativeQuery=true)
	public CMS_Account getByCard(String cardno,String cvv,String expiry);
	
	@Query(value="select * from cms.account where accountid=?1",nativeQuery=true)
	public CMS_Account getById(Integer accountid);
	
	@Modifying
	@Transactional
	@Query(value="update cms.account set balance=balance-?2 where accountid=?1",nativeQuery = true)
	public void deduct(Integer accountid,String amount);
	
	@Modifying
	@Transactional
	@Query(value="update cms.account set balance=balance+?2 where accountid=?1",nativeQuery = true)
	public void addamount(Integer accountid,String amount);

	@Query(value="select balance from cms.account where accountid=(select accountid from cms.cms_token where tokennumber=?1)",nativeQuery=true)
	public String getaccountbalance(String tokennumber);
	
	@Query(value="select status from cms.account where accountid=(select accountid from cms.cms_token where tokennumber=?1)",nativeQuery=true)
	public String getaccountstatus(String tokennumber);
	
	@Query(value="select * from cms.account where accountid=?1 and expiry>now()",nativeQuery=true)
	public CMS_Account checkExpiry(Integer accountid);

	

}
