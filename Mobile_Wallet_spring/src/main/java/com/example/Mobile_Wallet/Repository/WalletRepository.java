package com.example.Mobile_Wallet.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.Wallet;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
	
	@Query(value="select * from WMS.wallet where walletid=?1",nativeQuery=true)
	public Wallet getWalletById(String id);
	
	@Query(value="select status from WMS.wallet where walletid=?1",nativeQuery=true)
	public String getStatusById(String walletid);
	
	@Query(value="select count(*) from wms.wallet where walletid=?1",nativeQuery=true)
	public int countByWId(String walletid);
	
	@Query(value="select count(*) from wms.wallet where userid=?1",nativeQuery=true)
	public int countByUId(Integer userid);
	
	@Query(value="select count(*) from wms.wallet where walletname=?1 and userid=?2",nativeQuery=true)
	public int countByWname(String wname, Integer userid);
	
	@Query(value="select walletid from WMS.wallet where userid=?1",nativeQuery=true)
	public String getWalletIdByUserId(int userid);
	
	@Query(value="select * from wms.wallet where walletid=?1 and date_add(lastupdateddate,interval 1 day)<now()",nativeQuery=true)
	public Wallet checkStatusTime(String id);
	
	@Modifying
	@Transactional
	@Query(value="Insert into WMS.wallet values (?1,?2,?3,'STANDARD','EMONEY',0,?4,?5,now())",nativeQuery=true)
	public void insert(String walletid,int userid,String name,String cur,String status);
	
	@Modifying
	@Transactional
	@Query(value="update WMS.wallet set status=?2,lastupdateddate=now() where walletid=?1",nativeQuery=true)
	public void updateStatus(String id,String status);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value="update WMS.wallet set balance=balance+?2,lastupdateddate=now() where walletid=?1",nativeQuery=true)
	public void addMoney(String id,String amount);
	
	@Modifying
	@Transactional
	@Query(value="update WMS.wallet set balance=balance-?2,lastupdateddate=now() where walletid=?1",nativeQuery=true)
	public void deductMoney(String id,String amount);
	
	@Query(value="Select * from WMS.wallet where userid=?1 and status like ?2",nativeQuery=true)
	public List<Wallet> getByUserId(int userid, String status);

	@Query(value="Select walletid from WMS.wallet where userid=?1 and status like ?2 order by walletid desc",nativeQuery=true)
	public List<String> walletdesc(String userid, String status);
	
	@Query(value="select userid from wms.wallet where walletid=?1",nativeQuery=true)
	public int getUserIdByWalletid(String walletid);
	
	@Query(value="select userid from wms.wallet where walletid=?1",nativeQuery=true)
	public Integer getUserId(String walletid) ;
	
	@Query(value="select userid from wms.wallet where walletid=?1",nativeQuery=true)
	public String getId(String walletid);

	@Query(value="select balance from wms.wallet where walletid=?1",nativeQuery=true)
	public Double getbalance(String walletid);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value="update WMS.wallet set balance=balance-?2,lastupdateddate=now() where walletid=?1",nativeQuery=true)
	public void deduct(String senderwalletid, String money);
	
	@Query(value="select walletid from WMS.wallet where userid=?1 and status like 'ACTIVE' ",nativeQuery=true)
	public List<String> getWalletIdviaUserId(Integer userId);
	
	@Query(value="select mobileno from wms.user where userid=(select userid from wms.wallet where walletid=?1)",nativeQuery=true)
	public String getMobileFromWId(String walletid);
	
	@Query(value="select walletname from wms.wallet where walletid=?1",nativeQuery=true)
	public String getWnameFromWId(String walletid);
	
}