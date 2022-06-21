package com.example.Mobile_Wallet.Repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.WMS_Token;

public interface WMS_TokenRepository extends JpaRepository<WMS_Token,String> {
	
	@Query(value="select * from wms.wms_token where cardnumber=?1 and cardissuer=?2 and walletid=?3",nativeQuery=true)
	public WMS_Token getByCard(String cardno,String cardissuer,String walletid);
	
	@Query(value="select * from wms.wms_token where tokennumber=?1",nativeQuery=true)
	public WMS_Token getById(String token);
	
	@Query(value="select * from wms.wms_token where tokennumber=?1 and tokenexpiry>now()",nativeQuery=true)
	public WMS_Token checkExpiry(String tokennumber);
	
	@Modifying
	@Transactional
	@Query(value="update wms.wms_token set status=?2 where tokennumber=?1",nativeQuery = true)
	public void changeStatus(String token,String status);
	
	@Modifying
	@Transactional
	@Query(value="update wms.wms_token set tokennumber=?1,status='ACTIVE' where cardnumber=?2 and cardissuer=?3 and walletid=?4",nativeQuery = true)
	public void changeToken(String token,String cardno,String cardissuer,String walletid);
	
	@Query(value="update tokennumber=?2 from wms.wms_token where waletid=?1",nativeQuery=true)
	public void update(String id,String no);
	
	@Query(value="select tokennumber from wms.wms_token where waletid=?1",nativeQuery=true)
	public String gettoken(String walletid);
	
	@Modifying
	@Transactional
	@Query(value="insert into wms.wms_token (tokennumber,walletid,cardnumber,cardissuer,tokenexpiry,status) values (?1,?2,?3,?4,date_add(now(),interval ?5 second),'ACTIVE')",nativeQuery = true)
	public void insert(String token,String walletid,String card,String cardissuer,String expiry);
	
	@Query(value="select status from WMS.wms_token where walletid=?1",nativeQuery=true)
	public String getStatusById(String walletid);

	@Query(value="select * from wms.wms_token where walletid=?1",nativeQuery=true)
	public Object getwallet(String receiverwalletid);
	
	@Modifying
	@Transactional
	@Query(value="delete from wms.wms_token where walletid=?1 and cardnumber=?2 and cardissuer=?3",nativeQuery = true)
	public void deleteByCard(String walletid,String cardno,String cardissuer);
	
	@Query(value="select count(cardnumber=?1) from wms.wms_token where walletid=?2",nativeQuery=true)
	public int count(String cardno ,String walletid);

	@Query(value="select concat(cardnumber,',',cardissuer) as CardDetails from wms.wms_token where walletid=?1 and status='ACTIVE'",nativeQuery=true)
	public ArrayList<String> getCardById(String walletid);

	
}